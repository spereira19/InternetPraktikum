package com.androidclient.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import com.androidclient.pojo.AccelerometerData;

import android.telephony.TelephonyManager;

public class MqttConnection {
    static String topic        = "MQTT Test from Source code";
    static int qos             = 2;
    static String broker       = "tcp://130.83.160.103:1883";
    static String clientId     = null;
    static MemoryPersistence persistence = new MemoryPersistence();
    MqttClient sampleClient = null;
    
    public static void createConnection(TelephonyManager lObjTelephonyManager, ArrayList<AccelerometerData> lObjArraylistOfSensorData, String lStrDeviceId) throws IOException {
        try {
        	clientId = lStrDeviceId;
        	System.out.println("clientId is " +clientId);
//        	if(clientId == null) {
//        		throw new Exception("Device Id is null");
//        	}
        	
        	int i=0;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(baos);
            for (AccelerometerData lObjAccelerometerData : lObjArraylistOfSensorData) {
            	i++;
            out.writeUTF("AccelerometerData Object " + i + " is ("+lObjAccelerometerData.getTimestamp()+","+lObjAccelerometerData.getX()+","+lObjAccelerometerData.getY()+","+lObjAccelerometerData.getZ()+")");
            }
            
            /*
             * storing into bytes array and reading from it ::: TESTING
             */
            /*
            byte[] bytes = baos.toByteArray();
            
            
            ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
            DataInputStream in = new DataInputStream(bais);
            while (in.available() > 0) {
            String element = in.readUTF();
            
            System.out.println(element);
            }
            */
            
            i=0;
        	
            MqttClient sampleClient = new MqttClient(broker, clientId, persistence);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            System.out.println("Connecting to broker: "+broker);
            sampleClient.connect(connOpts);
            System.out.println("Connected");
            System.out.println("Publishing message Arraylist: "+lObjArraylistOfSensorData.size());
            
            MqttMessage message = new MqttMessage(baos.toByteArray());
            message.setQos(qos);
            sampleClient.publish(topic, message);
            System.out.println("Message published");
            sampleClient.disconnect();
            System.out.println("Disconnected");
            System.exit(0);
        } catch(MqttException me) {
            System.out.println("reason "+me.getReasonCode());
            System.out.println("msg "+me.getMessage());
            System.out.println("loc "+me.getLocalizedMessage());
            System.out.println("cause "+me.getCause());
            System.out.println("excep "+me);
            me.printStackTrace();
        }
    }
}
