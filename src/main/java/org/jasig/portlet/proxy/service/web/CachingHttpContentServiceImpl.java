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

package org.jasig.portlet.proxy.service.web;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;
import org.apache.commons.io.IOUtils;
import org.jasig.portlet.proxy.service.GenericContentResponseImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.portlet.PortletRequest;
import java.io.ByteArrayInputStream;
import java.io.IOException;

@Service("httpContentService")
public class CachingHttpContentServiceImpl extends HttpContentServiceImpl {
    private static final Logger LOG = LoggerFactory.getLogger(CachingHttpContentServiceImpl.class);

    private Cache cache;


    public CachingHttpContentServiceImpl() {
    }

    @Required
    @Resource(name="urlCache")
    public void setCache(Cache cache) {
        this.cache = cache;
    }

    @Override
    public GenericContentResponseImpl getContent(HttpContentRequestImpl proxyRequest, PortletRequest request) {
        GenericContentResponseImpl response = null;
        super.beforeGetContent(proxyRequest, request);
        String cacheKey = proxyRequest.getProxiedLocation();
        Element cachedElement = cache.get(cacheKey);
        if (cachedElement == null) {
            LOG.debug("Cache miss for cacheKey: {}", cacheKey);
            
            response = super.getContent(proxyRequest, request, false);
            try {
                response.setContent(new ByteArrayInputStream(IOUtils.toByteArray(response.getContent())));
                cachedElement = new Element(cacheKey, response);
                cache.put(cachedElement);
            } catch(IOException ioexception) {
                LOG.error("Exception retrieving remote content", ioexception);
            }
        } else {
            LOG.debug("Cache hit for cacheKey: {}" , cacheKey);
            response = (GenericContentResponseImpl)cachedElement.getValue();
            try {
                response.getContent().reset();
            } catch(IOException ioException) {
                LOG.error("Error retrieving cached page. Resending request...");
                cache.remove(cacheKey);
                return this.getContent(proxyRequest, request);
            }
        }

        return response;
    }
}
