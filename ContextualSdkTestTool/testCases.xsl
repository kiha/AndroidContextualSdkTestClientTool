<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:template match="/">
		<html>
			<body>
				<h2><xsl:value-of select="test/testSuite/@sdkType" /> Test cases</h2>
												
				<table border="1">
					<xsl:for-each select="test/testSuite">



						<tr bgcolor="#2895C4">
							<th><xsl:value-of select="@name" /></th>
							<th>SDK Method</th>
							<th>Test case title</th>
						</tr>

						<xsl:for-each select="testCase">
							<tr>
								<td>
									<xsl:value-of select="@id" />
								</td>
								<td>
									<xsl:value-of select="@sdk" />
								</td>
								<td>
									<xsl:value-of select="@name" />
								</td>								
							</tr>
						</xsl:for-each>
					</xsl:for-each>
				</table>
			</body>
		</html>
	</xsl:template>

</xsl:stylesheet>