

<%@ page import="gov.nasa.horizon.security.server.IngSystemUser" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'ingSystemUser.label', default: 'IngSystemUser')}" />
        <title><g:message code="default.edit.label" args="[entityName]" /></title>
    </head>
    <body>
        <div class="nav">
            <span class="menuButton"><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></span>
            <span class="menuButton"><g:link class="list" action="list"><g:message code="default.list.label" args="[entityName]" /></g:link></span>
            <span class="menuButton"><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></span>
        </div>
        <div class="body">
            <h1><g:message code="default.edit.label" args="[entityName]" /></h1>
            <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
            </g:if>
            <g:hasErrors bean="${ingSystemUserInstance}">
            <div class="errors">
                <g:renderErrors bean="${ingSystemUserInstance}" as="list" />
            </div>
            </g:hasErrors>
            <g:form method="post" >
                <g:hiddenField name="id" value="${ingSystemUserInstance?.id}" />
                <g:hiddenField name="version" value="${ingSystemUserInstance?.version}" />
                <div class="dialog">
                    <table>
                        <tbody>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                  <label for="name"><g:message code="ingSystemUser.name.label" default="Name" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: ingSystemUserInstance, field: 'name', 'errors')}">
                                    <g:textField name="name" maxlength="180" value="${ingSystemUserInstance?.name}" />
                                </td>
                            </tr>
                        
							<!--                         
                            <tr class="prop">
                                <td valign="top" class="name">
                                  <label for="password"><g:message code="ingSystemUser.password.label" default="Password" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: ingSystemUserInstance, field: 'password', 'errors')}">
                                    <g:passwordField name="password" maxlength="40" value="${ingSystemUserInstance?.password}" />
                                </td>
                            </tr>
                         	-->
                            <tr class="prop">
                                <td valign="top" class="name">
                                  <label for="fullname"><g:message code="ingSystemUser.fullname.label" default="Fullname" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: ingSystemUserInstance, field: 'fullname', 'errors')}">
                                    <g:textField name="fullname" maxlength="40" value="${ingSystemUserInstance?.fullname}" />
                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                  <label for="email"><g:message code="ingSystemUser.email.label" default="Email" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: ingSystemUserInstance, field: 'email', 'errors')}">
                                    <g:textField name="email" value="${ingSystemUserInstance?.email}" />
                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                  <label for="admin"><g:message code="ingSystemUser.admin.label" default="Admin" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: ingSystemUserInstance, field: 'admin', 'errors')}">
                                    <g:checkBox name="admin" value="${ingSystemUserInstance?.admin}" />
                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                  <label for="readAll"><g:message code="ingSystemUser.readAll.label" default="Read All" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: ingSystemUserInstance, field: 'readAll', 'errors')}">
                                    <g:checkBox name="readAll" value="${ingSystemUserInstance?.readAll}" />
                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                  <label for="writeAll"><g:message code="ingSystemUser.writeAll.label" default="Write All" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: ingSystemUserInstance, field: 'writeAll', 'errors')}">
                                    <g:checkBox name="writeAll" value="${ingSystemUserInstance?.writeAll}" />
                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                  <label for="note"><g:message code="ingSystemUser.note.label" default="Note" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: ingSystemUserInstance, field: 'note', 'errors')}">
                                    <g:textArea name="note" cols="40" rows="5" value="${ingSystemUserInstance?.note}" />
                                </td>
                            </tr>
                        
                        </tbody>
                    </table>
                </div>
                <div class="buttons">
                    <span class="button"><g:actionSubmit class="save" action="update" value="${message(code: 'default.button.update.label', default: 'Update')}" /></span>
                    <span class="button"><g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" /></span>
                </div>
            </g:form>
        </div>
    </body>
</html>
