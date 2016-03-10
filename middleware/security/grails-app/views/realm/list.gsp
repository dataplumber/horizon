
<%@ page import="gov.nasa.horizon.security.server.Realm" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'realm.label', default: 'Realm')}" />
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
                        
                            <g:sortableColumn property="id" title="${message(code: 'realm.id.label', default: 'Id')}" />
                        
                            <g:sortableColumn property="description" title="${message(code: 'realm.description.label', default: 'Description')}" />
                        
                            <g:sortableColumn property="name" title="${message(code: 'realm.name.label', default: 'Name')}" />
                        
                            <g:sortableColumn property="tokenExpiration" title="${message(code: 'realm.tokenExpiration.label', default: 'Token Expiration')}" />
                        
                            <th><g:message code="realm.verifier.label" default="Verifier" /></th>
                        
                        </tr>
                    </thead>
                    <tbody>
                    <g:each in="${realmInstanceList}" status="i" var="realmInstance">
                        <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                        
                            <td><g:link action="show" id="${realmInstance.id}">${fieldValue(bean: realmInstance, field: "id")}</g:link></td>
                        
                            <td>${fieldValue(bean: realmInstance, field: "description")}</td>
                        
                            <td>${fieldValue(bean: realmInstance, field: "name")}</td>
                        
                            <td>${fieldValue(bean: realmInstance, field: "tokenExpiration")}</td>
                        
                            <td>${fieldValue(bean: realmInstance, field: "verifier")}</td>
                        
                        </tr>
                    </g:each>
                    </tbody>
                </table>
            </div>
            <div class="paginateButtons">
                <g:paginate total="${realmInstanceTotal}" />
            </div>
        </div>
    </body>
</html>
