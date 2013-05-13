package edu.wisc.my.webproxy.beans.interceptors;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.wisc.my.webproxy.beans.config.ConfigUtils;
import edu.wisc.my.webproxy.beans.config.GeneralConfigImpl;
import edu.wisc.my.webproxy.beans.http.Request;

/**
 * Insures that every outgoing proxy request targets the configured BaseURL.
 */
public final class BaseUrlOnlyPreInterceptor implements PreInterceptor {

    private final Log log = LogFactory.getLog(getClass());

    @Override
    public void intercept(RenderRequest req, RenderResponse res, Request httpReq) {
        enforce(req, httpReq);
    }

    @Override
    public void intercept(ActionRequest req, ActionResponse res, Request httpReq) {
        enforce(req, httpReq);
    }

    @Override
    public void intercept(HttpServletRequest request, HttpServletResponse response, Request httpResp) {
        log.warn("Invoking intercept() with HttpServletRequest/HttpServletResponse;  " +
                "The BaseURL is not accessable since there is no access to the " +
                "PortletRequest.USER_INFO map.");
        /*
         * Nothing we can do here...
         */
    }
    
    /*
     * Implementation
     */

    private void enforce(final PortletRequest req, Request httpReq) {

        final PortletPreferences prefs = req.getPreferences();

        final String baseUrlString = ConfigUtils.checkEmptyNullString(prefs.getValue(GeneralConfigImpl.BASE_URL, null), null);
        if (baseUrlString == null) {
            throw new IllegalStateException("Preference '" + GeneralConfigImpl.BASE_URL + "' not set");
        }

        httpReq.setUrl(baseUrlString);

    }

}
