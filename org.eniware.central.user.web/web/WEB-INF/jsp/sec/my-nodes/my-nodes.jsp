<a id="top"></a>

<c:if test='${fn:length(pendingEdgeOwnershipRequests) > 0}'>
	<section id="pending-transfer-requests">
		<h2><fmt:message key='my-Edges.pending-transfer-requests.header'/></h2>
		<p>
			<fmt:message key='my-Edges.pending-transfer-requests.intro'>
				<fmt:param>${fn:length(pendingEdgeOwnershipRequests)}</fmt:param>
			</fmt:message>
		</p>
		<table class="table" id="pending-transfer-requests-table">
			<thead>
				<tr>
					<th><fmt:message key="user.Edge.id.label"/></th>
					<th><fmt:message key="my-Edges.transferOwnership.requester.label"/></th>
					<th><fmt:message key="my-Edges.transferOwnership.requestDate.label"/></th>
					<th></th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${pendingEdgeOwnershipRequests}" var="transfer">
					<tr>
						<td>${transfer.Edge.id}</td>
						<td>${transfer.user.email}</td>
						<td>
							<joda:dateTimeZone value="GMT">
								<joda:format value="${transfer.created}" pattern="dd MMM yyyy"/> GMT
							</joda:dateTimeZone>
						</td>
						<td>
							<button type="button" class="btn btn-small btn-default decide-ownership-transfer"
								data-user-id="${transfer.user.id}" data-Edge-id="${transfer.Edge.id}" 
								data-requester="${transfer.user.email}">
								<fmt:message key='my-Edges.transferOwnership.action.requestDecision'/>
							</button>
						</td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
	</section>
</c:if>

<c:if test='${fn:length(pendingUserEdgeConfirmationsList) > 0}'>
	<section id="pending">
		<h2><fmt:message key='my-Edges.pending-invite.header'/></h2>
		<p>
			<fmt:message key='my-Edges.pending-invite.intro'>
				<fmt:param>${fn:length(pendingUserEdgeConfirmationsList)}</fmt:param>
			</fmt:message>
		</p>
		<div class="alert alert-info alert-dismissible" role="alert">
			<button type="button" class="close" data-dismiss="alert" aria-label="<fmt:message key='close.label'/>"><span aria-hidden="true">&times;</span></button>
			<fmt:message key='my-Edges.eniwareedge.link.help'/>
		</div>
		<table class="table">
			<thead>
				<tr>
					<th class="col-sm-2"><fmt:message key="user.Edgeconf.created.label"/></th>
					<th class="col-sm-10"></th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${pendingUserEdgeConfirmationsList}" var="userEdgeConf">
					<tr>
						<td>
							<joda:dateTimeZone value="GMT">
								<joda:format value="${userEdgeConf.created}"
									 pattern="dd MMM yyyy"/> GMT
							</joda:dateTimeZone>
						</td>
						<td>
							<div class="btn-group">
								<a class="btn btn-default" href="<c:url value='/u/sec/my-Edges/invitation'/>?id=${userEdgeConf.id}">
									<fmt:message key='my-Edges.view-invitation.link'/>
								</a>
								<button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-expanded="false">
									<span class="caret"></span>
									<span class="sr-only"><fmt:message key='toggle.dropdown.label'/></span>
								</button>
								<ul class="dropdown-menu" role="menu">
									<li>
										<a class="btn btn-danger" href="<c:url value='/u/sec/my-Edges/cancelInvitation'/>?id=${userEdgeConf.id}">
											<i class="glyphicon glyphicon-trash"></i> <fmt:message key='my-Edges.cancel-invitation.link'/>
										</a>
									</li>
								</ul>
							</div>
						</td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
	</section>
</c:if>

<c:if test='${fn:length(pendingUserEdgeTransferList) > 0}'>
	<section id="pending-transfers">
		<h2>
			<fmt:message key='my-Edges.pending-transfer-ownership.header'/>
		</h2>
		<p class="intro">
			<fmt:message key='my-Edges.pending-transfer-ownership.intro'>
				<fmt:param value="${fn:length(pendingUserEdgeTransferList)}"/>
			</fmt:message>
		</p>
		<table class="table" id="pending-transfer">
			<thead>
				<tr>
					<th><fmt:message key="user.Edge.id.label"/></th>
					<th><fmt:message key="user.Edge.created.label"/></th>
					<th><fmt:message key="my-Edges.transferOwnership.recipient.label"/></th>
					<th><fmt:message key="my-Edges.transferOwnership.requestDate.label"/></th>
					<th></th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${pendingUserEdgeTransferList}" var="userEdge">
					<tr class="Edge-row">
						<td>
							${userEdge.Edge.id}
							<c:if test='${fn:length(userEdge.name) gt 0}'> - ${userEdge.name}</c:if>
							<c:if test='${fn:length(userEdge.description) gt 0}'> (${userEdge.description})</c:if>
						</td>
						<td>
							<joda:dateTimeZone value="GMT">
								<joda:format value="${userEdge.Edge.created}" pattern="dd MMM yyyy"/> GMT
							</joda:dateTimeZone>
						</td>
						<td>${userEdge.transfer.email}</td>
						<td>
							<joda:dateTimeZone value="GMT">
								<joda:format value="${userEdge.transfer.created}" pattern="dd MMM yyyy"/> GMT
							</joda:dateTimeZone>
						</td>
						<td>
							<button type="button" data-action="<c:url value='/u/sec/my-Edges/cancelEdgeTransferRequest'/>"
								data-user-id="${userEdge.user.id}"
								data-Edge-id="${userEdge.Edge.id}"
								title="<fmt:message key='my-Edges.transferOwnership.action.cancel'/>"
								class="btn btn-small btn-danger cancel-ownership-transfer"><i class="glyphicon glyphicon-remove"></i></button>
						</td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
	</section>
</c:if>

<section id="Edges">
	<h2>
		<fmt:message key='my-Edges.Edgelist.header'/>
		<button type="button" id="invite-new-Edge-button" class="btn btn-primary pull-right" data-target="#invite-modal" data-toggle="modal">
			<i class="glyphicon glyphicon-plus"></i> <fmt:message key='my-Edges.inviteEdge'/>
		</button>
	</h2>
	<p class="intro">
		<fmt:message key='my-Edges.intro'>
			<fmt:param value="${fn:length(userEdgesList)}"/>
		</fmt:message>
	</p>
	<c:if test="${fn:length(userEdgesList) > 0}">
		<table class="table" id="my-Edges-table">
			<thead>
				<tr>
					<th><fmt:message key="user.Edge.id.label"/></th>
					<th><fmt:message key="user.Edge.created.label"/></th>
					<th><fmt:message key="user.Edge.name.label"/></th>
					<th><fmt:message key="user.Edge.description.label"/></th>
					<th><fmt:message key="user.Edge.private.label"/></th>
					<th><fmt:message key="user.Edge.certificate.label"/></th>
					<th></th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${userEdgesList}" var="userEdge">
					<tr class="Edge-row" data-Edge-id="${userEdge.Edge.id}" data-user-id="${userEdge.user.id}"
						<c:if test='${fn:length(userEdge.name) gt 0}'>data-Edge-name="${userEdge.name}"</c:if>
						>
						<td>${userEdge.Edge.id}</td>
						<td>
							<joda:dateTimeZone value="GMT">
								<joda:format value="${userEdge.Edge.created}"
									 pattern="dd MMM yyyy"/> GMT
							</joda:dateTimeZone>
						</td>
						<td>${userEdge.name}</td>
						<td>${userEdge.description}</td>
						<td>
							<span class="label${userEdge.requiresAuthorization ? '' : ' label-success'}">
								<fmt:message key="user.Edge.private.${userEdge.requiresAuthorization}"/>
							</span>
						</td>
						<td>
							<c:choose>
								<c:when test="${empty userEdge.certificate}">
									<span class="label">
										<fmt:message key="user.Edge.certificate.unmanaged"/>
									</span>
								</c:when>
								<c:otherwise>
									<span class="label${userEdge.certificate.status.value eq 'Active' 
										? ' label-success' : userEdge.certificate.status.value eq 'Disabled' 
										? ' label-warning' : ' label-primary'}">
										<fmt:message key="user.Edge.certificate.status.${userEdge.certificate.status.value}"/>
									</span>
								</c:otherwise>
							</c:choose>
						</td>
						<td>
							<div class="btn-group">
								<button type="button" class="btn btn-small btn-default edit-Edge" data-target="#edit-Edge-modal"
									data-user-id="${userEdge.user.id}" data-Edge-id="${userEdge.Edge.id}"
									><fmt:message key='my-Edges.action.edit'/></button>
								<button type="button" class="btn btn-small btn-default dropdown-toggle" data-toggle="dropdown" aria-expanded="false">
									<span class="caret"></span>
									<span class="sr-only"><fmt:message key='toggle.dropdown.label'/></span>
								</button>
								<ul class="dropdown-menu dropdown-menu-right" role="menu">
									<c:if test='${userEdge.certificate.status.value eq "Active"}'>
										<li>
											<a href="#" class="view-cert">
												<fmt:message key="user.Edge.certificate.action.view"/>
											</a>
										</li>
										<li>
											<a href="#" class="view-cert">
												<fmt:message key="user.Edge.certificate.action.renew"/>
											</a>
										</li>
									</c:if>
									<li>
										<a href="#" class="transfer-ownership">
											<fmt:message key="user.Edge.action.transferOwnership"/>
										</a>
									</li>
									<li role="separator" class="divider"></li>
									<li>
										<a href="#" class="archive">
											<fmt:message key="user.Edge.action.archive"/>
										</a>
									</li>
								</ul>
							</div>
							<button class="btn btn-small btn-danger view-situation hidden" type="button">
								<span aria-hidden="true" class="glyphicon glyphicon-alert"></span>
							</button>
						</td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
	</c:if>
</section>

<%-- Modal forms --%>

<form id="invite-modal" class="modal fade" action="<c:url value='/u/sec/my-Edges/new'/>" method="post">
	<div class="modal-dialog">
		<div class="modal-content">
		 	<div class="modal-header">
		 		<button type="button" class="close" data-dismiss="modal">&times;</button>
		 		<h4 class="modal-title"><fmt:message key='my-Edges.inviteEdge'/></h4>
		 	</div>
		 	<div class="modal-body form-horizontal">
		 		<p><fmt:message key='my-Edges-invitation.create.intro'/></p>
		 		<div class="form-group">
		 			<label class="col-sm-3 control-label">
		 				<fmt:message key='my-Edges.invitation.securityPhrase.label'/>
		 				${' '}
		 			</label>
		 			<div class="col-sm-8">
		 				<input type="text" class="form-control" name="phrase" 
		 					placeholder="<fmt:message key='my-Edges.invitation.securityPhrase.label'/>"
		 					maxlength="128" required="required"/>
			 			<span class="help-block"><small><fmt:message key='my-Edges.invitation.securityPhrase.caption'/></small></span>
			 		</div>
		 		</div>
		 		<div class="form-group">
		 			<label class="col-sm-3 control-label">
		 				<fmt:message key='my-Edges.invitation.tz.label'/>
		 				${' '}
		 			</label>
		 			<div class="col-sm-8">
		 				<input type="text" class="form-control" name="timeZone" id="invite-tz"
		 					placeholder="<fmt:message key='my-Edges.invitation.tz.placeholder'/>"
		 					maxlength="128" required="required"/>
		 			</div>
		 			<div class="col-sm-1">
		 				<span class="help-block" id="invite-tz-country"></span>
		 			</div>
		 		</div>
	 			<div id="tz-picker-container" class="tz-picker-container"></div>
		 	</div>
		 	<div class="modal-footer">
		 		<a href="#" class="btn" data-dismiss="modal"><fmt:message key='close.label'/></a>
		 		<input type="hidden" name="country" id="invite-country"/>
		 		<button type="submit" class="btn btn-primary"><fmt:message key='my-Edges.inviteEdge'/></button>
		 	</div>
		 </div>
 	</div>
 	<sec:csrfInput/>
</form>
<form id="view-cert-modal" class="modal fade" action="<c:url value='/u/sec/my-Edges/cert'/>/0" method="post">
	<div class="modal-dialog">
		<div class="modal-content">
		 	<div class="modal-header">
		 		<button type="button" class="close" data-dismiss="modal">&times;</button>
		 		<h4 class="modal-title"><fmt:message key='my-Edges.cert.view.title'/></h4>
		 	</div>
		 	<div class="modal-body">
		 		<p class="nocert"><fmt:message key='my-Edges.cert.view.intro'/></p>
		 		<p class="cert hidden"><fmt:message key='my-Edges.cert.view.cert-intro'/></p>
		 		<div class="alert alert-info renewed hidden"><fmt:message key='my-Edges.cert.renewed.queued'/></div>
		 		<fieldset class="form-inline nocert">
		 			<label for="view-cert-password"><fmt:message key='my-Edges.cert.view.password.label'/></label>
					<input class="span3 form-control" type="password" name="password" id="view-cert-password" />
		 		</fieldset>
		 		<fieldset class="cert hidden">
			 		<table class="table">
			 			<tbody>
			 				<tr>
			 					<th><fmt:message key='my-Edges.cert.view.serialNumber.label'/></th>
			 					<td id="view-cert-serial-number"></td>
			 				</tr>
			 				<tr>
			 					<th><fmt:message key='my-Edges.cert.view.subject.label'/></th>
			 					<td id="view-cert-subject"></td>
			 				</tr>
			 				<tr>
			 					<th><fmt:message key='my-Edges.cert.view.issuer.label'/></th>
			 					<td id="view-cert-issuer"></td>
			 				</tr>
			 				<tr>
			 					<th><fmt:message key='my-Edges.cert.view.validFrom.label'/></th>
			 					<td id="view-cert-valid-from"></td>
			 				</tr>
			 				<tr>
			 					<th><fmt:message key='my-Edges.cert.view.validUntil.label'/></th>
			 					<td id="view-cert-valid-until"></td>
			 				</tr>
			 				<tr>
			 					<th><fmt:message key='my-Edges.cert.view.renewAfter.label'/></th>
			 					<td id="view-cert-renew-after"></td>
			 				</tr>
			 			</tbody>
			 		</table>
			 		<pre class="cert" id="modal-cert-container"></pre>
		 		</fieldset>
		 	</div>
		 	<div class="modal-footer">
		 		<a href="#" class="btn btn-default" data-dismiss="modal"><fmt:message key='close.label'/></a>
		 		<a href="<c:url value='/u/sec/my-Edges/cert/0'/>" id="modal-cert-download" class="btn btn-default">
		 			<fmt:message key='my-Edges.cert.action.download'/>
		 		</a>
		 		<a href="<c:url value='/u/sec/my-Edges/cert/renew/0'/>" id="modal-cert-renew" class="btn btn-primary renew hidden">
		 			<fmt:message key='my-Edges.cert.action.renew'/>
		 		</a>
		 		<button type="submit" class="btn btn-primary nocert">
		 			<fmt:message key='my-Edges.cert.action.view'/>
		 		</button>
		 	</div>
		 </div>
	</div>
 	<sec:csrfInput/>
</form>
<form id="edit-Edge-modal" class="modal fade page1" action="<c:url value='/u/sec/my-Edges/updateEdge'/>" method="post">
	<div class="modal-dialog">
		<div class="modal-content">
		 	<div class="modal-header">
		 		<button type="button" class="close" data-dismiss="modal">&times;</button>
		 		<h4 class="modal-title"><fmt:message key='my-Edges.edit-Edge.title'/></h4>
		 	</div>
		 	<div class="modal-body">
				<div class="hbox">
					<fieldset class="form-horizontal">
						<div class="form-group">
							<label class="col-sm-2 control-label" for="userEdge-id"><fmt:message key="user.Edge.id.label"/></label>
							<div class="col-sm-10">
								<span class="uneditable-input span2 form-control" id="userEdge-id"></span>
							</div>
						</div>
						<div class="form-group">
							<label class="col-sm-2 control-label" for="userEdge-name"><fmt:message key="user.Edge.name.label"/></label>
							<div class="col-sm-10">
								<input name="name" type="text" maxlength="128" class="form-control" id="userEdge-name"/>
								<span class="help-block"><fmt:message key="user.Edge.name.caption"/></span>
							</div>
						</div>
						<div class="form-group">
							<label class="col-sm-2 control-label" for="userEdge-description"><fmt:message key="user.Edge.description.label"/></label>
							<div class="col-sm-10">
								<input name="description" type="text" maxlength="512" class="form-control" id="userEdge-description"/>
								<span class="help-block"><fmt:message key="user.Edge.description.caption"/></span>
							</div>
						</div>
						<div class="form-group">
							<label class="col-sm-2 control-label" for="userEdge-private"><fmt:message key="user.Edge.private.label"/></label>
							<div class="col-sm-10">
								<div class="checkbox">
									<label>
										<input name="requiresAuthorization" type="checkbox" value="true" id="userEdge-private"/>
										<fmt:message key="user.Edge.private.caption"/>
									</label>
								</div>
							</div>
						</div>
						<div class="form-group">
							<label class="col-sm-2 control-label" for="userEdge-location"><fmt:message key="user.Edge.location.label"/></label>
							<div class="col-sm-10">
								<span id="userEdge-location"></span>
								<button type="button" class="btn btn-default change-location"><fmt:message key='change.label'/></button>
							</div>
						</div>
					</fieldset>
					<fieldset class="form-horizontal edit-location-tz">
						<p><fmt:message key='my-Edges.edit-Edge.choose-tz.intro'/></p>
						<div class="form-group">
				 			<label class="col-sm-3 control-label" for="edit-Edge-location-tz"><fmt:message key='location.tz.label'/></label>
							<div class="col-sm-9">
								<input type="text" class="form-control" name="Edge.location.timeZoneId" id="edit-Edge-location-tz"
		 							placeholder="<fmt:message key='my-Edges.invitation.tz.placeholder'/>"
		 							maxlength="128" />
							</div>
				 		</div>
						<div class="form-group">
				 			<label class="col-sm-3 control-label" for="edit-Edge-location-country"><fmt:message key='location.country.label'/></label>
							<div class="col-sm-9">
								<input type="text" class="form-control" name="Edge.location.country" id="edit-Edge-location-country" maxlength="2" />
							</div>
				 		</div>
						<div class="tz-picker-container"></div>
					</fieldset>
					<fieldset class="form-horizontal" id="edit-Edge-location-details" 
						data-lookup-url="<c:url context="/eniwarequery" value='/api/v1/pub/location'/>">
						<p><fmt:message key='my-Edges.edit-Edge.choose-location.intro'/></p>
						<div class="form-group">
				 			<label class="col-sm-3 control-label" for="edit-Edge-location-postal-code"><fmt:message key='location.postalCode.label'/></label>
							<div class="col-sm-9">
								<input type="text" class="form-control" name="Edge.location.postalCode" id="edit-Edge-location-postal-code"
		 							maxlength="128" />
							</div>
				 		</div>
						<div class="form-group">
				 			<label class="col-sm-3 control-label" for="edit-Edge-location-locality"><fmt:message key='location.locality.label'/></label>
							<div class="col-sm-9">
								<input type="text" class="form-control" name="Edge.location.locality" id="edit-Edge-location-locality"
		 							maxlength="128" />
							</div>
				 		</div>
						<div class="form-group">
				 			<label class="col-sm-3 control-label" for="edit-Edge-location-state"><fmt:message key='location.state.label'/></label>
							<div class="col-sm-9">
								<input type="text" class="form-control" name="Edge.location.stateOrProvince" id="edit-Edge-location-state"
		 							maxlength="128" />
							</div>
				 		</div>
						<div class="form-group">
				 			<label class="col-sm-3 control-label" for="edit-Edge-location-region"><fmt:message key='location.region.label'/></label>
							<div class="col-sm-9">
								<input type="text" class="form-control" name="Edge.location.region" id="edit-Edge-location-region"
		 							maxlength="128" />
							</div>
				 		</div>
				 		<p class="hidden" id="edit-Edge-location-search-no-match">
				 			<fmt:message key='my-Edges.edit-Edge.choose-location.nomatch'/>
				 		</p>
						<table class="table table-striped table-hover hidden" id="edit-Edge-location-search-results">
							<thead>
								<tr>
									<th><fmt:message key='location.country.label'/></th>
									<th><fmt:message key='location.state.label'/></th>
									<th><fmt:message key='location.region.label'/></th>
									<th><fmt:message key='location.locality.label'/></th>
									<th><fmt:message key='location.postalCode.label'/></th>
								</tr>
								<tr class="template">
									<td data-tprop="country"></td>
									<td data-tprop="stateOrProvince"></td>
									<td data-tprop="region"></td>
									<td data-tprop="locality"></td>
									<td data-tprop="postalCode"></td>
								</tr>
							</thead>
							<tbody>
							</tbody>
						</table>
					</fieldset>
					<fieldset class="form-horizontal" id="edit-Edge-location-private-details">
						<p><fmt:message key='my-Edges.edit-Edge.private-location.intro'/></p>
						<div class="form-group">
				 			<label class="col-sm-3 control-label" for="edit-Edge-location-street"><fmt:message key='location.address.label'/></label>
							<div class="col-sm-9">
								<input type="text" class="form-control" name="Edge.location.street" id="edit-Edge-location-street"
		 							maxlength="256" />
							</div>
				 		</div>
						<div class="form-group">
				 			<label class="col-sm-3 control-label" for="edit-Edge-location-latitude"><fmt:message key='location.lat.label'/></label>
							<div class="col-sm-9">
								<input type="text" class="form-control" name="Edge.location.latitude" id="edit-Edge-location-latitude"
		 							maxlength="16"  aria-describedby="edit-Edge-locaiton-latitude-help"/>
		 						<span class="help-block" id="edit-Edge-locaiton-latitude-help"><fmt:message key='my-Edges.edit-Edge.choose-location-private.latlon.caption'/></span>
							</div>
				 		</div>
						<div class="form-group">
				 			<label class="col-sm-3 control-label" for="edit-Edge-location-longitude"><fmt:message key='location.lon.label'/></label>
							<div class="col-sm-9">
								<input type="text" class="form-control" name="Edge.location.longitude" id="edit-Edge-location-longitude"
		 							maxlength="16" aria-describedby="edit-Edge-locaiton-longitude-help"/>
		 						<span class="help-block" id="edit-Edge-locaiton-longitude-help"><fmt:message key='my-Edges.edit-Edge.choose-location-private.latlon.caption'/></span>
							</div>
				 		</div>
						<div class="form-group">
				 			<label class="col-sm-3 control-label" for="edit-Edge-location-elevation"><fmt:message key='location.elevation.label'/></label>
							<div class="col-sm-9">
								<input type="text" class="form-control" name="Edge.location.elevation" id="edit-Edge-location-elevation"
		 							maxlength="12"  aria-describedby="edit-Edge-locaiton-elevation-help"/>
		 						<span class="help-block" id="edit-Edge-locaiton-elevation-help"><fmt:message key='my-Edges.edit-Edge.choose-location-private.elevation.caption'/></span>
							</div>
				 		</div>
					</fieldset>
				</div>
		 	</div>
		 	<div class="modal-footer">
		 		<button type="button" class="btn btn-default" data-dismiss="modal"><fmt:message key='close.label'/></button>
		 		<button type="button" class="btn page2 page3 page4 btn-info" id="edit-Edge-page-back"><fmt:message key='back.label'/></button>
		 		<button type="submit" class="btn page1 btn-primary"><fmt:message key='save.label'/></button>
		 		<button type="button" class="btn page2 btn-primary" id="edit-Edge-select-tz"><fmt:message key='my-Edges.edit-Edge.choose-tz.action.select'/></button>
		 		<button type="button" class="btn page3 btn-primary" id="edit-Edge-select-location" disabled="disabled"><fmt:message key='my-Edges.edit-Edge.choose-location.action.select'/></button>
		 		<button type="button" class="btn page4 btn-primary" id="edit-Edge-select-location-private"><fmt:message key='my-Edges.edit-Edge.choose-location-private.action.select'/></button>
		 	</div>
		</div>
	</div>
	<input type="hidden" name="Edge.id"/>
	<input type="hidden" name="user.id"/>
	<input type="hidden" name="Edge.locationId"/>
 	<sec:csrfInput/>
</form>
<form id="transfer-ownership-modal" class="modal fade" action="<c:url value='/u/sec/my-Edges/requestEdgeTransfer'/>" method="post">
	<div class="modal-dialog">
		<div class="modal-content">
		 	<div class="modal-header">
		 		<button type="button" class="close" data-dismiss="modal">&times;</button>
		 		<h4 class="modal-title"><fmt:message key='my-Edges.transferOwnership.title'/></h4>
		 	</div>
		 	<div class="modal-body form-horizontal">
		 		<p><fmt:message key='my-Edges.transferOwnership.intro'/></p>
				<div class="form-group">
					<label class="col-sm-2 control-label" for="transfer-ownership-Edge"><fmt:message key="user.Edge.id.label"/></label>
					<div class="col-sm-10">
						<p id="transfer-ownership-Edge" class="form-control-static"></p>
					</div>
				</div>
		 		<div class="form-group">
		 			<label class="col-sm-2 control-label" for="transfer-ownership-recipient"><fmt:message key='my-Edges.transferOwnership.recipient.label'/></label>
					<div class="col-sm-10">
						<input class="form-control" type="text" name="recipient" maxlength="240" id="transfer-ownership-recipient"
							required="required"
							placeholder="<fmt:message key='my-Edges.transferOwnership.recipient.placeholder'/>"
							aria-describedby="transfer-ownership-recipient-help"
							 />
						<span class="help-block" id="transfer-ownership-recipient-help"><fmt:message key='my-Edges.transferOwnership.recipient.caption'/></span>
					</div>
		 		</div>
		 	</div>
		 	<div class="modal-footer">
		 		<a href="#" class="btn btn-default" data-dismiss="modal"><fmt:message key='close.label'/></a>
		 		<button type="submit" class="btn btn-primary">
		 			<fmt:message key='my-Edges.transferOwnership.action.submit'/>
		 		</button>
		 	</div>
		 </div>
	</div>
	<input type="hidden" name="EdgeId"/>
	<input type="hidden" name="userId"/>
 	<sec:csrfInput/>
</form>
<form id="decide-transfer-ownership-modal" class="modal fade" action="<c:url value='/u/sec/my-Edges/confirmEdgeTransferRequest'/>" method="post">
	<div class="modal-dialog">
		<div class="modal-content">
		 	<div class="modal-header">
		 		<button type="button" class="close" data-dismiss="modal">&times;</button>
		 		<h4 class="modal-title"><fmt:message key='my-Edges.transferOwnership.requestDecision.title'/></h4>
		 	</div>
		 	<div class="modal-body form-horizontal">
		 		<p><fmt:message key='my-Edges.transferOwnership.requestDecision.intro'/></p>
				<div class="form-group">
					<label class="col-sm-2 control-label" for="transfer-ownership-request-Edge"><fmt:message key="user.Edge.id.label"/></label>
					<div class="col-sm-10">
						<p id="transfer-ownership-request-Edge" class="form-control-static"></p>
					</div>
				</div>
				<div class="form-group">
					<label class="col-sm-2 control-label" for="transfer-ownership-request-requester"><fmt:message key="my-Edges.transferOwnership.requester.label"/></label>
					<div class="col-sm-10">
						<p id="transfer-ownership-request-requester" class="form-control-static"></p>
					</div>
				</div>
		 	</div>
		 	<div class="modal-footer">
		 		<a href="#" class="btn btn-default" data-dismiss="modal"><fmt:message key='close.label'/></a>
		 		<button type="button" class="btn btn-danger submit">
		 			<fmt:message key='my-Edges.transferOwnership.action.decline'/>
		 		</button>
		 		<button type="button" class="btn btn-success submit" data-accept="true">
		 			<fmt:message key='my-Edges.transferOwnership.action.accept'/>
		 		</button>
		 	</div>
		 </div>
	</div>
	<input type="hidden" name="EdgeId"/>
	<input type="hidden" name="userId"/>
	<input type="hidden" name="accept" value="false"/>
 	<sec:csrfInput/>
</form>
<form id="archive-Edge-modal" class="modal fade" action="<c:url value='/u/sec/my-Edges/archived'/>" method="post">
	<div class="modal-dialog">
		<div class="modal-content">
		 	<div class="modal-header">
		 		<button type="button" class="close" data-dismiss="modal">&times;</button>
		 		<h4 class="modal-title"><fmt:message key='my-Edges.archive.title'/></h4>
		 	</div>
		 	<div class="modal-body form-horizontal">
		 		<p><fmt:message key='my-Edges.archive.intro'/></p>
				<div class="form-group">
					<label class="col-sm-2 control-label" for="archive-Edge"><fmt:message key="user.Edge.id.label"/></label>
					<div class="col-sm-10">
						<p class="form-control-static Edge-name-label"></p>
					</div>
				</div>
		 	</div>
		 	<div class="modal-footer">
		 		<a href="#" class="btn btn-default" data-dismiss="modal"><fmt:message key='close.label'/></a>
		 		<button type="submit" class="btn btn-danger submit">
		 			<fmt:message key='my-Edges.archive.action.archive'/>
		 		</button>
		 	</div>
		 </div>
	</div>
	<input type="hidden" name="archived" value="true"/>
	<input type="hidden" name="EdgeIds"/>
 	<sec:csrfInput/>
</form>

<%@include file="/WEB-INF/jsp/sec/alerts/situation-modal.jsp" %>
<%@include file="/WEB-INF/jsp/sec/alerts/alert-enums.jsp" %>
