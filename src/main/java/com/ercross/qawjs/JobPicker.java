package com.ercross.qawjs;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WindowType;

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

    private void pickJobs() {
        for ( int i=sortedJobs.size(); i != 0; i--) {
            driver.switchTo().newWindow(WindowType.TAB).get(sortedJobs.get(i-1).getCallUrl()); //opens each url in a new tab
            //todo note down the @contains in my journal if it works
            //driver.findElement(By.xpath("//tr[td[a[contains(@id,'" + sortedJobs.get(i-1).getCallId() +"')]]]")).sendKeys(Keys.CONTROL + "t");
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
