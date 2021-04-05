<%@ page session="false" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="petclinic" tagdir="/WEB-INF/tags" %>

<petclinic:layout pageName="bookings">
    <jsp:body>
     <form:form modelAttribute="petHotel" method="get" class="form-horizontal" id="add-pilot-form" action="/pethotel/selectUserToNew">
       <form:select class="form-control" path="userName" size="5" >
<%-- 							<form:options items="${owners}" itemLabel="firstName" itemValue="firstName" /> --%>
							<c:forEach var="o" items="${owners}">

        <form:option value="${o.user.username}"><c:out value="${o.lastName} ${o.firstName}"/></form:option>

    </c:forEach>
						</form:select>
            
            <div class="form-group">
                <div class="col-sm-offset-2 col-sm-10">
                    <button class="btn btn-default" type="submit"><spring:message code="hotel.addBooking" /></button>
                </div>
            </div>
    </form:form>
    </jsp:body>
</petclinic:layout>