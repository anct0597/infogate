package vn.infogate.ispider.extension.handler;

import vn.infogate.ispider.core.Request;

/**
 * @author code4crafer@gmail.com
 * @since 0.5.0
 */
public interface RequestMatcher {

    /**
     * Check whether to process the page.<br><br>
     * Please DO NOT change page status in this method.
     *
     * @param page page
     *
     * @return whether matches
     */
    boolean match(Request page);

    enum MatchOther {
        YES, NO
    }
}
