package vn.infogate.notify;

import lombok.extern.slf4j.Slf4j;
import vn.infogate.notify.client.ZaloClient;

/**
 * @author anct.
 */
@Slf4j
public class Application {

    public static void main(String[] args) {
        var zaloClient = new ZaloClient();
        zaloClient.login();
        while (true) {
            try {
                zaloClient.sendMessage("0856691999", "Hello");

                Thread.sleep(2000);
            }catch (Exception e) {
                log.error(e.getMessage(), e);
                break;
            }
        }
    }
}
