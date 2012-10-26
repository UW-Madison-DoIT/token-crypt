<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    <%@ taglib prefix="core" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Credential Encryption</title>
</head>
<script type="text/javascript" src="http://code.jquery.com/jquery-1.8.2.min.js"></script>
<script type="text/javascript">
function submitForm(element) {
	$('#serviceName').val(element.id);
	$('#keyType').val(element.name);
	document.forms[0].submit();
}
</script>
<body>

<a href="/"> Create A Service Key </a>

<form action="/download" method="post" name="downloadKey">
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
			<core:otherwise>
				<div id="error">
				<h2> Oops! Something's gonna bit wrong! <br/> </h2>
				<h3>${error}</h3>
				</div>
			</core:otherwise>
		</core:choose>
</form>
</body>
</html>