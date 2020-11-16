package bgu.spl.mics.application.passiveObjects;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Passive object representing the diary where all reports are stored.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private fields and methods to this class as you see fit.
 */
public class Diary {
    /**
     * Retrieves the single instance of this class.
     */
    private List<Report> reports = new LinkedList<>();
    private AtomicInteger total = new AtomicInteger();


    private static class SingleInstance {
        private static Diary d = new Diary();
    }

    public static Diary getInstance() {
        return SingleInstance.d;
    }

    public List<Report> getReports() {
        return this.reports;
    }

    /**
     * adds a report to the diary
     *
     * @param reportToAdd - the report to add
     */
    public void addReport(Report reportToAdd) {
        this.reports.add(reportToAdd);
    }

    /**
     * <p>
     * Prints to a file name @filename a serialized object List<Report> which is a
     * List of all the reports in the diary.
     * This method is called by the main method in order to generate the output.
     */
    public void printToFile(String filename) {
      try(Writer w = new FileWriter(filename)){
          Gson gson = new GsonBuilder().setPrettyPrinting().create();
          gson.toJson(Diary.getInstance(),w);
      }
      catch(Exception e){}
    }

    /**
     * Gets the total number of received missions (executed / aborted) be all the M-instances.
     *
     * @return the total number of received missions (executed / aborted) be all the M-instances.
     */
    public int getTotal() {
        return this.total.intValue();
    }

    public void incrementTotal() {
        this.total.incrementAndGet();
    }

}
