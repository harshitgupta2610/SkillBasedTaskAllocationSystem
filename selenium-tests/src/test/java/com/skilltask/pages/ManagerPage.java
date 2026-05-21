package com.skilltask.pages;

import com.skilltask.base.TestConfig;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Page Object for /manager
 * Polished UI: sidebar + stat cards + "New Task" inline form + tasks table.
 */
public class ManagerPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    private final By newTaskBtn        = By.xpath("//button[contains(@class,'btn-primary') and contains(.,'New Task')]");
    private final By taskTitleInput    = By.xpath("//input[contains(@placeholder,'Build REST API') or contains(@placeholder,'e.g.')]");
    private final By prioritySelect    = By.xpath("//label[normalize-space()='Priority']/following::select[1]");
    private final By addSkillBtnInForm = By.xpath("//button[contains(@class,'btn-outline-primary') and contains(.,'Add Skill')]");
    private final By submitTaskBtn     = By.xpath("//button[contains(@class,'btn-primary') and contains(.,'Create & Auto-Allocate')]");

    private final By allocBanner       = By.cssSelector(".alloc-banner");
    private final By allocBannerName   = By.cssSelector(".alloc-banner strong + strong, .alloc-banner em + strong");
    private final By allocScorePill    = By.cssSelector(".alloc-banner .score-pill");

    private final By sidebarTasksCount = By.cssSelector(".sidebar-link:nth-child(2) .badge");

    public ManagerPage(WebDriver driver) {
        this.driver = driver;
        this.wait   = new WebDriverWait(driver, Duration.ofSeconds(TestConfig.EXPLICIT_WAIT));
    }

    public boolean isOnManagerPage() {
        wait.until(ExpectedConditions.urlContains("/manager"));
        return driver.getCurrentUrl().contains("/manager");
    }

    public ManagerPage clickNewTask() {
        wait.until(ExpectedConditions.elementToBeClickable(newTaskBtn)).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(taskTitleInput));
        return this;
    }

    public ManagerPage fillTaskTitle(String title) {
        driver.findElement(taskTitleInput).sendKeys(title);
        return this;
    }

    public ManagerPage selectPriority(String priority) {
        new Select(driver.findElement(prioritySelect)).selectByVisibleText(priority);
        return this;
    }

    /**
     * Click "+ Add Skill" inside the form, then pick the skill and min-level
     * from the freshly-added row (the LAST row).
     *
     * The skill dropdown shows option text like "Java (BACKEND)" — we match
     * by start-of-text so the caller can just pass "Java".
     */
    public ManagerPage addSkillRequirement(String skillName, int minLevel) {
        driver.findElement(addSkillBtnInForm).click();

        // Skill row is a div with classes "d-flex gap-2 align-items-center mb-2"
        // Inside it there are exactly 2 selects: [skill, level]
        By skillRowSelects = By.xpath(
            "//div[contains(@class,'d-flex') and contains(@class,'gap-2') " +
            "and contains(@class,'align-items-center') and contains(@class,'mb-2')]//select");

        // Wait for the new skill row to render
        wait.until(d -> d.findElements(skillRowSelects).size() >= 2);

        // Wait until skill list (loaded async from API) populated the dropdown
        wait.until(d -> {
            java.util.List<WebElement> all = d.findElements(skillRowSelects);
            if (all.size() < 2) return false;
            return all.get(all.size() - 2).findElements(By.tagName("option")).size() > 1;
        });

        java.util.List<WebElement> selects = driver.findElements(skillRowSelects);
        WebElement skillSelect = selects.get(selects.size() - 2);
        WebElement levelSelect = selects.get(selects.size() - 1);

        // Skill option text is "Skill (CATEGORY)" — match by prefix.
        WebElement option = skillSelect.findElement(By.xpath(
            ".//option[starts-with(normalize-space(.), '" + skillName + " (') " +
            "or normalize-space(.)='" + skillName + "']"));
        option.click();

        // Level options have visible text like "Min: Expert" — match by that
        // (more robust than selectByValue with Angular [value] bindings).
        String[] labels = {"", "Beginner", "Elementary", "Intermediate", "Advanced", "Expert"};
        new Select(levelSelect).selectByVisibleText("Min: " + labels[minLevel]);
        return this;
    }

    public void submitTask() {
        driver.findElement(submitTaskBtn).click();
        // Wait for either the success banner or until form closes
        wait.until(ExpectedConditions.visibilityOfElementLocated(allocBanner));
    }

    public boolean isAllocationBannerVisible() {
        try {
            return driver.findElement(allocBanner).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public String getAllocationBannerText() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(allocBanner)).getText();
    }

    public String getAllocationScore() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(allocScorePill)).getText().trim();
    }

    public int getTaskCountFromSidebar() {
        return Integer.parseInt(driver.findElement(sidebarTasksCount).getText().trim());
    }
}
