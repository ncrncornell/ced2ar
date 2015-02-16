<!--  Stylesheet to generate SAS code for one specific variable in a Codebook-->

<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
	xmlns:ncrnxsl="https://www.ncrn.cornell.edu">
	<!--
		This function appends x to the variable name if the variable name ends with a digit.
		Example: if variable name is ABSCENT  varName will be ABSCENT
		   		 if variable name is ANCESTR1 var name will be ANCES1x
		   		 if variable is HINS1 var name HINS1x
	 -->
	 <xsl:function name="ncrnxsl:getShortenedVarName">
	    <xsl:param name="vName"/>

	    <xsl:variable name="length" select="string-length($vName)"/>
	    <xsl:variable name='lCharacter'>
			<xsl:value-of select="substring($vName,$length)"/>
		</xsl:variable>
		<xsl:variable name="uid" >
			<xsl:value-of select="ncrnxsl:getRandomNumber()"/>
		</xsl:variable>			

		<xsl:choose>
			<xsl:when test='$length > 7'>
				<xsl:value-of select="concat(substring($vName,1,3),$uid,substring($vName,$length - 3,3))"/>			
			</xsl:when>
			<xsl:when test='($length eq 7) and  contains("0123456789",$lCharacter)'>
				<xsl:value-of select="substring($vName,1,6)"/>			
			</xsl:when>
			
			<xsl:otherwise>
				<xsl:value-of select="$vName"/>
			</xsl:otherwise>
		</xsl:choose>
	 </xsl:function>
	 
	<xsl:function name="ncrnxsl:getAlternateVariableName">
	    <xsl:param name="vName"/>

		<xsl:variable name='origVariableName'>
			<xsl:value-of select="ncrnxsl:getShortenedVarName($vName)" />
		</xsl:variable>

	    <xsl:variable name="length" select="string-length($origVariableName)"/>
	    <xsl:variable name='lastCharacter'>
			<xsl:value-of select="substring($origVariableName,$length)"/>
		</xsl:variable>
		<xsl:choose>
			<xsl:when test='contains("0123456789",$lastCharacter)'>
				<xsl:value-of select="concat($origVariableName,'x')" />
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$origVariableName" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:function>
	
	<xsl:function name="ncrnxsl:getRandomNumber">
		<xsl:variable name ="currentTimeMilli">
			<xsl:value-of  select="current-time()"/>
		</xsl:variable>
		<xsl:variable name ="formatedCurrentTimeMilli">
			<xsl:value-of select="format-time($currentTimeMilli,'[f1]')" />
		</xsl:variable>
		<xsl:value-of select="substring($formatedCurrentTimeMilli,1,1)"/>			
	</xsl:function>
	
	
</xsl:stylesheet>