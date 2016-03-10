<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@ include file="/WEB-INF/jsp/headerNew.html" %>
<script language="javascript">document.title = "PO.DAAC <fmt:message key="appendRpt"/>"</script>

<html>
<head><title>PO.DAAC <fmt:message key="appendRpt"/></title>
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

<script src="utility.txt"></script>
<script language="JavaScript">
<!--
function myFocus(index)
{
  var txtName="sectionAppends["+index+"]";
  if(document["appendform"][txtName].value == "<fmt:message key="myPrompt"/>") {
    document["appendform"][txtName].className='c1';
    document["appendform"][txtName].value = '';
  }
}

function myBlur(index)
{
  var txtName="sectionAppends["+index+"]";
  if(document["appendform"][txtName].value == '') {
    document["appendform"][txtName].className='c2';
    document["appendform"][txtName].value = "<fmt:message key="myPrompt"/>";
  }
}

// function switchDiv()
//  this function takes the id of a div
//  and calls the other functions required
//  to show that div
//
function switchDiv(div_id)
{
  var style_sheet = getStyleObject(div_id);
  if (style_sheet)
  {
    hideAll();
    document.getElementById(div_id).style.display="block";
  }
  else
  {
    alert("sorry, this only works in browsers that do Dynamic HTML");
  }
}

// function hideAll()
//  hides a bunch of divs
//
function hideAll()
{
   document.getElementById("section1").style.display="none";
   document.getElementById("section2").style.display="none";
   document.getElementById("section3").style.display="none";
   document.getElementById("section4").style.display="none";
}

// function getStyleObject(string) -> returns style object
//  given a string containing the id of an object
//  the function returns the stylesheet of that object
//  or false if it can't find a stylesheet.  Handles
//  cross-browser compatibility issues.
//
function getStyleObject(objectId) {
  // checkW3C DOM, then MSIE 4, then NN 4.
  //
  if(document.getElementById && document.getElementById(objectId)) {
        return document.getElementById(objectId).style;
   }
   else if (document.all && document.all(objectId)) {
        return document.all(objectId).style;
   }
   else if (document.layers && document.layers[objectId]) {
        return document.layers[objectId];
   } else {
        return false;
   }
}

-->
</script>
</head>
<body bgcolor=#ffffff>

<br>
<table border="0" width="730">
<tr>
<td colspan="4" align="center">
<h2><fmt:message key="title"/></h2>
</td>
</tr>
<form method="GET" action="editReport.htm">
<tr>
<td  align="right">
<b><fmt:message key="manager"/></b>
</td>

    <td align="right">
     <select name="whichDate">
          <c:forEach items="${reportContent.weekList}" var="whichWeek">
            <option  <c:if test='${whichWeek == reportContent.reportWeek}'>selected="selected"</c:if> value = "<c:out value="${whichWeek}"/>" ><c:out value="${whichWeek}"/>
            </option>
          </c:forEach>
        </select>  
    </td>
    <td align="left">
    <input type="submit" value="<fmt:message key="editRpt"/>" title="Manager edit report.">
    </td>

<td align="left">
<a href="report_archive"><b>[<fmt:message key="archivedRpts"/>]</b></a>
</td>
</tr>
</table>
</form>

<hr>

<form name="selection">
<table border="0" width="730">
<tr>
<td align="center">
<fmt:message key="rptWeek"/> <c:out value="${reportContent.reportWeek}"/>
</td>
</tr>
<tr>
<td>
</td>
</tr>
<tr>
<td>
</td>
</tr>
<tr>
<td align="center">
<b>Choose your report section:</b>
</td>
</tr>
<tr>
<td align="center">
<input type="radio" name="form_type" value="section1" checked="checked"
  onClick="switchDiv('section1');"><b>Significant Events</b>

<input type="radio" name="form_type" value="section2"
  onClick="switchDiv('section2');"><b>Science Product Engineering</b>

<input type="radio" name="form_type" value="section3"
  onClick="switchDiv('section3');"><b>Development</b>

<input type="radio" name="form_type" value="section4"
  onClick="switchDiv('section4');"><b>Operations</b>
</td>
</tr>
</table>
</form>

<form method="POST" name="appendform">

<c:forEach var="sectionType" items="${reportContent.sections}" varStatus="sectionRow">

  <c:if test='${sectionRow.index == 0}'>
    <div id="section1" style=display:block;>
    <table border="0" width="730">
  </c:if>

  <c:if test='${sectionRow.index == 1}'>
    <div id="section2" style=display:none;>
    <table border="0" width="730">
  </c:if>

  <c:if test='${sectionRow.index == 11}'>
    <div id="section3" style=display:none;>
    <table border="0" width="730">
  </c:if>

  <c:if test='${sectionRow.index == 15}'>
    <div id="section4" style=display:none;>
    <table border="0" width="730">
  </c:if>

  <tr>
    <td colspan="3">
    <fmt:message key="sectitle${sectionRow.index}"/>
    </td>
  </tr>

  <tr>
    <td valign="top" align="right">
      <b><fmt:message key="rptDB"/></b>
    </td>
    <td valign="top" align="center">
      <spring:bind path="reportContent.sections[${sectionRow.index}]">  
      <textarea class="readonly" READONLY rows="8" cols="70" 
         name="<c:out value="${status.expression}"/>"
         ><c:out value="${status.value}"/></textarea>
      </spring:bind>
    </td>
  </tr>

  <tr>
    <td valign="top" align="right">
      <b><fmt:message key="appendRpt"/></b> 
    </td>
    <td valign="top" align="center">
      <spring:bind path="reportContent.sectionAppends[${sectionRow.index}]">  
      <textarea class="append" rows="8" cols="70" 
         name="<c:out value="${status.expression}"/>"
         onBlur="myBlur(<c:out value="${sectionRow.index}"/>);"
         onFocus="myFocus(<c:out value="${sectionRow.index}"/>);"
         ><fmt:message key="myPrompt"/></textarea>
      </spring:bind>
    </td>

    <td valign="middle" align="center">
      <input type="submit" value=" Submit " title="Staff submit report.">
    </td>
  </tr>

  <c:if test='${sectionRow.index == 0 || sectionRow.index == 10 || sectionRow.index == 14 || sectionRow.index == 23}'>
    </table>
    </div>
  </c:if>

</c:forEach>
      
</form>   

</body>
</html>

<%@ include file="/WEB-INF/jsp/footerNew.html" %>
