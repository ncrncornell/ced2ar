<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="html" indent="yes" encoding="UTF-8"
		omit-xml-declaration="yes" />
	<xsl:template match="/">
		<xsl:value-of select="//p[2]" />
	</xsl:template>
</xsl:stylesheet>