<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="html" indent="yes" encoding="UTF-8"
		omit-xml-declaration="yes" />
	<xsl:template match="/">
		<table>
			<tr>
				<td class="mergeTitle">Variable Name</td>
				<td class="value">
					<xsl:value-of select="codeBook/var/@name"></xsl:value-of>
				</td>
			</tr>
			<tr>
				<td class="mergeTitle">Label</td>
				<td class="value merge mergeELabel">
					<span class="mergeDisplay">
						<xsl:value-of select="codeBook/var/labl"></xsl:value-of>
					</span>
					<span class="mergeOriginal hidden">
						<xsl:value-of select="codeBook/var/labl"></xsl:value-of>
					</span>	
				</td>
			</tr>
			<tr>
				<td class="mergeTitle">Codebook</td>
				<td class="value">
					<xsl:value-of select="codeBook/titl"/>
				</td>
			</tr>
			<tr>
				<td class="mergeTitle">Concept</td>
				<td class="value merge mergeEConcept">
					<span class="mergeDisplay">
						<xsl:value-of select="codeBook/var/concept/@vocab"/>
					</span>
					<span class="mergeOriginal hidden">
						<xsl:value-of select="codeBook/var/concept/@vocab"/>
					</span>	
				</td>
			</tr>
			<tr>
				<td class="mergeTitle">Concept Vocabulary</td>
				<td class="value merge mergeEConceptVocab">
					<span class="mergeDisplay">
						<xsl:value-of select="codeBook/var/concept/@vocabURI" />
					</span>
					<span class="mergeOriginal hidden">
						<xsl:value-of select="codeBook/var/concept/@vocabURI" />
					</span>	
				</td>
			</tr>
			<tr>
				<td class="mergeTitle">Concept Vocabulary URI</td>
				<td class="value merge mergeEConceptURI">
					<span class="mergeDisplay">
						<xsl:value-of select="codeBook/var/concept" />
					</span>
					<span class="mergeOriginal hidden">
						<xsl:value-of select="codeBook/var/concept" />
					</span>	
				</td>
			</tr>
			<tr>
				<td class="mergeTitle">Type</td>
				<td class="value">
					<xsl:value-of select="codeBook/var/varFormat/@type"></xsl:value-of>
				</td>
			</tr>
		</table>
		<xsl:if test="codeBook/var/@files  != ''">
			<p class="mergeTitle">
				Files
			</p>
			<div class="value2">
				<!-- TODO: Should we allowing merging at the file level? -->
				<xsl:for-each select="codeBook/files/fileDscr">
					<p class="lb2">
						<xsl:value-of select="fileTxt/fileName" />
						<br />
						<xsl:value-of select="@URI" />
						<br />
						<xsl:value-of select="fileTxt/fileType" />
					</p>
				</xsl:for-each>
			</div>
		</xsl:if>
		<xsl:if test="count(codeBook/var/sumStat) gt 0">
			<p>
				<span class="mergeTitle">Summary Statistics </span>
			</p>
			<div class="value2">
				<table class="table3">
					<!--  -->
					<xsl:for-each select="codeBook/var/sumStat">
						<tr>		
							<xsl:choose>
								<xsl:when test="@type eq 'vald'">
									<td class="mergeTitle">
										Valid values
									</td>
									<td class="merge mergeEStatValid">
										<span class="mergeDisplay">
											<xsl:value-of select="current()" />
										</span>
										<span class="mergeOriginal hidden">
											<xsl:value-of select="current()" />
										</span>
									</td>
								</xsl:when>
								<xsl:when test="@type eq 'invd'">
									<td class="mergeTitle">
										Invalid values
									</td>
									<td class="merge mergeEStatInvalid">
										<span class="mergeDisplay">
											<xsl:value-of select="current()" />
										</span>
										<span class="mergeOriginal hidden">
											<xsl:value-of select="current()" />
										</span>
									</td>
								</xsl:when>
								<xsl:when test="@type eq 'min'">
									<td class="mergeTitle">
										Minimum
									</td>
									<td class="merge mergeEStatMin">
										<span class="mergeDisplay">
											<xsl:value-of select="current()" />
										</span>
										<span class="mergeOriginal hidden">
											<xsl:value-of select="current()" />
										</span>
									</td>
								</xsl:when>
								<xsl:when test="@type eq 'max'">
									<td class="mergeTitle">
										Maximum
									</td>
									<td class="merge mergeEStatMax">
										<span class="mergeDisplay">
											<xsl:value-of select="current()" />
										</span>
										<span class="mergeOriginal hidden">
											<xsl:value-of select="current()" />
										</span>
									</td>
								</xsl:when>
								<xsl:when test="@type eq 'mean'">
									<td class="mergeTitle">
										Mean
									</td>
									<td class="merge mergeEStatMean">
										<span class="mergeDisplay">
											<xsl:value-of select="current()" />
										</span>
										<span class="mergeOriginal hidden">
											<xsl:value-of select="current()" />
										</span>
									</td>
								</xsl:when>
								<xsl:when test="@type eq 'medn'">
									<td class="mergeTitle">
										Median
									</td>
									<td class="merge mergeEStatMedn">
										<span class="mergeDisplay">
											<xsl:value-of select="current()" />
										</span>
										<span class="mergeOriginal hidden">
											<xsl:value-of select="current()" />
										</span>
									</td>
									
								</xsl:when>
								<xsl:when test="@type eq 'stdev'">
									<td class="mergeTitle">
										Standard deviation
									</td>
									<td class="merge mergeEStatStdev">
										<span class="mergeDisplay">
											<xsl:value-of select="current()" />
										</span>
										<span class="mergeOriginal hidden">
											<xsl:value-of select="current()" />
										</span>
									</td>
								</xsl:when>
								<xsl:when test="@type eq 'mode'">
									<td class="mergeTitle">
										Mode
									</td>
									<td class="merge mergeEStatMin">
										<span class="mergeDisplay">
											<xsl:value-of select="current()" />
										</span>
										<span class="mergeOriginal hidden">
											<xsl:value-of select="current()" />
										</span>
									</td>
								</xsl:when>
							</xsl:choose>		
						</tr>
					</xsl:for-each>
				</table>
			</div>
		</xsl:if>
			<xsl:for-each select="codeBook/var/valrng">
				<xsl:variable name='valRangeI'>
					<xsl:value-of select="position()" />
				</xsl:variable>
				<p>
					<span class="staticHeader hs3">Value Range</span>
				</p>
				<!-- TODO how to deal with multiple -->
				<xsl:for-each select="current()/range">
					<p class="value2">
						
						Min: 
						<span class="merge mergeEValRngMin">
							<span class="mergeDisplay">
								<xsl:value-of select="current()/@min" />
							</span>
							<span class="mergeOriginal hidden">
								<xsl:value-of select="current()/@min" />
							</span>
						</span>						
						<br />
						Max:
						<span class="merge mergeEValRngMax">
							<span class="mergeDisplay">
								<xsl:value-of select="current()/@max"/>
							</span>
							<span class="mergeOriginal hidden">
								<xsl:value-of select="current()/@max"/>
							</span>
						</span>
					</p>
				</xsl:for-each>
			</xsl:for-each>

		<xsl:if test="count(codeBook/groups/group) gt 0">
			<p>
				<span>Groups</span>
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
		<p class="mergeTitle">
			Question Text
		</p>
		<div class="value merge mergeEQuestion">
			<span class="mergeDisplay">
				<xsl:copy-of select="codeBook/var/qstn/node()" />
			</span>
			<span class="mergeOriginal hidden">
				<xsl:copy-of select="codeBook/var/qstn/node()" />
			</span>
		</div>
		<p>
			<span class="mergeTitle">Full Description </span>
		</p>
		<div class="value merge mergeETxt">
			<div class="mergeDisplay">
				<xsl:copy-of select="codeBook/var/txt/node()" />
			</div>
			<div class="mergeOriginal hidden">
				<xsl:copy-of select="codeBook/var/txt/node()" />
			</div>
		</div>

		<xsl:if test="codeBook/var/catgry/*">
			<p class="mergeTitle">
				Values (<xsl:value-of select="count(codeBook/var/catgry)" />
				total)
			</p>
			<div>
				<table class="table2">
					<xsl:for-each select="codeBook/var/catgry">
						<tr>
							<td>
								<xsl:attribute name="class">headerSmall merge mergeECatValu<xsl:value-of 
								select="position()" /></xsl:attribute>
								<span class="mergeDisplay">
									<xsl:value-of select="catValu"></xsl:value-of>
								</span>
								<span class="mergeOriginal hidden">
									<xsl:value-of select="catValu"></xsl:value-of>
								</span>	
							</td>
							<td>
								<xsl:attribute name="class">merge mergeECatLabl<xsl:value-of 
								select="position()" /></xsl:attribute>	
								<span class="mergeDisplay">
									<xsl:value-of select="labl" />
								</span>
								<span class="mergeOriginal hidden">
									<xsl:value-of select="labl" />
								</span>	
							</td>
						</tr>
					</xsl:for-each>
				</table>
			</div>
		</xsl:if>
		<!-- 
		<xsl:if test="codeBook/var/codInstr != 'None'">
			<p class="mergeTitle">Codebook Instructions</p>
			<div class="merge mergeECodInst">				
					<span class="mergeDisplay">
						<xsl:value-of select="codeBook/var/codInstr" />
					</span>
					<span class="mergeOriginal hidden">
						<xsl:value-of select="codeBook/var/codInstr" />
					</span>	
			</div>
		</xsl:if>
		 -->
	
		<p class="mergeTitle">Notes (<xsl:value-of select="count(codeBook/var/notes)" /> total)
		</p>
		<div id="noteSection">			
			<xsl:for-each select="codeBook/var/notes">
			<div class="lb2"> 
				<p>#<xsl:value-of select="position()" /></p>
				<div>		
					<xsl:attribute name="class">merge mergeENote<xsl:value-of 
					select="position()" /></xsl:attribute>
					<span class="mergeDisplay">
						<xsl:copy-of select="current()/node()" />
					</span>
					<span class="mergeOriginal hidden">
						<xsl:copy-of select="current()/node()" />
					</span>	
				</div>
			</div>
			</xsl:for-each>
		</div>
		
	</xsl:template>
</xsl:stylesheet>