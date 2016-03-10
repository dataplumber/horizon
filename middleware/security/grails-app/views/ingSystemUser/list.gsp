
<%@ page import="gov.nasa.horizon.security.server.IngSystemUser" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'ingSystemUser.label', default: 'IngSystemUser')}" />
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
                        
                            <g:sortableColumn property="id" title="${message(code: 'ingSystemUser.id.label', default: 'Id')}" />
                        
                            <g:sortableColumn property="name" title="${message(code: 'ingSystemUser.name.label', default: 'Name')}" />
                        
                            <g:sortableColumn property="fullname" title="${message(code: 'ingSystemUser.fullname.label', default: 'Fullname')}" />
                        
                            <g:sortableColumn property="email" title="${message(code: 'ingSystemUser.email.label', default: 'Email')}" />
                        
                            <g:sortableColumn property="admin" title="${message(code: 'ingSystemUser.admin.label', default: 'Admin')}" />
                        
                        </tr>
                    </thead>
                    <tbody>
                    <g:each in="${ingSystemUserInstanceList}" status="i" var="ingSystemUserInstance">
                        <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                        
                            <td><g:link action="show" id="${ingSystemUserInstance.id}">${fieldValue(bean: ingSystemUserInstance, field: "id")}</g:link></td>
                        
                            <td>${fieldValue(bean: ingSystemUserInstance, field: "name")}</td>
                        
                            <td>${fieldValue(bean: ingSystemUserInstance, field: "fullname")}</td>
                        
                            <td>${fieldValue(bean: ingSystemUserInstance, field: "email")}</td>
                        
                            <td><g:formatBoolean boolean="${ingSystemUserInstance.admin}" /></td>
                        
                        </tr>
                    </g:each>
                    </tbody>
                </table>
            </div>
            <div class="paginateButtons">
                <g:paginate total="${ingSystemUserInstanceTotal}" />
            </div>
        </div>
    </body>
</html>
