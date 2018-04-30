<%--
	Input parameters:
	
		nodeDataAlertTypes 	- collection of UserAlertType that represent node data alerts
		alertStatuses       - collection of UesrAlertStatus
 --%>
<script type="text/javascript">
EniwareReg.userAlertTypes = {
<c:forEach items="${nodeDataAlertTypes}" var="alertType" varStatus="itr">
	${alertType} : "<fmt:message key='alert.type.${alertType}.label'/>"${itr.last ? '' : ', '}
</c:forEach>
};
EniwareReg.userAlertStatuses = {
<c:forEach items="${alertStatuses}" var="alertStatus" varStatus="itr">
	${alertStatus} : "<fmt:message key='alert.status.${alertStatus}.label'/>"${itr.last ? '' : ', '}
</c:forEach>
};
</script>
