====
    Licensed to Apereo under one or more contributor license
    agreements. See the NOTICE file distributed with this work
    for additional information regarding copyright ownership.
    Apereo licenses this file to you under the Apache License,
    Version 2.0 (the "License"); you may not use this file
    except in compliance with the License.  You may obtain a
    copy of the License at the following location:

      http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing,
    software distributed under the License is distributed on an
    "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
    KIND, either express or implied.  See the License for the
    specific language governing permissions and limitations
    under the License.
====

Portlet Name: Authenticated Web Proxy

Version Number: 1.0

Designers: University of Wisconsin

Developers:
Eric Dalquist
David Grimwood
Nabeel Ramzan
 
Requirements spec: Authenticated Web Proxy Requirements Specifications, version 1.0:
	-${url}

Design spec: Authenticated Web Proxy Design Document, version 1.0:
	-${url}

Summary
----------------------------------------
The Authenticated WebProxy portlet allows seamless integration with web-based services regardless of the technology used to implement them.  These services are represented within the portal as individual channels on a user's layout. All content from the proxied site is scraped, parsed and rendered inside the portal. Pages are refreshed and kept inside the portal when interacted with. HTTP standards are followed, allowing communication between the browser and dynamic web-based applications. In addition to this, Authenticated WebProxy provides additional technologies for authentication(Form and Basic authentication), clipping, and content caching. Authenticated Webproxy also provides a mechanism for passing user-specific information to the back-end application.

Configuration
----------------------------------------
All aspects of the Authenticated WebProxy portlet are configurable(i.e., Http Management, HTML Parsing,URL Filtering, and Http Clipping) . All implementation classes are plugged in via placing references to them in the applicationContext.xml file in the WebProxy\web\WEB-INF directory. 


Build and Installation:
----------------------------------------
The ant build file for the project provides a deploy target. This depends on the clean, init, compile and dist targets to create the WAR file and then copies the file in the designated web container. The web container home and portal directory locations need to be configured within the corresponding build.properties

In order for Authenticated WebProxy to utilize session and cache persistence, the latest version of Common Storage must be deployed. The appropriate version of the JAR is included in this WebProxy release. Please see the CommonStorage documentation for detailed instruction on deployment.

Publishing:
----------------------------------------
This channel can be published via the channel manager or by using the 'pubchan' target from your portal directory. However, since many specific configuration variables need to be properly set, it is recommended to use the channel manager method. To do so, please follow the steps outlined below:
-Login as user with admin privileges
-Click on 'Channel Manager'
-Click on 'Publish a new channel'
-Select Portlet for Channel Type
-Provide a Channel Title, Name, Functional Name, Description, and Timeout.
-The 'Portlet definition ID' for Authenticated Web Proxy is WebProxy.AuthenticatedWebProxy
-No portlet preferences are required for this portlet
-Select 'Editable' for the Channel Controls, if you would like the end user to be able to go to a predetermined URL to edit the proxied web application configuration.
-Select the channel categories for the portlet
-Select the groups or people who should have access to this portlet

You should now be in the custom CONFIG mode of the portlet, please consult this readme doc for descriptions of each possible configuration.

General Configuration:

-Base URL (REQUIRED): The Base URL is the starting point of the proxied application and will be the first page proxied for the end user after authentication. This value must contain the protocol of the URL. (e.g., http://www.foo.bar/, http://foo.bar/example.html)
-Edit URL (OPTIONAL): This URL will provide a link that will allow the end user to configure the proxied web-application for their own personal needs. This value must also contain the protocol. (e.g., http://foo.bar/edit.html)
-Portlet URL list (OPTIONAL): Must contain the URLs of the web application you would like or would not like to be proxied, depending on the URL List Type (see below). The portlet URL list uses regular expressions for matching. (e.g., .foo.)
-URL List Type: This value is set to Include by default. This value will designate how the portlet will read the Portlet Url list. If this value is set to Include,the portlet will rewrite and proxy all matching URL expressions in the list. If set to Exclude, the portlet will proxy all the sites that do not match the URL expressions listed.
-Pre-Interceptor class (Optional): This is an optional configuration that can be used to manipulate the http request before it is sent to the web-application. This will require a custom class file to use. (e.g. edu.wisc.my.webproxy.CPreInterceptor)
-Post-Interceptor class (Optional):  This is an optional configuration that can be used to manipulate the http response after it is received from the web-application. This will require a custom class file to use. (e.g. edu.wisc.my.webproxy.CPostInterceptor)

Cache Configuration:

-Use Cache (Optional) : Select this if you would like to enable the Authenticated WebProxy cache
-Cache Timeout (Optional): The amount of seconds you would like the cache to be valid for.
-User expired data if the remote server is not responding (Optional): If selected the portal will use expired data if the remote server stops responding. If selected, you must designate the amount of seconds Authenticated Web Proxy will wait before trying to contact the non-responding servers.
-Persist Cache (Optional): If you would like to keep the cache beyond the user's session.
-Cache Scope (Optional): If User is selected, the cache will only be valid for the end portal user. If Application is selected, all users will share the data stored in cache.

Http Headers:

-Header name value pair: This provides a list of Header Names and their corresponding value you would like to be included in all Http Requests.

Static HTML Configuration:

-Static Header (Optional): Any HTML you would like prepended to the displayable parsed content.
-Static Footer (Optional): Any HTML you would like appended tot he displayable parsed content.

Http Configuration:

-Http Timeout (Required): The amount of seconds you would like Authenticated WebProxy to wait before determining the remote server is non-responsive.
-Maximum Redirects (Optional): This configurable option has a default value of 5 and will determine the maximum number of times the proxied site is able to redirect  
the end user.
-Enable Authentication (Optional): Select this box if you would like to enable any type of authentication.
-Type of Authenticated (Optional): Select the type of Authentication the web-based application requires. (Form based, Basic, NTLM)
-Enable Session Persistence (Optional): Click on this to have the session of the end user persisted after the user logs out
-Shared Session Key (Optional): A key for storing session to be shared between other WebProxy portlets for user. If left blank, Shared Sessions will be disabled. It is recommended that this variable be unique so that only the intended proxied web applications share the session.
-Enter User Name (For Basic Auth ONLY): The username or Ldap value with the option to prompt the end user for individual username and persist this value beyond the end user's session. (e.g., ${photo_id}, or 1234556789) 
-Enter Password (For Basic Auth ONLY): The password or Ldap value with the option to prompt the end user for individual password and persist this value beyond the end user's session. If using the ldap value, you must wrap the value within ${INSERTVALUEHERE} for the user specific substitution to occur (e.g., ${mum_id}). Use must also add this user-attribute to your portlet.xml   
-Session Timeout(For Form Auth ONLY): The amount of minutes until the user's credentials must be posted again.
-Authentication URL (For Form Auth ONLY): The URL the credentials will be posted to.
-Additional Dynamic Authentication parameters (For Form Auth ONLY): Dynamic parameters are parameters that must be posted for authentication and are not the same for every portal user. You can enter the parameter name, whether or not you would like to persist the value beyond the user's session, and whether the value is sensitive to the end user. (e.g., userName, password).
-Additional Static Authentication Parameters: These parameters will be the same for every user that has permission to use the Authenticated WebProxy portlet. If the parameter name does not have a corresponding value, leave blank.

Clipping Configuration:
-Do Clipping: Click on this checkbox if you would like to configure the Authenticated WebProxy portlet for HTML Clipping. Html Clipping can be used to only display content within certain Absolute Element Paths (e.g., /html/body/), Comments(e.g., <!--clipping-->), and Element (e.g., <script>). Please keep in mind that once clipping is enabled the end user will only see the content that has been clipped, all other content will be dropped.

HTML Parser Configuration:
-Insert DocType (Optional): Default value of false.  Specifies whether the HTML parser should override the public and system identifier values specified in the document type declaration.
-Notify References (Optional): Default value of false.  Specifies whether the XML built-in entity references (e.g. &amp;, &lt;, etc) should be reported to the registered document handler. This only applies to the five pre-defined XML general entities -- specifically, "amp", "lt", "gt", "quot", and "apos". This is done for compatibility with the Xerces feature.
-Balance Tags (Optional): Default value of false and only recommended for non-malformed HTML.  Specifies if the HTML parser should attempt to balance the tags in the parsed document. Balancing the tags fixes up many common mistakes by adding missing parent elements, automatically closing elements with optional end tags, and correcting unbalanced inline element tags.
-Strip JavaScript commenting (Optional): Default value of false. Specifies whether the scanner should strip HTML comment delimiters (i.e. "<!--" and "-->") from <script> element content.
-Strip Comments (Optional): Default value of false.  Specifies whether the scanner should strip HTML comment delimiters (i.e. "<!--" and "-->") from <style> element content.
-Report Errors (Optional): Default value of false and should only be used when debugging.  Specifies whether errors should be reported to the registered error handler.

Testing
----------------------------------------
The applicationContext.xml file references the default implementations as of the initial check in. One can modify the existing implementations, or implement their own and update the applicationContext.xml accordingly to do further testing.

Running
----------------------------------------
<INSERT HERE>
