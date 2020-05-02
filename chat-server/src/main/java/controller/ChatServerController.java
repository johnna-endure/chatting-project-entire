package controller;

import annotations.RequestMapping;
import handler.ChatServerHandler;
import io.request.Request;
import io.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import parser.url.URLParser;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/*
 * 요청 받은 Request를 기반으로 ChatServerHandler 에 있는 핸들러에 매핑하는 역할의 클래스.
 */
public class ChatServerController {
    private static Logger logger = LogManager.getLogger(ChatServerController.class);
    private ChatServerHandler chatServerHandler;

    public ChatServerController(){
        init();
    }
    public void init() {
        chatServerHandler = new ChatServerHandler();
    }

    public Response dispatch(Request request) {
        logger.debug("[dispatch] 호출됨");
        Method[] methods = ChatServerHandler.class.getMethods();

        Optional<Method> handlerOpt = findHandler(methods, request);
        if(handlerOpt.isEmpty()) {
            logger.debug("[dispatch] not found handler");
            return new Response(400,"not found handler");
        }
        Response response = invoke(chatServerHandler, handlerOpt.get(), request);
        logger.debug("response = {}", response);
        return response;
    }

    private Optional<Method> findHandler(Method[] methods, Request request) {
        return Arrays.stream(methods)
                .filter(method -> method.isAnnotationPresent(RequestMapping.class))
                .filter(method -> method.getAnnotation(RequestMapping.class).method() == request.getMethod())
//                .peek(method -> System.out.println(method))
                .filter(method -> URLParser.validateUrl(method.getAnnotation(RequestMapping.class).url(), request.getUrl()))
                .findFirst();
    }

    public <T> Response invoke(T underlyingObject, java.lang.reflect.Method method, Request request) {
        String urlFormat = method.getAnnotation(RequestMapping.class).url();
        String requestUrl = request.getUrl();

        Map<String, String> map = getInnerBraceKeyMap(urlFormat, requestUrl);
        Object[] parameters = mappingParameter(method, request, map);
        try {
            return (Response) method.invoke(underlyingObject, parameters);
        } catch (IllegalAccessException | InvocationTargetException e) {
            String errLog = "invoke reflection error";
            logger.atError().withLocation().withThrowable(e).log(errLog);
            return new Response(500, errLog);
        } catch (IllegalArgumentException e) {
            String errLog = "argument type match error";
            logger.atError().withLocation().withThrowable(e).log(errLog);
            return new Response(500, errLog);
        }
    }

    private Map<String, String> getInnerBraceKeyMap(String urlFormat, String requestUrl) {
        String[] formatFractions = urlFormat.split("/");
        String[] requestUrlFractions = requestUrl.split("/");

        return Stream.iterate(0, i -> i+1)
                .limit(formatFractions.length)
                .filter(i -> formatFractions[i].contains("{"))
                .collect(Collectors.toMap(i -> formatFractions[i].substring(1,formatFractions[i].length()-1),
                        i -> requestUrlFractions[i]));
    }

    private Object[] mappingParameter( java.lang.reflect.Method method,
                                       Request request, Map<String, String> innerBraceKeyMap) {
        Parameter[] declaredParameters = method.getParameters();
        Object[] parameters = new Object[declaredParameters.length];

        Iterator<Map.Entry<String, String>> entryIterator = innerBraceKeyMap.entrySet().iterator();
        while(entryIterator.hasNext()) {
            Map.Entry<String, String> entry = entryIterator.next();
            String key = entry.getKey();
            String value = entry.getValue();
            // url 포맷의 중괄호 값 매핑
            Stream.iterate(0, i -> i+1)
                    .limit(declaredParameters.length)
                    .filter(i -> declaredParameters[i].getName().equals(key))
                    .forEach(i -> {
                        if(declaredParameters[i].getType() == int.class) {
                            parameters[i] = Integer.parseInt(value);
                        }else if(declaredParameters[i].getType() == long.class){
                            parameters[i] = Long.parseLong(value);
                        }else if(declaredParameters[i].getType() == double.class){
                            parameters[i] = Double.parseDouble(value);
                        }else{ //객체형 파라미터의 경우
                            parameters[i] = value;
                        }
                    });
        }
        // 파라미터에 Request 있을 경우 매핑
        Stream.iterate(0, i -> i+1)
                .limit(declaredParameters.length)
                .filter(i -> declaredParameters[i].getType() == Request.class)
                .forEach(i -> {
                    parameters[i] = request;
                });
        logger.debug("[mappingParameter] parameters = "+Arrays.toString(parameters));
        return parameters;
    }

}
