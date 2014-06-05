<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:template match="/">
		<html>
		
			<body>
				<h2>
					Contextual SDK Test Report.
					<xsl:value-of select="testReport/@date" />
				</h2>

				<table border="1">
	
					<xsl:for-each select="testReport/testSuite">

						<xsl:choose>
							<xsl:when test="testCase">

								<tr bgcolor="#2895C4">
									<th>#</th>
									<th>
										<xsl:value-of select="@name" />
									</th>
									<th>Test Case</th>
									<th>Input Data</th>
									<th>Expected Result</th>
									<th>Test Result</th>
								</tr>

							</xsl:when>
						</xsl:choose>

						<xsl:for-each select="testCase">
							<tr>
								<td  WIDTH="100">
									<a>
										<xsl:attribute name="href">#q_<xsl:value-of
											select="@id" /></xsl:attribute>
										<xsl:value-of select="@id" />
									</a>
								</td>

								<td  WIDTH="100">
									<xsl:value-of select="@sdk" />
								</td>

								<td  WIDTH="100">
									<xsl:value-of select="@name" />
								</td>

								<td  WIDTH="100">
									<xsl:for-each select="parameter">
										<xsl:value-of select="@name" />
										=
										<xsl:value-of select="substring(../parameter,1,60)" />
										<br />
									</xsl:for-each>
								</td>

								<td  WIDTH="100">
									<xsl:for-each select="expected">
										<xsl:value-of select="." />
										<br />
									</xsl:for-each>
								</td>

								<xsl:choose>

									<xsl:when test="testResult/@result = 'Pass'">
										<td bgcolor="#006600" style="font-weight:bold"  WIDTH="100">
											<xsl:value-of select="testResult/@result" />
										</td>
									</xsl:when>

									<xsl:otherwise>
										<td bgcolor="#CC0000" style="font-weight:bold"  WIDTH="100">
										
										<a>
										<xsl:attribute name="href">#q_<xsl:value-of
											select="@id" /></xsl:attribute>										
											<xsl:value-of select="testResult/@result" />
										</a>	
										
										</td>
									</xsl:otherwise>
								</xsl:choose>

							</tr>
						</xsl:for-each>
					</xsl:for-each>


				</table>


				<br />
				<br />
				<br />
				<br />
				<br />
				<br />
				<br />
				<br />
				<br />


				<table>

					<xsl:for-each select="testReport/testSuite">

					<xsl:choose>
					<xsl:when test="testCase">
					
						<tr bgcolor="#2895C4">
							<th>TC #</th>
							<th>Result from SDK</th>
						</tr>

					</xsl:when>
					</xsl:choose>	
						<xsl:for-each select="testCase">
							<tr>

								<td valign="top">
									<a>
										<xsl:attribute name="name">q_<xsl:value-of
											select="@id" /></xsl:attribute>
										<xsl:value-of select="@id" />
									</a>
								</td>
								<td>
									<xsl:value-of select="testResult" />
								</td>
							</tr>

						</xsl:for-each>


					</xsl:for-each>
				</table>



			</body>
		</html>
	</xsl:template>

</xsl:stylesheet>