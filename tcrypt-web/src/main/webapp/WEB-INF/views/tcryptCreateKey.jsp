<%@ include file="/WEB-INF/views/taglibs.jsp"%>
<z:layout pageTitle="Credential Encryption">
<script type="text/javascript">
	$(function() {
		var availableServiceNames = ${serviceNames} ;
		$( "#serviceNames" ).autocomplete({
	        source: availableServiceNames
	    });
	});
</script>

	<form name="encryptToken" action="encrypt" method="post" autocomplete="off">
			<div>
				<span class="label" >Service Name : </span>
				<span><input type="text" name="encryptServiceName" id="serviceNames"/> </span>
			</div>
			<div>
				<span class="label">Text : </span>
				<span><textarea rows="5" cols="20" id="text" name="text"></textarea> </span>
			</div>
			<div>
				<span><input type="submit" name="encrypt" value="Encrypt"></span>
			</div>
	</form>
</z:layout>