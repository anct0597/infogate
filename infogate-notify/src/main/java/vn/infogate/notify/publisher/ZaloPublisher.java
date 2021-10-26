package vn.infogate.notify.publisher;

import vn.infogate.notify.client.ZaloClient;
import vn.infogate.notify.model.ZaloMessage;

/**
 * @author anct.
 */
public class ZaloPublisher implements Publisher<ZaloMessage> {

    private ZaloClient zaloClient;

    @Override
    public void send(ZaloMessage message) {
    }
}
