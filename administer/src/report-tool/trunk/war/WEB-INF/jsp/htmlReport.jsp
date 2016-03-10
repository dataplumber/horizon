<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@ include file="/WEB-INF/jsp/headerNew.html" %>
<script language="javascript">document.title = "PO.DAAC <fmt:message key="htmlRpt"/>"</script>

<html>
<header><title>PO.DAAC <fmt:message key="htmlRpt"/></title>
<link rel="stylesheet" type="text/css" href="css/stylesheet.css" >
<link rel="stylesheet" type="text/css" href="css/pre-wrap.css" >
</head>
<body bgcolor=#ffffff>

<br>
<table border="0" width="730">
<tr>
<td colspan="2" align="center">
<h2><fmt:message key="title"/></h2>
</td>
</tr>
<tr>
<td align="center">
<b><fmt:message key="manager"/></b>
</td>
<td align="center">
<a href="report_archive"><b>[<fmt:message key="archivedRpts"/>]</b></a>
</td>
</tr>
</table>

<hr>

<table border="0" width="730">
<tr>
<td align="center">
<fmt:message key="rptWeek"/> <c:out value="${reportContent.reportWeek}"/>
</td>
</tr>
</table>
<img src="images/arrow_backward1.gif" alt="Back" width=22 height=18> <a href="editReport.htm?whichDate=<c:out value="${reportContent.reportWeek}"/>"><b><fmt:message key="back"/></b></a>
<br>
<br>
<br>

<table border="0" width="730">
<c:forEach var="sectionType" items="${reportContent.sections}" varStatus="sectionRow">
    <tr>
    <td>
    <fmt:message key="sectitle${sectionRow.index}"/>
    </td>
    </tr>
    
    <tr>
    <td>
    <spring:bind path="reportContent.sections[${sectionRow.index}]">  
      <c:out value="${status.value}" escapeXml="false" />
    </spring:bind>
    </td>
    </tr>
</c:forEach>
</table>

</body>
</html>
<%@ include file="/WEB-INF/jsp/footerNew.html" %>
