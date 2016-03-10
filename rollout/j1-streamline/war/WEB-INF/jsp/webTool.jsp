<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@ include file="/WEB-INF/jsp/headerNew.html" %>
<script language="javascript">document.title = "<fmt:message key="tool"/>"</script>

<head><title><fmt:message key="tool"/></title></head>
<body alink=#B93B8F link=#F0F0F0 vlink=#648FAF>
<div id="page"><div id="page-inner">
  <div id="header"><div id="header-inner" class="clear-block">
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
       <img src="images/jplHeader.jpg" usemap="#jplHeaderMap"/>
      </div>
 
      <div id="podaac-banner">
        <a href="http://podaac.jpl.nasa.gov">
          <div id="podaac-banner-logo">
            <img src="images/podaac_logo.png" />
          </div>      
    
          <div id="podaac-banner-text" >
            <h2>PO.DAAC</h2>
            <h3>PHYSICAL OCEANOGRAPHY</h3>
            <h4>DISTRIBUTED ACTIVE ARCHIVE CENTER</h4>
          </div>
        </a>

          <div id="podaac-j1-text" >
          <h2><fmt:message key="tool"/></h2>
          <h3><fmt:message key="release"/>, <fmt:message key="build"/></h3>
          </div>
      </div>
 
    </div> <!-- /#banners -->
  </div></div> <!-- /#header -->
<center>
<b> <a href="htmlSummary.htm">[View Summary]</a> </b>
<br>

<form method="POST" name="catalog">
<br>
<input type="submit" value=" Submit ">
<br>
<br>

<table align=center border="0px" border-spacing="0px" border-width="0px" cellspacing="0" cellpadding="4" bgcolor="#FFFFFF">

<!-- Show each and every row in the db. -->
<c:forEach var="fieldType" items="${catalog.fields}" varStatus="fieldRow">

<!-- Show the title fields of the table every 20 rows -->
<c:if test="${fieldRow.index % 20 == 0}">
<tr class=head>
<th align="center"> <b><font color="#FFFFFF">Cycle</font></b> </th>
<th align="center"> <b><font color="#FFFFFF">Ver.</font></b> </th>
<th align="center"> <b><font color="#FFFFFF">Author</font></b> </th>
<th align="center"> <b><font color="#FFFFFF">NASA Appro.</font></b> </th>
<th align="center"> <b><font color="#FFFFFF">CNES Appro.</font></b> </th>
<th align="center"> <b><font color="#FFFFFF">GDR Date</font></b> </th>
<th align="center"> <b><font color="#FFFFFF">SGDR Staged</font></b> </th>
<th align="center"> <b><font color="#FFFFFF">SGDR Date</font></b> </th>
<th align="center"> <b><font color="#FFFFFF">GDRnc Staged</font></b> </th>
<th align="center"> <b><font color="#FFFFFF">GDRnc Date</font></b> </th>
<th align="center"> <b><font color="#FFFFFF">SGDRnc Staged</font></b> </th>
<th align="center"> <b><font color="#FFFFFF">SGDRnc Date</font></b> </th>
<th align="center"> <b><font color="#FFFFFF">SSHAnc Staged</font></b> </th>
<th align="center"> <b><font color="#FFFFFF">SSHAnc Date</font></b> </th>
<th align="center"> <b><font color="#FFFFFF">Email ID</font></b> </th>
</tr>
</c:if>

<c:choose>
<c:when test="${fieldRow.index % 2 == 0}">
  <tr class="even">
</c:when>
<c:otherwise>
  <tr>
</c:otherwise>
</c:choose>

<td align="center"> 
  <c:out value="${fieldType.cycle}"/>
</td>
<td align="center">
  <c:out value="${fieldType.version}"/>
</td>
<td align="center">
  <c:choose>
  <c:when test="${fieldType.dataAuthor == 'NASA'}">
   <fmt:message key="rednasa"/>
  </c:when>
  <c:when test="${fieldType.dataAuthor == 'CNES'}">
   <fmt:message key="bluecnes"/>
  </c:when>
  <c:otherwise>
   <fmt:message key="NA"/>
  </c:otherwise>
  </c:choose>
</td>
<td align="center">
  <!-- Bind checkbox with the corresponding bean field -->
  <spring:bind path="catalog.fields[${fieldRow.index}].NASAapproved"> 
  <input type="hidden" name="_<c:out value="${status.expression}"/>">
  <input type="checkbox" name="<c:out value="${status.expression}"/>" value="true"
  <c:if test="${status.value == true}"> CHECKED</c:if> />
  </spring:bind>
</td>
<td align="center">
  <spring:bind path="catalog.fields[${fieldRow.index}].CNESapproved"> 
  <input type="hidden" name="_<c:out value="${status.expression}"/>">
  <input type="checkbox" name="<c:out value="${status.expression}"/>" value="true"
  <c:if test="${status.value == true}"> CHECKED</c:if> />
  </spring:bind>
</td>
<td align="center">
  <c:out value="${fieldType.GDRreleaseDate}"/>
</td>

<td align="center">
  <c:choose>
  <c:when test="${fieldType.SGDRstaged == true}">
   <fmt:message key="greenyes"/>
  </c:when>
  <c:otherwise>
   <fmt:message key="redno"/>
  </c:otherwise>
  </c:choose>
</td>
<td align="center">
  <c:out value="${fieldType.SGDRreleaseDate}"/>
</td>

<td align="center">
  <c:choose>
  <c:when test="${fieldType.GDRnetCDFstaged == true}">
   <fmt:message key="greenyes"/>
  </c:when>
  <c:otherwise>
   <fmt:message key="redno"/>
  </c:otherwise>
  </c:choose>
</td>
<td align="center">
  <c:out value="${fieldType.GDRnetCDFreleaseDate}"/>
</td>
<td align="center">
  <c:choose>
  <c:when test="${fieldType.SGDRnetCDFstaged == true}">
   <fmt:message key="greenyes"/>
  </c:when>
  <c:otherwise>
   <fmt:message key="redno"/>
  </c:otherwise>
  </c:choose>
</td>
<td align="center">
  <c:out value="${fieldType.SGDRnetCDFreleaseDate}"/>
</td>
<td align="center">
  <c:choose>
  <c:when test="${fieldType.SSHAnetCDFstaged == true}">
   <fmt:message key="greenyes"/>
  </c:when>
  <c:otherwise>
   <fmt:message key="redno"/>
  </c:otherwise>
  </c:choose>
</td>
<td align="center">
  <c:out value="${fieldType.SSHAnetCDFreleaseDate}"/>
</td>

<td align="center">
  <c:out value="${fieldType.emailId}"/>
</td>
</c:forEach>
</tr>

</table>

<br>
<input type="submit" value=" Submit ">
<br>
</form>

<br><br>
<b> <a href="htmlSummary.htm">[View Summary]</a></b>
<br><br>
<table border="0">
<tr>
<td>
<b><font color="#FFFFFF"> <fmt:message key="manager"/> </font></b>
</td>
</tr>
</table>
</div></div> <!-- /#page -->

<%@ include file="/WEB-INF/jsp/footerNew.html" %>
