<%@ attribute name="pageTitle" required="true" type="java.lang.String"%>
<%@ include file="/WEB-INF/views/taglibs.jsp"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<link rel="stylesheet" href="http://code.jquery.com/ui/1.9.1/themes/base/jquery-ui.css" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/typeahead.min.css"/>
<link rel="icon" type="image/gif" href="${pageContext.request.contextPath}/images/favicon.ico" />
<script type="text/javascript" src="http://code.jquery.com/jquery-1.9.1.min.js"></script>
<script type="text/javascript" src="http://code.jquery.com/ui/1.9.1/jquery-ui.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/typeahead.min.js"></script>
<title>${pageTitle} | University of Wisconsin-Madison</title>

<link rel="stylesheet"	href="${pageContext.request.contextPath}/css/main_no_top_nav.css" type="text/css" media="all" />
<link rel="stylesheet"	href="${pageContext.request.contextPath}/css/site.css" type="text/css" media="all" />
<!--[if IE 6]>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/ie6.css" type="text/css" media="screen" />
  <![endif]-->
<!--[if IE 7]>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/ie7.css" type="text/css" media="screen" />
  <![endif]-->
<!--[if IE 8]>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/ie8.css" type="text/css" media="screen" />
  <![endif]-->

</head>
<body>
	<div class="wrap">
		<div id="home">
			<div id="header">
				<div class="skip">
					<a href="#content" accesskey="S">Skip to main content</a>
				</div>
				
				<a id="uwhome" href="http://www.wisc.edu"><img src="${pageContext.request.contextPath}/images/wordmark.gif" alt="University of Wisconsin-Madison" width="260" height="11" /></a>
                <a id="crest" href="http://www.wisc.edu"><img src="${pageContext.request.contextPath}/images/crest.png" alt="UW-Madison crest." width="70" height="106" /></a>

				<div id="siteTitle">
					<h1>
						<a href="../apps/encrypt"><span><img	src="${pageContext.request.contextPath}/images/b_tokenEncyrption.png" alt="Token Encryption" /></span></a>
					</h1>
					<div id="tagline">
						<span>Encrypting your tokens, one line at a time.</span>
					</div>
				</div>

				<ul id="globalnav">
					<li id="uwsearch"><a href="http://www.wisc.edu/search/">UW Search</a></li>
					<li><a href="http://my.wisc.edu">My UW</a></li>
					<li><a href="http://map.wisc.edu">Map</a></li>
					<li id="last_tool"><a href="http://www.today.wisc.edu">Calendar</a></li>
				</ul>
<%--
				<form id="search" action="post">
					<div>
						<label for="searchstring">Search this site: </label> 
						<input name="searchstring" id="searchstring" type="text" value="" />
						<input name="submit" id="submit" type="submit" value="Go!" />
					</div>
				</form>
--%>
			</div>
			<div id="shell">
				<!--     <ol id="breadcrumbs"><li><a href="/">Home</a></li><li><a href="/topic/">Topic</a></li></ol> -->
				<div id="sidebar" class="col">

					<ul id="secondary-nav">
						<li>
							<core:url value="/apps/encrypt" var="EncryptTokenUrl" />
							<a href="${EncryptTokenUrl}">Encrypt Token</a>
						</li>
						<li>
						    <core:url value="/apps/create" var="HomeTokenUrl" />
							<a href="${HomeTokenUrl}">Create Service Key</a>
						</li>
					</ul>
				</div>
				<div id="content" class="main col">
				    <core:if test="${not empty errorMessage}">
					   	<div class="error">
					   		Error: <spring:message code="${errorMessage}" arguments="${zero}" />
					   	</div>
					</core:if>
				    <jsp:doBody />
				</div>
			</div>
		</div>

		<div id="footer">
			<p>DoIT, 1210 W Dayton St, Madison, WI 53706 | 608-262-1204</p>
			<p>
				<a href="http://www.doit.wisc.edu/feedback.aspx">Contact Us</a>
			</p>
			<p>
				&copy; 2013 Board of Regents of the <a href="http://www.wisconsin.edu">University of Wisconsin System</a>
			</p>
		</div>

	</div>
</body>
</html>