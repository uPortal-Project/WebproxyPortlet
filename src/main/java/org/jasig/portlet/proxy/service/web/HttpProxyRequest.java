package org.jasig.portlet.proxy.service.web;

public class HttpProxyRequest implements ProxyRequest {

    private String proxiedUrl;

    public String getProxiedUrl() {
        return proxiedUrl;
    }

    public void setProxiedUrl(String proxiedUrl) {
        this.proxiedUrl = proxiedUrl;
    }

}
