package vn.infogate.ispider.model.annotation;

import vn.infogate.ispider.model.Extractor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Define the extractor for field or class.<br>
 *
 * @author code4crafter@gmail.com <br>
 * @since 0.2.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE})
public @interface ExtractBy {

    /**
     * Extractor expression, support XPath, CSS Selector and regex.
     *
     * @return extractor expression
     */
    String value();

    /**
     * Extractor type, support XPath, CSS Selector and regex.
     *
     * @return extractor type
     */
    Extractor.Type type() default Extractor.Type.XPath;

    /**
     * Define whether the field can be null.<br>
     * If set to 'true' and the extractor get no result, the entire class will be discarded. <br>
     *
     * @return whether the field can be null
     */
    boolean notNull() default false;

    /**
     * types of source for extracting.
     */
    enum Source {
        /**
         * extract from the content extracted by class extractor
         */
        SelectedHtml,
        /**
         * extract from the raw html
         */
        RawHtml,
        RawText
    }

    /**
     * The source for extracting. <br>
     * It works only if you already added 'ExtractBy' to Class. <br>
     *
     * @return the source for extracting
     */
    Source source() default Source.SelectedHtml;
}
