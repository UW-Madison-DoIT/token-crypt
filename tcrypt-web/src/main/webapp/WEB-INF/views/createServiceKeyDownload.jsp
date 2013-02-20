<%@ include file="/WEB-INF/views/taglibs.jsp"%>

<z:layout pageTitle="Token Encryption">

	<script type="text/javascript">
	function submitForm(element) {
		$('#serviceName').val(element.id);
		$('#keyType').val(element.name);
		document.forms[0].submit();
	}
	</script>
	<form action="${pageContext.request.contextPath}/apps/download" method="post" name="downloadKey">
			<core:choose>
			<core:when test="${empty error}">
				<div id="success">
					<div>
						<span class="label"><spring:message code="label.newServiceKey" />:</span> <span>${serviceName} </span>
					</div>
					<div>
						<span>
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
						<span>
						<p>
							<spring:message code="message.createKeySuccessWarning" />
						</p>
						</span>
					</div>
				</div>
			</core:when>
		</core:choose>
	</form>
</z:layout>