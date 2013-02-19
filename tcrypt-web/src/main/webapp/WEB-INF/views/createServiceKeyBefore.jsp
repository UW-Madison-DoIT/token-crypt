<%@ include file="/WEB-INF/views/taglibs.jsp"%>
<z:layout pageTitle="Token Encryption">

    <div id="stylizedForm" class="userForms">
        <form name="createServiceKey" action="${pageContext.request.contextPath}/apps/create" method="post" autocomplete="off">
				<label>Service Name :</label>
				<input type="text" name="serviceName" id="serviceName"/>
				<label>Key Length :</label>
				<select name="keyLength" id="keyLength">
					<option>2048</option>
				</select>
				<button>Create Service Key</button>
    	</form>
    </div>

</z:layout>