package vn.infogate.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author anct
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class JsonPageModel {

    private String key;

    private List<String> targetUrls;

    private List<String> ignoreUrls;

    private List<String> startUrls;

    private String pageProcessor;

    private List<String> pipelines;

    private List<JsonDefinedField> fields;
}
