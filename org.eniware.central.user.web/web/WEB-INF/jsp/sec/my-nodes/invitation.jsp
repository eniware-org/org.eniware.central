<p class="intro">
	<fmt:message key="my-Edges.invitation.intro"/>
</p>
<div class="alert alert-info alert-dismissible" role="alert">
	<button type="button" class="close" data-dismiss="alert" aria-label="<fmt:message key='close.label'/>"><span aria-hidden="true">&times;</span></button>
	<fmt:message key='my-Edges.eniwareedge.link.help'/>
</div>
<pre class="well">${details.confirmationKey}</pre>
<h3></h3>
<p class="alert alert-info">
	<strong><fmt:message key='my-Edges.invitation.securityPhrase.label'/>:</strong>
	${details.securityPhrase}
</p>
<div>
	<a href="<c:url value='/u/sec/my-Edges'/>" class="btn btn-primary"><fmt:message key='back.label'/></a>
</div>