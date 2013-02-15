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
					<h3>Service Key created for ${serviceName} <br/>
					<a id="${serviceName}" name="private" href="javascript:void(0);"
						onclick="submitForm(this); return false;">Private Key</a> <br /> 
						<a id="${serviceName}" name="public" href="javascript:void(0);" onclick="submitForm(this); return false;">Public Key</a> 
						<input type="hidden" name="serviceName" id="serviceName" /> 
						<input type="hidden" name="keyType" id="keyType" />
					</h3>

					<h2>
						The private key can only be downloaded now! <br /> Please take
						care to transfer and store the private key securely. <br /> If you
						need to restore a private key please contact the Administrator
						(whoever that is!)
					</h2>
				</div>
			</core:when>
		</core:choose>
	</form>
</z:layout>