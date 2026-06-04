package com.skilltask.tests;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

public class TaskFlowTest extends BaseTest {

    @Test
    public void employeeMovesTaskInProgressThenDone() {
        String title = "Flow " + System.currentTimeMillis();

        login("manager@demo.com", "password123");
        wait.until(ExpectedConditions.urlContains("/manager"));

        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(.,'New Task')]"))).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("input[placeholder*='Build REST API']"))).sendKeys(title);

        new Select(driver.findElement(By.xpath("//label[text()='Priority']/following-sibling::select")))
                .selectByVisibleText("MEDIUM");

        driver.findElement(By.xpath("//button[contains(.,'Add Skill')]")).click();

        By rowSelects = By.xpath("//div[contains(@class,'gap-2') and contains(@class,'mb-2')]//select");
        wait.until(d -> d.findElements(rowSelects).size() >= 2);
        wait.until(d -> d.findElements(rowSelects).get(0).findElements(By.tagName("option")).size() > 1);

        List<WebElement> selects = driver.findElements(rowSelects);
        selects.get(selects.size() - 2)
                .findElement(By.xpath(".//option[starts-with(normalize-space(.),'Java (')]")).click();
        new Select(selects.get(selects.size() - 1)).selectByVisibleText("Min: Expert");

        jsClick(driver.findElement(By.xpath("//button[contains(.,'Create & Auto-Allocate')]")));

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".alloc-banner")));

        ((JavascriptExecutor) driver).executeScript("window.localStorage.clear();");
        login("alice@demo.com", "password123");
        wait.until(ExpectedConditions.urlContains("/employee"));

        By card = By.xpath("//div[contains(@class,'task-card')][.//h6[normalize-space()='" + title + "']]");
        wait.until(ExpectedConditions.visibilityOfElementLocated(card));

        jsClick(driver.findElement(card).findElement(By.cssSelector(".btn.w-100")));

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[contains(@class,'task-card')][.//h6[normalize-space()='" + title + "']]//span[contains(@class,'s-in_progress')]")));

        jsClick(driver.findElement(card).findElement(By.cssSelector(".btn.w-100")));

        WebElement doneBadge = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[contains(@class,'task-card')][.//h6[normalize-space()='" + title + "']]//span[contains(@class,'s-done')]")));
        Assert.assertTrue(doneBadge.getText().toLowerCase().contains("done"));
    }
}
