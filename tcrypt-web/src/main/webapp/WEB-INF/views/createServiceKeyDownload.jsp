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