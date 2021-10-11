package vn.infogate.model;

import lombok.Getter;
import lombok.Setter;
import vn.infogate.model.formatter.ObjectFormatter;
import vn.infogate.selector.Selector;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Wrapper of field and extractor.
 * @author code4crafter@gmail.com <br>
 * @since 0.2.0
 */
@Getter
@Setter
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
