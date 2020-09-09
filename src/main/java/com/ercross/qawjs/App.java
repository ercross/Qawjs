package com.ercross.qawjs;

import com.google.gson.Gson;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * @author ercross
 */
public class App {
    static final Logger log = LogManager.getLogger(App.class);

    public static void main( String[] args ) throws FileNotFoundException {
        List<Job> sortedJobs;

        while (true) {
            try {
                log.debug("----------------------------------------------Starting Qawjs............................................\n");
                log.debug("Instantiating web driver..............................................\n");
                WebDriver jobsProvisionerDriver = new App().initWebDriver();

                log.debug("Injecting session cookies...............................................................\n");
                jobsProvisionerDriver.get("https://app.qa-world.com");
                jobsProvisionerDriver.manage().addCookie(App.getSessionCookie());
                final JobsProvisioner jobsProvisioner = new JobsProvisioner(jobsProvisionerDriver);

                jobsProvisionerDriver.get("https://app.qa-world.com/calls");
                log.debug("Monitoring job site homepage for jobs.....................................");
                sortedJobs = jobsProvisioner.provisionJobs();

                log.debug("Jobs found. Picking jobs with Job picker.............");
                jobsProvisionerDriver = jobsProvisioner.driver; //to ensure the same driver instance is used throughout this iteration
                JobPicker jobPicker = new JobPicker(jobsProvisionerDriver, sortedJobs);
                jobPicker.pickAvailableJobs();
                jobsProvisionerDriver = jobPicker.driver; //to ensure the same driver instance is used throughout this iteration
                if (isJobPickedSuccessfully(jobsProvisionerDriver)) {
                    Notifier.notifyByEmail("tobins4u@gmail.com", "New QA job picked",
                            "A new job has been picked for you on QA-World. Kindly visit to check");
                }
                jobsProvisionerDriver.quit();
            } catch (Exception e) {
                log.error("Application crashed" + e);
            }
        }
    }

    private static boolean isJobPickedSuccessfully(WebDriver driver) {
        driver.get("https://app.qa-world.com/calls");
        if (driver.findElement(By.cssSelector("div.disabled-sidebar div.primary-content-container.grey-background div.container-md div.row:nth-child(3) div.col div.card div.card-body > h1:nth-child(1)")).isDisplayed())
            return true;
        log.info("No job picked yet");
        return  false;
    }

    private static Cookie getSessionCookie() throws FileNotFoundException {
        BufferedReader br = new BufferedReader( new FileReader("cookies.json"));
        return new Gson().fromJson(br, Cookie.class);
    }

    private WebDriver initWebDriver(){
        WebDriverManager.chromedriver().setup();
        ChromeOptions chromeSettings = new ChromeOptions();
        chromeSettings.setExperimentalOption("excludeSwitches", new String[] {"enable-automation"});
        chromeSettings.addArguments("--disable-gpu");
        //chromeSettings.addArguments("--headless");
        chromeSettings.addArguments("--ignore-certificate-errors");
        chromeSettings.addArguments("--silent");
        chromeSettings.addArguments("--disable--notifications");
        chromeSettings.addArguments("--disable-offline-auto-reload");
        return new ChromeDriver(chromeSettings);
    }
}
