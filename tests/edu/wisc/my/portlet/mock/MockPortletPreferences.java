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
package edu.wisc.my.portlet.mock;

import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.portlet.PortletPreferences;
import javax.portlet.ReadOnlyException;
import javax.portlet.ValidatorException;


/**
 * @author Eric Dalquist <a href="mailto:edalquist@unicon.net">edalquist@unicon.net</a>
 * @version $Id$
 */
public class MockPortletPreferences implements PortletPreferences {
    private Map initialPrefs;
    private final Map runtimePrefs = new Hashtable();

    public MockPortletPreferences() {
        this.initialPrefs = Collections.EMPTY_MAP;
    }
    
    public MockPortletPreferences(Map initialPrefs) {
        this.initialPrefs = Collections.unmodifiableMap(initialPrefs);
    }
    
    /**
     * @return Returns the initialPrefs.
     */
    public Map getInitialPrefs() {
        return this.initialPrefs;
    }
    
    /**
     * @param initialPrefs The initialPrefs to set.
     */
    public void setInitialPrefs(Map initialPrefs) {
        this.initialPrefs = Collections.unmodifiableMap(initialPrefs);;
    }


    /**
     * @see javax.portlet.PortletPreferences#isReadOnly(java.lang.String)
     */
    public boolean isReadOnly(String name) {
        final Preference pref = this.getPreference(name);
        
        if (pref != null)
            return pref.isReadOnly();
        else
            throw new IllegalArgumentException("No preference for '" + name + "'");
    }

    /**
     * @see javax.portlet.PortletPreferences#getValue(java.lang.String, java.lang.String)
     */
    public String getValue(String name, String defaultValue) {
        final Preference pref = this.getPreference(name);
        
        if (pref != null)
            return pref.getValue();
        else
            return defaultValue;
    }

    /**
     * @see javax.portlet.PortletPreferences#getValues(java.lang.String, java.lang.String[])
     */
    public String[] getValues(String name, String[] defaultValues) {
        final Preference pref = this.getPreference(name);
        
        if (pref != null)
            return pref.getValues();
        else
            return defaultValues;
    }

    /**
     * @see javax.portlet.PortletPreferences#setValue(java.lang.String, java.lang.String)
     */
    public void setValue(String name, String value) throws ReadOnlyException {
        final Preference pref = this.getPreference(name);

        if (pref == null || !pref.isReadOnly()) {
            this.runtimePrefs.put(name, new Preference(value, false));
        }
        else if (pref.isReadOnly()) {
            throw new ReadOnlyException("'" + name + "' is a read only preference.");
        }
    }

    /**
     * @see javax.portlet.PortletPreferences#setValues(java.lang.String, java.lang.String[])
     */
    public void setValues(String name, String[] values) throws ReadOnlyException {
        final Preference pref = this.getPreference(name);

        if (pref == null || !pref.isReadOnly()) {
            this.runtimePrefs.put(name, new Preference(values, false));
        }
        else if (pref.isReadOnly()) {
            throw new ReadOnlyException("'" + name + "' is a read only preference.");
        }
    }

    /**
     * @see javax.portlet.PortletPreferences#getNames()
     */
    public Enumeration getNames() {
        final Set temp = new HashSet();
        temp.addAll(this.initialPrefs.keySet());
        temp.addAll(this.runtimePrefs.keySet());
        return Collections.enumeration(Collections.unmodifiableSet(temp));
    }

    /**
     * @see javax.portlet.PortletPreferences#getMap()
     */
    public Map getMap() {
        final Map temp = new HashMap();
        
        for (final Iterator prefItr = this.runtimePrefs.entrySet().iterator(); prefItr.hasNext();) {
            final Map.Entry prefEntry = (Map.Entry)prefItr.next();
            temp.put(prefEntry.getKey(), ((Preference)prefEntry).getValues());
        }
        
        for (final Iterator prefItr = this.initialPrefs.entrySet().iterator(); prefItr.hasNext();) {
            final Map.Entry prefEntry = (Map.Entry)prefItr.next();
            
            if (!temp.containsKey(prefEntry.getKey()))
                temp.put(prefEntry.getKey(), ((Preference)prefEntry).getValues());
        }

        return Collections.unmodifiableMap(temp);
    }

    /**
     * @see javax.portlet.PortletPreferences#reset(java.lang.String)
     */
    public void reset(String name) throws ReadOnlyException {
        this.runtimePrefs.remove(name);
    }

    /**
     * @see javax.portlet.PortletPreferences#store()
     */
    public void store() throws IOException, ValidatorException {
        //Intentional noop
    }
    
    private Preference getPreference(String name) {
        Preference pref = (Preference)this.runtimePrefs.get(name);
        if (pref == null)
            pref = (Preference)this.initialPrefs.get(name);
        
        return pref;
    }
    
    public class Preference {
        private boolean readOnly = false;
        private String[] values = null;
        
        public Preference(String value, boolean readOnly) {
            this.setValue(value);
            this.setReadOnly(readOnly);
        }
        
        public Preference(String[] values, boolean readOnly) {
            this.setValues(values);
            this.setReadOnly(readOnly);
        }
        
        /**
         * @return Returns the readOnly.
         */
        public boolean isReadOnly() {
            return this.readOnly;
        }
        
        /**
         * @return Returns the values.
         */
        public String[] getValues() {
            return this.values;
        }
        
        /**
         * @return Returns the value.
         */
        public String getValue() {
            if (this.values == null || this.values.length <= 0) {
                return null;
            }
            else {
                return this.values[0];
            }
        }
        
        /**
         * @param readOnly The readOnly to set.
         */
        public void setReadOnly(boolean readOnly) {
            this.readOnly = readOnly;
        }
        
        /**
         * @param values The values to set.
         */
        public void setValues(String[] values) {
            this.values = values;
        }
        
        /**
         * @param values The value to set.
         */
        public void setValue(String value) {
            this.values = new String[] { value };
        }
    }
}
