package uk.ac.ed.myed.cosign;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletContext;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletContext;

import edu.wisc.my.webproxy.beans.http.IHeader;
import edu.wisc.my.webproxy.beans.http.Request;
import edu.wisc.my.webproxy.beans.interceptors.PreInterceptor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Pre-interceptor for extracting proxied credentials from the file on disk and
 * presenting them to remote web applications.
 */
public class CosignPreInterceptor implements PreInterceptor {
    /**
     * Default path under which to look for Cosign proxy cookie files.
     */
    public static final String DEFAULT_BASE_PATH = "/var/cosign/proxy";
    
    /**
     * Default service name to check against when searching cookies for the Cosign cookie.
     */
    public static final String DEFAULT_COSIGN_SERVICE = "cosign-eucsCosign-www.myed.ed.ac.uk";
    
    public static final String PREFERENCE_COSIGN_BASE_PATH = "uk.ac.ed.myed.cosign.cosign_base_path";
    public static final String PREFERENCE_COSIGN_SERVICE = "uk.ac.ed.myed.cosign.cosign_service";
    
    public static final String SESSION_COSIGN_COOKIES = "cosign_cookies";
    
    private final Log log = LogFactory.getLog(getClass());

    public void intercept(RenderRequest request, RenderResponse response, Request outgoingRequest) {
        this.intercept(request, outgoingRequest);
    }

    public void intercept(ActionRequest request, ActionResponse response, Request outgoingRequest) {
        this.intercept(request, outgoingRequest);
    }

    protected void intercept(PortletRequest request, final Request outgoingRequest) {
        if (log.isDebugEnabled()) {
            log.debug("Adding Cosign cookies to request for "
                + outgoingRequest.getUrl());
        }

        final org.apache.http.cookie.Cookie cookie;
        try {
            cookie = getProxyCookie(request, outgoingRequest);
        } catch (MalformedURLException e) {
            throw new RuntimeException("Proxied URL is invalid.", e);
        } catch (IOException e) {
            throw new RuntimeException("There was an IO exception while accessing the Cosign proxy file.", e);
        } catch (CosignProxyException e) {
            throw new RuntimeException(e.getMessage());
        } catch (UnknownServiceException e) {
            throw new RuntimeException(e.getMessage());
        }

        if (log.isDebugEnabled()) {
            log.debug("Cosign: "
                + cookie.getName() + "="
                + cookie.getValue());
        }
        
        outgoingRequest.addCookie(cookie);
    }

    public void intercept(HttpServletRequest request, HttpServletResponse response, Request outgoingRequest) {
        if (log.isDebugEnabled()) {
            log.debug("Adding Cosign cookies to request for "
                + outgoingRequest.getUrl());
        }

        final org.apache.http.cookie.Cookie cookie;
        try {
            cookie = getProxyCookie(request, outgoingRequest);
        } catch (MalformedURLException e) {
            throw new RuntimeException("Proxied URL is invalid.", e);
        } catch (IOException e) {
            throw new RuntimeException("There was an IO exception while accessing the Cosign proxy file.", e);
        } catch (CosignProxyException e) {
            throw new RuntimeException(e.getMessage());
        } catch (UnknownServiceException e) {
            throw new RuntimeException(e.getMessage());
        }

        if (log.isInfoEnabled()) {
            log.info("Cosign: "
                + cookie.getName() + "="
                + cookie.getValue());
        }
        
        outgoingRequest.addCookie(cookie);
    }

    /**
     * Extracts the relevant proxy cookie for the service that the request connects to.
     */
    private org.apache.http.cookie.Cookie getProxyCookie(final PortletRequest req, final Request outgoingRequest)
            throws IOException, MalformedProxyLineException, MissingCosignCookieException, MissingCosignFileException, MalformedURLException, UnknownServiceException {
        final PortletSession session = req.getPortletSession();
        final PortletPreferences preferences = req.getPreferences();
        
        Map<String, org.apache.http.cookie.Cookie> cookieHash = null;
        
        /*
         *  We need to check if person has a session attribute called cookies set.
         */
        if (null != session.getAttribute(SESSION_COSIGN_COOKIES)) {
            cookieHash = (Map<String, org.apache.http.cookie.Cookie>)session.getAttribute(SESSION_COSIGN_COOKIES);
        } else {
            final PortletContext context = session.getPortletContext();
            
            /*
             * The values 'path' and 'cosignService' are read here from
             * the portlet context, or we use intelligent defaults if they're
             * not available.
             */
             
            final String path = preferences.getValue(PREFERENCE_COSIGN_BASE_PATH, DEFAULT_BASE_PATH);
            final String cosignService = preferences.getValue(PREFERENCE_COSIGN_SERVICE, DEFAULT_COSIGN_SERVICE);
        
            cookieHash = this.getCookieHash(outgoingRequest, getCookiesKludge(req),
                new File(path), cosignService);
            session.setAttribute(SESSION_COSIGN_COOKIES, cookieHash);
        }

        final URL url = new URL(outgoingRequest.getUrl());
        final String host = url.getHost();
        final org.apache.http.cookie.Cookie cosignCookie = cookieHash.get(host);

        if (null == cosignCookie) {
            throw new UnknownServiceException("Could not find the host \""
                    + host + "\" in the list of proxied hosts: "
                    + cookieHash.keySet());
        }

        return cosignCookie;
    }

    /**
     * Extracts the relevant proxy cookie for the service that the request connects to.
     */
    private org.apache.http.cookie.Cookie getProxyCookie(final HttpServletRequest req, final Request outgoingRequest)
            throws IOException, MalformedProxyLineException, MissingCosignCookieException, MissingCosignFileException, MalformedURLException, UnknownServiceException {
        final HttpSession session = req.getSession(true);
        Map<String, org.apache.http.cookie.Cookie> cookieHash = null;
        
        /*
         *  We need to check if person has a session attribute called cookies set.
         */
        if (null != session.getAttribute(SESSION_COSIGN_COOKIES)) {
            cookieHash = (Map<String, org.apache.http.cookie.Cookie>)session.getAttribute(SESSION_COSIGN_COOKIES);
        } else {
            final ServletContext context = session.getServletContext();
            
            /*
             *  The values 'path' and 'cosignService' are read here from
             *  the servlet context, or we use intelligent defaults otherwise.
             */
            String path = context.getInitParameter(PREFERENCE_COSIGN_BASE_PATH);
            final String cosignService = context.getInitParameter(PREFERENCE_COSIGN_SERVICE);
            if (null == path) {
                path = DEFAULT_BASE_PATH;
            }
            
            cookieHash = this.getCookieHash(outgoingRequest, req.getCookies(),
                new File(path), cosignService);
            session.setAttribute(SESSION_COSIGN_COOKIES, cookieHash);
        }

        final URL url = new URL(outgoingRequest.getUrl());
        final String host = url.getHost();
        final org.apache.http.cookie.Cookie cosignCookie = cookieHash.get(host);

        if (null == cosignCookie) {
            throw new UnknownServiceException("Could not find the host \""
                    + host + "\" in the list of proxied hosts: "
                    + cookieHash.keySet());
        }

        return cosignCookie;
    }

    /**
     * Ugly hack to get cookies in any way we can; for the Portlet 2.0 spec this
     * is reasonably straightforward, but for the 1.0 spec we have to hope we
     * can pull the underlying HttpServletRequest out of the PortletRequest.
     */
    private javax.servlet.http.Cookie[] getCookiesKludge(final PortletRequest req)
            throws RuntimeException {
        final Class<? extends PortletRequest> reqClazz = req.getClass();

        try {
            try {
                final Method getCookiesMethod = reqClazz.getMethod("getCookies");

                return (javax.servlet.http.Cookie[]) getCookiesMethod.invoke(req);
            } catch (NoSuchMethodException e) {
                // Means we're on the portlet 1.0 spec, so we try a work-around instead
            }

            // Basically we just have to hope we've got a class as in org.apache.pluto.internal.InternalPortletRequest
            try {
                log.info("Attempting to extract cookies from request using Pluto-specific API; this is a work-around until moving to a JSR-268 complaint portal.");
                final Method getHttpServletRequestMethod = reqClazz.getMethod("getHttpServletRequest");
                final HttpServletRequest httpReq = (HttpServletRequest) getHttpServletRequestMethod.invoke(req);

                return httpReq.getCookies();
            } catch (NoSuchMethodException e) {
                // Means we're not on portlet 2.0 spec, nor using Pluto. Allow processing
                // to continue to the throw at the end of the method.
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Could not use reflection to access \"getCookies()\" method on PortletRequest.", e);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Could not use reflection to access \"getCookies()\" method on PortletRequest.", e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException("Could not use reflection to access \"getCookies()\" method on PortletRequest.", e);
        } catch (SecurityException e) {
            throw new RuntimeException("Could not use reflection to access \"getCookies()\" method on PortletRequest.", e);
        }

        throw new RuntimeException("Could not extract cookies from request; need to use the Portlet 2.0 spec, or Pluto.");
    }

    /**
     * Gets the hashmap of service names to their repesective cookies.
     *
     * @param outoingRequest the request being sent to the remote server.
     * @param cookies the cookies sent by the client.
     * @param path the path to the Cosign cookies on disk.
     * @param cosignService the unique identifier for the Cosign service.
     * @return a map from service names to cookie values.
     */
    private Map<String, org.apache.http.cookie.Cookie> getCookieHash(final Request outgoingRequest,
        final javax.servlet.http.Cookie[] cookies, final File path, final String cosignServiceId)
            throws IOException, MalformedProxyLineException, MissingCosignCookieException, MissingCosignFileException {
        final Map<String, org.apache.http.cookie.Cookie> cookieHash = new HashMap<String, org.apache.http.cookie.Cookie>();

        /*
         *  We need to find our file path and try to read service cookies.
         */
        javax.servlet.http.Cookie cosignCookie = null;

        for (int cookieIdx = 0; cookieIdx < cookies.length; cookieIdx++) {
            if (cosignServiceId.equals(cookies[cookieIdx].getName())) {
                cosignCookie = cookies[cookieIdx];
                break;
            }
        }

        if (null == cosignCookie) {
            final List<String> cookieNames = new ArrayList<String>(cookies.length);

            for (int cookieIdx = 0; cookieIdx < cookies.length; cookieIdx++) {
                cookieNames.add(cookies[cookieIdx].getName());
            }

            throw new MissingCosignCookieException("Could not find a suitable Cosign cookie. Received cookies are: "
                    + cookieNames);
        }

        final String cosignService = cosignCookie.getName();
        final File cookieFile;
        String filename = cosignCookie.getValue();

        // Remove any content after a '/' character in the filename
        if (filename.lastIndexOf("/") != -1) {
            filename = filename.substring(0, filename.lastIndexOf("/"));
        }
        cookieFile = new File(path, cosignService + "=" + filename);

        try {
            final BufferedReader br = new BufferedReader(new FileReader(cookieFile));
            try {
                String line;

                while ((line = br.readLine()) != null) {
                    String[] parts = line.split(" ");

                    if (parts.length != 2) {
                        throw new MalformedProxyLineException("Encountered mangled configuration line \""
                                + line + "\".");
                    }
                    String service = parts[0];
                    final String host = parts[1];

                    // strip the leading 'x' from service cookie
                    if (service.startsWith("x")) {
                        service = service.substring(1, service.length());
                    }

                    final String[] serviceParts = service.split("=");

                    if (serviceParts.length != 2) {
                        throw new MalformedProxyLineException("Encountered mangled cookie \""
                            + service + "\".");
                    }

                    final org.apache.http.impl.cookie.BasicClientCookie serviceCookie = new org.apache.http.impl.cookie.BasicClientCookie(serviceParts[0], serviceParts[1]);

                    serviceCookie.setDomain(host);
                    serviceCookie.setPath("/");
                    serviceCookie.setSecure(cosignCookie.getSecure());

                    cookieHash.put(host, serviceCookie);
                }
            } finally {
                br.close();
            }
        } catch (java.io.FileNotFoundException e) {
            throw new MissingCosignFileException("Cosign proxy file \""
                    + cookieFile.getCanonicalPath() + "\" does not exist.", e);
        }

        if (cookieHash.isEmpty()) {
            log.warn("Cosign proxy file \""
                    + cookieFile.getCanonicalPath() + "\" contained no data.");
        }

        return cookieHash;
    }

    /**
     * Exception that indicates there as a mangled data line in the proxy file
     * on disk.
     */
    public static class MalformedProxyLineException extends CosignProxyException {

        protected MalformedProxyLineException(final String message) {
            super(message);
        }
    }

    /**
     * Exception that indicates the Cosign cookie could not be found in the
     * request.
     */
    public static class MissingCosignCookieException extends CosignProxyException {

        protected MissingCosignCookieException(final String message) {
            super(message);
        }
    }

    /**
     * Exception that indicates the Cosign proxy data file was not present on disk.
     */
    public static class MissingCosignFileException extends CosignProxyException {

        protected MissingCosignFileException(final String message, final Throwable cause) {
            super(message, cause);
        }

        protected MissingCosignFileException(final String message) {
            super(message);
        }
    }

    /**
     * Exception that indicates the Cosign proxy data file did not contain an entry for the
     * requested service. This is a local configuration issue, not a Cosign problem, and hence
     * does not extend CosignProxyException.
     */
    public static class UnknownServiceException extends Exception {

        protected UnknownServiceException(final String message) {
            super(message);
        }
    }
}
