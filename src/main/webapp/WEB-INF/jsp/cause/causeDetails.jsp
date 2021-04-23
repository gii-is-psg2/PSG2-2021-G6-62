<%@ page session="false" trimDirectiveWhitespaces="true" %> 
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %> 
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %> 
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> 
<%@ taglib prefix="petclinic" tagdir="/WEB-INF/tags" %> 
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %> 
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %> 
 
<petclinic:layout pageName="causeDetails"> 
 
    <h2><spring:message code="cause.details" /></h2> 
 
	<c:if test="${budgetAchieved == cause.target }"> 
		<h1><spring:message code="cause.closed" /></h1> 
	</c:if> 
    <table class="table table-striped"> 
        <tr> 
            <th><spring:message code="cause.organization" /></th> 
            <td><b><c:out value="${cause.organization}"/></b></td> 
        </tr> 
        <tr> 
            <th><spring:message code="cause.description" /></th> 
            <td><c:out value="${cause.description}"/></td> 
        </tr> 
        <tr> 
            <th><spring:message code="cause.target" /></th> 
            <td><c:out value="${cause.target}"/></td> 
        </tr> 
         <tr> 
            <th><spring:message code="cause.budgetAchieved" /></th> 
            <td><c:out value="${budgetAchieved}"/></td> 
        </tr> 
    </table> 
    <br/> 
    <c:if test="${validar}"> 
    	HOLA 
    </c:if> 
     
 	<c:if test="${budgetAchieved != cause.target }"> 
	<sec:authorize access="hasAnyAuthority('owner')"> 
	<spring:message code="donation.info" /> 
	<form:form modelAttribute="donation" class="form-horizontal" action="/donation/${causeId}/save"> 
		<input type="hidden" name="id" value="${donation.id}"/> 
		 
		<spring:message code="donation.amount" var="amount" /> 
		<petclinic:inputField label="${amount}" name="amount"/> 
		<h5><spring:message code="donation.excess"/></h5> 
		<button class="btn btn-default" type="submit"><spring:message code="donation.makeDonation" /></button> 
	</form:form> 
	</sec:authorize> 
	</c:if> 
    <br/> 
    <h2><spring:message code="cause.donations" /></h2> 
 
    <table class="table table-striped"> 
        <c:forEach var="donation" items="${donations}"> 
 
            <tr> 
                <td valign="top"> 
                    <dl class="dl-horizontal"> 
                        <dt><spring:message code="owner.username" /></dt> 
                        <dd><c:out value="${donation.userName}"/></dd> 
                        <dt><spring:message code="donation.date" /></dt> 
                        <dd><petclinic:localDate date="${donation.date}" pattern="yyyy-MM-dd"/></dd> 
                        <dt><spring:message code="donation.amount" /></dt> 
                        <dd><c:out value="${donation.amount}"/></dd> 
                    </dl> 
                </td> 
                 
            </tr> 
 
        </c:forEach> 
    </table> 
    
</petclinic:layout>