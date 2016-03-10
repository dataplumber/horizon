
<%@ page import="gov.nasa.horizon.security.server.Token" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'token.label', default: 'Token')}" />
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
                        
                            <g:sortableColumn property="id" title="${message(code: 'token.id.label', default: 'Id')}" />
                        
                            <g:sortableColumn property="createDate" title="${message(code: 'token.createDate.label', default: 'Create Date')}" />
                        
                        	<g:sortableColumn property="realm" title="${message(code: 'token.realm.label', default: 'Realm')}" />
                            <%--<th><g:message code="token.realm.label" default="Realm" /></th>--%>
                            <g:sortableColumn property="user" title="${message(code: 'token.user.label', default: 'User')}" />
                        
                        </tr>
                    </thead>
                    <tbody>
                    <g:each in="${tokenInstanceList}" status="i" var="tokenInstance">
                        <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                        
                            <td><g:link action="show" id="${tokenInstance.id}">${fieldValue(bean: tokenInstance, field: "id")}</g:link></td>
                        
                            <td>${ new Date(tokenInstance?.createDate).toString()}
                            </td>
                        
                            <td>${fieldValue(bean: tokenInstance, field: "realm")}</td>
                        
                            <td>${fieldValue(bean: tokenInstance, field: "user")}</td>
                        
                        </tr>
                    </g:each>
                    </tbody>
                </table>
            </div>
            <div class="paginateButtons">
                <g:paginate total="${tokenInstanceTotal}" />
            </div>
        </div>
    </body>
</html>
