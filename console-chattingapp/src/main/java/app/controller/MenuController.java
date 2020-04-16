package app.controller;

import app.annotations.menu.MenuMapping;
import app.enums.menu.Menu;
import app.handlers.menu.MenuHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;

public class MenuController {
    private MenuHandler handler;
    private static final Logger logger = LogManager.getLogger(MenuController.class);

    public void setHandler(MenuHandler handler) {
        this.handler = handler;
    }

    public void dispatch(Menu menu) throws InvocationTargetException, IllegalAccessException {
        logger.debug("[dispatch] 호출됨");
        Method[] methods = handler.getClass().getMethods();
        Optional<Method> methodOptional = Arrays.stream(methods)
                .filter(method -> method.isAnnotationPresent(MenuMapping.class))
                .filter(method -> method.getAnnotation(MenuMapping.class).menu() == menu)
                .findFirst();
        Method method = methodOptional.orElseThrow(() ->
                new RuntimeException("조건에 맞는 핸들러가 없습니다."));
        method.invoke(handler);
    }
}
