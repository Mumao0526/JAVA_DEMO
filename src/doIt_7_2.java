import edu.emory.mathcs.backport.java.util.concurrent.TimeUnit;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import javax.annotation.processing.Messager;

import org.fusesource.mqtt.client.BlockingConnection;
import org.fusesource.mqtt.client.MQTT;
import org.fusesource.mqtt.client.Message;
import org.fusesource.mqtt.client.QoS;
import org.fusesource.mqtt.client.Topic;
import org.json.JSONArray;
import org.json.JSONObject;

/* Get data form MQTT and send to API */

public class doIt_7_2 {
    public static void main(String[] args) {
        int coun = 10;
        int counter = 10;
        String API = "";
        ArrayList<String> getData = new ArrayList<String>();
        try {
            MQTT mqtt = new MQTT();
            mqtt.setHost("192.168.0.49", 8002);
            BlockingConnection con = mqtt.blockingConnection();
            con.connect();

            Topic[] topics = { new Topic("CDC72", QoS.EXACTLY_ONCE) };
            con.subscribe(topics);

            while (true) {
                Message message = con.receive(1, TimeUnit.SECONDS);
                if (message != null) {
                    if (counter <= coun) {
                        getData.add(message.toString());
                        counter--;
                    }
                    if (getData.size() >= coun) {
                        try {
                            sendDataToAPI(toJSONObject(getData), API);
                        } catch (Exception e) {
                            // TODO: handle exception
                        }
                    }
                }

            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

    public static void sendDataToAPI(JSONObject json, String API) throws IOException {
        String api = "http://140.128.88.190:3640/test/doIt64_sample.jsp";
        String jsonPayload = jsonToString(json);

        // Startup Connection
        URL url = new URL(api);
        HttpURLConnection con = (HttpURLConnection)url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json; utf-8");
        con.setRequestProperty("Accept", "application/json");
        con.setDoOutput(true);


        try(OutputStream os = con.getOutputStream()){
            byte[] input = jsonPayload.getBytes("utf-8");
            os.write(input, 0, input.length);
        }catch(Exception e){
            e.printStackTrace();
        }
        String resultStr = "";
        try(BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"))){
            StringBuilder r = new StringBuilder();
            String responseLine = null;
            while((responseLine = br.readLine()) != null){
                r.append(responseLine.trim());
            }
            resultStr += r. toString();
        }
        System.out.println(resultStr);

    }

    public static JSONObject toJSONObject(ArrayList data) {
        JSONObject answer = new JSONObject();
        return answer;
    }

    public static String jsonToString(JSONObject json) {

        return "";
    }
}
