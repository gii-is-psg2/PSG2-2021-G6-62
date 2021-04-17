<%@ page session="false" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="petclinic" tagdir="/WEB-INF/tags" %>

<petclinic:layout pageName="adoptions">
    <h2><spring:message code="adoption.myPetsInAdoption" /></h2>

    <table id="adoptionsTable" class="table table-striped">
        <thead>
        <tr>
            <th style="width: 150px;"><spring:message code="adoption.pet" /></th>
            <th style="width: 200px;"><spring:message code="adoption.description" /></th>
            <th><spring:message code="adoption.actions" /></th>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${adoptionApplications}" var="adoptionApplication">
            <tr>
            	<td>
	            	<c:out value="${adoptionApplication.adoptionRequest.pet} "/>
            	</td>
            	<td>
	            	<c:out value="${adoptionApplication.description} "/>
            	</td>
                <td>
                    <spring:url value="/adoptions/{adoptionApplicationId}/adopt" var="adoptionApplicationUrl">
                        <spring:param name="adoptionApplicationId" value="${adoptionApplication.id}"/>
                    </spring:url>
                    <a href="${fn:escapeXml(adoptionApplicationUrl)}"><spring:message code="adoption.letBecomeNewOwner" /></a> <br>
                    
	                <spring:url value="/adoptionApplications/{adoptionApplicationId}/delete" var="deleteUrl">
				        <spring:param name="adoptionApplicationId" value="${adoptionApplication.id}" />
			        </spring:url> <a href="${deleteUrl}"><spring:message code="adoption.delete" /></a>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</petclinic:layout>
