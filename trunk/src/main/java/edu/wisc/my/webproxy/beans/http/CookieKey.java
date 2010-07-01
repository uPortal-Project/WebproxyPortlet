/**
 * Copyright (c) 2000-2009, Jasig, Inc.
 * See license distributed with this file and available online at
 * https://www.ja-sig.org/svn/jasig-parent/tags/rel-10/license-header.txt
 */

package edu.wisc.my.webproxy.beans.http;

import org.apache.commons.lang.Validate;

/**
 * @author Eric Dalquist
 * @version $Revision$
 */
public class CookieKey implements Comparable<CookieKey> {
    public final String name;
    public final String domain;
    
    public CookieKey(String name, String domain) {
        Validate.notNull(name, "name cannot be null");
        this.name = name;
        
        if (domain == null) {
            this.domain = "";
        }
        else if (domain.indexOf('.') == -1) {
            this.domain = domain.toLowerCase() + ".local";
        }
        else {
            this.domain = domain.toLowerCase();
        }
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((domain == null) ? 0 : domain.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        CookieKey other = (CookieKey) obj;
        if (domain == null) {
            if (other.domain != null)
                return false;
        }
        else if (!domain.equals(other.domain))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        }
        else if (!name.equals(other.name))
            return false;
        return true;
    }
    public int compareTo(CookieKey o) {
        int res = this.name.compareTo(o.name);
        if (res == 0) {
            res = this.domain.compareToIgnoreCase(o.domain);
        }
        return res;
    }

    @Override
    public String toString() {
        return "CookieKey [name=" + name + ", domain=" + domain + "]";
    }
}
