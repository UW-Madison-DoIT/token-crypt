<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
	 <%@ taglib prefix="core" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">

<title>Credential Encryption</title>

<link rel="stylesheet" href="http://code.jquery.com/ui/1.9.1/themes/base/jquery-ui.css" />
<script type="text/javascript" src="http://code.jquery.com/jquery-1.8.2.min.js"></script>
<script type="text/javascript" src="http://code.jquery.com/ui/1.9.1/jquery-ui.js"></script>

<script type="text/javascript">
	$(function() {
		var availableServiceNames = ${serviceNames} ;
		$( "#serviceNames" ).autocomplete({
	        source: availableServiceNames
	    });
	});
</script>
</head>
<body>
	
	<form name="createServiceKey" action="create" method="post" autocomplete="off">
			Service Name : <input type="text" name="createServiceName"/>
			KeyLength : <Select name="keyLength"><option value="2048">2048</option></Select>
			<input type="submit" name="create" value="Create Service Key">
	</form>
	
	<form name="encryptToken" action="encrypt" method="post" autocomplete="off">
			Service Name : <input type="text" name="encryptServiceName" id="serviceNames"/> <br/>
			Text : <textarea rows="20" cols="20" id="text" name="text"></textarea> <br/>
			<input type="submit" name="encrypt" value="Encrypt">
	</form>
</body>
</html>