package vn.infogate.ispider.extension.json;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import vn.infogate.ispider.core.Page;
import vn.infogate.ispider.core.Request;
import vn.infogate.ispider.core.processor.PageProcessor;
import vn.infogate.ispider.extension.model.Extractor;
import vn.infogate.ispider.extension.utils.ExtractorUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author anct.
 */
@Slf4j
@Setter
public abstract class JsonDefinedPageProcessor implements PageProcessor {

    private List<Pattern> targetUrlPatterns;
    private List<Pattern> ignoredPatterns;
    private List<JsonFieldExtractor> extractors;

    public JsonDefinedPageProcessor() {
        this.targetUrlPatterns = new ArrayList<>(2);
        this.ignoredPatterns = new ArrayList<>(1);
        this.extractors = new ArrayList<>();
    }

    @Override
    public void process(Page page) {
        extractLinks(page);
        for (var fieldExtractor : extractors) {
            if (fieldExtractor.isMulti()) {
                var values = extractMultiField(page, fieldExtractor);
                if (fieldExtractor.isNotNull() && CollectionUtils.isEmpty(values)) {
                    page.setSkip(true);
                    return;
                }
                page.putField(fieldExtractor.getFieldName(), values);
            } else {
                var value = extractNormalField(page, fieldExtractor);
                if (fieldExtractor.isNotNull() && StringUtils.isEmpty(value)) {
                    page.setSkip(true);
                    return;
                }
                page.putField(fieldExtractor.getFieldName(), value);
            }
        }
        postProcess(page);
    }

    public void init(JsonPageModel pageModel) {
        initFieldExtractor(pageModel.getFields());

        for (var pattern : pageModel.getTargetUrls()) {
            targetUrlPatterns.add(Pattern.compile(pattern.replace(".", "\\.").replace("*", "[^\"'#]*")));
        }

        for (var pattern : pageModel.getIgnoreUrls()) {
            ignoredPatterns.add(Pattern.compile(pattern.replace(".", "\\.").replace("*", "[^\"'#]*")));
        }
    }

    private void initFieldExtractor(List<JsonDefinedField> fields) {
        for (var field : fields) {
            var selector = ExtractorUtils.getSelector(field.getExpression(), field.getType());
            var sourceType = field.getSource();
            if (field.getType() == Extractor.Type.JsonPath) {
                sourceType = JsonDefinedField.Source.RawText;
            }
            JsonFieldExtractor.Source source;
            switch (sourceType) {
                case RawText:
                    source = JsonFieldExtractor.Source.RawText;
                    break;
                case RawHtml:
                    source = JsonFieldExtractor.Source.RawHtml;
                    break;
                default:
                    source = JsonFieldExtractor.Source.Html;
            }

            var fieldExtractor = new JsonFieldExtractor(field.getName(), selector, source, field.isRequired(), field.isMulti());
            extractors.add(fieldExtractor);
        }
    }

    private void extractLinks(Page page) {
        List<String> links = page.getHtml().links().all();
        for (String link : links) {
            var ignored = false;
            for (var ignoredPattern : ignoredPatterns) {
                var matcher = ignoredPattern.matcher(link);
                if (matcher.matches()) {
                    ignored = true;
                    break;
                }
            }
            if (ignored) continue;
            for (var urlPattern : targetUrlPatterns) {
                var matcher = urlPattern.matcher(link);
                if (matcher.find()) {
                    page.addTargetRequest(new Request(matcher.group(0)));
                }
            }
        }
    }

    private List<String> extractMultiField(Page page, JsonFieldExtractor fieldExtractor) {
        if (fieldExtractor.getSource() == Extractor.Source.RawHtml) {
            return page.getHtml().selectDocumentForList(fieldExtractor.getSelector());
        } else if (fieldExtractor.getSource() == Extractor.Source.RawText) {
            return fieldExtractor.getSelector().selectList(page.getRawText());
        }
        return page.getHtml().selectDocumentForList(fieldExtractor.getSelector());
    }

    private String extractNormalField(Page page, JsonFieldExtractor fieldExtractor) {
        if (fieldExtractor.getSource() == Extractor.Source.RawHtml) {
            return page.getHtml().selectDocument(fieldExtractor.getSelector());
        } else if (fieldExtractor.getSource() == Extractor.Source.RawText) {
            return fieldExtractor.getSelector().select(page.getRawText());
        }
        return page.getHtml().selectDocument(fieldExtractor.getSelector());
    }

    protected abstract void postProcess(Page page);
}
