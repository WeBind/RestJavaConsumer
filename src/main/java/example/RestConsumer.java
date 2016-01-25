/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package example;


import com.rabbitmq.client.AMQP;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.soap.SOAPException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.json.simple.parser.ParseException;

/**
 *
 * @author Eliott
 */
public class RestConsumer {

    private String id;
    private String exchange;
    private String broadcast;
    private String callback;
    private Connection queueConnection;
    private Channel channel;
    private JSONParser parser;
    private int startingTime;
    private int size;
    private int duration;
    private int period;
    private String provider;
    private Boolean endReceived;
    private JSONObject data;
    private JSONArray listSent;
    private JSONArray listReceived;
    
    @SuppressWarnings("unchecked")
	public RestConsumer(String id, String exchange, String broadcast, String callback)
    {
        this.id = id;
        this.exchange = exchange;
        this.broadcast = broadcast;
        this.callback = callback;
        this.queueConnection = null;
        this.channel = null;
        this.parser = new JSONParser();
        this.startingTime = 0;
        this.size = 1;
        this.duration = 0;
        this.period = 0;
        this.provider = null;
        this.endReceived = false;
        this.data = new JSONObject();
        this.data.put("id", this.id);
        this.listSent = new JSONArray();
        this.listReceived = new JSONArray();
    }

    public void run () throws Exception
    {
        //new rabbitMQ connection
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        this.queueConnection = factory.newConnection();
        this.channel = this.queueConnection.createChannel();
        
        // declare the exchange
        this.channel.exchangeDeclare(this.exchange, "direct");

        //declare the queue
        String queueName = this.channel.queueDeclare().getQueue();
        
        //bind the queue to the exchange (id + broadcast);
        this.channel.queueBind(queueName, this.exchange, this.id);
        this.channel.queueBind(queueName, this.exchange, this.broadcast);
        
        //wait for orders message
        System.out.println(" [*] Waiting for orders.");

        //create new consumer
        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope,
                                     AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, "UTF-8");
                System.out.println(" [x] Received '" + envelope.getRoutingKey() + "':'" + message + "'");
                JSONObject obj;
                try {
                    obj = (JSONObject) parser.parse(message);
                    if (obj.containsKey("type")) {
                        String type = (String) obj.get("type");
                        if(type.equals("config")) {
                            if (obj.containsKey("startingTime"))
                                setStartingTime(Integer.parseInt((String) obj.get("startingTime")));
                            if (obj.containsKey("size"))
                                setSize(Integer.parseInt((String) obj.get("size")));
                            if (obj.containsKey("duration"))
                                setDuration(Integer.parseInt((String) obj.get("duration")));
                            if (obj.containsKey("period"))
                                setPeriod(Integer.parseInt((String) obj.get("period")));
                            if (obj.containsKey("provider"))
                                setProvider((String) obj.get("provider"));
                        } else {
                            if(type.equals("go")) {
                                try {
                                    if(getProvider() == null) {
                                        putData("error", "provider unknown");
                                        this.getChannel().getConnection().close();
                                    } else {
                                        doRequest();
                                    }
                                } catch (SOAPException ex) {
                                    Logger.getLogger(RestConsumer.class.getName()).log(Level.SEVERE, null, ex);
                                    putData("error", "SOAPException");
                                } catch (InterruptedException ex) {
                                    Logger.getLogger(RestConsumer.class.getName()).log(Level.SEVERE, null, ex);
                                    putData("error", "InterruptedException");
                                } catch (Exception ex) {
                                    Logger.getLogger(RestConsumer.class.getName()).log(Level.SEVERE, null, ex);
                                    putData("error", "Exception");
                                }
                            } else {
                            	//if end message
                            	if(type.equals("end")) {
                            		if(getProvider() == null) {
                                        this.getChannel().getConnection().close();
                            		} else {
                            			setEndReceived(true);
                                    }
                            	} else {
                            		System.out.println("[!] Last message was unreadable");
                            	}
                            }
                        }
                    } else {
                        System.out.println("[!] Last message was unreadable");
                    }
                } catch (ParseException ex) {
                        Logger.getLogger(RestConsumer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        };

        this.channel.basicConsume(queueName, true, consumer);
    }

    @SuppressWarnings("unchecked")
	public void doRequest() throws SOAPException, InterruptedException, Exception {
        int cpt = 0;
        long time1, time2, time3, time4;
        
        this.data.put("id", this.id);

        //new rest client
        HttpClient client = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet(this.provider);

        //sleep waiting for the starting time
        System.out.println("Starting time : Sleep for " + this.startingTime + " ms");
        Thread.sleep(startingTime);
        
        time3 = System.currentTimeMillis();
        time4 = System.currentTimeMillis();

        while(time4 + this.duration > time3 && !this.endReceived) {
        	// Process the rest call
            time1 = System.currentTimeMillis();
            HttpResponse response = client.execute(request);
            time2 = System.currentTimeMillis();
            
            JSONObject sent = new JSONObject();
            sent.put("id", this.id + "-" + cpt);
            sent.put("time", String.valueOf(time1 - time4));
            JSONObject received = new JSONObject();
            received.put("id", this.id + "-" + cpt);
            received.put("time", String.valueOf(time2 - time4));
            this.listSent.add(sent);
            this.listReceived.add(received);
            
            BufferedReader rd = new BufferedReader (new InputStreamReader(response.getEntity().getContent()));
            String line = "";
            String res = "";
            while ((line = rd.readLine()) != null) {
            	res = res + line;
            }

            System.out.println("\nRest response : " + res);
            System.out.println("Number of bytes received : " + res.getBytes("UTF-8").length);
            
            System.out.println("Delay of call : " + (time2 - time1) + " ms");
            //System.out.println("\nPeriod : Sleep for " + period + " ms");
            Thread.sleep(this.period);
            cpt++;
            time3 = System.currentTimeMillis();
        }

        System.out.println("\nMission executed : " + cpt + " requests in " + (time3 - time4) + " ms");
        doCallback();
    }
    
    @SuppressWarnings("unchecked")
	private void doCallback() throws IOException {
        this.data.put("sent", listSent);
        this.data.put("received", listReceived);
        this.channel.queueDeclare(this.callback, false, false, false, null);
        channel.basicPublish("", this.callback, null, this.data.toJSONString().getBytes());
        System.out.println("\nDisconnecting rabbitmq");
        this.queueConnection.close();
    }

    public static void main(String[] args)
    {
        System.out.println("Just Another Test Text.");
      /*  try {
            RestConsumer test = new RestConsumer(args[0], args[1], args[2], args[3]);
            test.run();
        } catch (Exception ex) {
            Logger.getLogger(RestConsumer.class.getName()).log(Level.SEVERE, null, ex);
        }*/
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public float getPeriod() {
        return period;
    }

    public void setPeriod(int period) {
        this.period = period;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getStartingTime() {
        return startingTime;
    }

    public void setStartingTime(int startingTime) {
        this.startingTime = startingTime;
    }
    
    public void setEndReceived(Boolean endReceived) {
        this.endReceived = endReceived;
    }
    
    @SuppressWarnings("unchecked")
	public void putData(String key, Object value) {
        this.data.put(key, value);
    }
}
