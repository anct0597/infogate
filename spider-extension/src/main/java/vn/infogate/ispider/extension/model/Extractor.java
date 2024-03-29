package vn.infogate.ispider.extension.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.infogate.ispider.core.selector.Selector;

/**
 * The object contains 'ExtractBy' information.
 *
 * @author anct
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Extractor {

    private Selector selector;

    private Source source;

    private boolean notNull;

    private boolean multi;

    public enum Source {Html, Url, RawHtml, RawText}

    public enum Type {
        XPath, Regex, Css, JsonPath
    }
}
