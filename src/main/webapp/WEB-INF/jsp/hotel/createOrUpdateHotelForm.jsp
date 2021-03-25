<%@ page session="false" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="petclinic" tagdir="/WEB-INF/tags" %>

<petclinic:layout pageName="bookings">
    <jsp:attribute name="customScript">
        <script>
            $(function () {
                $("#startDate").datepicker({dateFormat: 'yy/mm/dd'});
                $("#endDate").datepicker({dateFormat: 'yy/mm/dd'});
            });
        </script>
    </jsp:attribute>
    <jsp:body>
        <form:form modelAttribute="petHotel" class="form-horizontal" action="/pethotel/save">
            <input type="hidden" name="id" value="${petHotel.id}"/>
            
            <input type="hidden" name="userName" value="${nombre}"/>
            <div class="form-group has-feedback">
                <div class="form-group">
                    <label class="col-sm-2 control-label">Owner</label>
                    
                    <div class="col-sm-10">
                        <c:out value="${nombre}"/>
                    </div>
                </div>
                <petclinic:inputField label="description" name="description"/>
                <petclinic:inputField label="start of the Date" name="startDate"/>
                <petclinic:inputField label="end of the Date" name="endDate"/>
                <div class="control-group">
                    <petclinic:selectField name="pet" label="pets" names="${pets}" size="5"/>
                </div>
            </div>
            <div class="form-group">
                <div class="col-sm-offset-2 col-sm-10">
                    <button class="btn btn-default" type="submit">Add booking</button>
                </div>
            </div>
        </form:form>
    </jsp:body>
</petclinic:layout>
