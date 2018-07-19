<%--
	Input parameters:
	
		userAuthTokens - collection of "user" UserAuthToken objects
		dataAuthTokens - collection of "data" UserAuthToken objects
		userEdges      - collection of UserEdge objects
 --%>
<p class="intro">
	<fmt:message key='auth-tokens.intro'/>
</p>

<section id="user-auth-tokens">
<h2>
	<fmt:message key='auth-tokens.user.title'/>
	<a href="#create-user-auth-token" class="btn btn-primary pull-right" data-toggle="modal" 
		title="<fmt:message key='auth-tokens.action.create'/>">
		<i class="glyphicon glyphicon-plus"></i>
	</a>
</h2>
<p>
	<fmt:message key='auth-tokens.user.intro'/>
</p>
<c:choose>
	<c:when test="${fn:length(userAuthTokens) eq 0}">
		<fmt:message key='auth-tokens.user.none'/>
	</c:when>
	<c:otherwise>
		<table class="table">
			<thead>
				<tr>
					<th><fmt:message key='auth-tokens.label.token'/></th>
					<th><fmt:message key='auth-tokens.label.created'/></th>
					<th><fmt:message key='auth-tokens.label.status'/></th>
					<th></th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${userAuthTokens}" var="token">
				<tr>
					<td class="monospace"><c:out value='${token.authToken}'/></td>
					<td>
						<joda:dateTimeZone value="GMT">
							<joda:format value="${token.created}"
								 pattern="dd MMM yyyy"/> GMT
						</joda:dateTimeZone>
					</td>
					<td>
						<span class="label label-${token.status eq 'Active' ? 'success' : 'warning'}">
							<fmt:message key='auth-tokens.label.status.${token.status}'/>
						</span>
					</td>
					<td>
						<form class="action-user-token">
							<c:set var="tokenId"><c:out value='${token.authToken}'/></c:set>
							<input type="hidden" name="id" value="${tokenId}"/>
							<button type="button" class="btn btn-small btn-default user-token-change-status" 
								data-status="${token.status}"
								data-action="<c:url value='/u/sec/auth-tokens/changeStatus'/>">
								<fmt:message key="auth-tokens.action.${token.status eq 'Active' ? 'disable' : 'enable'}"/>
							</button>
							<button type="button" class="btn btn-small btn-default user-token-delete" title="<fmt:message key='auth-tokens.action.delete'/>">
								<i class="glyphicon glyphicon-trash"></i>
							</button>
							<sec:csrfInput/>
						</form>
					</td>
				</tr>
				</c:forEach>
			</tbody>
		</table>
	</c:otherwise>
</c:choose>
</section>

<c:if test="${not empty userEdges}">
	<section id="data-auth-tokens">
	<h2>
		<fmt:message key='auth-tokens.data.title'/>
		<a href="#create-data-auth-token" class="btn btn-primary pull-right" data-toggle="modal" 
			title="<fmt:message key='auth-tokens.action.create'/>">
			<i class="glyphicon glyphicon-plus"></i>
		</a>
	</h2>
	<p>
		<fmt:message key='auth-tokens.data.intro'/>
	</p>
	<c:choose>
		<c:when test="${fn:length(dataAuthTokens) eq 0}">
			<fmt:message key='auth-tokens.data.none'/>
		</c:when>
		<c:otherwise>
			<table class="table">
				<thead>
					<tr>
						<th><fmt:message key='auth-tokens.label.token'/></th>
						<th><fmt:message key='auth-tokens.label.policy'/></th>
						<th><fmt:message key='auth-tokens.label.created'/></th>
						<th><fmt:message key='auth-tokens.label.status'/></th>
						<th></th>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${dataAuthTokens}" var="token">
					<tr>
						<td class="monospace"><c:out value='${token.authToken}'/></td>
						<td>
							<c:if test="${not empty token.policy}">
								<dl>
									<c:if test="${fn:length(token.policy.EdgeIds) gt 0}">
										<dt><fmt:message key='auth-tokens.label.Edges'/></dt>
										<dd>
											<c:forEach items="${token.policy.EdgeIds}" var="EdgeId" varStatus="EdgeIdStatus">
												${EdgeId}<c:if test="${not EdgeIdStatus.last}">, </c:if>
											</c:forEach>
										</dd>
									</c:if>
									<c:if test="${fn:length(token.policy.sourceIds) gt 0}">
										<dt><fmt:message key='auth-tokens.label.sources'/></dt>
										<dd>
											<c:forEach items="${token.policy.sourceIds}" var="sourceId" varStatus="sourceIdStatus">
												${sourceId}<c:if test="${not sourceIdStatus.last}">, </c:if>
											</c:forEach>
										</dd>
									</c:if>
									<c:if test="${not empty token.policy.minAggregation}">
										<dt><fmt:message key='auth-tokens.label.minAggregation'/></dt>
										<dd>
											<fmt:message key='aggregation.${token.policy.minAggregation}.label'/>
										</dd>
									</c:if>
									<c:if test="${fn:length(token.policy.EdgeMetadataPaths) gt 0}">
										<dt><fmt:message key='auth-tokens.label.EdgeMetadataPaths'/></dt>
										<dd>
											<c:forEach items="${token.policy.EdgeMetadataPaths}" var="EdgeMetadataPath" varStatus="EdgeMetadataPathStatus">
												${EdgeMetadataPath}<c:if test="${not EdgeMetadataPathStatus.last}">, </c:if>
											</c:forEach>
										</dd>
									</c:if>
									<c:if test="${fn:length(token.policy.userMetadataPaths) gt 0}">
										<dt><fmt:message key='auth-tokens.label.userMetadataPaths'/></dt>
										<dd>
											<c:forEach items="${token.policy.userMetadataPaths}" var="userMetadataPath" varStatus="EdgeMetadataPathStatus">
												${userMetadataPath}<c:if test="${not userMetadataPathStatus.last}">, </c:if>
											</c:forEach>
										</dd>
									</c:if>
								</dl>
							</c:if>
						</td>
						<td>
							<joda:dateTimeZone value="GMT">
								<joda:format value="${token.created}"
									 pattern="dd MMM yyyy"/> GMT
							</joda:dateTimeZone>
						</td>
						<td>
							<span class="label label-${token.status eq 'Active' ? 'success' : 'warning'}">
								<fmt:message key='auth-tokens.label.status.${token.status}'/>
							</span>
						</td>
						<td>
							<form class="action-data-token">
								<c:set var="tokenId"><c:out value='${token.authToken}'/></c:set>
								<input type="hidden" name="id" value="${tokenId}"/>
								<button type="button" class="btn btn-small btn-default data-token-change-status" 
									data-status="${token.status}"
									data-action="<c:url value='/u/sec/auth-tokens/changeStatus'/>">
									<fmt:message key="auth-tokens.action.${token.status eq 'Active' ? 'disable' : 'enable'}"/>
								</button>
								<button type="button" class="btn btn-small btn-default data-token-delete" title="<fmt:message key='auth-tokens.action.delete'/>">
									<i class="glyphicon glyphicon-trash"></i>
								</button>
								<sec:csrfInput/>
							</form>
						</td>
					</tr>
					</c:forEach>
				</tbody>
			</table>
		</c:otherwise>
	</c:choose>
	</section>
</c:if>

<form id="create-user-auth-token" class="modal fade" action="<c:url value='/u/sec/auth-tokens/generateUser'/>" method="post">
	<div class="modal-dialog">
		<div class="modal-content">
		 	<div class="modal-header">
		 		<button type="button" class="close" data-dismiss="modal">&times;</button>
		 		<h4 class="modal-title"><fmt:message key='auth-tokens.user.create.title'/></h4>
		 	</div>
		 	<div class="modal-body">
		 		<p class="before"><fmt:message key='auth-tokens.user.create.intro'/></p>
		 		<div class="after">
		 			<p><fmt:message key='auth-tokens.created.intro'/></p>
			 		<table class="table">
			 			<thead>
			 				<tr>
			 					<th><fmt:message key='auth-tokens.label.token'/></th>
			 					<th class="text-danger"><fmt:message key='auth-tokens.label.secret'/></th>
			 				</tr>
			 			</thead>
			 			<tbody>
			 				<tr>
			 					<td class="monospace result-token"></td>
			 					<td class="monospace result-secret text-danger"></td>
			 				</tr>
			 			</tbody>
			 		</table>
			 		<div class="alert alert-danger">
			 			<strong><fmt:message key='auth-tokens.created.reiterate.title'/></strong>
			 			<fmt:message key='auth-tokens.created.reiterate'/>
			 		</div>
		 		</div>
		 	</div>
		 	<div class="modal-footer">
		 		<a href="#" class="btn btn-default" data-dismiss="modal"><fmt:message key='close.label'/></a>
		 		<button type="submit" class="btn btn-primary before">
		 			<fmt:message key='auth-tokens.action.create'/>
		 		</button>
		 	</div>
		</div>
	</div>
	<sec:csrfInput/>
</form>

<form id="delete-user-auth-token" class="modal fade" action="<c:url value='/u/sec/auth-tokens/delete'/>" method="post">
	<div class="modal-dialog">
		<div class="modal-content">
		 	<div class="modal-header">
		 		<button type="button" class="close" data-dismiss="modal">&times;</button>
		 		<h4 class="modal-title"><fmt:message key='auth-tokens.user.delete.title'/></h4>
		 	</div>
		 	<div class="modal-body">
		 		<p><fmt:message key='auth-tokens.user.delete.intro'/></p>
		 		<div class="row">
		 			<label class="col-sm-3">
		 				<fmt:message key='auth-tokens.label.token'/>
		 			</label>
		 			<div class="col-sm-9 container-token monospace"></div>
		 		</div>
		 	</div>
		 	<div class="modal-footer">
		 		<input type="hidden" name="id" value=""/>
		 		<a href="#" class="btn btn-default" data-dismiss="modal"><fmt:message key='close.label'/></a>
		 		<button type="submit" class="btn btn-danger">
		 			<i class="glyphicon glyphicon-trash"></i>
		 			<fmt:message key='auth-tokens.action.delete'/>
		 		</button>
		 	</div>
		</div>
	</div>
	<sec:csrfInput/>
</form>

<c:if test="${not empty userEdges}">
	<form id="create-data-auth-token" class="modal fade" action="<c:url value='/u/sec/auth-tokens/generateData'/>" method="post">
		<div class="modal-dialog">
			<div class="modal-content">
			 	<div class="modal-header">
			 		<button type="button" class="close" data-dismiss="modal">&times;</button>
			 		<h4 class="modal-title"><fmt:message key='auth-tokens.data.create.title'/></h4>
			 	</div>
			 	<div class="modal-body">
			 		<p class="before"><fmt:message key='auth-tokens.data.create.intro'/></p>
			 		<ul class="nav nav-pills form-group before">
						<li class="active"><a data-toggle="pill" href="#create-data-auth-token-tab-ids"><fmt:message key='auth-tokens.data.create.group.ids'/></a></li>
						<li><a data-toggle="pill" href="#create-data-auth-token-tab-agg"><fmt:message key='auth-tokens.data.create.group.agg'/></a></li>
						<li><a data-toggle="pill" href="#create-data-auth-token-tab-Edge-meta"><fmt:message key='auth-tokens.data.create.group.Edge-meta'/></a></li>
						<li><a data-toggle="pill" href="#create-data-auth-token-tab-user-meta"><fmt:message key='auth-tokens.data.create.group.user-meta'/></a></li>
					</ul>
					<div class="tab-content before">
						<div id="create-data-auth-token-tab-ids" class="tab-pane fade in active">
					 		<div id="create-data-auth-token-policy-Edgeids" class="toggle-buttons">
					 			<c:forEach items="${userEdges}" var="userEdge" varStatus="status">
					 				<button type="button" class="toggle btn btn-sm btn-default" data-Edge-id="${userEdge.Edge.id}">${userEdge.Edge.id}<c:if test="${fn:length(userEdge.name) gt 0}"> - ${userEdge.name}</c:if></button>
					 			</c:forEach>
					 		</div>
					 		<div class="form-group">
					 			<label for="create-data-auth-token-policy-sourceids"><fmt:message key='auth-tokens.policy.sourceIds.label'/></label>
					 			<textarea id="create-data-auth-token-policy-sourceids" class="form-control" name="sourceIds" rows="2" 
					 				placeholder="<fmt:message key='auth-tokens.policy.sourceIds.placeholder'/>"></textarea>
					 			<div id="create-data-auth-token-policy-sourceids-hint" class="toggle-buttons"></div>
					 		</div>
						</div>
						<div id="create-data-auth-token-tab-agg" class="tab-pane fade">
				 			<label for="create-data-auth-token-policy-minagg"><fmt:message key='auth-tokens.policy.minAggregation.label'/></label>
				 			<select id="create-data-auth-token-policy-minagg" name="minAggregation">
								<option value=""><fmt:message key='auth-tokens.policy.minAggregation.none'/></option>
								<c:forEach items="${policyAggregations}" var="agg" varStatus="itr">
									<option value="${agg}"><fmt:message key='aggregation.${agg}.label'/></option>
								</c:forEach>
				 			</select>
							<div class="help-block"><fmt:message key='auth-tokens.policy.minAggregation.caption'/></div>
						</div>
						<div id="create-data-auth-token-tab-Edge-meta" class="tab-pane fade">
				 			<label for="create-data-auth-token-policy-Edgemeta"><fmt:message key='auth-tokens.policy.EdgeMetadataPaths.label'/></label>
				 			<textarea id="create-data-auth-token-policy-Edgemeta" class="form-control" name="EdgeMetadataPaths" rows="2" 
				 				placeholder="<fmt:message key='auth-tokens.policy.EdgeMetadataPaths.placeholder'/>"></textarea>
				 			<div class="help-block"><fmt:message key='auth-tokens.policy.EdgeMetadataPaths.caption'/></div>
						</div>
						<div id="create-data-auth-token-tab-user-meta" class="tab-pane fade">
				 			<label for="create-data-auth-token-policy-usermeta"><fmt:message key='auth-tokens.policy.userMetadataPaths.label'/></label>
				 			<textarea id="create-data-auth-token-policy-usermeta" class="form-control" name="userMetadataPaths" rows="2" 
				 				placeholder="<fmt:message key='auth-tokens.policy.userMetadataPaths.placeholder'/>"></textarea>
				 			<div class="help-block"><fmt:message key='auth-tokens.policy.userMetadataPaths.caption'/></div>
						</div>
					</div>
			 		<div class="after">
			 			<p><fmt:message key='auth-tokens.created.intro'/></p>
				 		<table class="table">
				 			<thead>
				 				<tr>
				 					<th><fmt:message key='auth-tokens.label.token'/></th>
				 					<th class="text-danger"><fmt:message key='auth-tokens.label.secret'/></th>
				 				</tr>
				 			</thead>
				 			<tbody>
				 				<tr>
				 					<td class="monospace result-token"></td>
				 					<td class="monospace result-secret text-danger"></td>
				 				</tr>
				 			</tbody>
				 		</table>
				 		<div class="alert alert-danger">
				 			<strong><fmt:message key='auth-tokens.created.reiterate.title'/></strong>
				 			<fmt:message key='auth-tokens.created.reiterate'/>
				 		</div>
			 		</div>
			 	</div>
			 	<div class="modal-footer">
			 		<a href="#" class="btn btn-default" data-dismiss="modal"><fmt:message key='close.label'/></a>
			 		<button type="submit" class="btn btn-primary before">
			 			<fmt:message key='auth-tokens.action.create'/>
			 		</button>
			 	</div>
			</div>
		</div>
		<sec:csrfInput/>
	</form>
	
	<form id="delete-data-auth-token" class="modal fade" action="<c:url value='/u/sec/auth-tokens/delete'/>" method="post">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal">&times;</button>
					<h4 class="modal-title"><fmt:message key='auth-tokens.data.delete.title'/></h4>
				</div>
				<div class="modal-body">
					<p><fmt:message key='auth-tokens.data.delete.intro'/></p>
					<div class="row">
						<label class="col-sm-3">
							<fmt:message key='auth-tokens.label.token'/>
						</label>
						<div class="col-sm-9 container-token monospace"></div>
					</div>
				</div>
				<div class="modal-footer">
					<input type="hidden" name="id" value=""/>
					<a href="#" class="btn btn-default" data-dismiss="modal"><fmt:message key='close.label'/></a>
					<button type="submit" class="btn btn-danger">
						<i class="glyphicon glyphicon-trash"></i>
						<fmt:message key='auth-tokens.action.delete'/>
					</button>
				</div>
			</div>
		</div>
		<sec:csrfInput/>
	</form>
</c:if>
