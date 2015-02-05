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

/**
 * IFormField is an interface used to store form field information within an IContentRequest object.  
 * The name getter/setter refer to the HTML input field name.
 * The value getter/setter refer to the value of the HTML input field name.  The value
 * can contain either a static value or a value that will be substituted through a properly
 * configured IPreInterceptor class.
 * The secured getter/setter refers to whether the field needs to be encrypted in the database
 * and displayed as a password instead of a text html input field.
 * 
 * @author mgillian
 *
 */
public interface IFormField {
	
	/**
	 * setName() sets the name of the field (name used in form submission)
	 * @param name
	 */
	public void setName(String name);
	
	/**
	 * getName() gets the name of the field (name used in form submission)
	 * @return
	 */
	public String getName();
	
	/**
	 * setValue() sets the first value of the field
	 * @param value
	 */
	public void setValue(String value);
	
	/**
	 * setValues() sets the values of the field
	 * @param values
	 */
	public void setValues(String[] values);
	
	/**
	 * getValue() returns the first value associated with the Field
	 * @return
	 */
	public String getValue();
	
	/**
	 * getValues() gets the values of the field
	 * @return
	 */
	public String[] getValues();
	
	/**
	 * getSecured() returns whether the field is encrypted and should be displayed obscured
	 * to the user
	 * @return true if field should be encrypted and secured, false otherwise
	 */
	public boolean getSecured();
	
	/**
	 * setSecured(boolean) changes whether the field should be encrypted and obscured
	 * @param isSecured
	 */
	public void setSecured(boolean isSecured);
	
	/**
	 * duplicate() copies the data from the current IFormField into the passed-in parameter
	 */
	public IFormField duplicate();
}