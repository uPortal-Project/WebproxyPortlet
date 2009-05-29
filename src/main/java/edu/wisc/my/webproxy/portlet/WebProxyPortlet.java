/*******************************************************************************
 * Copyright 2004, The Board of Regents of the University of Wisconsin System.
 * All rights reserved.
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
package edu.wisc.my.webproxy.portlet;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.GenericPortlet;
import javax.portlet.PortletException;
import javax.portlet.PortletMode;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.PortletSession;
import javax.portlet.ReadOnlyException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ValidatorException;
import javax.portlet.WindowState;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.NTCredentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.springframework.context.ApplicationContext;
import org.springframework.web.portlet.context.PortletApplicationContextUtils;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import edu.wisc.my.webproxy.beans.PortletPreferencesWrapper;
import edu.wisc.my.webproxy.beans.cache.CacheEntry;
import edu.wisc.my.webproxy.beans.cache.CacheOutputStream;
import edu.wisc.my.webproxy.beans.cache.PageCache;
import edu.wisc.my.webproxy.beans.config.CacheConfigImpl;
import edu.wisc.my.webproxy.beans.config.ConfigPage;
import edu.wisc.my.webproxy.beans.config.ConfigUtils;
import edu.wisc.my.webproxy.beans.config.GeneralConfigImpl;
import edu.wisc.my.webproxy.beans.config.HttpClientConfigImpl;
import edu.wisc.my.webproxy.beans.config.HttpHeaderConfigImpl;
import edu.wisc.my.webproxy.beans.config.StaticHtmlConfigImpl;
import edu.wisc.my.webproxy.beans.filtering.ChainingSaxFilter;
import edu.wisc.my.webproxy.beans.filtering.HtmlOutputFilter;
import edu.wisc.my.webproxy.beans.filtering.HtmlParser;
import edu.wisc.my.webproxy.beans.http.HttpManager;
import edu.wisc.my.webproxy.beans.http.HttpManagerService;
import edu.wisc.my.webproxy.beans.http.HttpTimeoutException;
import edu.wisc.my.webproxy.beans.http.IHeader;
import edu.wisc.my.webproxy.beans.http.ParameterPair;
import edu.wisc.my.webproxy.beans.http.Request;
import edu.wisc.my.webproxy.beans.http.Response;
import edu.wisc.my.webproxy.beans.interceptors.PostInterceptor;
import edu.wisc.my.webproxy.beans.interceptors.PreInterceptor;
import edu.wisc.my.webproxy.servlet.ProxyServlet;
import edu.wisc.my.webproxy.servlet.SessionMappingListener;

/**
 * 
 * @author Dave Grimwood <a
 *         href="mailto:dgrimwood@unicon.net">dgrimwood@unicon.net </a>
 * @version $Id$
 */
public class WebProxyPortlet extends GenericPortlet {

    private static final Log LOG = LogFactory.getLog(WebProxyPortlet.class);

    private static final String HEADER = "/WEB-INF/jsp/header.jsp";

    private static final String FOOTER = "/WEB-INF/jsp/footer.jsp";

    private static final String MANUAL = "/WEB-INF/jsp/manual.jsp";

    public final static String preferenceKey = WebProxyPortlet.class.getName();
    
    
    private static WebProxyPortlet instance = null;
    
    public static WebProxyPortlet getInstances() {
        return instance;
    }

    /**
     * @see javax.portlet.Portlet#destroy()
     */
    public void destroy() {
        super.destroy();
        instance = null;
    }
    /**
     * @see javax.portlet.GenericPortlet#init()
     */
    public void init() throws PortletException {
        super.init();
        instance = this;
    }
    
    @Override
    public void render(RenderRequest request, RenderResponse response) throws PortletException, IOException {
        
        // We're overriding render() on GenericPortlet for the sole purpose of 
        // *not* executing the following commented below, which would typically 
        // set all titles from this portlet to 'Authenticated Web Proxy.'
        //
        // response.setTitle(getTitle(request));
        doDispatch(request, response);

    }

    public void doDispatch(final RenderRequest request, final RenderResponse response) throws PortletException, IOException {
        final ApplicationContext context = PortletApplicationContextUtils.getWebApplicationContext(this.getPortletContext());
        ApplicationContextLocator.setApplicationContext(context);
        
        try {
            final PortletMode mode = request.getPortletMode();
            final WindowState windowState = request.getWindowState();
            if (LOG.isDebugEnabled()) {
                LOG.debug("Rendering with PortletMode='" + mode + "' and WindowState='" + windowState + "'");
            }
            
            //If the windowstate is minimized do not call doView
            if (!windowState.equals(WindowState.MINIMIZED)) {
                if (PortletMode.VIEW.equals(mode) || PortletMode.EDIT.equals(mode)) {
                    if (!manualLogin(request, response)) {
                        renderContent(request, response);
                    }
                }
                else if (WebproxyConstants.CONFIG_MODE.equals(mode)) {
                    ConfigPage currentConfig = getConfig(request.getPortletSession());
    
                    PortletRequestDispatcher headerDispatch = getPortletContext().getRequestDispatcher(HEADER);
                    headerDispatch.include(request, response);
    
                    try {
                        currentConfig.render(getPortletContext(), request, response);
                    }
                    catch (PortletException pe) {
                        LOG.error("Caught an exception trying to retreive portlet preferences in configuration mode: ", pe);
                    }
    
                    PortletRequestDispatcher footerDispatch = getPortletContext().getRequestDispatcher(FOOTER);
                    footerDispatch.include(request, response);
                }
                else {
                    throw new PortletException("'" + mode + "' Not Implemented");
                }
            }
        }
        finally {
            ApplicationContextLocator.setApplicationContext(null);
        }
    }


    private void renderContent(final RenderRequest request, final RenderResponse response) throws PortletException, IOException {
        //Gets a reference to the ApplicationContext created by the
        //ContextLoaderListener which is configured in the web.xml for
        //this portlet
        final ApplicationContext context = PortletApplicationContextUtils.getWebApplicationContext(this.getPortletContext());
        ApplicationContextLocator.setApplicationContext(context);

        //retrieve portlet preferences from PortletPreferencesWrapper object
        // substitution
        final Map userInfo = (Map)request.getAttribute(RenderRequest.USER_INFO);
        PortletPreferences myPreferences = request.getPreferences();
        myPreferences = new PortletPreferencesWrapper(myPreferences, userInfo);

        //Get users session
        PortletSession session = request.getPortletSession();
        session.setAttribute(WebproxyConstants.NAMESPACE, response.getNamespace());

        
        String sUrl = (String)session.getAttribute(GeneralConfigImpl.BASE_URL);
        
        if (sUrl == null) {
            sUrl = ConfigUtils.checkEmptyNullString(myPreferences.getValue(GeneralConfigImpl.BASE_URL, null), null);

            if (sUrl == null) {
                throw new PortletException("No Initial URL Configured");
            }
        }
        
        

        //use Edit Url if in Edit mode
        final PortletMode mode = request.getPortletMode();
        if (PortletMode.EDIT.equals(mode)) {
            String sTemp = myPreferences.getValue(GeneralConfigImpl.EDIT_URL, null);
            if (sTemp!=null){
                sUrl = sTemp;
            }
        }
        
        String sRequestType;
        if (request.getParameter(WebproxyConstants.REQUEST_TYPE) != null)
            sRequestType = request.getParameter(WebproxyConstants.REQUEST_TYPE);
        else
            sRequestType = null;

        HttpManagerService findingService = (HttpManagerService) context.getBean("HttpManagerService", HttpManagerService.class);
        final HttpManager httpManager = findingService.findManager(request);
        httpManager.setRenderData(request, response);

        this.doFormAuth(httpManager, request);

        final boolean sUseCache = new Boolean(myPreferences.getValue(CacheConfigImpl.USE_CACHE, null)).booleanValue();
        if (sUseCache) {
            final PageCache cache = (PageCache)context.getBean("PageCache", PageCache.class);

            final String cacheKey = generateCacheKey(sUrl, response.getNamespace());

            final CacheEntry cachedData = cache.getCachedPage(cacheKey);

            if (cachedData != null) {
                if (LOG.isTraceEnabled())
                    LOG.trace("Using cached content for key '" + cacheKey + "'");

                response.setContentType(cachedData.getContentType());
                response.getPortletOutputStream().write(cachedData.getContent());
                return;
            }
        }

        Response httpResponse = null;
        try {
            boolean redirect = true;
            final int maxRedirects = ConfigUtils.parseInt(myPreferences.getValue(HttpClientConfigImpl.MAX_REDIRECTS, null), 5);
            for (int index = 0; index < maxRedirects && redirect; index++) {
                this.doHttpAuth(request, httpManager);

                //create request object
                final Request httpRequest = httpManager.createRequest();

                //set URL in request
                httpRequest.setUrl(sUrl);

                //Set headers
                final String[] headerNames = myPreferences.getValues(HttpHeaderConfigImpl.HEADER_NAME, new String[0]);
                final String[] headerValues = myPreferences.getValues(HttpHeaderConfigImpl.HEADER_VALUE, new String[0]);
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
                //check to see if form was a GET form
                //set Type (e.g., GET, POST, HEAD)
                if (sRequestType == null) {
                    httpRequest.setType(WebproxyConstants.GET_REQUEST);
                }
                else {
                    httpRequest.setType(sRequestType);

                    //If post add any parameters to the method
                    if (sRequestType.equals(WebproxyConstants.POST_REQUEST)) {
                        final List<ParameterPair> postParameters = new ArrayList<ParameterPair>(request.getParameterMap().size());
                        for (Enumeration e = request.getParameterNames(); e.hasMoreElements();) {
                            final String paramName = (String)e.nextElement();

                            if (!paramName.startsWith(WebproxyConstants.UNIQUE_CONSTANT)) {
                                final String[] values = request.getParameterValues(paramName);
                                
                                for (int valIndex = 0; valIndex < values.length; valIndex++) {
                                    final ParameterPair param = new ParameterPair(paramName, values[valIndex]);
                                    postParameters.add(param);
                                }
                            }
                        }

                        final ParameterPair[] params = postParameters.toArray(new ParameterPair[postParameters.size()]);
                        httpRequest.setParameters(params);
                    }
                }

                //Check to see if pre-interceptors are used.
                final String sPreInterceptor = ConfigUtils.checkEmptyNullString(myPreferences.getValue(GeneralConfigImpl.PRE_INTERCEPTOR_CLASS, null), null);
                if (sPreInterceptor != null) {
                    try {
                        final Class preInterceptorClass = Class.forName(sPreInterceptor);
                        PreInterceptor myPreInterceptor = (PreInterceptor)preInterceptorClass.newInstance();
                        myPreInterceptor.intercept(request, response, httpRequest);
                    }
                    catch (ClassNotFoundException cnfe) {
                        final String msg = "Could not find specified pre-interceptor class '" + sPreInterceptor + "'";
                        LOG.error(msg, cnfe);
                        throw new PortletException(msg, cnfe);
                    }
                    catch (InstantiationException ie) {
                        final String msg = "Could instatiate specified pre-interceptor class '" + sPreInterceptor + "'";
                        LOG.error(msg, ie);
                        throw new PortletException(msg, ie);
                    }
                    catch (IllegalAccessException iae) {
                        final String msg = "Could instatiate specified pre-interceptor class '" + sPreInterceptor + "'";
                        LOG.error(msg, iae);
                        throw new PortletException(msg, iae);
                    }
                    catch (ClassCastException cce) {
                        final String msg = "Could not cast '" + sPreInterceptor + "' to 'edu.wisc.my.webproxy.beans.interceptors.PreInterceptor'";
                        LOG.error(msg, cce);
                        throw new PortletException(msg, cce);
                    }
                }

                try {
                    //send httpRequest
                    httpResponse = httpManager.doRequest(httpRequest);
                }
                catch (HttpTimeoutException hte) {
                    final boolean sUseExpired = new Boolean(myPreferences.getValue(CacheConfigImpl.USE_EXPIRED, null)).booleanValue();
                    if (sUseCache && sUseExpired) {
                        LOG.info("Request '" + sUrl + "' timed out. Attempting to use expired cache data.");
                        final PageCache cache = (PageCache)context.getBean("PageCache", PageCache.class);

                        final String cacheKey = generateCacheKey(sUrl, response.getNamespace());

                        final CacheEntry cachedData = cache.getCachedPage(cacheKey, true);

                        if (cachedData != null) {
                            final int retryDelay = ConfigUtils.parseInt(myPreferences.getValue(CacheConfigImpl.RETRY_DELAY, null), -1);
                            
                            if (retryDelay > 0) {
                                final boolean persistData = new Boolean(myPreferences.getValue(CacheConfigImpl.PERSIST_CACHE, null)).booleanValue();
                                
                                cachedData.setExpirationDate(new Date(System.currentTimeMillis() + (retryDelay * 1000)));
                                cache.cachePage(cacheKey, cachedData, persistData);
                            }
                            
                            if (LOG.isTraceEnabled())
                                LOG.trace("Using cached content for key '" + cacheKey + "'");

                            response.setContentType(cachedData.getContentType());
                            response.getPortletOutputStream().write(cachedData.getContent());
                            return;
                        }
                    }
                    
                    //If cached content was used this won't be reached, all other
                    //cases an exception needs to be thrown.
                    LOG.warn("Request '" + httpRequest + "' timed out", hte);
                    throw hte;
                    //TODO handle timeout cleanly
                }
                
                //Track last activity time in session
                session.setAttribute(HttpClientConfigImpl.SESSION_TIMEOUT, new Long(System.currentTimeMillis()));

                //Check to see if post-interceptors are used
                final String sPostInterceptor = ConfigUtils.checkEmptyNullString(myPreferences.getValue(GeneralConfigImpl.POST_INTERCEPTOR_CLASS, null), null);
                if (sPostInterceptor != null) {
                    try {
                        final Class postInterceptorClass = Class.forName(sPostInterceptor);
                        PostInterceptor myPostInterceptor = (PostInterceptor)postInterceptorClass.newInstance();
                        myPostInterceptor.intercept(request, response, httpResponse);
                    }
                    catch (ClassNotFoundException cnfe) {
                        final String msg = "Could not find specified post-interceptor class '" + sPostInterceptor + "'";
                        LOG.error(msg, cnfe);
                        throw new PortletException(msg, cnfe);
                    }
                    catch (InstantiationException ie) {
                        final String msg = "Could instatiate specified post-interceptor class '" + sPostInterceptor + "'";
                        LOG.error(msg, ie);
                        throw new PortletException(msg, ie);
                    }
                    catch (IllegalAccessException iae) {
                        final String msg = "Could instatiate specified post-interceptor class '" + sPostInterceptor + "'";
                        LOG.error(msg, iae);
                        throw new PortletException(msg, iae);
                    }
                    catch (ClassCastException cce) {
                        final String msg = "Could not cast '" + sPostInterceptor + "' to 'edu.wisc.my.webproxy.beans.interceptors.PostInterceptor'";
                        LOG.error(msg, cce);
                        throw new PortletException(msg, cce);
                    }
                }
           
                //store the state
                findingService.saveHttpManager(request, httpManager);

                //Check to see if redirected
                final String tempUrl = checkRedirect(sUrl, httpResponse);
                //TODO make sure this works
                if (sUrl.equals(tempUrl))
                    redirect = false;
                else
                    sUrl = tempUrl;
            }
            
            final String realUrl = httpResponse.getRequestUrl();
            

            //check response object for binary content
            String sContentType = httpResponse.getContentType();
            if (sContentType != null) {
                StringTokenizer st = new StringTokenizer(sContentType, ";");
                sContentType = st.nextToken();
            }
            else {
                sContentType = "text/html";
            }
            
            //TODO how do we handle 'unknown'?
            if ("unknown".equals(sContentType))
                sContentType = "text/html";

            final List acceptedContent = (List)context.getBean("ContentTypeBean", List.class);
            boolean matches = false;
            for (Iterator iterateContent = acceptedContent.iterator(); iterateContent.hasNext() && !matches; ) {
                final String sAcceptedContent = (String)iterateContent.next();
                final Pattern contentPattern = Pattern.compile(sAcceptedContent, Pattern.CASE_INSENSITIVE);
                final Matcher contentMatcher = contentPattern.matcher(sContentType);
                
                matches = contentMatcher.matches();
            }

            response.setContentType(sContentType);
            
            //Get InputStream and OutputStream
            InputStream in = null;
            OutputStream out = null;
            try {
                in = httpResponse.getResponseBodyAsStream();
                out = response.getPortletOutputStream();

                if (!matches) {
                    //TODO Display page with direct link to content and back link to previous URL
                }
                else {
                    if (realUrl != null)
                        sUrl = realUrl;
                    
                    session.setAttribute(GeneralConfigImpl.BASE_URL, sUrl);

                	//prepend the back button to the outputstream
                    if (PortletMode.EDIT.equals(mode)) {
                        out.write(createBackButton(response).getBytes());
                    }
                    //Matched a filterable content type, parse and filter stream.
                    if (sUseCache) {
                        final PageCache cache = (PageCache)context.getBean("PageCache", PageCache.class);
    
                        final String cacheKey = generateCacheKey(sUrl, response.getNamespace());

                        final int cacheExprTime = ConfigUtils.parseInt(myPreferences.getValue(CacheConfigImpl.CACHE_TIMEOUT, null), -1);
                        final boolean persistData = new Boolean(myPreferences.getValue(CacheConfigImpl.PERSIST_CACHE, null)).booleanValue();
                        
                        final CacheEntry entryBase = new CacheEntry();
                        entryBase.setContentType(sContentType);
                        
                        if (cacheExprTime >= 0)
                            entryBase.setExpirationDate(new Date(System.currentTimeMillis() + cacheExprTime * 1000));
    
                        out = new CacheOutputStream(out, entryBase, cache, cacheKey, persistData);
                    }
                    //Write out static header data
                    final String sHeader = ConfigUtils.checkEmptyNullString(myPreferences.getValue(StaticHtmlConfigImpl.STATIC_HEADER, null), null);
                    if (sHeader != null) {
                        out.write(sHeader.getBytes());
                    }   
                    final HtmlParser htmlParser = (HtmlParser)context.getBean("HtmlParserBean", HtmlParser.class);
                    final HtmlOutputFilter outFilter = new HtmlOutputFilter(out);
                    try {
                        htmlParser.setRenderData(request, response);
                         
                        //Setup filter chain
                        final List saxFilters = (List)context.getBean("SaxFilterBean", List.class);
                        ChainingSaxFilter parent = null;
                        final Iterator filterItr = saxFilters.iterator();
                        if (filterItr.hasNext()) {
                            parent = (ChainingSaxFilter)filterItr.next();
                            outFilter.setParent(parent);
                            
                            while (filterItr.hasNext()) {
                                final ChainingSaxFilter nextParent = (ChainingSaxFilter)filterItr.next();
                                parent.setParent(nextParent);
                                parent = nextParent;
                            }
                        }
                        
                        //This call should be chained so it only needs to be done on the end filter
                        outFilter.setRenderData(request, response);
    
                        //Get the xmlReader and set a reference to the last filter for Lexical Handling
                        final XMLReader xmlReader = htmlParser.getReader(parent);
                        //Set the parent of the last filter so parsing will work
                        parent.setParent(xmlReader);

                        try {
                            outFilter.parse(new InputSource(in));
                        }
                        catch (SAXException se) {
                            throw new PortletException("A error occured while parsing the content", se);
                        }
                            
                        //Write out static footer data
                        final String sFooter = ConfigUtils.checkEmptyNullString(myPreferences.getValue(StaticHtmlConfigImpl.STATIC_FOOTER, null), null);
                        if (sFooter != null) {
                            out.write(sFooter.getBytes());
                        }                        
                    }
                    finally {
                        htmlParser.clearData();
                        outFilter.clearData();
                    }
                }
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
            if (httpResponse != null)
                httpResponse.close();
        }
    }

    /**
     * Creates new URL for Forms with GET methods
     * @param url
     * @param request
     */
    private String newGetUrl(String url, ActionRequest request) throws IOException {
        StringBuffer newUrl = new StringBuffer(url).append("?");

        for (Enumeration e = request.getParameterNames(); e.hasMoreElements();) {
            final String paramName = (String)e.nextElement();

            if (!paramName.startsWith(WebproxyConstants.UNIQUE_CONSTANT)) {
                final String[] values = request.getParameterValues(paramName);
                for (int valIndex = 0; valIndex < values.length; valIndex++) {
                    newUrl.append(URLEncoder.encode(paramName, "UTF-8")).append("=").append(URLEncoder.encode(values[valIndex], "UTF-8"));

                    if ((valIndex + 1) != values.length || e.hasMoreElements()) {
                        newUrl.append("&");
                    }
                }
            }
        }

        return newUrl.toString();
    }



    /**
     * @param portletRequest
     * @param httpRequest
     */
    private void doHttpAuth(final PortletRequest portletRequest, HttpManager manager) {
        final PortletPreferences myPreferences = new PortletPreferencesWrapper(portletRequest.getPreferences(), (Map)portletRequest.getAttribute(PortletRequest.USER_INFO));

        final boolean authEnabled = new Boolean(myPreferences.getValue(HttpClientConfigImpl.AUTH_ENABLE, null)).booleanValue();
        final String authType = ConfigUtils.checkEmptyNullString(myPreferences.getValue(HttpClientConfigImpl.AUTH_TYPE, ""), "");

        if (authEnabled && HttpClientConfigImpl.AUTH_TYPE_BASIC.equals(authType) || HttpClientConfigImpl.AUTH_TYPE_NTLM.equals(authType)) {
            final PortletSession session = portletRequest.getPortletSession();

            String userName = (String)session.getAttribute(HttpClientConfigImpl.USER_NAME);
            if (userName == null)
                userName = myPreferences.getValue(HttpClientConfigImpl.USER_NAME, "");

            String password = (String)session.getAttribute(HttpClientConfigImpl.PASSWORD);
            if (password == null)
                password = myPreferences.getValue(HttpClientConfigImpl.PASSWORD, "");

            final Credentials creds;
            if (HttpClientConfigImpl.AUTH_TYPE_BASIC.equals(authType)) {
                creds = new UsernamePasswordCredentials(userName, password);
            }
            else {
                String domain = (String)session.getAttribute(HttpClientConfigImpl.DOMAIN);
                if (domain == null)
                    domain = myPreferences.getValue(HttpClientConfigImpl.DOMAIN, "");

                final String host = portletRequest.getProperty("REMOTE_HOST");

                creds = new NTCredentials(userName, password, domain, host);
            }

            manager.setCredentials(creds);
        }
    }

    private String doFormAuth(final HttpManager httpManager, PortletRequest request) throws PortletException, IOException {
        final PortletSession session = request.getPortletSession();
        final PortletPreferences prefs = new PortletPreferencesWrapper(request.getPreferences(), (Map)request.getAttribute(PortletRequest.USER_INFO));

        final boolean authEnabled = new Boolean(prefs.getValue(HttpClientConfigImpl.AUTH_ENABLE, null)).booleanValue();
        final String authType = ConfigUtils.checkEmptyNullString(prefs.getValue(HttpClientConfigImpl.AUTH_TYPE, ""), "");

        final String sessionTimeoutStr = prefs.getValue(HttpClientConfigImpl.SESSION_TIMEOUT, null);
        long sessionTimeout = 25; //25 Minute default
        try {
            sessionTimeout = Long.parseLong(sessionTimeoutStr);
        }
        catch (NumberFormatException nfe) {
            //Ignore NFE, sessionTimeout has a default value
        }
        //Convert Minutes to Milliseconds
        sessionTimeout *= 60000;

        final boolean sessionExpired;

        final Long lastActivity = (Long)session.getAttribute(HttpClientConfigImpl.SESSION_TIMEOUT);
        if (lastActivity == null || (lastActivity.longValue() + sessionTimeout) <= System.currentTimeMillis()) {
            sessionExpired = true;
        }
        else {
            sessionExpired = false;
        }

        if (authEnabled && sessionExpired && HttpClientConfigImpl.AUTH_TYPE_FORM.equals(authType)) {
            //get all loginAttributes for Posting
            final String[] sStaticParameterNames = prefs.getValues(HttpClientConfigImpl.STATIC_PARAM_NAMES, new String[0]);
            final String[] sStaticParameterValues = prefs.getValues(HttpClientConfigImpl.STATIC_PARAM_VALUES, new String[0]);

            final String[] sDynamicParameterNames = prefs.getValues(HttpClientConfigImpl.DYNAMIC_PARAM_NAMES, new String[0]);
            final String[] sDynamicParameterValues = new String[sDynamicParameterNames.length];

            //get and set additional dynamic post paramters
            final String[] postAttributes = (String[])session.getAttribute(HttpClientConfigImpl.DYNAMIC_PARAM_VALUES);
            if (postAttributes != null) {
                for (int index = 0; index < postAttributes.length && index < sDynamicParameterValues.length; index++) {
                    sDynamicParameterValues[index] = postAttributes[index];
                }
            }

            final List<ParameterPair> sLoginAttributes = new ArrayList<ParameterPair>(sDynamicParameterNames.length + sStaticParameterNames.length);

            for (int dynamicIndex = 0; dynamicIndex < sDynamicParameterNames.length; dynamicIndex++) {
                final String value;
                if (dynamicIndex < sDynamicParameterValues.length)
                    value = ConfigUtils.checkEmptyNullString(sDynamicParameterValues[dynamicIndex], "");
                else
                    value = "";

                final ParameterPair pair = new ParameterPair(sDynamicParameterNames[dynamicIndex], value);
                sLoginAttributes.add(pair);
            }
            for (int staticIndex = 0; staticIndex < sStaticParameterNames.length; staticIndex++) {
                final String value;
                if (staticIndex < sStaticParameterValues.length)
                    value = ConfigUtils.checkEmptyNullString(sStaticParameterValues[staticIndex], "");
                else
                    value = "";

                final ParameterPair pair = new ParameterPair(sStaticParameterNames[staticIndex], value);
                sLoginAttributes.add(pair);
            }

            //create Post object
            final Request authPost = httpManager.createRequest();

            authPost.setType(WebproxyConstants.POST_REQUEST);

            final String authUrl = ConfigUtils.checkEmptyNullString(prefs.getValue(HttpClientConfigImpl.AUTH_URL, ""), "");
            authPost.setUrl(authUrl);

            authPost.setParameters(sLoginAttributes.toArray(new ParameterPair[sLoginAttributes.size()]));

            final Response authResponse = httpManager.doRequest(authPost);
            session.setAttribute(HttpClientConfigImpl.SESSION_TIMEOUT, new Long(System.currentTimeMillis()));

            //Check for redirect
            final String redirUrl = checkRedirect(authUrl, authResponse);
            //Get new State
            // TODO: save state

            //close response
            authResponse.close();

            if (!authUrl.equals(redirUrl))
                return redirUrl;
            else
                return null;
        }

        return null;
    }

    public static String generateCacheKey(String pageUrl, String namespace) {
        final StringBuffer cacheKeyBuf = new StringBuffer();

        cacheKeyBuf.append(namespace);
        cacheKeyBuf.append(".");
        cacheKeyBuf.append(pageUrl);

        return cacheKeyBuf.toString();
    }
    
    public static String generateStateKey(String key, String namespace) {
        final StringBuffer cacheKeyBuf = new StringBuffer();
        
        cacheKeyBuf.append(namespace);
        cacheKeyBuf.append(".");
        cacheKeyBuf.append(key);

        return cacheKeyBuf.toString();
    }

    private boolean manualLogin(RenderRequest request, RenderResponse response) throws PortletException, IOException {
        PortletSession session = request.getPortletSession();
        PortletPreferences myPreferences = request.getPreferences();

        final boolean authEnabled = new Boolean(myPreferences.getValue(HttpClientConfigImpl.AUTH_ENABLE, null)).booleanValue();

        if (authEnabled) {
            final String authType = myPreferences.getValue(HttpClientConfigImpl.AUTH_TYPE, null);

            if (HttpClientConfigImpl.AUTH_TYPE_BASIC.equals(authType)) {
                final boolean userNamePrompt = new Boolean(myPreferences.getValue(HttpClientConfigImpl.PROMPT_USER_NAME, null)).booleanValue();
                final boolean passwordPrompt = new Boolean(myPreferences.getValue(HttpClientConfigImpl.PROMPT_PASSWORD, null)).booleanValue();
                
                String userName = (String)session.getAttribute(HttpClientConfigImpl.USER_NAME);
                if (userName == null)
                    userName = myPreferences.getValue(HttpClientConfigImpl.USER_NAME, null);
                
                String password = (String)session.getAttribute(HttpClientConfigImpl.PASSWORD);
                if (password == null)
                    password = myPreferences.getValue(HttpClientConfigImpl.PASSWORD, null);
                
                userName = ConfigUtils.checkEmptyNullString(userName, null);
                password = ConfigUtils.checkEmptyNullString(password, null);
                
                if ((userNamePrompt && userName==null) || (passwordPrompt && password==null)) {
                    PortletRequestDispatcher manualLoginDispatch = getPortletContext().getRequestDispatcher(MANUAL);
                    manualLoginDispatch.include(request, response);
                    return true;
                }
            }
            else if (HttpClientConfigImpl.AUTH_TYPE_FORM.equals(authType)) {
                final String[] dynamicParamNames = myPreferences.getValues(HttpClientConfigImpl.DYNAMIC_PARAM_NAMES, new String[0]);

                final String[] dynamicParamValues = myPreferences.getValues(HttpClientConfigImpl.DYNAMIC_PARAM_VALUES, new String[dynamicParamNames.length]);
                final String[] sessionDynamicParamValues = (String[])session.getAttribute(HttpClientConfigImpl.DYNAMIC_PARAM_VALUES);
                boolean emptyValue = false;
                for (int index = 0; index < dynamicParamValues.length; index++) {
                    if (dynamicParamValues[index] == null && sessionDynamicParamValues != null && index < sessionDynamicParamValues.length)
                        dynamicParamValues[index] = sessionDynamicParamValues[index];

                    if (dynamicParamValues[index] == null)
                        emptyValue = true;
                }

                session.setAttribute(HttpClientConfigImpl.DYNAMIC_PARAM_VALUES, dynamicParamValues);

                if (emptyValue) {
                    PortletRequestDispatcher manualLoginDispatch = getPortletContext().getRequestDispatcher(MANUAL);
                    manualLoginDispatch.include(request, response);
                    return true;
                }
            }
            else {
                throw new IllegalArgumentException("Unknown authType specified '" + authType + "'");
            }
        }

        return false;
    }

    public void processAction(final ActionRequest request, final ActionResponse response) throws PortletException, IOException {
        final PortletMode mode = request.getPortletMode();
        final WindowState windowState = request.getWindowState();
        if (LOG.isDebugEnabled()) {
            LOG.debug("Processing action with PortletMode='" + mode + "' and WindowState='" + windowState + "'");
        }
        
        final PortletSession session = request.getPortletSession();
        final Map userInfo = (Map)request.getAttribute(PortletRequest.USER_INFO);
        final PortletPreferences pp = new PortletPreferencesWrapper(request.getPreferences(), userInfo);

        final String manualAuthSubmit = request.getParameter("AUTH_CREDS");
        if (manualAuthSubmit != null) {
            this.processManualAuthForm(request);
        }
        //if Back to application button is selected, set PortletMode to 'VIEW'
        else if(request.getPortletMode().equals(PortletMode.EDIT)){
            //TODO review EDIT mode code
            if(ConfigUtils.checkEmptyNullString(request.getParameter(WebproxyConstants.BACK_BUTTON), null)!=null){
                response.setPortletMode(PortletMode.VIEW);
            }
        }
        else {
            //Gets a reference to the ApplicationContext created by the
            //ContextLoaderListener which is configured in the web.xml for
            //this portlet
            final ApplicationContext context = PortletApplicationContextUtils.getWebApplicationContext(this.getPortletContext());
            ApplicationContextLocator.setApplicationContext(context);

            if (request.getPortletMode().equals(WebproxyConstants.CONFIG_MODE)) {
                this.processConfigAction(request, response);
                return;
            }
            
            HttpManagerService findingService = (HttpManagerService) context.getBean("HttpManagerService", HttpManagerService.class);
            final HttpManager httpManager = findingService.findManager(request);
            httpManager.setActionData(request, response);



            if (request.getPortletMode().equals(PortletMode.EDIT)) {
                String sUrl = request.getParameter(GeneralConfigImpl.EDIT_URL);
                session.setAttribute(GeneralConfigImpl.EDIT_URL, sUrl);
                response.setPortletMode(PortletMode.VIEW);         
                return;
            }

            //retrieve URL from request object
            String sUrl = request.getParameter(WebproxyConstants.BASE_URL);
            
            //TODO do cache check for content type

            String sRequestType = request.getProperty("REQUEST_METHOD");
            
            if(request.getParameter(WebproxyConstants.UNIQUE_CONSTANT + ".getMethod")!=null){
                sRequestType=WebproxyConstants.GET_REQUEST;
                sUrl = newGetUrl(sUrl, request);
            }
            
            this.doFormAuth(httpManager, request);

            String sContentType = null;

            Response httpResponse = null;
            try {
                boolean redirect = true;
                final int maxRedirects = ConfigUtils.parseInt(pp.getValue(HttpClientConfigImpl.MAX_REDIRECTS, null), 5);
                
                for (int index = 0; index < maxRedirects && redirect; index++) {
                    this.doHttpAuth(request, httpManager);

                    //create request object
                    final Request httpRequest = httpManager.createRequest();

                    //set URL in request
                    httpRequest.setUrl(sUrl);

                    //Set Type to HEAD
                    httpRequest.setType(WebproxyConstants.HEAD_REQUEST);
 
                    final String[] headerNames = pp.getValues(HttpHeaderConfigImpl.HEADER_NAME, new String[0]);
                    final String[] headerValues = pp.getValues(HttpHeaderConfigImpl.HEADER_VALUE, new String[0]);
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
                
                    //Check to see if pre-interceptors are used.
                    final String sPreInterceptor = ConfigUtils.checkEmptyNullString(pp.getValue(GeneralConfigImpl.PRE_INTERCEPTOR_CLASS, null), null);
                    if (sPreInterceptor != null) {
                        try {
                            final Class preInterceptorClass = Class.forName(sPreInterceptor);
                            PreInterceptor myPreInterceptor = (PreInterceptor)preInterceptorClass.newInstance();
                            myPreInterceptor.intercept(request, response, httpRequest);
                        }
                        catch (ClassNotFoundException cnfe) {
                            final String msg = "Could not find specified pre-interceptor class '" + sPreInterceptor + "'";
                            LOG.error(msg, cnfe);
                            throw new PortletException(msg, cnfe);
                        }
                        catch (InstantiationException ie) {
                            final String msg = "Could instatiate specified pre-interceptor class '" + sPreInterceptor + "'";
                            LOG.error(msg, ie);
                            throw new PortletException(msg, ie);
                        }
                        catch (IllegalAccessException iae) {
                            final String msg = "Could instatiate specified pre-interceptor class '" + sPreInterceptor + "'";
                            LOG.error(msg, iae);
                            throw new PortletException(msg, iae);
                        }
                        catch (ClassCastException cce) {
                            final String msg = "Could not cast '" + sPreInterceptor + "' to 'edu.wisc.my.webproxy.beans.interceptors.PreInterceptor'";
                            LOG.error(msg, cce);
                            throw new PortletException(msg, cce);
                        }
                    }
                    
                    //send httpRequest
                    httpResponse = httpManager.doRequest(httpRequest);
                    
                    session.setAttribute(HttpClientConfigImpl.SESSION_TIMEOUT, new Long(System.currentTimeMillis()));
                    
                    //Check to see if post-interceptors are used
                    final String sPostInterceptor = ConfigUtils.checkEmptyNullString(pp.getValue(GeneralConfigImpl.POST_INTERCEPTOR_CLASS, null), null);
                    if (sPostInterceptor != null) {
                        try {
                            final Class postInterceptorClass = Class.forName(sPostInterceptor);
                            PostInterceptor myPostInterceptor = (PostInterceptor)postInterceptorClass.newInstance();
                            myPostInterceptor.intercept(request, response, httpResponse);
                        }
                        catch (ClassNotFoundException cnfe) {
                            final String msg = "Could not find specified post-interceptor class '" + sPostInterceptor + "'";
                            LOG.error(msg, cnfe);
                            throw new PortletException(msg, cnfe);
                        }
                        catch (InstantiationException ie) {
                            final String msg = "Could instatiate specified post-interceptor class '" + sPostInterceptor + "'";
                            LOG.error(msg, ie);
                            throw new PortletException(msg, ie);
                        }
                        catch (IllegalAccessException iae) {
                            final String msg = "Could instatiate specified post-interceptor class '" + sPostInterceptor + "'";
                            LOG.error(msg, iae);
                            throw new PortletException(msg, iae);
                        }
                        catch (ClassCastException cce) {
                            final String msg = "Could not cast '" + sPostInterceptor + "' to 'edu.wisc.my.webproxy.beans.interceptors.PostInterceptor'";
                            LOG.error(msg, cce);
                            throw new PortletException(msg, cce);
                        }
                    }

                    findingService.saveHttpManager(request, httpManager);

                    final String tempUrl = checkRedirect(sUrl, httpResponse);
                    //if not redirect, set redirect to false to break from while
                    if (tempUrl.equals(sUrl))
                        redirect = false;
                }

                //check response object for binary content
                if (httpResponse.getContentType() != null) {
                    StringTokenizer st = new StringTokenizer(httpResponse.getContentType(), ";");
                    sContentType = st.nextToken();
                }
            }
            finally {
                if (httpResponse != null)
                    httpResponse.close();
            }
            
            boolean matches = false;
            if (sContentType != null) {
                final List acceptedContent = (List)context.getBean("ContentTypeBean", List.class);
    
                String sAcceptedContent = null;
                Iterator iterateContent = acceptedContent.iterator();
                while (iterateContent.hasNext() && !matches) {
                    sAcceptedContent = (String)iterateContent.next();
                    Pattern contentPattern = Pattern.compile(sAcceptedContent, Pattern.CASE_INSENSITIVE);
                    Matcher contentMatcher = contentPattern.matcher(sContentType);
                    if (contentMatcher.matches())
                        matches = true;
                }
            }

            if (!matches) {
                final int protocolEnd = sUrl.indexOf("//");
                final int queryStringStart = sUrl.indexOf("?");
                final int fileBaseStart = (protocolEnd < 0 ? 0 : protocolEnd + 2); //Add 2 to exclude the protocol seperator
                final int fileBaseEnd = (queryStringStart < 0 ? sUrl.length() : queryStringStart);
                final String fileBase = sUrl.substring(fileBaseStart, fileBaseEnd);
                
                final StringBuffer servletUrl = new StringBuffer();
                servletUrl.append(request.getContextPath());
                servletUrl.append("/ProxyServlet/"); //TODO make this an init parameter
                servletUrl.append(fileBase);
                servletUrl.append("?");
                servletUrl.append(URLEncoder.encode(ProxyServlet.URL_PARAM, "UTF-8"));
                servletUrl.append("=");
                servletUrl.append(URLEncoder.encode(sUrl, "UTF-8"));
                
                servletUrl.append("&");
                servletUrl.append(URLEncoder.encode(ProxyServlet.SESSION_ID_PARAM, "UTF-8"));
                servletUrl.append("=");
                servletUrl.append(URLEncoder.encode(session.getId(), "UTF-8"));
                
                final Object namespaceTestObj = new Object();
                final String NAMESPACE_TEST_NAME = "NAMESPACE_TEST";
                session.setAttribute(NAMESPACE_TEST_NAME, namespaceTestObj);
                for (Enumeration nameEnum = session.getAttributeNames(PortletSession.APPLICATION_SCOPE); nameEnum.hasMoreElements(); ) {
                    final String name = (String)nameEnum.nextElement();
                    final Object value = session.getAttribute(name, PortletSession.APPLICATION_SCOPE);
                    
                    if (value.equals(namespaceTestObj)) {
                        final String prefix;
                        final String sufix;
                        
                        final int index = name.indexOf(NAMESPACE_TEST_NAME);
                        if (index >= 0) {
                            prefix = name.substring(0, index);
                            sufix = name.substring(index + NAMESPACE_TEST_NAME.length());
                        }
                        else {
                            prefix = "";
                            sufix = "";
                        }
                        
                        servletUrl.append("&");
                        servletUrl.append(URLEncoder.encode(ProxyServlet.NAMESPACE_PREFIX_PARAM, "UTF-8"));
                        servletUrl.append("=");
                        servletUrl.append(URLEncoder.encode(prefix, "UTF-8"));
                        
                        servletUrl.append("&");
                        servletUrl.append(URLEncoder.encode(ProxyServlet.NAMESPACE_SUFIX_PARAM, "UTF-8"));
                        servletUrl.append("=");
                        servletUrl.append(URLEncoder.encode(sufix, "UTF-8"));
                    }
                }
                
                session.setAttribute(PortletPreferences.class.getName(), request.getPreferences());
                
                //Make sure the session is in the shared map
                if (session instanceof HttpSession && SessionMappingListener.getSession(session.getId()) == null) {
                    SessionMappingListener.setSession((HttpSession)session);
                }
                
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Redirecting request to '" + servletUrl + "'");
                }

                response.sendRedirect(servletUrl.toString());
                return;
            }
            else {
                final Map params = request.getParameterMap();
                if (params != null){
                    response.setRenderParameters(params);
                }
                response.setRenderParameter(WebproxyConstants.REQUEST_TYPE, sRequestType);
                response.setRenderParameter(GeneralConfigImpl.BASE_URL, sUrl);
                session.setAttribute(GeneralConfigImpl.BASE_URL, sUrl);
            }
        }
    }

    /**
     * @param request
     * @param response
     * @throws ReadOnlyException
     */
    private void processConfigAction(final ActionRequest request, final ActionResponse response) throws ReadOnlyException {
        final PortletSession session = request.getPortletSession();
        final Map userInfo = (Map)request.getAttribute(PortletRequest.USER_INFO);
        final PortletPreferences pp = new PortletPreferencesWrapper(request.getPreferences(), userInfo);

                 if (request.getParameter("configPlacer") != null) {
                    try {
                        session.setAttribute("configPlacer", new Integer(request.getParameter("configPlacer")));
                    }
                    catch (NumberFormatException e) {
                        LOG.error("Caught NumberFormatException when retrieving configuration page placer", e);
                    }
                }
                else {
                    ConfigPage tempConfig = getConfig(session);
                    Integer configPlacer = (Integer)session.getAttribute("configPlacer");
                    boolean error = false;
                    try {
                        tempConfig.process(request, response);
                    }
                    catch (Exception e) {
                        LOG.error(new StringBuffer("Caught RuntimeException when calling action on ").append(tempConfig.getName()).toString(), e);
                        response.setRenderParameter("msg", e.getMessage());
                        error = true;
                    }
                    String sPrevious = request.getParameter("previous");
                    String sNext = request.getParameter("next");
                    if (sNext != null) {
                        // user has clicked on next
                        if(!error){
                            configPlacer = new Integer(configPlacer.intValue() + 1);
                        }
                        session.setAttribute("configPlacer", configPlacer);
                    }
                    else if (sPrevious != null) {
                        // user has clicked on back1
                        if(!error){
                            configPlacer = new Integer(configPlacer.intValue() - 1);
                        }
                        session.setAttribute("configPlacer", configPlacer);
                    }
                    else {
                        response.setRenderParameter("msg", "Thank you for submitting the parameters.");
                        pp.reset("configPlacer");
                        session.removeAttribute("configList");
                    }
                }

    }

    /**
     * @param request
     * @throws ReadOnlyException
     * @throws IOException
     * @throws ValidatorException
     */
    private void processManualAuthForm(final ActionRequest request) throws ReadOnlyException, IOException, ValidatorException {
        final PortletSession session = request.getPortletSession();
        final Map userInfo = (Map)request.getAttribute(PortletRequest.USER_INFO);
        final PortletPreferences pp = new PortletPreferencesWrapper(request.getPreferences(), userInfo);

        final String authType = pp.getValue(HttpClientConfigImpl.AUTH_TYPE, null);

        if (HttpClientConfigImpl.AUTH_TYPE_BASIC.equals(authType)) {
            final String userName = ConfigUtils.checkEmptyNullString(request.getParameter(HttpClientConfigImpl.USER_NAME), "");
            final String password = ConfigUtils.checkEmptyNullString(request.getParameter(HttpClientConfigImpl.PASSWORD), "");

            if (userName.length() > 0) {
                session.setAttribute(HttpClientConfigImpl.USER_NAME, userName);

                final boolean userNamePersist = new Boolean(pp.getValue(HttpClientConfigImpl.PERSIST_USER_NAME, null)).booleanValue();
                if (userNamePersist)
                    pp.setValue(HttpClientConfigImpl.USER_NAME, userName);
            }
            if (password.length() > 0) {
                session.setAttribute(HttpClientConfigImpl.PASSWORD, password);

                final boolean passwordPersist = new Boolean(pp.getValue(HttpClientConfigImpl.PERSIST_PASSWORD, null)).booleanValue();
                if (passwordPersist)
                    pp.setValue(HttpClientConfigImpl.PASSWORD, password);
            }

            pp.store();
        }
        else if (HttpClientConfigImpl.AUTH_TYPE_FORM.equals(authType)) {
            final String[] dynamicParamNames = pp.getValues(HttpClientConfigImpl.DYNAMIC_PARAM_NAMES, new String[0]);
            String[] dynamicParamValues = ConfigUtils.checkNullStringArray(request.getParameterValues(HttpClientConfigImpl.DYNAMIC_PARAM_VALUES),
                                                                           new String[0]);

            if (dynamicParamValues.length == dynamicParamNames.length) {
                dynamicParamValues = ConfigUtils.checkArrayForNulls(dynamicParamValues, "");

                session.setAttribute(HttpClientConfigImpl.DYNAMIC_PARAM_VALUES, dynamicParamValues);

                final String[] persistedParamValues = new String[dynamicParamValues.length];
                final String[] dynamicParamPersist = pp.getValues(HttpClientConfigImpl.DYNAMIC_PARAM_PERSIST, new String[0]);
                for (int index = 0; index < dynamicParamPersist.length; index++) {
                    try {
                        final int paramIndex = Integer.parseInt(dynamicParamPersist[index]);
                        persistedParamValues[paramIndex] = dynamicParamValues[paramIndex];
                    }
                    catch (NumberFormatException nfe) {
                    }
                }
                pp.setValues(HttpClientConfigImpl.DYNAMIC_PARAM_VALUES, persistedParamValues);
                pp.store();
            }
            else {
                LOG.warn("Invalid data submitted during manual authentication prompt. dynamicParamNames.length='" + dynamicParamNames.length
                        + "' != dynamicParamValues.length='" + dynamicParamValues.length + "'");
            }
        }
        else {
            throw new IllegalArgumentException("Unknown authType specified '" + authType + "'");
        }
    }

    public static String checkRedirect(String sUrl, Response httpResponse) {
        StringBuffer myUrl = new StringBuffer(sUrl);
        int statusCode = httpResponse.getStatusCode();

        if ((statusCode == Response.SC_MOVED_TEMPORARILY) || (statusCode == Response.SC_MOVED_PERMANENTLY) || (statusCode == Response.SC_SEE_OTHER)
                || (statusCode == Response.SC_TEMPORARY_REDIRECT)) {
            final IHeader[] headers = httpResponse.getHeaders();

            for (int index = 0; index < headers.length; index++) {
                if ("location".equalsIgnoreCase(headers[index].getName())) {
                    final String location = headers[index].getValue();

                    //if location is null or blank, URL remains unchanged
                    if ((location == null) || (location.equals("")))
                        myUrl.append("/");
                    //check to see if redirected to absolute URL
                    else if (location.toUpperCase().startsWith("HTTP"))
                        myUrl = new StringBuffer(location);
                    //append location to old URL
                    else
                        myUrl.append(location);

                    break;
                }
            }
        }

        return myUrl.toString();
    }

    private ConfigPage getConfig(PortletSession session) {

        final ApplicationContext context = PortletApplicationContextUtils.getWebApplicationContext(this.getPortletContext());

        final List configurationList = (List)context.getBean("ConfigBean", List.class);

        session.setAttribute("configList", configurationList);

        ConfigPage currentConfig = null;

        Integer configPlacer = null;

        //retrieve doAction Parameter
        configPlacer = (Integer)session.getAttribute("configPlacer");

        if (configPlacer == null)
            configPlacer = new Integer(0);

        currentConfig = (ConfigPage)configurationList.get(configPlacer.intValue());

        session.setAttribute("configPlacer", configPlacer);

        return currentConfig;
    }

    //create Back Button to leave editMode
    private String createBackButton(RenderResponse response){
        StringBuffer backButton = new StringBuffer("<br><form name=\"back\" action=\"").append(response.createActionURL()).append(">\" method=\"post\"><input type=\"submit\" name=\"").append(WebproxyConstants.BACK_BUTTON).append("\" value=\"Back to Application\"></form>");
        return backButton.toString();
    }
    
}