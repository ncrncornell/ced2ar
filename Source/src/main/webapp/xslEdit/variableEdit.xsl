<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:cdr="http://www2.ncrn.cornell.edu/ced2ar-web"
	exclude-result-prefixes="cdr">
	<xsl:output method="html" indent="yes" encoding="UTF-8" omit-xml-declaration="yes" />
	
<!-- Functions -->	
	<!-- Schema doc link-->
	<xsl:function name="cdr:schemaDoc">
		<xsl:param name="elementName"/>
		<xsl:if test="$elementName ne ''">
			<a title="Schema Documentation" class="schemaDocLink baseURIa">
				<xsl:attribute name="href">/schema/doc/<xsl:value-of select="$elementName"/></xsl:attribute>
				<i class="fa fa-info-circle"></i>
			</a>
		</xsl:if>
	</xsl:function>	
	
	<!-- Schema doc link with label-->
	<xsl:function name="cdr:schemaDoc2">
		<xsl:param name="elementName"/>
		<xsl:param name="label"/>
		<xsl:if test="$elementName ne ''">
			<a title="Schema Documentation" class="schemaDocLink iLinkL baseURIa">
				<xsl:attribute name="href">/schema/doc/<xsl:value-of select="$elementName"/></xsl:attribute>
				<i class="fa fa-info-circle"></i><xsl:value-of select="$label" />
			</a>
		</xsl:if>
	</xsl:function>	
	
<!-- Main Template -->			
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
					<xsl:copy-of select="cdr:schemaDoc('var')" />				
				</td>
				<td class="value">
					<xsl:value-of select="/codeBook/var/@name"></xsl:value-of>
				</td>
			</tr>
			<tr>
				<td class="staticHeader">Top Access Level</td>
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
					Concept <xsl:copy-of select="cdr:schemaDoc('concept')" />	
				</td>
				<td class="value">
					<xsl:if test="/codeBook/var/concept != ''">
						<xsl:value-of select="/codeBook/var/concept" />
					</xsl:if>
					<a title="Edit field" class="editIcon2">
						<xsl:attribute name="href">
		    				<xsl:value-of select="$varname" />/editMulti?f=concept&amp;i=1</xsl:attribute>
						<i class="fa fa-pencil"></i>
					</a>
				</td>
			</tr>
			<tr>
				<td class="staticHeader">Type</td>
				<td class="value">
					<xsl:value-of select="/codeBook/var/varFormat/@type"></xsl:value-of>
				</td>
			</tr>
			<!--   This is a placeholder, please comment out on production -->
			<!-- 
			<tr>
				<td class="staticHeader">Vocabulary</td>
				<td class="value">
					<a class="editIcon2	editIconText" title="Add Tags" href="#">
						<i class="fa fa-tag"></i><em>Add Tags</em>
					</a>				
				</td>
			</tr>
			 -->
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
			<xsl:copy-of select="cdr:schemaDoc('qstn')" />	
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
			<xsl:copy-of select="cdr:schemaDoc('txt')" />	
		</p>
		<div class="value2">
			<xsl:copy-of select="/codeBook/var/txt/node()" />
		</div>
		<xsl:if test="/codeBook/var/@files  != ''">
			<p>
				<span class="staticHeader">Files</span>
				<xsl:copy-of select="cdr:schemaDoc('fileDscr')" />	
			</p>
			<p class="value2">
				<xsl:for-each select="codeBook/files/fileDscr">
					<xsl:value-of select="fileTxt/fileName" />
					&#160;
					<xsl:choose>
						<xsl:when test="not(contains(@URI,'http') or contains(@URI,'ftp:')) and @URI != ''">
							<xsl:value-of select="fileTxt/fileName" />
							(Incomplete URL provided - <xsl:value-of select="@URI" />)
						</xsl:when>
						<xsl:when test="string-length(@URI) gt 0 ">
							<a class="iLinkR">
								<xsl:attribute name="target">_blank</xsl:attribute>
								<xsl:attribute name="href"><xsl:value-of
									select="@URI" /></xsl:attribute>
								<xsl:value-of select="@URI" /><i class="fa fa-external-link"></i>
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
				<xsl:copy-of select="cdr:schemaDoc('sumStat')" />
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
									<xsl:when test="@type eq 'medn'">
										Median
									</xsl:when>
									<xsl:when test="@type eq 'mode'">
										Mode
									</xsl:when>
									<xsl:when test="@type eq 'other'">
										Other
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
				<xsl:copy-of select="cdr:schemaDoc2('valrng','Value Ranges')" />
				<xsl:copy-of select="cdr:schemaDoc2('range','Range')" />
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
		<p class="lb2">
			<span class="staticHeader">Universe</span>
			<a title="Edit field" class="editIcon2">
				<xsl:choose>
					<xsl:when test="/codeBook/var/universe != ''">
						<xsl:attribute name="href"><xsl:value-of select="$varname" />/edit?f=universe</xsl:attribute>
						<i class="fa fa-pencil"></i>
					</xsl:when>
					<xsl:otherwise>
						<xsl:attribute name="href"><xsl:value-of select="$varname" />/edit?f=universe&amp;a=true</xsl:attribute>
						<i class="fa fa-plus"></i>
					</xsl:otherwise>
				</xsl:choose>
			</a>
			<xsl:copy-of select="cdr:schemaDoc('universe')" />	
		</p>
		<div class="value2">
			<xsl:copy-of select="/codeBook/var/universe/node()" />
		</div>
		<p class="lb2">
			<span class="staticHeader">Analysis Unit</span>
			<a title="Edit field" class="editIcon2">
				<xsl:choose>
					<xsl:when test="/codeBook/var/anlysUnit != ''">
						<xsl:attribute name="href"><xsl:value-of select="$varname" />/edit?f=anlysUnit</xsl:attribute>
						<i class="fa fa-pencil"></i>
					</xsl:when>
					<xsl:otherwise>
						<xsl:attribute name="href"><xsl:value-of select="$varname" />/edit?f=anlysUnit&amp;a=true</xsl:attribute>
						<i class="fa fa-plus"></i>
					</xsl:otherwise>
				</xsl:choose>
			</a>
			<xsl:copy-of select="cdr:schemaDoc('anlysUnit')" />	
		</p>
		<div class="value2">
			<xsl:copy-of select="/codeBook/var/anlysUnit/node()" />
		</div>
		<xsl:if test="count(/codeBook/groups/group) gt 0">
			<p>
				<span class="staticHeader">Groups</span>
				<xsl:copy-of select="cdr:schemaDoc('varGrp')" />
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
			<xsl:copy-of select="cdr:schemaDoc2('catgry','Categories')" />
			<xsl:copy-of select="cdr:schemaDoc2('catValu','Category Values')" />
			<xsl:copy-of select="cdr:schemaDoc2('catStat','Category Statistics')" />
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
				<xsl:copy-of select="cdr:schemaDoc('codInstr')" />
			</p>
			<div class="value2">
				<xsl:copy-of select="/codeBook/var/codInstr" />
			</div>
		</xsl:if>
		<p>
			<span class="staticHeader">Notes</span>
			<xsl:copy-of select="cdr:schemaDoc('notes')" />	
		</p>
		<xsl:if test="count(/codeBook/var/notes) gt 0">
			<xsl:for-each select="/codeBook/var/notes">
				<div class="value2">
					<div>
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
					</div>
					<span class="valueInline">
						<xsl:copy-of select="current()/node()" />
					</span>
					<a title="Edit field" class="editIcon2">
						<xsl:attribute name="href">
			    				<xsl:value-of select="$varname"/>/edit?f=notes&amp;i=<xsl:value-of
							select="position()" /></xsl:attribute>
						<i class="fa fa-pencil"></i>
					</a>
				</div>
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