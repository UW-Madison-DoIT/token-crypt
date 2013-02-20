<%@ include file="/WEB-INF/views/taglibs.jsp"%>
<z:layout pageTitle="Token Encryption">
    <script lang="javascript">
        $(document).ready(function() {
            $('#serviceNames').typeahead({
                name: 'serviceNames',
                remote: '${pageContext.request.contextPath}/apps/encryptionServices'
            });
        });
    </script>

    <div id="stylizedForm" class="userForms">
        <form name="encryptToken" action="${pageContext.request.contextPath}/apps/encrypt" method="post" autocomplete="off">
            <label>Service Name:</label>
            <input type="text" class="typeahead" name="serviceNames" id="serviceNames" placeholder="Find A Key" autocomplete="off"/>
            <label>Text :</label>
            <textarea rows="5" cols="20" id="text" name="text"></textarea>
            <button type="submit">Encrypt</button>
        </form>
    </div>
</z:layout>