package vn.infogate.ispider.extension.model;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import vn.infogate.ispider.core.Page;
import vn.infogate.ispider.core.selector.AndSelector;
import vn.infogate.ispider.core.selector.OrSelector;
import vn.infogate.ispider.core.selector.RegexSelector;
import vn.infogate.ispider.core.selector.Selector;
import vn.infogate.ispider.core.selector.XpathSelector;
import vn.infogate.ispider.extension.model.annotation.ComboExtract;
import vn.infogate.ispider.extension.model.annotation.ExtractBy;
import vn.infogate.ispider.extension.model.annotation.ExtractByUrl;
import vn.infogate.ispider.extension.model.annotation.IgnoreUrl;
import vn.infogate.ispider.extension.model.annotation.TargetUrl;
import vn.infogate.ispider.extension.model.formatter.ObjectFormatter;
import vn.infogate.ispider.extension.model.formatter.ObjectFormatterBuilder;
import vn.infogate.ispider.extension.utils.ClassUtils;
import vn.infogate.ispider.extension.utils.ExtractorUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import static vn.infogate.ispider.extension.model.annotation.ExtractBy.Source.RawText;


/**
 * The main internal logic of page model extractor.
 *
 * @author anct.
 * @version 1.0
 */
@Slf4j
@Getter
public class PageModelExtractor {

    private Class<?> clazz;
    private Extractor objectExtractor;
    private Selector targetUrlRegionSelector;
    private final List<FieldExtractor> fieldExtractors;
    private final List<Pattern> targetUrlPatterns;
    private final List<Pattern> ignoredPatterns;

    private PageModelExtractor() {
        this.targetUrlPatterns = new ArrayList<>(5);
        this.ignoredPatterns = new ArrayList<>(1);
        this.fieldExtractors = new ArrayList<>();
    }

    public static PageModelExtractor create(Class<?> clazz) {
        var pageModelExtractor = new PageModelExtractor();
        pageModelExtractor.init(clazz);
        return pageModelExtractor;
    }

    /**
     * Init class extractor, url patterns, field extractor.
     *
     * @param clazz class.
     */
    private void init(Class<?> clazz) {
        this.clazz = clazz;
        initClassExtractors();
        for (Field field : ClassUtils.getFieldsIncludeSuperClass(clazz)) {
            field.setAccessible(true);
            validateAnnotationOnField(field);
            var fieldExtractor = getFieldExtractor(field);
            if (fieldExtractor != null) {
                fieldExtractor.setObjectFormatter(new ObjectFormatterBuilder().setField(field).build());
                fieldExtractors.add(fieldExtractor);
            }
        }
    }

    private FieldExtractor getFieldExtractor(Field field) {
        var fieldExtractor = getAnnotationExtractBy(clazz, field);
        if (fieldExtractor == null) fieldExtractor = getAnnotationExtractCombo(clazz, field);
        if (fieldExtractor == null) fieldExtractor = getAnnotationExtractByUrl(clazz, field);
        return fieldExtractor;
    }

    private void validateAnnotationOnField(Field field) {
        if (field.isAnnotationPresent(ComboExtract.class) && field.isAnnotationPresent(ExtractBy.class)) {
            throw new IllegalStateException("Only one of 'ExtractBy ComboExtract ExtractByUrl' can be added to a field!");
        }
        if (field.isAnnotationPresent(ComboExtract.class) && field.isAnnotationPresent(ExtractByUrl.class)) {
            throw new IllegalStateException("Only one of 'ExtractBy ComboExtract ExtractByUrl' can be added to a field!");
        }
        if (field.isAnnotationPresent(ExtractBy.class) && field.isAnnotationPresent(ExtractByUrl.class)) {
            throw new IllegalStateException("Only one of 'ExtractBy ComboExtract ExtractByUrl' can be added to a field!");
        }
    }

    private FieldExtractor getAnnotationExtractByUrl(Class<?> clazz, Field field) {
        ExtractByUrl extractByUrl = field.getAnnotation(ExtractByUrl.class);
        if (extractByUrl == null) return null;
        String regexPattern = extractByUrl.value();
        if (StringUtils.isEmpty(regexPattern)) {
            regexPattern = ".*";
        }
        var fieldExtractor = new FieldExtractor(field, new RegexSelector(regexPattern), FieldExtractor.Source.Url,
                extractByUrl.notNull(), List.class.isAssignableFrom(field.getType()));
        Method setterMethod = getSetterMethod(clazz, field);
        if (setterMethod != null) {
            fieldExtractor.setSetterMethod(setterMethod);
        }
        return fieldExtractor;
    }

    private FieldExtractor getAnnotationExtractCombo(Class<?> clazz, Field field) {
        ComboExtract comboExtract = field.getAnnotation(ComboExtract.class);
        if (comboExtract == null) return null;

        ExtractBy[] extractByArr = comboExtract.value();
        Selector selector = comboExtract.op() == ComboExtract.Op.Or
                ? new OrSelector(ExtractorUtils.getSelectors(extractByArr))
                : new AndSelector(ExtractorUtils.getSelectors(extractByArr));

        var fieldExtractor = new FieldExtractor(field, selector,
                comboExtract.source() == ComboExtract.Source.RawHtml ? FieldExtractor.Source.RawHtml : FieldExtractor.Source.Html,
                comboExtract.notNull(), List.class.isAssignableFrom(field.getType()));
        Method setterMethod = getSetterMethod(clazz, field);
        if (setterMethod != null) {
            fieldExtractor.setSetterMethod(setterMethod);
        }
        return fieldExtractor;
    }

    /**
     * Get field extractor by annotation.
     */
    private FieldExtractor getAnnotationExtractBy(Class<?> clazz, Field field) {
        ExtractBy extractBy = field.getAnnotation(ExtractBy.class);
        if (extractBy == null) return null;

        Selector selector = ExtractorUtils.getSelector(extractBy);
        ExtractBy.Source sourceType = extractBy.source();
        if (extractBy.type() == Extractor.Type.JsonPath) {
            sourceType = RawText;
        }
        FieldExtractor.Source source;
        switch (sourceType) {
            case RawText:
                source = FieldExtractor.Source.RawText;
                break;
            case RawHtml:
                source = FieldExtractor.Source.RawHtml;
                break;
            default:
                source = FieldExtractor.Source.Html;

        }

        var fieldExtractor = new FieldExtractor(field, selector, source,
                extractBy.notNull(), List.class.isAssignableFrom(field.getType()));
        fieldExtractor.setSetterMethod(getSetterMethod(clazz, field));
        return fieldExtractor;
    }

    /**
     * Get setter method for field.
     * TODO: Should rewrite with lambada factory.
     */
    public Method getSetterMethod(Class<?> clazz, Field field) {
        try {
            String name = "set" + StringUtils.capitalize(field.getName());
            Method declaredMethod = clazz.getDeclaredMethod(name, field.getType());
            declaredMethod.setAccessible(true);
            return declaredMethod;
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    private void initClassExtractors() {
        initTargetUrl();
        initIgnoreUrl();
//        annotation = clazz.getAnnotation(ExtractBy.class);
//        if (annotation != null) {
//            ExtractBy extractBy = (ExtractBy) annotation;
//            objectExtractor = new Extractor(new XpathSelector(extractBy.value()), Extractor.Source.Html, extractBy.notNull(), false); //TODO: ??
//        }
    }

    /**
     * Url will be ignored if necessary.
     */
    private void initIgnoreUrl() {
        var ignoreUrl = clazz.getAnnotation(IgnoreUrl.class);
        if (ignoreUrl != null) {
            for (var pattern : ignoreUrl.value()) {
                ignoredPatterns.add(Pattern.compile(pattern.replace(".", "\\.").replace("*", "[^\"'#]*")));
            }
        }
    }

    /**
     * Get target url will be crawled and store.
     */
    private void initTargetUrl() {
        var targetUrl = clazz.getAnnotation(TargetUrl.class);
        if (targetUrl == null) {
            targetUrlPatterns.add(Pattern.compile(".*"));
            return;
        }
        for (var pattern : targetUrl.value()) {
            targetUrlPatterns.add(Pattern.compile(pattern.replace(".", "\\.").replace("*", "[^\"'#]*")));
        }
        if (StringUtils.isNotEmpty(targetUrl.sourceRegion())) {
            targetUrlRegionSelector = new XpathSelector(targetUrl.sourceRegion());
        }
    }

    /**
     * Main process page.
     *
     * @param page page.
     */
    public Object process(Page page) {
        boolean matched = false;
        for (Pattern targetPattern : targetUrlPatterns) {
            if (targetPattern.matcher(page.getUrl().toString()).matches()) {
                matched = true;
            }
        }
        if (!matched) return null;

        // Process on single page.
        if (objectExtractor == null) {
            return processSingle(page, null, true);
        }

        if (objectExtractor.isMulti()) {
            var lstModelObj = new ArrayList<>();
            List<String> rawExtract = objectExtractor.getSelector().selectList(page.getRawText());
            for (String html : rawExtract) {
                Object modelObj = processSingle(page, html, false);
                if (modelObj != null) lstModelObj.add(modelObj);
            }
            return lstModelObj;
        } else {
            String select = objectExtractor.getSelector().select(page.getRawText());
            return processSingle(page, select, false);
        }
    }

    private Object processSingle(Page page, String html, boolean isRaw) {
        Object modelObj = null;
        try {
            modelObj = clazz.getDeclaredConstructor().newInstance();
            for (FieldExtractor fieldExtractor : fieldExtractors) {
                if (fieldExtractor.isMulti()) {
                    var values = extractFieldMultiValue(page, html, isRaw, fieldExtractor);
                    if (CollectionUtils.isEmpty(values) && fieldExtractor.isNotNull()) {
                        return null;
                    }
                    if (fieldExtractor.getObjectFormatter() != null) {
                        List<Object> converted = convert(values, fieldExtractor.getObjectFormatter());
                        setField(modelObj, fieldExtractor, converted);
                    } else {
                        setField(modelObj, fieldExtractor, values);
                    }
                } else {
                    var value = extractNormalField(page, html, isRaw, fieldExtractor);
                    if (StringUtils.isEmpty(value) && fieldExtractor.isNotNull()) {
                        return null;
                    }
                    if (fieldExtractor.getObjectFormatter() != null) {
                        Object converted = convert(value, fieldExtractor.getObjectFormatter());
                        setField(modelObj, fieldExtractor, converted);
                    } else {
                        setField(modelObj, fieldExtractor, value);
                    }
                }
            }
            if (AfterExtractor.class.isAssignableFrom(clazz)) {
                ((AfterExtractor) modelObj).afterProcess(page);
            }
        } catch (Exception e) {
            log.error("extract fail", e);
        }
        return modelObj;
    }

    private String extractNormalField(Page page,
                                      String html,
                                      boolean isRaw,
                                      FieldExtractor fieldExtractor) {
        switch (fieldExtractor.getSource()) {
            case RawHtml:
                return page.getHtml().selectDocument(fieldExtractor.getSelector());
            case Html:
                if (isRaw) {
                    return page.getHtml().selectDocument(fieldExtractor.getSelector());
                } else {
                    return fieldExtractor.getSelector().select(html);
                }
            case Url:
                return fieldExtractor.getSelector().select(page.getUrl().toString());
            case RawText:
                return fieldExtractor.getSelector().select(page.getRawText());
            default:
                return fieldExtractor.getSelector().select(html);
        }
    }

    private List<String> extractFieldMultiValue(Page page,
                                                String html,
                                                boolean isRaw,
                                                FieldExtractor fieldExtractor) {
        switch (fieldExtractor.getSource()) {
            case RawHtml:
                return page.getHtml().selectDocumentForList(fieldExtractor.getSelector());
            case Html:
                if (isRaw) {
                    return page.getHtml().selectDocumentForList(fieldExtractor.getSelector());
                } else {
                    return fieldExtractor.getSelector().selectList(html);
                }
            case Url:
                return fieldExtractor.getSelector().selectList(page.getUrl().toString());
            case RawText:
                return fieldExtractor.getSelector().selectList(page.getRawText());
            default:
                return fieldExtractor.getSelector().selectList(html);
        }
    }

    @SuppressWarnings("rawtypes")
    private Object convert(String value, ObjectFormatter objectFormatter) {
        try {
            if (StringUtils.isNotEmpty(value)) {
                Object format = objectFormatter.format(value);
                log.debug("String {} is converted to {}", value, format);
                return format;
            }
        } catch (Exception e) {
            log.error("convert " + value + " to " + objectFormatter.clazz() + " error!", e);
        }
        return null;
    }

    @SuppressWarnings("rawtypes")
    private List<Object> convert(List<String> values, ObjectFormatter objectFormatter) {
        if (CollectionUtils.isEmpty(values)) return Collections.emptyList();

        List<Object> objects = new ArrayList<>(values.size());
        for (String value : values) {
            Object converted = convert(value, objectFormatter);
            if (converted != null) {
                objects.add(converted);
            }
        }
        return objects;
    }

    private void setField(Object o, FieldExtractor fieldExtractor, Object value) throws IllegalAccessException, InvocationTargetException {
        if (value != null) {
            if (fieldExtractor.getSetterMethod() != null) {
                fieldExtractor.getSetterMethod().invoke(o, value);
            }
            fieldExtractor.getField().set(o, value);
        }
    }
}
