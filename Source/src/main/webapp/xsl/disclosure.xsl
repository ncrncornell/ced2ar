<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:cdr="http://www2.ncrn.cornell.edu/ced2ar-web"
	exclude-result-prefixes="cdr">
	<xsl:output method="html" indent="yes" encoding="UTF-8"
		omit-xml-declaration="yes" />
	<xsl:template match="/">
		<div class="lb2">
			<p class="lb2">b. Please provide a general description of the outputs you wish to clear:</p>
			<div class="response">
				<xsl:copy-of select="codeBook/stdyDscr/stdyInfo/abstract/node()" />
			</div>
		</div>
		<div class="lb2">
			<p>
				c. Please state how the outputs are part of the research project as approved
				(You may summarize or copy descriptions from your proposal, with page
				references.)
			</p>
			<div class="response"><p></p></div>
		</div>
		<h4>2. Research</h4>
		<div class="lb2">
			<p>a. Descriptions of Research</p>
			<p>
				Describe your Research sample(s) or "cuts" of data used in research output.
				For each sample, please describe your selection criteria and how the research
				sample differs from the samples underlying survey publications or other
				samples you have used. Take as much space as you need for each; add samples as
				needed.
			</p>
		</div>
		<div class="lb2">
			<!-- Methodology -->
			<div class="response">
				<xsl:if test="codeBook/stdyDscr/method/dataColl/collMode != ''">
					<div>
						<xsl:copy-of select="codeBook/stdyDscr/method/dataColl/collMode/node()" />
					</div>
				</xsl:if>
				<xsl:if test="codeBook/stdyDscr/method/dataColl/sources/dataSrc  != ''">
						<div>
							<p>Sources</p>
							<ol>
								<xsl:for-each select="codeBook/stdyDscr/method/dataColl/sources/dataSrc">
									<li>
										<xsl:copy-of select="current()" />
									</li>
								</xsl:for-each>
							</ol>
						</div>
				</xsl:if>
			</div>
		</div>
		<div class="lb2">
			<p>b. Relationship Between Samples</p>
			<p>
				Describe how your samples relate to each other (e.g., if you have two
				samples, is one a subsample of another?) In the cases of samples and
				subsamples, there is an implicit third sample, the difference between the two.
				Please describe this sample above. We probably will need to examine any
				implicit samples as well.
			</p>
			<div class="response">
				<xsl:copy-of select="codeBook/stdyDscr/studyAuthorization/node()" />
			</div>
		</div>
		<div class="lb2">
			<p>c. Relationship to other Publications</p>
			<p>
				Describe how your samples may relate to similar samples from other projects
				or from survey publications. (e.g., how your sample of an industry in the LRD
				differs from the Census of Manufactures or Annual Survey of Manufactures files
				in the LRD).
			</p>
			<div class="response">
				<xsl:if test="count(codeBook/stdyDscr/othrStdyMat/relMat) gt 0">
					<p>Related Materials</p>
					<ol>
						<xsl:for-each select="codeBook/stdyDscr/othrStdyMat/relMat">
							<li>
								<xsl:copy-of select="current()/node()" />
							</li>
						</xsl:for-each>
					</ol>	
				</xsl:if>
				<xsl:if test="count(codeBook/stdyDscr/othrStdyMat/relStdy) gt 0">
					<p>Related Studies</p>
					<ol>
						<xsl:for-each select="codeBook/stdyDscr/othrStdyMat/relStdy">
							<li>
								<xsl:copy-of select="current()/node()" />
							</li>
						</xsl:for-each>
					</ol>	
				</xsl:if>
				<xsl:if test="count(codeBook/stdyDscr/othrStdyMat/relPubl) gt 0">
					<p>Related Publications</p>
					<ol>
						<xsl:for-each select="codeBook/stdyDscr/othrStdyMat/relPubl">
							<li>
								<xsl:copy-of select="current()/node()" />
							</li>
						</xsl:for-each>
					</ol>	
				</xsl:if>
			</div>
		</div>
		<div class="lb2">
			<h4>3. Output Files</h4>
			<p>For each research output file to be removed, please enter the following information:</p>
		</div>
		<div class="lb2">
			<p>a. File name (e.g., output.rtf)</p>
			<div class="response">
				<xsl:for-each select="codeBook/fileDscr">
					<p>
						<xsl:value-of select="fileTxt/fileName" />
						&#160;
						<xsl:choose>
							<xsl:when
								test="(not(contains(@URI,'http')) and not(contains(@URI,'ftp:'))) and @URI != ''">
								 - Incomplete location provided:
								<xsl:value-of select="@URI" />
							</xsl:when>
							<xsl:when test="string-length(@URI) gt 0 ">
								<xsl:value-of select="@URI" />
							</xsl:when>
							<xsl:otherwise>
								(No hyperlink available)
							</xsl:otherwise>
						</xsl:choose>
						&#160;(<xsl:value-of select="fileTxt/fileType" />)
					</p>
				</xsl:for-each>
			</div>
		</div>
		<div class="lb2">
			<p>b. Description of file</p>
			<div class="response">
				<xsl:for-each select="codeBook/fileDscr">
					<p>
						<xsl:value-of select="fileTxt/fileName" />:
					</p>
					<p>
						<xsl:value-of select="fileTxt" />
					</p>
				</xsl:for-each>
			</div>
		</div>
	</xsl:template>
</xsl:stylesheet>