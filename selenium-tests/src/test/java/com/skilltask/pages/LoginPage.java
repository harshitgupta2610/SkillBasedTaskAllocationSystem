package com.skilltask.pages;

import com.skilltask.base.TestConfig;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Page Object for /login
 * Polished UI: split-screen with email + password + Sign In + demo pills.
 */
public class LoginPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    private final By emailInput      = By.cssSelector("input[type='email']");
    private final By passwordInput   = By.cssSelector("input[type='password']");
    private final By signInButton    = By.xpath("//button[contains(@class,'btn-primary') and contains(.,'Sign')]");
    private final By errorBanner     = By.cssSelector(".login-form-wrap .d-flex.align-items-center.gap-2.p-3");
    private final By signupLink      = By.cssSelector("a[routerLink='/signup']");

    // 4 demo pills in order: Manager Mike, Alice, Bob, Charlie
    private final By demoPillManager = By.cssSelector(".demo-pill:nth-child(1)");
    private final By demoPillAlice   = By.cssSelector(".demo-pill:nth-child(2)");
    private final By demoPillBob     = By.cssSelector(".demo-pill:nth-child(3)");
    private final By demoPillCharlie = By.cssSelector(".demo-pill:nth-child(4)");

    public LoginPage(WebDriver driver) {
        this.driver = driver;
        this.wait   = new WebDriverWait(driver, Duration.ofSeconds(TestConfig.EXPLICIT_WAIT));
    }

    public LoginPage open() {
        driver.get(TestConfig.BASE_URL + "/login");
        wait.until(ExpectedConditions.visibilityOfElementLocated(emailInput));
        return this;
    }

    public LoginPage enterEmail(String email) {
        driver.findElement(emailInput).clear();
        driver.findElement(emailInput).sendKeys(email);
        return this;
    }

    public LoginPage enterPassword(String pw) {
        driver.findElement(passwordInput).clear();
        driver.findElement(passwordInput).sendKeys(pw);
        return this;
    }

    public void clickSignIn() {
        driver.findElement(signInButton).click();
    }

    /** One-shot login via the demo pills (auto-fills email/password). */
    public void loginAsDemo(String who) {
        By pill = switch (who.toLowerCase()) {
            case "manager" -> demoPillManager;
            case "alice"   -> demoPillAlice;
            case "bob"     -> demoPillBob;
            case "charlie" -> demoPillCharlie;
            default -> throw new IllegalArgumentException("Unknown demo: " + who);
        };
        driver.findElement(pill).click();
        clickSignIn();
    }

    /** Conventional login: type credentials manually. */
    public void login(String email, String password) {
        enterEmail(email).enterPassword(password).clickSignIn();
    }

    public String getErrorText() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(errorBanner)).getText();
    }

    public void goToSignup() {
        driver.findElement(signupLink).click();
    }

    public boolean isOnLoginPage() {
        return driver.getCurrentUrl().contains("/login");
    }
}
