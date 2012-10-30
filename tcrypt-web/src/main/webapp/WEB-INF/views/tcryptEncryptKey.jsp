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
<script type="text/javascript" src="<core:url value="js/jquery.zclip.min.js" />"></script>
<script type="text/javascript">
$(document).ready(function(){
    $('a#copy').zclip({
        path:'<core:url value="js/ZeroClipboard.swf" />',
        copy:$('#encrypted').val()
    });
});
</script>
<body>

<a href="/"> Create A Service Key </a>

<div>
<h3> Service Name: ${serviceName} </h2> 
<input type="text" width="150" id="encrypted" value="${ encryptedText }"/></div>
<a href="javascript:void(0);" id="copy">Copy to Clipboard</a>
</body>
</html>