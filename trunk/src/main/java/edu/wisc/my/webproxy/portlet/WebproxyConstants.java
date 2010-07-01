/*
 * Created on Apr 22, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package edu.wisc.my.webproxy.portlet;

import javax.portlet.PortletMode;

/**
 * @author nramzan
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public interface WebproxyConstants {
    public static final PortletMode CONFIG_MODE = new PortletMode("CONFIG");
    
    public static final String UNIQUE_CONSTANT = "edu.wisc.my.webproxy.";

    public static final String REQUEST_TYPE = new StringBuffer(UNIQUE_CONSTANT).append("sRequestType").toString();
    
    public static final String BASE_URL = new StringBuffer(UNIQUE_CONSTANT).append("URL").toString();
    
    public static final String CURRENT_STATE = "current_state";

    public static final String GET_REQUEST = "GET";

    public static final String POST_REQUEST = "POST";

    public static final String HEAD_REQUEST = "HEAD";

    public static final String LAST_USED = "lastUsed";
    
    public static final String BACK_BUTTON = "backButton";
    

}