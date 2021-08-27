<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <xsl:template match="@*|node()">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()"/>
        </xsl:copy>
    </xsl:template>

    <!--
    ==============ANTES==================
        CRITERION   RECORD      CONTROL
    0   NO/NO       NO/NO       NO/NO (5)
    A   A/A         NO/A        NO/NO (5)
    1   SI/SI       NO/SI       SI/SI (5)
    C   SI/NO (1)   NO/NO (3)   SI/NO (1)
    M   NO/SI (2)   NO/SI (4)   NO/SI (4)

    ==============DESPUES====================================
        CRITERION   RECORD      CONTROL     CRITERION_CONTROL
    0   NO/NO       NO/NO       NO/NO       NO/NO
    A   A/A         NO/A        NO/NO       NO/NO
    1   SI/SI       NO/SI       NO/SI       SI/SI

    (1) No existen formularios de mantenimiento que usen esta combinación.
        Pasa a ser requerido=1
    (2) Inválido, todo criterio requerido para mantenimiento es requerido
        para consulta. Pasa a ser requerido=1
    (3) Pasa a ser requerido=0
    (4) Pasa a ser requerido=1
    (5) Pasa a ser CRITERION_CONTROL
    -->

    <xsl:template match="@type[parent::*[@type='CONTROL' and parent::*[child::attribute[@nom='req' and text()!='M' and text()!='C'] or not(child::attribute[@nom='req'])]]]">
        <!-- Caso (5) -->
        <xsl:attribute name="type">CRITERION_CONTROL</xsl:attribute>
    </xsl:template>

    <xsl:template match="attribute[@nom='req' and (text()='C') and parent::*[child::*[@type='RECORD']]]">
        <!-- Caso (3) -->
        <attribute nom="req">0</attribute>
    </xsl:template>

    <xsl:template match="attribute[@nom='req' and (text()='C' or text()='M')]">
        <!-- Casos (1), (2) y (4) -->
        <attribute nom="req">1</attribute>
    </xsl:template>

</xsl:stylesheet>