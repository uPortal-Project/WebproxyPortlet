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

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;

import org.apache.commons.lang.Validate;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

/**
 * PersistedCookieImpl is an embeddable, JPA-persistable implementation of
 * ICookie.  This implementation is intended for use by the JPA implementation
 * of IWebProxyState and should closely mirror the information available from
 * an HttpClient cookie instance.
 * 
 * @author Jen Bourey, jbourey@unicon.net
 */
@Entity
@Table(name = "WP_COOKIE")
@GenericGenerator(name = "WP_COOKIE_ID_GEN", strategy = "native", parameters = {
        @Parameter(name = "sequence", value = "WP_COOKIE_ID_SEQ"),
        @Parameter(name = "table", value = "WP_JPA_UNIQUE_KEY"),
        @Parameter(name = "column", value = "NEXT_WP_COOKIE_ID_HI") })
public class PersistedCookieImpl implements ICookie, Serializable {
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(generator = "WP_COOKIE_ID_GEN")
    @Column(name = "COOKIE_ID")
    private final long cookieId;

    @Column( name = "COOKIE_NAME", length = 4000, nullable = false )
	private final String name;
    
    @Column( name = "DOMAIN", length = 1000 )
    private final String domain;

	@Column( name = "COOKIE_VALUE", length = 4000 )
	private String value;
	
	@Column( name = "PATH", length = 1000 )
	private String path;
	
	@Column( name = "EXPIRY_DATE" )
	private Date expiryDate;
	
	@Column( name = "COOKIE_COMMENT", length = 4000 )
	private String comment;
	
	@Column( name = "SECURE" )
	private boolean secure;
	
	@Column( name = "VERSION" )
	private int version;
	
	@Column( name = "CREATE_DATETIME" )
	private Date created;
	
	@Column( name = "UPDATE_DATETIME" )
    private Date updated;
    
    @PrePersist
    protected void onCreate() {
        created = new Date();
    }

    @PreUpdate
    protected void onUpdate() {
        updated = new Date();
    }
	
	

	/*
	 * Only used by JPA
	 */
	@SuppressWarnings("unused")
    private PersistedCookieImpl() { 
	    this.cookieId = -1;
        this.name = null;
        this.domain = null;
	}
	
    public PersistedCookieImpl(String name, String domain) {
        Validate.notNull(name, "name cannot be null");
        
        this.cookieId = -1;
        this.name = name;
        this.domain = domain;
    }



    public boolean isExpired() {
		return expiryDate != null && expiryDate.before(new Date());
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getValue() {
		return this.value;
	}
	
	public void setValue(String value) {
		this.value = value;
	}
	
	public String getPath() {
		return this.path;
	}
	
	public void setPath(String path) {
		this.path = path;
	}
	
	public String getDomain() {
		return this.domain;
	}
	
	public Date getExpiryDate() {
		return this.expiryDate;
	}
	
	public void setExpiryDate(Date expiryDate) {
		this.expiryDate = expiryDate;
	}
	
	public String getComment() {
		return this.comment;
	}
	
	public void setComment(String comment) {
		this.comment = comment;
	}
	
	public boolean isSecure() {
		return this.secure;
	}
	
	public void setSecure(boolean secure) {
		this.secure = secure;
	}
	
	public int getVersion() {
		return this.version;
	}
	
	public void setVersion(int version) {
		this.version = version;
	}
	
    public Date getCreated() {
        return created;
    }
    public Date getUpdated() {
        return updated;
    }

    @Override
    public String toString() {
        return "PersistedCookieImpl " +
        		"[cookieId=" + cookieId + ", domain=" + domain + ", path=" + path + ", expiryDate=" + expiryDate + ", " +
    				"secure=" + secure + ", version=" + version + ", name=" + name + ", " +
					"value=" + value + ", comment=" + comment + ", created=" + created + ", updated=" + updated + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((comment == null) ? 0 : comment.hashCode());
        result = prime * result + ((domain == null) ? 0 : domain.hashCode());
        result = prime * result + ((expiryDate == null) ? 0 : expiryDate.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((path == null) ? 0 : path.hashCode());
        result = prime * result + (secure ? 1231 : 1237);
        result = prime * result + ((value == null) ? 0 : value.hashCode());
        result = prime * result + version;
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
        PersistedCookieImpl other = (PersistedCookieImpl) obj;
        if (comment == null) {
            if (other.comment != null)
                return false;
        }
        else if (!comment.equals(other.comment))
            return false;
        if (domain == null) {
            if (other.domain != null)
                return false;
        }
        else if (!domain.equals(other.domain))
            return false;
        if (expiryDate == null) {
            if (other.expiryDate != null)
                return false;
        }
        else if (!expiryDate.equals(other.expiryDate))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        }
        else if (!name.equals(other.name))
            return false;
        if (path == null) {
            if (other.path != null)
                return false;
        }
        else if (!path.equals(other.path))
            return false;
        if (secure != other.secure)
            return false;
        if (value == null) {
            if (other.value != null)
                return false;
        }
        else if (!value.equals(other.value))
            return false;
        if (version != other.version)
            return false;
        return true;
    }

	
}
