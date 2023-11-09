**IMPORTANT NOTE:  The old v1 code is now on the rel-1-patches branch.  The v2 branch has merged into master!**

# Web Proxy Portlet

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.jasig.portlet.proxy/WebProxyPortlet/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.jasig.portlet.proxy/WebProxyPortlet)
[![build status](https://github.com/uPortal-Project/WebproxyPortlet/workflows/CI/badge.svg?branch=master)](https://github.com/uPortal-Project/WebproxyPortlet/actions)
[![Code Climate](https://codeclimate.com/github/uPortal-Project/WebproxyPortlet/badges/gpa.svg)](https://codeclimate.com/github/uPortal-Project/WebproxyPortlet)
[![codeclimate.com Issue Count](https://codeclimate.com/github/uPortal-Project/WebproxyPortlet/badges/issue_count.svg)](https://codeclimate.com/github/uPortal-Project/WebproxyPortlet)

[Link to old documentation](https://wiki.jasig.org/display/PLT/WebProxy)

## Table of Contents
  - [Description](#desc)
  - [Feature Overview](#features)
  - [Usage / Common Uses](#usage)
  - [User Preferences](#prefs)
  - [Installation](#install)
  - [Configuration](#config)
    - [Portal Overlay Configuration](#overlay)
    - [Portlet Instance Configuration](#portlet_config)
  - [Where to Get Help](#help)
  - [Contribution Guidelines](#contrib)
  - [License](#license)

## <a name="desc"></a> Description
This project encompasses four portlets: Web Proxy Portlet, Gateway SSO, XSLT Portlet and JSON Portlet.

The Web Proxy Portlet is used to incorporate arbitrary web content as a portlet. It provides mechanisms for connecting
to and rendering HTML with options for clipping, maintaining session information, and handling cookies. Proxied content
is rendered within the portlet window.  Web Proxy Portlet is often used to incorporate web content or applications that
are built and run in non-Java environments allowing a site flexibility for integrating content with many different
technologies.

WebproxyPortlet v2 Gateway SSO (DEPRECATED) is a feature that allows uPortal to sign on to any remote system even if the remote system
does not share any authentication information with uPortal.  Gateway SSO will submit login information to the remote
system and then redirect to that remote system.  Other SSO solution assume that uPortal has authenticated to some system,
such as CAS and will then trust CAS to say the user is authenticated.  In this system, the authentication information is
submitted to the remote system invisible to the user.  This solution has the inherent risks of sending user authentication
information over the wire, rather than a security token, but this solution does not require external systems to implement
CAS or another authentication system.  It is therefore nearly invisible to any external system to which uPortal would want
to connect.

XSLT Portlet takes an XML source and transforms it with an XSLT source into HTML that can be rendered as content. The XML
can be placed in the classpath or on a web server. Similary, the XSLT source can also be a file placed on the classpath
or on a web server.

JSON Portlet is very similar to XLST Portlet except the source is JSON and the output is defined via JSP rather than
an XLST transformation.

## <a name="features"></a> Feature Overview
Web Proxy Portlet
  - Session handling
  - Clipping to get subsections of the targeted URL
  - Uses AJAX to request and replace portlet content from whitelist of proxy URLs

Gateway SSO (DEPRECATED)
  - Log into remote services for users based on user attributes

XSLT Portlet
  - Uses XSLT to transform XML into renderable output

JSON Portlet
  - Uses JSPs to render JSON into renderable output

## <a name="usage"></a> Usage / Common Uses
It is very common for a University with skills in PHP, Ruby, or other technologies to create small webapps/pages
specifically to expose content in a page meant to be consumed exclusively by the Web Proxy Portlet to render in the
portal. This allows the portal to apply styling to the proxied content so the rendered content fits in well with the
overall portal look and feel. This is one advantage over using an iframe where the styling of the content would have to
be modified in the system the content is obtained from.

Gateway SSO allows services to be brought into the portal that require a login form.

XSLT Portlet is an excellent solution when a back-end service provides XML output via an API. This output could then
be styled in a portlet window for user viewing.

JSON Portlet is a great tool to render JSON from a back-end API to styled content for users.

## <a name="prefs"></a> User Preferences
These portlets are rarely configured to allow users to modify any preferences; however, there is some support in the
Gateway Portlet to supply user preferences for form field values.

## <a name="install"></a> Installation
This project is published to Maven Central and can be installed with uPortal
using the Portal Overlay Configuration section below.

## <a name="config"></a> Configuration
Portlets are web applications that sit inside a larger, aggregating project, the portal.
Configuration of different concerns is often performed by deployment engineers,
portal administrators, and end-users. End-User configuration is addressed
in User Preferences. Deployment and portal-wide configuration is described below.

### <a name="overlay"></a> Portal Overlay Configuration
This project is already included with uPortal. To upgrade in uPortal 4.x, simply change the version
used in the top-level `pom.xml`:

```xml
        <WebProxyPortlet.version>2.3.2</WebProxyPortlet.version>
```

For uPortal 5, change the version in `gradle.properties` in uPortal-start:

```properties
webProxytPortletVersion=2.3.2
```

### <a name="portlet_config"></a> Portlet Instance Configuration
Web Proxy Portlet
  - [Web Proxy Use Cases](/docs/wpp_usage.md)
  - [Web Proxy Configuration](/docs/wpp_config.md)

Gateway SSO
  - [Gateway Configuration](/docs/gateway_config.md)

XSLT Portlet
  - [XSLT Portlet Configuration](/docs/xslt_config.md)

JSON Portlet
  - [JSON Portlet Configuration](/docs/json_config.md)

## <a name="help"></a> Where to Get Help
The <uportal-user@apereo.org> mailing list is the best place to go with
questions related to Apereo portlets and uPortal.

Issues should be reported at <https://issues.jasig.org/browse/WPP>.
Check if your issue has already been reported. If so, comment that you are also
experiencing the issue and add any detail that could help resolve it. Feel free to
create an issue if it has not been reported. Creating an account is free and can be
initiated at the Login widget in the default dashboard.

## <a name="contrib"></a> Contribution Guidelines
Apereo requires contributors sign a contributor license agreement (CLA).
We realize this is a hurdle. To learn why we require CLAs, see
"Q5. Why does Apereo require Contributor License Agreements (CLAs)?"
at <https://www.apereo.org/licensing>.

The CLA form(s) can be found <https://www.apereo.org/licensing/agreements> along
with the various ways to submit the form.

Contributions will be accepted once the contributor's name appears at
<http://licensing.apereo.org/completed-clas>.

See <https://www.apereo.org/licensing> for details.

## <a name="license"></a> License

Copyright 2016 Apereo Foundation, Inc.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this project except in compliance with the License.
You may obtain a copy of the License at

<http://www.apache.org/licenses/LICENSE-2.0>

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

See <https://www.apereo.org/licensing> for additional details.
