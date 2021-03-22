<%@ page session="false" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="petclinic" tagdir="/WEB-INF/tags" %>

<petclinic:layout pageName="pethotels">
    <h2>hotels</h2>

    <table id="petHotelTable" class="table table-striped">
        <thead>
        <tr>
            <th style="width: 150px;">Owner</th>
            <th style="width: 200px;">Pet</th>
            <th>Start</th>
            <th>End</th>
            <th style="width: 120px">Description</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${petHotel}" var="petHotel">
            <tr>
                <td>
                     <c:out value="${petHotel.userName}"/>
                </td>
                   <td>
                     <c:out value="${petHotel.pet.name}"/>
                </td>
                <td>
                    <c:out value="${petHotel.startDate}"/>
                </td>
                <td>
                    <c:out value="${petHotel.endDate}"/>
                </td>
                <td>
                    <c:out value="${petHotel.description}"/>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</petclinic:layout>
