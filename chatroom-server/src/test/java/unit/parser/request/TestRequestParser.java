package unit.parser.request;

import io.request.Method;
import io.request.Request;
import org.junit.Ignore;
import org.junit.Test;
import parser.reqeuest.RequestParser;

import static org.assertj.core.api.Assertions.assertThat;

public class TestRequestParser {

    @Test
    public void whenHasEmptyBodyURL() {
        String request = "GET /room/1";
        Request actual = RequestParser.parse(request);
        Request expected = new Request(Method.GET, "/room/1");
        assertThat(actual).isEqualToComparingFieldByField(expected);
    }

    @Test
    public void whenHasEmptyBodyAndHasSpaceAtLast() {
        String request = "GET /room/1  ";
        Request actual = RequestParser.parse(request);
        Request expected = new Request(Method.GET, "/room/1");
        assertThat(actual).isEqualToComparingFieldByField(expected);
    }

    @Test
    public void whenHasEmptyBodyAndHasSpaceAtFirst() {
        String request = "   GET /room/1";
        Request actual = RequestParser.parse(request);
        Request expected = new Request(Method.GET, "/room/1");
        assertThat(actual).isEqualToComparingFieldByField(expected);
    }

    @Test
    public void whenHasEmptyBodyAndHasSpaceAtBothSide() {
        String request = "   GET /room/1   ";
        Request actual = RequestParser.parse(request);
        Request expected = new Request(Method.GET, "/room/1");
        assertThat(actual).isEqualToComparingFieldByField(expected);
    }

    @Test
    public void whenHasEmptyBodyAndCarriageReturnURL() {
        String request = "GET /room/1\\n\\n";
        Request actual = RequestParser.parse(request);
        Request expected = new Request(Method.GET, "/room/1");
        assertThat(actual).isEqualToComparingFieldByField(expected);
    }

    /*
    추가적인 캐리지 리턴 문자가 발견되면 실패함.
    request 포맷에 명시를 하고 이 부분이 지켜지도록 강제할수도 있으나
    메서드에 기능을 추가해 허용하도록 할 수도 있다.
    나중에 기능 추가할 것.

    메모.
    정규표현식 그룹화 기능을 이용해 \\n 사이에 있는 문자열 빼낼 수 있음.
     */
    @Ignore
    @Test()
    public void whenTooMuchCarriage() {
        String request = "GET /room/1\\n\\nbody\\n";
        Request actual = RequestParser.parse(request);
        Request expected = new Request(Method.GET, "/room/1","body");
        assertThat(actual).isEqualToComparingFieldByField(expected);
    }

}
