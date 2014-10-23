package uk.ac.ed.myed.cosign;

/**
 * Common super-exception for exceptions thrown due to a problem in Cosign (cookie missing,
 * file missing, file mangled, etc.).
 */
public abstract class CosignProxyException extends Exception {
    public CosignProxyException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public CosignProxyException(final String message) {
        super(message);
    }
}
