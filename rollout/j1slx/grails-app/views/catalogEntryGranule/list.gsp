
<%@ page import="gov.nasa.podaac.j1slx.CatalogEntryGranule" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'catalogEntryGranule.label', default: 'CatalogEntryGranule')}" />
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
                        
                            <g:sortableColumn property="id" title="${message(code: 'catalogEntryGranule.id.label', default: 'Id')}" />
                        
                            <th><g:message code="catalogEntryGranule.entry.label" default="Entry" /></th>
                        
                            <g:sortableColumn property="granuleId" title="${message(code: 'catalogEntryGranule.granuleId.label', default: 'Granule Id')}" />
                        
                        </tr>
                    </thead>
                    <tbody>
                    <g:each in="${catalogEntryGranuleInstanceList}" status="i" var="catalogEntryGranuleInstance">
                        <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                        
                            <td><g:link action="show" id="${catalogEntryGranuleInstance.id}">${fieldValue(bean: catalogEntryGranuleInstance, field: "id")}</g:link></td>
                        
                            <td>${fieldValue(bean: catalogEntryGranuleInstance, field: "entry")}</td>
                        
                            <td>${fieldValue(bean: catalogEntryGranuleInstance, field: "granuleId")}</td>
                        
                        </tr>
                    </g:each>
                    </tbody>
                </table>
            </div>
            <div class="paginateButtons">
                <g:paginate total="${catalogEntryGranuleInstanceTotal}" />
            </div>
        </div>
    </body>
</html>
