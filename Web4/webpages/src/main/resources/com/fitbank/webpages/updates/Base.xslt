<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <xsl:template match="@*|node()">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()"/>
        </xsl:copy>
    </xsl:template>

    <!-- Forzar = para comparadores de dependencias de descripciones -->
    <xsl:template match="@comparator[parent::*[@class='com.fitbank.webpages.data.Dependency' and ancestor::*[@class='com.fitbank.webpages.data.DataSource']]]">
        <xsl:attribute name="comparator">=</xsl:attribute>
    </xsl:template>
    <xsl:template match="*[not(@comparator) and @class='com.fitbank.webpages.data.Dependency' and ancestor::*[@class='com.fitbank.webpages.data.DataSource']]">
        <xsl:copy>
            <xsl:attribute name="comparator">=</xsl:attribute>
            <xsl:apply-templates select="@*|node()"/>
        </xsl:copy>
    </xsl:template>

    <!-- Eliminar callback nulos -->
    <xsl:template match="callback[@class and not(child::*) and count(attribute::*)=1]">
    </xsl:template>
    <xsl:template match="callback[not(child::*) and not(attribute::*)]">
    </xsl:template>

    <!-- Eliminar preQuery nulos -->
    <xsl:template match="preQuery[@class and not(child::*) and count(attribute::*)=1]">
    </xsl:template>
    <xsl:template match="preQuery[not(child::*) and not(attribute::*)]">
    </xsl:template>

</xsl:stylesheet>