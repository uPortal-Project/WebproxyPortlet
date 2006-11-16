/* Copyright 2006 The JA-SIG Collaborative.  All rights reserved.
*  See license distributed with this file and
*  available online at http://www.uportal.org/license.html
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
