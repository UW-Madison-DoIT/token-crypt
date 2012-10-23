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
function submit(element) {
	$('#filePath').val(element.id);
	document.forms[0].submit();
}
</script>
<body>
<form action="/download" method="post" name="downloadKey">
	<a id="${privateKey}" href="#" onclick="submit(this)">Private Key</a> <br/>
	<a id="${publicKey}" href="#" onclick="submit(this)">Public Key</a>
	<input type="hidden" name ="filePath" id="filePath"/>
</form>
</body>
</html>