package org.jasig.portlet.proxy.service.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.portlet.PortletPreferences;
import java.io.IOException;
import java.net.URL;

public class ExampleCustomFormModifier implements IAuthenticationFormModifier {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	private String fieldName;

	public ExampleCustomFormModifier() {
	}

    /**
     * Example of modifications to a login form that invokes the system to gateway to in order to obtain a token
     * from that system that must be submitted with the login form and inserts the token into the url to submit.
     * Also demonstrates that you can custom-calculate the url to submit to by having the fieldname of proxiedLocation
     * (per the configuration in gateway-sso-portlet.xml).
     * @param preferences PortletPreferences that may be useful when calculating the return result
     * @return value of the form field
     * @throws IOException
     */
	@Override
	public String getResult(PortletPreferences preferences) throws IOException {

	      String urlSource ="https://prod.sdbor.edu/WebAdvisor/webadvisor?&TYPE=M&PID=CORE-WBMAIN&TOKENIDX="; //must be secure
	      URL xmlURLToOpen = new URL(urlSource);

	      String headerInfo = xmlURLToOpen.openConnection().getHeaderFields().toString();
	      int headerInfoBegin = headerInfo.indexOf("LASTTOKEN=");
	      String tokenID = headerInfo.substring(headerInfoBegin+10,headerInfoBegin+20);
	      tokenID = tokenID.replaceAll("=","");
	      tokenID = tokenID.replaceAll(",","");
	      logger.debug("urlSource: " + urlSource);
	      String formAction = urlSource+tokenID+"&SS=LGRQ&URL=https%3A%2F%2Fprod.sdbor.edu%2FWebAdvisor%2Fwebadvisor%3F%26TYPE%3DM%26PID%3DCORE-WBMAIN%26TOKENIDX%3D"+tokenID;
	      logger.debug("formAction: " + formAction);
		return formAction;
	}
	
	@Override
	public String getFieldName() {
		return this.fieldName;
	}
	
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

}
