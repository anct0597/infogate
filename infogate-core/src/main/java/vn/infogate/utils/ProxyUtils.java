package vn.infogate.utils;

import lombok.extern.slf4j.Slf4j;
import vn.infogate.proxy.Proxy;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Pooled Proxy Object
 *
 * @author yxssfxwzy@sina.com <br>
 * @since 0.5.1
 */
@Slf4j
public class ProxyUtils {


    public static boolean validateProxy(Proxy p) {
        Socket socket = null;
        try {
            socket = new Socket();
            InetSocketAddress endpointSocketAddr = new InetSocketAddress(p.getHost(), p.getPort());
            socket.connect(endpointSocketAddr, 3000);
            return true;
        } catch (IOException e) {
            log.error("FAILURE - CAN not connect! remote: " + p);
            return false;
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    log.error("Error occurred while closing socket of validating proxy", e);
                }
            }
        }
    }

}
