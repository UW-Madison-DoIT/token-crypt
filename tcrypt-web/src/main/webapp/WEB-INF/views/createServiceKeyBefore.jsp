<%@ include file="/WEB-INF/views/taglibs.jsp"%>
<z:layout pageTitle="Token Encryption">
	
	
	<form name="createServiceKey" action="${pageContext.request.contextPath}/apps/create" method="post" autocomplete="off">
			<div class="labelDiv">
				<span class="label" >Service Name : </span>
				<span>
					<input type="text" name="serviceName" id="serviceName"/> 
				</span>
			</div>
			<div>
				<span class="label">Key Length : </span>
				<span style="padding-left: 17px"><select name="keyLength">
					<option>2048</option>
				</select> </span>
			</div>
			<div>
				<span><input type="submit" name="create" value="Create Service Key"></span>
			</div>
	</form>
</z:layout>