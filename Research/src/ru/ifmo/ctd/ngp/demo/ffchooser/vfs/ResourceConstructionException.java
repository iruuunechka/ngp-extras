package ru.ifmo.ctd.ngp.demo.ffchooser.vfs;

/**
 * A superclass for all specific exceptions that may occur during
 * resource construction.
 *
 * @author Maxim Buzdalov
 */
public class ResourceConstructionException extends RuntimeException {
    private static final long serialVersionUID = 448900845930430639L;

    public ResourceConstructionException(String message) {
        super(message);
    }

    public ResourceConstructionException(String message, Throwable cause) {
        super(message, cause);
    }
}
