

<%@ page import="gov.nasa.podaac.j1slx.CatalogEntry" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'catalogEntry.label', default: 'CatalogEntry')}" />
        <title><g:message code="default.edit.label" args="[entityName]" /></title>
    </head>
    <body>
        <div class="nav">
            <span class="menuButton"><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></span>
            <span class="menuButton"><g:link class="list" action="list"><g:message code="default.list.label" args="[entityName]" /></g:link></span>
            <span class="menuButton"><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></span>
        </div>
        <div class="body">
            <h1><g:message code="default.edit.label" args="[entityName]" /></h1>
            <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
            </g:if>
            <g:hasErrors bean="${catalogEntryInstance}">
            <div class="errors">
                <g:renderErrors bean="${catalogEntryInstance}" as="list" />
            </div>
            </g:hasErrors>
            <g:form method="post" >
                <g:hiddenField name="id" value="${catalogEntryInstance?.id}" />
                <g:hiddenField name="version" value="${catalogEntryInstance?.version}" />
                <div class="dialog">
                    <table>
                        <tbody>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                  <label for="gdrDate"><g:message code="catalogEntry.gdrDate.label" default="Gdr Date" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: catalogEntryInstance, field: 'gdrDate', 'errors')}">
                                    <g:textField name="gdrDate" value="${fieldValue(bean: catalogEntryInstance, field: 'gdrDate')}" />
                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                  <label for="sgdrDate"><g:message code="catalogEntry.sgdrDate.label" default="Sgdr Date" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: catalogEntryInstance, field: 'sgdrDate', 'errors')}">
                                    <g:textField name="sgdrDate" value="${fieldValue(bean: catalogEntryInstance, field: 'sgdrDate')}" />
                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                  <label for="gdrncDate"><g:message code="catalogEntry.gdrncDate.label" default="Gdrnc Date" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: catalogEntryInstance, field: 'gdrncDate', 'errors')}">
                                    <g:textField name="gdrncDate" value="${fieldValue(bean: catalogEntryInstance, field: 'gdrncDate')}" />
                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                  <label for="sgdrncDate"><g:message code="catalogEntry.sgdrncDate.label" default="Sgdrnc Date" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: catalogEntryInstance, field: 'sgdrncDate', 'errors')}">
                                    <g:textField name="sgdrncDate" value="${fieldValue(bean: catalogEntryInstance, field: 'sgdrncDate')}" />
                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                  <label for="sshancDate"><g:message code="catalogEntry.sshancDate.label" default="Sshanc Date" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: catalogEntryInstance, field: 'sshancDate', 'errors')}">
                                    <g:textField name="sshancDate" value="${fieldValue(bean: catalogEntryInstance, field: 'sshancDate')}" />
                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                  <label for="author"><g:message code="catalogEntry.author.label" default="Author" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: catalogEntryInstance, field: 'author', 'errors')}">
                                    <g:textField name="author" value="${catalogEntryInstance?.author}" />
                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                  <label for="cnesApproval"><g:message code="catalogEntry.cnesApproval.label" default="Cnes Approval" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: catalogEntryInstance, field: 'cnesApproval', 'errors')}">
                                    <g:checkBox name="cnesApproval" value="${catalogEntryInstance?.cnesApproval}" />
                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                  <label for="cycle"><g:message code="catalogEntry.cycle.label" default="Cycle" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: catalogEntryInstance, field: 'cycle', 'errors')}">
                                    <g:textField name="cycle" value="${fieldValue(bean: catalogEntryInstance, field: 'cycle')}" />
                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                  <label for="emailId"><g:message code="catalogEntry.emailId.label" default="Email Id" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: catalogEntryInstance, field: 'emailId', 'errors')}">
                                    <g:textField name="emailId" value="${catalogEntryInstance?.emailId}" />
                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                  <label for="gdrncStaged"><g:message code="catalogEntry.gdrncStaged.label" default="Gdrnc Staged" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: catalogEntryInstance, field: 'gdrncStaged', 'errors')}">
                                    <g:checkBox name="gdrncStaged" value="${catalogEntryInstance?.gdrncStaged}" />
                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                  <label for="nasaApproval"><g:message code="catalogEntry.nasaApproval.label" default="Nasa Approval" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: catalogEntryInstance, field: 'nasaApproval', 'errors')}">
                                    <g:checkBox name="nasaApproval" value="${catalogEntryInstance?.nasaApproval}" />
                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                  <label for="productVersion"><g:message code="catalogEntry.productVersion.label" default="Product Version" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: catalogEntryInstance, field: 'productVersion', 'errors')}">
                                    <g:textField name="productVersion" value="${catalogEntryInstance?.productVersion}" />
                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                  <label for="sgdrStaged"><g:message code="catalogEntry.sgdrStaged.label" default="Sgdr Staged" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: catalogEntryInstance, field: 'sgdrStaged', 'errors')}">
                                    <g:checkBox name="sgdrStaged" value="${catalogEntryInstance?.sgdrStaged}" />
                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                  <label for="sgdrncStaged"><g:message code="catalogEntry.sgdrncStaged.label" default="Sgdrnc Staged" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: catalogEntryInstance, field: 'sgdrncStaged', 'errors')}">
                                    <g:checkBox name="sgdrncStaged" value="${catalogEntryInstance?.sgdrncStaged}" />
                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                  <label for="sshancStaged"><g:message code="catalogEntry.sshancStaged.label" default="Sshanc Staged" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: catalogEntryInstance, field: 'sshancStaged', 'errors')}">
                                    <g:checkBox name="sshancStaged" value="${catalogEntryInstance?.sshancStaged}" />
                                </td>
                            </tr>
                        
                        </tbody>
                    </table>
                </div>
                <div class="buttons">
                    <span class="button"><g:actionSubmit class="save" action="update" value="${message(code: 'default.button.update.label', default: 'Update')}" /></span>
                    <span class="button"><g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" /></span>
                </div>
            </g:form>
        </div>
    </body>
</html>
