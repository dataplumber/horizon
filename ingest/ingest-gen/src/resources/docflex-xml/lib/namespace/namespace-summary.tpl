<DOCFLEX_TEMPLATE VER='1.9'>
CREATED='2004-06-21 01:50:00'
LAST_UPDATE='2007-09-08 09:40:53'
DESIGNER_TOOL='DocFlex SDK 1.x'
TEMPLATE_TYPE='DocumentTemplate'
DSM_TYPE_ID='xsddoc'
ROOT_ET='#DOCUMENTS'
<TEMPLATE_PARAMS>
	PARAM={
		param.name='nsURI';
		param.displayName='Namespace URI';
		param.type='string';
	}
	PARAM={
		param.name='doc.namespace.profile';
		param.displayName='Namespace Profile';
		param.type='boolean';
		param.boolean.default='true';
	}
	PARAM={
		param.name='doc.namespace.cs.element';
		param.displayName='Element Summary';
		param.type='enum';
		param.enum.values='all\nglobal\nlocal\nglobal_local\nnone';
	}
	PARAM={
		param.name='doc.namespace.cs.complexType';
		param.displayName='Complex Type Summary';
		param.type='boolean';
		param.boolean.default='true';
	}
	PARAM={
		param.name='doc.namespace.cs.simpleType';
		param.displayName='Simple Type Summary';
		param.type='boolean';
		param.boolean.default='true';
	}
	PARAM={
		param.name='doc.namespace.cs.group';
		param.displayName='Element Group Summary';
		param.type='boolean';
		param.boolean.default='true';
	}
	PARAM={
		param.name='doc.namespace.cs.attribute';
		param.displayName='Global Attribute Summary';
		param.type='boolean';
		param.boolean.default='true';
	}
	PARAM={
		param.name='doc.namespace.cs.attributeGroup';
		param.displayName='Attribute Group Summary';
		param.type='boolean';
		param.boolean.default='true';
	}
</TEMPLATE_PARAMS>
<HTARGET>
	HKEYS={
		'getStringParam("nsURI")';
		'"detail"';
	}
</HTARGET>
FMT={
	doc.lengthUnits='pt';
	doc.hlink.style.link='cs3';
}
<STYLES>
	CHAR_STYLE={
		style.name='Code';
		style.id='cs1';
		text.font.name='Courier New';
		text.font.size='9';
	}
	CHAR_STYLE={
		style.name='Default Paragraph Font';
		style.id='cs2';
		style.default='true';
	}
	CHAR_STYLE={
		style.name='Hyperlink';
		style.id='cs3';
		text.decor.underline='true';
		text.color.foreground='#0000FF';
	}
	PAR_STYLE={
		style.name='Main Heading';
		style.id='s1';
		text.font.name='Verdana';
		text.font.size='13';
		text.font.style.bold='true';
		text.color.foreground='#4477AA';
		par.bkgr.opaque='true';
		par.bkgr.color='#EEEEEE';
		par.border.style='solid';
		par.border.color='#4477AA';
		par.margin.top='0';
		par.margin.bottom='9';
		par.padding.left='5';
		par.padding.right='5';
		par.padding.top='3';
		par.padding.bottom='3';
		par.page.keepTogether='true';
		par.page.keepWithNext='true';
	}
	PAR_STYLE={
		style.name='Normal';
		style.id='s2';
		style.default='true';
	}
	CHAR_STYLE={
		style.name='Normal Smaller';
		style.id='cs4';
		text.font.name='Arial';
		text.font.size='9';
	}
	CHAR_STYLE={
		style.name='Page Header Font';
		style.id='cs5';
		text.font.name='Arial';
		text.font.style.italic='true';
	}
	CHAR_STYLE={
		style.name='Page Number';
		style.id='cs6';
		text.font.size='9';
	}
	CHAR_STYLE={
		style.name='Summary Heading Font';
		style.id='cs7';
		text.font.size='12';
		text.font.style.bold='true';
	}
</STYLES>
<PAGE_HEADER>
	<AREA_SEC>
		FMT={
			text.style='cs5';
			table.cellpadding.both='0';
			table.border.style='none';
			table.border.bottom.style='solid';
		}
		<AREA>
			<CTRL_GROUP>
				FMT={
					par.border.bottom.style='solid';
				}
				<CTRLS>
					<LABEL>
						TEXT='Namespace'
					</LABEL>
					<DATA_CTRL>
						FORMULA='(ns = getStringParam("nsURI")) != "" ? \'"\' + ns + \'"\' : "{global namespace}"'
					</DATA_CTRL>
				</CTRLS>
			</CTRL_GROUP>
		</AREA>
	</AREA_SEC>
</PAGE_HEADER>
<ROOT>
	<AREA_SEC>
		COND='getStringParam("nsURI") == ""'
		FMT={
			par.style='s1';
		}
		<AREA>
			<CTRL_GROUP>
				<CTRLS>
					<LABEL>
						TEXT='{global namespace}'
					</LABEL>
				</CTRLS>
			</CTRL_GROUP>
		</AREA>
	</AREA_SEC>
	<AREA_SEC>
		COND='getStringParam("nsURI") != ""'
		FMT={
			par.style='s1';
		}
		<AREA>
			<CTRL_GROUP>
				<CTRLS>
					<LABEL>
						TEXT='Namespace'
					</LABEL>
					<DATA_CTRL>
						FMT={
							text.font.style.italic='true';
						}
						FORMULA='\'"\' + getStringParam("nsURI") + \'"\''
					</DATA_CTRL>
				</CTRLS>
			</CTRL_GROUP>
		</AREA>
	</AREA_SEC>
	<TEMPLATE_CALL>
		COND='getBooleanParam("doc.namespace.profile")'
		FMT={
			sec.spacing.before='12';
		}
		TEMPLATE_FILE='namespaceProfile.tpl'
	</TEMPLATE_CALL>
	<FOLDER>
		DESCR='COMPONENT SUMMARY'
		FMT={
			sec.spacing.before='14';
		}
		<BODY>
			<TEMPLATE_CALL>
				DESCR='all elements'
				COND='getStringParam("doc.namespace.cs.element") == "all"'
				FMT={
					sec.spacing.before='12';
				}
				TEMPLATE_FILE='allElementSummary.tpl'
			</TEMPLATE_CALL>
			<TEMPLATE_CALL>
				DESCR='global elements'
				COND='param = getStringParam("doc.namespace.cs.element");\nparam == "global" || param == "global_local"'
				FMT={
					sec.spacing.before='12';
				}
				TEMPLATE_FILE='globalElementSummary.tpl'
			</TEMPLATE_CALL>
			<TEMPLATE_CALL>
				DESCR='local elements'
				COND='param = getStringParam("doc.namespace.cs.element");\nparam == "local" || param == "global_local"'
				FMT={
					sec.spacing.before='12';
				}
				TEMPLATE_FILE='localElementSummary.tpl'
			</TEMPLATE_CALL>
			<TEMPLATE_CALL>
				DESCR='complexTypes'
				COND='getBooleanParam("doc.namespace.cs.complexType")'
				FMT={
					sec.spacing.before='12';
				}
				TEMPLATE_FILE='complexTypeSummary.tpl'
			</TEMPLATE_CALL>
			<TEMPLATE_CALL>
				DESCR='simpleTypes'
				COND='getBooleanParam("doc.namespace.cs.simpleType")'
				FMT={
					sec.spacing.before='12';
				}
				TEMPLATE_FILE='simpleTypeSummary.tpl'
			</TEMPLATE_CALL>
			<TEMPLATE_CALL>
				DESCR='element groups'
				COND='getBooleanParam("doc.namespace.cs.group")'
				FMT={
					sec.spacing.before='12';
				}
				TEMPLATE_FILE='groupSummary.tpl'
			</TEMPLATE_CALL>
			<TEMPLATE_CALL>
				DESCR='attributes'
				COND='getBooleanParam("doc.namespace.cs.attribute")'
				FMT={
					sec.spacing.before='12';
				}
				TEMPLATE_FILE='attributeSummary.tpl'
			</TEMPLATE_CALL>
			<TEMPLATE_CALL>
				DESCR='attribute groups'
				COND='getBooleanParam("doc.namespace.cs.attributeGroup")'
				FMT={
					sec.spacing.before='12';
				}
				TEMPLATE_FILE='attributeGroupSummary.tpl'
			</TEMPLATE_CALL>
		</BODY>
	</FOLDER>
	<TEMPLATE_CALL>
		DESCR='Bottom Message'
		COND='output.type == "document"'
		TEMPLATE_FILE='../about.tpl'
	</TEMPLATE_CALL>
</ROOT>
CHECKSUM='pJkL2TnNEXMAXQAAYiH+og'
</DOCFLEX_TEMPLATE>
