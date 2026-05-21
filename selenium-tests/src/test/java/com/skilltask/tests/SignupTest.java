package com.skilltask.tests;

import com.skilltask.base.BaseTest;
import com.skilltask.base.TestConfig;
import com.skilltask.pages.SignupPage;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.Duration;

/**
 * Flow 3: signup a new EMPLOYEE and verify redirect to /employee.
 * Uses a timestamped email so the test is idempotent.
 */
public class SignupTest extends BaseTest {

    @Test(description = "Sign up a new EMPLOYEE -> redirects to /employee")
    public void newEmployeeSignupRedirectsToEmployeePage() {
        String uniqueEmail = "selenium_" + System.currentTimeMillis() + "@test.com";

        new SignupPage(driver).open()
                .fillName("Selenium Tester")
                .fillEmail(uniqueEmail)
                .selectRole("EMPLOYEE")
                .fillPassword("test1234")
                .fillConfirm("test1234")
                .submit();

        new WebDriverWait(driver, Duration.ofSeconds(TestConfig.EXPLICIT_WAIT))
                .until(ExpectedConditions.urlContains("/employee"));

        Assert.assertTrue(driver.getCurrentUrl().contains("/employee"),
                "After signup the new employee should land on /employee");
    }

    @Test(description = "Mismatched passwords show field error and stay on /signup")
    public void mismatchedPasswordsShowError() {
        SignupPage signup = new SignupPage(driver).open();
        signup.fillName("Test")
              .fillEmail("mismatch_" + System.currentTimeMillis() + "@test.com")
              .selectRole("EMPLOYEE")
              .fillPassword("abc123")
              .fillConfirm("xyz999")
              .submit();

        // Still on /signup
        Assert.assertTrue(driver.getCurrentUrl().contains("/signup"),
                "Should stay on /signup when passwords mismatch");
    }
}
