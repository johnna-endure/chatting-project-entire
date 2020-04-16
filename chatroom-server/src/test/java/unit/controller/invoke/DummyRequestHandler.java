package unit.controller.invoke;

import com.google.common.annotations.VisibleForTesting;
import handler.RequestMapping;
import io.request.Method;
import io.request.Request;
import io.response.Response;

import java.lang.invoke.MethodHandle;

public class DummyRequestHandler {
    @RequestMapping(method = Method.POST, url = "/hello/{id}/{name}")
    public Response bracedURLMethod(long id, Request request, String name) {
        return new Response(200);
    }

    @RequestMapping(method = Method.GET, url = "/hello")
    public Response plainURLMethod(Request request) {
        return new Response(200);
    }

    @RequestMapping(method = Method.GET, url = "/hello/{id}/name")
    public Response hybridURLMethod(int id, Request request) {
        return new Response(200);
    }

    @RequestMapping(method = Method.GET, url = "/hello")
    public Response noArgumentMethod() {
        return new Response(200);
    }

    // 아래부터 실제 url
    @RequestMapping(method = Method.POST, url = "/room")
    public Response createRoom(){return new Response(200,"createRoom");}

    @RequestMapping(method = Method.POST, url = "/room/{id}")
    public Response joinRoom(){return new Response(200, "joinRoom");}
}
