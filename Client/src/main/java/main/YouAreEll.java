package main;

import controllers.*;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class YouAreEll {

    private MessageController msgCtrl;
    private IdController idCtrl;

    public YouAreEll (MessageController m, IdController j) {
        // used j because i seems awkward
        this.msgCtrl = m;
        this.idCtrl = j;
    }

    public static void main(String[] args) {
        // hmm: is this Dependency Injection?
        YouAreEll urlhandler = new YouAreEll(new MessageController(), new IdController());
        System.out.println(urlhandler.MakeURLCall("/ids", "GET", ""));
        System.out.println(urlhandler.MakeURLCall("/messages", "GET", ""));
    }

    public String get_ids() {
        return MakeURLCall("/ids", "GET", "");
    }
    public String post_ids(String id,String name) {

        return MakeURLCall("/ids", "POST", "{\n" +
                "    \"userid\": \"-\",\n" +
                "    \"name\": \""+name+"\",\n" +
                "    \"github\": \""+id+"\"\n" +
                "}");
    }

    public String get_messages() {
        return MakeURLCall("/messages", "GET", "");
    }
    public String get_messages_id(String id) {

        return MakeURLCall("/ids/"+id+"/messages", "GET", "");
    }
    public String send_message_id(String id,String message) {

        return MakeURLCall("/ids/"+id+"/messages", "POST", "{\n" +
                "    \"sequence\": \"-\",\n" +
                "    \"fromid\": \""+id+"\",\n" +
                "    \"toid\": \"\",\n" +
                "    \"message\": \""+message+"\"\n" +
                "  }");
    }
    public String send_message_to_friend_id(String id,String message,String fid) {

        return MakeURLCall("/ids/"+id+"/messages", "POST", "{\n" +
                "    \"sequence\": \"-\",\n" +
                "    \"fromid\": \""+id+"\",\n" +
                "    \"toid\": \""+fid+"\",\n" +
                "    \"message\": \""+message+"\"\n" +
                "  }");
    }


    public String MakeURLCall(String mainurl, String method, String jpayload) {
        try {

            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpResponse response = null;
            if(method.equals("GET")) {
                HttpGet getRequest = new HttpGet(
                        "http://zipcode.rocks:8085" + mainurl);
                getRequest.addHeader("accept", "application/json");
                response = httpClient.execute(getRequest);
            }
            else if(method.equals("POST")) {
                System.out.println(jpayload);
                HttpPost postRequest = new HttpPost(
                        "http://zipcode.rocks:8085" + mainurl);

                StringEntity input = new StringEntity(jpayload);
                input.setContentType("application/json");
                postRequest.setEntity(input);
                response = httpClient.execute(postRequest);
            }
            else if(method.equals("PUT")) {
                HttpPut  httpPut = new HttpPut("http://zipcode.rocks:8085" + mainurl);
                httpPut.setHeader("Accept", "application/json");
                httpPut.setHeader("Content-type", "application/json");

                StringEntity stringEntity = new StringEntity(jpayload);
                httpPut.setEntity(stringEntity);
                response = httpClient.execute(httpPut);
            }



            if (response.getStatusLine().getStatusCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + response.getStatusLine().getStatusCode());
            }


            ResponseHandler<String> handler = new BasicResponseHandler();
            String body = handler.handleResponse(response);
            httpClient.getConnectionManager().shutdown();

            return body;

        } catch (ClientProtocolException e) {

            e.printStackTrace();

        } catch (IOException e) {

            e.printStackTrace();
        }

        return null;
    }
}
