
<%@ page import="gov.nasa.horizon.security.server.IngSystemUserRole" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'ingSystemUserRole.label', default: 'IngSystemUserRole')}" />
        <title><g:message code="default.list.label" args="[entityName]" /></title>
    </head>
    <body>
        <div class="nav">
            <span class="menuButton"><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></span>
            <span class="menuButton"><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></span>
        </div>
        <div class="body">
            <h1><g:message code="default.list.label" args="[entityName]" /></h1>
            <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
            </g:if>
            <div class="list">
                <table>
                    <thead>
                        <tr>
                        
                            <g:sortableColumn property="id" title="${message(code: 'ingSystemUserRole.id.label', default: 'Id')}" />
                        
                            <th><g:message code="ingSystemUserRole.user.label" default="User" /></th>
                        
                            <th><g:message code="ingSystemUserRole.role.label" default="Role" /></th>
                        
                        </tr>
                    </thead>
                    <tbody>
                    <g:each in="${ingSystemUserRoleInstanceList}" status="i" var="ingSystemUserRoleInstance">
                        <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                        
                            <td><g:link action="show" id="${ingSystemUserRoleInstance.id}">${fieldValue(bean: ingSystemUserRoleInstance, field: "id")}</g:link></td>
                        
                            <td>${fieldValue(bean: ingSystemUserRoleInstance, field: "user")}</td>
                        
                            <td>${fieldValue(bean: ingSystemUserRoleInstance, field: "role")}</td>
                        
                        </tr>
                    </g:each>
                    </tbody>
                </table>
            </div>
            <div class="paginateButtons">
                <g:paginate total="${ingSystemUserRoleInstanceTotal}" />
            </div>
        </div>
    </body>
</html>
