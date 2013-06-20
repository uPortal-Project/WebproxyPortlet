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

var webproxyGatewayHandleRequest = function ($,contentRequests, index, formId) {
    var contentRequest = contentRequests[index];
    if (index == contentRequests.length-1) {
        if (contentRequest.form) {
            // In case a form is already on the page, remove it
            $("#"+formId).remove();
            var form = $(document.createElement("form"))
                .attr("id", formId)
                .attr("action", contentRequest.proxiedLocation)
                .attr("method", contentRequest.method);

            $.each(contentRequest.parameters, function (key, formFields) {
                $(formFields).each(function (idx, formField) {
                    form.append($(document.createElement("input")).attr("name", formFields.name).attr("value", formField.value)
                        .attr("type", "hidden"));
                });
            });

            form.appendTo("body");
            form.submit();

        } else {
            window.location = contentRequest.proxiedLocation;
        }
    }

    else if (contentRequest.form) {
        // TODO
    } else {
        var iframe = $(document.createElement("iframe"));
        iframe.load(function () {
            handleRequest(contentRequests, index+1);
        });
        iframe.attr("src", contentRequest.proxiedLocation);
    }
};

