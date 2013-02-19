<%@ include file="/WEB-INF/views/taglibs.jsp"%>
<z:layout pageTitle="Token Encryption">
<script type="text/javascript">
    $(function() {
		var availableServiceNames = ${serviceNames} ;
		$( "#serviceNames" ).autocomplete({
	        source: availableServiceNames
	    });
	});
</script>

    <div id="stylizedForm" class="userForms">
        <form name="encryptToken" action="${pageContext.request.contextPath}/apps/encrypt" method="post">
            <label>Service Name:</label>
            <input type="text" name="serviceNames" id="serviceNames"/>
            <label>Text :</label>
            <textarea rows="5" cols="20" id="text" name="text"></textarea>
            <button type="submit">Encrypt</button>
        </form>
    </div>

</z:layout>