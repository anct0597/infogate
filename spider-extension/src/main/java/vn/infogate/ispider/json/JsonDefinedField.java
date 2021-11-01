package vn.infogate.ispider.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import vn.infogate.ispider.model.Extractor;

/**
 * @author anct
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class JsonDefinedField {

    private String name;

    @JsonProperty("expr")
    private String expression;

    private Extractor.Type type = Extractor.Type.XPath;

    private boolean required = false;

    private boolean multi = false;

    private ValueType valueType = ValueType.Text;

    private Source source = Source.SelectedHtml;

    /**
     * types value.
     */
    public enum ValueType {Text, Number, Float}

    /**
     * types of source for extracting.
     */
    public enum Source {
        /**
         * extract from the content extracted by class extractor.
         */
        SelectedHtml,
        /**
         * extract from the raw html
         */
        RawHtml,
        RawText
    }
}
