<%@ include file="/WEB-INF/views/taglibs.jsp"%>
<z:layout pageTitle="Token Encryption">
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