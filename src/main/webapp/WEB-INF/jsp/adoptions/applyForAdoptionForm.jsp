<%@ page session="false" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="petclinic" tagdir="/WEB-INF/tags" %>

<petclinic:layout pageName="owners">
    <h2>
        <spring:message code="adoption.applicationForm" />
    </h2> <br>
    <form:form modelAttribute="adoptionApplication" class="form-horizontal" id="add-owner-form">
        <div class="form-group has-feedback">
            <spring:message code="pet.pet" var="pet" />
            <spring:message code="adoption.description" var="description" />
            <h2>
            	<t><c:out value="${pet}: ${adoptionRequest.pet.name}" />
            </h2>
            
            <petclinic:inputField label="${description}" name="description"/>

		<spring:bind path="owner">
    		<c:set var="valid" value="${not status.error and not empty status.actualValue}"/>
    	    <div class="${cssGroup}">
				<spring:message code="owner.owner" var="owner" />
				
				<label for="ownerSelect"><c:out value="${owner}" /></label>
				<form:select id="ownerSelect" class="form-control" path="owner" size="5">
					<c:forEach var="owner" items="${ownersOfUser}">
						<form:option value="${owner.id}">
							<c:out value="${owner.lastName} ${owner.firstName}" />
						</form:option>
					</c:forEach>
				</form:select>
				
				<c:if test="${valid}">
	                <span class="glyphicon glyphicon-ok form-control-feedback" aria-hidden="true"></span>
	            </c:if>
	            <c:if test="${status.error}">
	                <span class="help-inline">${status.errorMessage}</span>
	            </c:if>
	        </div>
		</spring:bind>
		</div>
        <div class="form-group">
            <div class="col-sm-offset-2 col-sm-10">
				<button class="btn btn-default" type="submit"><spring:message code="adoption.apply" /></button>
            </div>
        </div>
    </form:form>
</petclinic:layout>
