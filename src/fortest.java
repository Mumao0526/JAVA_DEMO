import java.util.ArrayList;

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
        System.out.println(toJsonFormatString(getData));
    }

    public static String toJsonFormatString(ArrayList data) {
        JSONObject answer = new JSONObject();

        try {
            answer.append("data", data.toString());
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return answer.toString();
    }
}
