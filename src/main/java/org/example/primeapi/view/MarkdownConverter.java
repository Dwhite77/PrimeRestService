package org.example.primeapi.view;

import org.commonmark.ext.gfm.tables.TablesExtension;
import org.commonmark.Extension;
import org.commonmark.node.*;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

import java.util.List;

public class MarkdownConverter {
    private static final List<Extension> extensions = List.of(TablesExtension.create());
    private static final Parser parser = Parser.builder().build();
    private static final HtmlRenderer renderer = HtmlRenderer.builder().build();

    public static String toHtml(String markdown) {
        Node document = parser.parse(markdown);
        return renderer.render(document);
    }
}
