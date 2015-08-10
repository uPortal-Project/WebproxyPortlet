<%--

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

--%>
<%@ include file="/WEB-INF/jsp/include.jsp" %>
<portlet:actionURL var="formUrl" escapeXml="false"/>
<c:set var="n"><portlet:namespace/></c:set>

<style type="text/css">
    #${n}proxyConfig ul {
        list-style-type: none;
    }
</style>

<!-- Portlet -->
<div id="${n}proxyConfig" class="fl-widget portlet" role="section">
    
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
                    <label>Content source:</label>
                    <ul>
                        <li><form:radiobutton path="contentService" value="httpContentService"/> HTTP (Web-based document)</li>
                        <li><form:radiobutton path="contentService" value="fileContentService"/> File in the portlet's classpath</li>
                    </ul>
                </p>

                <p>
                    <label>Content location (fully-qualified URL or classpath location):</label><br/>
                    <form:input path="location" type="text" size="80"/>
                </p>
                <p>
                    <label>Content location for Maximised view:</label><br/>
                    <form:input path="maxLocation" type="text" size="80"/>
                </p>
                <div class="instructions form-group">
                    <div class="collapse" id="advancedUrlOptions">
                        <div class="well">
                            <spring:message code="edit.proxy.url.instructions"/>
                            <ol>
                                <li><spring:message code="edit.proxy.url.instructions.ex1"/></li>
                                <li><spring:message code="edit.proxy.url.instructions.ex2"/></li>
                                <li><spring:message code="edit.proxy.url.instructions.ex3"/></li>
                            </ol>
                        </div>
                    </div>
                    <a class="btn btn-info" href="javascript:void(0)" data-toggle="collapse" data-target="#advancedUrlOptions" aria-expanded="false" aria-controls="advancedUrlOptions">
                        <spring:message code="edit.proxy.url.advancedUrlOptions"/>
                    </a>
                </div>
                <p>
                <label>Page Encoding Format:</label>&nbsp;
                    <form:select path="pageCharacterEncodingFormat">
                        <form:options items="${pageCharacterEncodings}"/>
                    </form:select>
                </p>
                
                <p>
                    <label>Authentication:</label>
                    <ul>
                        <li><form:radiobutton path="authType" value="NONE"/> None</li>
                        <li><form:radiobutton path="authType" value="CAS"/> CAS</li>
                        <li><form:radiobutton path="authType" value="BASIC"/> Basic</li>
                    </ul>
                </p>
                
                <p>
                    <label>Whitelist Regex to partially-matching URLs to rewrite to proxy through portlet;</label><br/>
                    e.g. /news/ matches http://www.my.edu/news/academic: <br/>
                    <form:input path="whitelistRegexes" type="text" size="80"/>
                </p>
                
                <p>
                    <label>Content clipping HTML element name (leave blank to avoid clipping);</label><br/>
                    e.g. body will return everything inside &lt;body&gt; ... &lt;/body&gt;:<br/>
                    <form:input path="clippingSelector" type="text" size="80"/>
                </p>

                <p>
                    <label>Static HTML header content (cannot include inline Javascript):</label><br/>
                    <form:textarea path="header" rows="5" cols="80"/>
                </p>

                <p>
                    <label>Static HTML footer content (cannot include inline Javascript):</label><br>
                    <form:textarea path="footer" rows="5" cols="80"/>
                </p>

                <p>
                    <label>Page Search Strategies</label><br>
                    <form:checkboxes path="searchStrategies" items="${strategyNames }" />
                </p>
                
                <p>
                    <label>Google Search Appliance Host</label><br>
                    <form:input path="gsaHost" type="text" size="80"/>
                </p>
                
                <p>
                    <label>Google Search Appliance Collection</label><br>
                    <form:input path="gsaCollection" type="text" size="80"/>
                </p>
                
                <p>
                    <label>Google Search Appliance Frontend</label><br>
                    <form:input path="gsaFrontend" type="text" size="80"/>
                </p>
                
                <p>
                    <label>Google Search Appliance Whitelist Regex</label><br>
                    <form:input path="gsaWhitelistRegex" type="text" size="80"/>
                </p>
                
                <p>
                    <label>Anchor Strategy Whitelist Regex</label><br>
                    <form:input path="anchorWhitelistRegex" type="text" size="80"/>
                </p>
                
                					                
                <div class="buttons">
                    <input class="button primary" type="submit" name="Save" value="<spring:message code="save"/>"/>
                    <input class="button" type="submit" name="Cancel" value="<spring:message code="edit.proxy.cancel"/>"/>
                </div>

            </form:form>
            
        </div>
    </div>

    </div> <!-- end: portlet-content -->
</div> <!-- end: portlet -->
