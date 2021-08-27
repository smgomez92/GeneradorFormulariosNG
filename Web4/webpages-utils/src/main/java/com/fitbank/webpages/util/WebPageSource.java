package com.fitbank.webpages.util;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

import com.fitbank.enums.AttachedPosition;
import com.fitbank.enums.Modificable;
import com.fitbank.js.FuncionJS;
import com.fitbank.js.JSParser;
import com.fitbank.js.LiteralJS;
import com.fitbank.js.NamedJSFunction;
import com.fitbank.serializador.xml.ExcepcionParser;
import com.fitbank.util.Debug;
import com.fitbank.webpages.Assistant;
import com.fitbank.webpages.AttachedWebPage;
import com.fitbank.webpages.Container;
import com.fitbank.webpages.WebElement;
import com.fitbank.webpages.WebPage;
import com.fitbank.webpages.WebPageXml;
import com.fitbank.webpages.assistants.AutoListOfValues;
import com.fitbank.webpages.assistants.ListOfValues;
import com.fitbank.webpages.assistants.None;
import com.fitbank.webpages.behaviors.FormulaDisabler;
import com.fitbank.webpages.data.FormElement;
import com.fitbank.webpages.data.Reference;
import com.fitbank.webpages.widgets.Button;
import com.fitbank.webpages.widgets.DeleteRecord;
import com.fitbank.webpages.widgets.TabBar;
import org.apache.commons.collections.Transformer;

/**
 * Define un interfaz para fuentes simples de WebPages.
 *
 * @author FitBank CI
 */
public abstract class WebPageSource {

    protected final Map<String, String> attachedCache =
            new HashMap<String, String>();

    public abstract WebPage getWebPage(String subsystem, String transaction,
            boolean attached);

    public WebPage processWebPage(WebPage webPage, String tabBase) {
        this.manageAttached(webPage);

        if (webPage.getRequiresQuery()) {
            setDisabled(webPage);
        }

        if (!webPage.getStore()) {
            setAlwaysDisabled(webPage);
        }

        tabBase = tabBase.equals("0") ? "" : tabBase + "-";

        for (TabBar tabBar : IterableWebElement.get(webPage, TabBar.class)) {
            int i = 0;
            for (String s : tabBar.getTabs()) {
                tabBar.getTabs().set(i++, tabBase + s);
            }
        }

        for (Container container : webPage) {
            container.setTab(tabBase + container.getTab());

            if (!container.getVisible()) {
                container.setCSSClass("oculto");
                continue;
            }

            if (container.getReadOnly()) {
                setReadOnly(container);
                container.hideElements(new Button(), true);
                container.hideElements(new DeleteRecord(), false);
            }
        }

        return webPage;
    }

    private void manageAttached(WebPage webPage) {
        List<AttachedWebPage> attached =
                new LinkedList<AttachedWebPage>(webPage.getAttached());

        Set<AttachedWebPage> fixed =
                new TreeSet<AttachedWebPage>(new Comparator<AttachedWebPage>() {

            @Override
            public int compare(AttachedWebPage a, AttachedWebPage b) {
                return a.getContainerIndex() < b.getContainerIndex() ? 1 : -1;
            }

        });

        for (AttachedWebPage attachedWebPage : attached) {
            if (attachedWebPage.getPosition() == AttachedPosition.FIXED) {
                fixed.add(attachedWebPage);
            }
        }

        for (AttachedWebPage attachedWebPage : fixed) {
            if (attachedWebPage.getPosition() == AttachedPosition.FIXED) {
                WebPage nueva = getAttached(attachedWebPage);
                if (attachedWebPage.getContainerIndex() > webPage.size()
                        || attachedWebPage.getContainerIndex() < 0) {
                    webPage.addAll(nueva);
                } else {
                    webPage.addAll(attachedWebPage.getContainerIndex(), nueva);
                }
                mergeReferences(webPage.getReferences(), nueva.getReferences(),
                        attachedWebPage.getPosition());
                mergeInitialJS(webPage, nueva, attachedWebPage.getPosition());
            }
        }

        for (AttachedWebPage attachedWebPage : attached) {
            if (attachedWebPage.getPosition() == AttachedPosition.AFTER) {
                WebPage nueva = getAttached(attachedWebPage);
                webPage.addAll(nueva);
                mergeReferences(webPage.getReferences(), nueva.getReferences(),
                        attachedWebPage.getPosition());
                mergeInitialJS(webPage, nueva, attachedWebPage.getPosition());
            }
        }

        Collections.reverse(attached);

        for (AttachedWebPage attachedWebPage : attached) {
            if (attachedWebPage.getPosition() == AttachedPosition.BEFORE) {
                WebPage nueva = getAttached(attachedWebPage);
                webPage.addAll(0, nueva);
                mergeReferences(webPage.getReferences(), nueva.getReferences(),
                        attachedWebPage.getPosition());
                mergeInitialJS(webPage, nueva, attachedWebPage.getPosition());
            }
        }
    }

    private void setAlwaysDisabled(WebElement webElement) {
        for (FormElement formElement : IterableWebElement.get(webElement,
                FormElement.class)) {
            if (formElement.getDataSource().esRegistro()) {
                formElement.setModificable(Modificable.SOLO_LECTURA);
                CollectionUtils.filter(formElement.getBehaviors(),
                        new Predicate() {

                            @Override
                            public boolean evaluate(Object object) {
                                return !(object instanceof FormulaDisabler);
                            }

                        });
                Assistant assistant = formElement.getAssistant();
                if (assistant instanceof AutoListOfValues
                        || (assistant instanceof ListOfValues
                        && ((ListOfValues) assistant).getVisible())) {
                    formElement.setAssistant(new None());
                }
            }
        }
    }

    private void setDisabled(WebElement webElement) {
        for (FormElement formElement : IterableWebElement.get(webElement,
                FormElement.class)) {
            if (formElement.getDataSource().esRegistro()) {
                for (int i = 0; i < formElement.getRegistrosConsulta(); i++) {
                    formElement.getFieldData().setDisabled(i, true);
                }
            }
        }
    }

    private void setReadOnly(WebElement webElement) {
        for (FormElement formElement : IterableWebElement.get(webElement,
                FormElement.class)) {
            if (formElement.getDataSource().esRegistro()) {
                formElement.setModificable(Modificable.SOLO_LECTURA);
            }
        }
    }

    private WebPage getAttached(AttachedWebPage attachedWebPage) {
        String subs = attachedWebPage.getSubsystem();
        String tran = attachedWebPage.getTransaction();
        WebPage webPage = null;

        if (attachedCache.containsKey(subs + tran)) {
            try {
                webPage = WebPageXml.parseString(attachedCache.remove(subs + tran));
            } catch (ExcepcionParser ex) {
                Debug.error(ex);
            }
        } else {
            webPage = getWebPage(subs, tran, true);
        }

        if (webPage == null) {
            throw new Error("No se encontró el formulario adjunto "
                    + attachedWebPage);
        }

        if (attachedWebPage.getReadOnly()) {
            for (Container container : webPage) {
                container.setReadOnly(true);
            }
        }

        return processWebPage(webPage, attachedWebPage.getTabBase());
    }

    private void mergeReferences(Collection<Reference> references,
            Collection<Reference> references2, AttachedPosition attachedPosition) {
        references2 = new LinkedList<Reference>(references2);

        if (attachedPosition == AttachedPosition.BEFORE) {
            Collections.reverse((List<Reference>) references2);
        }

        ReferenceUtils referencesUtils = new ReferenceUtils(references);
        for (Reference newReference : references2) {
            Reference reference = referencesUtils.get(newReference.getAlias());

            if (reference != null) {
                reference.getDependencies().addAll(
                        newReference.getDependencies());
            } else {
                if (attachedPosition == AttachedPosition.BEFORE) {
                    Collections.reverse((List<Reference>) references);
                    references.add(newReference);
                    Collections.reverse((List<Reference>) references);
                } else if (attachedPosition == AttachedPosition.AFTER || attachedPosition
                        == AttachedPosition.FIXED) {
                    references.add(newReference);
                }
            }
        }
    }

    private void mergeInitialJS(WebPage base, WebPage attached, 
            final AttachedPosition attachedPosition) {
        final Collection<LiteralJS> attJS = JSParser.parse(attached.getInitialJS());

        if (attJS.isEmpty()) {
            return;
        }

        final Collection<LiteralJS> baseJS = JSParser.parse(base.getInitialJS());
        Collection<LiteralJS> attLiteralJS = CollectionUtils.select(attJS, new Predicate() {

                    @Override
                    public boolean evaluate(Object o) {
                        return !(o instanceof FuncionJS);
                    }
        });

        CollectionUtils.filter(attJS, new Predicate() {

                    @Override
                    public boolean evaluate(Object o) {
                        return o instanceof NamedJSFunction;
                    }
        });

        CollectionUtils.transform(baseJS, new Transformer() {

            @Override
            public Object transform(Object o) {
                if (!(o instanceof NamedJSFunction)) {
                    return o;
                }

                final NamedJSFunction basefn = (NamedJSFunction) o;
                Collection<LiteralJS> attNamedFns = CollectionUtils.select(attJS, new Predicate() {

                    @Override
                    public boolean evaluate(Object o2) {
                        NamedJSFunction attfn = (NamedJSFunction) o2;
                        return attfn.getName().equals(basefn.getName());
                    }
                });

                if (attNamedFns.isEmpty()) {
                    return o;
                }

                NamedJSFunction attfn =  (NamedJSFunction) attNamedFns.iterator().next();
                attJS.remove(attfn);
                String baseValue = basefn.getValor();
                String attachedValue = attfn.getValor();
                if (AttachedPosition.BEFORE == attachedPosition) {
                    basefn.setValor(attachedValue + "\n" + baseValue);
                } else {
                    basefn.setValor(baseValue + "\n" + attachedValue);
                }

                return o;
            }
        });

        CollectionUtils.addAll(baseJS, attJS.iterator());
        CollectionUtils.addAll(baseJS, attLiteralJS.iterator());

        String mergedJS = "";
        for (LiteralJS literal : baseJS) {
            mergedJS = mergedJS.concat(literal.toJS()).concat(";");
        }

        base.setInitialJS(mergedJS);
    }
}
