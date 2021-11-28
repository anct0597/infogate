package vn.infogate.ispider.extension.json;

import lombok.Getter;
import lombok.Setter;
import vn.infogate.ispider.core.model.Extractor;
import vn.infogate.ispider.core.selector.Selector;

/**
 * @author anct.
 */
@Getter
@Setter
public class JsonFieldExtractor extends Extractor {

    private String fieldName;

    public JsonFieldExtractor(String fieldName,
                              Selector selector,
                              Source source,
                              boolean notNull,
                              boolean multi) {
        super(selector, source, notNull, multi);
        this.fieldName = fieldName;
    }
}
