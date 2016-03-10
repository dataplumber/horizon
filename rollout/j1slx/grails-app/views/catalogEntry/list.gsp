
<%@ page import="gov.nasa.podaac.j1slx.CatalogEntry" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'catalogEntry.label', default: 'CatalogEntry')}" />
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
                        
                            <g:sortableColumn property="id" title="${message(code: 'catalogEntry.id.label', default: 'Id')}" />
                        
                            <g:sortableColumn property="gdrDate" title="${message(code: 'catalogEntry.gdrDate.label', default: 'Gdr Date')}" />
                        
                            <g:sortableColumn property="sgdrDate" title="${message(code: 'catalogEntry.sgdrDate.label', default: 'Sgdr Date')}" />
                        
                            <g:sortableColumn property="gdrncDate" title="${message(code: 'catalogEntry.gdrncDate.label', default: 'Gdrnc Date')}" />
                        
                            <g:sortableColumn property="sgdrncDate" title="${message(code: 'catalogEntry.sgdrncDate.label', default: 'Sgdrnc Date')}" />
                        
                            <g:sortableColumn property="sshancDate" title="${message(code: 'catalogEntry.sshancDate.label', default: 'Sshanc Date')}" />
                        
                        </tr>
                    </thead>
                    <tbody>
                    <g:each in="${catalogEntryInstanceList}" status="i" var="catalogEntryInstance">
                        <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                        
                            <td><g:link action="show" id="${catalogEntryInstance.id}">${fieldValue(bean: catalogEntryInstance, field: "id")}</g:link></td>
                        
                            <td>${fieldValue(bean: catalogEntryInstance, field: "gdrDate")}</td>
                        
                            <td>${fieldValue(bean: catalogEntryInstance, field: "sgdrDate")}</td>
                        
                            <td>${fieldValue(bean: catalogEntryInstance, field: "gdrncDate")}</td>
                        
                            <td>${fieldValue(bean: catalogEntryInstance, field: "sgdrncDate")}</td>
                        
                            <td>${fieldValue(bean: catalogEntryInstance, field: "sshancDate")}</td>
                        
                        </tr>
                    </g:each>
                    </tbody>
                </table>
            </div>
            <div class="paginateButtons">
                <g:paginate total="${catalogEntryInstanceTotal}" />
            </div>
        </div>
    </body>
</html>
