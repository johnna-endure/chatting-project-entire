package app.parser;

import app.io.response.Response;

public class ResponseParser {
    public static Response messageConvertToResponse(String responseMessage) {
        String[] splittedMsg = responseMessage.split("\\\\n\\\\n");
        //헤더 작업
        String responseHeader = splittedMsg[0].trim();
        String[] splittedHeader = responseHeader.split(" ");
        int statusCode = Integer.parseInt(splittedHeader[0]);
        String desc = null;
        if(splittedHeader.length > 1) desc = splittedHeader[1];

        //바디 작업
        String responseBody = null;
        if(splittedMsg.length > 1){
            responseBody = splittedMsg[1].trim();
        }

        return new Response(statusCode, desc, responseBody);
    }

}
