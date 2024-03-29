package vn.infogate.ispider.extension.model;

import vn.infogate.ispider.core.Page;

/**
 * Interface to be implemented by page models that need to do something after fields are extracted.<br>
 *
 * @author code4crafter@gmail.com <br>
 * @since 0.2.0
 */
public interface AfterExtractor {

    void afterProcess(Page page);
}
