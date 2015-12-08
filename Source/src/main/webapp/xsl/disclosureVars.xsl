<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="html" indent="yes" encoding="UTF-8"
	omit-xml-declaration="yes" />
	<xsl:template match="/">
		<!-- Variables -->
		<xsl:for-each select="codeBook/dataDscr/var">
				<div class="varBlock">
					<p>
						Name :
						<span class="responseInline"><xsl:value-of select="@name"></xsl:value-of></span>
					</p>
					<xsl:if test="labl != ''">
						<p>
							Label :
							<span class="responseInline"><xsl:value-of select="labl"></xsl:value-of></span>
						</p>
					</xsl:if>
					<xsl:if test="varFormat/@type != ''">
						<p>
							Type :
							<span class="responseInline"><xsl:value-of select="varFormat/@type"></xsl:value-of></span>
						</p>		
					</xsl:if>
					<xsl:if test="txt != ''">
						<p>
							Description :
						</p>
						<p class="response"><xsl:value-of select="txt"></xsl:value-of></p>
					</xsl:if>	
					<xsl:if test="count(notes) gt 0">
						<p>Notes : </p>
						<div class="response">			
							<xsl:for-each select="notes">
							<p class="lb2"> 
								#<xsl:value-of select="position()" />: <xsl:value-of select="current()" />
							</p>
							</xsl:for-each>
						</div>
					</xsl:if>		
				</div>			
		</xsl:for-each>
	</xsl:template>
</xsl:stylesheet>