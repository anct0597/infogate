package vn.infogate.notify.publisher;

import vn.infogate.notify.model.Message;

/**
 * @author anct.
 */
public interface Publisher<M extends Message> {
    void send(M message);
}
