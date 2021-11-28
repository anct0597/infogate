package vn.infogate.ispider.core.model.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Combo 'ExtractBy' extractor with and/or operator.
 *
 * @author code4crafter@gmail.com <br>
 * @since 0.2.1
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE})
public @interface ComboExtract {

    /**
     * The extractors to be combined.
     *
     * @return the extractors to be combined
     */
    ExtractBy[] value();

    enum Op {
        /**
         * All extractors will be arranged as a pipeline. <br>
         * The next extractor uses the result of the previous as source.
         */
        And,
        /**
         * All extractors will do extracting separately, <br>
         * and the results of extractors will combine as the final result.
         */
        Or;
    }

    /**
     * Combining operation of extractors.<br>
     *
     * @return combining operation of extractors
     */
    Op op() default Op.And;

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
        RawHtml
    }

    /**
     * The source for extracting. <br>
     * It works only if you already added 'ExtractBy' to Class. <br>
     *
     * @return the source for extracting
     */
    Source source() default Source.SelectedHtml;
}
