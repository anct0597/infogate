package vn.infogate.notify.client;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.Closeable;

/**
 * @author anct.
 */
public abstract class SeleniumClient implements Closeable {

    protected WebDriver driver;
    protected String loginUrl;

    public SeleniumClient(String loginUrl) {
        this.loginUrl = loginUrl;
        System.setProperty("webdriver.chrome.driver", "infogate-notify/driver/chromedriver.exe");
        this.driver = new ChromeDriver();
    }

    protected abstract void login();

    protected abstract void sendMessage(String to, String message) throws InterruptedException;

    @Override
    public void close() {
        if (driver != null) {
            driver.quit();
        }
    }
}
