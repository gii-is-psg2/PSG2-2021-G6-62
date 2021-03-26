<%@ page session="false" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="petclinic" tagdir="/WEB-INF/tags" %>


<petclinic:layout pageName="vets">
    <h2><spring:message code="vets.title" /></h2>

    <table id="vetsTable" class="table table-striped">
        <thead>
	        <tr>
                <th><spring:message code="vets.name" /></th>
                <th><spring:message code="vets.specialties" /></th>
	            <th><spring:message code="vet.actions" /></th>
	        </tr>
        </thead>
        <tbody>
	        <c:forEach items="${vets.vetList}" var="vet">
	            <tr>
	                <td>
	                    <c:out value="${vet.firstName} ${vet.lastName}"/>
	                </td>
	                <td>
	                    <c:forEach var="specialty" items="${vet.specialties}">
	                        <c:out value="${specialty.name} "/>
	                    </c:forEach>
	                    <c:if test="${vet.nrOfSpecialties == 0}"><spring:message code="vet.noneSpecialty" /></c:if>
	                </td>
	                <td>
		                <spring:url value="/vets/{vetId}/edit" var="editUrl">
			                <spring:param name="vetId" value="${vet.id}" />
		                </spring:url> <a href="${editUrl}"><spring:message code="vet.edit" /></a>
		                
		                <spring:url value="/vets/{vetId}/delete" var="deleteUrl">
			                <spring:param name="vetId" value="${vet.id}" />
		                </spring:url> <a href="${deleteUrl}"><spring:message code="vet.delete" /></a>
            		</td> 
	            </tr>
	        </c:forEach>
        </tbody>
    </table>

    <table class="table-buttons">
        <tr>
        	<td>
                <a href="<spring:url value="/vets/new" htmlEscape="true" />"><spring:message code="vet.create" /></a>
            </td> 
        </tr>
<!--         <tr> -->
<!--             <td> -->
<%--                 <a href="<spring:url value="/vets.xml" htmlEscape="true" />">View as XML</a> --%>
<!--             </td>          -->
<!--         </tr> -->
    </table>
</petclinic:layout>
