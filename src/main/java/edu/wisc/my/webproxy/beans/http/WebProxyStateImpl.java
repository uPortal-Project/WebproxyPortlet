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

package edu.wisc.my.webproxy.beans.http;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.IndexColumn;
import org.hibernate.annotations.Parameter;

/**
 * WebProxyStateImpl represents the default JPA/Hibernate implementation of 
 * IWebProxyState.
 * 
 * @author Jen Bourey, jbourey@unicon.net
 */
@Entity
@Table(name = "WP_STATE")
@GenericGenerator(name = "WP_STATE_ID_GEN", strategy = "native", parameters = {
        @Parameter(name = "sequence", value = "WP_STATE_ID_SEQ"),
        @Parameter(name = "table", value = "WP_JPA_UNIQUE_KEY"),
        @Parameter(name = "column", value = "NEXT_WP_STATE_ID_HI") })
public class WebProxyStateImpl implements IWebProxyState {
    @Id
    @GeneratedValue(generator = "WP_STATE_ID_GEN")
    @Column(name = "STATE_ID")
    private final long stateId;
    
	@Column(name = "STATE_KEY", unique = true, updatable = false, length = 500)
	@Index(name = "STATE_KEY_IDX")
	private String stateKey;
	
    @OneToMany(cascade = CascadeType.ALL, targetEntity = PersistedCookieImpl.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "STATE_ID")
    @IndexColumn(name = "COOKIE_ORDER")
    @Cascade( { org.hibernate.annotations.CascadeType.DELETE_ORPHAN, org.hibernate.annotations.CascadeType.ALL })
	private List<ICookie> cookies = new ArrayList<ICookie>();
    
	
	/**
	 * Default constructor
	 */
	public WebProxyStateImpl() { 
	    this.stateId = -1;
	}
	
	/**
	 * Construct a new WebProxyStateImpl with the specified state key.
	 * 
	 * @param stateKey
	 */
	public WebProxyStateImpl(String stateKey) {
	    this.stateId = -1;
		this.stateKey = stateKey;
	}
	
	/*
	 * (non-Javadoc)
	 * @see edu.wisc.my.webproxy.beans.http.IWebProxyState#getStateKey()
	 */
	public String getStateKey() {
		return this.stateKey;
	}
	
	/*
	 * (non-Javadoc)
	 * @see edu.wisc.my.webproxy.beans.http.IWebProxyState#getCookies()
	 */
	public List<ICookie> getCookies() {
		return this.cookies;
	}

	/*
	 * (non-Javadoc)
	 * @see edu.wisc.my.webproxy.beans.http.IWebProxyState#setCookies(java.util.Collection)
	 */
	public void setCookies(List<ICookie> cookies) {
	    if (this.cookies == cookies) {
	        return;
	    }
	    
		this.cookies.clear();
		for (ICookie cookie : cookies) {
			this.cookies.add(cookie);
		}
	}

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((stateKey == null) ? 0 : stateKey.hashCode());
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
        WebProxyStateImpl other = (WebProxyStateImpl) obj;
        if (stateKey == null) {
            if (other.stateKey != null)
                return false;
        }
        else if (!stateKey.equals(other.stateKey))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "WebProxyStateImpl [stateId=" + stateId + ", stateKey=" + stateKey + ", cookies=" + cookies + "]";
    }
}
