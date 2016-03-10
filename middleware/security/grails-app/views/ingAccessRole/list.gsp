
<%@ page import="gov.nasa.horizon.security.server.IngAccessRole" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'ingAccessRole.label', default: 'IngAccessRole')}" />
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
                        
                            <g:sortableColumn property="id" title="${message(code: 'ingAccessRole.id.label', default: 'Id')}" />
                        
                            <g:sortableColumn property="name" title="${message(code: 'ingAccessRole.name.label', default: 'Name')}" />
                        
                            <g:sortableColumn property="capabilities" title="${message(code: 'ingAccessRole.capabilities.label', default: 'Capabilities')}" />
                        
                        </tr>
                    </thead>
                    <tbody>
                    <g:each in="${ingAccessRoleInstanceList}" status="i" var="ingAccessRoleInstance">
                        <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                        
                            <td><g:link action="show" id="${ingAccessRoleInstance.id}">${fieldValue(bean: ingAccessRoleInstance, field: "id")}</g:link></td>
                        
                            <td>${fieldValue(bean: ingAccessRoleInstance, field: "name")}</td>
                        
                            <td>${fieldValue(bean: ingAccessRoleInstance, field: "capabilities")}</td>
                        
                        </tr>
                    </g:each>
                    </tbody>
                </table>
            </div>
            <div class="paginateButtons">
                <g:paginate total="${ingAccessRoleInstanceTotal}" />
            </div>
        </div>
    </body>
</html>
