<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="html" indent="yes" encoding="UTF-8"
		omit-xml-declaration="yes" />
	<xsl:template match="/">
		<xsl:for-each select="codeBooks/codeBook">
			<xsl:sort select="@handle" />
			<xsl:variable name="label">
				<xsl:value-of select="current()/@label" />
			</xsl:variable>
			<xsl:variable name="baseHandle">
				<xsl:value-of select="current()/@handle" />
			</xsl:variable>
			<div class="versionBlock">
				<h4>
					<xsl:value-of select="@label" />
				</h4>
				<form class="indexForm" method="post">
					<table class="indexSettings">
						<tr>
							<th>Version</th>
							<th>Default</th>
						</tr>
						<xsl:for-each select="current()/version">
							<tr>
								<td>
									<xsl:value-of select="current()/@v" />
								</td>
								<td>
									<input type="radio" name="default">
										<xsl:attribute name="value">
										<xsl:value-of select="$baseHandle" />.<xsl:value-of
											select="current()/@v" />
									</xsl:attribute>
										<xsl:if test="current()/@use = 'default'">
											<xsl:attribute name="checked" />
										</xsl:if>
									</input>
								</td>
							</tr>
						</xsl:for-each>
					</table>
				</form>
			</div>
		</xsl:for-each>
	</xsl:template>
</xsl:stylesheet>