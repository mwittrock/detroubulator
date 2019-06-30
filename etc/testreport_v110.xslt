<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:output method="html" version="1.0" encoding="UTF-8" indent="yes"/>

	<xsl:variable name="program" select="/testreport/header/program"/>
	<xsl:variable name="server" select="/testreport/header/server"/>
	<xsl:variable name="test_started" select="/testreport/header/started"/>
	<xsl:variable name="test_finished" select="/testreport/header/finished"/>
	<xsl:variable name="exectime_millis" select="/testreport/header/exectime/millis"/>
	<xsl:variable name="exectime_seconds" select="/testreport/header/exectime/seconds"/>
	<xsl:variable name="testcases" select="/testreport/header/testcases"/>
	<xsl:variable name="assertions_passed" select="/testreport/header/assertions/passed"/>
	<xsl:variable name="assertions_failed" select="/testreport/header/assertions/failed"/>

	<xsl:template match="/">
		<html>
			<head>
				<title><xsl:text>Detroubulator test results</xsl:text></title>
				<style type='text/css'>
					<xsl:text disable-output-escaping="yes">
						body {
							background: #FFF;
							margin-left: 40px;
							margin-top: 0px;
						}

						h1 {
							font: 160% Palatino Linotype, Book Antiqua, Palatino, serif;
							letter-spacing: 3px;
							color: #4d6897;
							margin-top: 0px;
							margin-bottom: 10px;
							padding-top: 20px;
							border-bottom: 1px dotted #E6EAE9;
						}

						a {
							color: #4d6897;
						}

						li {
							margin-bottom: 5px;
						}

						p {
							font: 80% Trebuchet MS, Helvetica, sans-serif;
							margin: 0px;
							padding-bottom: 10px;
						}

						#generatedby {
							margin-top: 20px;
							font: 70% Arial, arial, sans-serif;
							color: #999;
						}

						#quickview {
							float: right;
							width: 250px;
							background: #E6EAE9;
							padding-top: 10px;
							padding-bottom: 10px;
							margin-right: 50px;
							border-left: 1px solid #CCC;
							border-right: 1px solid #999;
							border-bottom: 1px solid #999;
						}

						#quickview h1 {
							font: 250% Palatino Linotype, Book Antiqua, Palatino, serif;
							letter-spacing: 1px;
							border: 0px;
							margin: 0px;
							padding: 0px;
						}

						#quickview h2 {
							font: 100% Palatino Linotype, Book Antiqua, Palatino, serif;
							margin: 0px;
							padding-left: 5px;
						}

						#quickview .colorblack {
							color: #000000;
						}

						#quickview .colorgreen {
							color: #00B000;
						}

						#quickview .colorred {
							color: #B00000;
						}

						.message {
							font: 80% Lucida Console, Monaco, monospace;
							padding-top: 5px;
							padding-bottom: 5px;
							padding-left: 10px;
							padding-right: 10px;
							margin-bottom: 5px;
							margin-left: 10px;
							border-left: 3px solid #4d6897;
						}

						.separator {
							margin-top: 5px;
							margin-bottom: 5px;
						}
					</xsl:text>
				</style>
			</head>
			<body>
				<div id="quickview">
					<table border="0" align="center">
						<xsl:call-template name="TestCases"/>
						<xsl:call-template name="PassedAssertions"/>
						<xsl:call-template name="FailedAssertions"/>
					</table>				
				</div>
				<h1><xsl:text>Detroubulator test results</xsl:text></h1>
				<p>
					<b><xsl:text>Mapping program:</xsl:text></b><br/><xsl:value-of select="$program"/>
				</p>
				<p>
					<b><xsl:text>XI server:</xsl:text></b><br/><xsl:value-of select="$server"/>
				</p>
				<p>
					<xsl:text>Test started </xsl:text><b><xsl:value-of select="$test_started"/></b><xsl:text> and finished </xsl:text><b><xsl:value-of select="$test_finished"/></b><xsl:text>.</xsl:text><br/>
					<xsl:text>Execution time was </xsl:text><b><xsl:value-of select="$exectime_seconds"/><xsl:text> seconds</xsl:text></b><xsl:text>.</xsl:text>
				</p>
				<xsl:if test="/testreport/details/failure">
					<h1><xsl:text>Messages</xsl:text></h1>
					<xsl:for-each select="/testreport/details/failure">
						<xsl:call-template name="failure">
							<xsl:with-param name="f" select="."/>
						</xsl:call-template>
					</xsl:for-each>
				</xsl:if>
				<div id="generatedby"><xsl:text>Generated by </xsl:text><a href="http://www.applicon.dk/detroubulator"><xsl:text>Detroubulator</xsl:text></a></div>
			</body>
		</html>
	</xsl:template>

	<xsl:template name="failure">
		<xsl:param name="f"/>
		<p>
			<xsl:text>Input document </xsl:text>
			<a>
				<xsl:attribute name="href">
					<xsl:value-of select="$f/input/url"/>
				</xsl:attribute>
				<xsl:attribute name="target">
					<xsl:text>_blank</xsl:text>
				</xsl:attribute>
				<xsl:value-of select="$f/input/filename"/>
			</a>
			<xsl:if test="$f/output">
				<xsl:text> - Dumped output </xsl:text>
				<a>
					<xsl:attribute name="href">
						<xsl:value-of select="$f/output/url"/>
					</xsl:attribute>
					<xsl:attribute name="target">
						<xsl:text>_blank</xsl:text>
					</xsl:attribute>
					<xsl:value-of select="$f/output/filename"/>
				</a>
			</xsl:if>
		</p>
		<xsl:for-each select="$f/messages/message">
			<div class="message">
				<xsl:value-of select="."/>
			</div>
		</xsl:for-each>
		<div class="separator">
			<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
		</div>
	</xsl:template>

	<xsl:template name="TestCases">
		<tr>
			<td align="right"><h1 class="colorblack"><xsl:value-of select="$testcases"/></h1></td>
			<td><h2>
				<xsl:choose>
					<xsl:when test="$testcases = 1">
						<xsl:text>Test case</xsl:text>
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>Test cases</xsl:text>
					</xsl:otherwise>
				</xsl:choose>				
			</h2></td>
		</tr>
	</xsl:template>

	<xsl:template name="PassedAssertions">
		<tr>
			<td align="right"><h1 class="colorgreen"><xsl:value-of select="$assertions_passed"/></h1></td>
			<td><h2>
				<xsl:choose>
					<xsl:when test="$assertions_passed = 1">
						<xsl:text>Passed assertion</xsl:text>
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>Passed assertions</xsl:text>
					</xsl:otherwise>
				</xsl:choose>						
			</h2></td>
		</tr>
	</xsl:template>

	<xsl:template name="FailedAssertions">
		<tr>
			<td align="right"><h1 class="colorred"><xsl:value-of select="$assertions_failed"/></h1></td>
			<td><h2>
				<xsl:choose>
					<xsl:when test="$assertions_failed = 1">
						<xsl:text>Failed assertion</xsl:text>
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>Failed assertions</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
			</h2></td>
		</tr>
	</xsl:template>

</xsl:stylesheet>