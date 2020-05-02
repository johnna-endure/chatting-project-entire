package io.request;

import java.util.Optional;

public class Request {
    private final Method method;
    private final String url;
    private final Optional<String> bodyOpt;

    public Request(Method method, String url, String body) {
        this.method = method;
        this.url = url;
        this.bodyOpt = Optional.ofNullable(body);
    }

    public Request(Method method, String url) {
        this(method, url, null);
    }

    public Optional<String> getBodyOpt() {
        return bodyOpt;
    }

    public String getUrl() {
        return url;
    }

    public Method getMethod() {
        return method;
    }

    @Override
    public String toString() {
        return "Request{" +
                "method=" + method +
                ", url='" + url + '\'' +
                ", bodyOpt=" + bodyOpt +
                '}';
    }
}
