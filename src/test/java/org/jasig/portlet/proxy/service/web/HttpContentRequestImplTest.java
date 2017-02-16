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
package org.jasig.portlet.proxy.service.web;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.http.client.protocol.HttpClientContext;
import org.jasig.portlet.proxy.service.IFormField;
import org.junit.Before;
import org.junit.Test;

public class HttpContentRequestImplTest {
	
	@Before
	public void setUp() {
		
	}
	
	@Test
	/**
	 * ensures duplicate() returns a unique object by duplicating an object,
	 * changing the original, and then ensuring that the duplicate and the original
	 * are not the same
	 */
	public void testDuplicate() {
		final String originalMethod = "post"; 
		final String newMethod = "get";
		
		final String originalProxiedLocation = "http://www.yahoo.com";
		final String newProxiedLocation = "http://www.espn.com";
		
		final String originalHeaderKey = "headerKey";
		final String originalHeaderValue = "headerValue";
		final String newHeaderValue = "newHeaderValue";
		
		final String originalParameterKey = "parameterA";
		final String originalParameterValue1 = "A";
		final String originalParameterValue2 = "B";
		final String newParameterValue1 = "99";

		final HttpClientContext originalHttpClientContext = HttpClientContext.create();
		final HttpClientContext newHttpClientContext = HttpClientContext.create();

		Map<String, IFormField> parameters = new LinkedHashMap<String, IFormField>();
		IFormField parameter = new FormFieldImpl();
		parameter.setName(originalParameterKey);
		String[] values ={ originalParameterValue1, originalParameterValue2};
		parameter.setValues(values);
		parameters.put(originalParameterKey,  parameter);
		
		Map<String, String> headers = new LinkedHashMap<String, String>();
		headers.put(originalHeaderKey, originalHeaderValue);
		
		HttpContentRequestImpl original = new HttpContentRequestImpl();
		original.setForm(true);
		original.setProxiedLocation(originalProxiedLocation);
		original.setMethod(originalMethod);
		original.setHeaders(headers);
		original.setParameters(parameters);
		original.setHttpContext(originalHttpClientContext);
		
		HttpContentRequestImpl copy = original.duplicate();
		
		assertEquals(copy.isForm(), true);
		assertEquals(copy.getMethod(), originalMethod);
		assertEquals(copy.getProxiedLocation(), originalProxiedLocation);
		assertEquals(copy.getHeaders().get(originalHeaderKey), originalHeaderValue);
		assertEquals(copy.getHttpContext(), originalHttpClientContext);
		IFormField copyParameter = copy.getParameters().get(originalParameterKey);
		assertEquals(copyParameter.getValues()[0], originalParameterValue1);
		assertEquals(copyParameter.getValues()[1], originalParameterValue2);
		
		original.setForm(false);
		original.setProxiedLocation(newProxiedLocation);
		original.setMethod(newMethod);
		original.setHttpContext(newHttpClientContext);
		IFormField originalParameter = original.getParameters().get(originalParameterKey);
		originalParameter.getValues()[0] = newParameterValue1;
		original.getHeaders().put(originalHeaderKey, newHeaderValue);
		
		assertNotSame(original.isForm(), copy.isForm());
		assertNotSame(original.getMethod(), copy.getMethod());
		assertNotSame(original.getProxiedLocation(), copy.getProxiedLocation());
		assertNotSame(original.getHttpContext(), copy.getHttpContext());
		assertNotSame(original.getParameters().get(originalParameterKey), copy.getParameters().get(originalParameterKey));
		assertNotSame(original.getHeaders().get(originalHeaderKey), copy.getHeaders().get(originalHeaderKey));
	}
}
