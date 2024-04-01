package selenium.learning;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.*;

import java.io.File;
import java.io.IOException;
import java.time.Duration;

public class LoginToApplication {
    public static ExtentSparkReporter sparkReporter;
    public static ExtentReports extent;
    public static ExtentTest test;
    WebDriver driver;

    public void initializer() {
        sparkReporter = new ExtentSparkReporter(System.getProperty("user.dir") + "/Reports/extentSparkReport.html");
        sparkReporter.config().setDocumentTitle("Automation Report");
        sparkReporter.config().setReportName("Test Execution Report");
        sparkReporter.config().setTheme(Theme.STANDARD);
        sparkReporter.config().setTimeStampFormat("yyyy-MM-dd HH:mm:ss");
        extent = new ExtentReports();
        extent.attachReporter(sparkReporter);
    }

    public static String captureScreenshot(WebDriver driver) throws IOException {
        String FileSeparator = System.getProperty("file.separator"); // "/" or "\"
        String Extent_report_path = "."+FileSeparator+"Reports"; // . means parent directory
        File Src = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
        String screenShotName = "screenshot"+Math.random()+".png";
        File Dst = new File(Extent_report_path+FileSeparator+"Screenshots"+FileSeparator+screenShotName);
        FileUtils.copyFile(Src, Dst);
        String absPath = Dst.getAbsolutePath();
        return absPath;
    }


    @Parameters({"userName" , "password"})
    @Test
    public void loginToApplication(String userName, String password) throws IOException {
        String methodName = new Exception().getStackTrace()[0].getMethodName();
        test = extent.createTest(methodName,"Validate login to the application");
        test.log(Status.INFO, "Starting Open Login");
        test.assignCategory("Regression Testing");

//        First Screenshot after Login
        test.addScreenCaptureFromPath(captureScreenshot(driver));
        driver.findElement(By.xpath("//input[@name='userName']")).sendKeys(userName);
        test.log(Status.INFO, "Entered UserName Successfully");
//        Second Screenshot after entering username
        test.addScreenCaptureFromPath(captureScreenshot(driver));
        driver.findElement(By.xpath("//input[@name='password']")).sendKeys(password);
        test.log(Status.PASS, "Entered password Successfully");
//        Third screenshot after entering password
        test.addScreenCaptureFromPath(captureScreenshot(driver));
        driver.findElement(By.xpath("//input[@name='submit']")).click();
//        After Login
        test.addScreenCaptureFromPath(captureScreenshot(driver));

//          to check whether login passed or failed
        String result = "Fail";
        String actualTitle = driver.getTitle();
        System.out.println("Actual Title is :"+actualTitle);
        String expectedTitle = "Login: Mercury Tours";
        if(actualTitle.equals(expectedTitle)) {
            result = "Pass";
        }
        System.out.println("Login validation :"+ result);
    }

    @BeforeTest
    public void beforeTest() {
        initializer();
        driver = new ChromeDriver();
        driver.get("https://demo.guru99.com/test/newtours/login.php");
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
    }

    @AfterTest
    public void afterTest() {
        extent.flush();
        driver.close();
    }
}

