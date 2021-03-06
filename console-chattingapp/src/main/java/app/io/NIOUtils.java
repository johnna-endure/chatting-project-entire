package app.io;

import app.io.response.Response;
import app.parser.ResponseParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousCloseException;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Optional;

public class NIOUtils {
    private static final Logger logger = LogManager.getLogger(NIOUtils.class);

    public Optional<SocketChannel> accept(ServerSocketChannel serverSocketChannel) throws InterruptedException {
        try {
            return Optional.of(serverSocketChannel.accept());
        } catch (AsynchronousCloseException e){
            logger.atError().withThrowable(e).withLocation().log(e.getMessage());
            throw new InterruptedException();
        } catch (IOException e) {
            logger.atError().withThrowable(e).withLocation().log(e.getMessage());
            return Optional.empty();
        }
    }

    public void write(String message, SocketChannel socketChannel) {
        try {
            socketChannel.write(ByteBuffer.wrap(message.getBytes()));
        } catch (IOException e) {
            logger.atError().withThrowable(e).withLocation().log(e.getMessage());
            close(socketChannel);
            throw new RuntimeException(e);
        }
    }
/*
버퍼 넘는 경우도 모두 read 할 수 있도록 수정 필요.
 */
    public String read(SocketChannel socketChannel) {
        StringBuilder sb = new StringBuilder();
        ByteBuffer buffer = ByteBuffer.allocate(1000);
        try {
            int reads = socketChannel.read(buffer);
            buffer.flip();
            byte[] dst = new byte[reads];
            buffer.get(dst);
            sb.append(new String(dst));
            return sb.toString();
        } catch (IOException e) {
            logger.atError().withThrowable(e).withLocation().log(e.getMessage());
            close(socketChannel);
            throw new RuntimeException(e);
        }
    }

    public Response receiveResponse(SocketChannel senderSocket) throws IOException {
        String responseMessage = read(senderSocket);
        logger.debug("[receiveResponse] responseMessage = {}", responseMessage);
        Response response = ResponseParser.messageConvertToResponse(responseMessage);
        logger.debug("[receiveResponse] Response = {}", response);
        return response;
    }

    public void sendRequest(String request, SocketChannel senderSocket){
        logger.debug("[sendRequest] request = {}", request);
        write(request, senderSocket);
    }

    public void close(SocketChannel socketChannel) {
        try {
            socketChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
