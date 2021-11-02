package vn.infogate.ispider.storage.solr.exception;

/**
 * @author an.cantuong
 * created at 11/2/2021
 */
public class MissingRequiredFieldException extends RuntimeException {

    public MissingRequiredFieldException(String message) {
        super(message);
    }
}
