

<%@ page import="gov.nasa.horizon.security.server.Realm" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'realm.label', default: 'Realm')}" />
        <title><g:message code="default.create.label" args="[entityName]" /></title>
    </head>
    <body>
        <div class="nav">
            <span class="menuButton"><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></span>
            <span class="menuButton"><g:link class="list" action="list"><g:message code="default.list.label" args="[entityName]" /></g:link></span>
        </div>
        <div class="body">
            <h1><g:message code="default.create.label" args="[entityName]" /></h1>
            <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
            </g:if>
            <g:hasErrors bean="${realmInstance}">
            <div class="errors">
                <g:renderErrors bean="${realmInstance}" as="list" />
            </div>
            </g:hasErrors>
            <g:form action="save" >
                <div class="dialog">
                    <table>
                        <tbody>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="name"><g:message code="realm.name.label" default="Name" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: realmInstance, field: 'name', 'errors')}">
                                    <g:textField name="name" value="${realmInstance?.name}" />
                                </td>
                            </tr>
                            
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="description"><g:message code="realm.description.label" default="Description" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: realmInstance, field: 'description', 'errors')}">
                                    <g:textField name="description" value="${realmInstance?.description}" />
                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="tokenExpiration"><g:message code="realm.tokenExpiration.label" default="Token Expiration" /></label>
                                    </br>(in days)
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: realmInstance, field: 'tokenExpiration', 'errors')}">
                                    <g:textField name="tokenExpiration" value="${fieldValue(bean: realmInstance, field: 'tokenExpiration')}" />
                                    <span>* A value of 0 will mean the token never expires.</span>
                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="verifier"><g:message code="realm.verifier.label" default="Verifier" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: realmInstance, field: 'verifier', 'errors')}">
                                    <g:select name="verifier.id" from="${gov.nasa.horizon.security.server.Verifier.list()}" optionKey="id" value="${realmInstance?.verifier?.id}"  />
                                </td>
                            </tr>
                        
                        </tbody>
                    </table>
                </div>
                <div class="buttons">
                    <span class="button"><g:submitButton name="create" class="save" value="${message(code: 'default.button.create.label', default: 'Create')}" /></span>
                </div>
            </g:form>
        </div>
    </body>
</html>
