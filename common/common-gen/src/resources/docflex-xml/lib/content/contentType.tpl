<DOCFLEX_TEMPLATE VER='1.9'>
CREATED='2005-04-26 03:31:00'
LAST_UPDATE='2007-09-08 09:40:53'
DESIGNER_TOOL='DocFlex SDK 1.x'
TEMPLATE_TYPE='DocumentTemplate'
DSM_TYPE_ID='xsddoc'
ROOT_ETS={'xs:%element';'xs:complexType'}
FMT={
	doc.lengthUnits='pt';
	doc.hlink.style.link='cs2';
}
<STYLES>
	CHAR_STYLE={
		style.name='Default Paragraph Font';
		style.id='cs1';
		style.default='true';
	}
	CHAR_STYLE={
		style.name='Hyperlink';
		style.id='cs2';
		text.font.style.bold='false';
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
	<ELEMENT_ITER>
		FMT={
			sec.outputStyle='text-par';
		}
		TARGET_ETS={'xs:%complexType';'xs:%simpleType'}
		SCOPE='advanced-location-rules'
		RULES={
			'xs:%element -> {findElementsByKey ("types", getAttrQNameValue("type"))}::(xs:complexType|xs:simpleType)';
			'xs:%element -> (xs:complexType|xs:simpleType)';
			'xs:complexType -> .';
		}
		<BODY>
			<FOLDER>
				MATCHING_ET='xs:%complexType'
				<BODY>
					<AREA_SEC>
						COND='((el = findChild("xs:complexContent")) != null && el.hasAttr ("mixed"))  ?\n  el.getAttrBooleanValue("mixed") : getAttrBooleanValue("mixed")'
						<AREA>
							<CTRL_GROUP>
								<CTRLS>
									<LABEL>
										TEXT='mixed'
									</LABEL>
								</CTRLS>
							</CTRL_GROUP>
						</AREA>
					</AREA_SEC>
					<AREA_SEC>
						COND='sectionBlock.execSecNone && \n{\n  key = (rootElement.instanceOf ("xs:%element") && rootElement.getAttrValue("type") != "")\n    ? findElementByKey ("types", rootElement.getAttrQNameValue("type")).id \n    : rootElement.id;\n\n  findElementByKey ("content-elements", key) != null\n}'
						<AREA>
							<CTRL_GROUP>
								<CTRLS>
									<LABEL>
										TEXT='complex'
									</LABEL>
								</CTRLS>
							</CTRL_GROUP>
						</AREA>
					</AREA_SEC>
					<AREA_SEC>
						COND='sectionBlock.execSecNone &&\nhasChild("xs:simpleContent") && \n! stockSection.contextElement.hasAttrValue("fixed", "")'
						<AREA>
							<CTRL_GROUP>
								<CTRLS>
									<LABEL>
										TEXT='simple'
									</LABEL>
								</CTRLS>
							</CTRL_GROUP>
						</AREA>
					</AREA_SEC>
					<AREA_SEC>
						COND='sectionBlock.execSecNone'
						<AREA>
							<CTRL_GROUP>
								<CTRLS>
									<LABEL>
										TEXT='empty'
									</LABEL>
								</CTRLS>
							</CTRL_GROUP>
						</AREA>
					</AREA_SEC>
				</BODY>
			</FOLDER>
			<FOLDER>
				MATCHING_ET='xs:%simpleType'
				<BODY>
					<AREA_SEC>
						COND='! stockSection.contextElement.hasAttrValue("fixed", "")'
						<AREA>
							<CTRL_GROUP>
								<CTRLS>
									<LABEL>
										TEXT='simple'
									</LABEL>
								</CTRLS>
							</CTRL_GROUP>
						</AREA>
					</AREA_SEC>
					<AREA_SEC>
						COND='sectionBlock.execSecNone'
						<AREA>
							<CTRL_GROUP>
								<CTRLS>
									<LABEL>
										TEXT='empty'
									</LABEL>
								</CTRLS>
							</CTRL_GROUP>
						</AREA>
					</AREA_SEC>
				</BODY>
			</FOLDER>
		</BODY>
		<ELSE>
			<AREA_SEC>
				COND='type = getAttrStringValue("type");\ntype == "" || isXSPredefinedType (QName (type))'
				<AREA>
					<CTRL_GROUP>
						<CTRLS>
							<LABEL>
								TEXT='simple'
							</LABEL>
						</CTRLS>
					</CTRL_GROUP>
				</AREA>
			</AREA_SEC>
			<AREA_SEC>
				COND='sectionBlock.execSecNone'
				<AREA>
					<CTRL_GROUP>
						<CTRLS>
							<LABEL>
								TEXT='???'
							</LABEL>
						</CTRLS>
					</CTRL_GROUP>
				</AREA>
			</AREA_SEC>
		</ELSE>
	</ELEMENT_ITER>
</ROOT>
CHECKSUM='oACpT6oS4PgWe+nMcJl5Ww'
</DOCFLEX_TEMPLATE>
