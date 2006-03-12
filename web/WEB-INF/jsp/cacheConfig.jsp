<%@ page 
	import="javax.portlet.*, 
	        edu.wisc.my.webproxy.beans.config.*"%>
<%@ taglib uri='/WEB-INF/tld/portlet.tld' prefix='portlet'%>

<portlet:defineObjects/>

<%
    final PortletPreferences prefs = renderRequest.getPreferences(); 
    
    final boolean useCache     = new Boolean(prefs.getValue(CacheConfigImpl.USE_CACHE, null)).booleanValue();
    final boolean useExpired   = new Boolean(prefs.getValue(CacheConfigImpl.USE_EXPIRED, null)).booleanValue();
    final boolean persistCache = new Boolean(prefs.getValue(CacheConfigImpl.PERSIST_CACHE, null)).booleanValue();

    final String cacheTimeout = prefs.getValue(CacheConfigImpl.CACHE_TIMEOUT, "");
    final String retryDelay   = prefs.getValue(CacheConfigImpl.RETRY_DELAY, "");
    final String cacheScope   = prefs.getValue(CacheConfigImpl.CACHE_SCOPE, "");
    
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
<p>
    Cache Scope
    <select name="<%=CacheConfigImpl.CACHE_SCOPE%>">
      <option value="<%=CacheConfigImpl.CACHE_SCOPE_USER%>" <%=(CacheConfigImpl.CACHE_SCOPE_USER.equals(cacheScope) ? "selected=\"selected\"" : "")%>>User</option>
      <option value="<%=CacheConfigImpl.CACHE_SCOPE_INSTANCE%>" <%=(CacheConfigImpl.CACHE_SCOPE_INSTANCE.equals(cacheScope) ? "selected=\"selected\"" : "")%>>Instance</option>
    </select>
    (User - cached data is scoped to the user of the instance, not shared at all)
    (Instance - cached data is scoped to the instance, shared between users)
</p>