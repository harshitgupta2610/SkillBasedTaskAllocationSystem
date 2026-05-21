package com.skilltask.pages;

import com.skilltask.base.TestConfig;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

/**
 * Page Object for /employee
 * Polished UI: gradient welcome card + stat cards + grid of task cards.
 */
public class EmployeePage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    private final By taskCards    = By.cssSelector(".task-card");
    private final By profileLink  = By.cssSelector("a[routerLink='/profile']");
    private final By refreshBtn   = By.cssSelector("button .bi-arrow-clockwise");

    public EmployeePage(WebDriver driver) {
        this.driver = driver;
        this.wait   = new WebDriverWait(driver, Duration.ofSeconds(TestConfig.EXPLICIT_WAIT));
    }

    public boolean isOnEmployeePage() {
        wait.until(ExpectedConditions.urlContains("/employee"));
        return driver.getCurrentUrl().contains("/employee");
    }

    public boolean hasTaskWithTitle(String title) {
        try {
            // Wait specifically for a card whose h6 matches the title,
            // not just any card (other tasks may render first).
            wait.until(d -> findCardByTitle(title) != null);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String getStatusBadge(String taskTitle) {
        WebElement card = findCardByTitle(taskTitle);
        if (card == null) throw new RuntimeException("Task not found: " + taskTitle);
        return card.findElement(By.cssSelector(".badge")).getText().trim();
    }

    /** Click the bottom action button on a task card. Advances status. */
    public EmployeePage clickActionButton(String taskTitle) {
        WebElement card = findCardByTitle(taskTitle);
        if (card == null) throw new RuntimeException("Task not found: " + taskTitle);
        WebElement btn = card.findElement(By.cssSelector(".btn.w-100"));

        // Scroll the button into the middle of viewport — avoids click intercepted
        // by sticky footers or notification dropdowns.
        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
            "arguments[0].scrollIntoView({block:'center'});", btn);

        try {
            wait.until(ExpectedConditions.elementToBeClickable(btn)).click();
        } catch (Exception e) {
            // Last-resort JS click if something still overlaps
            ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
                "arguments[0].click();", btn);
        }

        // Wait until Angular re-renders the badge with the new status
        try { Thread.sleep(1200); } catch (InterruptedException ignored) {}
        return this;
    }

    public void goToProfile() {
        driver.findElement(profileLink).click();
        wait.until(ExpectedConditions.urlContains("/profile"));
    }

    /** Helper — find a task card whose h6 title matches. */
    private WebElement findCardByTitle(String title) {
        List<WebElement> cards = driver.findElements(taskCards);
        for (WebElement c : cards) {
            String h6 = c.findElement(By.tagName("h6")).getText().trim();
            if (h6.equalsIgnoreCase(title)) return c;
        }
        return null;
    }
}
