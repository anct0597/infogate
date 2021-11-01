package vn.infogate.ispider.common.utils;

import java.util.UUID;

public final class UUIdUtils {

    public static String generateSimpleUUID() {
        return UUID.randomUUID().toString();
    }
}
