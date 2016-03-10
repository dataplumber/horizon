<DOCFLEX_TEMPLATE VER='1.9'>
CREATED='2005-10-13 03:37:00'
LAST_UPDATE='2007-09-08 09:40:54'
DESIGNER_TOOL='DocFlex SDK 1.x'
TEMPLATE_TYPE='DocumentTemplate'
DSM_TYPE_ID='xsddoc'
ROOT_ETS={'xs:complexType';'xs:simpleType'}
<TEMPLATE_PARAMS>
	PARAM={
		param.name='nsURI';
		param.displayName='type namespace URI';
		param.description='The namespace to which this type belongs';
		param.type='string';
	}
	PARAM={
		param.name='usageCount';
		param.description='number of locations where this type is used';
		param.type='int';
		param.defaultExpr='countElementsByKey (\n  "type-usage",\n  QName (getStringParam("nsURI"), getAttrStringValue("name"))\n)';
		param.hidden='true';
	}
	PARAM={
		param.name='contentMapKey';
		param.displayName='"content-attributes", "content-elements" map key';
		param.description='The key for "content-attributes" and "content-elements" maps to find items associated with this component';
		param.type='object';
		param.defaultExpr='contextElement.id';
		param.hidden='true';
	}
	PARAM={
		param.name='attributeCount';
		param.displayName='number of all attributes';
		param.description='total number of attributes declared for this component';
		param.type='int';
		param.defaultExpr='countElementsByKey (\n  "content-attributes", \n  getParam("contentMapKey"),\n  BooleanQuery (! instanceOf ("xs:anyAttribute"))\n)';
		param.hidden='true';
	}
	PARAM={
		param.name='elementCount';
		param.displayName='number of all content elements';
		param.description='total number of content elements declared for this component';
		param.type='int';
		param.defaultExpr='countElementsByKey (\n  "content-elements", \n  getParam("contentMapKey"),\n  BooleanQuery (! instanceOf ("xs:any"))\n)';
		param.hidden='true';
	}
	PARAM={
		param.name='anyAttribute';
		param.displayName='component has any-attribute';
		param.description='indicates that the component allows any attributes';
		param.type='boolean';
		param.defaultExpr='checkElementsByKey (\n  "content-attributes", \n  getParam("contentMapKey"),\n  BooleanQuery (instanceOf ("xs:anyAttribute"))\n)';
		param.hidden='true';
	}
	PARAM={
		param.name='anyElement';
		param.displayName='component has any-content-element';
		param.description='indicates that the component allows any content elements';
		param.type='boolean';
		param.defaultExpr='checkElementsByKey (\n  "content-elements", \n  getParam("contentMapKey"),\n  BooleanQuery (instanceOf ("xs:any"))\n)';
		param.hidden='true';
	}
	PARAM={
		param.name='ownAttributeCount';
		param.displayName='number of component\'s own attributes';
		param.description='number of attributes defined within this component';
		param.type='int';
		param.defaultExpr='countElementsByKey (\n  "content-attributes", \n  getParam("contentMapKey"),\n  BooleanQuery (\n    ! instanceOf ("xs:anyAttribute") &&\n    findPredecessorByType("xs:%element;xs:complexType;xs:attributeGroup").id \n    == rootElement.id\n  )\n)';
		param.hidden='true';
	}
	PARAM={
		param.name='ownElementCount';
		param.displayName='number of component\'s own content elements';
		param.description='number of content elements defined within this component';
		param.type='int';
		param.defaultExpr='countElementsByKey (\n  "content-elements", \n  getParam("contentMapKey"),\n  BooleanQuery (\n    ! instanceOf ("xs:any") &&\n    findPredecessorByType("xs:%element;xs:complexType;xs:group").id \n    == rootElement.id\n  )\n)';
		param.hidden='true';
	}
	PARAM={
		param.name='ownAnyAttribute';
		param.displayName='any-attribute is defined in this component';
		param.description='indicates that this component contains the wildcard attribute definition';
		param.type='boolean';
		param.defaultExpr='checkElementsByKey (\n  "content-attributes", \n  getParam("contentMapKey"),\n  BooleanQuery (\n    instanceOf ("xs:anyAttribute") &&\n    findPredecessorByType("xs:%element;xs:complexType;xs:attributeGroup").id \n    == rootElement.id\n  )\n)';
		param.hidden='true';
	}
	PARAM={
		param.name='ownAnyElement';
		param.displayName='any-content-element is defined in this component';
		param.description='indicates that this component contains the wildcard content element definition';
		param.type='boolean';
		param.defaultExpr='checkElementsByKey (\n  "content-elements", \n  getParam("contentMapKey"),\n  BooleanQuery (\n    instanceOf ("xs:any") &&\n    findPredecessorByType("xs:%element;xs:complexType;xs:group").id \n    == rootElement.id\n  )\n)';
		param.hidden='true';
	}
	PARAM={
		param.name='showNS';
		param.type='boolean';
		param.boolean.default='true';
	}
	PARAM={
		param.name='showSchema';
		param.type='boolean';
		param.boolean.default='true';
	}
	PARAM={
		param.name='sec.attributes.ownOnly';
		param.displayName='Component\'s Only';
		param.type='boolean';
	}
	PARAM={
		param.name='sec.contentElements.ownOnly';
		param.displayName='Component\'s Only';
		param.type='boolean';
	}
	PARAM={
		param.name='page.refs';
		param.displayName='Generate page references';
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
	CHAR_STYLE={
		style.name='Page Number Small';
		style.id='cs4';
		text.font.name='Courier New';
		text.font.size='8';
	}
	PAR_STYLE={
		style.name='Property Title';
		style.id='s2';
		text.font.size='8';
		text.font.style.bold='true';
		par.lineHeight='11';
		par.margin.right='7';
	}
	PAR_STYLE={
		style.name='Property Value';
		style.id='s3';
		text.font.name='Verdana';
		text.font.size='8';
		par.lineHeight='11';
	}
</STYLES>
<ROOT>
	<FOLDER>
		FMT={
			sec.outputStyle='table';
			table.cellpadding.both='0';
			table.border.style='none';
			table.page.keepTogether='true';
			table.option.borderStylesInHTML='true';
		}
		<BODY>
			<AREA_SEC>
				COND='getBooleanParam("showNS")'
				FMT={
					trow.align.vert='Top';
				}
				<AREA>
					<CTRL_GROUP>
						<CTRLS>
							<LABEL>
								FMT={
									ctrl.size.width='54.8';
									ctrl.size.height='17.3';
									par.style='s2';
								}
								TEXT='Namespace:'
							</LABEL>
							<DATA_CTRL>
								FMT={
									ctrl.size.width='444.8';
									ctrl.size.height='17.3';
									text.style='cs1';
									par.style='s3';
								}
								<DOC_HLINK>
									HKEYS={
										'getStringParam("nsURI")';
										'"detail"';
									}
								</DOC_HLINK>
								<URL_HLINK>
									COND='getStringParam("nsURI") != ""'
									ALT_HLINK
									TARGET_FRAME_EXPR='"_blank"'
									URL_EXPR='getStringParam("nsURI")'
								</URL_HLINK>
								FORMULA='(ns = getParam("nsURI")) != "" ? ns : "{global namespace}"'
							</DATA_CTRL>
						</CTRLS>
					</CTRL_GROUP>
				</AREA>
			</AREA_SEC>
			<AREA_SEC>
				MATCHING_ET='xs:complexType'
				FMT={
					trow.align.vert='Top';
				}
				<AREA>
					<CTRL_GROUP>
						<CTRLS>
							<LABEL>
								FMT={
									ctrl.size.width='54.8';
									ctrl.size.height='98.3';
									par.style='s2';
								}
								TEXT='Content:'
							</LABEL>
							<PANEL>
								FMT={
									content.outputStyle='text-par';
									ctrl.size.width='444.8';
									ctrl.size.height='98.3';
									txtfl.delimiter.type='text';
									txtfl.delimiter.text=', ';
									par.style='s3';
								}
								<AREA>
									<CTRL_GROUP>
										<CTRLS>
											<TEMPLATE_CALL_CTRL>
												TEMPLATE_FILE='../content/contentType.tpl'
											</TEMPLATE_CALL_CTRL>
											<PANEL>
												COND='count = getIntParam("attributeCount") + \n        getBooleanParam("anyAttribute").toInt();\n\ncount > 0 ? { setVar ("count", count); true } : false'
												FMT={
													ctrl.size.width='276.8';
													ctrl.size.height='38.3';
												}
												<AREA>
													<CTRL_GROUP>
														<CTRLS>
															<DATA_CTRL>
																COND='getIntParam("attributeCount") > 0'
																FORMULA='getIntParam("attributeCount")'
															</DATA_CTRL>
															<DELIMITER>
																COND='getIntParam("attributeCount") > 0'
																FMT={
																	txtfl.delimiter.type='text';
																	txtfl.delimiter.text='+';
																}
															</DELIMITER>
															<LABEL>
																COND='getBooleanParam("anyAttribute")'
																TEXT='any'
															</LABEL>
															<DELIMITER>
																FMT={
																	txtfl.delimiter.type='nbsp';
																}
															</DELIMITER>
															<LABEL>
																COND='getVar("count").toInt() == 1'
																<DOC_HLINK>
																	COND='! getBooleanParam("sec.attributes.ownOnly")'
																	HKEYS={
																		'contextElement.id';
																		'"attribute-detail"';
																	}
																</DOC_HLINK>
																TEXT='attribute'
															</LABEL>
															<LABEL>
																COND='getVar("count").toInt() > 1'
																<DOC_HLINK>
																	COND='! getBooleanParam("sec.attributes.ownOnly")'
																	HKEYS={
																		'contextElement.id';
																		'"attribute-detail"';
																	}
																</DOC_HLINK>
																TEXT='attributes'
															</LABEL>
														</CTRLS>
													</CTRL_GROUP>
												</AREA>
											</PANEL>
											<PANEL>
												COND='count = getIntParam("elementCount") + \n        getBooleanParam("anyElement").toInt();\n\ncount > 0 ? { setVar ("count", count); true } : false'
												FMT={
													ctrl.size.width='286.5';
													ctrl.size.height='38.3';
												}
												<AREA>
													<CTRL_GROUP>
														<CTRLS>
															<DATA_CTRL>
																COND='getIntParam("elementCount") > 0'
																FORMULA='getIntParam("elementCount")'
															</DATA_CTRL>
															<DELIMITER>
																COND='getIntParam("elementCount") > 0'
																FMT={
																	txtfl.delimiter.type='text';
																	txtfl.delimiter.text='+';
																}
															</DELIMITER>
															<LABEL>
																COND='getBooleanParam("anyElement")'
																TEXT='any'
															</LABEL>
															<DELIMITER>
																FMT={
																	txtfl.delimiter.type='nbsp';
																}
															</DELIMITER>
															<LABEL>
																COND='getVar("count").toInt() == 1'
																<DOC_HLINK>
																	COND='! getBooleanParam("sec.contentElements.ownOnly")'
																	HKEYS={
																		'contextElement.id';
																		'"content-element-detail"';
																	}
																</DOC_HLINK>
																TEXT='element'
															</LABEL>
															<LABEL>
																COND='getVar("count").toInt() > 1'
																<DOC_HLINK>
																	COND='! getBooleanParam("sec.contentElements.ownOnly")'
																	HKEYS={
																		'contextElement.id';
																		'"content-element-detail"';
																	}
																</DOC_HLINK>
																TEXT='elements'
															</LABEL>
														</CTRLS>
													</CTRL_GROUP>
												</AREA>
											</PANEL>
										</CTRLS>
									</CTRL_GROUP>
								</AREA>
							</PANEL>
						</CTRLS>
					</CTRL_GROUP>
				</AREA>
			</AREA_SEC>
			<AREA_SEC>
				COND='getBooleanParam("showSchema")'
				FMT={
					trow.align.vert='Top';
				}
				<AREA>
					<CTRL_GROUP>
						<CTRLS>
							<LABEL>
								FMT={
									ctrl.size.width='54.8';
									ctrl.size.height='98.3';
									par.style='s2';
								}
								TEXT='Defined:'
							</LABEL>
							<PANEL>
								FMT={
									ctrl.size.width='444.8';
									ctrl.size.height='98.3';
									par.style='s3';
								}
								<AREA>
									<CTRL_GROUP>
										<CTRLS>
											<LABEL>
												TEXT='globally in'
											</LABEL>
											<DATA_CTRL>
												<DOC_HLINK>
													HKEYS={
														'getXMLDocument().id';
														'"detail"';
													}
												</DOC_HLINK>
												FORMULA='getXMLDocument().getAttrStringValue("xmlName")'
											</DATA_CTRL>
											<PANEL>
												COND='hyperTargetExists (Array (contextElement.id, "xml-source"))'
												FMT={
													ctrl.size.width='289.5';
													ctrl.size.height='59.3';
												}
												<AREA>
													<CTRL_GROUP>
														<CTRLS>
															<DELIMITER>
																FMT={
																	txtfl.delimiter.type='text';
																	txtfl.delimiter.text=', ';
																}
															</DELIMITER>
															<LABEL>
																TEXT='see'
															</LABEL>
															<LABEL>
																<DOC_HLINK>
																	HKEYS={
																		'contextElement.id';
																		'"xml-source"';
																	}
																</DOC_HLINK>
																TEXT='XML source'
															</LABEL>
															<PANEL>
																COND='output.format.supportsPagination && \ngetBooleanParam("page.refs")'
																FMT={
																	ctrl.size.width='186';
																	ctrl.size.height='38.3';
																	txtfl.delimiter.type='none';
																}
																<AREA>
																	<CTRL_GROUP>
																		<CTRLS>
																			<DELIMITER>
																				FMT={
																					txtfl.delimiter.type='nbsp';
																				}
																			</DELIMITER>
																			<LABEL>
																				FMT={
																					text.style='cs4';
																				}
																				TEXT='['
																			</LABEL>
																			<DATA_CTRL>
																				FMT={
																					ctrl.option.noHLinkFmt='true';
																					text.style='cs4';
																					text.hlink.fmt='none';
																				}
																				<DOC_HLINK>
																					HKEYS={
																						'contextElement.id';
																						'"xml-source"';
																					}
																				</DOC_HLINK>
																				DOCFIELD='page-htarget'
																			</DATA_CTRL>
																			<LABEL>
																				FMT={
																					text.style='cs4';
																				}
																				TEXT=']'
																			</LABEL>
																		</CTRLS>
																	</CTRL_GROUP>
																</AREA>
															</PANEL>
														</CTRLS>
													</CTRL_GROUP>
												</AREA>
											</PANEL>
										</CTRLS>
									</CTRL_GROUP>
								</AREA>
							</PANEL>
						</CTRLS>
					</CTRL_GROUP>
				</AREA>
			</AREA_SEC>
			<AREA_SEC>
				CONTEXT_ELEMENT_EXPR='findElementByKey ("redefined-component", contextElement.id)'
				MATCHING_ET='<ANY>'
				FMT={
					trow.align.vert='Top';
				}
				<AREA>
					<CTRL_GROUP>
						<CTRLS>
							<LABEL>
								FMT={
									ctrl.size.width='54.8';
									ctrl.size.height='38.3';
									par.style='s2';
								}
								TEXT='Redefines:'
							</LABEL>
							<PANEL>
								FMT={
									ctrl.size.width='444.8';
									ctrl.size.height='38.3';
									par.style='s3';
								}
								<AREA>
									<CTRL_GROUP>
										<CTRLS>
											<DATA_CTRL>
												<DOC_HLINK>
													HKEYS={
														'contextElement.id';
														'"detail"';
													}
												</DOC_HLINK>
												FORMULA='QName (getStringParam("nsURI"), getAttrStringValue("name"))'
											</DATA_CTRL>
											<LABEL>
												TEXT='in'
											</LABEL>
											<DATA_CTRL>
												<DOC_HLINK>
													HKEYS={
														'getXMLDocument().id';
														'"detail"';
													}
												</DOC_HLINK>
												FORMULA='getXMLDocument().getAttrStringValue("xmlName")'
											</DATA_CTRL>
										</CTRLS>
									</CTRL_GROUP>
								</AREA>
							</PANEL>
						</CTRLS>
					</CTRL_GROUP>
				</AREA>
			</AREA_SEC>
			<AREA_SEC>
				COND='checkElementsByKey ("redefining-components", contextElement.id)'
				FMT={
					trow.align.vert='Top';
				}
				<AREA>
					<CTRL_GROUP>
						<CTRLS>
							<LABEL>
								FMT={
									ctrl.size.width='54.8';
									ctrl.size.height='17.3';
									par.style='s2';
								}
								TEXT='Redefined:'
							</LABEL>
							<SS_CALL_CTRL>
								FMT={
									ctrl.size.width='444.8';
									ctrl.size.height='17.3';
									par.style='s3';
								}
								SS_NAME='Redefining Components'
							</SS_CALL_CTRL>
						</CTRLS>
					</CTRL_GROUP>
				</AREA>
			</AREA_SEC>
			<AREA_SEC>
				COND='count = getIntParam("ownAttributeCount") + \n        getIntParam("ownElementCount") +\n        getBooleanParam("ownAnyAttribute").toInt() +\n        getBooleanParam("ownAnyElement").toInt();\n\ncount > 0 ? { setVar ("count", count); true } : false'
				FMT={
					trow.align.vert='Top';
				}
				<AREA>
					<CTRL_GROUP>
						<CTRLS>
							<LABEL>
								FMT={
									ctrl.size.width='54.8';
									ctrl.size.height='98.3';
									par.style='s2';
								}
								TEXT='Includes:'
							</LABEL>
							<PANEL>
								FMT={
									ctrl.size.width='444.8';
									ctrl.size.height='98.3';
									par.style='s3';
								}
								<AREA>
									<CTRL_GROUP>
										<CTRLS>
											<LABEL>
												COND='getVar("count").toInt() == 1'
												TEXT='definition'
											</LABEL>
											<LABEL>
												COND='getVar("count").toInt() > 1'
												TEXT='definitions'
											</LABEL>
											<LABEL>
												TEXT='of'
											</LABEL>
											<PANEL>
												COND='count = getIntParam("ownAttributeCount") + \n        getBooleanParam("ownAnyAttribute").toInt();\n\ncount > 0 ? { setVar ("count", count); true } : false'
												FMT={
													ctrl.size.width='309.8';
												}
												<AREA>
													<CTRL_GROUP>
														<CTRLS>
															<DATA_CTRL>
																COND='getIntParam("ownAttributeCount") > 0'
																FORMULA='getIntParam("ownAttributeCount")'
															</DATA_CTRL>
															<DELIMITER>
																COND='getIntParam("ownAttributeCount") > 0'
																FMT={
																	txtfl.delimiter.type='text';
																	txtfl.delimiter.text='+';
																}
															</DELIMITER>
															<LABEL>
																COND='getBooleanParam("ownAnyAttribute")'
																TEXT='any'
															</LABEL>
															<DELIMITER>
																FMT={
																	txtfl.delimiter.type='nbsp';
																}
															</DELIMITER>
															<LABEL>
																COND='getVar("count").toInt() == 1'
																<DOC_HLINK>
																	COND='getBooleanParam("sec.attributes.ownOnly")'
																	HKEYS={
																		'contextElement.id';
																		'"attribute-defs"';
																	}
																</DOC_HLINK>
																TEXT='attribute'
															</LABEL>
															<LABEL>
																COND='getVar("count").toInt() > 1'
																<DOC_HLINK>
																	COND='getBooleanParam("sec.attributes.ownOnly")'
																	HKEYS={
																		'contextElement.id';
																		'"attribute-defs"';
																	}
																</DOC_HLINK>
																TEXT='attributes'
															</LABEL>
															<DELIMITER>
																FMT={
																	txtfl.delimiter.type='text';
																	txtfl.delimiter.text=' and ';
																}
															</DELIMITER>
														</CTRLS>
													</CTRL_GROUP>
												</AREA>
											</PANEL>
											<PANEL>
												COND='count = getIntParam("ownElementCount") + \n        getBooleanParam("ownAnyElement").toInt();\n\ncount > 0 ? { setVar ("count", count); true } : false'
												FMT={
													ctrl.size.width='285';
													ctrl.size.height='38.3';
												}
												<AREA>
													<CTRL_GROUP>
														<CTRLS>
															<DATA_CTRL>
																COND='getIntParam("ownElementCount") > 0'
																FORMULA='getIntParam("ownElementCount")'
															</DATA_CTRL>
															<DELIMITER>
																COND='getIntParam("ownElementCount") > 0'
																FMT={
																	txtfl.delimiter.type='text';
																	txtfl.delimiter.text='+';
																}
															</DELIMITER>
															<LABEL>
																COND='getBooleanParam("ownAnyElement")'
																TEXT='any'
															</LABEL>
															<DELIMITER>
																FMT={
																	txtfl.delimiter.type='nbsp';
																}
															</DELIMITER>
															<LABEL>
																COND='getVar("count").toInt() == 1'
																<DOC_HLINK>
																	COND='getBooleanParam("sec.contentElements.ownOnly")'
																	HKEYS={
																		'contextElement.id';
																		'"content-element-defs"';
																	}
																</DOC_HLINK>
																TEXT='element'
															</LABEL>
															<LABEL>
																COND='getVar("count").toInt() > 1'
																<DOC_HLINK>
																	COND='getBooleanParam("sec.contentElements.ownOnly")'
																	HKEYS={
																		'contextElement.id';
																		'"content-element-defs"';
																	}
																</DOC_HLINK>
																TEXT='elements'
															</LABEL>
														</CTRLS>
													</CTRL_GROUP>
												</AREA>
											</PANEL>
										</CTRLS>
									</CTRL_GROUP>
								</AREA>
							</PANEL>
						</CTRLS>
					</CTRL_GROUP>
				</AREA>
			</AREA_SEC>
			<AREA_SEC>
				COND='getIntParam("usageCount") == 0'
				FMT={
					trow.align.vert='Top';
				}
				<AREA>
					<CTRL_GROUP>
						<CTRLS>
							<LABEL>
								FMT={
									ctrl.size.width='54.8';
									ctrl.size.height='17.3';
									par.style='s2';
								}
								TEXT='Used:'
							</LABEL>
							<LABEL>
								FMT={
									ctrl.size.width='444.8';
									ctrl.size.height='17.3';
									par.style='s3';
								}
								TEXT='never'
							</LABEL>
						</CTRLS>
					</CTRL_GROUP>
				</AREA>
			</AREA_SEC>
			<AREA_SEC>
				COND='getIntParam("usageCount") > 0'
				FMT={
					trow.align.vert='Top';
				}
				<AREA>
					<CTRL_GROUP>
						<CTRLS>
							<LABEL>
								FMT={
									ctrl.size.width='54.8';
									ctrl.size.height='38.3';
									par.style='s2';
								}
								TEXT='Used:'
							</LABEL>
							<PANEL>
								FMT={
									ctrl.size.width='444.8';
									ctrl.size.height='38.3';
									par.style='s3';
								}
								<AREA>
									<CTRL_GROUP>
										<CTRLS>
											<LABEL>
												TEXT='at'
											</LABEL>
											<DATA_CTRL>
												FORMULA='getIntParam("usageCount")'
											</DATA_CTRL>
											<LABEL>
												COND='getIntParam("usageCount") == 1'
												<DOC_HLINK>
													HKEYS={
														'contextElement.id';
														'"usage-locations"';
													}
												</DOC_HLINK>
												TEXT='location'
											</LABEL>
											<LABEL>
												COND='getIntParam("usageCount") > 1'
												<DOC_HLINK>
													HKEYS={
														'contextElement.id';
														'"usage-locations"';
													}
												</DOC_HLINK>
												TEXT='locations'
											</LABEL>
										</CTRLS>
									</CTRL_GROUP>
								</AREA>
							</PANEL>
						</CTRLS>
					</CTRL_GROUP>
				</AREA>
			</AREA_SEC>
		</BODY>
	</FOLDER>
</ROOT>
<STOCK_SECTIONS>
	<ELEMENT_ITER>
		FMT={
			sec.outputStyle='text-par';
			txtfl.delimiter.type='text';
			txtfl.delimiter.text=', ';
		}
		TARGET_ET='<ANY>'
		SCOPE='custom'
		ELEMENT_ENUM_EXPR='findElementsByKey ("redefining-components", contextElement.id)'
		SS_NAME='Redefining Components'
		<BODY>
			<AREA_SEC>
				<AREA>
					<CTRL_GROUP>
						FMT={
							txtfl.delimiter.type='space';
						}
						<CTRLS>
							<LABEL>
								TEXT='with'
							</LABEL>
							<DATA_CTRL>
								<DOC_HLINK>
									HKEYS={
										'contextElement.id';
										'"detail"';
									}
								</DOC_HLINK>
								FORMULA='QName (getStringParam("nsURI"), getAttrStringValue("name"))'
							</DATA_CTRL>
							<LABEL>
								TEXT='in'
							</LABEL>
							<DATA_CTRL>
								<DOC_HLINK>
									HKEYS={
										'getXMLDocument().id';
										'"detail"';
									}
								</DOC_HLINK>
								FORMULA='getXMLDocument().getAttrStringValue("xmlName")'
							</DATA_CTRL>
						</CTRLS>
					</CTRL_GROUP>
				</AREA>
			</AREA_SEC>
		</BODY>
	</ELEMENT_ITER>
</STOCK_SECTIONS>
CHECKSUM='AefbAS5Ty1wZXGIiiwnMIA'
</DOCFLEX_TEMPLATE>
