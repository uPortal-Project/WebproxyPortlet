/*******************************************************************************
 * Copyright 2004, The Board of Regents of the University of Wisconsin System.
 * All rights reserved.
 *
 * A non-exclusive worldwide royalty-free license is granted for this Software.
 * Permission to use, copy, modify, and distribute this Software and its
 * documentation, with or without modification, for any purpose is granted
 * provided that such redistribution and use in source and binary forms, with or
 * without modification meets the following conditions:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * 3. Redistributions of any form whatsoever must retain the following
 * acknowledgement:
 *
 * "This product includes software developed by The Board of Regents of
 * the University of Wisconsin System."
 *
 *THIS SOFTWARE IS PROVIDED BY THE BOARD OF REGENTS OF THE UNIVERSITY OF
 *WISCONSIN SYSTEM "AS IS" AND ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING,
 *BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 *PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE BOARD OF REGENTS OF
 *THE UNIVERSITY OF WISCONSIN SYSTEM BE LIABLE FOR ANY DIRECT, INDIRECT,
 *INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 *LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 *PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 *LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 *OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 *ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *******************************************************************************/
package edu.wisc.my.webproxy.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.portlet.PortletPreferences;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.web.util.LRUTrackingModelPasser;
import org.jasig.web.util.ModelPasser;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import edu.wisc.my.webproxy.beans.config.ConfigUtils;
import edu.wisc.my.webproxy.beans.config.GeneralConfigImpl;
import edu.wisc.my.webproxy.beans.config.HttpClientConfigImpl;
import edu.wisc.my.webproxy.beans.config.HttpHeaderConfigImpl;
import edu.wisc.my.webproxy.beans.http.HttpManager;
import edu.wisc.my.webproxy.beans.http.HttpTimeoutException;
import edu.wisc.my.webproxy.beans.http.IHeader;
import edu.wisc.my.webproxy.beans.http.ParameterPair;
import edu.wisc.my.webproxy.beans.http.Request;
import edu.wisc.my.webproxy.beans.http.Response;
import edu.wisc.my.webproxy.beans.interceptors.PostInterceptor;
import edu.wisc.my.webproxy.beans.interceptors.PreInterceptor;
import edu.wisc.my.webproxy.portlet.ApplicationContextLocator;
import edu.wisc.my.webproxy.portlet.WebProxyPortlet;
import edu.wisc.my.webproxy.portlet.WebproxyConstants;

/**
 * @author Eric Dalquist <a href="mailto:edalquist@unicon.net">edalquist@unicon.net</a>
 * @version $Id$
 */
public class ProxyServlet extends HttpServlet {
    private Log LOG = LogFactory.getLog(ProxyServlet.class);
    
    public static final String SESSION_ID_PARAM = "sid";
    public static final String URL_PARAM = "url";
    public static final String POST_PARAMETERS = "POST_PARAMETERS";
    public static final String HTTP_MANAGER = "HTTP_MANAGER";
    public static final String SESSION_KEY = "SESSION_KEY";

    //All values must be lower case
    private static final Set<String> ALLOWED_HEADERS = Collections.unmodifiableSet(new HashSet<String>(Arrays.asList(
            "Cache-control".toLowerCase(), 
            "Content-Disposition".toLowerCase(), 
            "Content-Length".toLowerCase(),
            "Date".toLowerCase(), 
            "Expires".toLowerCase(),
            "Last-Modified".toLowerCase() 
            )));
    

    private final ModelPasser modelPasser = new LRUTrackingModelPasser();
    

    /**
     * @see javax.servlet.http.HttpServlet#service(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        final WebApplicationContext context = WebApplicationContextUtils.getWebApplicationContext(this.getServletContext());
        ApplicationContextLocator.setApplicationContext(context);
        
        try {
            super.service(request, response);
        }
        finally {
            ApplicationContextLocator.setApplicationContext(null);
        }
    }

    /**
     * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        final String sessionKey = request.getParameter(SESSION_KEY);
        
        final Map<Object, Object> model = this.modelPasser.getModelFromPortlet(request, response, sessionKey);
        
        if (model == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "No model exists in the session for key '" + sessionKey + "'");
            return;
        }
        
        
        final String requestType = (String)model.get(WebproxyConstants.REQUEST_TYPE);
        final Map<String, String[]> postParameters = (Map<String, String[]>)model.get(POST_PARAMETERS);
        String url = (String)model.get(URL_PARAM);
        

        final PortletPreferences prefs = (PortletPreferences)model.get(PortletPreferences.class.getName());

        //Get Persisted HTTP State
        HttpManager httpManager = (HttpManager)model.get(HTTP_MANAGER);

        Response httpResponse = null;
        try {

            boolean redirect = true;
            final int maxRedirects = ConfigUtils.parseInt(prefs.getValue(HttpClientConfigImpl.MAX_REDIRECTS, null), 5);
            for (int index = 0; index < maxRedirects && redirect; index++) {
                //create request object
                final Request httpRequest = httpManager.createRequest();

                //set URL in request
                httpRequest.setUrl(url);

                //Set headers
                final String[] headerNames = prefs.getValues(HttpHeaderConfigImpl.HEADER_NAME, new String[0]);
                final String[] headerValues = prefs.getValues(HttpHeaderConfigImpl.HEADER_VALUE, new String[0]);
                if (headerNames.length == headerValues.length) {
                    final List<IHeader> headerList = new ArrayList<IHeader>(headerNames.length);

                    for (int headerIndex = 0; headerIndex < headerNames.length; headerIndex++) {
                        final IHeader h = httpRequest.createHeader(headerNames[headerIndex], headerValues[headerIndex]);
                        headerList.add(h);
                    }

                    httpRequest.setHeaders(headerList.toArray(new IHeader[headerList.size()]));
                }
                else {
                    LOG.error("Invalid data in preferences. Header name array length does not equal header value array length");
                }

                //set Type 
                if (requestType != null) {
                    httpRequest.setType(requestType);
                }
                else {
                    httpRequest.setType(WebproxyConstants.GET_REQUEST);
                }
                
                //Set parameters
                if (postParameters != null) {
                    final List<ParameterPair> postParameterPairs = new ArrayList<ParameterPair>(postParameters.size());
                    
                    for (final Map.Entry<String, String[]> parameterEntry : postParameters.entrySet()) {
                        final String paramName = parameterEntry.getKey();

                        if (!paramName.startsWith(WebproxyConstants.UNIQUE_CONSTANT)) {
                            final String[] values = parameterEntry.getValue();
                            
                            for (int valIndex = 0; valIndex < values.length; valIndex++) {
                                final ParameterPair param = new ParameterPair(paramName, values[valIndex]);
                                postParameterPairs.add(param);
                            }
                        }
                    }

                    final ParameterPair[] params = postParameterPairs.toArray(new ParameterPair[postParameterPairs.size()]);
                    httpRequest.setParameters(params);
                }
                

                //Check to see if pre-interceptors are used.
                final String sPreInterceptor = ConfigUtils.checkEmptyNullString(prefs.getValue(GeneralConfigImpl.PRE_INTERCEPTOR_CLASS, null), null);
                if (sPreInterceptor != null) {
                    try {
                        final Class<?> preInterceptorClass = Class.forName(sPreInterceptor);
                        PreInterceptor myPreInterceptor = (PreInterceptor)preInterceptorClass.newInstance();
                        myPreInterceptor.intercept(request, response, httpRequest);
                    }
                    catch (ClassNotFoundException cnfe) {
                        final String msg = "Could not find specified pre-interceptor class '" + sPreInterceptor + "'";
                        LOG.error(msg, cnfe);
                        throw new ServletException(msg, cnfe);
                    }
                    catch (InstantiationException ie) {
                        final String msg = "Could instatiate specified pre-interceptor class '" + sPreInterceptor + "'";
                        LOG.error(msg, ie);
                        throw new ServletException(msg, ie);
                    }
                    catch (IllegalAccessException iae) {
                        final String msg = "Could instatiate specified pre-interceptor class '" + sPreInterceptor + "'";
                        LOG.error(msg, iae);
                        throw new ServletException(msg, iae);
                    }
                    catch (ClassCastException cce) {
                        final String msg = "Could not cast '" + sPreInterceptor + "' to 'edu.wisc.my.webproxy.beans.interceptors.PreInterceptor'";
                        LOG.error(msg, cce);
                        throw new ServletException(msg, cce);
                    }
                }

                try {
                    //send httpRequest
                    httpResponse = httpManager.doRequest(httpRequest);
                }
                catch (HttpTimeoutException hte) {

                    //If cached content was used this won't be reached, all other
                    //cases an exception needs to be thrown.
                    LOG.warn("Request '" + httpRequest + "' timed out", hte);
                    throw new ServletException(hte);
                    //TODO handle timeout cleanly
                }

                //Check to see if post-interceptors are used
                final String sPostInterceptor = ConfigUtils.checkEmptyNullString(prefs.getValue(GeneralConfigImpl.POST_INTERCEPTOR_CLASS, null),
                                                                                 null);
                if (sPostInterceptor != null) {
                    try {
                        final Class<?> postInterceptorClass = Class.forName(sPostInterceptor);
                        PostInterceptor myPostInterceptor = (PostInterceptor)postInterceptorClass.newInstance();
                        myPostInterceptor.intercept(request, response, httpResponse);
                    }
                    catch (ClassNotFoundException cnfe) {
                        final String msg = "Could not find specified post-interceptor class '" + sPostInterceptor + "'";
                        LOG.error(msg, cnfe);
                        throw new ServletException(msg, cnfe);
                    }
                    catch (InstantiationException ie) {
                        final String msg = "Could instatiate specified post-interceptor class '" + sPostInterceptor + "'";
                        LOG.error(msg, ie);
                        throw new ServletException(msg, ie);
                    }
                    catch (IllegalAccessException iae) {
                        final String msg = "Could instatiate specified post-interceptor class '" + sPostInterceptor + "'";
                        LOG.error(msg, iae);
                        throw new ServletException(msg, iae);
                    }
                    catch (ClassCastException cce) {
                        final String msg = "Could not cast '" + sPostInterceptor + "' to 'edu.wisc.my.webproxy.beans.interceptors.PostInterceptor'";
                        LOG.error(msg, cce);
                        throw new ServletException(msg, cce);
                    }
                }

                //Check to see if redirected
                final String tempUrl = WebProxyPortlet.checkRedirect(url, httpResponse);
                //TODO make sure this works
                if (url.equals(tempUrl))
                    redirect = false;
                else
                    url = tempUrl;
            }

            //Copy headers from proxied server
            for (final IHeader header : httpResponse.getHeaders()) {
                final String name = header.getName();
                final String value = header.getValue();

                if (!ALLOWED_HEADERS.contains(name.toLowerCase())) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Ignoring response header: " + name + "=" + value);
                    }
                    
                    continue;
                }

                if (LOG.isDebugEnabled()) {
                    LOG.debug("Copying header from request to response: " + name + "=" + value);
                }
            
                response.addHeader(name, value);
            }
            
            response.setContentType(httpResponse.getContentType());
            response.setStatus(httpResponse.getStatusCode());
            
            //Get InputStream and OutputStream
            InputStream in = null;
            OutputStream out = null;
            try {
                in = httpResponse.getResponseBodyAsStream();
                out = response.getOutputStream();

                IOUtils.copy(in, out);
                out.flush();
            }
            finally {
                if (in != null)
                    in.close();

                if (out != null) {
                    out.flush();
                    out.close();
                }
            }
        }
        finally {
            httpManager.clearData();
        }
    }

    /**
     * @see javax.servlet.http.HttpServlet#doHead(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    protected void doHead(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.err.println("doHead not implemented");
        super.doHead(request, response);
    }

    /**
     * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.err.println("doPost not implemented");
        super.doPost(request, response);
    }
}