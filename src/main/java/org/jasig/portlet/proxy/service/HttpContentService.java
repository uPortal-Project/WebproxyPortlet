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
package org.jasig.portlet.proxy.service;

import java.io.IOException;
import java.io.InputStream;

import javax.portlet.PortletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.springframework.stereotype.Service;

/**
 * @author Jen Bourey, jennifer.bourey@gmail.com
 */
@Service("httpContentService")
public class HttpContentService implements IContentService {

    private final Log log = LogFactory.getLog(getClass());
    
    public InputStream getContent(String url, PortletRequest request) {

        final HttpClient httpclient = new DefaultHttpClient();

        // TODO: authentication, parameterizable URLs
        
        final HttpGet httpget = new HttpGet(url);

        try {
            final HttpResponse response = httpclient.execute(httpget);
            final HttpEntity entity = response.getEntity();
            return entity.getContent();
        } catch (ClientProtocolException e) {
            log.error("Exception retrieving remote content", e);
        } catch (IOException e) {
            log.error("Exception retrieving remote content", e);
        }

        return null;
    }

}
