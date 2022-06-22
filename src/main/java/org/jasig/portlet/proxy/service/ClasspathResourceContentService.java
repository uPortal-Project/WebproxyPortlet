/**
 * Licensed to Apereo under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Apereo licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License.  You may obtain a
 * copy of the License at the following location:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jasig.portlet.proxy.service;

import java.io.IOException;

import javax.portlet.PortletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

/**
 * ClasspathResourceContentService retrieves content from a document in the
 * portlet classpath.
 *
 * @author Jen Bourey, jennifer.bourey@gmail.com
 * @version $Id: $Id
 */
@Service("classpathContentService")
public class ClasspathResourceContentService implements IContentService<GenericContentRequestImpl, GenericContentResponseImpl> {
    
    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    /** {@inheritDoc} */
    @Override
    public GenericContentRequestImpl getRequest(final PortletRequest request) {
    	return new GenericContentRequestImpl(request);
    }

    /** {@inheritDoc} */
    @Override
    public GenericContentResponseImpl getContent(final GenericContentRequestImpl proxyRequest, final PortletRequest request) {
        
    	// get the resource corresponding to the configured location
        final Resource resource = new ClassPathResource(proxyRequest.getProxiedLocation());
        
        try {
        	// construct a content response using this resource
            final GenericContentResponseImpl proxyResponse = new GenericContentResponseImpl(proxyRequest.getProxiedLocation(), resource.getInputStream());
            return proxyResponse;
        } catch (IOException e) {
            log.error("IOException retrieving resource {}", proxyRequest.getProxiedLocation());
        }
        
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public void beforeGetContent(GenericContentRequestImpl contentRequest, PortletRequest request) {
    }

    /** {@inheritDoc} */
    @Override
    public void afterGetContent(GenericContentRequestImpl contentRequest, PortletRequest request, GenericContentResponseImpl proxyResponse) {
    }

}
