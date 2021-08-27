<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <xsl:template match="@*|node()">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()"/>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="container[not(@clo)]">
        <xsl:copy>
            <!-- Default N para formularios anteriores a la version 5 -->
            <xsl:attribute name="clo">N</xsl:attribute>
            <xsl:apply-templates select="@*|node()"/>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="/webPage/properties">
        <xsl:copy>
            <!-- Default true para join quirk para formularios anteriores -->
            <xsl:attribute name="joinQuirk">1</xsl:attribute>
            <xsl:apply-templates select="@*|node()"/>
        </xsl:copy>
    </xsl:template>

</xsl:stylesheet>