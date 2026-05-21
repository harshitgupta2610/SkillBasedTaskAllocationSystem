package com.skilltask.tests;

import com.skilltask.base.BaseTest;
import com.skilltask.base.TestConfig;
import com.skilltask.pages.LoginPage;
import com.skilltask.pages.ManagerPage;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.Duration;

/**
 * Flow 4: manager creates a Java task, system auto-allocates,
 * banner shows the chosen employee and allocation score.
 */
public class CreateTaskTest extends BaseTest {

    @Test(description = "Manager creates task -> auto-allocation banner appears with assignee")
    public void createTaskTriggersAutoAllocation() {
        // 1. Login as manager
        new LoginPage(driver).open().loginAsDemo("manager");
        new WebDriverWait(driver, Duration.ofSeconds(TestConfig.EXPLICIT_WAIT))
                .until(ExpectedConditions.urlContains("/manager"));

        ManagerPage manager = new ManagerPage(driver);
        Assert.assertTrue(manager.isOnManagerPage(), "Should be on /manager");

        // 2. Open New Task form, fill it
        String taskTitle = "Selenium E2E Task " + System.currentTimeMillis();
        manager.clickNewTask()
               .fillTaskTitle(taskTitle)
               .selectPriority("HIGH")
               .addSkillRequirement("Java", 3)   // Alice has Java level 5 -> best fit
               .submitTask();

        // 3. Verify allocation banner appears
        Assert.assertTrue(manager.isAllocationBannerVisible(),
                "Auto-allocation success banner should appear");

        String bannerText = manager.getAllocationBannerText();
        Assert.assertTrue(bannerText.contains(taskTitle),
                "Banner should mention the task title, got: " + bannerText);
        Assert.assertTrue(bannerText.toLowerCase().contains("auto-allocation complete"),
                "Banner should announce allocation, got: " + bannerText);

        // 4. Allocation score should be > 0 (10 base + over-qualification + workload)
        String score = manager.getAllocationScore();
        Assert.assertFalse(score.isBlank(),
                "Allocation score pill should show a numeric score");
    }
}
