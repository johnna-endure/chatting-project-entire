package room;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
/*
key : 포트번호
value : 채팅방 스레드
 */
public class RoomThreadPool {
    private Map<Integer, RoomThread> roomThreads;
    private static Logger logger = LogManager.getLogger(RoomThreadPool.class);

    public RoomThreadPool(){
        roomThreads = new ConcurrentHashMap<>();
    }

    public RoomThread putRoomThread(int port, RoomThread roomThread) {
        logger.debug("[putRoomThread] port = {}인 RoomThread 추가됨. ", port);
        return roomThreads.put(port, roomThread);
    }

    public RoomThread getRoomThread(int port) {
        return roomThreads.get(port);
    }

    public void deleteRoomThread(int port){
        RoomThread roomThread = roomThreads.remove(port);
        roomThread.exitThread();
    }

}
