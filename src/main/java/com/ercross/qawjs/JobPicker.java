package com.ercross.qawjs;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class JobPicker {

    final WebDriver driver;
    private final List<Job> sortedJobs;

    public JobPicker (WebDriver driver, List<Job> sortedJobs) {
        this.sortedJobs = sortedJobs;
        this.driver = driver;
    }

    private static final Logger log = LogManager.getLogger(JobPicker.class);

    private void pickJobs() {
        for ( int i=sortedJobs.size(); i != 0; i--) {
            try{
                driver.findElement(By.xpath("//a[contains(@id, '-" + sortedJobs.get(i-1).getCallId() +"')]")).click();
            } catch (UnhandledAlertException e) {
                log.debug("Unhandled alert exception ignored");
            } catch (NoSuchElementException e) { //catching this here because elements can disappear from page after being seen using findElement above
                log.debug ("The element is no more on page");
            }
        }
    }

    private static final List<Long> visitedJobs = new ArrayList<>();

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

    public void pickAvailableJobs() {
        removeVisitedJobs();
        pickJobs();
        addJobsToVisitedJobs(this.sortedJobs);
        if (visitedJobs.size() == 100) {
            cutVisitedJobs(80,99);
        }
    }
}
