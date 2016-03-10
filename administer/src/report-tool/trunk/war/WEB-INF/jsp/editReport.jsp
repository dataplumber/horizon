<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@ include file="/WEB-INF/jsp/headerNew.html" %>
<script language="javascript">document.title = "PO.DAAC <fmt:message key="managerEdit"/>"</script>

<html>
<header><title>PO.DAAC <fmt:message key="managerEdit"/></title>
<link rel="stylesheet" type="text/css" href="css/stylesheet.css" >
<style>
textarea.c1{
background-color:FFFFFF;
color:#000000;
}
textarea.c2{
background-color:FFFFFF;
color:#999999;
}
</style>
<script language="JavaScript">
<!--
function myFocus(index)
{
  var txtName="sections["+index+"]";
  document["editForm"][txtName].className='c1';
  if(document["editForm"][txtName].value == "<fmt:message key="emptyPrompt"/>") {
    document["editForm"][txtName].value = '';
  }
}

function myBlur(index)
{
  var txtName="sections["+index+"]";
  if(document["editForm"][txtName].value == '') {
    document["editForm"][txtName].className='c2';
    document["editForm"][txtName].value = "<fmt:message key="emptyPrompt"/>";
  }
}
-->
</script>
</head>
<body bgcolor=#ffffff>

<br>
<table border="0" width="730">
<tr>
<td colspan="3" align="center">
<h2><fmt:message key="title"/></h2>
</td>
</tr>
<tr>
<td  align="center">
<b><fmt:message key="manager"/></b>
</td>
<td align="left">
<a href="htmlReport.htm?whichDate=<c:out value="${reportContent.reportWeek}"/>">
<b>[<fmt:message key="htmlRpt"/>]</b></a>
</td>
<td align="left">
<a href="report_archive"><b>[<fmt:message key="archivedRpts"/>]</b></a>
</td>
</tr>
</table>

<hr>

<table border="0" width="730">
<tr>
<td colspan="3" align="center">
<fmt:message key="rptWeek"/> <c:out value="${reportContent.reportWeek}"/>
</td>
</tr>
</table>

<img src="images/arrow_backward1.gif" alt="Back" width=22 height=18> <a href="appendReport.htm"><b><fmt:message key="back"/></b></a>
<br>
<br>
<br>

<form method="POST" name="editForm">
<table border="0" width="730">

<c:forEach var="sectionType" items="${reportContent.sections}" varStatus="sectionRow">
  <tr>
    <td colspan="3">
    <fmt:message key="sectitle${sectionRow.index}"/>
    </td>
  </tr>

  <tr>
    <td valign="top" align="right">   
      <b><fmt:message key="managerEdit"/></b> 
    </td>
    <td valign="top" align="center">
      <spring:bind path="reportContent.sections[${sectionRow.index}]">  
      <textarea class="append" rows="8" cols="70" 
         name="<c:out value="${status.expression}"/>"
         onBlur="myBlur(<c:out value="${sectionRow.index}"/>);"
         onFocus="myFocus(<c:out value="${sectionRow.index}"/>);"
         ><c:out value="${status.value}"/></textarea>
      </spring:bind>
    </td>

    <td valign="middle" align="center">
      <input type="submit" value=" Submit " title="Manager submit edited report.">
    </td>
  </tr>
</c:forEach>

</table>
</form>   
</body>
</html>
<%@ include file="/WEB-INF/jsp/footerNew.html" %>
