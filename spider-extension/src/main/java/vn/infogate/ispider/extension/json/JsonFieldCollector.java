package vn.infogate.ispider.extension.json;

/**
 * @author anct.
 */
public interface JsonFieldCollector {

    Object collect(Object raw);

    String extractFrom();

    default String fieldName() {
        return extractFrom();
    }
}
