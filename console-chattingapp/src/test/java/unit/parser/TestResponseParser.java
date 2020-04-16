package unit.parser;

import app.io.response.Response;
import app.parser.ResponseParser;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class TestResponseParser {

    @Test
    public void testMessageConvertToResponse_whenAllFieldsExist() {
        String message = "200 desc\\n\\nbody";
        Response expected = new Response(200, "desc","body");

        assertThat(ResponseParser.messageConvertToResponse(message)).isEqualToComparingFieldByField(expected);
    }

    @Test
    public void testMessageConvertToResponse_whenOnlyStatusCodeExists() {
        String message = "200";
        Response expected = new Response(200, null,null);

        assertThat(ResponseParser.messageConvertToResponse(message)).isEqualToComparingFieldByField(expected);
    }

    @Test
    public void testMessageConvertToResponse_whenStatusCodeAndDescExist() {
        String msg = "200 desc";
        Response expected = new Response(200, "desc", null);
        assertThat(ResponseParser.messageConvertToResponse(msg)).isEqualToComparingFieldByField(expected);
    }

    @Test
    public void testMessageConvertToResponse_whenStatusCodeAndBodyExist() {
        String msg = "200\\n\\nbody";
        Response expected = new Response(200, null, "body");
        assertThat(ResponseParser.messageConvertToResponse(msg)).isEqualToComparingFieldByField(expected);
    }

}
