package com.skilltask.pages;

import com.skilltask.base.TestConfig;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

/**
 * Page Object for /profile
 * Polished UI: header card + availability toggle + skills list + add-skill form.
 */
public class ProfilePage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    private final By addSkillToggleBtn = By.xpath("//button[contains(@class,'btn-primary') and contains(.,'Add Skill') and not(ancestor::div[contains(@class,'add-skill-form')])]");
    private final By skillSelect       = By.cssSelector(".add-skill-form select");
    private final By yearsInput        = By.cssSelector(".add-skill-form input[type='number']");
    private final By submitAddSkillBtn = By.xpath("//div[contains(@class,'add-skill-form')]//button[contains(@class,'btn-primary')]");
    private final By skillRows         = By.cssSelector(".skill-row");
    private final By skillError        = By.cssSelector(".add-skill-form .form-error-banner");

    public ProfilePage(WebDriver driver) {
        this.driver = driver;
        this.wait   = new WebDriverWait(driver, Duration.ofSeconds(TestConfig.EXPLICIT_WAIT));
    }

    public ProfilePage open() {
        driver.get(TestConfig.BASE_URL + "/profile");
        wait.until(ExpectedConditions.urlContains("/profile"));
        return this;
    }

    public ProfilePage clickAddSkillToggle() {
        wait.until(ExpectedConditions.elementToBeClickable(addSkillToggleBtn)).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(skillSelect));
        return this;
    }

    public ProfilePage selectSkill(String skillName) {
        new Select(driver.findElement(skillSelect)).selectByVisibleText(skillName);
        return this;
    }

    /** proficiency 1..5  → click the matching button */
    public ProfilePage selectProficiency(int level) {
        if (level < 1 || level > 5) throw new IllegalArgumentException("level must be 1..5");
        By btn = By.cssSelector(".add-skill-form .prof-selector .prof-btn:nth-child(" + level + ")");
        driver.findElement(btn).click();
        return this;
    }

    public ProfilePage enterYears(int years) {
        WebElement el = driver.findElement(yearsInput);
        el.clear();
        el.sendKeys(String.valueOf(years));
        return this;
    }

    public void submitAddSkill() {
        driver.findElement(submitAddSkillBtn).click();
        // Wait for form to close OR skill list to refresh
        try { Thread.sleep(800); } catch (InterruptedException ignored) {}
    }

    /** Returns true if a skill row with the given name is shown. */
    public boolean skillExists(String skillName) {
        List<WebElement> rows = driver.findElements(skillRows);
        for (WebElement r : rows) {
            try {
                String name = r.findElement(By.cssSelector(".skill-row-name")).getText().trim();
                if (name.equalsIgnoreCase(skillName)) return true;
            } catch (Exception ignored) {}
        }
        return false;
    }

    public String getSkillError() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(skillError)).getText();
    }
}
