package edu.wisc.my.webproxy.beans.http;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Table;

/**
 * PersistedCookieImpl is an embeddable, JPA-persistable implementation of
 * ICookie.  This implementation is intended for use by the JPA implementation
 * of IWebProxyState and should closely mirror the information available from
 * an HttpClient cookie instance.
 * 
 * @author Jen Bourey, jbourey@unicon.net
 */
@Embeddable
@Table(name = "WEB_PROXY_STATE_COOKIE")
public class PersistedCookieImpl implements ICookie, Serializable {
	
	@Column( name = "NAME" )
	private String name;

	@Column( name = "VALUE", length = 2048 )
	private String value;
	
	@Column( name = "PATH" )
	private String path;
	
	@Column( name = "DOMAIN" )
	private String domain;
	
	@Column( name = "EXPIRY_DATE" )
	private Date expiryDate;
	
	@Column( name = "COMMENT" )
	private String comment;
	
	@Column( name = "SECURE" )
	private boolean secure;
	
	@Column( name = "VERSION" )
	private int version;
	
	public PersistedCookieImpl() { }
	
	public boolean isExpired() {
		return expiryDate != null && expiryDate.before(new Date());
	}
	
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
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
	
	public void setDomain(String domain) {
		this.domain = domain;
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

}
