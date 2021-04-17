<%@ page session="false" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="petclinic" tagdir="/WEB-INF/tags" %>

<petclinic:layout pageName="adoptions">
    <h2><spring:message code="adoption.adoptions" /></h2>

    <table id="adoptionsTable" class="table table-striped">
        <thead>
        <tr>
            <th style="width: 150px;"><spring:message code="adoption.pet" /></th>
            <th style="width: 200px;"><spring:message code="pet.owner" /></th>
            <th><spring:message code="adoption.actions" /></th>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${adoptionRequests}" var="adoptionRequest">
            <tr>
            	<td>
	            	<c:out value="${adoptionRequest.pet} "/>
            	</td>
            	<td>
	            	<c:out value="${adoptionRequest.pet.owner} "/>
            	</td>
                <td>
                	<c:if test="${adoptionRequest.pet.owner.user.username != currentUser}">
	                    <spring:url value="/adoptions/{adoptionRequestId}/apply" var="adoptionRequestUrl">
	                        <spring:param name="adoptionRequestId" value="${adoptionRequest.id}"/>
	                    </spring:url>
	                    <a href="${fn:escapeXml(adoptionRequestUrl)}"><spring:message code="adoption.apply" /></a> <br>
                    </c:if>
                    
                    <c:if test="${adoptionRequest.pet.owner.user.username == currentUser}">
	                	<spring:url value="/adoptions/{adoptionRequestId}/delete" var="deleteUrl">
				        	<spring:param name="adoptionRequestId" value="${adoptionRequest.id}" />
			            </spring:url> <a href="${deleteUrl}"><spring:message code="adoption.delete" /></a>
                	</c:if>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</petclinic:layout>
