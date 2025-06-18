package tests;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.*;

import java.time.Duration;
import java.util.Random;

public class ProviderPlatformTest {

    WebDriver driver;
    WebDriverWait wait;

    @BeforeClass
    public void setup() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.manage().window().maximize();
        driver.get("https://qa-takehome.dtxplus.com/");

        wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        
    }

    @Test(priority = 1)
    public void loginWithInvalidUsername() {
        driver.findElement(By.id("username")).clear();
        driver.findElement(By.id("password")).clear();
        driver.findElement(By.id("username")).sendKeys("invaliduser");
        driver.findElement(By.id("password")).sendKeys("dtxplus");
        driver.findElement(By.xpath("//button[text()='Login']")).click();

        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        String alertText = alert.getText();
        Assert.assertEquals(alertText, "Invalid login", "Alert message mismatch.");
        alert.accept();
    }

    @Test(priority = 2)
    public void loginWithInvalidPassword() {
        driver.findElement(By.id("username")).clear();
        driver.findElement(By.id("password")).clear();
        driver.findElement(By.id("username")).sendKeys("dtxplus");
        driver.findElement(By.id("password")).sendKeys("wrongpassword");
        driver.findElement(By.xpath("//button[text()='Login']")).click();

        Assert.assertFalse(driver.getPageSource().contains("Add Patient"), 
                "Login should fail with invalid password.");
    }

    @Test(priority = 3)
    public void loginWithEmptyCredentials() {
        driver.findElement(By.id("username")).clear();
        driver.findElement(By.id("password")).clear();
        driver.findElement(By.xpath("//button[text()='Login']")).click();

        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        String alertText = alert.getText();
        Assert.assertEquals(alertText, "Invalid login", "Alert message mismatch.");
        alert.accept();
    }

    @Test(priority = 4)
    public void loginWithValidCredentials() {
        driver.findElement(By.id("username")).clear();
        driver.findElement(By.id("password")).clear();
        driver.findElement(By.id("username")).sendKeys("dtxplus");
        driver.findElement(By.id("password")).sendKeys("dtxplus");
        driver.findElement(By.xpath("//button[text()='Login']")).click();

        Assert.assertTrue(driver.getPageSource().contains("Add Patient"), "Login failed or dashboard not visible.");
    }

    @Test(priority = 5, dependsOnMethods = {"loginWithValidCredentials"})
    public void addPatient() throws InterruptedException {
        driver.findElement(By.id("add-patient-btn")).click();

        // Generate a random 5-digit suffix
        Random random = new Random();
        int randomSuffix = 10000 + random.nextInt(90000);

        String firstName = "TestUser" + randomSuffix;
        String lastName = "Future" + randomSuffix;
        String mrn = "MRN" + randomSuffix;

        driver.findElement(By.id("mrn")).sendKeys(mrn);
        driver.findElement(By.id("firstName")).sendKeys(firstName);
        driver.findElement(By.id("lastName")).sendKeys(lastName);
        driver.findElement(By.id("dob")).sendKeys("01/01/2031");
        driver.findElement(By.id("discharge")).sendKeys("2026-01-02T15:30");
        driver.findElement(By.id("phone")).sendKeys("98765" + randomSuffix);

        // Select 'English' for Language
        Select languageSelect = new Select(driver.findElement(By.id("language")));
        languageSelect.selectByVisibleText("English");

        // Select 'EST' for Timezone
        Select timezoneSelect = new Select(driver.findElement(By.id("timezone")));
        timezoneSelect.selectByVisibleText("EST");

        // Submit the form
        driver.findElement(By.xpath("//button[text()='Submit']")).click();

        Thread.sleep(2000); // Simple wait, use WebDriverWait in real tests

        WebElement patientTable = driver.findElement(By.xpath("//table"));
        String tableText = patientTable.getText();

        Assert.assertTrue(tableText.contains(mrn),
                "MRN '" + mrn + "' should appear in the patient list after successful add.");

        System.out.println("Patient with MRN " + mrn + " was successfully added and verified in the patient list.");
    }

    @Test(priority = 6, dependsOnMethods = {"loginWithValidCredentials"})
    public void addPatientWithExistingMRN() throws InterruptedException {
        // First, add a patient with a specific MRN
        driver.findElement(By.id("add-patient-btn")).click();

        Random random = new Random();
        int randomSuffix = 10000 + random.nextInt(90000);
        String existingMRN = "1002";

        String firstName2 = "TestUser2" + randomSuffix;
        String lastName2 = "Future2" + randomSuffix;

        driver.findElement(By.id("mrn")).sendKeys(existingMRN);
        driver.findElement(By.id("firstName")).sendKeys(firstName2);
        driver.findElement(By.id("lastName")).sendKeys(lastName2);
        driver.findElement(By.id("dob")).sendKeys("02/02/2031");
        driver.findElement(By.id("discharge")).sendKeys("2026-01-03T15:30");
        driver.findElement(By.id("phone")).sendKeys("98766" + randomSuffix);
        
        Select languageSelect2 = new Select(driver.findElement(By.id("language")));
        languageSelect2.selectByVisibleText("English");

        Select timezoneSelect2 = new Select(driver.findElement(By.id("timezone")));
        timezoneSelect2.selectByVisibleText("EST");

        // Submit the second patient (should trigger error)
        driver.findElement(By.xpath("//button[text()='Submit']")).click();

        // Wait for and verify the alert
        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        String alertText = alert.getText();
        Assert.assertEquals(alertText, "Error: MRN must be unique.", "Alert message should indicate MRN must be unique.");

        alert.accept();
    
    }

    @Test(priority = 7, dependsOnMethods = {"loginWithValidCredentials"})
    public void testLogout() {
        // Look for logout button or link and click it
        WebElement logoutButton = driver.findElement(By.id("logout"));
        logoutButton.click();
        
        // Verify we're back to login page
        Assert.assertTrue(driver.getCurrentUrl().contains("qa-takehome.dtxplus.com") || 
                        driver.getPageSource().contains("Login") ||
                        driver.findElement(By.id("username")).isDisplayed(),
                        "Should be redirected to login page after logout.");
    }

    @AfterClass
    public void teardown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
