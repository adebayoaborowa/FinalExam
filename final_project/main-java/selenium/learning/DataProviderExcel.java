package selenium.learning;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import org.apache.commons.io.FileUtils;
import org.apache.poi.ss.usermodel.*;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.Select;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.Duration;

public class DataProviderExcel {
    public static ExtentSparkReporter sparkReporter;
    public static ExtentReports extent;
    public static ExtentTest test;

    WebDriver driver;

    public void initializer() {
        sparkReporter = new ExtentSparkReporter(System.getProperty("user.dir") + "/Reports/extentSparkReport.html");
        sparkReporter.config().setDocumentTitle("Excel Automation Report");
        sparkReporter.config().setReportName("Test Execution Report for Excel");
        sparkReporter.config().setTheme(Theme.STANDARD);
        sparkReporter.config().setTimeStampFormat("yyyy-MM-dd HH:mm:ss");
        extent = new ExtentReports();
        extent.attachReporter(sparkReporter);
    }

    public static String captureScreenshot(WebDriver driver) throws IOException {
        String FileSeparator = System.getProperty("file.separator"); // "/" or "\"
        String Extent_report_path = "." + FileSeparator + "Reports"; // . means parent directory
        File Src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        String screenShotName = "screenshot" + Math.random() + ".png";
        File Dst = new File(Extent_report_path + FileSeparator + "Screenshots" + FileSeparator + screenShotName);
        FileUtils.copyFile(Src, Dst);
        String absPath = Dst.getAbsolutePath();
        return absPath;
    }



    @Test(dataProvider = "getData")
    public void register(String firstName, String lastName, String phone, String email, String address, String city,
                         String state, String postalCode, String country, String userName, String password, String confirmPassword) throws IOException {
        String methodName = new Exception().getStackTrace()[0].getMethodName();
        test = extent.createTest(methodName,"Validate registration to the application");
        test.log(Status.INFO, "Starting Open Register");
        test.assignCategory("Regression Testing");

        driver.findElement(By.xpath("//input[@name='firstName']")).sendKeys(firstName);
        driver.findElement(By.xpath("//input[@name='lastName']")).sendKeys(lastName);
        driver.findElement(By.xpath("//input[@name='phone']")).sendKeys(phone);
        driver.findElement(By.xpath("//input[@id='userName']")).sendKeys(email);
        driver.findElement(By.xpath("//input[@name='address1']")).sendKeys(address);
        driver.findElement(By.xpath("//input[@name='city']")).sendKeys(city);
        driver.findElement(By.xpath("//input[@name='state']")).sendKeys(state);
        driver.findElement(By.xpath("//input[@name='postalCode']")).sendKeys(postalCode);

// Select country by dropdown
        Select countryDropdown = new Select(driver.findElement(By.xpath("//select[@name='country']")));
        countryDropdown.selectByVisibleText(country);

        driver.findElement(By.xpath("//input[@id='email']")).sendKeys(userName);
        driver.findElement(By.xpath("//input[@name='password']")).sendKeys(password);
        driver.findElement(By.xpath("//input[@name='confirmPassword']")).sendKeys(confirmPassword);
        driver.findElement(By.xpath("//input[@name='submit']")).click();
        test.addScreenCaptureFromPath(captureScreenshot(driver));
        driver.findElement(By.xpath("//a[normalize-space()='REGISTER']")).click();
    }

    @DataProvider(name = "getData")
    public Object[][] getData() throws IOException {
        String excelPath = "C:/Users/Hp/Downloads/FinalExamExcel.xlsx";
        Object[][] data;

        FileInputStream file = new FileInputStream(excelPath);
        Workbook workbook = WorkbookFactory.create(file);
        Sheet sheet = workbook.getSheetAt(0);

        int rowCount = sheet.getLastRowNum();
        int colCount = sheet.getRow(0).getLastCellNum();

        data = new Object[rowCount][colCount];
        for (int i = 0; i < rowCount; i++) {
            Row row = sheet.getRow(i + 1);
            for (int j = 0; j < colCount; j++) {
                Cell cell = row.getCell(j);
                data[i][j] = cell.toString();
            }
        }

        workbook.close();

        return data;
    }

    @BeforeTest
    public void beforeTest() {
        initializer();
        driver = new ChromeDriver();
        driver.get("https://demo.guru99.com/test/newtours/register.php");
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
    }

    @AfterTest
    public void afterTest() {
        extent.flush();
        driver.close();
    }


}
