<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="html" indent="yes" encoding="UTF-8"
		omit-xml-declaration="yes" />
	<xsl:template match="/">
		<table class="table1">
			<tr>
				<td class="staticHeader">Variable Name</td>
				<td class="value">
					<span itemprop="name">
						<xsl:value-of select="codeBook/var/@name"></xsl:value-of>
					</span>
				</td>
			</tr>
			<tr>
				<td class="staticHeader">Top Access Level</td>
				<td class="value">
					<xsl:choose>
						<xsl:when test="codeBook/var/@access != ''">
							<xsl:value-of select="/codeBook/var/@access" />
						</xsl:when>
						<xsl:otherwise>
							<em>undefined (no restrictions)</em>
						</xsl:otherwise>
					</xsl:choose>
				</td>
			</tr>
			<tr>
				<td class="staticHeader">Label</td>
				<td class="value">
					<xsl:value-of select="codeBook/var/labl"></xsl:value-of>
				</td>
			</tr>
			<tr>
				<td class="staticHeader">Codebook</td>
				<td class="value">
					<xsl:variable name='codebook'>	
							<span itemprop="isPartOf">
								<xsl:value-of select="codeBook/titl" />
							</span>
					</xsl:variable>
					<span itemprop="isPartOf"><xsl:value-of select="$codebook" /></span>
				</td>
			</tr>
			<tr>
				<td class="staticHeader">Concept</td>
				<td class="value">
					<xsl:if test="codeBook/var/concept != ''">
						<a class="baseURIa">
							<xsl:variable name='concept'>
								<xsl:value-of select="replace(codeBook/var/concept,'-','')" />
							</xsl:variable>
							<xsl:attribute name="href">/search?t=<xsl:value-of
								select="$concept" /></xsl:attribute>
							<xsl:value-of select="$concept" />
						</a>
					</xsl:if>
				</td>
			</tr>
			<tr>
				<td class="staticHeader">Type</td>
				<td class="value">
					<xsl:value-of select="codeBook/var/varFormat/@type"></xsl:value-of>
				</td>
			</tr>
		</table>
		
		<xsl:if test="codeBook/var/@files  != ''">
			<p>
				<span class="staticHeader">Files</span>
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
		
		<xsl:if test="codeBook/var/txt != ''">
			<p>
				<span class="staticHeader">Full Description </span>
			</p>
			<div class="value2">
				<span itemprop="description">
					<xsl:copy-of select="codeBook/var/txt/node()" />
				</span>
			</div>
		</xsl:if>
		
		<xsl:if test="count(codeBook/var/sumStat) gt 0">
			<p>
				<span class="staticHeader">Summary Statistics </span>
			</p>
			<div class="value2">
				<table class="table3">
					<xsl:for-each select="codeBook/var/sumStat">
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
									<xsl:when test="@type eq 'medn'">
										Median
									</xsl:when>
									<xsl:when test="@type eq 'stdev'">
										Standard deviation
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
						</tr>
					</xsl:for-each>
				</table>
			</div>
		</xsl:if>
		<xsl:if test="count(codeBook/var/valrng) gt 0">
			<p class="lb2">
				<span class="staticHeader">Value Ranges</span>
			</p>
			<xsl:for-each select="codeBook/var/valrng">
				<xsl:variable name='valRangeI'>
					<xsl:value-of select="position()" />
				</xsl:variable>
				<p>
					<span class="staticHeader hs3">Value Range</span>
				</p>
				<xsl:for-each select="current()/range">
					<p class="value2">
						Range:&#160;[
						<xsl:value-of select="current()/@min" />
						,
						<xsl:value-of select="current()/@max" />
						]
					</p>
				</xsl:for-each>
			</xsl:for-each>
		</xsl:if>
		<!-- Universe here -->
		<xsl:if test="codeBook/var/universe != ''">
			<p>
				<span class="staticHeader">Universe </span>
			</p>
			<div class="value2">
				<span itemprop="description">
					<xsl:copy-of select="codeBook/var/universe/node()" />
				</span>
			</div>
		</xsl:if>
		
		<xsl:if test="codeBook/var/anlysUnit != ''">
			<p>
				<span class="staticHeader">Analysis Unit</span>
			</p>
			<div class="value2">
				<span itemprop="description">
					<xsl:copy-of select="codeBook/var/anlysUnit/node()" />
				</span>
			</div>
		</xsl:if>
		
		<xsl:if test="count(codeBook/groups/group) gt 0">
			<p>
				<span class="staticHeader">Groups</span>
			</p>
			<p class="value2">
				<xsl:for-each select="codeBook/groups/group">
					<a>
						<xsl:attribute name="href">../groups/<xsl:value-of
							select="current()/@id" />/</xsl:attribute>
						<xsl:value-of select="current()/@name" />
					</a>
					<br />
				</xsl:for-each>
			</p>
		</xsl:if>
		<xsl:if test="codeBook/var/qstn != ''">
			<p>
				<span class="staticHeader">Question Text</span>
			</p>
			<div class="value2">
				<span itemprop="description">
					<xsl:copy-of select="codeBook/var/qstn/node()" />
				</span>
			</div>
		</xsl:if>
		
		<xsl:if test="codeBook/var/catgry/*">
			<p class="toggleHeader">
				Values (<xsl:value-of select="count(codeBook/var/catgry)" />
				total)
			</p>
			<div class="toggleContent">
				<table class="table2">
					<xsl:for-each select="codeBook/var/catgry">
						<tr>
							<td class="headerSmall">
								<xsl:value-of select="catValu"></xsl:value-of>
							</td>
							<td>
								<xsl:value-of select="labl" />
							</td>
						</tr>
					</xsl:for-each>
				</table>
			</div>
		</xsl:if>
		<xsl:if test="codeBook/var/codInstr != 'None'">
			<p class="toggleHeader">Codebook Instructions</p>
			<div class="toggleContent value2">
				<p>
					<xsl:value-of select="codeBook/var/codInstr" />
				</p>
			</div>
		</xsl:if>
		<xsl:if test="count(codeBook/var/notes) gt 0">
			<p class="toggleHeader tcs">Notes (<xsl:value-of select="count(codeBook/var/notes)" />
				total)
			</p>
			<div class="toggleContent">			
				<xsl:for-each select="codeBook/var/notes">
				<div class="lb2"> 
					<p>#<xsl:value-of select="position()" /></p>
					<xsl:copy-of select="current()/node()" />
				</div>
				</xsl:for-each>
			</div>
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>