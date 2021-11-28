package vn.infogate.ispider.core.model.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Define the 'ignore' url patterns for class
 * All urls matching the pattern will be skipped.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface IgnoreUrl {

    /**
     * The url patterns to crawl. <br>
     * Use regex expression with some changes: <br>
     * "." stand for literal character "." instead of "any character". <br>
     * "*" stand for any legal character for url in 0-n length ([^"'#]*) instead of "any length". <br>
     *
     * @return the url patterns for class
     */
    String[] value();

    /**
     * Define the region for url extracting. <br>
     * Only support XPath.<br>
     * When sourceRegion is set, the urls will be extracted only from the region instead of entire content. <br>
     *
     * @return the region for url extracting
     */
    String sourceRegion() default "";
}
