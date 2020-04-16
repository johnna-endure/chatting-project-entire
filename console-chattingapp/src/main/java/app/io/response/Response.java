package app.io.response;

import java.util.Optional;

/**
 * statusCode 는 필수이다.
 * description, responseBody 생략될 수 있다.
 */
public class Response {
    private final int statusCode;
    private final String description, responseBody;

    public Response(int statusCode) {
        this(statusCode, null,  null);
    }

    public Response(int statusCode, String description) {
        this(statusCode, description,  null);
    }

    public Response(int statusCode, String description, String responseBody) {
        this.statusCode = statusCode;
        this.description = description;
        this.responseBody = responseBody;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public Optional<String> getDescriptionOpt() {
        return Optional.ofNullable(description);
    }

    public Optional<String> getResponseBodyOpt() {
        return Optional.ofNullable(responseBody);
    }

    @Override
    public String toString() {
        return "Response{" +
                "statusCode=" + statusCode +
                ", descriptionOpt=" + description +
                ", responseBodyOpt=" + responseBody +
                '}';
    }
}