package unit.controller.invoke;

import controller.ChatRoomController;
import io.request.Method;
import io.request.Request;
import io.response.Response;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public class TestChatRoomController_invoke {
    ChatRoomController controller;
    DummyRequestHandler requestHandler;

    @Before
    public void init() {
        controller = new ChatRoomController();
        requestHandler = new DummyRequestHandler();
    }

    @Test
    public void testInvoke_bracedUrlMethod() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        java.lang.reflect.Method method = getMethod(DummyRequestHandler.class, "bracedURLMethod");
        Response response = controller.invoke(requestHandler,
                        method,
                        new Request(Method.POST, "/hello/2/cws"));
        assertThat(response).isEqualToComparingFieldByField(new Response(200));
    }

    @Test
    public void testInvoke_whenPlainURLMethod() {
        java.lang.reflect.Method method =
                getMethod(DummyRequestHandler.class, "plainURLMethod");
        Response actual = controller.invoke(requestHandler,
                method,
                new Request(Method.GET, "/hello"));
        assertThat(actual).isEqualToComparingFieldByField(new Response(200));
    }

    @Test
    public void testInvoke_whenHybridURLMethod() {
        java.lang.reflect.Method method =
                getMethod(DummyRequestHandler.class, "hybridURLMethod");

        Response actual = controller.invoke(requestHandler,
                method,
                new Request(Method.GET, "/hello/23/name"));
        assertThat(actual).isEqualToComparingFieldByField(new Response(200));
    }

    @Test
    public void testInvoke_whenNoArgumentMethod() {
        java.lang.reflect.Method method =
                getMethod(DummyRequestHandler.class, "noArgumentMethod");

        Response actual = controller.invoke(requestHandler,
                method,
                new Request(Method.GET, "/hello"));
        assertThat(actual).isEqualToComparingFieldByField(new Response(200));
    }

    @Test
    public void testInvoke_joinRoom() {
        java.lang.reflect.Method method =
                getMethod(DummyRequestHandler.class, "joinRoom");
        Response actual = controller.invoke(requestHandler,
                method,
                new Request(Method.POST, "/room/1"));
        assertThat(actual).isEqualToComparingFieldByField(
                new Response(200, "joinRoom"));
    }


    private java.lang.reflect.Method getMethod(Class clazz, String methodName) {
        return Arrays.stream(clazz.getMethods())
                .filter(m -> m.getName().equals(methodName))
                .findFirst().get();
    }
}
