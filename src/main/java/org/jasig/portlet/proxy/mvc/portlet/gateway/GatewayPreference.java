/**
 * Licensed to Jasig under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Jasig licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a
 * copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.jasig.portlet.proxy.mvc.portlet.gateway;

import org.jasig.portlet.proxy.service.IFormField;

/**
 * Simple DTO to hold a gateway preference editable by the user.
 *
 * @author James Wennmacher, jwennmacher@unicon.net
 */

public class GatewayPreference {
    String system;
    String logicalFieldName;
    String preferenceName;
    String fieldValue;
    boolean secured;

    public GatewayPreference(String system, String logicalFieldName, String preferenceName, String fieldValue, boolean secured) {
        this.system = system;
        this.logicalFieldName = logicalFieldName;
        this.preferenceName = preferenceName;
        this.fieldValue = fieldValue;
        this.secured = secured;
    }

    public String getSystem() {
        return system;
    }

    public void setSystem(String system) {
        this.system = system;
    }

    public String getLogicalFieldName() {
        return logicalFieldName;
    }

    public void setLogicalFieldName(String logicalFieldName) {
        this.logicalFieldName = logicalFieldName;
    }

    public String getPreferenceName() {
        return preferenceName;
    }

    public void setPreferenceName(String preferenceName) {
        this.preferenceName = preferenceName;
    }

    public String getFieldValue() {
        return fieldValue;
    }

    public void setFieldValue(String fieldValue) {
        this.fieldValue = fieldValue;
    }

    public boolean isSecured() {
        return secured;
    }

    public void setSecured(boolean secured) {
        this.secured = secured;
    }
}
