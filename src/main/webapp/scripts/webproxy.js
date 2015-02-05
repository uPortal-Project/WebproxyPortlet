/*
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
var webproxyGatewayHandleRequest = function ($,data, index, formId) {
    var contentRequests = data.contentRequests;
    var contentRequest = contentRequests[index];
    if (index == contentRequests.length-1) {
        if (contentRequest.form) {
            // In case a form is already on the page, remove it
            $("#"+formId).remove();
            var form = $(document.createElement("form"))
                .attr("id", formId)
                .attr("action", contentRequest.proxiedLocation)
                .attr("method", contentRequest.method)
                .attr("class", "webproxy-gateway-form");

            $.each(contentRequest.parameters, function (key, formFields) {
                $(formFields).each(function (idx, formField) {
                    form.append($(document.createElement("input")).attr("name", formFields.name).attr("value", formField.value)
                        .attr("type", "hidden"));
                });
            });

            form.appendTo("body");

            if (data.javascriptFile && data.javascriptFile.length > 0) {
                var jsFileUrl = window.location.protocol + "//" + window.location.host + data.javascriptFile;
                $.ajax({
                    url: jsFileUrl,
                    dataType: "script",
                    success: function (script, textStatus, jqXHR ) {
                        eval(script);
                        gatewayPortletFormModifier($, form);
                        form.submit();
                    },
                    error: function (jqXHR, textStatus, errorThrown) {
                        form.after($(document.createElement("p")).attr("class", "error-message")
                            .text("Error fetching or executing " + jsFileUrl
                                + ". Error: " + textStatus + ", error thrown: " + errorThrown));
                    }
                });
//                $.getScript(jsFileUrl)
//                    .done(function( script, textStatus ) {
////                        try {
//                            gatewayPortletFormModifier($, form);
//                            form.submit();
////                        } case
//                    })
//                    .fail(function( jqxhr, settings, exception ) {
//                    });
            } else {
                form.submit();
            }

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

