import java.util.ArrayList;
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
        List<Integer> result = new ArrayList<>(data.size());

        for (String s : data) {
            result.add(Integer.valueOf(s));
        }

        try {
            answer.put("null", result);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return answer.toString();
    }
}
