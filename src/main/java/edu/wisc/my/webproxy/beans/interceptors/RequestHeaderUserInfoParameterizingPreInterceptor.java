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
package edu.wisc.my.webproxy.beans.interceptors;

import java.util.ArrayList;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletRequest;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import edu.wisc.my.webproxy.beans.http.HeaderImpl;
import edu.wisc.my.webproxy.beans.http.IHeader;
import edu.wisc.my.webproxy.beans.http.Request;

public class RequestHeaderUserInfoParameterizingPreInterceptor extends UserInfoParameterizingPreInterceptor {

    public void intercept(RenderRequest request, RenderResponse response, Request httpReq) {
        updateHeaders(request, httpReq);
    }

    public void intercept(ActionRequest request, ActionResponse response, Request httpReq) {
        updateHeaders(request, httpReq);
    }

    /*
     * Implementation
     */

    private void updateHeaders(final PortletRequest req, final Request httpReq) {

        IHeader[] headers = httpReq.getHeaders();
        List<IHeader> replacements = new ArrayList<IHeader>(headers.length);

        for (IHeader h : headers) {
            final String before = h.getValue();
            final String after = this.resolveTokens(req, before, Strategy.REPLACE);
            replacements.add(new HeaderImpl(h.getName(), after));
        }

        httpReq.setHeaders(replacements.toArray(new IHeader[headers.length]));

    }

}
