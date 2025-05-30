import com.thoughtworks.gauge.Step;
import com.thoughtworks.gauge.AfterStep;
import com.thoughtworks.gauge.ExecutionContext;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URL;
import java.time.Duration;
import java.util.Set;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.io.IOException;

public class StepImplementation {

    private WebDriver driver;
    private WebDriverWait wait;
    private static final int DEFAULT_TIMEOUT = 10;

    @Step("<url> adresine git")
    public void navigateTo(String url) {
        String browserType = System.getenv("BROWSER_TYPE");
        if (browserType == null || browserType.isEmpty()) {
            browserType = "chrome";
            System.out.println("BROWSER_TYPE environment variable not set. Defaulting to Chrome.");
        }
        if (driver == null) { 
            driver = getDriver(browserType); 
        }
        System.out.println("Navigating to: " + url + " using " + browserType);
        driver.get(url);
        if (wait == null && driver != null) { 
             wait = new WebDriverWait(driver, Duration.ofSeconds(DEFAULT_TIMEOUT));
        }
    }

    @Step("<xpathLocator> elementine tıkla")
    public void clickElementByXpath(String xpathLocator) {
        try {
            WebElement elementToClick = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(xpathLocator)));
            elementToClick.click();
            System.out.println("'" + xpathLocator + "' XPath'ine sahip elemente tıklandı.");
        } catch (Exception e) {
            System.err.println("'" + xpathLocator + "' XPath'ine sahip element bulunamadı veya tıklanamadı: " + e.getMessage());
            throw e; 
        }
    }

    @Step("<xpathLocator> input alanına <textToEnter> metnini gir")
    public void enterTextIntoField(String xpathLocator, String textToEnter) {
        try {
            WebElement inputField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpathLocator)));
            inputField.clear();
            inputField.sendKeys(textToEnter);
            System.out.println("'" + xpathLocator + "' XPath'ine sahip input alanına '" + textToEnter + "' girildi.");
        } catch (Exception e) {
            System.err.println("'" + xpathLocator + "' XPath'ine sahip input alanı bulunamadı veya metin girilemedi: " + e.getMessage());
            throw e;
        }
    }

    @Step("<xpathLocator> XPath'e kadar kaydırılır")
    public void scrollToElement(String xpathLocator) {
        try {
            WebElement element = driver.findElement(By.xpath(xpathLocator)); 
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
            wait.until(ExpectedConditions.visibilityOf(element));
            System.out.println("'" + xpathLocator + "' XPath'ine sahip elemente kadar kaydırıldı ve element görünür.");
        } catch (Exception e) {
            System.err.println("'" + xpathLocator + "' XPath'ine sahip element bulunamadı veya kaydırılamadı: " + e.getMessage());
            throw e;
        }
    }

    @Step("<seconds> saniye beklenir")
    public void waitForSeconds(String seconds) {
        try {
            long timeToWaitInMillis = Long.parseLong(seconds) * 1000;
            Thread.sleep(timeToWaitInMillis);
            System.out.println(seconds + " saniye beklendi.");
        } catch (InterruptedException e) {
            System.err.println("Bekleme sırasında bir kesinti oluştu: " + e.getMessage());
            Thread.currentThread().interrupt();
        } catch (NumberFormatException e) {
            System.err.println("Geçersiz saniye değeri: " + seconds + ". Bir sayı bekleniyordu.");
           
            throw new IllegalArgumentException("Geçersiz saniye değeri: " + seconds, e);
        }
    }

    @Step("<expectedMessage> mesajının göründüğünü doğrula")
    public void verifyMessageVisible(String expectedMessage) {
        try {
            
            String locator = String.format("//*[contains(text(), '%s') and not(self::script) and not(self::style)]", expectedMessage);
            WebElement messageElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(locator)));
            assertTrue(messageElement.isDisplayed(), "Beklenen mesaj görünür değil: " + expectedMessage);
            System.out.println("Doğrulandı: '" + expectedMessage + "' mesajı sayfada görünüyor.");
        } catch (Exception e) {
            System.err.println("'" + expectedMessage + "' mesajı bulunamadı veya görünür değil: " + e.getMessage());
            throw e;
        }
    }

    @AfterStep
    public void afterStep(ExecutionContext context) {
        if (context.getCurrentStep().getIsFailing()) {
            if (driver instanceof TakesScreenshot) {
                try {
                    // screenshots klasörünü oluştur (eğer yoksa)
                    Path screenshotDir = Paths.get("screenshots");
                    if (!Files.exists(screenshotDir)) {
                        Files.createDirectories(screenshotDir);
                        System.out.println("Oluşturulan ekran görüntüsü klasörü: " + screenshotDir.toAbsolutePath());
                    }

                    // Dosya adı için senaryo ve adım bilgilerini al, geçersiz karakterleri temizle
                    String scenarioName = context.getCurrentScenario().getName().replaceAll("[^a-zA-Z0-9_\\-]", "_");
                    String stepText = context.getCurrentStep().getDynamicText().replaceAll("[^a-zA-Z0-9_\\-]", "_");
                    if (stepText.length() > 50) { // Adım metni çok uzunsa kısalt
                        stepText = stepText.substring(0, 50);
                    }
                    String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmssSSS").format(new Date());
                    String fileName = String.format("%s_%s_%s.png", scenarioName, stepText, timestamp);
                    Path screenshotPath = screenshotDir.resolve(fileName);

                    // Ekran görüntüsünü al ve kaydet
                    File screenshotFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
                    Files.copy(screenshotFile.toPath(), screenshotPath);
                    System.out.println("Hata oluştu, ekran görüntüsü kaydedildi: " + screenshotPath.toAbsolutePath());
                } catch (IOException e) {
                    System.err.println("Ekran görüntüsü alınırken veya kaydedilirken bir hata oluştu: " + e.getMessage());
                    e.printStackTrace();
                } catch (Exception e) {
                    System.err.println("Ekran görüntüsü alma sırasında beklenmedik bir hata: " + e.getMessage());
                    e.printStackTrace();
                }
            } else {
                System.err.println("Mevcut WebDriver TakesScreenshot arayüzünü desteklemiyor, ekran görüntüsü alınamadı.");
            }
        }
    }

    @Step("Tarayıcıyı kapat")
    public void closeBrowser() {
        if (driver != null) {
            System.out.println("Closing the browser.");
            driver.quit();
            driver = null;
            wait = null;
        }
    }

    @Step("<XpathYeniSekme> elementine tıkla ve açılan yeni sekmeye geç")
    public void clickElementAndSwitchToNewTab(String xpathLocator) {
        String originalWindow = driver.getWindowHandle();
        Set<String> oldWindows = driver.getWindowHandles();

        try {
            WebElement elementToClick = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(xpathLocator)));
            elementToClick.click();
            System.out.println("'" + xpathLocator + "' XPath'ine sahip elemente tıklandı (yeni sekme bekleniyor).");

            WebDriverWait newTabWait = new WebDriverWait(driver, Duration.ofSeconds(DEFAULT_TIMEOUT));
            newTabWait.until(ExpectedConditions.numberOfWindowsToBe(oldWindows.size() + 1));

            Set<String> allWindows = driver.getWindowHandles();
            if (allWindows.size() > oldWindows.size()) {
                for (String windowHandle : allWindows) {
                    if (!oldWindows.contains(windowHandle)) {
                        driver.switchTo().window(windowHandle);
                        System.out.println("Yeni sekmeye geçildi. URL: " + driver.getCurrentUrl());
                        this.wait = new WebDriverWait(driver, Duration.ofSeconds(DEFAULT_TIMEOUT));
                        break;
                    }
                }
            } else {
                System.out.println("Tıklama sonrası yeni bir sekme açılmadı veya tespit edilemedi.");
            }

        } catch (Exception e) {
            System.err.println("'" + xpathLocator + "' XPath'ine sahip element bulunamadı, tıklanamadı veya yeni sekmeye geçilemedi: " + e.getMessage());
            throw e;
        }
    }

    private WebDriver getDriver(String browser) {
        browser = browser.toLowerCase();
        String seleniumGridUrl = System.getenv("SELENIUM_GRID_URL");

        try {
            if (seleniumGridUrl != null && !seleniumGridUrl.isEmpty()) {
                System.out.println("Connecting to Selenium Grid at: " + seleniumGridUrl);
                switch (browser) {
                    case "chrome":
                        ChromeOptions chromeOptions = new ChromeOptions();
                        return new RemoteWebDriver(new URL(seleniumGridUrl), chromeOptions);
                    case "firefox":
                        FirefoxOptions firefoxOptions = new FirefoxOptions();
                        return new RemoteWebDriver(new URL(seleniumGridUrl), firefoxOptions);
                    case "edge":
                        EdgeOptions edgeOptions = new EdgeOptions();
                        return new RemoteWebDriver(new URL(seleniumGridUrl), edgeOptions);
                    default:
                        System.out.println("Unsupported browser for Grid: " + browser + ". Defaulting to Chrome on Grid.");
                        return new RemoteWebDriver(new URL(seleniumGridUrl), new ChromeOptions());
                }
            } else {
                System.out.println("Running locally with browser: " + browser);
                switch (browser) {
                    case "chrome":
                        WebDriverManager.chromedriver().setup();
                        ChromeOptions chromeOptionsLocal = new ChromeOptions();
                        driver = new ChromeDriver(chromeOptionsLocal);
                        break;
                    case "firefox":
                        WebDriverManager.firefoxdriver().setup();
                        FirefoxOptions firefoxOptionsLocal = new FirefoxOptions();
                        String firefoxBinaryPath = System.getenv("FIREFOX_BINARY_PATH");
                        if (firefoxBinaryPath != null && !firefoxBinaryPath.isEmpty()) {
                            firefoxOptionsLocal.setBinary(firefoxBinaryPath);
                            System.out.println("Using Firefox binary from FIREFOX_BINARY_PATH: " + firefoxBinaryPath);
                        }
                        driver = new FirefoxDriver(firefoxOptionsLocal);
                        break;
                    case "edge":
                        WebDriverManager.edgedriver().setup();
                        driver = new EdgeDriver();
                        break;
                    case "safari":
                        driver = new SafariDriver();
                        break;
                    default:
                        System.out.println("Unsupported browser: " + browser + ". Defaulting to Chrome locally.");
                        WebDriverManager.chromedriver().setup();
                        driver = new ChromeDriver();
                }
                wait = new WebDriverWait(driver, Duration.ofSeconds(DEFAULT_TIMEOUT));
                return driver;
            }
        } catch (Exception e) {
            System.err.println("Error initializing WebDriver: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to initialize WebDriver", e);
        }
    }
}
