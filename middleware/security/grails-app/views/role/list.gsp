
<%@ page import="gov.nasa.horizon.security.server.Role" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'role.label', default: 'Role')}" />
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
                        
                            <g:sortableColumn property="id" title="${message(code: 'role.id.label', default: 'Id')}" />
                        
                            <th><g:message code="role.realm.label" default="Realm" /></th>
                        
                            <g:sortableColumn property="roleGroup" title="${message(code: 'role.roleGroup.label', default: 'Role Group')}" />
                        
                            <g:sortableColumn property="roleName" title="${message(code: 'role.roleName.label', default: 'Role Name')}" />
                        
                        </tr>
                    </thead>
                    <tbody>
                    <g:each in="${roleInstanceList}" status="i" var="roleInstance">
                        <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                        
                            <td><g:link action="show" id="${roleInstance.id}">${fieldValue(bean: roleInstance, field: "id")}</g:link></td>
                        
                            <td>${fieldValue(bean: roleInstance, field: "realm")}</td>
                        
                            <td>${fieldValue(bean: roleInstance, field: "roleGroup")}</td>
                        
                            <td>${fieldValue(bean: roleInstance, field: "roleName")}</td>
                        
                        </tr>
                    </g:each>
                    </tbody>
                </table>
            </div>
            <div class="paginateButtons">
                <g:paginate total="${roleInstanceTotal}" />
            </div>
        </div>
    </body>
</html>
