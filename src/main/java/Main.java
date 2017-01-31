import com.github.davidmoten.rx.jdbc.Database;
import io.reactivex.*;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class Main{



    public static void main(String [] args) throws InterruptedException {
        // user hipster
        // db datum
        // pass roflcopter
        // port 5432
        // address 127.0.0.1
        Database db = Database.from("jdbc:postgresql://localhost/datum?user=hipster&password=roflcopter");
        Database adb = db.asynchronous();

        // async dude and string can be anything
        Flowable<String> source_one = Flowable.create(
                // no one likes anonymous clases anymore..,
                (FlowableEmitter<String> emitter) -> {
                    int time = 10;
                    while(time > 0) {
                        time--;
                        // db results
                        // from sync request publish when done 
                        emitter.onNext(db.select("select * from user").getAs(String.class).toBlocking().single());

                        emitter.onNext("BLOCKING AFTER SYNC");
                        Thread.sleep(100);

                        emitter.onNext(adb.select("select * from user").getAs(String.class).limit(1).toBlocking().single());
                        // async request

                        emitter.onNext("BLOCKING AFTER ASYNC");
                        Thread.sleep(100);
                        // publish dee and dumb
                        emitter.onNext("dee");
                        emitter.onNext("dumb");

                        // chill
                        Thread.sleep(1);
                    }
                    //done bro
                    emitter.onComplete();

        }, BackpressureStrategy.BUFFER/* try hard or DROP*/);

        // simulate reader one and two from same aggregated source

        // background thread
        source_one.subscribeOn(Schedulers.newThread()).subscribe((String s) -> {
                System.out.println("Subscriber 1: " +s);
            }
        );

        // foreground so main doesn't exit until complete
        source_one.subscribe((String s) -> {
            System.out.println("Subscriber 2: "+s);
            Thread.sleep(100);
        });

    }


}