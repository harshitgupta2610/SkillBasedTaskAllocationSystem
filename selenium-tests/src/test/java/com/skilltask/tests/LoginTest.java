package com.skilltask.tests;

import com.skilltask.base.BaseTest;
import com.skilltask.base.TestConfig;
import com.skilltask.pages.LoginPage;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.Duration;

/**
 * Flows 1 + 2: manager + employee login.
 * Plus one negative case: wrong password shows error.
 */
public class LoginTest extends BaseTest {

    @Test(description = "Manager logs in with valid credentials and lands on /manager")
    public void managerLoginRedirectsToManagerPage() {
        new LoginPage(driver).open()
                .login(TestConfig.MANAGER_EMAIL, TestConfig.MANAGER_PW);

        new WebDriverWait(driver, Duration.ofSeconds(TestConfig.EXPLICIT_WAIT))
                .until(ExpectedConditions.urlContains("/manager"));

        Assert.assertTrue(driver.getCurrentUrl().contains("/manager"),
                "Manager should be redirected to /manager after login");
    }

    @Test(description = "Employee (Alice) logs in via demo pill and lands on /employee")
    public void employeeDemoPillLoginRedirectsToEmployeePage() {
        LoginPage login = new LoginPage(driver).open();
        login.loginAsDemo("alice");

        new WebDriverWait(driver, Duration.ofSeconds(TestConfig.EXPLICIT_WAIT))
                .until(ExpectedConditions.urlContains("/employee"));

        Assert.assertTrue(driver.getCurrentUrl().contains("/employee"),
                "Employee should be redirected to /employee after login");
    }

    @Test(description = "Wrong password shows error banner and stays on /login")
    public void wrongPasswordShowsError() {
        LoginPage login = new LoginPage(driver).open();
        login.login(TestConfig.MANAGER_EMAIL, "WRONG_PASSWORD");

        String err = login.getErrorText();
        Assert.assertTrue(err.toLowerCase().contains("invalid"),
                "Error banner should mention invalid credentials, got: " + err);
        Assert.assertTrue(login.isOnLoginPage(), "Should still be on /login");
    }
}
