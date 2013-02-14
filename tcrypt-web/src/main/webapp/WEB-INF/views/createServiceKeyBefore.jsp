<%@ include file="/WEB-INF/views/taglibs.jsp"%>
<z:layout pageTitle="Credential Encryption">
	<form name="createServiceKey" action="${pageContext.request.contextPath}/apps/create" method="post" autocomplete="off">
			<div>
				<span class="label" >Service Name : </span>
				<span><input type="text" name="createServiceName" id="createServiceName"/> </span>
			</div>
			<div>
				<span class="label">Key Length : </span>
				<span><select name="keyLength">
					<option>2048</option>
				</select> </span>
			</div>
			<div>
				<span><input type="submit" name="encrypt" value="Create Service Key"></span>
			</div>
	</form>
</z:layout>