<%@ page session="false" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="petclinic" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<!-- %@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %-->  

<petclinic:layout pageName="home">
    <h2><fmt:message key="welcome"/></h2>
    <div class="row">
        <div class="col-md-12">
            <spring:url value="/resources/images/otter.png" htmlEscape="true" var="petsImage"/>
            <img class="img-responsive" src="${petsImage}"/>
        </div>
        
    
        
    </div>
    

    
    <sec:authorize access="hasAuthority('owner')">
<%--     	<h1><spring:message code="home.booking" /></h1> --%>
		<a class="btn btn-default" href='<spring:url value="/pethotel/${nombre}/new" htmlEscape="true"/>'><spring:message code="home.bookARoom" /></a>
	</sec:authorize><br>
	
	<sec:authorize access="hasAnyAuthority('admin,owner')">
		<a class="btn btn-default" href='<spring:url value="/adoptions" htmlEscape="true"/>'><spring:message code="menu.adoptions" /></a>
	</sec:authorize><br>
				
	<sec:authorize access="hasAnyAuthority('owner')">
		<a class="btn btn-default" href='<spring:url value="adoptionApplications" htmlEscape="true"/>'><spring:message code="menu.adoptionApplications" /></a>
	</sec:authorize>
		
</petclinic:layout>
