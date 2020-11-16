package bgu.spl.mics.application;

import bgu.spl.mics.Publisher;
import bgu.spl.mics.Subscriber;
import bgu.spl.mics.application.passiveObjects.Diary;
import bgu.spl.mics.application.passiveObjects.Inventory;
import bgu.spl.mics.application.passiveObjects.MissionInfo;
import bgu.spl.mics.application.passiveObjects.Squad;
import bgu.spl.mics.application.publishers.Intelligence;
import bgu.spl.mics.application.publishers.TimeService;
import bgu.spl.mics.application.subscribers.M;
import bgu.spl.mics.application.subscribers.Moneypenny;
import bgu.spl.mics.application.subscribers.Q;
import com.google.gson.Gson;
import sun.misc.GThreadHelper;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.concurrent.*;

/**
 * This is the Main class of the application. You should parse the input file,
 * create the different instances of the objects, and run the system.
 * In the end, you should output serialized objects.
 */
public class MI6Runner {
    public static void main(String[] args) throws FileNotFoundException, InterruptedException {
        Gson g = new Gson();
        GsonObject j = g.fromJson(new FileReader(args[0]), GsonObject.class);
        Inventory.getInstance().load(j.inventory);
        Squad.getInstance().load(j.squad);
        GsonObject.Service s = j.services;
        int numberOfM = s.M;
        int numberOfMP = s.Moneypenny;
        GsonObject.Service.GsonIntelligence[] intelligences = s.intelligence;
        CountDownLatch countDownLatch= new CountDownLatch(numberOfM+numberOfMP+intelligences.length+1); 	
        ExecutorService MExecuter = Executors.newCachedThreadPool();
        ExecutorService QITexecuter = Executors.newCachedThreadPool();
        ExecutorService MoneypannyExecuter = Executors.newCachedThreadPool();

        for (int i = 1; i <= numberOfM; i++) {
            MExecuter.submit(new M(i,countDownLatch));
        }

        for (int i = 1; i <= numberOfMP; i++) {
            MoneypannyExecuter.submit(new Moneypenny(i,countDownLatch));
        }

        for (int i = 0; i < intelligences.length; i++) {
            QITexecuter.submit(new Intelligence(i, j.services.intelligence[i].missions,countDownLatch));
        }
        QITexecuter.submit(new Q(countDownLatch));

        countDownLatch.await();
        Publisher TS = new TimeService();
        ((TimeService) TS).setDuration(s.time);
        QITexecuter.submit(new Thread(TS));
        QITexecuter.shutdown();

        QITexecuter.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);

       MoneypannyExecuter.shutdownNow();
        MoneypannyExecuter.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);

        MExecuter.shutdownNow();
        MExecuter.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);

        Inventory.getInstance().printToFile(args[1]);
        Diary.getInstance().printToFile(args[2]);


    }

}
