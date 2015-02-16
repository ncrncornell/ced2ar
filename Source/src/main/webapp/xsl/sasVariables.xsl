<!--  Stylesheet to generate SAS code for one specific variable in a Codebook-->

<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
	xmlns:ncrnxsl="https://www.ncrn.cornell.edu">
	<xsl:import href="sasVariableFunctions.xsl" />
	<xsl:output method="html" indent="yes" encoding="UTF-8" omit-xml-declaration="yes" />
	<xsl:template match="/">
		<xsl:for-each select="codeBook/dataDscr/var">
			
			<!--
				This block of code appends x to the variable name if the variable name ends with a digit.
				Example: if variable name is ABSCENT  varName will be ABSCENT
		   		if variable name is ANCESTR1 var name will be ANCESTR1x
	 		-->
			
			<xsl:variable name='originalVarName'>
				<xsl:value-of select="@name" />
			</xsl:variable>
			<xsl:variable name='varName'>
				<xsl:value-of select="ncrnxsl:getAlternateVariableName($originalVarName)" />
			</xsl:variable>

			<!--  End Variable append -->	
			<xsl:if test="catgry/*"> <!--  print proc format if variable have caegory values -->
				<xsl:text>&#xa;</xsl:text>
				<xsl:text>proc format;</xsl:text>
				<xsl:text>&#xa;</xsl:text>
				<xsl:text>value </xsl:text>
				<!-- 
					This block of code determines if the variable name should be prepended with $
					 A variable name will be prepended with $ sign if the category contains an alpha character
				 -->
				<xsl:variable name="dollarPrepend">
					<xsl:for-each select="catgry">
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

			<xsl:if test="catgry/*">
				<xsl:for-each select="catgry">
			<!--
				Example output of this block 
					1 ="N/A"
					2 ="Yes, laid off"
					3 = ""	
				in case the value is "N/A" we will print "." (dot) 1 = "."
				in case the value is empty, we will print the label it self 3 = "3"
				 
			 -->
			 		
					<xsl:value-of select="catValu"/>
					<xsl:variable name = "valueLabel">
						<xsl:value-of select="labl"/>
					</xsl:variable>
					<xsl:text> ="</xsl:text>
					<xsl:choose>
						<xsl:when test='$valueLabel eq "N/A"'>
							<xsl:text>.</xsl:text>
						</xsl:when>
						<xsl:when test='$valueLabel eq ""'> 
							<xsl:value-of select="catValu"/>
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of select="$valueLabel"/>
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
		</xsl:for-each>
		
		<xsl:text>&#xa;</xsl:text>
		<xsl:text>/* data </xsl:text>
		
		<!-- 
		<xsl:value-of select="codeBook/fileDscr/fileTxt/fileName"/>
		 -->
		
				<!--  Get data file name if the it contains sas file name -->
		<xsl:variable name ="sasDataFileName">		
			<xsl:for-each select="codeBook/fileDscr/fileTxt">
				<xsl:variable name ="fileType">
					<xsl:value-of select="fileType"/>
				</xsl:variable>
				<xsl:if test ="$fileType eq 'SAS'">
					<xsl:value-of select="fileName"/>
				</xsl:if>
			</xsl:for-each>
		</xsl:variable>

		<xsl:variable name ="altDataFileName">		
			<xsl:for-each select="codeBook/fileDscr/fileTxt">
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
		
		
		
		<xsl:for-each select="codeBook/dataDscr/var">
			<xsl:variable name="labelLength" select="string-length(labl)"/>
			<xsl:if test="$labelLength > 0">
				<xsl:text>label </xsl:text> 
				<xsl:value-of select="@name"/>
				<xsl:text> = "</xsl:text>
				<xsl:value-of select="labl"/>
				<xsl:text>";</xsl:text>
				<xsl:text>&#xa;</xsl:text>
			</xsl:if>
		</xsl:for-each>

		<xsl:text>format&#xa;</xsl:text>
		<xsl:for-each select="codeBook/dataDscr/var">
			
			<!--
				This block of code appends x to the variable name if the variable name ends with a digit.
				Example: if variable name is ABSCENT  varName will be ABSCENT
		   		if variable name is ANCESTR1 var name will be ANCESTR1x
	 		-->
			
			<xsl:variable name='originalVarName'>
				<xsl:value-of select="@name" />
			</xsl:variable>
			
			<xsl:value-of select="$originalVarName"/> <xsl:text> </xsl:text><xsl:value-of select="ncrnxsl:getAlternateVariableName($originalVarName)"/><xsl:text>.</xsl:text>
			<xsl:text>&#xa;</xsl:text>
		</xsl:for-each>
		<xsl:text>;&#xa;</xsl:text>
		
		<xsl:text>run;&#xa;*/</xsl:text>
		
	</xsl:template>
</xsl:stylesheet>