<%@ page session="false" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="petclinic" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<petclinic:layout pageName="cause">
	<h2>
		<spring:message code="cause.title" />
	</h2>
	
	<sec:authorize access="isAuthenticated()">
		<a class="btn btn-default" href='<spring:url value="/cause/new" htmlEscape="true"/>'><spring:message code="cause.makeCause" /></a>
	</sec:authorize>
	
	<table class="table table-striped">
        <c:forEach var="cause" items="${causes}">

            <tr>
                <td valign="top">
                    <dl class="dl-horizontal">
                        <dt><spring:message code="cause.organization" /></dt>
                        <dd><c:out value="${cause.organization}"/></dd>
                        <dt><spring:message code="cause.target" /></dt>
                        <dd><c:out value="${cause.target}"/>$</dd>
                    </dl>
                </td>
                <td valign="top">
                    <table class="table-condensed">
                    
                       
                       <tr>
                           <th><spring:message code="cause.description" /></th> 
                           <td><c:out value="${cause.description}"/></td>
                       </tr>
                       <td> 
                            <spring:url value="/donation/{causeId}" var="causeurl"> 
                                <spring:param name="causeId" value="${cause.id}"/> 
                            </spring:url> 
 							<a href="${fn:escapeXml(causeurl)}"><spring:message code="cause.details" /></a> 
                        </td> 
               
                    </table>
                    </td>       
            	</tr>

        </c:forEach>
    </table>
</petclinic:layout>