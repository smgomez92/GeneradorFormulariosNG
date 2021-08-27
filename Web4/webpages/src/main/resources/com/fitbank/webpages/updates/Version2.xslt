<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <xsl:template match="@*|node()">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()"/>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="widget[starts-with(attribute[@nom='val'], '=') and not(attribute[@nom='mod'])]">
        <xsl:copy>
            <!-- Hacer no modificable widgets que tienen formulas -->
            <xsl:apply-templates select="@*|node()"/>
            <attribute nom="mod">0</attribute>
        </xsl:copy>
    </xsl:template>

</xsl:stylesheet>