package com.ercross.qawjs;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.List;

/**
 * @author ercross
 *
 * App provides an entry point into the program.
 */
public class App
{
    public static void main( String[] args )
    {
        //todo **************************************save password into a properties file and add to .gitignore*********************
        User user = new User (args[0], args[1].toCharArray());
        List<Job> sortedJobs;
        final WebDriver jobsProvisionerDriver = new App().initWebDriver();
        final JobsProvisioner jobsProvisioner = new JobsProvisioner(jobsProvisionerDriver);

        while (true) {
            jobsProvisionerDriver.get("https://app.qa-world.com/calls");
            if (isSignInPage(jobsProvisionerDriver))
                user.signIn(jobsProvisionerDriver);
            sortedJobs = jobsProvisioner.provisionJobs();
            new Thread(new JobPicker(new App().initWebDriver(), sortedJobs)).start();
            if (jobPickedSuccessfully(jobsProvisionerDriver)) {
                user.alertThroughEmail();
            }
        }
    }

    private static boolean jobPickedSuccessfully(WebDriver driver) {
        driver.get("https://app.qa-world.com/calls");
        return false; //TODO  implement by checking if page indicates user already has a job picked
    }

    private static boolean isSignInPage (WebDriver driver) {
        final String currentUrl = driver.getCurrentUrl();
        final String signInPageUrl = "https://app.qa-world.com/contractor_users/sign_in";
        return  (currentUrl.equalsIgnoreCase(signInPageUrl));
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
