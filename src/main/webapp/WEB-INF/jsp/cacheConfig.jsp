<%--

    Licensed to Jasig under one or more contributor license
    agreements. See the NOTICE file distributed with this work
    for additional information regarding copyright ownership.
    Jasig licenses this file to you under the Apache License,
    Version 2.0 (the "License"); you may not use this file
    except in compliance with the License. You may obtain a
    copy of the License at:

    http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing,
    software distributed under the License is distributed on
    an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
    KIND, either express or implied. See the License for the
    specific language governing permissions and limitations
    under the License.

--%>

<%@ page 
	import="javax.portlet.*, 
	        edu.wisc.my.webproxy.beans.config.*"%>
<%@ include file="/WEB-INF/jsp/include.jsp" %>

<%
    final PortletPreferences prefs = renderRequest.getPreferences(); 
    
    final boolean useCache     = new Boolean(prefs.getValue(CacheConfigImpl.USE_CACHE, null)).booleanValue();
    final boolean useExpired   = new Boolean(prefs.getValue(CacheConfigImpl.USE_EXPIRED, null)).booleanValue();
    final boolean persistCache = new Boolean(prefs.getValue(CacheConfigImpl.PERSIST_CACHE, null)).booleanValue();

    final String cacheTimeout = prefs.getValue(CacheConfigImpl.CACHE_TIMEOUT, "");
    final String retryDelay   = prefs.getValue(CacheConfigImpl.RETRY_DELAY, "");
    
%>
<p>
    <input type="checkbox" name="<%=CacheConfigImpl.USE_CACHE%>" value="true" <%=(useCache ? "checked=\"checked\"" : "")%>/> Use Cache
</p>
<p>
    Cache timeout 
    <input name="<%=CacheConfigImpl.CACHE_TIMEOUT%>" type="text" value="<%=cacheTimeout%>" size="10"/> (seconds) (-1 = never expires)
</p>
<p>
    <input type="checkbox" name="<%=CacheConfigImpl.USE_EXPIRED%>" value="true" <%=(useExpired ? "checked=\"checked\"" : "")%>/> 
    Use expired data if the remote server is not responding.
</p>
<p>
    If using expired data how long should web proxy wait before trying to
    contact the non-responding server 
    <input name="<%=CacheConfigImpl.RETRY_DELAY%>" type="text" value="<%=retryDelay%>" size="10"/> (seconds) 
    (-1 = no delay)
</p>
<p>
    <input type="checkbox" name="<%=CacheConfigImpl.PERSIST_CACHE%>" value="true" <%=(persistCache ? "checked=\"checked\"" : "")%>/> 
    Persist cached  (will remain in cache between restarts)
</p>
