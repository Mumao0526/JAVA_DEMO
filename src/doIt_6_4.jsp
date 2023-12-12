<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="org.jsoup.Jsoup" %>
<%@ page import="org.jsoup.Connection" %>
<%@ page import="java.net.HttpURLConnection" %>
<%@ page import="java.net.URL" %>
<%@ page import="java.io.*" %>

<%!
public static String jsonFileToString(String path) throws IOException {
    StringBuilder result = new StringBuilder();
    InputStream in = new FileInputStream(path);
    InputStreamReader isr = new InputStreamReader(in, "UTF-8");
    BufferedReader buf = new BufferedReader(isr);
    String line;
    while ((line = buf.readLine()) != null) {
        result.append(line).append(System.lineSeparator());
    }
    isr.close();
    return result.toString();
}
%>

<%
String jsonSrcPath = "/opt/tomcat/webapps/test/doit64_input.txt";
String api = "http://140.128.88.190:3640/test/doIt64_sample.jsp";
String jsonPayload = "";

// Startup Connection
URL url = new URL(api);
HttpURLConnection con = (HttpURLConnection)url.openConnection();
con.setRequestMethod("POST");
con.setRequestProperty("Content-Type", "application/json; utf-8");
con.setRequestProperty("Accept", "application/json");
con.setDoOutput(true);

try {
    jsonPayload = jsonFileToString(jsonSrcPath);
    // out.println(jsonPayload);
} catch (Exception e) {
    e.printStackTrace();
}
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
out.println(resultStr);

// try {
//     String result = Jsoup.connect(api)
//                     .validateTLSCertificates(false)
//                     .execute()
//                     .body();
//     out.println(result); // 打印從API收到的回應
// } catch (Exception e) {
//     e.printStackTrace();
// }
%>
