<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="html" indent="yes" encoding="UTF-8" omit-xml-declaration="yes" />
	<xsl:template match="/">
		<xsl:variable name='handle'>
			<xsl:value-of select="codeBook/@handle" />
		</xsl:variable>
		<!-- 
			This is empty because ced2ar does not display any /otherMat element data.  (Nothing codebook.xsl)
			It is set up to contain /codeBook/otherMat content. 
			
			The EL below is really just a placeholder, so it will not error out.  You can replace it with any EL you want. 
			Right now, you could replace it with any EL you want for DDI complex types: /docDscr, /stdyDscr, /fileDscr and /dataDscr.
		-->
		<div class="value4">
			<xsl:if test="count(codeBook/otherMat) eq 0">
				<br></br>
				<p>Sorry, no OtherMat information is available.</p>
			</xsl:if>
		</div>
		<div class="printLB"></div>
	</xsl:template>
</xsl:stylesheet>