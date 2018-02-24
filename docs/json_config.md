# JSON Portlet Configuration

There are only a few preferences for JSON Portlet. An example portlet
definition can be found [here](/docs/json-sample.portlet-definition.xml).

## `contentService`

The `contentService` preference dictates where the JSON source is located.
The two options for this preference are:
  - `classpathContentService`
  - `httpContentService`

## `location`

The `location` preference is either a path on the WebProxyPortlet classpath
or a URL to the JSON source. Examples are:
  - `/test-content/sample.json`
  - `http://localhost:8080/sample.json`

## `mainView`

The `mainView` preference is the name of the JSP to use for rendering the JSON source.
The value should omit the `.jsp` suffix. JSP files should be placed in
`src/main/webapp/WEB-INF/jsp/` directory. Example:
  - `sampleJson`

## `preInterceptors`

The `preInterceptors` preference is used to configure additional features
in `httpContentService` if specified above. Interceptors provide support
for dynamic location URL parameters and authentication.
See [/docs/wpp_usage.md](/docs/wpp_usage.md)

