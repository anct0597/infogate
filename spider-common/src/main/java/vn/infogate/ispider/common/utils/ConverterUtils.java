package vn.infogate.ispider.common.utils;

/**
 * @author anct.
 */
public class ConverterUtils {

    private ConverterUtils() {
    }

    /**
     * Get money.
     *
     * @param number number.
     * @param unit   unit.
     * @return long.
     */
    public static long flatPrice(double number, String unit) {
        if ("tr".equals(unit) || "trieu".equals(unit) || "triệu".equals(unit)) {
            return (long) (number * 1_000_000L);
        } else if ("ty".equals(unit) || "tỷ".equals(unit)) {
            return (long) (number * 100_000_0000L);
        }
        return (long) (number * 1000L);
    }

}
