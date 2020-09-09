package com.ercross.qawjs;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * JobsProvisioner monitors the jobs webpage for job availability
 * scrapes the page if job is available and processes the scraped page into a collection of Jobs objects
 * for further processing by the JobPicker
 */
public class JobsProvisioner {

    final WebDriver driver;
    private final List<Job> sortedJobs = new ArrayList<>();

    public JobsProvisioner(WebDriver driver) {
        this.driver = driver;
    }

    private static final String tableXpath = "//table[@class='table table-bordered']";

    public List<Job> provisionJobs () {
        JobsProvisioner.monitorPage(this.driver);
        String scrapedPage = JobsProvisioner.scrapePageOnJobAvailability(this.driver);
        processScrapedPage(scrapedPage);
        return sortedJobs;
    }

    private static void monitorPage (WebDriver driver) {
        final WebDriverWait wait = new WebDriverWait (driver, Duration.ofDays(3));
        wait.until(page -> driver.findElement(By.xpath(tableXpath)));
    }

    private static String scrapePageOnJobAvailability(WebDriver driver) {
        String scrapedTable = driver.findElement(By.xpath(tableXpath)).getText();
        return scrapedTable;
    }

    private static final String tableRowRegex = "Start Work\\s" + "([0-9]+\\s)" + "([0-9]+\\.[0-9]+\\s)"
            + "minutes\\s\\$[0-9]+\\.[0-9]+\\s/\\s\\$[0-9]+\\.[0-9]+\\s/\\s\\$[0-9]+\\.[0-9]+\\n";
    private static final Pattern pattern = Pattern.compile(tableRowRegex, Pattern.MULTILINE);

    private void processScrapedPage (String scrapedTable) {
        final Matcher matcher = pattern.matcher(scrapedTable);
        setCallDataFields(matcher);
        sortByCallDuration();
    }

    private static final String callUrlPrefix = "https://app.qa-world.com/transcriptions/";
    final Logger log = LogManager.getLogger(JobsProvisioner.class);

    private void setCallDataFields(Matcher matcher) {
        Job jobData;
        while (matcher.find()) {
            jobData = new Job( Long.parseLong(matcher.group(1).trim()), Float.parseFloat(matcher.group(2).trim()), callUrlPrefix + matcher.group(1));
            log.info(jobData.toString() + "\n");
            sortedJobs.add(jobData);
        }
    }

    private void sortByCallDuration() {
        sortedJobs.stream().sorted(Comparator.comparingDouble(Job::getCallDuration))
                .collect(Collectors.toList());
    }
}
