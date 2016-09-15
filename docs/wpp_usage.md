# Web Proxy Portlet Usage

## Table of Contents
  - [Dynmaic Parameters in Location URL](#dynamic_params)
  - [Basic Authentication Based On User Attributes](#basic_auth)

## <a name="dynamic_params"></a> Dynamic Parameters in Location URL
The parameters need to be included in the portlet definitions as user
attributes. Once there, you can add them to the URL by surrounding them
in curly braces. The `userInfoUrlParameterizingPreInterceptor`
preInterceptor must be used.

For example, say you want to add ihe user's ID in the location URL.

Add the attribute to the portlet definition:

```
    <user-attribute>
        <description>User ID</description>
        <name>user.login.id</name>
    </user-attribute>
```

Then add "{user.login.id}" in the URL as needed:

    location: http://localhost:8080/test{user.login.id}
 
Make sure userInfoUrlParameterizingPreInterceptor is in the
preInterceptors list:

    preInteceptors: userInfoUrlParameterizingPreInterceptor
 
Portlet definition snippets for the previous two preferences:

```
    <portlet-preference>
        <name>location</name>
        <readOnly>false</readOnly>
        <value>http://localhost:8080/test{user.login.id}</value>
    </portlet-preference>
    <portlet-preference>
        <name>preInterceptors</name>
        <readOnly>false</readOnly>
        <value>userInfoUrlParameterizingPreInterceptor</value>
    </portlet-preference>
    ...
    <user-attribute>
        <description>User ID</description>
        <name>user.login.id</name>
    </user-attribute>
```

## <a name="basic_auth"></a> Basic Authentication Based On User Attributes
Basic Authentication with user values is implemented with `UserInfoBasicAuthenticationPreInterceptor`.
The user values for ID and password need to be added to the portlet
definition. Then, BASIC authentication interceptor will use them.

```
    <portlet-preference>
        <name>preInterceptors</name>
        <readOnly>false</readOnly>
        <value>UserInfoBasicAuthenticationPreInterceptor</value>
    </portlet-preference>
    ...
    <user-attribute>
        <description>User ID</description>
        <name>user.login.id</name>
    </user-attribute>
    <user-attribute>
        <description>User Password</description>
        <name>password</name>
    </user-attribute>
```

If other user attributes are used for authentication, then you can add
those attributes and then configure WPP to use them:

```
    <portlet-preference>
        <name>preInterceptors</name>
        <readOnly>false</readOnly>
        <value>UserInfoBasicAuthenticationPreInterceptor</value>
    </portlet-preference>
    ...
    <portlet-preference>
        <name>usernameKey</name>
        <readOnly>false</readOnly>
        <value>userGoogleAcct</value>
    </portlet-preference>
    <portlet-preference>
        <name>usernamePassword</name>
        <readOnly>false</readOnly>
        <value>userGooglePassword</value>
    </portlet-preference>
    ...
    <user-attribute>
        <description>Google ID</description>
        <name>userGoogleAcct</name>
    </user-attribute>
    <user-attribute>
        <description>Google Password</description>
        <name>userGooglePassword</name>
    </user-attribute>
```
