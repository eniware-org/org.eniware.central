<%--
	Input parameters:
	
		EdgeDataAlerts 		- collection of UserAuth objects of type EdgeStaleData
		EdgeDataAlertTypes 	- collection of UserAlertType that represent Edge data alerts
		userEdges      		- collection of UserEdge objects
		alertStatuses       - collection of UesrAlertStatus
 --%>
<p class="intro">
	<fmt:message key='alerts.intro'/>
</p>

<section class="Edge-data-alerts">
	<h2>
		<fmt:message key='alerts.Edge.data.header'/>
		<button type="button" id="add-Edge-data-button" class="btn btn-primary pull-right">
			<i class="glyphicon glyphicon-plus"></i> <fmt:message key='alerts.action.create'/>
		</button>
	</h2>
	<p class="intro">
		<fmt:message key='alerts.Edge.data.intro'>
			<fmt:param value="${fn:length(EdgeDataAlerts)}"/>
		</fmt:message>
	</p>
	<c:if test="${not empty EdgeDataAlerts}">
		<table class="table" id="Edge-data-alerts"
			data-action-situation='<c:url value="/u/sec/alerts/situation"/>'>
			<thead>
				<tr>
					<th><fmt:message key="alert.EdgeId.label"/></th>
					<th><fmt:message key="alert.type.label"/></th>
					<th><fmt:message key="alert.options.sourceIds.label"/></th>
					<th><fmt:message key="alert.options.ageMinutes.heading"/></th>
					<th><fmt:message key="alert.options.window.label"/></th>
					<th><fmt:message key="alert.status.label"/></th>
					<th></th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${EdgeDataAlerts}" var="alert">
					<tr class="alert-row${alert.situation != null ? ' alert-danger' : ''}">
						<td>
							<c:choose>
								<c:when test="${alert.EdgeId != null}">
									${alert.EdgeId}
								</c:when>
								<c:otherwise>
									<fmt:message key='alert.EdgeId.any'/>
								</c:otherwise>
							</c:choose>
						</td>
						<td>
							<fmt:message key='alert.type.${alert.type}.label'/>
						</td>
						<td>
							<c:set var="alertSources">
								<c:if test="${not empty alert.options.sourceIds}">
									<c:forEach items="${alert.options.sourceIds}" var="source" varStatus="itr">
										<c:if test="${not itr.first}">, </c:if>
										${source}
									</c:forEach>
								</c:if>
							</c:set>
							<c:out value="${alertSources}"/>
						</td>
						<td>
							<c:set var="alertAge">
								<c:if test="${alert.options.age != null}">
									<fmt:formatNumber type="number" maxFractionDigits="0" >${alert.options.age / 60}</fmt:formatNumber>
								</c:if>
							</c:set>
							<c:out value="${alertAge}"/>
						</td>
						<td>
							<c:set var="alertWindowTimeStart">
								<c:if test="${alert.options.windows != null && not empty alert.options.windows}">
									${alert.options.windows[0].timeStart}
								</c:if>
							</c:set>
							<c:set var="alertWindowTimeEnd">
								<c:if test="${alert.options.windows != null && not empty alert.options.windows}">
									${alert.options.windows[0].timeEnd}
								</c:if>
							</c:set>
							<c:if test="${fn:length(alertWindowTimeStart) gt 0 && fn:length(alertWindowTimeEnd) gt 0}">
								<c:out value="${alertWindowTimeStart}"/> - <c:out value="${alertWindowTimeEnd}"/>
							</c:if>
						</td>
						<td>
							<span class="label${alert.status eq 'Active' 
								? ' label-success' : alert.status eq 'Disabled' 
								? ' label-default' : ' label-primary'}">
								<fmt:message key='alert.status.${alert.status}.label'/>
							</span>
						</td>
						<td>
							<button type="button" class="btn btn-small btn-default edit-alert"
								data-Edge-id="${alert.EdgeId}" data-alert-id="${alert.id}"
								data-alert-type="${alert.type}" data-alert-status="${alert.status}"
								data-sources="${alertSources}" data-age="${alertAge}"
								data-window-time-start="${alertWindowTimeStart}" data-window-time-end="${alertWindowTimeEnd}">
								<fmt:message key='alerts.action.edit'/>
							</button>
							<c:if test="${alert.situation != null}">
								<button type="button" class="btn btn-small btn-danger view-situation"
									data-alert-id="${alert.id}">
									<span class="glyphicon glyphicon-alert" aria-hidden="true"></span>
									<fmt:message key='alerts.action.situation.view'/>
								</button>
							</c:if>
						</td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
	</c:if>
</section>

<%-- Modal views --%>

<form id="create-Edge-data-alert-modal" class="modal fade alert-form" action="<c:url value='/u/sec/alerts/save'/>" method="post"
	data-action-Edge-sources='<c:url value="/u/sec/alerts/Edge/0/sources"/>'>
	<div class="modal-dialog">
		<div class="modal-content">
		 	<div class="modal-header">
		 		<button type="button" class="close" data-dismiss="modal">&times;</button>
		 		<h4 class="modal-title"><fmt:message key='alerts.Edge.data.create.title'/></h4>
		 	</div>
		 	<div class="modal-body form-horizontal carousel slide" id="create-Edge-data-alert-carousel">
		 		<ol class="carousel-indicators dark">
    				<li data-target="#create-Edge-data-alert-carousel" data-slide-to="0" class="active" title="<fmt:message key='alerts.Edge.data.create.section.main.title'/>"></li>
    				<li data-target="#create-Edge-data-alert-carousel" data-slide-to="1" title="<fmt:message key='alerts.Edge.data.create.section.filters.title'/>"></li>
  				</ol>
		 		<div class="carousel-inner" role="listbox">
		 			<div class="item active">
				 		<p><fmt:message key='alerts.Edge.data.create.intro'/></p>
						<div class="form-group">
							<label class="col-sm-2 control-label" for="create-Edge-data-alert-Edge-id"><fmt:message key="alert.EdgeId.label"/></label>
							<div class="col-sm-10">
								<select name="EdgeId" class="form-control" id="create-Edge-data-alert-Edge-id">
									<option value=""><fmt:message key='alert.EdgeId.any'/></option>
									<c:forEach items="${userEdges}" var="userEdge">
										<option value="${userEdge.Edge.id}">${userEdge.idAndName}</option>
									</c:forEach>
								</select>
							</div>
						</div>
						<div class="form-group">
							<label class="col-sm-2 control-label" for="create-Edge-data-alert-type"><fmt:message key="alert.type.label"/></label>
							<div class="col-sm-10 checkbox">
								<select name="type" class="form-control" id="create-Edge-data-alert-type">
									<c:forEach items="${EdgeDataAlertTypes}" var="alertType">
										<option value="${alertType}" title="<fmt:message key='alert.type.${alertType}.caption'/>">
											<fmt:message key='alert.type.${alertType}.label'/>
										</option>
									</c:forEach>
								</select>
							</div>
						</div>
				 		<div class="form-group">
				 			<label class="col-sm-2 control-label" for="create-Edge-data-alert-age"><fmt:message key='alert.options.ageMinutes.label'/></label>
							<div class="col-sm-10">
								<input name='option-age-minutes' type="number" min="10" required="required" value="30" class="form-control col-sm-3" id="create-Edge-data-alert-age"/>
								<span class="help-block"><fmt:message key="alert.options.ageMinutes.caption"/></span>
							</div>
				 		</div>
				 		<div class="form-group">
				 			<label class="col-sm-2 control-label" for="create-Edge-data-alert-status"><fmt:message key='alert.status.label'/></label>
							<div class="col-sm-10" id="create-Edge-data-alert-status">
								<c:forEach items="${alertStatuses}" var="alertStatus" varStatus="itr">
									<label class="radio-inline">
										<input type="radio" name="status" value="${alertStatus}" 
											<c:if test='${itr.first}'>checked</c:if>
											title="<fmt:message key='alert.status.${alertStatus}.caption'/>"/>
										<c:out value=" "/>
										<fmt:message key='alert.status.${alertStatus}.label'/>
									</label>
								</c:forEach>
								<div class="help-block alert-status-help"></div>
							</div>
				 		</div>
		 			</div>
		 			<div class="item">
				 		<div class="form-group">
				 			<label class="col-sm-2 control-label" for="create-Edge-data-alert-sources"><fmt:message key='alert.options.sourceIds.label'/></label>
							<div class="col-sm-10">
								<input name='option-sources' type="text" maxlength="255" class="form-control" id="create-Edge-data-alert-sources"/>
								<span class="help-block"><fmt:message key="alert.options.sourceIds.caption"/></span>
								<div class="text-info hidden" id="create-Edge-data-alert-sources-list">
									<%-- This container will be used to display the available sources for a given Edge. --%>
									<b><fmt:message key='alert.options.sourceIds.available.label'/>: </b>
									<span class="sources"></span>
								</div>
							</div>
				 		</div>
				 		<div class="form-group">
				 			<label class="col-sm-2 control-label" for="create-Edge-data-alert-window"><fmt:message key='alert.options.window.label'/></label>
							<div class="col-sm-10 form-inline" id="create-Edge-data-alert-window">
								<input name='option-window-time-start' type="text" size="5" maxlength="5" placeholder="08:00" class="form-control" id="create-Edge-data-alert-window-time-start"/>
								<input name='option-window-time-end' type="text" size="5" maxlength="5" placeholder="20:00" class="form-control" id="create-Edge-data-alert-window-time-end"/>
								<span class="help-block"><fmt:message key="alert.options.window.caption"/></span>
							</div>
				 		</div>
				 	</div>
				 </div>
		 	</div>
		 	<div class="modal-footer">
		 		<button class="btn btn-small btn-danger action-delete pull-left" type="button">
					<i class="glyphicon glyphicon-trash"></i>
					<fmt:message key='alerts.action.delete'/>
				</button>
		 		<a href="#" class="btn btn-default" data-dismiss="modal"><fmt:message key='close.label'/></a>
		 		<button type="submit" class="btn btn-primary before">
		 			<fmt:message key='alerts.action.save'/>
		 		</button>
		 	</div>
		</div>
	</div>
	<input type="hidden" name="id" value=""/>
	<sec:csrfInput/>
</form>

<%@include file="/WEB-INF/jsp/sec/alerts/situation-modal.jsp" %>
<%@include file="/WEB-INF/jsp/sec/alerts/alert-enums.jsp" %>
