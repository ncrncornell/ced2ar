<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="html" indent="yes" encoding="UTF-8"
		omit-xml-declaration="yes" />
	<xsl:template match="/">
		<xsl:for-each select="dataDscr/var">
			<xsl:variable name="currentVariableId" select="@ID" />
			<table class="table1">
				<tr>
					<td class="staticHeader">Variable Name</td>
					<td class="value">
						<xsl:value-of select="@name"></xsl:value-of>
					</td>
				</tr>
				<tr>
					<td class="staticHeader">Label</td>
					<td class="value">
						<xsl:value-of select="labl"></xsl:value-of>
					</td>
				</tr>
				<tr>
					<td class="staticHeader">Concept</td>
					<td class="value">
						<xsl:if test="concept != ''">
							<a class="baseURIa">
								<xsl:variable name='concept'>
									<xsl:value-of select="replace(concept,'-','')" />
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
						<xsl:value-of select="varFormat/@type"></xsl:value-of>
					</td>
				</tr>
				<tr>
					<td class="staticHeader">Files</td>
					<td class="value">
						<xsl:value-of select="@files"></xsl:value-of>
					</td>
				</tr>
			</table>
			
			<!-- Full Description -->
			<xsl:if test="txt != ''">
				<p>
					<span class="staticHeader">Full Description </span>
				</p>
				<div class="value2">
					<xsl:copy-of select="txt/node()" />
				</div>
			</xsl:if>

			<!-- Variable Values -->
			<xsl:if test="catgry/*">
				<p class="toggleHeader">
					Values (
					<xsl:value-of select="count(catgry)" />
					total)
				</p>
				<div class="toggleContent">
					<table class="table2">
						<xsl:for-each select="catgry">
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

			<!-- Summary Statistics -->
			<xsl:if test="count(sumStat) gt 0">
				<p>
					<span class="staticHeader">Summary Statistics </span>
				</p>
				<div class="value2">
					<table class="table3">
						<xsl:for-each select="sumStat">
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
							</tr>
						</xsl:for-each>
					</table>
				</div>
			</xsl:if>

			<!-- Value Ranges -->
			<xsl:if test="count(valrng) gt 0">
				<p class="lb2">
					<span class="staticHeader">Value Ranges</span>
				</p>
				<xsl:for-each select="valrng">
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

			<!--groups -->
			<xsl:if test="count(./../varGrp) gt 0">
				<p>
					<span class="staticHeader">Groups</span>
				</p>
				<p class="value2">
					<xsl:for-each select="./../varGrp">
						<xsl:choose>
							<xsl:when
								test="contains(concat(@var,' '),concat($currentVariableId,' '))">
								<xsl:value-of select="@name"></xsl:value-of>
								<br />
							</xsl:when>
							<xsl:otherwise>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:for-each>
				</p>
			</xsl:if>
			
			<!-- CodeBook Instruction -->
			<xsl:if test="codInstr != 'None'">
				<p class="toggleHeader">Codebook Instructions</p>
				<div class="toggleContent">
					<p>
						<xsl:value-of select="codInstr" />
					</p>
				</div>
			</xsl:if>
			<div class="printLB"></div>
		</xsl:for-each>
	</xsl:template>
</xsl:stylesheet>