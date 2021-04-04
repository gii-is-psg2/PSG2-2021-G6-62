<%@ page session="false" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="petclinic" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<petclinic:layout pageName="pethotels">
	<h2>
		<spring:message code="hotel.title" />
	</h2>

	<table id="petHotelTable" class="table table-striped">
		<thead>
			<tr>
				<th style="width: 150px;"><spring:message code="owner.owner" /></th>
				<th style="width: 200px;"><spring:message code="pet.pet" /></th>
				<th><spring:message code="hotel.checkIn" /></th>
				<th><spring:message code="hotel.checkOut" /></th>
				<th style="width: 120px"><spring:message
						code="hotel.description" /></th>
			</tr>
		</thead>
		<tbody>
			<sec:authorize access="hasAuthority('admin')">
				<a class="btn btn-default" href='<spring:url value="/pethotel/selectUser" htmlEscape="true"/>'>
					<spring:message	code="home.bookARoom" /></a>
			</sec:authorize>
			<c:forEach items="${petHotel}" var="petHotel">
				<tr>
					<td><c:out value="${petHotel.userName}" /></td>
					<td><c:out value="${petHotel.pet.name}" /></td>
					<td><c:out value="${petHotel.startDate}" /></td>
					<td><c:out value="${petHotel.endDate}" /></td>
					<td><c:out value="${petHotel.description}" /></td>
				</tr>
			</c:forEach>

		</tbody>
	</table>
</petclinic:layout>
