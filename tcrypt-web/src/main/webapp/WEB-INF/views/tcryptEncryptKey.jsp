<%@ taglib prefix="z" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="core" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<z:layout pageTitle="Encrypt Key">
	<script type="text/javascript">
	$(document).ready(function(){
	    $('a#copy').zclip({
	        path:'<core:url value="js/ZeroClipboard.swf" />',
	        copy:$('#encrypted').val()
	    });
	});
	</script>	
	<div>
	<span class="label"> Service Name: ${serviceName} </span> 
	<span><input type="text" width="150" id="encrypted" value="${ encryptedText }"/>
	</span>
	</div>
	
	<div>
		<span><a href="javascript:void(0);" id="copy">Copy to Clipboard</a></span>
	</div>
</z:layout>