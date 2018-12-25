package ru.ifmo.ctd.ngp.demo.ffchooser.vfs;

/**
 * This exception is thrown when it is impossible to construct a resource
 * using existing resource constructors and resources.
 *
 * @author Maxim Buzdalov
 */
public class ResourceInconstructableException extends ResourceConstructionException {
    private static final long serialVersionUID = 6670886727432688656L;

    public ResourceInconstructableException(String resource) {
        super("Resource " + resource + " is inconstructable");
    }
}
