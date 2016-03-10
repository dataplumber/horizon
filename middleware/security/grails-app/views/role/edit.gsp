

<%@ page import="gov.nasa.horizon.security.server.Role" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'role.label', default: 'Role')}" />
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
            <g:hasErrors bean="${roleInstance}">
            <div class="errors">
                <g:renderErrors bean="${roleInstance}" as="list" />
            </div>
            </g:hasErrors>
            <g:form method="post" >
                <g:hiddenField name="id" value="${roleInstance?.id}" />
                <g:hiddenField name="version" value="${roleInstance?.version}" />
                <div class="dialog">
                    <table>
                        <tbody>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                  <label for="realm"><g:message code="role.realm.label" default="Realm" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: roleInstance, field: 'realm', 'errors')}">
                                    <g:select name="realm.id" from="${gov.nasa.horizon.security.server.Realm.list()}" optionKey="id" value="${roleInstance?.realm?.id}"  />
                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                  <label for="roleGroup"><g:message code="role.roleGroup.label" default="Role Group" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: roleInstance, field: 'roleGroup', 'errors')}">
                                    <g:textField name="roleGroup" value="${roleInstance?.roleGroup}" />
                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                  <label for="roleName"><g:message code="role.roleName.label" default="Role Name" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: roleInstance, field: 'roleName', 'errors')}">
                                    <g:textField name="roleName" value="${roleInstance?.roleName}" />
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
