<%@ page session="false" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="petclinic" tagdir="/WEB-INF/tags" %>

<petclinic:layout pageName="vet">
	
	
    <h2>
        <c:if test="${vet['new']}"><spring:message code="vet.new" /></c:if> <spring:message code="vet.vet" />
    </h2>
    <form:form modelAttribute="vet" class="form-horizontal" id="add-pilot-form" action="/vets/new">
        <div class="form-group has-feedback">
        	<input type="hidden" name="id" value="${vet.id}"/>
            <spring:message code="vet.firstName" var="firstName"/>
            <spring:message code="vet.lastName" var="lastName"/>
            <petclinic:inputField label="${firstName}" name="firstName"/>
            <petclinic:inputField label="${lastName}" name="lastName"/>
            
            
            <div class="control-group">
    			<div class="form-group">
    				<label class="col-sm-2 control-label"><spring:message code="vets.specialties" /></label>
    				<div class="col-sm-10">
						<form:select class="form-control" multiple="true" path="specialties" size="5">
							<form:options items="${specialties}" itemLabel="name" itemValue="id" />
						</form:select>
					</div>
				</div>
			</div>
					
        </div>
        <div class="form-group">
            <div class="col-sm-offset-2 col-sm-10">
                <c:choose>
                    <c:when test="${vet['new']}">
                        <button class="btn btn-default" type="submit"><spring:message code="vet.add" /></button>
                    </c:when>
                    <c:otherwise>
                        <button class="btn btn-default" type="submit"><spring:message code="vet.update" /></button>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </form:form>
</petclinic:layout>
