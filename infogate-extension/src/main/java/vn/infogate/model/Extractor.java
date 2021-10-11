package vn.infogate.model;

import lombok.Getter;
import lombok.Setter;
import vn.infogate.selector.Selector;

/**
 * The object contains 'ExtractBy' information.
 * @author code4crafter@gmail.com <br>
 * @since 0.2.0
 */
@Getter
@Setter
public class Extractor {

    private Selector selector;

    private final Source source;

    private final boolean notNull;

    private final boolean multi;

    enum Source {Html, Url, RawHtml, RawText}

    public Extractor(Selector selector, Source source, boolean notNull, boolean multi) {
        this.selector = selector;
        this.source = source;
        this.notNull = notNull;
        this.multi = multi;
    }
}
