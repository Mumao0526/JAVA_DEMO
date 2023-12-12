<%@ page language="java" contentType="test/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.stream.Collectors" %>
<%@ page import="org.json.JSONObject" %>
<%@ page import="org.json.JSONArray" %>
<%@ page import="java.text.ParseException" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.Date" %>


<%
/*
 * << Introduction to Production Data Center Course >>
 * This API: http://140.128.88.190:3649/test/do_6_3.jsp
 *
 * PURPOSE: Get barcode which Timestamp between startTime and endTime.
 * EXAMPLE:
 *      INPUT:
 *          {
                "startTime": "2019-06-18 09:32:47",
                "endTime": "2019-06-19 09:32:47",
                "data": [
                    {"Timestamp":"2019-06-18 09:32:47.912","Barcode":"95M00032"},
                    {"Timestamp":"2019-06-18 09:35:11.485","Barcode":"95M00001"},
                    {"Timestamp":"2019-06-18 09:41:06.870","Barcode":"95M00043"}
            ]
 *      OUTPUT: 95M00001 95M00043
}
*/

// If get data via POST
if("POST".equalsIgnoreCase(request.getMethod())){
    String jsonSrc = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));   // to string

    JSONObject jsonObject = new JSONObject(jsonSrc);
    String startTimeStr = jsonObject.getString("startTime");
    String endTimeStr = jsonObject.getString("endTime");
    JSONArray dataArray = jsonObject.getJSONArray("data");

    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  // Create timeData format

    try {
        // Transfer to my timeData format
        Date startTime = dateFormat.parse(startTimeStr);
        Date endTime = dateFormat.parse(endTimeStr);

        for (int i = 0; i < dataArray.length(); i++) {
            JSONObject dataObject = dataArray.getJSONObject(i);
            String timestampStr = dataObject.getString("Timestamp").split("\\.")[0];    // we don't need microseconds
            Date timestamp = dateFormat.parse(timestampStr);

            // startTime < Needed < endTime
            if (timestamp.after(startTime) && timestamp.before(endTime)) {
                out.println(dataObject.getString("Barcode"));
            }
        }
    } catch (ParseException e) {
        e.printStackTrace();    // print to the log
    }
}

%>