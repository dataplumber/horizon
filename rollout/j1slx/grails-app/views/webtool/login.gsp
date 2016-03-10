<%@ page import="gov.nasa.podaac.j1slx.CatalogEntry" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'catalogEntry.label', default: 'CatalogEntry')}" />
        <title>J1SLX Login</title>
    </head>
    <body>
        <div class="body">
            <h1></h1>
            <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
            </g:if>
            <g:if test="${flash.error}">
            <div class="errors">${flash.error}</div>
            </g:if>
			<center>            
            <g:form action="doLogin">
            <div style="width: 960px; height: 250px;padding-top: 25px;text-align: center;" >
            
            <div style="margin-left: 330px; width:250px; background-color: #EEEEEE; border: 1px black solid; padding: 20px;">
            	<table style="border: none;">
		        	<tr>
		        		<td ><label> User name </label></td>
		        		<td ><input type="text" name="user" style="border:1px solid black;"> </td>
		        	</tr>
		            <tr>
		            	<td ><label> Password </label></td>
		            	<td><input type="password" name="password" style="border:1px solid black;"></td>
		            </tr>
		            <tr>
		            	<td colspan="2"><input type="submit" value="login" /></td>
		            </tr>
                </table>
            <div>
            
            </div>
            </g:form>
            </center>
        </div>
    </body>
</html>
