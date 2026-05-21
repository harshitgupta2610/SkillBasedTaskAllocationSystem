package com.skilltask.tests;

import com.skilltask.base.BaseTest;
import com.skilltask.base.TestConfig;
import com.skilltask.pages.EmployeePage;
import com.skilltask.pages.LoginPage;
import com.skilltask.pages.ManagerPage;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.Map;

/**
 * Flow 5: end-to-end status advancement.
 *
 * 1. Manager creates a Java-Expert task.
 * 2. Parse the allocation banner to learn WHO got it (DB may have polished
 *    seed: Alice etc. OR basic-mvp seed: Emp One etc. — we handle both).
 * 3. Logout, log in as that person.
 * 4. Click Start  -> IN_PROGRESS.
 * 5. Click Mark Done -> DONE.
 */
public class UpdateTaskStatusTest extends BaseTest {

    /** Maps a user's display name to their {email, password}. */
    private static final Map<String, String[]> CREDENTIALS = Map.of(
        // Polished (original-mvp) seed
        "Alice Johnson",  new String[]{"alice@demo.com",   "password123"},
        "Bob Smith",      new String[]{"bob@demo.com",     "password123"},
        "Charlie Brown",  new String[]{"charlie@demo.com", "password123"},
        "Diana Prince",   new String[]{"diana@demo.com",   "password123"},
        // basic-mvp seed
        "Emp One",        new String[]{"emp1@test.com",    "emp123"},
        "Emp Two",        new String[]{"emp2@test.com",    "emp123"},
        "Emp Three",      new String[]{"emp3@test.com",    "emp123"}
    );

    @Test(description = "Employee advances task ASSIGNED -> IN_PROGRESS -> DONE")
    public void employeeAdvancesTaskThroughLifecycle() {
        String title = "Status Flow Task " + System.currentTimeMillis();

        // ── 1. Manager creates a Java level 5 task ─────────────────
        new LoginPage(driver).open().loginAsDemo("manager");
        new WebDriverWait(driver, Duration.ofSeconds(TestConfig.EXPLICIT_WAIT))
                .until(ExpectedConditions.urlContains("/manager"));

        ManagerPage manager = new ManagerPage(driver);
        manager.clickNewTask()
               .fillTaskTitle(title)
               .selectPriority("MEDIUM")
               .addSkillRequirement("Java", 5)
               .submitTask();

        // ── 2. Parse banner to find the assignee ──────────────────
        String bannerText = manager.getAllocationBannerText();
        String assigneeName = extractAssignee(bannerText);
        String[] creds = CREDENTIALS.get(assigneeName);
        Assert.assertNotNull(creds,
            "Unknown assignee: " + assigneeName + " — add credentials to the CREDENTIALS map. Banner: " + bannerText);

        System.out.println("Task assigned to: " + assigneeName + " (" + creds[0] + ")");

        // ── 3. Log out manager and log in as the actual assignee ──
        ((org.openqa.selenium.JavascriptExecutor) driver)
                .executeScript("window.localStorage.clear();");

        new LoginPage(driver).open().login(creds[0], creds[1]);

        WebDriverWait longWait = new WebDriverWait(driver, Duration.ofSeconds(20));
        longWait.until(ExpectedConditions.urlContains("/employee"));

        // Force refresh so the task list reloads cleanly
        try { Thread.sleep(500); } catch (InterruptedException ignored) {}
        driver.navigate().refresh();
        try { Thread.sleep(1500); } catch (InterruptedException ignored) {}

        EmployeePage employee = new EmployeePage(driver);
        Assert.assertTrue(employee.hasTaskWithTitle(title),
                assigneeName + " should see the assigned task: " + title);

        // ── 4. Click Start -> IN_PROGRESS ─────────────────────────
        String initialStatus = employee.getStatusBadge(title);
        Assert.assertTrue(initialStatus.toLowerCase().contains("assigned"),
                "Initial status should be Assigned, got: " + initialStatus);

        employee.clickActionButton(title);
        String afterStart = employee.getStatusBadge(title);
        Assert.assertTrue(afterStart.toLowerCase().contains("progress"),
                "Status after Start should be In Progress, got: " + afterStart);

        // ── 5. Click Mark Done -> DONE ────────────────────────────
        employee.clickActionButton(title);
        String afterDone = employee.getStatusBadge(title);
        Assert.assertTrue(afterDone.toLowerCase().contains("done"),
                "Status after Mark Done should be Done, got: " + afterDone);
    }

    /**
     * Extracts the assignee name from a banner like:
     *   Auto-allocation complete! Task "..." assigned to Alice Johnson  15
     * Returns "Alice Johnson".
     */
    private String extractAssignee(String banner) {
        int idx = banner.indexOf("assigned to ");
        if (idx < 0) throw new RuntimeException("Banner format unexpected: " + banner);
        String tail = banner.substring(idx + "assigned to ".length()).trim();
        // The score number / newline appears after the name — cut at the first digit or newline.
        StringBuilder name = new StringBuilder();
        for (char c : tail.toCharArray()) {
            if (Character.isDigit(c) || c == '\n' || c == '\r') break;
            name.append(c);
        }
        return name.toString().trim();
    }
}
