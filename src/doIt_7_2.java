import java.util.concurrent.TimeUnit;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.fusesource.mqtt.client.BlockingConnection;
import org.fusesource.mqtt.client.MQTT;
import org.fusesource.mqtt.client.Message;
import org.fusesource.mqtt.client.QoS;
import org.fusesource.mqtt.client.Topic;
import org.json.JSONException;
import org.json.JSONObject;

/* Get data form MQTT and send to API */

public class doIt_7_2 {
    public static void main(String[] args) {
        ArrayList<String> getData = new ArrayList<String>(); // data buffer
        int coun = 10; // data quantity
        String API = "http://140.128.88.190:3640/test/doIt64_sample.jsp";

        String MQTT_host = "192.168.0.49";
        int MQTT_port = 8002;
        String MQTT_topicName = "CDC72";

        try {
            // Connect to MQTT
            MQTT mqtt = new MQTT();
            mqtt.setHost(MQTT_host, MQTT_port);
            BlockingConnection con = mqtt.blockingConnection();

            // Attempting to connect
            con.connect();
            System.out.println("Connected to MQTT Broker at " + MQTT_host + ":" + MQTT_port);

            // Set subscribe topic
            Topic[] topics = { new Topic(MQTT_topicName, QoS.EXACTLY_ONCE) };
            con.subscribe(topics);
            System.out.println("Subscribed to topic: " + MQTT_topicName);

            System.out.println("Start!");

            // waiting for data
            while (true) {
                Message message = con.receive(1, TimeUnit.SECONDS); // require data by 1 times/seconds
                if (message != null) {
                    String msgContent = new String(message.getPayload());
                    System.out.println("Get data from MQTT: " + msgContent);
                    if (getData.size() < coun) {
                        getData.add(msgContent);
                    } else {
                        try {
                            String feedback = sendDataToAPI(convertToJSON(getData), API);
                            System.out.println("API Response: " + feedback);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        getData.clear();    // flush data buffer
                    }
                } else {
                    System.out.println("No message received within the time limit.");
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String sendDataToAPI(String json, String API) throws IOException {
        String jsonPayload = json;

        // Startup Connection
        URL url = new URL(API);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json; utf-8");
        con.setRequestProperty("Accept", "application/json");
        con.setDoOutput(true);

        try (OutputStream os = con.getOutputStream()) {
            byte[] input = jsonPayload.getBytes("utf-8");
            os.write(input, 0, input.length);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String resultStr = "";
        try (BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"))) {
            StringBuilder r = new StringBuilder();
            String responseLine = null;
            while ((responseLine = br.readLine()) != null) {
                r.append(responseLine.trim());
            }
            resultStr += r.toString();
        }
        System.out.println(resultStr); // debug
        return resultStr;
    }

    private static String convertToJSON(ArrayList<String> data) {
        JSONObject answer = new JSONObject();
        List<Integer> intList = new ArrayList<>(data.size());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date now = new Date();
        String formattedDate = dateFormat.format(now);  // Get time for now

        // convert to integer list
        for (String s : data) {
            intList.add(Integer.valueOf(s));
        }

        // put in json
        try {
            answer.put("timeStamp", formattedDate);
            answer.put("data", intList);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return answer.toString();
    }
}
