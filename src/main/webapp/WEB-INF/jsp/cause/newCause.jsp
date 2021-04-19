<%@ page session="false" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="petclinic" tagdir="/WEB-INF/tags" %>

<petclinic:layout pageName="causeNew">
  
    <jsp:body>
        <form:form modelAttribute="cause" class="form-horizontal" action="/cause/save">
            <input type="hidden" name="id" value="${cause.id}"/>
            <div class="form-group has-feedback">
            
		        
                <spring:message code="cause.organization" var="organization" />
                <spring:message code="cause.description" var="description" />
                <spring:message code="cause.target" var="target" />
                <petclinic:inputField label="${organization}" name="organization"/>
                <petclinic:inputField label="${description}" name="description"/>
                <petclinic:inputField label="${target}" name="target"/>
            </div>
            <div class="form-group">
                <div class="col-sm-offset-2 col-sm-10">
                    <button class="btn btn-default" type="submit"><spring:message code="cause.add" /></button>
                </div>
            </div>
        </form:form>
    </jsp:body>
</petclinic:layout>
