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
					<span class="label">A service key has been created for: ${serviceName} </span>
					</div>
					<div>
					<span>
						<a id="${serviceName}" name="private" href="javascript:void(0);" onclick="submitForm(this); return false;">Private Key Download</a> 
						&nbsp;|&nbsp;
						<a id="${serviceName}" name="public" href="javascript:void(0);" onclick="submitForm(this); return false;">Public Key Download</a>
						<input type="hidden" name="serviceName" id="serviceName" /> 
						<input type="hidden" name="keyType" id="keyType" />
					</span>
					<span>
					<p>
						The private key can <b>only</b> be downloaded now! Please take
						care to transfer and store the private key securely.  If you
						need to restore a private key please contact the Administrator (whoever that is!)
					</p>
					</span>
				</div>
			</core:when>
		</core:choose>
	</form>
</z:layout>