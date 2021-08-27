<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <xsl:template match="@*|node()">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()"/>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="*[(@class='com.fitbank.webpages.data.Dependency' or @class='com.fitbank.webpages.data.DataSource') and not(@comparator)]">
        <xsl:copy>
            <!-- Mantener LIKE como default para WebPages -->
            <xsl:attribute name="comparator">LIKE</xsl:attribute>
            <xsl:apply-templates select="@*|node()"/>
        </xsl:copy>
    </xsl:template>

</xsl:stylesheet>