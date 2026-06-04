package com.skilltask.tests;

import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.Test;

public class LoginTest extends BaseTest {
    ChromeOptions options = new ChromeOptions();
    @Test
    public void managerLogin() {
        login("manager@demo.com", "password123");
        wait.until(ExpectedConditions.urlContains("/manager"));

        options.addArguments("--disable-notifications");
        Assert.assertTrue(driver.getCurrentUrl().contains("/manager"));
    }

    @Test
    public void employeeLogin() {
        login("alice@demo.com", "password123");
        options.addArguments("--disable-notifications");
        wait.until(ExpectedConditions.urlContains("/employee"));
        Assert.assertTrue(driver.getCurrentUrl().contains("/employee"));
    }

    @Test
    public void invalidLogin() {
        login("manager@demo.com", "wrongpass");
        String error = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector(".form-error-banner"))).getText();
        Assert.assertTrue(error.toLowerCase().contains("invalid"));
    }
}
