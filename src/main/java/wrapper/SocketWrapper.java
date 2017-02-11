package wrapper;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import org.eclipse.jetty.websocket.api.*;
import org.eclipse.jetty.websocket.api.annotations.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;

/**
 * Simple Jetty Websocket class that enables:
 * 1. broadcast of messages received from websockets via Flowables
 * 2. ability to write to the client via the raw session
 */
@WebSocket
public class SocketWrapper {

    private static final Map<Session, Flowable<String>> sessions = new ConcurrentHashMap<>();
    private static final Map<Session, Queue<String>> buffers = new ConcurrentHashMap<>();

    public static Map<Session, Flowable<String>> getConnections(){
        return sessions;
    }

    @OnWebSocketConnect
    public void connected(Session session) {
        // create buffer
        buffers.put(session, new ConcurrentLinkedQueue<>());
        // flowable to consume buffer
        Flowable<String> flow = Flowable.create(
                // called on each subscriptions, no history
                (FlowableEmitter<String> emitter) -> {
                    // get the queue of incoming messages
                    Queue<String> buffer = buffers.get(session);

                    // poll while the session is active
                    while(sessions.containsKey(session)) {
                        // user hipster
                        // db datum
                        // pass roflcopter
                        // port 5432
                        // address 127.0.0.1
                        //Database db = Database.from("jdbc:postgresql://localhost/datum?user=hipster&password=roflcopter");
                        //Database adb = db.asynchronous();
                        // db results
                        // from sync request publish when done
                        //emitter.onNext(db.select("select * from user").getAs(String.class).toBlocking().single());
                        //emitter.onNext(adb.select("select * from user").getAs(String.class).limit(1).toBlocking().single());
                        // async request

                        // get all off buffer
                        while(!buffer.isEmpty()){
                            //re publish
                            emitter.onNext(buffer.poll());
                        }
                        Thread.sleep(1);
                    }

                    // session is done
                    emitter.onComplete();

                }, BackpressureStrategy.BUFFER);
        // session with flowable for connections
        sessions.put(session, flow);
    }

    @OnWebSocketClose
    public void closed(Session session, int statusCode, String reason) {
        buffers.get(session).add("Session Closed: "+reason+" Status Code: "+statusCode);
        sessions.remove(session);
        buffers.remove(session);
    }

    @OnWebSocketMessage
    public void message(Session session, String message) throws IOException {
        buffers.get(session).add(message);
        session.getRemote().sendString(message);
    }

}
