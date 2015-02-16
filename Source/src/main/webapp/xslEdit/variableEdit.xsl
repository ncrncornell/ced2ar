<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="html" indent="yes" encoding="UTF-8"
		omit-xml-declaration="yes" />
	<xsl:template match="/">
		<xsl:variable name='handle'>
			<xsl:value-of select="/codeBook/@handle" />
		</xsl:variable>
		<xsl:variable name='varname'>
			<xsl:value-of select="/codeBook/var/@name" />
		</xsl:variable>
		<table class="table1">
			<tr>
				<td class="staticHeader">
					Variable Name
					<a title="Schema Documentation" class="schemaDocLink baseURIa">
						<xsl:attribute name="href">/schema/doc/var</xsl:attribute>
						<i class="fa fa-info-circle"></i>
					</a>
				</td>
				<td class="value">
					<xsl:value-of select="/codeBook/var/@name"></xsl:value-of>
				</td>
			</tr>
			<tr>
				<td class="staticHeader">Top Level Access</td>
				<td class="value">
					<xsl:choose>
						<xsl:when test="/codeBook/var/@access != ''">
							<xsl:value-of select="/codeBook/var/@access" />
						</xsl:when>
						<xsl:otherwise>
							<em>undefined (no restrictions)</em>
						</xsl:otherwise>
					</xsl:choose>
					<a title="Edit field" class="editIcon2">
						<xsl:attribute name="href">
		    				<xsl:value-of select="$varname" />/edit?f=topAcs</xsl:attribute>
						<i class="fa fa-pencil"></i>
					</a>
				</td>
			</tr>
			<!-- <tr> <td class="staticHeader">Version</td> <td class="value"> <xsl:choose> 
				<xsl:when test="/codeBook/var/verStmt/@date != ''"> <xsl:value-of select="/codeBook/var/verStmt/@date" 
				/> </xsl:when> <xsl:otherwise> <em>No Value Entered</em> </xsl:otherwise> 
				</xsl:choose> </td> </tr> -->
			<tr>
				<td class="staticHeader">Label</td>
				<td class="value">
					<xsl:value-of select="/codeBook/var/labl"></xsl:value-of>
					<a title="Edit field" class="editIcon2">
						<xsl:attribute name="href">
		    				<xsl:value-of select="$varname" />/edit?f=labl</xsl:attribute>
						<i class="fa fa-pencil"></i>
					</a>
				</td>
				<td class="value lb2">
					Access Level:&#160;
					<xsl:choose>
						<xsl:when test="/codeBook/var/labl/@access != ''">
							<xsl:value-of select="/codeBook/var/labl/@access" />
						</xsl:when>
						<xsl:otherwise>
							<em>undefined</em>
						</xsl:otherwise>
					</xsl:choose>
					<a title="Edit field" class="editIcon2">
						<xsl:attribute name="href">
		    				<xsl:value-of select="$varname" />/edit?f=lablAcs</xsl:attribute>
						<i class="fa fa-pencil"></i>
					</a>
				</td>
			</tr>
			<tr>
				<td class="staticHeader">Codebook</td>
				<td class="value">
					<xsl:variable name='codebook'>
						<xsl:value-of select="/codeBook/titl" />
					</xsl:variable>
					<xsl:value-of select="$codebook" />
				</td>
			</tr>
			<tr>
				<td class="staticHeader">
					Concept
					<a title="Schema Documentation" class="schemaDocLink baseURIa">
						<xsl:attribute name="href">/schema/doc/concept</xsl:attribute>
						<i class="fa fa-info-circle"></i>
					</a>
				</td>
				<td class="value">
					<xsl:if test="/codeBook/var/concept != ''">
						<xsl:value-of select="/codeBook/var/concept" />
					</xsl:if>
				</td>
			</tr>
			<tr>
				<td class="staticHeader">Type</td>
				<td class="value">
					<xsl:value-of select="/codeBook/var/varFormat/@type"></xsl:value-of>
				</td>
			</tr>
		</table>
			<p class="lb2">
			<span class="staticHeader">Question Text</span>
			<a title="Edit field" class="editIcon2">
				<xsl:choose>
					<xsl:when test="/codeBook/var/qstn != ''">
						<xsl:attribute name="href"><xsl:value-of select="$varname" />/edit?f=qstn</xsl:attribute>
						<i class="fa fa-pencil"></i>
					</xsl:when>
					<xsl:otherwise>
						<xsl:attribute name="href"><xsl:value-of select="$varname" />/edit?f=qstn&amp;a=true</xsl:attribute>
						<i class="fa fa-plus"></i>
					</xsl:otherwise>
				</xsl:choose>
				
			</a>
			<a title="Schema Documentation" class="schemaDocLink baseURIa">
				<xsl:attribute name="href">/schema/doc/qstn</xsl:attribute>
				<i class="fa fa-info-circle"></i>
			</a>
		</p>
		<div class="value2">
			<xsl:copy-of select="/codeBook/var/qstn/node()" />
		</div>
		<p class="lb2">
			<span class="staticHeader">Full Description</span>
			<a title="Edit field" class="editIcon2">
				<xsl:attribute name="href">
    				<xsl:value-of select="$varname" />/edit?f=txt</xsl:attribute>
				<i class="fa fa-pencil"></i>
			</a>
			<a title="Schema Documentation" class="schemaDocLink baseURIa">
				<xsl:attribute name="href">/schema/doc/txt</xsl:attribute>
				<i class="fa fa-info-circle"></i>
			</a>
		</p>
		<div class="value2">
			<xsl:copy-of select="/codeBook/var/txt/node()" />
		</div>
		<xsl:if test="/codeBook/var/@files  != ''">
			<p>
				<span class="staticHeader">Files</span>
				<a title="Schema Documentation" class="schemaDocLink baseURIa">
					<xsl:attribute name="href">/schema/doc/fileDscr</xsl:attribute>
					<i class="fa fa-info-circle"></i>
				</a>
			</p>
			<p class="value2">
				<xsl:for-each select="/codeBook/files/fileDscr">
					<xsl:value-of select="@ID" />
					&#160;
					<xsl:choose>
						<xsl:when test="not(contains(@URI,'http')) and @URI != ''">
							<xsl:value-of select="fileTxt/fileName" />
							(Incomplete URL provided)
						</xsl:when>
						<xsl:when test="string-length(@URI) gt 0 ">
							<a>
								<xsl:attribute name="href"><xsl:value-of
									select="@URI" /></xsl:attribute>
								<xsl:value-of select="fileTxt/fileName" />
							</a>
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of select="fileTxt/fileName" />
							(No hyperlink available)
						</xsl:otherwise>
					</xsl:choose>
					&#160;(
					<xsl:value-of select="fileTxt/fileType" />
					)
					<br />
				</xsl:for-each>
			</p>
		</xsl:if>
		<xsl:if test="count(/codeBook/var/sumStat) gt 0">
			<p>
				<span class="staticHeader">Summary Statistics </span>
				<a title="Schema Documentation" class="schemaDocLink baseURIa">
					<xsl:attribute name="href">/schema/doc/sumStat</xsl:attribute>
					<i class="fa fa-info-circle"></i>
				</a>
			</p>
			<div class="value2">
				<table class="table3">
					<xsl:for-each select="/codeBook/var/sumStat">
						<tr>
							<td>
								<xsl:choose>
									<xsl:when test="@type eq 'vald'">
										Valid values
									</xsl:when>
									<xsl:when test="@type eq 'invd'">
										Invalid values
									</xsl:when>
									<xsl:when test="@type eq 'min'">
										Minimum
									</xsl:when>
									<xsl:when test="@type eq 'max'">
										Maximum
									</xsl:when>
									<xsl:when test="@type eq 'mean'">
										Mean
									</xsl:when>
									<xsl:when test="@type eq 'stdev'">
										Standard deviation
									</xsl:when>
								</xsl:choose>
							</td>
							<td>
								<xsl:value-of select="current()" />
							</td>
							<td>
								<xsl:choose>
									<xsl:when test="current()/@access != ''">
										<xsl:value-of select="current()/@access" />
									</xsl:when>
									<xsl:otherwise>
										<em>undefined</em>
									</xsl:otherwise>
								</xsl:choose>
								<a title="Edit field" class="editIcon2">
									<xsl:attribute name="href">
					    				<xsl:value-of select="$varname" />/edit?f=sumStat&amp;i=<xsl:value-of
										select="position()" /></xsl:attribute>
									<i class="fa fa-pencil"></i>
								</a>
							</td>
						</tr>
					</xsl:for-each>
				</table>
			</div>
		</xsl:if>
		<xsl:if test="count(/codeBook/var/valrng) gt 0">
			<p class="lb2">
				<span class="staticHeader">Value Ranges</span>
				<a title="Schema Documentation" class="schemaDocLink schemaDocLinkPad baseURIa">
					<xsl:attribute name="href">/schema/doc/valrng</xsl:attribute>
					<i class="fa fa-info-circle"></i>Value Range
				</a>
				<a title="Schema Documentation" class="schemaDocLink schemaDocLinkPad baseURIa">
					<xsl:attribute name="href">/schema/doc/range</xsl:attribute>
					<i class="fa fa-info-circle"></i>Range
				</a>
			</p>
			<xsl:for-each select="/codeBook/var/valrng">
				<xsl:variable name='valRangeI'>
					<xsl:value-of select="position()" />
				</xsl:variable>
				<p>
					<span class="staticHeader hs3">Value Range</span>
					&#160;Access Level:&#160;
					<xsl:choose>
						<xsl:when test="current()/@access != ''">
							<xsl:value-of select="current()/@access" />
						</xsl:when>
						<xsl:otherwise>
							<em>undefined</em>
						</xsl:otherwise>
					</xsl:choose>
					<a title="Edit field" class="editIcon2">
						<xsl:attribute name="href">
			    			<xsl:value-of select="$varname" />/edit?f=valRange&amp;i=<xsl:value-of
							select="$valRangeI" /></xsl:attribute>
						<i class="fa fa-pencil"></i>
					</a>
				</p>
				<xsl:for-each select="current()/range">
					<p class="value2">
						Range:&#160;[
						<xsl:value-of select="current()/@min" />
						,
						<xsl:value-of select="current()/@max" />
						]
						<br />
						Access Level:&#160;
						<xsl:choose>
							<xsl:when test="current()/@access != ''">
								<xsl:value-of select="current()/@access" />
							</xsl:when>
							<xsl:otherwise>
								<em>undefined</em>
							</xsl:otherwise>
						</xsl:choose>
						<a title="Edit field" class="editIcon2">
							<xsl:attribute name="href">
				    				<xsl:value-of select="$varname" />/edit?f=range&amp;i=<xsl:value-of
								select="$valRangeI" />&amp;k=<xsl:value-of select="position()" /></xsl:attribute>
							<i class="fa fa-pencil"></i>
						</a>
					</p>
				</xsl:for-each>
			</xsl:for-each>
		</xsl:if>
		<xsl:if test="count(/codeBook/groups/group) gt 0">
			<p>
				<span class="staticHeader">Groups</span>
				<a title="Schema Documentation" class="schemaDocLink baseURIa">
					<xsl:attribute name="href">/schema/doc/varGrp</xsl:attribute>
					<i class="fa fa-info-circle"></i>
				</a>
			</p>
			<p class="value2">
				<xsl:for-each select="/codeBook/groups/group">
					<xsl:value-of select="current()/@name" />
					<br />
				</xsl:for-each>
			</p>
		</xsl:if>
		<p>
			<span class="staticHeader">Values</span>
			<a title="Schema Documentation" class="schemaDocLink schemaDocLinkPad baseURIa">
				<xsl:attribute name="href">/schema/doc/catgry</xsl:attribute>
				<i class="fa fa-info-circle"></i>Categories
			</a>
			<a title="Schema Documentation" class="schemaDocLink schemaDocLinkPad baseURIa">
				<xsl:attribute name="href">/schema/doc/catValu</xsl:attribute>
				<i class="fa fa-info-circle"></i>Category Values
			</a>
			<a title="Schema Documentation" class="schemaDocLink schemaDocLinkPad baseURIa">
				<xsl:attribute name="href">/schema/doc/catStat</xsl:attribute>
				<i class="fa fa-info-circle"></i>Category Statistics
			</a>
		</p>
		<xsl:if test="/codeBook/var/catgry/*">
			<div class="value2">
				<table class="table2">
					<xsl:for-each select="/codeBook/var/catgry">
						<xsl:variable name='catI'>
							<xsl:value-of select="position()" />
						</xsl:variable>
						<tr class="valueTableHeader">
							<td class="headerSmall">
								<xsl:value-of select="catValu"></xsl:value-of>
								<a title="Edit field" class="editIcon2">
									<xsl:attribute name="href">
					    				<xsl:value-of select="$varname" />/edit?f=catValu&amp;i=<xsl:value-of
										select="$catI" /></xsl:attribute>
									<i class="fa fa-pencil"></i>
								</a>
							</td>
							<td>
								<xsl:value-of select="labl" />
								<a title="Edit field" class="editIcon2">
									<xsl:attribute name="href">
					    				<xsl:value-of select="$varname" />/edit?f=catLabl&amp;i=<xsl:value-of
										select="$catI" /></xsl:attribute>
									<i class="fa fa-pencil"></i>
								</a>
							</td>
							<td class="accessCell">
								<xsl:text>Access Level:</xsl:text>
								<xsl:choose>
									<xsl:when test="current()/@access != ''">
										<xsl:value-of select="current()/@access" />
									</xsl:when>
									<xsl:otherwise>
										<em>undefined</em>
									</xsl:otherwise>
								</xsl:choose>
								<a title="Edit field" class="editIcon2">
									<xsl:attribute name="href">
					    				<xsl:value-of select="$varname" />/edit?f=val&amp;i=<xsl:value-of
										select="$catI" />&amp;a=true</xsl:attribute>
									<i class="fa fa-pencil"></i>
								</a>
							</td>
							<td>
								<a title="Delete field" class="editIcon2">
									<xsl:attribute name="href">
					    				<xsl:value-of select="$varname" />/delete?f=catgry&amp;i=<xsl:value-of
										select="$catI" />&amp;k=<xsl:value-of select="position()" /></xsl:attribute>
									<i class="fa fa-times"></i>
								</a>
							</td>
							<td></td>
						</tr>
						<xsl:for-each select="current()/catStat">
							<tr>
								<td class="headerSmall"></td>
								<td>
									Statistic&#160;-&#160;
									<xsl:value-of select="current()/@type" />
								</td>
								<td>
									<xsl:value-of select="current()" />
								</td>
								<td class="accessCell">
									Access Level:&#160;
									<xsl:choose>
										<xsl:when test="current()/@access != ''">
											<xsl:value-of select="current()/@access" />
										</xsl:when>
										<xsl:otherwise>
											<em>undefined</em>
										</xsl:otherwise>
									</xsl:choose>
									<a title="Edit field" class="editIcon2">
										<xsl:attribute name="href">
							   				<xsl:value-of select="$varname" />/edit?f=catStat&amp;i=<xsl:value-of
											select="$catI" />&amp;k=<xsl:value-of select="position()" /></xsl:attribute>
										<i class="fa fa-pencil"></i>
									</a>
								</td>
							</tr>
						</xsl:for-each>
						<xsl:if test="count(current()/catStat) gt 0">
							<tr class="varStatBreak">
								<td>&#160;</td>
							</tr>
						</xsl:if>
					</xsl:for-each>
				</table>
			</div>
		</xsl:if>
		<p class="value2">
			<a title="Add field" class="editIcon2 editIconText">
				<xsl:attribute name="href">
					<xsl:value-of select="$varname" />/edit?f=catLabl&amp;i=<xsl:value-of
					select="count(/codeBook/var/catgry) + 1" />&amp;a=true</xsl:attribute>
				<i class="fa fa-plus"></i>
				<em>Add Category and Value</em>
			</a>
		</p>
		<xsl:if test="/codeBook/var/codInstr != 'None'">
			<p>
				<span class="staticHeader">Codebook Instructions</span>
				<a title="Schema Documentation" class="schemaDocLink baseURIa">
					<xsl:attribute name="href">/schema/doc/codInstr</xsl:attribute>
					<i class="fa fa-info-circle"></i>
				</a>
			</p>
			<div class="value2">
				<p>
					<xsl:value-of select="/codeBook/var/codInstr" />
				</p>
			</div>
		</xsl:if>
		<p>
			<span class="staticHeader">Notes</span>
			<a title="Schema Documentation" class="schemaDocLink baseURIa">
				<xsl:attribute name="href">/schema/doc/notes</xsl:attribute>
				<i class="fa fa-info-circle"></i>
			</a>
		</p>
		<xsl:if test="count(/codeBook/var/notes) gt 0">
			<xsl:for-each select="/codeBook/var/notes">
				<p class="value2">
					Note #<xsl:value-of select="position()" /> -
					Access Level:&#160;
					<xsl:choose>
						<xsl:when test="current()/@access != ''">
							<xsl:value-of select="current()/@access" />
						</xsl:when>
						<xsl:otherwise>
							<em>undefined</em>
						</xsl:otherwise>
					</xsl:choose>
					<a title="Edit field" class="editIcon2">
						<xsl:attribute name="href">
			    				<xsl:value-of select="$varname"/>/edit?f=notesAccs&amp;i=<xsl:value-of
							select="position()" /></xsl:attribute>
						<i class="fa fa-pencil"></i>
					</a>
					<br />
					<xsl:value-of select="current()" />
					<a title="Edit field" class="editIcon2">
						<xsl:attribute name="href">
			    				<xsl:value-of select="$varname"/>/edit?f=notes&amp;i=<xsl:value-of
							select="position()" /></xsl:attribute>
						<i class="fa fa-pencil"></i>
					</a>
				</p>
			</xsl:for-each>
		</xsl:if>
		<p class="value2">
			<a title="Add Note" class="editIcon2 editIconText">
				<xsl:attribute name="href">
					<xsl:value-of select="$varname" />/edit?f=notes&amp;i=<xsl:value-of
					select="count(/codeBook/notes) + 1" />&amp;a=true</xsl:attribute>
				<i class="fa fa-plus"></i>
				<em>Add Note</em>
			</a>
		</p>
	</xsl:template>
</xsl:stylesheet>