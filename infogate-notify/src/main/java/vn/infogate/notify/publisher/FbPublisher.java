package vn.infogate.notify.publisher;

import vn.infogate.notify.client.FbClient;
import vn.infogate.notify.model.FbMessage;

/**
 * @author anct.
 */
public class FbPublisher implements Publisher<FbMessage> {

    private FbClient fbClient;

    @Override
    public void send(FbMessage message) {
    }
}
