<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="html" indent="yes" encoding="UTF-8"
		omit-xml-declaration="yes" />
	<xsl:template match="/codeBook">
		<table class="table1">
			<tr>
				<td class="staticHeader">Variable Name</td>
				<td class="value">
					<xsl:value-of select="var/@name" />
				</td>
			</tr>
			<tr>
				<td class="staticHeader">Label</td>
				<td class="value">
					<xsl:value-of select="var/labl"></xsl:value-of>
				</td>
			</tr>
			<tr>
				<td class="staticHeader">Codebook</td>
				<td class="value">
					<xsl:variable name='codebook'>
						<xsl:value-of select="titl" />
					</xsl:variable>
					<xsl:value-of select="$codebook" />
				</td>
			</tr>
			<tr>
				<td class="staticHeader">Concept</td>
				<td class="value">
					<xsl:if test="var/concept != ''">
						<xsl:value-of select="var/concept" />
					</xsl:if>
				</td>
			</tr>
			<tr>
				<td class="staticHeader">Type</td>
				<td class="value">
					<xsl:value-of select="var/varFormat/@type"></xsl:value-of>
				</td>
			</tr>
			<tr>
				<td class="staticHeader">Direct Link </td>
				<td class="value">
					<a>
						<xsl:attribute name="href">codebooks/<xsl:value-of
							select="@handle" />/vars/<xsl:value-of select="var/@name" /></xsl:attribute>
						<xsl:attribute name="target">_blank</xsl:attribute>
						<xsl:value-of select="var/@name" />
					</a>
				</td>
			</tr>
		</table>
		<xsl:if test="count(var/sumStat) gt 0">
			<p class="toggleHeader tcs">Summary Statistics</p>
			<div class="toggleContent value2">
				<xsl:for-each select="var/sumStat">
					<xsl:value-of select="@type" />
					&#160;
					<xsl:value-of select="current()" />
					<br />
				</xsl:for-each>
			</div>
		</xsl:if>
		<xsl:if test="var/txt != ''">
			<p class="toggleHeader tcs">Full Description</p>
			<div class="toggleContent value2">
				<xsl:copy-of select="var/txt/node()" />
			</div>
		</xsl:if>
		<xsl:if test="var/catgry != 'None'">
			<p class="toggleHeader tcs">
				Values (<xsl:value-of select="count(var/catgry)" />
				total)
			</p>
			<div class="toggleContent value2">
				<table class="table2">
					<xsl:for-each select="var/catgry">
						<tr>
							<td class="headerSmall">
								<xsl:value-of select="catValu"></xsl:value-of>
							</td>
							<td>
								<xsl:value-of select="labl" />
							</td>

						</tr>
					</xsl:for-each>
				</table>
			</div>
		</xsl:if>
		<xsl:if test="count(var/notes) gt 0">
			<p class="toggleHeader tcs">Notes (<xsl:value-of select="count(var/notes)" />
				total)
			</p>
			<div class="toggleContent">			
				<xsl:for-each select="var/notes">
				<p class="lb2"> 
					#<xsl:value-of select="position()" /><br />
					<xsl:value-of select="current()" />
				</p>
				</xsl:for-each>
			</div>
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>