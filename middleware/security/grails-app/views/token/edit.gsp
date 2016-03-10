

<%@ page import="gov.nasa.horizon.security.server.Token" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'token.label', default: 'Token')}" />
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
            <g:hasErrors bean="${tokenInstance}">
            <div class="errors">
                <g:renderErrors bean="${tokenInstance}" as="list" />
            </div>
            </g:hasErrors>
            <g:form method="post" >
                <g:hiddenField name="id" value="${tokenInstance?.id}" />
                <g:hiddenField name="version" value="${tokenInstance?.version}" />
                <g:hiddenField name="createDate" value="${new Date().getTime()}" />
                <div class="dialog">
                    <table>
                        <tbody>
                        
                            
                                    
                                
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                  <label for="realm"><g:message code="token.realm.label" default="Realm" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: tokenInstance, field: 'realm', 'errors')}">
                                    <g:select name="realm.id" from="${gov.nasa.horizon.security.server.Realm.list()}" optionKey="id" value="${tokenInstance?.realm?.id}"  />
                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                  <label for="token"><g:message code="token.token.label" default="Token" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: tokenInstance, field: 'token', 'errors')}">
                                    <g:textField name="token" value="${tokenInstance?.token}" />
                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                  <label for="user"><g:message code="token.user.label" default="User" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: tokenInstance, field: 'user', 'errors')}">
                                    <g:textField name="user" value="${tokenInstance?.user}" />
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
