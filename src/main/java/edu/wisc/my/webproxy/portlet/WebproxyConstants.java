/**
 * Licensed to Jasig under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Jasig licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a
 * copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

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
    
    public static final String PASS_THROUGH = UNIQUE_CONSTANT + "PASS_THROUGH";
    
    public static final String BASE_URL = new StringBuffer(UNIQUE_CONSTANT).append("URL").toString();
    
    public static final String CURRENT_STATE = "current_state";

    public static final String GET_REQUEST = "GET";

    public static final String POST_REQUEST = "POST";

    public static final String HEAD_REQUEST = "HEAD";

    public static final String LAST_USED = "lastUsed";
    
    public static final String BACK_BUTTON = "backButton";
    

}