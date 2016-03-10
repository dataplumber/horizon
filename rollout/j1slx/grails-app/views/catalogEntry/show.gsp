
<%@ page import="gov.nasa.podaac.j1slx.CatalogEntry" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'catalogEntry.label', default: 'CatalogEntry')}" />
        <title><g:message code="default.show.label" args="[entityName]" /></title>
    </head>
    <body>
        <div class="nav">
            <span class="menuButton"><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></span>
            <span class="menuButton"><g:link class="list" action="list"><g:message code="default.list.label" args="[entityName]" /></g:link></span>
            <span class="menuButton"><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></span>
        </div>
        <div class="body">
            <h1><g:message code="default.show.label" args="[entityName]" /></h1>
            <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
            </g:if>
            <div class="dialog">
                <table>
                    <tbody>
                    
                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="catalogEntry.id.label" default="Id" /></td>
                            
                            <td valign="top" class="value">${fieldValue(bean: catalogEntryInstance, field: "id")}</td>
                            
                        </tr>
                    
                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="catalogEntry.gdrDate.label" default="Gdr Date" /></td>
                            
                            <td valign="top" class="value">${fieldValue(bean: catalogEntryInstance, field: "gdrDate")}</td>
                            
                        </tr>
                    
                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="catalogEntry.sgdrDate.label" default="Sgdr Date" /></td>
                            
                            <td valign="top" class="value">${fieldValue(bean: catalogEntryInstance, field: "sgdrDate")}</td>
                            
                        </tr>
                    
                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="catalogEntry.gdrncDate.label" default="Gdrnc Date" /></td>
                            
                            <td valign="top" class="value">${fieldValue(bean: catalogEntryInstance, field: "gdrncDate")}</td>
                            
                        </tr>
                    
                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="catalogEntry.sgdrncDate.label" default="Sgdrnc Date" /></td>
                            
                            <td valign="top" class="value">${fieldValue(bean: catalogEntryInstance, field: "sgdrncDate")}</td>
                            
                        </tr>
                    
                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="catalogEntry.sshancDate.label" default="Sshanc Date" /></td>
                            
                            <td valign="top" class="value">${fieldValue(bean: catalogEntryInstance, field: "sshancDate")}</td>
                            
                        </tr>
                    
                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="catalogEntry.author.label" default="Author" /></td>
                            
                            <td valign="top" class="value">${fieldValue(bean: catalogEntryInstance, field: "author")}</td>
                            
                        </tr>
                    
                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="catalogEntry.cnesApproval.label" default="Cnes Approval" /></td>
                            
                            <td valign="top" class="value"><g:formatBoolean boolean="${catalogEntryInstance?.cnesApproval}" /></td>
                            
                        </tr>
                    
                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="catalogEntry.cycle.label" default="Cycle" /></td>
                            
                            <td valign="top" class="value">${fieldValue(bean: catalogEntryInstance, field: "cycle")}</td>
                            
                        </tr>
                    
                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="catalogEntry.emailId.label" default="Email Id" /></td>
                            
                            <td valign="top" class="value">${fieldValue(bean: catalogEntryInstance, field: "emailId")}</td>
                            
                        </tr>
                    
                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="catalogEntry.gdrncStaged.label" default="Gdrnc Staged" /></td>
                            
                            <td valign="top" class="value"><g:formatBoolean boolean="${catalogEntryInstance?.gdrncStaged}" /></td>
                            
                        </tr>
                    
                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="catalogEntry.nasaApproval.label" default="Nasa Approval" /></td>
                            
                            <td valign="top" class="value"><g:formatBoolean boolean="${catalogEntryInstance?.nasaApproval}" /></td>
                            
                        </tr>
                    
                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="catalogEntry.productVersion.label" default="Product Version" /></td>
                            
                            <td valign="top" class="value">${fieldValue(bean: catalogEntryInstance, field: "productVersion")}</td>
                            
                        </tr>
                    
                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="catalogEntry.sgdrStaged.label" default="Sgdr Staged" /></td>
                            
                            <td valign="top" class="value"><g:formatBoolean boolean="${catalogEntryInstance?.sgdrStaged}" /></td>
                            
                        </tr>
                    
                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="catalogEntry.sgdrncStaged.label" default="Sgdrnc Staged" /></td>
                            
                            <td valign="top" class="value"><g:formatBoolean boolean="${catalogEntryInstance?.sgdrncStaged}" /></td>
                            
                        </tr>
                    
                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="catalogEntry.sshancStaged.label" default="Sshanc Staged" /></td>
                            
                            <td valign="top" class="value"><g:formatBoolean boolean="${catalogEntryInstance?.sshancStaged}" /></td>
                            
                        </tr>
                    
                    </tbody>
                </table>
            </div>
            <div class="buttons">
                <g:form>
                    <g:hiddenField name="id" value="${catalogEntryInstance?.id}" />
                    <span class="button"><g:actionSubmit class="edit" action="edit" value="${message(code: 'default.button.edit.label', default: 'Edit')}" /></span>
                    <span class="button"><g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" /></span>
                </g:form>
            </div>
        </div>
    </body>
</html>
