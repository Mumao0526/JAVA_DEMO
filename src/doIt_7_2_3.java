import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.io.OutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;

/* Data from MQTT:
 * {
 * "timeStamp": "2022-05-16 09:31:17",
 * “data”: [3179, 1353, 2866, 3215, 1468, 2836, 3046, 3109, 4338, 1468 ]
 * }
 *
 * POST to API:
 * {"data": [3179, 1353, 2866, 3215, 1468, 2836, 3046, 3109, 4338, 1468 ]}
 */
public class doIt_7_2_3 {
    private static final int BUFFER_SIZE = 10;
    private static ArrayList<Integer> messageBuffer = new ArrayList<>();
    private static final String MQTT_BROKER = "tcp://192.168.0.40:8002";
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
                        ArrayList<Integer> dataBuffer = getDataFromReceive(messageContent);
                        messageBuffer.addAll(dataBuffer);
                        if (messageBuffer.size() >= BUFFER_SIZE) {
                            ArrayList<Integer> messageData = pollElement(messageBuffer, BUFFER_SIZE);
                            String jsonData = convertToJSON(messageData);
                            System.out.println("send: " + jsonData);
                            String response = sendDataToAPI(jsonData, API_URL);
                            System.out.println("API Response: " + response);
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

    private static String convertToJSON(ArrayList<Integer> data) {
        JSONObject answer = new JSONObject();
        List<Integer> intList = new ArrayList<>(data.size());

        // convert to integer list
        for (Integer s : data) {
            intList.add(Integer.valueOf(s));
        }

        // put in json
        try {
            answer.put("data", intList);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return answer.toString();
    }

    private static ArrayList<Integer> getDataFromReceive(String jsonSrc) {
        ArrayList<Integer> result = new ArrayList<>();

        try {
            JSONObject obj_json = new JSONObject(jsonSrc);
            JSONArray data = obj_json.optJSONArray("data");
            // String timestamp = obj_json.getString("Timestamp");
            for (int i = 0; i < data.length(); i++) {
                result.add(data.getInt(i));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private static ArrayList<Integer> pollElement(ArrayList<Integer> strList, int count) {
        ArrayList<Integer> result = new ArrayList<>();
        Queue<Integer> queue = new LinkedList<>(strList);
        for (int i = 0; i < count; i++) {
            if (queue.size() > 0)
                result.add(queue.poll());
        }
        return result;
    }
}
