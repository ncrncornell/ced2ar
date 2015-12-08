<!--  Stylesheet to generate SAS code for one specific variable in a Codebook-->

<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
	xmlns:ncrnxsl="https://www.ncrn.cornell.edu">
	<xsl:import href="sasVariableFunctions.xsl" />
	<xsl:output method="html" indent="yes" encoding="UTF-8" omit-xml-declaration="yes" />
	<xsl:template match="/">
	
	
	 
		<xsl:variable name='originalVarName'>
			<xsl:value-of select="codeBook/var/@name" />
		</xsl:variable>


		
		<xsl:variable name='varName'>
			<xsl:value-of select="ncrnxsl:getAlternateVariableName($originalVarName)" />
		</xsl:variable>
		
		<xsl:if test="codeBook/var/catgry/*"> <!--  print proc format if variable have caegory values -->
			<xsl:text>&#xa;</xsl:text>
			<xsl:text>proc format;</xsl:text>
			<xsl:text>&#xa;</xsl:text>
			<xsl:text>value </xsl:text>
							<!-- 
					This block of code determines if the variable name should be prepended with $
					 A variable name will be prepended with $ sign if the category contains an alpha character
				 -->
			
			<xsl:variable name="dollarPrepend">
				<xsl:for-each select="codeBook/var/catgry">
					<xsl:variable name="categoryName">
						<xsl:value-of select="catValu"/>
					</xsl:variable>
					<xsl:variable name="alpha" 
	             		 select="'abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ'"/>
					<xsl:if test="string-length(translate($categoryName,translate($categoryName, $alpha, ''),'')) &gt; 0">
	    					<xsl:value-of select = "'$'"/>
					</xsl:if>
				</xsl:for-each>
			</xsl:variable>
			
			<xsl:if test="string-length($dollarPrepend) &gt; 0">
				<xsl:text>$</xsl:text>
			</xsl:if>
			<xsl:value-of select="$varName"/>
			<xsl:text>&#xa;</xsl:text>
		</xsl:if>		
		<xsl:if test="codeBook/var/catgry/*">
			<xsl:for-each select="codeBook/var/catgry">
			<!--
				This block prints
				variableName = Variable Label
								Example output of this block 
					1 ="N/N"
					2 ="Yes, laid off"
					3 = ""	
				
				
				if the variable name is 'Sysmiss' prin "."
				otherwise
				do the following 
					in case the value is "N/A" we will print "." (dot) 1 = "."
					in case the value is empty, we will print the label it self 3 = "3"
				
				 
			 -->
			 		<xsl:variable name='valueName'>
						<xsl:value-of select="catValu" />
					</xsl:variable>
			 
					<xsl:value-of select="ncrnxsl:getSanitizedString($valueName)"/>
					<xsl:variable name = "valueLabel">
						<xsl:value-of select="labl"/>
					</xsl:variable>
					<xsl:text> ="</xsl:text>
					<xsl:choose>
						<xsl:when test='$valueName eq "Sysmiss"'>
							<xsl:text>.</xsl:text>
						</xsl:when>
						<xsl:otherwise>
							<xsl:choose>
								<xsl:when test='$valueLabel eq "N/A"'>
									<xsl:text>.</xsl:text>
								</xsl:when>
								<xsl:when test='$valueLabel eq ""'> 
									<xsl:value-of select='ncrnxsl:getSanitizedString($valueName)'/>
								</xsl:when>
								<xsl:otherwise>
									<xsl:value-of select='ncrnxsl:getSanitizedString($valueLabel)'/>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:otherwise>
					</xsl:choose>
					
					<xsl:text>"&#xa;</xsl:text>
				</xsl:for-each>
			
			<xsl:text>;</xsl:text>
			<xsl:text>&#xa;</xsl:text>
			<xsl:text>run;</xsl:text>
			<xsl:text>&#xa;</xsl:text>
			<xsl:text>&#xa;</xsl:text>
		</xsl:if>
		<xsl:text>/* data </xsl:text>

		<!--  Get data file name if the it contains sas file name -->
		<xsl:variable name ="sasDataFileName">		
			<xsl:for-each select="codeBook/files/fileDscr/fileTxt">
				<xsl:variable name ="fileType">
					<xsl:value-of select="fileType"/>
				</xsl:variable>
				<xsl:if test ="$fileType eq 'SAS'">
					<xsl:value-of select="fileName"/>
				</xsl:if>
			</xsl:for-each>
		</xsl:variable>

		<xsl:variable name ="altDataFileName">		
			<xsl:for-each select="codeBook/files/fileDscr/fileTxt">
				<xsl:value-of select="fileName"/>
			</xsl:for-each>
		</xsl:variable>	
		<xsl:choose>
			<xsl:when test ="string-length($sasDataFileName) eq 0">
				<xsl:value-of select='substring-before($altDataFileName,".")'/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select='substring-before($sasDataFileName,".")'/>
			</xsl:otherwise>
		</xsl:choose>				
			
	
		
		<xsl:text>;&#xa;</xsl:text>
		
		<xsl:text>set input;</xsl:text>
		<xsl:text>&#xa;</xsl:text>
		<xsl:variable name="labelLength" select="string-length(codeBook/var/labl)"/>
		<xsl:if test="$labelLength > 0">
			<xsl:text>label </xsl:text>
			<xsl:value-of select="$originalVarName"/>
			<xsl:text>="</xsl:text>
			<xsl:value-of select="codeBook/var/labl"/>
			<xsl:text>";</xsl:text>
			<xsl:text>&#xa;</xsl:text>
		</xsl:if>	
		
		<xsl:if test="codeBook/var/catgry/*"> <!--  No format statement is needed if there are no categories-->
			<xsl:text>format </xsl:text>
			<xsl:value-of select="$originalVarName"/>
			<xsl:text> </xsl:text>
			<xsl:value-of select="$varName"/>
			<xsl:text>.;</xsl:text>
			<xsl:text>&#xa;</xsl:text>
		</xsl:if>
		
		<xsl:text>run;</xsl:text>
		<xsl:text>&#xa;</xsl:text>
   		<xsl:text>*/</xsl:text>
		
	</xsl:template>
</xsl:stylesheet>