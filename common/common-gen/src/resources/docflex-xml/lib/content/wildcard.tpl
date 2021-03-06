<DOCFLEX_TEMPLATE VER='1.9'>
CREATED='2007-08-09 02:06:27'
LAST_UPDATE='2007-09-08 09:40:53'
DESIGNER_TOOL='DocFlex SDK 1.x'
TEMPLATE_TYPE='DocumentTemplate'
DSM_TYPE_ID='xsddoc'
ROOT_ET='xs:%wildcard'
<TEMPLATE_PARAMS>
	PARAM={
		param.name='wildcardType';
		param.type='string';
		param.defaultExpr='instanceOf("xs:any") ? "element" : "attribute"';
		param.autoPassed='false';
	}
</TEMPLATE_PARAMS>
FMT={
	doc.lengthUnits='pt';
	doc.hlink.style.link='cs3';
}
<STYLES>
	CHAR_STYLE={
		style.name='Code Smallest';
		style.id='cs1';
		text.font.name='Courier New';
		text.font.size='7.5';
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
		style.name='Normal';
		style.id='s1';
		style.default='true';
	}
</STYLES>
<ROOT>
	<ATTR_ITER>
		FMT={
			sec.outputStyle='text-par';
			txtfl.delimiter.type='nbtxt';
			txtfl.delimiter.text=';\\n ';
		}
		SCOPE='attr-values'
		ATTR='namespace'
		<BODY>
			<AREA_SEC>
				COND='iterator.value == "##any"'
				<AREA>
					<CTRL_GROUP>
						FMT={
							txtfl.delimiter.type='space';
						}
						<CTRLS>
							<LABEL>
								TEXT='any'
							</LABEL>
							<DATA_CTRL>
								FORMULA='getParam("wildcardType")'
							</DATA_CTRL>
							<LABEL>
								TEXT='with any namespace'
							</LABEL>
						</CTRLS>
					</CTRL_GROUP>
				</AREA>
			</AREA_SEC>
			<AREA_SEC>
				COND='iterator.value == "##other"'
				<AREA>
					<CTRL_GROUP>
						FMT={
							txtfl.delimiter.type='space';
						}
						<CTRLS>
							<LABEL>
								TEXT='any'
							</LABEL>
							<DATA_CTRL>
								FORMULA='getParam("wildcardType")'
							</DATA_CTRL>
							<LABEL>
								TEXT='with non-schema namespace'
							</LABEL>
						</CTRLS>
					</CTRL_GROUP>
				</AREA>
			</AREA_SEC>
			<AREA_SEC>
				COND='iterator.value == "##local"'
				<AREA>
					<CTRL_GROUP>
						FMT={
							txtfl.delimiter.type='space';
						}
						<CTRLS>
							<LABEL>
								TEXT='any local'
							</LABEL>
							<DATA_CTRL>
								FORMULA='getParam("wildcardType")'
							</DATA_CTRL>
							<LABEL>
								TEXT='(without namespace)'
							</LABEL>
						</CTRLS>
					</CTRL_GROUP>
				</AREA>
			</AREA_SEC>
			<AREA_SEC>
				COND='iterator.value == "##targetNamespace"'
				<AREA>
					<CTRL_GROUP>
						FMT={
							txtfl.delimiter.type='space';
						}
						<CTRLS>
							<LABEL>
								TEXT='any'
							</LABEL>
							<DATA_CTRL>
								FORMULA='getParam("wildcardType")'
							</DATA_CTRL>
							<LABEL>
								TEXT='with schema namespace'
							</LABEL>
						</CTRLS>
					</CTRL_GROUP>
				</AREA>
			</AREA_SEC>
			<AREA_SEC>
				COND='sectionBlock.execSecNone'
				<AREA>
					<CTRL_GROUP>
						FMT={
							txtfl.delimiter.type='space';
						}
						<CTRLS>
							<LABEL>
								TEXT='any'
							</LABEL>
							<DATA_CTRL>
								FORMULA='getParam("wildcardType")'
							</DATA_CTRL>
							<LABEL>
								TEXT='with'
							</LABEL>
							<DATA_CTRL>
								FMT={
									text.style='cs1';
								}
								<URL_HLINK>
									TARGET_FRAME_EXPR='"_blank"'
									URL_EXPR='iterator.value.toString()'
								</URL_HLINK>
								FORMULA='iterator.value'
							</DATA_CTRL>
							<LABEL>
								TEXT='namespace'
							</LABEL>
						</CTRLS>
					</CTRL_GROUP>
				</AREA>
			</AREA_SEC>
		</BODY>
		<HEADER>
			<AREA_SEC>
				<AREA>
					<CTRL_GROUP>
						<CTRLS>
							<LABEL>
								TEXT='{'
							</LABEL>
						</CTRLS>
					</CTRL_GROUP>
				</AREA>
			</AREA_SEC>
		</HEADER>
		<FOOTER>
			<AREA_SEC>
				<AREA>
					<CTRL_GROUP>
						<CTRLS>
							<LABEL>
								TEXT='}'
							</LABEL>
						</CTRLS>
					</CTRL_GROUP>
				</AREA>
			</AREA_SEC>
		</FOOTER>
	</ATTR_ITER>
</ROOT>
CHECKSUM='L?JlWUQNPKXfeXN64Dy2bQ'
</DOCFLEX_TEMPLATE>
