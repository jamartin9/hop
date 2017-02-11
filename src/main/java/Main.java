import io.reactivex.*;
import org.eclipse.jetty.websocket.api.Session;
import wrapper.SocketWrapper;

import static spark.Spark.*;

import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Main{

    public static void main(String [] args) throws InterruptedException {

        // socket server
        webSocket("/echo", SocketWrapper.class);
        init();
        // poll connections and print out all messages async
        Map<Session, Flowable<String>> sessions = SocketWrapper.getConnections();
        while(true){
            int socketcount = 0;
            for(Session session: sessions.keySet()){
                socketcount++;
                int mycount = socketcount;
                // get the flowable of messages
                Flowable<String> source_one = sessions.get(session);
                // background thread
                //source_one.subscribeOn(Schedulers.newThread()).subscribe((String s) -> {
                //        System.out.println("Subscriber "+mycount+" "+s);
                //});
                source_one.subscribe((String s) -> {
                    System.out.println("Subscriber "+mycount+" "+s);
                });
            }
            Thread.sleep(TimeUnit.MILLISECONDS.toMillis(10));
        }
    }


}