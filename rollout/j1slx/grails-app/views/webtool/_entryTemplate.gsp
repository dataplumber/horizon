<tr>
	<td>
     	${c.getCycle()}
	</td>
	<td>
     	${c.getProductVersion()}
	</td>
	<td>
     	${c.getAuthor()}
	</td>
	<td>
		<g:checkBox name='${c.getCycle()}_${c.getAuthor()}_NASA' checked="${c.isNasaApproval()}" onClick="approveGDR(${c.getCycle()},'NASA','${c.getAuthor()}');"> </g:checkBox>
	</td>  
	<td>
     	<g:checkBox name='${c.getCycle()}_${c.getAuthor()}_CNES' checked="${c.isCnesApproval()}" onClick="approveGDR(${c.getCycle()},'CNES','${c.getAuthor()}');"> </g:checkBox>
	</td>
	<td>
		${c.getApprover()}
	</td>
	<td>
     	<g:formateDateObject date="${c.getGdrArchTime()}"/>
	</td>
	<td>
     	<g:formateDateObject date="${c.getGdrDate()}"/>
	</td>
	<td>
     	<g:if test="${c.isSgdrStaged() == true}">
     		<span style="color:green"> YES </span>
     	</g:if>
     	<g:else>
     		<span style="color:red"> NO </span>
     	</g:else>
	</td>
	<td>
     	<g:formateDateObject date="${c.getSgdrDate()}"/>
	</td>
	<td>
		<g:if test="${c.isGdrncStaged() == true}">
     		<span style="color:green"> YES </span>
     	</g:if>
     	<g:else>
     		<span style="color:red"> NO </span>
     	</g:else>
	</td>
	<td>
     	<g:formateDateObject date="${c.getGdrncDate()}"/>
	</td>
	<td>
     	<g:if test="${c.isSgdrncStaged() == true}">
     		<span style="color:green"> YES </span>
     	</g:if>
     	<g:else>
     		<span style="color:red"> NO </span>
     	</g:else>
	</td>
	<td>
     	<g:formateDateObject date="${c.getSgdrncDate()}"/>
	</td>
	<td>
     	<g:if test="${c.isSshancStaged() == true}">
     		<span style="color:green"> YES </span>
     	</g:if>
     	<g:else>
     		<span style="color:red"> NO </span>
     	</g:else>
	</td>
	<td>
     	<g:formateDateObject date="${c.getSshancDate()}"/>
	</td>
	<td>
     	${c.getEmailId()}
	</td>
</tr>
