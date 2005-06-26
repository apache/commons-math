<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:template match="/">
        <html xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
            <head>
                <xsl:apply-templates select="/document/properties/title"/>
                <xsl:apply-templates select="/document/meta"/>
                <style type="text/css">
                    @import url("../style/tigris.css");
                    @import url("../style/maven.css");
                    @import url("../style/project.css");
                </style>
                <link rel="stylesheet" href="../style/print.css" type="text/css" media="print"></link>
            </head>
            <xsl:apply-templates select="/document/body"/>
        </html>
    </xsl:template>
    <xsl:template match="body">
        <body>
            <div class="app">
                <xsl:apply-templates/>
            </div>
        </body> 
    </xsl:template>
    <xsl:template match="section">
        <div>
            <h3><xsl:value-of select="@name"/></h3>
            <xsl:apply-templates/>
        </div>
    </xsl:template>
    <xsl:template match="subsection">
        <div>
            <h4><xsl:value-of select="@name"/></h4>
            <xsl:apply-templates/>
        </div>
    </xsl:template>
    <xsl:template match="source">
        <div id="source">
            <pre>
                <xsl:apply-templates/>
            </pre>
        </div>
    </xsl:template> 
    <xsl:template match="node()|@*">
        <xsl:copy>
            <xsl:apply-templates select="@*"/>
            <xsl:apply-templates/>
        </xsl:copy>
    </xsl:template>
</xsl:stylesheet>
