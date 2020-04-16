package parser.response;

import io.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;


public class ResponseParser {
    private static final Logger logger = LogManager.getLogger(ResponseParser.class);
    /**
     * Response 객체를 메세지 프로토콜에 맞는 String 으로 변환합니다.
     */
    public static String parse(Response response){
        int statusCode = response.getStatusCode();
        Optional<String> descOpt = response.getDescriptionOpt();
        Optional<String> bodyOpt = response.getResponseBodyOpt();

        String messageHead = makeMessageHead(statusCode, descOpt);
        String parsedResponse = appendOptionalBody(messageHead, bodyOpt);
        logger.debug("[parse] parsedResponse = \"{}\"", parsedResponse);
        return parsedResponse;
    }

    /**
     *
     * @param statusCode 응답 상태코드
     * @param descOpt 응답 상태 설명
     * @return descOpt 이 존재하는 경우 desc 를 포함한 헤드를 반환, 없는 경우 상태 코드만을 반환.
     */
    private static String makeMessageHead(int statusCode, Optional<String> descOpt) {
        return descOpt.map(desc -> statusCode + " " + desc)
                .orElseGet(() -> String.valueOf(statusCode));
    }

    /**
     *
     * @param messageHead 메세지 head. method url 에 해당함.
     * @param bodyOpt 메세지 body 에 해당.
     * @return body 가 있는 경우 바디를 포함한 메세지를 반환, 없는 경우 헤더만 반환.
     */
    private static String appendOptionalBody(String messageHead, Optional<String> bodyOpt) {
        String body = bodyOpt.orElse("");
        if(body.equals("") || body.equals("[]")) return messageHead;
        return messageHead + "\\n\\n" + body;
    }




}
