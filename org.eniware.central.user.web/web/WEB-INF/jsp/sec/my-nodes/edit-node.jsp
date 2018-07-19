<spring:nestedPath path="userEdge">
	<form:hidden path="Edge.id"/>
	<form:hidden path="user.id"/>
	<fieldset>
		<div class="control-group">
			<label class="control-label" for="userEdge-id"><fmt:message key="user.Edge.id.label"/></label>
			<div class="controls">
				<span class="uneditable-input span2" id="userEdge-id">${userEdge.id}</span>
			</div>
		</div>
		<c:set var="errors"><form:errors path="name" cssClass="help-inline" element="span"/></c:set>
		<div class="control-group<c:if test='${not empty errors}'> error</c:if>">
			<label class="control-label" for="userEdge-name"><fmt:message key="user.Edge.name.label"/></label>
			<div class="controls">
				<form:input path="name" maxlength="128" cssClass="span3" id="userEdge-name"/>
				<span class="help-block"><fmt:message key="user.Edge.name.caption"/></span>
				<c:out value="${errors}" escapeXml="false"/>
			</div>
		</div>
		<c:set var="errors"><form:errors path="description" cssClass="help-inline" element="span"/></c:set>
		<div class="control-group<c:if test='${not empty errors}'> error</c:if>">
			<label class="control-label" for="userEdge-description"><fmt:message key="user.Edge.description.label"/></label>
			<div class="controls">
				<form:input path="description" maxlength="512" cssClass="span3" id="userEdge-description"/>
				<span class="help-block"><fmt:message key="user.Edge.description.caption"/></span>
				<c:out value="${errors}" escapeXml="false"/>
			</div>
		</div>
		<c:set var="errors"><form:errors path="requiresAuthorization" cssClass="help-inline" element="span"/></c:set>
		<div class="control-group<c:if test='${not empty errors}'> error</c:if>">
			<label class="control-label" for="userEdge-private"><fmt:message key="user.Edge.private.label"/></label>
			<div class="controls">
				<form:checkbox path="requiresAuthorization" id="userEdge-private"/>
				<span class="help-block"><fmt:message key="user.Edge.private.caption"/></span>
				<c:out value="${errors}" escapeXml="false"/>
			</div>
		</div>
	</fieldset>
</spring:nestedPath>
