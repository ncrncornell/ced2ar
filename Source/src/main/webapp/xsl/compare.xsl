<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xs="http://www.w3.org/2001/XMLSchema" exclude-result-prefixes="xs"
	version="2.0">
	<xsl:output method="html" indent="yes" encoding="UTF-8"
		omit-xml-declaration="yes" />
	<xsl:template match="/">
		<xsl:variable name='varName'>
			<xsl:value-of select="codeBook/var/@name" />
		</xsl:variable>
		<xsl:variable name='codebook'>
			<xsl:value-of select="codeBook/titl" />
		</xsl:variable>
		<xsl:variable name='handle'>
			<xsl:value-of select="codeBook/@handle" />
		</xsl:variable>
		<td>
			<a>
				<xsl:attribute name="href">codebooks/<xsl:value-of
					select="$handle" />/vars/<xsl:value-of select="$varName" /></xsl:attribute>
				<xsl:value-of select="$varName" />
			</a>
		</td>
		<td class="codebookColumn">
			<a>
				<xsl:attribute name="href">codebooks/<xsl:value-of
					select="$handle" />
						</xsl:attribute>
				<xsl:value-of select="$codebook" />
			</a>
		</td>
		<td class="labelColumn">
			<xsl:value-of select="codeBook/var/labl" />
		</td>
		<td class="descColumn">
			<xsl:choose>
				<xsl:when test="string-length(codeBook/var/txt) > 250">
					<span class="truncPre printRemove">
						<xsl:copy-of select="substring(codeBook/var/txt,0,250)" />
					</span>				
					<span class="truncFull hidden" itemprop="description">
						<xsl:copy-of select="codeBook/var/txt" />
					</span>
					<span class="truncExp"> ... more</span>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="codeBook/var/txt" />
				</xsl:otherwise>
			</xsl:choose>			
		</td>
	</xsl:template>
</xsl:stylesheet>