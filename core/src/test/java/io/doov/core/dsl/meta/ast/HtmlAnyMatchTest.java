/*
 * Copyright (C) by Courtanet, All Rights Reserved.
 */
package io.doov.core.dsl.meta.ast;

import static io.doov.core.dsl.DOOV.matchAny;
import static io.doov.core.dsl.DOOV.when;
import static io.doov.core.dsl.meta.ast.AstHtmlVisitor.astToHtml;
import static io.doov.core.dsl.meta.ast.HtmlAnyMatchTest.EnumTest.VAL1;
import static io.doov.core.dsl.meta.ast.HtmlAnyMatchTest.EnumTest.VAL2;
import static io.doov.core.dsl.meta.ast.HtmlAnyMatchTest.EnumTest.VAL3;
import static org.assertj.core.api.Assertions.assertThat;
import static org.jsoup.Jsoup.parseBodyFragment;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Locale;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Document.OutputSettings;
import org.junit.jupiter.api.*;

import io.doov.core.dsl.DOOV;
import io.doov.core.dsl.field.types.EnumFieldInfo;
import io.doov.core.dsl.lang.Result;
import io.doov.core.dsl.lang.StepCondition;
import io.doov.core.dsl.runtime.GenericModel;

public class HtmlAnyMatchTest {
    private static final Locale LOCALE = Locale.US;
    private StepCondition A;
    private Result result;
    private Document doc;
    private GenericModel model;
    private EnumFieldInfo<EnumTest> enumField;

    static Document documentOf(Result result) {
        return parseBodyFragment(astToHtml(result.getContext().getRootMetadata(), LOCALE));
    }

    static String format(Result result, Document doc) {
        return "<!-- " + AstVisitorUtils.astToString(result.getContext().getRootMetadata(), LOCALE) + " -->\n"
                + doc.outputSettings(new OutputSettings().prettyPrint(true).indentAmount(2)).toString();
    }

    @BeforeEach
    void beforeEach() {
        this.model = new GenericModel();
        this.enumField = model.enumField(VAL1, "enumField");
    }

    @Test
    void anyMatch_success() {
        result = when(enumField.anyMatch(VAL1, VAL2, VAL3)).validate().executeOn(model);
        doc = documentOf(result);

        assertTrue(result.value());
        assertThat(doc.select("ol.dsl-ol-nary")).hasSize(0);
        assertThat(doc.select("li.dsl-li-binary")).hasSize(0);
        assertThat(doc.select("li.dsl-li-nary")).hasSize(0);
        assertThat(doc.select("li.dsl-li-leaf")).hasSize(0);
        assertThat(doc.select("ul.dsl-ul-when")).hasSize(0);
        assertThat(doc.select("ul.dsl-ul-binary")).hasSize(0);
        assertThat(doc.select("ul.dsl-ul-binary-child")).hasSize(0);
        assertThat(doc.select("ul.dsl-ul-unary")).hasSize(0);
        assertThat(doc.select("div.percentage-value")).extracting(Element::text)
                .containsExactly("100 %");
        assertThat(doc.select("span.dsl-token-operator")).extracting(Element::text)
                .containsExactly("match any");
        assertThat(doc.select("span.dsl-token-value")).extracting(Element::text)
                .containsExactly(": VAL1, VAL2, VAL3");
        assertThat(doc.select("span.dsl-token-field")).extracting(Element::text)
                .containsExactly("enumField");
    }

    @Test
    void anyMatch_failure() {
        result = when(enumField.anyMatch(VAL2, VAL3)).validate().executeOn(model);
        doc = documentOf(result);

        assertFalse(result.value());
        assertThat(doc.select("ol.dsl-ol-nary")).hasSize(0);
        assertThat(doc.select("li.dsl-li-binary")).hasSize(0);
        assertThat(doc.select("li.dsl-li-nary")).hasSize(0);
        assertThat(doc.select("li.dsl-li-leaf")).hasSize(0);
        assertThat(doc.select("ul.dsl-ul-when")).hasSize(0);
        assertThat(doc.select("ul.dsl-ul-binary")).hasSize(0);
        assertThat(doc.select("ul.dsl-ul-binary-child")).hasSize(0);
        assertThat(doc.select("ul.dsl-ul-unary")).hasSize(0);
        assertThat(doc.select("div.percentage-value")).extracting(Element::text)
                .containsExactly("0 %");
        assertThat(doc.select("span.dsl-token-operator")).extracting(Element::text)
                .containsExactly("match any");
        assertThat(doc.select("span.dsl-token-value")).extracting(Element::text)
                .containsExactly(": VAL2, VAL3");
        assertThat(doc.select("span.dsl-token-field")).extracting(Element::text)
                .containsExactly("enumField");
    }

    @Test
    void and_combined_anyMatch_success() {
        A = DOOV.alwaysTrue("A");
        result = when(A.and(enumField.anyMatch(VAL1, VAL2, VAL3))).validate().executeOn(model);
        doc = documentOf(result);

        assertTrue(result.value());
        assertThat(doc.select("ol.dsl-ol-nary")).hasSize(0);
        assertThat(doc.select("li.dsl-li-binary")).hasSize(1);
        assertThat(doc.select("li.dsl-li-nary")).hasSize(0);
        assertThat(doc.select("li.dsl-li-leaf")).hasSize(0);
        assertThat(doc.select("ul.dsl-ul-when")).hasSize(0);
        assertThat(doc.select("ul.dsl-ul-binary")).hasSize(0);
        assertThat(doc.select("ul.dsl-ul-binary-child")).hasSize(0);
        assertThat(doc.select("ul.dsl-ul-unary")).hasSize(0);
        assertThat(doc.select("div.percentage-value")).extracting(Element::text)
                .containsExactly("100 %", "100 %");
        assertThat(doc.select("span.dsl-token-operator")).extracting(Element::text)
                .containsExactly("always true", "match any");
        assertThat(doc.select("span.dsl-token-value")).extracting(Element::text)
                .containsExactly("A", ": VAL1, VAL2, VAL3");
        assertThat(doc.select("span.dsl-token-binary")).extracting(Element::text)
                .containsExactly("and");
        assertThat(doc.select("span.dsl-token-field")).extracting(Element::text)
                .containsExactly("enumField");
    }

    @Test
    void and_combined_anyMatch_failure() {
        A = DOOV.alwaysTrue("A");
        result = when(A.and(enumField.anyMatch(VAL2, VAL3))).validate().executeOn(model);
        doc = documentOf(result);

        assertFalse(result.value());
        assertThat(doc.select("ol.dsl-ol-nary")).hasSize(0);
        assertThat(doc.select("li.dsl-li-binary")).hasSize(1);
        assertThat(doc.select("li.dsl-li-nary")).hasSize(0);
        assertThat(doc.select("li.dsl-li-leaf")).hasSize(0);
        assertThat(doc.select("ul.dsl-ul-when")).hasSize(0);
        assertThat(doc.select("ul.dsl-ul-binary")).hasSize(0);
        assertThat(doc.select("ul.dsl-ul-binary-child")).hasSize(0);
        assertThat(doc.select("ul.dsl-ul-unary")).hasSize(0);
        assertThat(doc.select("div.percentage-value")).extracting(Element::text)
                .containsExactly("100 %", "0 %");
        assertThat(doc.select("span.dsl-token-operator")).extracting(Element::text)
                .containsExactly("always true", "match any");
        assertThat(doc.select("span.dsl-token-value")).extracting(Element::text)
                .containsExactly("A", ": VAL2, VAL3");
        assertThat(doc.select("span.dsl-token-binary")).extracting(Element::text)
                .containsExactly("and");
        assertThat(doc.select("span.dsl-token-field")).extracting(Element::text)
                .containsExactly("enumField");
    }

    @Test
    void matchAny_combined_anyMatch_success() {
        A = DOOV.alwaysTrue("A");
        result = when(matchAny(A, enumField.anyMatch(VAL1, VAL2, VAL3))).validate().withShortCircuit(false)
                .executeOn(model);
        doc = documentOf(result);

        assertTrue(result.value());
        assertThat(doc.select("ol.dsl-ol-nary")).hasSize(1);
        assertThat(doc.select("li.dsl-li-binary")).hasSize(0);
        assertThat(doc.select("li.dsl-li-nary")).hasSize(1);
        assertThat(doc.select("li.dsl-li-leaf")).hasSize(2);
        assertThat(doc.select("ul.dsl-ul-when")).hasSize(0);
        assertThat(doc.select("ul.dsl-ul-binary")).hasSize(0);
        assertThat(doc.select("ul.dsl-ul-binary-child")).hasSize(0);
        assertThat(doc.select("ul.dsl-ul-unary")).hasSize(0);
        assertThat(doc.select("div.percentage-value")).extracting(Element::text)
                .containsExactly("100 %", "100 %", "100 %");
        assertThat(doc.select("span.dsl-token-operator")).extracting(Element::text)
                .containsExactly("always true", "match any");
        assertThat(doc.select("span.dsl-token-value")).extracting(Element::text)
                .containsExactly("A", ": VAL1, VAL2, VAL3");
        assertThat(doc.select("span.dsl-token-nary")).extracting(Element::text)
                .containsExactly("match any");
        assertThat(doc.select("span.dsl-token-field")).extracting(Element::text)
                .containsExactly("enumField");
    }

    @Test
    void matchAny_combined_anyMatch_failure() {
        A = DOOV.alwaysFalse("A");
        result = when(matchAny(A, enumField.anyMatch(VAL2, VAL3))).validate().executeOn(model);
        doc = documentOf(result);

        assertFalse(result.value());
        assertThat(doc.select("ol.dsl-ol-nary")).hasSize(1);
        assertThat(doc.select("li.dsl-li-binary")).hasSize(0);
        assertThat(doc.select("li.dsl-li-nary")).hasSize(1);
        assertThat(doc.select("li.dsl-li-leaf")).hasSize(2);
        assertThat(doc.select("ul.dsl-ul-when")).hasSize(0);
        assertThat(doc.select("ul.dsl-ul-binary")).hasSize(0);
        assertThat(doc.select("ul.dsl-ul-binary-child")).hasSize(0);
        assertThat(doc.select("ul.dsl-ul-unary")).hasSize(0);
        assertThat(doc.select("div.percentage-value")).extracting(Element::text)
                .containsExactly("0 %", "0 %", "0 %");
        assertThat(doc.select("span.dsl-token-operator")).extracting(Element::text)
                .containsExactly("always false", "match any");
        assertThat(doc.select("span.dsl-token-value")).extracting(Element::text)
                .containsExactly("A", ": VAL2, VAL3");
        assertThat(doc.select("span.dsl-token-nary")).extracting(Element::text)
                .containsExactly("match any");
        assertThat(doc.select("span.dsl-token-field")).extracting(Element::text)
                .containsExactly("enumField");
    }

    @AfterEach
    void afterEach() {
        System.out.println(format(result, doc));
    }

    enum EnumTest {
        VAL1, VAL2, VAL3;
    }
}
