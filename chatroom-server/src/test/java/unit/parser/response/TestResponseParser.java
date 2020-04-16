package unit.parser.response;

import io.response.Response;
import org.junit.Test;
import parser.response.ResponseParser;

import static org.assertj.core.api.Assertions.assertThat;

public class TestResponseParser {

    @Test
    public void whenOnlyStatus() {
        Response response = new Response(200);
        assertThat(ResponseParser.parse(response)).isEqualTo("200");
    }

    @Test
    public void whenExceptOnlyDesc() {
        Response response = new Response(200,null,"body");
        assertThat(ResponseParser.parse(response)).isEqualTo("200\\n\\nbody");
    }

    @Test
    public void whenExceptOnlyBoby() {
        Response response = new Response(200,"desc");
        assertThat(ResponseParser.parse(response)).isEqualTo("200 desc");
    }

    @Test
    public void whenAllExist() {
        Response response = new Response(200,"desc","body");
        assertThat(ResponseParser.parse(response)).isEqualTo("200 desc\\n\\nbody");
    }



}
