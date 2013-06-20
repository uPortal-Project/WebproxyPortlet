package org.jasig.portlet.proxy.service.web;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import java.util.LinkedHashMap;
import java.util.Map;

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
		
		HttpContentRequestImpl copy = original.duplicate();
		
		assertEquals(copy.isForm(), true);
		assertEquals(copy.getMethod(), originalMethod);
		assertEquals(copy.getProxiedLocation(), originalProxiedLocation);
		assertEquals(copy.getHeaders().get(originalHeaderKey), originalHeaderValue);
		IFormField copyParameter = copy.getParameters().get(originalParameterKey);
		assertEquals(copyParameter.getValues()[0], originalParameterValue1);
		assertEquals(copyParameter.getValues()[1], originalParameterValue2);
		
		original.setForm(false);
		original.setProxiedLocation(newProxiedLocation);
		original.setMethod(newMethod);
		IFormField originalParameter = original.getParameters().get(originalParameterKey);
		originalParameter.getValues()[0] = newParameterValue1;
		original.getHeaders().put(originalHeaderKey, newHeaderValue);
		
		assertNotSame(original.isForm(), copy.isForm());
		assertNotSame(original.getMethod(), copy.getMethod());
		assertNotSame(original.getProxiedLocation(), copy.getProxiedLocation());
		assertNotSame(original.getParameters().get(originalParameterKey), copy.getParameters().get(originalParameterKey));
		assertNotSame(original.getHeaders().get(originalHeaderKey), copy.getHeaders().get(originalHeaderKey));
	}
}
