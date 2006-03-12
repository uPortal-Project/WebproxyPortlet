/*******************************************************************************
* Copyright 2004, The Board of Regents of the University of Wisconsin System.
* All rights reserved.
*
* A non-exclusive worldwide royalty-free license is granted for this Software.
* Permission to use, copy, modify, and distribute this Software and its
* documentation, with or without modification, for any purpose is granted
* provided that such redistribution and use in source and binary forms, with or
* without modification meets the following conditions:
*
* 1. Redistributions of source code must retain the above copyright notice,
* this list of conditions and the following disclaimer.
*
* 2. Redistributions in binary form must reproduce the above copyright notice,
* this list of conditions and the following disclaimer in the documentation
* and/or other materials provided with the distribution.
*
* 3. Redistributions of any form whatsoever must retain the following
* acknowledgement:
*
* "This product includes software developed by The Board of Regents of
* the University of Wisconsin System."
*
*THIS SOFTWARE IS PROVIDED BY THE BOARD OF REGENTS OF THE UNIVERSITY OF
*WISCONSIN SYSTEM "AS IS" AND ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING,
*BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
*PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE BOARD OF REGENTS OF
*THE UNIVERSITY OF WISCONSIN SYSTEM BE LIABLE FOR ANY DIRECT, INDIRECT,
*INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
*LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
*PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
*LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
*OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
*ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*******************************************************************************/
package edu.wisc.my.webproxy.beans;

import edu.wisc.my.apilayer.portlet.IScopeIdentifier;


/**
 * Base IScopeIdentifier for use by WebProxy components.
 * 
 * @author Eric Dalquist <a href="mailto:edalquist@unicon.net">edalquist@unicon.net</a>
 * @version $Id$
 */
public abstract class WebProxyScopeIdentifier implements IScopeIdentifier {

    /**
     * @see edu.wisc.my.apilayer.portlet.IScopeIdentifier#getSystemIdentifier()
     */
    public String getSystemIdentifier() {
        return "edu.wisc.my.webproxy";
    }

    /**
     * @see edu.wisc.my.apilayer.portlet.IScopeIdentifier#getApplicationIdentifier()
     */
    public String getApplicationIdentifier() {
        return "EMPTY_APP_ID";
    }

    /**
     * @see edu.wisc.my.apilayer.portlet.IScopeIdentifier#getPublishedIdentifier()
     */
    public String getPublishedIdentifier() {
        return "EMPTY_PUB_ID";
    }

    /**
     * @see edu.wisc.my.apilayer.portlet.IScopeIdentifier#getInstanceIdentifier()
     */
    public String getInstanceIdentifier() {
        return "EMPTY_INST_ID";
    }

    /**
     * @see edu.wisc.my.apilayer.portlet.IScopeIdentifier#getUserIdentifier()
     */
    public String getUserIdentifier() {
        return null;
    }
}
