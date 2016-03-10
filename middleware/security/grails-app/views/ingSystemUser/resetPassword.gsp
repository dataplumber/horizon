

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
                                    <g:textField disabled="true" name="name" maxlength="180" value="${ingSystemUserInstance?.name}" />
                                </td>
                            </tr>
                        
							                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                  <label for="password"><g:message code="ingSystemUser.password.label" default="Password" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: ingSystemUserInstance, field: 'password', 'errors')}">
                                    <g:passwordField name="password" maxlength="40" value="" />
                                </td>
                            </tr>
                            <tr class="prop">
                                <td valign="top" class="name">
                                  <label for="password"><g:message code="ingSystemUser.password.label" default="Confirm Password" /></label>
                                </td>
                                <td valign="top" class="value">
                                    <g:passwordField name="passwordConfirm" maxlength="40" value="" />
                                </td>
                            </tr>

                        
                        </tbody>
                    </table>
                </div>
                <div class="buttons">
                    <span class="button"><g:actionSubmit class="save" action="resetPass" value="${message(code: 'default.button.update.label', default: 'Update')}" /></span>
                </div>
            </g:form>
        </div>
    </body>
</html>
