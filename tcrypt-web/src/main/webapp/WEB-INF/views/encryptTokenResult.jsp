<%@ include file="/WEB-INF/views/taglibs.jsp"%>
<z:layout pageTitle="Token Encryption">
	<div>
		<span class="label"> Service Name: ${serviceName} </span> 
		<span><input type="text" size="60" id="encrypted" value="${ encryptedText }"/>
		</span>
	</div>
	
	<div>
		<span><a href="#" id="copy-encrypted">Copy to Clipboard</a><img class="check" src="${ pageContext.request.contextPath }/images/checkmark.png" style="display : none" alt='copied' /></span>
	</div>
	<script type="text/javascript">
	$(document).ready(function() {
		$("#copy-encrypted").zclip({
		    path: "${ pageContext.request.contextPath }/js/ZeroClipboard.swf",
		    copy:function(){return $('input#encrypted').val();
			},
			afterCopy:function(){
	            $(this).next('.check').show();
	        }
		});
	});

	</script>
</z:layout>