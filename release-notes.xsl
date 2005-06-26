<!--
   Copyright 2005 The Apache Software Foundation

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<!--
   Stylesheet to convert maven changelog plugin xml files (changes.xml) to
   plain text format.
-->
<xsl:output method="text" version="1.0" encoding="iso-8859-1"/>
<xsl:variable name="CRLF" select="'&#013;&#010;'"/>
<xsl:template match="/">
  <xsl:value-of select="document/properties"/>
    <xsl:for-each select="document/body/release">
-------------------------------------------------------------------------------
Version <xsl:value-of select="@version"/><xsl:value-of select="$CRLF"/>
<xsl:value-of select="@date"/><xsl:value-of select="$CRLF"/>
<xsl:value-of select="$CRLF"/>
<xsl:call-template name="keep.breaks">
<xsl:with-param name="input" select="@description"/>
</xsl:call-template><xsl:value-of select="$CRLF"/>
BUG FIXES
      <xsl:for-each select="action">
        <xsl:if test="contains(@type,'fix')">
          <xsl:choose>
            <xsl:when test="@issue">
  Bugzilla # <xsl:value-of select="@issue"/>
            </xsl:when>
            <xsl:otherwise>
            </xsl:otherwise>
         </xsl:choose>
    <xsl:value-of select="."/>
       </xsl:if>
    </xsl:for-each>
ENHANCEMENTS
    <xsl:for-each select="action">
      <xsl:if test="contains(@type,'update')">
        <xsl:choose>
          <xsl:when test="@issue">
  Bugzilla # <xsl:value-of select="@issue"/>
          </xsl:when>
          <xsl:otherwise>
          </xsl:otherwise>
        </xsl:choose>
    <xsl:value-of select="."/>
      </xsl:if>
    </xsl:for-each>
NEW FEATURES
    <xsl:for-each select="action">
      <xsl:if test="contains(@type,'add')">
        <xsl:choose>
          <xsl:when test="@issue">
  Bugzilla # <xsl:value-of select="@issue"/>
          </xsl:when>
          <xsl:otherwise>
          </xsl:otherwise>
        </xsl:choose>
    <xsl:value-of select="."/>
      </xsl:if>
    </xsl:for-each>
  </xsl:for-each>
<xsl:value-of select="$CRLF"/>
</xsl:template>

<!-- 
     kludge to re-insert line feeds removed by parser. Assumes new lines all
     start with three or more leading spaces. 
-->
<xsl:variable name="pad" select="'   '"/>
<xsl:template name="keep.breaks">
  <xsl:param name="input"/>
  <xsl:if test="string-length($input) &gt; 0">
    <xsl:choose>
      <xsl:when test="contains($input,$pad)">
        <xsl:variable name="init" select="normalize-space(substring-before($input,$pad))"/>
        <xsl:variable name="term" select="substring-after($input,$pad)"/>
        <xsl:if test="string-length($init) &gt; 0">
          <xsl:value-of select="$init"/><xsl:value-of select="$CRLF"/>
        </xsl:if>
        <xsl:if test="string-length($term) &gt; 0">
          <xsl:call-template name="keep.breaks">
            <xsl:with-param name="input" select="$term"/>
          </xsl:call-template>
        </xsl:if>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="normalize-space($input)"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:if>
</xsl:template>
</xsl:stylesheet>
