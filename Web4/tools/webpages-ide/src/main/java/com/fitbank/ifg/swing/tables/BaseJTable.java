package com.fitbank.ifg.swing.tables;

import java.awt.Color;
import java.awt.Component;

import javax.swing.BorderFactory;

import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.BorderHighlighter;
import org.jdesktop.swingx.decorator.ColorHighlighter;
import org.jdesktop.swingx.decorator.ComponentAdapter;
import org.jdesktop.swingx.decorator.HighlightPredicate;
import org.jdesktop.swingx.decorator.Highlighter;
import org.jdesktop.swingx.decorator.HighlighterFactory;
import org.jdesktop.swingx.decorator.ShadingColorHighlighter;

import com.fitbank.css.Propiedad;
import com.fitbank.ifg.iFG;

/**
 * Tabla base para otras tablas
 *
 * @author FitBank CI
 */
public class BaseJTable extends JXTable {

    public BaseJTable() {
        Highlighter striping = HighlighterFactory.createAlternateStriping();
        Highlighter shading = new ShadingColorHighlighter(
                new HighlightPredicate.ColumnHighlightPredicate(0));
        Highlighter border = new BorderHighlighter(HighlightPredicate.HAS_FOCUS,
                BorderFactory.createLineBorder(Color.DARK_GRAY), true, false);
        Highlighter error = new ColorHighlighter(new HighlightPredicate() {

            public boolean isHighlighted(Component renderer,
                    ComponentAdapter adapter) {
                Object value = adapter.getValue();
                if (value instanceof Propiedad) {
                    value = ((Propiedad) value).getValor();
                }
                return iFG.getSingleton().hasError(value);
            }

        }, new Color(1.0f, 0.0f, 0.0f, 0.2f), Color.BLACK,
                new Color(1.0f, 0.0f, 0.0f, 0.5f), Color.WHITE);

        addHighlighter(striping);
        addHighlighter(shading);
        addHighlighter(border);
        addHighlighter(error);

        setForeground(Color.BLACK);
    }

}
