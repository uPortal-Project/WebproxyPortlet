# XSLT Portlet Configuration

There are only a few preferences for XSLT Portlet. An example portlet
definition can be found [here](/docs/xslt-sample.portlet-definition.xml).

## `contentService`

The `contentService` preference dictates where the XML source is located.
The two options for this preference are:
  - `classpathContentService`
  - `httpContentService`

## `location`
The `location` preference is either a path on the WebProxyPortlet classpath
or a URL to the XML source. Examples are:
  - `/test-content/sample.xml`
  - `http://localhost:8080/sample.xml`

## `mainXslt`
The `mainXslt` preference is the URI to the XSLT source. This can be either
on the classpath or web. To source on the classpath, the format is different
from `location`. Examples:
  - `classpath:/test-content/sample.xsl`
  - `http://localhost:8080/sample.xsl`

## `mobileXslt`
While this preference is still implemented, support for "mobile" views has
been superseded by responsive design rather than mobile views. It follows
the same conventions as `mainXslt`.

## `preInterceptors`
The `preInterceptors` preference is used to configure additional features
in `httpContentService` if specified above. Interceptors provide support
for dynamic location URL parameters and authentication.
See [/docs/wpp_usage.md](/docs/wpp_usage.md)

