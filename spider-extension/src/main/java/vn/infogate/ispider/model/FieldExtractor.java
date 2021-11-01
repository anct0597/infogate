package vn.infogate.ispider.model;

import lombok.Getter;
import lombok.Setter;
import vn.infogate.ispider.model.formatter.ObjectFormatter;
import vn.infogate.ispider.selector.Selector;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Wrapper of field and extractor.
 *
 * @author anct.
 */
@Getter
@Setter
@SuppressWarnings("rawtypes")
public class FieldExtractor extends Extractor {

    private final Field field;
    private Method setterMethod;
    private ObjectFormatter objectFormatter;

    public FieldExtractor(Field field,
                          Selector selector,
                          Source source,
                          boolean notNull,
                          boolean multi) {
        super(selector, source, notNull, multi);
        this.field = field;
    }
}
