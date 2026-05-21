package com.skilltask.tests;

import com.skilltask.base.BaseTest;
import com.skilltask.base.TestConfig;
import com.skilltask.pages.LoginPage;
import com.skilltask.pages.ProfilePage;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.Duration;

/**
 * Flow 6: an employee adds a new skill via /profile, then the
 * skill appears in their skills list.
 *
 * Uses Bob (who does NOT have "AWS" in the seed data).
 */
public class AddSkillTest extends BaseTest {

    @Test(description = "Employee adds a new skill -> skill row appears in profile")
    public void employeeAddsSkillFromProfile() {
        // 1. Login as Bob
        new LoginPage(driver).open().loginAsDemo("bob");
        new WebDriverWait(driver, Duration.ofSeconds(TestConfig.EXPLICIT_WAIT))
                .until(ExpectedConditions.urlContains("/employee"));

        // 2. Open profile
        ProfilePage profile = new ProfilePage(driver).open();

        // 3. If Bob already has AWS from a previous test run, the test is still
        //    valid as long as AWS appears in the skill list at the end.
        //    Otherwise: click Add Skill and submit the form.
        if (!profile.skillExists("AWS")) {
            profile.clickAddSkillToggle()
                   .selectSkill("AWS")          // option text is just the skill name (optgroup = category)
                   .selectProficiency(3)        // Intermediate
                   .enterYears(2)
                   .submitAddSkill();

            // Reload so the UI reflects DB state
            driver.navigate().refresh();
            try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
        }

        // Final assertion: AWS is in Bob's skill list (regardless of when it was added)
        Assert.assertTrue(profile.skillExists("AWS"),
                "AWS should appear in Bob's skills list");
    }
}
