package vn.infogate.notify.client;

/**
 * @author anct.
 */
public class FbClient extends SeleniumClient {

    public FbClient(String loginUrl) {
        super(loginUrl);
    }

    @Override
    protected void login() {
    }

    @Override
    protected void sendMessage(String to, String message) {

    }
}
