<%@ page session="false" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="petclinic" tagdir="/WEB-INF/tags" %>

<petclinic:layout pageName="vet">
	
	
    <h2>
        <c:if test="${vet['new']}">New </c:if> Vet
    </h2>
    <form:form modelAttribute="vet" class="form-horizontal" id="add-pilot-form" action="/vets/new">
        <div class="form-group has-feedback">
        	<input type="hidden" name="id" value="${vet.id}"/>
            <petclinic:inputField label="First Name" name="firstName"/>
            <petclinic:inputField label="Last Name" name="lastName"/>
            
            
            <div class="control-group">
    			<div class="form-group">
    				<label class="col-sm-2 control-label">Specialties</label>
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
                        <button class="btn btn-default" type="submit">Add Vet</button>
                    </c:when>
                    <c:otherwise>
                        <button class="btn btn-default" type="submit">Update Vet</button>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </form:form>
</petclinic:layout>
