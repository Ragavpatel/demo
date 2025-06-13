package tests;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;
import org.testng.annotations.*;

import java.time.Duration;
import java.util.Random;

public class AddPatientDOBTest {

    WebDriver driver;

    @BeforeClass
    public void setup() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.manage().window().maximize();
        driver.get("https://qa-takehome.dtxplus.com/");
        System.out.println("Navigated to Provider Platform URL.");
    }

    @Test(priority = 1)
    public void loginTest() {
        driver.findElement(By.id("username")).sendKeys("dtxplus");
        driver.findElement(By.id("password")).sendKeys("dtxplus");
        driver.findElement(By.xpath("//button[text()='Login']")).click();

        Assert.assertTrue(driver.getPageSource().contains("Add Patient"), "Login failed or dashboard not visible.");
        System.out.println("Login successful, dashboard visible.");
    }

    @Test(priority = 2, dependsOnMethods = {"loginTest"})
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

    @AfterClass
    public void teardown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
