<%--

    Copyright 2012, Board of Regents of the University of
    Wisconsin System. See the NOTICE file distributed with
    this work for additional information regarding copyright
    ownership. Board of Regents of the University of Wisconsin
    System licenses this file to you under the Apache License,
    Version 2.0 (the "License"); you may not use this file
    except in compliance with the License. You may obtain a
    copy of the License at:

    http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing,
    software distributed under the License is distributed on
    an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
    KIND, either express or implied. See the License for the
    specific language governing permissions and limitations
    under the License.

--%>
<%@ include file="/WEB-INF/views/taglibs.jsp"%>
<z:layout pageTitle="Token Encryption">

    
        <form name="createServiceKey" action="${pageContext.request.contextPath}/apps/create" method="post" autocomplete="off">
        	<ul class="center">
	        	<li>
					<label>Service Name</label>
					<div>
						<input type="text" name="serviceName" id="serviceName" />
					</div>
				</li>
				<li>
					<label>Key Length</label>
					<div>
						<select name="keyLength" id="keyLength">
							<option>2048</option>
						</select>
					</div>
				</li>
				<li>
					<div>
						<button>Create Service Key</button>
					</div>
				</li>
			</ul>
    	</form>
    

</z:layout>