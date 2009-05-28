package edu.wisc.my.webproxy.beans.http;

import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Table;

import org.hibernate.annotations.CollectionId;

/**
 * WebProxyStateImpl represents the default JPA/Hibernate implementation of 
 * IWebProxyState.
 * 
 * @author Jen Bourey, jbourey@unicon.net
 */
@Entity
@Table(name = "WEB_PROXY_STATE")
public class WebProxyStateImpl implements IWebProxyState {

	@Id
	@Column(name = "STATE_KEY")
	private String stateKey;
	
	@org.hibernate.annotations.CollectionOfElements(fetch = FetchType.EAGER)
	@JoinTable(name = "WEB_PROXY_STATE_COOKIE", joinColumns = @JoinColumn(name = "STATE_ID"))
	@CollectionId(
		columns = @Column(name = "STATE_COOKIE_ID"),
		type = @org.hibernate.annotations.Type(type = "long"),
		generator = "sequence"
	)
	private Collection<PersistedCookieImpl> cookies = new ArrayList<PersistedCookieImpl>();
	
	/**
	 * Default constructor
	 */
	public WebProxyStateImpl() { }
	
	/**
	 * Construct a new WebProxyStateImpl with the specified state key.
	 * 
	 * @param stateKey
	 */
	public WebProxyStateImpl(String stateKey) {
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
	 * @see edu.wisc.my.webproxy.beans.http.IWebProxyState#setStateKey(java.lang.String)
	 */
	public void setStateKey(String stateKey) {
		this.stateKey = stateKey;
	}

	/*
	 * (non-Javadoc)
	 * @see edu.wisc.my.webproxy.beans.http.IWebProxyState#getCookies()
	 */
	public Collection<ICookie> getCookies() {
		Collection<ICookie> cookies = new ArrayList<ICookie>();
		cookies.addAll(this.cookies);
		return cookies;
	}

	/*
	 * (non-Javadoc)
	 * @see edu.wisc.my.webproxy.beans.http.IWebProxyState#setCookies(java.util.Collection)
	 */
	public void setCookies(Collection<ICookie> cookies) {
		this.cookies.clear();
		for (ICookie cookie : cookies) {
			this.cookies.add((PersistedCookieImpl) cookie);
		}
	}

}
