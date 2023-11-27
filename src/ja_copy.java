import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.json.JSONObject;
import org.json.JSONArray;

class ja_copy {
    public static void main(String[] args) throws FileNotFoundException {
        try {
            String jsonSrc = jsonFileToString("doit_5_2.json");

            JSONArray arr_json = new JSONArray(jsonSrc);
            for (int i = 0; i < arr_json.length(); i++) {
                JSONObject obj_json = arr_json.getJSONObject(i);
                String barcode = obj_json.getString("Barcode");
                String timestamp = obj_json.getString("Timestamp");

                if(barcode.equals("95M00047"))
                {
                    System.out.println("{\"Barcode\":\"95M00047\",\"Timestamp\":\"" + timestamp + "\"}");
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static String jsonFileToString(String path) throws IOException{
        StringBuilder result = new StringBuilder();
        InputStream in = new FileInputStream(path);
        InputStreamReader isr = new InputStreamReader(in, "UTF-8");
        BufferedReader buf = new BufferedReader(isr);
        String line = null;
        while ((line = buf.readLine()) != null) {
            result.append(System.lineSeparator() + line);
        }
        isr.close();
        return result.toString();
    }

}