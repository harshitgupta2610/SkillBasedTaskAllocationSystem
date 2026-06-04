package com.skilltask.tests;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

public class AddTaskTest extends BaseTest {

    @Test
    public void managerAddsTask() {
        login("manager@demo.com", "password123");
        wait.until(ExpectedConditions.urlContains("/manager"));

        String title = "Task " + System.currentTimeMillis();

        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(.,'New Task')]"))).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("input[placeholder*='Build REST API']"))).sendKeys(title);

        new Select(driver.findElement(By.xpath("//label[text()='Priority']/following-sibling::select")))
                .selectByVisibleText("HIGH");

        driver.findElement(By.xpath("//button[contains(.,'Add Skill')]")).click();

        By rowSelects = By.xpath("//div[contains(@class,'gap-2') and contains(@class,'mb-2')]//select");
        wait.until(d -> d.findElements(rowSelects).size() >= 2);
        wait.until(d -> d.findElements(rowSelects).get(0).findElements(By.tagName("option")).size() > 1);

        List<WebElement> selects = driver.findElements(rowSelects);
        WebElement skillSelect = selects.get(selects.size() - 2);
        WebElement levelSelect = selects.get(selects.size() - 1);

        skillSelect.findElement(By.xpath(".//option[starts-with(normalize-space(.),'Java (')]")).click();
        new Select(levelSelect).selectByVisibleText("Min: Intermediate");

        jsClick(driver.findElement(By.xpath("//button[contains(.,'Create & Auto-Allocate')]")));

        String banner = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector(".alloc-banner"))).getText();
        Assert.assertTrue(banner.toLowerCase().contains("assigned to"));
    }
}
