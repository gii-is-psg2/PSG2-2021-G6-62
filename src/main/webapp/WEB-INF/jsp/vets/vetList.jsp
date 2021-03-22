<%@ page session="false" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="petclinic" tagdir="/WEB-INF/tags" %>


<petclinic:layout pageName="vets">
    <h2>Veterinarians</h2>

    <table id="vetsTable" class="table table-striped">
        <thead>
	        <tr>
	            <th>Name</th>
	            <th>Specialties</th>
	            <th>Actions</th>
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
	                    <c:if test="${vet.nrOfSpecialties == 0}">none</c:if>
	                </td>
	                <td>
		                <spring:url value="/vets/{vetId}/edit" var="editUrl">
			                <spring:param name="vetId" value="${vet.id}" />
		                </spring:url> <a href="${editUrl}">Edit</a>
		                
		                <spring:url value="/vets/{vetId}/delete" var="deleteUrl">
			                <spring:param name="vetId" value="${vet.id}" />
		                </spring:url> <a href="${deleteUrl}">Delete</a>
            		</td> 
	            </tr>
	        </c:forEach>
        </tbody>
    </table>

    <table class="table-buttons">
        <tr>
        	<td>
                <a href="<spring:url value="/vets/new" htmlEscape="true" />">Create a new Vet</a>
            </td> 
        </tr>
<!--         <tr> -->
<!--             <td> -->
<%--                 <a href="<spring:url value="/vets.xml" htmlEscape="true" />">View as XML</a> --%>
<!--             </td>          -->
<!--         </tr> -->
    </table>
</petclinic:layout>
