<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="html" indent="yes" encoding="UTF-8" omit-xml-declaration="yes" />
	<xsl:template match="/">
		<xsl:text>&#xa;</xsl:text> 
		
		<xsl:text>label variable </xsl:text>
		<xsl:value-of select="codeBook/var/@name"/>
		<xsl:text> "</xsl:text>
		<xsl:value-of select="codeBook/var/labl"/>
		<xsl:text>"</xsl:text>
		<xsl:text>&#xa;</xsl:text> <!--  Carriage return and new line -->
		
		<xsl:text>label define l</xsl:text>
		<xsl:value-of select="codeBook/var/@name"/>
		<xsl:text> </xsl:text>
					
		<xsl:if test="codeBook/var/catgry/*">
			<xsl:for-each select="codeBook/var/catgry">
				<xsl:value-of select="catValu"/> 
				<xsl:text>  "</xsl:text>
				<xsl:variable name = "valueLabel">
					<xsl:value-of select="labl"/>
				</xsl:variable>
				
				<xsl:choose>
						<xsl:when test='$valueLabel eq ""'> 
							<xsl:value-of select="catValu"/>
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of select="labl"/>
						</xsl:otherwise>
				</xsl:choose>
				
				
				<xsl:text>"</xsl:text>
				<xsl:if test="position() != last()">
				   <xsl:text> ///</xsl:text>
				</xsl:if>
				<xsl:text>&#xa;&#9;&#9;&#9;</xsl:text> 
			</xsl:for-each>
			<xsl:text>&#xa;</xsl:text> 
			<xsl:text>/*</xsl:text>
			<xsl:text>&#xa;</xsl:text> 
			<xsl:text>label values </xsl:text>
			<xsl:value-of select="codeBook/var/@name"/> 
			<xsl:text> l</xsl:text>
			<xsl:value-of select="codeBook/var/@name"/>
			<xsl:text>&#xa;*/</xsl:text>
			
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>