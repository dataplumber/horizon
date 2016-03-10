
<%@ page import="gov.nasa.podaac.j1slx.CatalogEntry" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'catalogEntry.label', default: 'CatalogEntry')}" />
        <title><g:message code="default.list.label" args="[entityName]" /></title>
    </head>
    <body >
        <div class="body">
            <h1><g:message code="default.list.label" args="[entityName]" /></h1>
            <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
            </g:if>
            <div class="list">
                <table>
                    <thead>
                        <tr>
                                                
                            <g:sortableColumn property="cycle" title="${message(code: 'catalogEntry.cycle.label', default: 'cycle')}" />
                        
                        	<g:sortableColumn property="productVersion" title="${message(code: 'catalogEntry.productVersion.label', default: 'Version')}" />
                          
                        
                            <g:sortableColumn property="author" title="${message(code: 'catalogEntry.author.label', default: 'Author')}" />
                        
                            <g:sortableColumn property="nasaApproval" title="${message(code: 'catalogEntry.nasaApprov.label', default: 'NASA Appro.')}" />
                        
                            <g:sortableColumn property="cnesApproval" title="${message(code: 'catalogEntry.cnesApprov.label', default: 'CNES Appro.')}" />
                            
                            <th> approver </th>
                            
                            <g:sortableColumn property="gdrArchTime" title="${message(code: 'catalogEntry.gdrArchTime.label', default: 'GDR Archive Date')}" />
                          	
                          	<g:sortableColumn property="gdrDate" title="${message(code: 'catalogEntry.gdrDate.label', default: 'GDR Date')}" />  
                          	
                          	<g:sortableColumn property="sgdrStaged" title="${message(code: 'catalogEntry.sgdrStaged.label', default: 'SGDR Staged')}" />
                          	<g:sortableColumn property="sgdrDate" title="${message(code: 'catalogEntry.sgdrDate.label', default: 'SGDR Date')}" />  
                          	
                            <g:sortableColumn property="gdrncStaged" title="${message(code: 'catalogEntry.gdrncStaged.label', default: 'GDRnc Staged')}" />
                          	<g:sortableColumn property="gdrncDate" title="${message(code: 'catalogEntry.gdrncDate.label', default: 'GDRnc Date')}" />  
                          	
                          	<g:sortableColumn property="sgdrncStaged" title="${message(code: 'catalogEntry.sgdrncStaged.label', default: 'SGDRnc Staged')}" />
                          	<g:sortableColumn property="sgdrncDate" title="${message(code: 'catalogEntry.sgdrncDate.label', default: 'SGDRnc Date')}" />
                        
                        	<g:sortableColumn property="sshancStaged" title="${message(code: 'catalogEntry.sshancStaged.label', default: 'SSHAnc Staged')}" />
                          	<g:sortableColumn property="sshancDate" title="${message(code: 'catalogEntry.sshancDate.label', default: 'SSHAnc Date')}" />
                        
                        	<g:sortableColumn property="emailId" title="${message(code: 'catalogEntry.emailId.label', default: 'Email Id')}" />
                        
                        </tr>
                    </thead>
                    <tbody>
                    <g:render template="entryTemplate" collection="${catalogEntryInstanceList}" status="i" var="c">
                    </g:render>
                    </tbody>
                </table>
            </div>
            <div class="paginateButtons">
                <g:paginate total="${catalogEntryInstanceTotal}" />
            </div>
        </div>
        
        <div id="dialog" title="Approve Jason-1 Author/Cycle Data For Release">
			<p><span class="ui-icon ui-icon-alert" style="float:left; margin:0 7px 0 0;"></span> You are about to approve the release of Jason-1 data for:</p>
			<strong><p id="dialog-combo"></p></strong>
		</div>
        
    </body>
</html>
