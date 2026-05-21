package com.skilltask.pages;

import com.skilltask.base.TestConfig;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Page Object for /signup
 * Polished UI: name, email, role picker (Employee/Manager), password, confirm password.
 */
public class SignupPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    private final By nameInput     = By.cssSelector("input[placeholder*='Alice']");
    private final By emailInput    = By.cssSelector("input[type='email']");
    private final By passwordInput = By.cssSelector("input[placeholder*='Min']");
    private final By confirmInput  = By.cssSelector("input[placeholder*='Re-enter']");
    private final By roleEmployee  = By.cssSelector(".role-option:nth-child(1)");
    private final By roleManager   = By.cssSelector(".role-option:nth-child(2)");
    private final By submitBtn     = By.xpath("//button[contains(@class,'btn-primary') and contains(.,'Create Account')]");
    private final By errorBanner   = By.cssSelector(".form-error-banner");

    public SignupPage(WebDriver driver) {
        this.driver = driver;
        this.wait   = new WebDriverWait(driver, Duration.ofSeconds(TestConfig.EXPLICIT_WAIT));
    }

    public SignupPage open() {
        driver.get(TestConfig.BASE_URL + "/signup");
        wait.until(ExpectedConditions.visibilityOfElementLocated(nameInput));
        return this;
    }

    public SignupPage fillName(String s)     { driver.findElement(nameInput).sendKeys(s);     return this; }
    public SignupPage fillEmail(String s)    { driver.findElement(emailInput).sendKeys(s);    return this; }
    public SignupPage fillPassword(String s) { driver.findElement(passwordInput).sendKeys(s); return this; }
    public SignupPage fillConfirm(String s)  { driver.findElement(confirmInput).sendKeys(s);  return this; }

    public SignupPage selectRole(String role) {
        if ("MANAGER".equalsIgnoreCase(role)) {
            driver.findElement(roleManager).click();
        } else {
            driver.findElement(roleEmployee).click();
        }
        return this;
    }

    public void submit() {
        WebElement btn = wait.until(ExpectedConditions.presenceOfElementLocated(submitBtn));
        // The signup form is long — submit button is often below the viewport
        // and gets click-intercepted by the footer/divider. Scroll first, then click.
        ((JavascriptExecutor) driver).executeScript(
            "arguments[0].scrollIntoView({block:'center'});", btn);
        try {
            wait.until(ExpectedConditions.elementToBeClickable(btn)).click();
        } catch (Exception ignored) {
            // Last-resort JavaScript click if something still overlaps it
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
        }
    }

    public String getErrorText() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(errorBanner)).getText();
    }
}
