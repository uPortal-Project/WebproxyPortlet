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
