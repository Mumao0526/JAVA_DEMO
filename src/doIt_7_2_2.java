import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.io.OutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class doIt_7_2_2 {
    private static final int BUFFER_SIZE = 10;
    private static ArrayList<String> messageBuffer = new ArrayList<>();
    private static final String MQTT_BROKER = "tcp://192.168.0.49:8002";
    private static final String MQTT_TOPIC = "CDC72";
    private static final String API_URL = "http://140.128.88.190:3640/test/doIt72.jsp";

    public static void main(String[] args) {
        try {
            MqttClient client = new MqttClient(MQTT_BROKER, MqttClient.generateClientId(), new MemoryPersistence());
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);

            System.out.println("Connecting to MQTT broker: " + MQTT_BROKER);
            client.connect(connOpts);
            System.out.println("Connected");
            System.out.println("Ready to receive data.");

            client.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    System.out.println("Connection lost: " + cause.getMessage());
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    String messageContent = new String(message.getPayload());
                    System.out.println("Received message: " + messageContent);

                    synchronized (messageBuffer) {
                        messageBuffer.add(messageContent);
                        if (messageBuffer.size() >= BUFFER_SIZE) {
                            String jsonData = convertToJSON(messageBuffer);
                            System.out.println("send: " + jsonData);
                            String response = sendDataToAPI(jsonData, API_URL);
                            System.out.println("API Response: " + response);
                            messageBuffer.clear();
                        }
                    }
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    // not used in this example
                }
            });

            System.out.println("Subscribing to topic: " + MQTT_TOPIC);
            client.subscribe(MQTT_TOPIC);

        } catch (MqttException me) {
            System.out.println("Reason " + me.getReasonCode());
            System.out.println("Message " + me.getMessage());
            System.out.println("Localized Message " + me.getLocalizedMessage());
            System.out.println("Cause " + me.getCause());
            System.out.println("Exception " + me);
            me.printStackTrace();
        }
    }

    private static String sendDataToAPI(String data, String api_url) {
        String resultStr = "";
        try {
            URL url = new URL(api_url);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = data.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"))) {
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                resultStr += response.toString();
            }

            conn.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultStr;
    }
    private static String convertToJSON(ArrayList<String> data) {
        JSONObject answer = new JSONObject();
        List<Integer> result = new ArrayList<>(data.size());

        // convert to integer list
        for (String s : data) {
            result.add(Integer.valueOf(s));
        }

        // put in json
        try {
            answer.put("null", result);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return answer.toString();
    }
}
