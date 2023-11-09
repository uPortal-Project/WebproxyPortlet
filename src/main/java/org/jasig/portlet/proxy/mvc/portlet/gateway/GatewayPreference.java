/**
 * Licensed to Apereo under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Apereo licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License.  You may obtain a
 * copy of the License at the following location:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jasig.portlet.proxy.mvc.portlet.gateway;

/**
 * Simple DTO to hold a gateway preference editable by the user.
 *
 * @author James Wennmacher, jwennmacher@unicon.net
 * @version $Id: $Id
 */
@Deprecated // No known usage of this portlet type. Remove when we jump to Java 17.
public class GatewayPreference {
    String system;
    String logicalFieldName;
    String preferenceName;
    String fieldValue;
    boolean secured;

    /**
     * <p>Constructor for GatewayPreference.</p>
     *
     * @param system a {@link java.lang.String} object
     * @param logicalFieldName a {@link java.lang.String} object
     * @param preferenceName a {@link java.lang.String} object
     * @param fieldValue a {@link java.lang.String} object
     * @param secured a boolean
     */
    public GatewayPreference(String system, String logicalFieldName, String preferenceName, String fieldValue, boolean secured) {
        this.system = system;
        this.logicalFieldName = logicalFieldName;
        this.preferenceName = preferenceName;
        this.fieldValue = fieldValue;
        this.secured = secured;
    }

    /**
     * <p>Getter for the field <code>system</code>.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getSystem() {
        return system;
    }

    /**
     * <p>Setter for the field <code>system</code>.</p>
     *
     * @param system a {@link java.lang.String} object
     */
    public void setSystem(String system) {
        this.system = system;
    }

    /**
     * <p>Getter for the field <code>logicalFieldName</code>.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getLogicalFieldName() {
        return logicalFieldName;
    }

    /**
     * <p>Setter for the field <code>logicalFieldName</code>.</p>
     *
     * @param logicalFieldName a {@link java.lang.String} object
     */
    public void setLogicalFieldName(String logicalFieldName) {
        this.logicalFieldName = logicalFieldName;
    }

    /**
     * <p>Getter for the field <code>preferenceName</code>.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getPreferenceName() {
        return preferenceName;
    }

    /**
     * <p>Setter for the field <code>preferenceName</code>.</p>
     *
     * @param preferenceName a {@link java.lang.String} object
     */
    public void setPreferenceName(String preferenceName) {
        this.preferenceName = preferenceName;
    }

    /**
     * <p>Getter for the field <code>fieldValue</code>.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getFieldValue() {
        return fieldValue;
    }

    /**
     * <p>Setter for the field <code>fieldValue</code>.</p>
     *
     * @param fieldValue a {@link java.lang.String} object
     */
    public void setFieldValue(String fieldValue) {
        this.fieldValue = fieldValue;
    }

    /**
     * <p>isSecured.</p>
     *
     * @return a boolean
     */
    public boolean isSecured() {
        return secured;
    }

    /**
     * <p>Setter for the field <code>secured</code>.</p>
     *
     * @param secured a boolean
     */
    public void setSecured(boolean secured) {
        this.secured = secured;
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((fieldValue == null) ? 0 : fieldValue.hashCode());
        result = prime
                * result
                + ((logicalFieldName == null) ? 0 : logicalFieldName.hashCode());
        result = prime * result
                + ((preferenceName == null) ? 0 : preferenceName.hashCode());
        result = prime * result + (secured ? 1231 : 1237);
        result = prime * result + ((system == null) ? 0 : system.hashCode());
        return result;
    }

    /** {@inheritDoc} */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        GatewayPreference other = (GatewayPreference) obj;
        if (fieldValue == null) {
            if (other.fieldValue != null)
                return false;
        } else if (!fieldValue.equals(other.fieldValue))
            return false;
        if (logicalFieldName == null) {
            if (other.logicalFieldName != null)
                return false;
        } else if (!logicalFieldName.equals(other.logicalFieldName))
            return false;
        if (preferenceName == null) {
            if (other.preferenceName != null)
                return false;
        } else if (!preferenceName.equals(other.preferenceName))
            return false;
        if (secured != other.secured)
            return false;
        if (system == null) {
            if (other.system != null)
                return false;
        } else if (!system.equals(other.system))
            return false;
        return true;
    }

}
