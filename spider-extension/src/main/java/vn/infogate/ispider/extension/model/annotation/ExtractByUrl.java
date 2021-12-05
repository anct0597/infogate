package vn.infogate.ispider.extension.model.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Define an extractor to extract data in url of current page. Only regex can be used. <br>
 *
 * @author code4crafter@gmail.com <br>
 * @since 0.2.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface ExtractByUrl {

    /**
     * Extractor expression, only regex can be used
     *
     * @return extractor expression
     */
    String value() default "";

    /**
     * Define whether the field can be null.<br>
     * If set to 'true' and the extractor get no result, the entire class will be discarded. <br>
     *
     * @return whether the field can be null
     */
    boolean notNull() default false;
}
