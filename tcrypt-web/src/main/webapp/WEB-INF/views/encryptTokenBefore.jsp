<%@ include file="/WEB-INF/views/taglibs.jsp"%>
<z:layout pageTitle="Token Encryption">
    <script lang="javascript">
        $('#serviceNames').typeahead({
            name: 'serviceNames',
            local: ["Tim", "Eric", "Brad"]
        });
    </script>

    <div id="stylizedForm" class="userForms">
        <form name="encryptToken" action="${pageContext.request.contextPath}/apps/encrypt" method="post">
            <label>Service Name:</label>
            <input type="text" class="typeahead" name="serviceNames" id="serviceNames" placeholder="Find A Key"/>
            <label>Text :</label>
            <textarea rows="5" cols="20" id="text" name="text"></textarea>
            <button type="submit">Encrypt</button>
        </form>
    </div>
</z:layout>