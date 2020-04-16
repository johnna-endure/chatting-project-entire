package parser.reqeuest;

import io.request.Method;
import io.request.Request;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;

public class RequestParser {
    private static final Logger logger = LogManager.getLogger(RequestParser.class);
    /**
     * RequestBody 가 존재하는 Message 를 파싱해 Request 객체를 반환합니다.
     */
    public static Request parse(String message) {
        String[] splittedMessage = message.split("\\\\n\\\\n");

        String messageHeader = splittedMessage[0].trim();
        String[] splittedHeader = messageHeader.split(" ");
        Method method = Method.valueOf(splittedHeader[0]);
        String url = splittedHeader[1];

        if(splittedMessage.length == 1){
            Request request = new Request(method, url);
            logger.debug("[parse] Request = {}", request);
            return request;
        } else {
            String messageBody = splittedMessage[1].trim();
            Request request = new Request(method, url, messageBody);
            logger.debug("[parse] Request = {}", request);
            return request;
        }
    }
}
