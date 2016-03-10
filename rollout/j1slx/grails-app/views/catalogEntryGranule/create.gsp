

<%@ page import="gov.nasa.podaac.j1slx.CatalogEntryGranule" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'catalogEntryGranule.label', default: 'CatalogEntryGranule')}" />
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
            <g:hasErrors bean="${catalogEntryGranuleInstance}">
            <div class="errors">
                <g:renderErrors bean="${catalogEntryGranuleInstance}" as="list" />
            </div>
            </g:hasErrors>
            <g:form action="save" >
                <div class="dialog">
                    <table>
                        <tbody>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="entry"><g:message code="catalogEntryGranule.entry.label" default="Entry" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: catalogEntryGranuleInstance, field: 'entry', 'errors')}">
                                    <g:select name="entry.id" from="${gov.nasa.podaac.j1slx.CatalogEntry.list()}" optionKey="id" value="${catalogEntryGranuleInstance?.entry?.id}"  />
                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="granuleId"><g:message code="catalogEntryGranule.granuleId.label" default="Granule Id" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: catalogEntryGranuleInstance, field: 'granuleId', 'errors')}">
                                    <g:textField name="granuleId" value="${fieldValue(bean: catalogEntryGranuleInstance, field: 'granuleId')}" />
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
