package uk.ac.ed.myed.cosign;

import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.wisc.my.webproxy.beans.http.Request;
import edu.wisc.my.webproxy.beans.interceptors.PreInterceptor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Pre-interceptor for running a chain of interceptors on requests. This should be
 * extended and the {@link #getPreInterceptors()} method extended.
 */
public abstract class ChainOfPreInterceptor extends Object implements PreInterceptor {    
    private final Log log = LogFactory.getLog(getClass());
    
    /**
     * Retrieves the list of pre-interceptors to run on requests.
     *
     * @return a list of pre-interceptors. These will be run in-order on requests.
     */
    public abstract List<? extends PreInterceptor> getPreInterceptors()
        throws InterceptorConstructionException;

    public void intercept(RenderRequest request, RenderResponse response, Request outgoingRequest) {
        try {
            for (PreInterceptor interceptor: this.getPreInterceptors()) {
                interceptor.intercept(request, response, outgoingRequest);
            }
        } catch(InterceptorConstructionException e) {
            throw new RuntimeException("Could not construct interceptors.", e);
        }
    }

    public void intercept(ActionRequest request, ActionResponse response, Request outgoingRequest) {
        try {
            for (PreInterceptor interceptor: this.getPreInterceptors()) {
                interceptor.intercept(request, response, outgoingRequest);
            }
        } catch(InterceptorConstructionException e) {
            throw new RuntimeException("Could not construct interceptors.", e);
        }
    }

    public void intercept(HttpServletRequest request, HttpServletResponse response, Request outgoingRequest) {
        try {
            for (PreInterceptor interceptor: this.getPreInterceptors()) {
                interceptor.intercept(request, response, outgoingRequest);
            }
        } catch(InterceptorConstructionException e) {
            throw new RuntimeException("Could not construct interceptors.", e);
        }
    }
    
    /**
     * Exception to be thrown in case of problems constructing interceptors.
     */
    public static class InterceptorConstructionException extends Exception {
        public              InterceptorConstructionException() {
            super();
        }
        
        public              InterceptorConstructionException(final Throwable cause) {
            super(cause);
        }
        
        public              InterceptorConstructionException(final String message) {
            super(message);
        }
        
        public              InterceptorConstructionException(final String message, final Throwable cause) {
            super(message, cause);
        }
    }
}
