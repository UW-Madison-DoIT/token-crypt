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

<body>

<a href="/"> Create A Service Key </a>

<div>
<h3> Service Name: ${serviceName} </h2> 
<input type="text" width="150" value="${ encryptedText }"/></div>

</body>
</html>