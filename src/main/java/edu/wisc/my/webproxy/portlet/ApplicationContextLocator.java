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

package edu.wisc.my.webproxy.portlet;

import org.springframework.context.ApplicationContext;

/**
 * Locator class for accessing the ApplicationContext from code that
 * doesn't have access to the servlet context.
 * 
 * @author Eric Dalquist <a href="mailto:eric.dalquist@doit.wisc.edu">eric.dalquist@doit.wisc.edu</a>
 * @version $Revision$
 */
public final class ApplicationContextLocator {
    private static final ThreadLocal<ApplicationContext> appContextLocal = new ThreadLocal<ApplicationContext>();
    
    public static void setApplicationContext(ApplicationContext context) {
        appContextLocal.set(context);
    }
    
    public static ApplicationContext getApplicationContext() {
        return appContextLocal.get();
    }
    
    
    private ApplicationContextLocator() { }
}
