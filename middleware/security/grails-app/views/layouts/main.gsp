<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en" dir="ltr">
<head>
        <title>HORIZON Security Service</title>
        <link rel="stylesheet" href="${resource(dir:'css',file:'main.css')}" />
        <link rel="shortcut icon" href="${resource(dir:'images',file:'favicon.ico')}" type="image/x-icon" />
        <r:layoutResources/>
        <g:layoutHead />
        <g:javascript library="application" />
    
   <meta http-equiv="Content-Type"  content="text/html; charset=iso-8859-1">

</head>
    <body onload="init()">
      <div id="pagexyz"><div id="page-inner">
  <div id="1header"><div id="header-inner" class="clear-block">

  </div></div> <!-- /#header -->
      <div style="background-color:white;"> <!-- the page-->
        <div id="spinner" class="spinner" style="display:none;">
            <img src="${resource(dir:'images',file:'spinner.gif')}" alt="${message(code:'spinner.alt',default:'Loading...')}" />
        </div>
        <div style="float: right; margin-top: 10px; margin-right: 10px;">
         <g:if test="${session.loggedIn}">
            <span>User: ${session.user} | <g:link controller="session" action="logout">logout </g:link> </span>
         </g:if>
         <g:else>
            
         </g:else>
        </div>
        
        <g:layoutBody />
        
</body>
</html>
