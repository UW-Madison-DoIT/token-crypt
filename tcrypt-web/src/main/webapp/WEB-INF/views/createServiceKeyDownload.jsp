<%--

    Copyright 2012, Board of Regents of the University of
    Wisconsin System. See the NOTICE file distributed with
    this work for additional information regarding copyright
    ownership. Board of Regents of the University of Wisconsin
    System licenses this file to you under the Apache License,
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
<%@ include file="/WEB-INF/views/taglibs.jsp"%>

<z:layout pageTitle="Token Encryption">
	<div id="success">
		<div>
			<span class="label"><spring:message code="label.newServiceKey" />:</span> <span>${serviceName}</span>
		</div>
		<p/>
		<div>
			<span class="keyDownloads">
				<core:url value="/apps/download" var="publicKeyUrl">
					<core:param name="serviceName" value="${serviceName}"/>
					<core:param name="keyType" value="public"/>
				</core:url>
				<core:url value="/apps/download" var="privateKeyUrl">
					<core:param name="serviceName" value="${serviceName}"/>
					<core:param name="keyType" value="private"/>
				</core:url>
				<a href="${publicKeyUrl }" target="_blank"><spring:message code="label.publicKeyDownload" /></a>
				&nbsp;|&nbsp;
				<a href="${privateKeyUrl }" target="_blank"><spring:message code="label.privateKeyDownload" /></a>
			</span>
		</div>
		<p class="warning">
			<spring:message code="message.createKeySuccessWarning" />
		</p>
		
	</div>
</z:layout>