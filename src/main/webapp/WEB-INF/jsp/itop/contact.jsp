<%@ page session="false" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="petclinic" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
 
<head>
<script src="http://code.jquery.com/jquery-3.3.1.js"></script>
</head>

<petclinic:layout pageName="home">
    <h2><fmt:message key="menu.contact"/></h2>
    
    <div id="container">
    
	    <div id="felipe" class="top left">
	    	<span id="nombre" ><fmt:message key="contact.name"/>: </span><br>
	    	<span id="email"><fmt:message key="contact.email"/>: </span><br>
	    	<span id="telefono"><fmt:message key="contact.phone"/>:</span><br>
	    	<img src="https://avatars.githubusercontent.com/u/72603609?v=4"style="width: 100px; height:100px;"><br>
	    </div>
	    <br>
	     <div id="jose" class="top right">
	    	<span id="nombre" ><fmt:message key="contact.name"/>: </span><br>
	    	<span id="email"><fmt:message key="contact.email"/>: </span><br>
	    	<span id="telefono"><fmt:message key="contact.phone"/>: </span><br>
	    	<img src="https://avatars.githubusercontent.com/u/60939740?v=4"style="width: 100px; height:100px;"><br>
	    </div>
	    <br>
	     <div id="mariano" class="bottom left">
	    	<span id="nombre" ><fmt:message key="contact.name"/>: </span><br>
	    	<span id="email"><fmt:message key="contact.email"/>: </span><br>
	    	<span id="telefono"><fmt:message key="contact.phone"/>: </span><br>
	    	<img src="https://avatars.githubusercontent.com/u/63660411?s=64&v=4" style="width: 100px; height:100px;"><br>
	    </div>
	    <br>
	     <div id="nico" class="bottom right">
	    	<span id="nombre" ><fmt:message key="contact.name"/>: </span><br>
	    	<span id="email"><fmt:message key="contact.email"/>: </span><br>
	    	<span id="telefono"><fmt:message key="contact.phone"/>: </span><br>
	    	<img src="https://avatars.githubusercontent.com/u/1417708?v=4"style="width: 100px; height:100px;"><br>	
	    </div>
	   <br> 
   </div>
</petclinic:layout>
<script>

	$('#result').val('');
	var oJSON = {
		operation: 'core/get',
		'class': "Contact",
		key: 'SELECT Contact WHERE org_id_friendlyname LIKE "psg2-2021-g6-62"'
	};

	var sURL ="http://localhost/itop-master/webservices/rest.php?version=1.0";
	$('#result').html('');
	var sDataType = 'json';

	$.ajax({
	  	type: "POST",
	  	url: sURL,
	    dataType: sDataType,
		data: { auth_user:"root", auth_pwd: "ABC123xyz", json_data: JSON.stringify(oJSON) },
		crossDomain: 'true',
	    success: function (data) {
	    json = JSON.stringify(data['objects']['Person::40']['fields']['friendlyname'], undefined, 2);
	    $('#felipe #nombre').text($('#felipe #nombre').text()+json.replace(/['"]+/g, ''));
	    json = JSON.stringify(data['objects']['Person::40']['fields']['email'], undefined, 2);
	    $('#felipe #email').text($('#felipe #email').text()+json.replace(/['"]+/g, '')); 
	    json = JSON.stringify(data['objects']['Person::40']['fields']['phone'], undefined, 2);
	    $('#felipe #telefono').text($('#felipe #telefono').text()+json.replace(/['"]+/g, '')); 
	        	
	    json = JSON.stringify(data['objects']['Person::38']['fields']['friendlyname'], undefined, 2);
	    $('#jose #nombre').text($('#jose #nombre').text()+json.replace(/['"]+/g, ''));
	    json = JSON.stringify(data['objects']['Person::38']['fields']['email'], undefined, 2);
	    $('#jose #email').text($('#jose #email').text()+json.replace(/['"]+/g, '')); 
	   	json = JSON.stringify(data['objects']['Person::38']['fields']['phone'], undefined, 2);
	    $('#jose #telefono').text($('#jose #telefono').text()+json.replace(/['"]+/g, '')); 
	        	
	    json = JSON.stringify(data['objects']['Person::39']['fields']['friendlyname'], undefined, 2);
	    $('#nico #nombre').text($('#nico #nombre').text()+json.replace(/['"]+/g, ''));
	    json = JSON.stringify(data['objects']['Person::39']['fields']['email'], undefined, 2);
	 	$('#nico #email').text($('#nico #email').text()+json.replace(/['"]+/g, '')); 
	   	json = JSON.stringify(data['objects']['Person::39']['fields']['phone'], undefined, 2);
	   	$('#nico #telefono').text($('#nico #telefono').text()+json.replace(/['"]+/g, '')); 
	        	
	   	json = JSON.stringify(data['objects']['Person::36']['fields']['friendlyname'], undefined, 2);
	  	$('#mariano #nombre').text($('#mariano #nombre').text()+json.replace(/['"]+/g, ''));
	  	json = JSON.stringify(data['objects']['Person::36']['fields']['email'], undefined, 2);
	   	$('#mariano #email').text($('#mariano #email').text()+json.replace(/['"]+/g, '')); 
	   	json = JSON.stringify(data['objects']['Person::36']['fields']['phone'], undefined, 2);
	   	$('#mariano #telefono').text($('#mariano #telefono').text()+json.replace(/['"]+/g, '')); 
	       }
	 });
	    
</script>
	