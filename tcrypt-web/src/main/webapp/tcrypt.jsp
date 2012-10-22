<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Credential Encryption</title>
</head>
<body>
	<form name="createServiceKey" action="/create" method="post">
		<div>
			Service Name : <input type="text" name="serviceName">
		</div>
		<div>
			KeyLength : <Select><option value="2048">2048</option></Select>
		</div>
		<input type="button" name="create" value="Create Service Key">
	</form>
</body>
</html>