package io.response;

import java.util.Optional;

/**
 * statusCode 는 필수이다.
 * description, responseBody 생략될 수 있다.
 */
public class Response {
    private final int statusCode;
    private final Optional<String> descriptionOpt, responseBodyOpt;

    public Response(int statusCode) {
        this(statusCode, null,  null);
    }

    public Response(int statusCode, String description) {
        this(statusCode, description,  null);
    }

    public Response(int statusCode, String description, String responseBody) {
        this.statusCode = statusCode;
        this.descriptionOpt = Optional.ofNullable(description);
        this.responseBodyOpt = Optional.ofNullable(responseBody);
    }

    public int getStatusCode() {
        return statusCode;
    }

    public Optional<String> getDescriptionOpt() {
        return descriptionOpt;
    }

    public Optional<String> getResponseBodyOpt() {
        return responseBodyOpt;
    }

    @Override
    public String toString() {
        return "Response{" +
                "statusCode=" + statusCode +
                ", descriptionOpt=" + descriptionOpt +
                ", responseBodyOpt=" + responseBodyOpt +
                '}';
    }
}