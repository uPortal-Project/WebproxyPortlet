<%--

    Licensed to Jasig under one or more contributor license
    agreements. See the NOTICE file distributed with this work
    for additional information regarding copyright ownership.
    Jasig licenses this file to you under the Apache License,
    Version 2.0 (the "License"); you may not use this file
    except in compliance with the License. You may obtain a
    copy of the License at:

    http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing,
    software distributed under the License is distributed on
    an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
    KIND, either express or implied. See the License for the
    specific language governing permissions and limitations
    under the License.

--%>

<%@ include file="/WEB-INF/jsp/include.jsp" %>
<portlet:actionURL var="formUrl" escapeXml="false"/>
<c:set var="n"><portlet:namespace/></c:set>

<!-- Portlet -->
<div class="fl-widget portlet" role="section">
    
    <!-- Portlet Titlebar -->
    <div class="fl-widget-titlebar titlebar portlet-titlebar" role="sectionhead">
        <h2 class="title" role="heading">
            <spring:message code="edit.portlet"/>
        </h2>
    </div>
    
    <!-- Portlet Content -->
    <div class="fl-widget-content content portlet-content" role="main">

        <!-- Portlet Section -->
        <div class="portlet-section" role="region">
            <div class="content">

            <form:form modelAttribute="form" action="${formUrl}" method="POST">
            
                <p>
                    Content source:
                    <ul>
                        <li><form:radiobutton path="contentService" value="httpContentService"/> HTTP (Web-based document)</li>
                        <li><form:radiobutton path="contentService" value="fileContentService"/> File in the portlet's classpath</li>
                    </ul>
                </p>

                <p>
                    Content location (fully-qualified URL or classpath location):<br/>
                    <form:input path="location" type="text"/>
                </p>
                
                <p>
                    Authentication:
                    <ul>
                        <li><form:radiobutton path="authType" value="NONE"/> None</li>
                        <li><form:radiobutton path="authType" value="CAS"/> CAS</li>
                        <li><form:radiobutton path="authType" value="BASIC"/> Basic</li>
                    </ul>
                </p>
                
                <p>
                    Whitelist:<br/>
                    <form:input path="whitelistRegexes" type="text"/>
                </p>
                
                <p>
                    Content clipping selector (leave blank to avoid clipping):
                    <form:input path="clippingSelector" type="text"/>
                </p>

                <p>
                    Static header:
                    <form:input path="header" type="text"/>
                </p>

                <p>
                    Static footer:
                    <form:input path="footer" type="text"/>
                </p>

                <div class="buttons">
                    <input class="button primary" type="submit" value="<spring:message code="save"/>"/>
                </div>

            </form:form>
            
        </div>
    </div>

    </div> <!-- end: portlet-content -->
</div> <!-- end: portlet -->
