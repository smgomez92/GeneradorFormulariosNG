package com.fitbank.webpages.formulas;

import java.util.Collection;
import java.util.LinkedList;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.parboiled.BaseParser;
import org.parboiled.Context;
import org.parboiled.Node;
import org.parboiled.Parboiled;
import org.parboiled.ReportingParseRunner;
import org.parboiled.Rule;
import org.parboiled.annotations.SkipNode;
import org.parboiled.annotations.SuppressNode;
import org.parboiled.annotations.SuppressSubnodes;
import org.parboiled.support.ParseTreeUtils;
import org.parboiled.support.ParsingResult;

import com.fitbank.util.Debug;
import com.fitbank.webpages.data.FormElement;
import java.util.LinkedHashSet;

/**
 * Clase para parsear formulas. El proceso de parseo es así:
 *
 * <ol>
 *   <li>Se quita el primer caracter ('=') de la expresión a parsear
 *    usando input.substring(1). Ejemplo: input = "=SUM(....)", entonces
 *    se hace que input = "SUM(...)" para que se pueda parsear correctamente.</li>
 *   <li>Se pasa un Expression para que se evalúe la cadena input como una
 *    expresión en el método parse.</li>
 *   <li>Las reglas aquí escritas definen una jerarquía, de tal manera que todo
 *    es en última instancia una expresión. La regla Expression contiene
 *    subnodos que representan las reglas aplicadas recursivamente a cada
 *    fragmento de la cadena input.</li>
 * </ol>
 *
 * @author FitBank CI
 */
public class FormulaParser extends BaseParser<Object> {

    protected final static String PREV = "function (record) { var _ = Formulas.resolve.curry(record); var __ = { record: record, resolve: _ }; return _(";

    protected final static String POST = "); }";

    private final Collection<String> elements = new LinkedHashSet<String>();

    private final Collection<String> strings = new LinkedHashSet<String>();

    public static Formula parse(FormElement formElement) {
        return parse(formElement.getNameOrDefault(), formElement.getRelleno());
    }

    public static Formula parse(String name, String input) {
        FormulaParser parser = Parboiled.createParser(FormulaParser.class);

        ParsingResult<?> result = ReportingParseRunner.run(parser.Expression(),
                input.substring(1));

        Debug.debug(ParseTreeUtils.printNodeTree(result));

        boolean evaluable = result.parseTreeRoot.getChildren().size() == 1;

        if (evaluable) {
            evaluable = result.parseTreeRoot.getChildren().get(0).getLabel().equals("ContextReference");
        }

        StringBuilder js = new StringBuilder();

        if (name != null) {
            js.append(String.format("Formulas.execute('%s', ", name));
        }
        js.append(PREV);
        js.append(result.parseTreeRoot.getValue());
        js.append(POST);
        if (name != null) {
            js.append(");");
        }

        Formula formula = new Formula(name, js.toString(), parser.elements, parser.strings);

        return formula;
    }

    protected final JSAction eval = new JSAction() {

        @Override
        public String toJS(Context context) {
            Collection<String> children = new LinkedList<String>();

            Predicate p = new Predicate() {

                public boolean evaluate(Object object) {
                    Node n = (Node) object;
                    return n.getLabel().equals("ExpressionOperation") || n.getLabel().equals("Number");
                }

            };

            boolean resolve = CollectionUtils.exists(context.getSubNodes(), p);

            for (Object o : context.getSubNodes()) {
                String val = String.valueOf(((Node) o).getValue());
                if (!resolve || p.evaluate(o)) {
                    children.add(val);
                } else {
                    children.add("_(" + val + ")");
                }
            }

            if (children.size() > 1) {
                return StringUtils.join(children, " ");
            } else if (children.size() == 1) {
                return children.iterator().next();
            } else {
                return "";
            }
        }

    };

    // Expression <- Factor (ExpressionOperation Factor)*
    protected Rule Expression() {
        return Sequence(
                WhiteSpace(),
                Factor(),
                ZeroOrMore(
                    Sequence(
                        WhiteSpace(),
                        ExpressionOperation(),
                        WhiteSpace(),
                        Factor()
                    ).skipNode()
                ).skipNode(),
                eval
            );
    }

    // Factor <- Function | ContextReference | ElementReference | Number |
    //           StringLiteral | Parenthesis
    @SkipNode
    protected Rule Factor() {
        return FirstOf(
                Function(),
                Number(),
                ContextReference(),
                ElementReference(),
                StringLiteral(),
                Parenthesis()
            );
    }

    // Parenthesis <- '(' Expression ')'
    protected Rule Parenthesis() {
        return Sequence(
                Ch('(').skipNode(),
                Expression(),
                Ch(')').skipNode(),
                new JSAction() {

                    @Override
                    public String toJS(Context context) {
                        return String.valueOf(context.getLastNode().getValue());
                    }

                }
            );
    }

    // ExpressionOperation <- ('+'|'-')
    @SuppressSubnodes
    public Rule ExpressionOperation() {
        return Sequence(
                FirstOf(
                    '+',
                    '-',
                    '*',
                    '/',
                    "==",
                    "!=",
                    "<=",
                    ">=",
                    '<',
                    '>',
                    '%',
                    "&&",
                    "||",
                    '^'
                ),
                new JSAction() {

                    @Override
                    public String toJS(Context context) {
                        return context.getPrevText();
                    }

                }
            );
    }

    // Function <- FunctionName '(' FunctionParameters* ')'
    protected Rule Function() {
        return Sequence(
                FunctionName(),
                WhiteSpace(),
                Ch('(').skipNode(),
                WhiteSpace(),
                Optional(FunctionParameters()).skipNode(),
                new JSAction() {

                    @Override
                    public String toJS(Context context) {
                        Collection<String> children = new LinkedList<String>();
                        String functionName = null;

                        children.add("__");
                        for (Object o : context.getSubNodes()) {
                            Node subNode = (Node) o;
                            if (subNode.getLabel().equals("FunctionName")) {
                                functionName = context.getNodeText(subNode);
                            } else {
                                children.add(String.valueOf(subNode.getValue()));
                            }
                        }

                        return String.format("Formulas['%s'].call(%s)",
                                functionName, StringUtils.join(children, ", "));
                    }

                },
                WhiteSpace(),
                Ch(')').skipNode()
            );
    }

    // FunctionName <- [A-Z]+
    @SuppressSubnodes
    protected Rule FunctionName() {
        return OneOrMore(CharRange('A', 'Z'));
    }

    // FunctionParameters <- Expression (';' Expression)*
    @SkipNode
    protected Rule FunctionParameters() {
        return Sequence(
                Expression(),
                ZeroOrMore(
                    Sequence(
                        FirstOf(';', ',').suppressNode(),
                        Expression()
                    ).skipNode()
                ).skipNode()
            );
    }

    // Number <- [0-9]+
    @SuppressSubnodes
    protected Rule Number() {
        return Sequence(
                FirstOf(
                    Sequence(
                        OneOrMore(CharRange('0', '9')),
                        Ch('.'),
                        OneOrMore(CharRange('0', '9'))
                    ).skipNode(),
                    OneOrMore(CharRange('0', '9'))
                ).skipNode(),
                new JSAction() {

                    @Override
                    public String toJS(Context context) {
                        return context.getPrevText();
                    }

                }
            );
    }

    // ElementReference <- [LetterOrDigit]+
    @SuppressSubnodes
    protected Rule ElementReference() {
        return Sequence(
                OneOrMore(LetterOrDigit()),
                new JSAction() {

                    @Override
                    public String toJS(Context context) {
                        String name = context.getPrevText();

                        elements.add(name);

                        return String.format("c.$N('%s')", name);
                    }

                }
            );
    }

    // ContextReference <- '$'[LetterOrDigit]+
    @SuppressSubnodes
    protected Rule ContextReference() {
        return Sequence(
                '$',
                OneOrMore(LetterOrDigit()),
                new JSAction() {

                    @Override
                    public String toJS(Context context) {
                        String evalTerm = context.getPrevText();

                        if (evalTerm.equals("record")) {
                            return "record";
                        } else {
                            return "c." + evalTerm;
                        }
                    }

                }
            );
    }

    // LetterOrDigit <- [a-zA-Z0-9_]+
    public Rule LetterOrDigit() {
        return FirstOf(CharRange('a', 'z'), CharRange('A', 'Z'), CharRange('0',
                '9'), '_');
    }

    // WhiteSpace <- [ \t\f]+
    @SuppressNode
    public Rule WhiteSpace() {
        return ZeroOrMore(CharSet(" \t\f"));
    }

    // StringLiteral: <- QuotedStringLiteral(") | QuotedStringLiteral(')
    @SkipNode
    public Rule StringLiteral(){
        return FirstOf(QuotedStringLiteral('"'), QuotedStringLiteral('\''));
    }

    // QuotedStringLiteral => quote (StringEscape | [^quote] Any )* quote
    public Rule QuotedStringLiteral(char quote) {
        return Sequence(
                Ch(quote).skipNode(),
                ZeroOrMore(FirstOf(
                    StringEscape(),
                    Sequence(TestNot(quote), Any()))
                ).suppressSubnodes().label("String"),
                Ch(quote).skipNode(),
                new JSAction() {

                    @Override
                    public String toJS(Context context) {
                        String string = context.getNodeText((Node) context.getSubNodes().get(0));
                        strings.add(string);
                        return "'" + StringEscapeUtils.escapeJavaScript(StringEscapeUtils.unescapeJavaScript(string)) + "'";
                    }

                }
            );
    }

    // StringEscape => '\'['"]
    public Rule StringEscape(){
        return Sequence('\\', CharSet("\"'"));
    }

}
