
<%@ page import="gov.nasa.horizon.security.server.IngSystemUser" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'ingSystemUser.label', default: 'IngSystemUser')}" />
        <title><g:message code="default.show.label" args="[entityName]" /></title>
    </head>
    <body>
        <div class="nav">
            <span class="menuButton"><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></span>
            <span class="menuButton"><g:link class="list" action="list"><g:message code="default.list.label" args="[entityName]" /></g:link></span>
            <span class="menuButton"><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></span>
        </div>
        <div class="body">
            <h1><g:message code="default.show.label" args="[entityName]" /></h1>
            <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
            </g:if>
            <div class="dialog">
                <table>
                    <tbody><%--
                    
                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="ingSystemUser.id.label" default="Id" /></td>
                            
                            <td valign="top" class="value">${fieldValue(bean: ingSystemUserInstance, field: "id")}</td>
                            
                        </tr>
                    
                        --%><tr class="prop">
                            <td valign="top" class="name"><g:message code="ingSystemUser.name.label" default="Name" /></td>
                            
                            <td valign="top" class="value">${fieldValue(bean: ingSystemUserInstance, field: "name")}</td>
                            
                        </tr>
                    
                        <%--<tr class="prop">
                            <td valign="top" class="name"><g:message code="ingSystemUser.password.label" default="Password" /></td>
                            
                            <td valign="top" class="value">${fieldValue(bean: ingSystemUserInstance, field: "password")}</td>
                            
                        </tr>
                    
                        --%><tr class="prop">
                            <td valign="top" class="name"><g:message code="ingSystemUser.fullname.label" default="Fullname" /></td>
                            
                            <td valign="top" class="value">${fieldValue(bean: ingSystemUserInstance, field: "fullname")}</td>
                            
                        </tr>
                    
                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="ingSystemUser.email.label" default="Email" /></td>
                            
                            <td valign="top" class="value">${fieldValue(bean: ingSystemUserInstance, field: "email")}</td>
                            
                        </tr>
                    
                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="ingSystemUser.admin.label" default="Admin" /></td>
                            
                            <td valign="top" class="value"><g:formatBoolean boolean="${ingSystemUserInstance?.admin}" /></td>
                            
                        </tr>
                    
                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="ingSystemUser.readAll.label" default="Read All" /></td>
                            
                            <td valign="top" class="value"><g:formatBoolean boolean="${ingSystemUserInstance?.readAll}" /></td>
                            
                        </tr>
                    
                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="ingSystemUser.writeAll.label" default="Write All" /></td>
                            
                            <td valign="top" class="value"><g:formatBoolean boolean="${ingSystemUserInstance?.writeAll}" /></td>
                            
                        </tr>
                    
                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="ingSystemUser.note.label" default="Note" /></td>
                            
                            <td valign="top" class="value">${fieldValue(bean: ingSystemUserInstance, field: "note")}</td>
                            
                        </tr>
                    
                    </tbody>
                </table>
            </div>
            <div class="buttons">
                <g:form>
                    <g:hiddenField name="id" value="${ingSystemUserInstance?.id}" />
                    <span class="button"><g:actionSubmit class="edit" action="edit" value="${message(code: 'default.button.edit.label', default: 'Edit')}" /></span>
                    <span class="button"><g:actionSubmit class="edit" action="resetPassword" value="Reset Password" /></span>
                    <span class="button"><g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" /></span>
                </g:form>
            </div>
        </div>
    </body>
</html>
