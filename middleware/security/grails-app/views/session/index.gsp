<html>
    <head>
        <title>Welcome to Grails</title>
        <meta name="layout" content="main" />
        <style type="text/css" media="screen">

        #nav {
            margin-top:20px;
            margin-left:30px;
            width:228px;
            float:left;

        }
        .homePagePanel * {
            margin:0px;
        }
        .homePagePanel .panelBody ul {
            list-style-type:none;
            margin-bottom:10px;
        }
        .homePagePanel .panelBody h1 {
            text-transform:uppercase;
            font-size:1.1em;
            margin-bottom:10px;
        }
        .homePagePanel .panelBody {
            background: url(images/leftnav_midstretch.png) repeat-y top;
            margin:0px;
            padding:15px;
        }
        .homePagePanel .panelBtm {
            background: url(images/leftnav_btm.png) no-repeat top;
            height:20px;
            margin:0px;
        }

        .homePagePanel .panelTop {
            background: url(images/leftnav_top.png) no-repeat top;
            height:11px;
            margin:0px;
        }
        h2 {
         color: #48802c; 
            margin-top:15px;
            margin-bottom:5px;
            font-size:1.2em;
        }
        #pageBody {
            margin-top:20px;
            margin-left:280px;
            margin-right:20px;
        }
        </style>
    </head>
    <body>
      
        <div  style="padding-left: 30px; padding-top:10px; min-height: 400px">
          <div>
         <div style="padding-top:10px;">
                <h1>HORIZON Security Service QuickLinks</h1>
            </div>
        
         <g:if test="${flash.message}">
               <div class="message">${flash.message}</div>
            </g:if>
            
            <g:if test="!${session.admin}">
               <h2>Realms</h2> 
               <ul>
                  <li><g:link controller="realm" action="list">List Realms</g:link></li>
                  <li><g:link controller="realm" action="create">New Realm</g:link></li>
               </ul>
            <h2>Tokens</h2>
               <ul>
                  <li><g:link controller="token" action="list">List Tokens</g:link></li>
               </ul>
            
            <h2>Database Users</h2>
               <ul>
                  <li><g:link controller="ingSystemUser" action="list">List Users</g:link></li>
                  <li><g:link controller="ingSystemUser" action="create">Create User</g:link></li>
                  <li><g:link controller="ingSystemUser" action="changePassword">Change your password</g:link></li>
                  
                  <li><g:link controller="ingSystemUserRole" action="list">List/Manage User Roles</g:link></li>
                  <li><g:link controller="ingAccessRole" action="list">List/Manage Access Roles</g:link></li>
               </ul>
            </g:if>
            <g:else>
               <h2>Database Users</h2>
               <ul>
                  <li><g:link controller="ingSystemUser" action="changePassword">Change your password</g:link></li>
               </ul>
            </g:else>
            
        </div>  
        <g:if test="${tokens?.size > 0}">
        <div class="list" style="width: 500px; margin-top:20px; ">
         <h2>Your Active Tokens</h2>
        
                <table>
                    <thead>
                        <tr>
                             <th><g:message code='token.id.label' default='Id' /></th>
                             <th><g:message code='token.createDate.label' default='Date' /></th>
                            <th><g:message code="token.realm.label" default="Realm" /></th>
                           <th><g:message code= 'token.token.label' default='Token' /></th>
                            
                        
                        </tr>
                    </thead>
                    <tbody>
                    <g:each in="${tokens}" status="i" var="tokenInstance">
                        <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                        
                            <td>${fieldValue(bean: tokenInstance, field: "id")}</td>
                        
                            <td><g:formatDate format="yyyy-MM-dd" date="${new Date(tokenInstance?.createDate)}" /></td>
                        
                            <td>${fieldValue(bean: tokenInstance, field: "realm")}</td>
                        
                            <td>${fieldValue(bean: tokenInstance, field: "token")}</td>
                        
                        </tr>
                    </g:each>
                    </tbody>
                </table>
            </div>
            </g:if>
          </div>
    </body>
</html>
