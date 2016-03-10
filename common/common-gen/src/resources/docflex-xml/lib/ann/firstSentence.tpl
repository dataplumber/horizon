<DOCFLEX_TEMPLATE VER='1.9'>
CREATED='2005-04-26 03:31:00'
LAST_UPDATE='2007-09-08 09:40:52'
DESIGNER_TOOL='DocFlex SDK 1.x'
TEMPLATE_TYPE='DocumentTemplate'
DSM_TYPE_ID='xsddoc'
ROOT_ETS={'xs:%annotated';'xs:schema'}
<TEMPLATE_PARAMS>
	PARAM={
		param.name='sec.annotation.xhtml';
		param.displayName='Process XHTML tags';
		param.type='boolean';
		param.boolean.default='true';
		param.hidden='true';
	}
	PARAM={
		param.name='sec.annotation.images.copy';
		param.displayName='Copy images';
		param.type='boolean';
		param.boolean.default='true';
	}
</TEMPLATE_PARAMS>
FMT={
	doc.lengthUnits='pt';
	doc.hlink.style.link='cs3';
}
<STYLES>
	CHAR_STYLE={
		style.name='Code Smaller';
		style.id='cs1';
		text.font.name='Courier New';
		text.font.size='8';
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
	<AREA_SEC>
		COND='getBooleanParam("sec.annotation.xhtml")'
		FMT={
			sec.outputStyle='text-par';
		}
		<AREA>
			<CTRL_GROUP>
				<CTRLS>
					<DATA_CTRL>
						FMT={
							ctrl.option.text.collapseSpaces='true';
							ctrl.option.text.noBlankOutput='true';
							text.option.renderNLs='false';
							txtfl.option.renderEmbeddedHTML='true';
						}
						FORMULA='firstSentence (\n  callStockSection("HTMLDoc"),\n  true,\n  output.format.renderEmbeddedHTML\n)'
					</DATA_CTRL>
				</CTRLS>
			</CTRL_GROUP>
		</AREA>
	</AREA_SEC>
	<AREA_SEC>
		COND='sectionBlock.execSecNone'
		FMT={
			sec.outputStyle='text-par';
		}
		<AREA>
			<CTRL_GROUP>
				<CTRLS>
					<DATA_CTRL>
						FMT={
							ctrl.option.text.collapseSpaces='true';
							ctrl.option.text.noBlankOutput='true';
							text.option.renderNLs='false';
						}
						FORMULA='firstSentence(mergeStrings(\n  getValuesByLPath ("xs:annotation/xs:documentation//(#TEXT | #CDATA)")\n), false)'
					</DATA_CTRL>
				</CTRLS>
			</CTRL_GROUP>
		</AREA>
	</AREA_SEC>
</ROOT>
<STOCK_SECTIONS>
	<ATTR_ITER>
		DESCR='generates the list of the element\'s attributes'
		FMT={
			sec.outputStyle='text-par';
			txtfl.delimiter.type='none';
		}
		SCOPE='enumerated-attrs'
		EXCL_PASSED=false
		FILTER='name = iterator.attr.name;\nname != "xmlns" && ! name.startsWith ("xmlns:")'
		SS_NAME='AttrList'
		<BODY>
			<AREA_SEC>
				<AREA>
					<CTRL_GROUP>
						<CTRLS>
							<DELIMITER>
							</DELIMITER>
							<DATA_CTRL>
								FORMULA='iterator.attr.name'
							</DATA_CTRL>
							<LABEL>
								TEXT='="'
							</LABEL>
							<DATA_CTRL>
								FORMULA='encodeXMLChars (\n  iterator.attr.dsmAttr.rawValue,\n  true, true, true, false\n)'
							</DATA_CTRL>
							<LABEL>
								TEXT='"'
							</LABEL>
						</CTRLS>
					</CTRL_GROUP>
				</AREA>
			</AREA_SEC>
		</BODY>
	</ATTR_ITER>
	<ELEMENT_ITER>
		DESCR='iterate by all nodes contained in all \'xs:documentation\' elements'
		FMT={
			sec.outputStyle='text-par';
			txtfl.delimiter.type='none';
		}
		TARGET_ET='<ANY>'
		SCOPE='advanced-location-rules'
		RULES={
			'* -> xs:annotation/xs:documentation/*';
		}
		SS_NAME='HTMLDoc'
		<BODY>
			<SS_CALL>
				DESCR='processing of each node'
				FMT={
					sec.indent.left='10';
				}
				SS_NAME='Node'
			</SS_CALL>
		</BODY>
	</ELEMENT_ITER>
	<FOLDER>
		DESCR='processing the documentation node'
		FMT={
			sec.outputStyle='text-par';
			txtfl.delimiter.type='none';
		}
		SS_NAME='Node'
		<BODY>
			<FOLDER>
				DESCR='in case of a pseudo-element (non-element node) process only TEXT or CDATA nodes (see Component | Context Element tab in the Area Section)'
				COND='contextElement.dsmElement.pseudoElement'
				<BODY>
					<AREA_SEC>
						MATCHING_ETS={'#CDATA';'#TEXT'}
						<AREA>
							<CTRL_GROUP>
								<CTRLS>
									<DATA_CTRL>
										FORMULA='text = contextElement.value.toString();\n\nencodeXMLChars (\n  stockSection.recursionDepth > 0 ? text :\n    text.trim (iterator.isFirstItem, iterator.isLastItem)\n)'
									</DATA_CTRL>
								</CTRLS>
							</CTRL_GROUP>
						</AREA>
					</AREA_SEC>
				</BODY>
			</FOLDER>
			<ELEMENT_ITER>
				DESCR='otherwise, if the current element belongs to the XHTML namespace and processing of XHTML tags is enabled, reprint the element\'s tags so as they look as an ordinary HTML and process the element\'s content.'
				COND='sectionBlock.execSecNone &&\ngetBooleanParam("sec.annotation.xhtml") &&\ncontextElement.belongsToNS ("xhtml")'
				TARGET_ET='<ANY>'
				SCOPE='simple-location-rules'
				RULES={
					'* -> *';
				}
				<BODY>
					<SS_CALL>
						DESCR='calls this stock-section recursively'
						FMT={
							sec.indent.left='10';
						}
						SS_NAME='Node'
					</SS_CALL>
				</BODY>
				<HEADER>
					<AREA_SEC>
						<AREA>
							<CTRL_GROUP>
								<CTRLS>
									<LABEL>
										TEXT='<'
									</LABEL>
									<DATA_CTRL>
										FORMULA='contextElement.dsmElement.localName'
									</DATA_CTRL>
									<SS_CALL_CTRL>
										SS_NAME='AttrList'
									</SS_CALL_CTRL>
									<LABEL>
										TEXT='>'
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
										TEXT='</'
									</LABEL>
									<DATA_CTRL>
										FORMULA='contextElement.dsmElement.localName'
									</DATA_CTRL>
									<LABEL>
										TEXT='>'
									</LABEL>
								</CTRLS>
							</CTRL_GROUP>
						</AREA>
					</AREA_SEC>
				</FOOTER>
				<ELSE>
					DESCR='this is executed when no child nodes encountered -- the case of a simple element'
					<AREA_SEC>
						COND='! contextElement.dsmElement.localName.equalsIgnoreCase ("img")'
						<AREA>
							<CTRL_GROUP>
								<CTRLS>
									<LABEL>
										TEXT='<'
									</LABEL>
									<DATA_CTRL>
										FORMULA='contextElement.dsmElement.localName'
									</DATA_CTRL>
									<SS_CALL_CTRL>
										SS_NAME='AttrList'
									</SS_CALL_CTRL>
									<LABEL>
										TEXT='>'
									</LABEL>
								</CTRLS>
							</CTRL_GROUP>
						</AREA>
					</AREA_SEC>
				</ELSE>
			</ELEMENT_ITER>
			<ELEMENT_ITER>
				DESCR='otherwise, in case of any non-XHTML element, only process the element content'
				COND='sectionBlock.execSecNone'
				TARGET_ET='<ANY>'
				SCOPE='simple-location-rules'
				RULES={
					'* -> *';
				}
				<BODY>
					<SS_CALL>
						DESCR='calls this stock-section recursively'
						FMT={
							sec.indent.left='10';
						}
						SS_NAME='Node'
					</SS_CALL>
				</BODY>
			</ELEMENT_ITER>
		</BODY>
	</FOLDER>
</STOCK_SECTIONS>
CHECKSUM='4+4Krh3i351HB+7RzSa2Ug'
</DOCFLEX_TEMPLATE>
