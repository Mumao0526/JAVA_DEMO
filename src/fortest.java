import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

public class fortest {
    public static void main(String[] args) {
        ArrayList<String> getData = new ArrayList<String>();
        String nnn = "";
        for (int i = 0; i < 10; i++) {
            nnn += "1";
            getData.add(nnn);
        }
        System.out.println(convertToJSON(getData));
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
