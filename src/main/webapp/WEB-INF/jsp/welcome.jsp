<%@ page session="false" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="petclinic" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<!-- %@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %-->  

<style>
	.row {
	  display: flex;
	}
	
	.column {
	  flex: 33.33%;
	  padding: 5px;
	}
	
	@media screen and (max-width: 500px) {
	.column {
	  width: 100%;
	}
}
</style>

<petclinic:layout pageName="home">
    <h2><fmt:message key="welcome"/></h2>
    <div class="row">

        <div class="column">
            <spring:url value="/resources/images/otter.png" htmlEscape="true" var="petsImage"/>
            <img src="${petsImage}" style="width: 60%"/>
            <br><a class="btn btn-default" href='<spring:url value="/owners/find" htmlEscape="true"/>'><spring:message code="menu.findOwners" /></a><br>
            <br><sec:authorize access="hasAuthority('owner')">
		<%--     	<h1><spring:message code="home.booking" /></h1> --%>
				<a class="btn btn-default" href='<spring:url value="/pethotel/${nombre}/new" htmlEscape="true"/>'><spring:message code="home.bookARoom" /></a>
			</sec:authorize><br>
			
			<sec:authorize access="hasAnyAuthority('admin,owner')">
				<a class="btn btn-default" href='<spring:url value="/adoptions" htmlEscape="true"/>'><spring:message code="menu.adoptions" /></a>
			</sec:authorize><br>
						
			<sec:authorize access="hasAnyAuthority('owner')">
				<a class="btn btn-default" href='<spring:url value="adoptionApplications" htmlEscape="true"/>'><spring:message code="menu.adoptionApplications" /></a>
			</sec:authorize>
			
			<a class="btn btn-default" href='<spring:url value="/contact" htmlEscape="true"/>'><spring:message code="menu.contact" /></a>
		</div>
		<div class="column">
            <spring:url value="/resources/images/dogtor.png" htmlEscape="true" var="dogtor"/>
            <img src="${dogtor}" style="width: 60%"/>
			<a class="btn btn-default" href='<spring:url value="/manage/health" htmlEscape="true"/>'><spring:message code="menu.health" /></a>
        </div>
    </div>
</petclinic:layout>
