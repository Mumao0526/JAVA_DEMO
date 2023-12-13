import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class fortest {
    public static void main(String[] args) {
        ArrayList<String> getData = new ArrayList<String>();
        String nnn = "";
        String aaa = "{\"timeStamp\": \"2022-05-16 09:31:17\", \"data\": [3179, 1353, 2866, 3215, 1468, 2836, 3046, 3109, 4338, 1468]}";
        for (int i = 0; i < 10; i++) {
            nnn += "1";
            getData.add(nnn);
        }
        System.out.println(convertToJSON(getData));

        System.out.println(getDataFromReceive(aaa));
    }

    private static String convertToJSON(ArrayList<String> data) {
        JSONObject answer = new JSONObject();
        List<Integer> intList = new ArrayList<>(data.size());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date now = new Date();
        String formattedDate = dateFormat.format(now); // Get time for now

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
}
