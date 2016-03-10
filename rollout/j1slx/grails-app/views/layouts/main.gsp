<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"	"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en" dir="ltr">
<head>
        <title><g:layoutTitle default="Grails" /></title>
        <link rel="stylesheet" href="${resource(dir:'css',file:'main.css')}" />
        <link rel="shortcut icon" href="${resource(dir:'images',file:'favicon.ico')}" type="image/x-icon" />
        <g:layoutHead />
        <g:javascript library="application" />
    
	<meta http-equiv="Content-Type"	content="text/html; charset=iso-8859-1">
	<link type="text/css" rel="stylesheet" media="all" href="http://podaac.jpl.nasa.gov/sites/all/themes/podaac/stylesheets/podaac.css?L" />
	<link type="text/css" rel="stylesheet" media="all" href="/j1-slx/css/jquery-ui.css" />
	<script  type="text/javascript" src="/j1-slx/js/jquery.min.js"></script>
	<script  type="text/javascript" src="/j1-slx/js/jquery-ui.min.js"></script>
	

	<style type="text/css"> 
	#page-inner { width: 990px; text-align: left; margin: 0 auto; }
	 body { text-align: center; margin: 5em 0 0 0; vertical-align: middle; } 
	  #podaac-banner-text
	  {
	    padding-top: 30px;
	    padding-left: 5px;
	    width: 200px;
	    float:left;
	    color:#fff;
	    text-decoration:none;
	    position: relative;
	  }
	  
	  #podaac-j1-text
	  {
	    padding-top: 30px;
	    padding-left: 250px;
	    width: 740px;
	    
	    float:center;
	    color:#fff;
	    overflow:hidden;
	    text-decoration:none;
	    position: absolute; 
	    _position: absolute;
	  }
	 
	/** table **/
	  tr.head { background-color:#336699; }
	  tr.even { background-color:#F0F0F0; }
	  td,th{ font-family: Arial, Helvetica; font-size: 11px; text-align: center }
</style>
</head>
    <body onload="init()">
    	<div id="pagexyz"><div id="page-inner">
  <div id="1header"><div id="header-inner" class="clear-block">
    <!-- JPL/NASA Banner -->
    <div id="banner">
      <div id="jpl-banner">
       <map name="jplHeaderMap" id="jplHeaderMap">
          <area shape="rect" coords="21,6,101,65" href="http://www.nasa.gov" target="_blank" alt="NASA LOGO" />
          <area shape="rect" coords="103,13,379,43" href="http://www.jpl.nasa.gov/" target="_blank" alt="Jet Propulsion Laboratory" />
          <area shape="rect" coords="103,42,378,61" href="http://www.caltech.edu/" target="_blank" alt="California Institute of Technology" />
          <area shape="rect" coords="483,24,547,37" href="http://www.jpl.nasa.gov/index.cfm" target="_blank" alt="JPL Home"/>
          <area shape="rect" coords="563,20,601,38" href="http://jpl.nasa.gov/earth/index.cfm" target="_blank" alt="JPL Earth" />
          <area shape="rect" coords="617,27,706,38" href="http://jpl.nasa.gov/solar-system/index.cfm" target="_blank" alt="JPL Solar System" />
          <area shape="rect" coords="722,22,817,37" href="http://jpl.nasa.gov/stars-galaxies/index.cfm" target="_blank" alt="JPL Stars &amp; Galaxies" />
          <area shape="rect" coords="826,21,959,41" href="http://scienceandtechnology.jpl.nasa.gov/" target="_blank" alt="JPL Science &amp; Technology" />
          <area shape="rect" coords="675,43,757,61" href="http://www.kintera.org/site/apps/ka/ct/contactcustom.asp?c=bsJKK2PNJtH&b=198474" target="_blank" alt="JPL Email News" />
          <area shape="rect" coords="768,41,805,59" href="http://www.jpl.nasa.gov/news/index.cfm" target="_blank" alt="JPL News" />
          <area shape="rect" coords="814,43,847,61"  href="http://jpl.nasa.gov/rss/index.cfm" target="_blank" alt="JPL RSS"/>
          <area shape="rect" coords="858,43,904,62"  href="http://jpl.nasa.gov/podcast/index.cfm" target="_blank" alt="JPL Podcast"/>
          <area shape="rect" coords="916,40,952,63" href="http://jpl.nasa.gov/video/index.cfm" target="_blank" alt="JPL Video" />
       </map>
       <img src="${resource(dir:'images',file:'jplHeader.jpg')}" usemap="#jplHeaderMap"/>
      </div>

      <div id="podaac-banner">
        <a href="http://podaac.jpl.nasa.gov">
          <div style="margin-left:-1px;" id="podaac-banner-logo">
            <img src="${resource(dir:'images',file:'podaac_logo.png')}" />
          </div>

          <div id="podaac-banner-text" >
            <h2>PO.DAAC</h2>
            <h3>PHYSICAL OCEANOGRAPHY</h3>
            <h4>DISTRIBUTED ACTIVE ARCHIVE CENTER</h4>
          </div>
        </a>

          <div id="podaac-j1-text" >
          <h2>Jason-1 Streamlining Web Tool</h2>
          <h3>v4.1.0, 1</h3>
          </div>
      </div>

    </div> <!-- /#banners -->
  </div></div> <!-- /#header -->
    	<div style="background-color:white;"> <!-- the page-->
        <div id="spinner" class="spinner" style="display:none;">
            <img src="${resource(dir:'images',file:'spinner.gif')}" alt="${message(code:'spinner.alt',default:'Loading...')}" />
        </div>
        <div style="float: right; margin-top: 10px; margin-right: 10px;">
        	<g:if test="${session.loggedIn}">
	        	<span>User: ${session.user} | <g:link controller="webtool" action="logout">logout </g:link> </span>
        	</g:if>
        	<g:else>
        		
        	</g:else>
        </div>
        <g:layoutBody />
        <div style="clear:both;"></div>
       <div id="jpl-footer">
        <map name="jplFooter">
	  <area shape="rect" coords="25,10,182,55" href="http://www.usa.gov" target="_blank" alt="usa.gov"/>
	  <area shape="rect" coords="378,27,436,43" href="http://www.jpl.nasa.gov/copyrights.cfm" target="_blank" alt="JPL Privacy" />
	  <area shape="rect" coords="446,24,472,45" href="http://podaac.jpl.nasa.gov/help" alt="PODAAC Help" />
	  <area shape="rect" coords="482,25,550,45" href="mailto:podaac@podaac.jpl.nasa.gov?subject=PO.DAAC Website Feedback" alt="PO.DAAC Feedback" />
        </map>
        <img src="${resource(dir:'images',file:'jplFooter.jpg')}" usemap="#jplFooter"/>
      </div>
		</div> <!--/the page -->
</body>
</html>
