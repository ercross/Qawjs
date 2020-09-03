package com.ercross.qawjs;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.Arrays;

public class User {
    private final String email;
    private final char[] password;

    User (String email, char[] password) {
        this.password = password;
        this.email = email;
    }

    public void signIn (WebDriver driver) {
        final WebElement emailField = driver.findElement(By.xpath("//input[@id='contractor_user_email']"));
        final WebElement passwordField = driver.findElement(By.xpath("//input[@id='contractor_user_password']"));
        final WebElement rememberMeButton = driver.findElement(By.xpath("//input[@id='contractor_user_remember_me']"));

        emailField.sendKeys(this.email);
        passwordField.sendKeys(Arrays.toString(this.password));
        rememberMeButton.click();
        passCaptcha(driver);
        passwordField.submit();
    }

    public void passCaptcha (WebDriver driver) {
        try{
            final WebElement captchaButton = driver.findElement(By.className("#recaptcha-checkbox-spinner"));
            captchaButton.click();
        }
        catch (NoSuchElementException e) {

        }
    }

    public void alertThroughEmail() {

    }
}
