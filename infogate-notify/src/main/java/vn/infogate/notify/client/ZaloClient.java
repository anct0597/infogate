package vn.infogate.notify.client;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;

/**
 * @author anct.
 */
@Slf4j
public class ZaloClient extends SeleniumClient {

    private String previous;

    public ZaloClient() {
        super("https://id.zalo.me/account?continue=https://chat.zalo.me");
    }

    @Override
    public void login() {
        try {
            driver.get(loginUrl);
            Thread.sleep(1000);
            var el = driver.findElement(new By.ByXPath("//*[@id='app']/div/div/div[2]/div[2]/div[1]/ul/li[2]"));
            el.click();
            var phoneInput = driver.findElement(By.id("input-phone"));
            var pwdInput = driver.findElement(By.cssSelector("input[type=password]"));

            phoneInput.sendKeys("0962049752");
            pwdInput.sendKeys("An123456ytrewq");

            var btnSubmit = driver.findElement(By.cssSelector("div.textAlign-center.has-2btn > a"));
            btnSubmit.click();

            Thread.sleep(20000);

            driver.get("https://chat.zalo.me/");

            Thread.sleep(1000);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public void sendMessage(String to, String message) throws InterruptedException {
        if (StringUtils.isNotEmpty(to)) {
            if (to.equals(previous)) {
                sendMessage(message);
                return;
            }

            var searchContact = driver.findElement(By.id("contact-search-input"));
            searchContact.sendKeys(to);
            Thread.sleep(3000);

            var elements = driver.findElements(By.cssSelector("#searchResultList .conv-item"));
            if (elements.size() > 0) {
                elements.get(0).click();
                Thread.sleep(1000);
                sendMessage(message);
            }

            previous = to;
        }
    }

    private void sendMessage(String message) throws InterruptedException {
        var messageInput = driver.findElement(By.id("input_line_0"));
        messageInput.sendKeys(message);
        Thread.sleep(500);
        messageInput.sendKeys(Keys.ENTER);
    }
}
