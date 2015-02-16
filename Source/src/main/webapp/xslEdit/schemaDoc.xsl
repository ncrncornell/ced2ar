<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="html" indent="yes" encoding="UTF-8"
		omit-xml-declaration="yes" />
	<xsl:template match="/">	  
		<div>
			<xsl:copy-of select="/documentation/node()" />
		</div>		  		
	</xsl:template>
</xsl:stylesheet>