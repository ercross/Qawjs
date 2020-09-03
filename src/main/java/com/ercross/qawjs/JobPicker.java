package com.ercross.qawjs;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WindowType;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class JobPicker implements Runnable{

    private final WebDriver driver;
    private final List<Job> sortedJobs;

    public JobPicker (WebDriver driver, List<Job> sortedJobs) {
        this.sortedJobs = sortedJobs;
        this.driver = driver;
    }

    /*
     * pickAvailableJob opens each job-url in a new tab.
     * Since No further interaction is needed with the opened tabs other than to close such tabs, the driver is closed after use.
     * Therefore, driver instance monitoring the jobs page should not be passed into JobPicker constructor
     */
    private void pickAvailableJobs() {
        for ( int i=sortedJobs.size(); i != 0; i--) {
            driver.switchTo().newWindow(WindowType.TAB).get(sortedJobs.get(i-1).getCallUrl()); //opens each url in a new tab
        }
        driver.close();
    }

    private static final List<Double> visitedJobs = new ArrayList<>();

    private static void addJobsToVisitedJobs (List<Job> sortedJobs) {
        sortedJobs.forEach(job -> {
            visitedJobs.add(job.getCallId());
        });
    }

    private static void cutVisitedJobs(int fromIndex, int toIndex) {

        if (visitedJobs.size() == toIndex) {
            throw new IndexOutOfBoundsException();
        }
        for (int i = fromIndex; i<=toIndex; i++) {
            visitedJobs.remove(i);
        }
    }

    private void removeVisitedJobs() {
        Iterator<Job> iterator = sortedJobs.iterator();
        Job job;
        while (iterator.hasNext()) {
            job = iterator.next();
            if(visitedJobs.contains(job.getCallId()))
                iterator.remove();
        }
    }

    @Override
    public void run() {
        removeVisitedJobs();
        pickAvailableJobs();
        addJobsToVisitedJobs(this.sortedJobs);
        if (visitedJobs.size() == 100) {
            cutVisitedJobs(80,99);
        }
    }
}
