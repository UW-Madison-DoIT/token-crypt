<%@ include file="/WEB-INF/views/taglibs.jsp"%>
<z:layout pageTitle="Token Encryption">
    <div id="stylizedForm" class="userForms">
        <form name="encryptToken" action="${pageContext.request.contextPath}/apps/encrypt" method="post" autocomplete="off">
            <label>Service Name:</label>
            <select id="serviceNames">
                <core:forEach var="name" items="${serviceNames}">
                    <option value ="<core:out value="${name}"/>"><core:out value="${name}"/></option>
                </core:forEach>
            </select>
            <label>Text :</label>
            <textarea rows="5" cols="20" id="text" name="text"></textarea>
            <button type="submit">Encrypt</button>
        </form>
    </div>
</z:layout>