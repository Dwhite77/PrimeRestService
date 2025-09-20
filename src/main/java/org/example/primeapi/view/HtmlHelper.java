package org.example.primeapi.view;

import org.example.primeapi.model.PrimePayload;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class HtmlHelper {



    public static List<String> getMarkdownFiles() {
        try {
            var uri = HtmlHelper.class.getClassLoader().getResource("docs");
            if (uri == null) return List.of();

            File docsDir = new File(uri.toURI());
            File[] files = docsDir.listFiles((dir, name) -> name.endsWith(".md"));
            if (files == null) return List.of();

            return Arrays.stream(files)
                    .sorted(Comparator.comparing(File::getName))
                    .map(File::getName)
                    .toList();

        } catch (Exception e) {
            return List.of(); // fallback if running from inside a JAR
        }
    }

    public static List<String> getMarkdownFilenames() {
        try {
            var uri = HtmlHelper.class.getClassLoader().getResource("docs");
            if (uri == null) return List.of();

            File docsDir = new File(uri.toURI());
            File[] files = docsDir.listFiles((dir, name) -> name.endsWith(".md"));
            if (files == null) return List.of();

            return Arrays.stream(files)
                    .sorted(Comparator.comparing(File::getName))
                    .map(File::getName)
                    .toList();

        } catch (Exception e) {
            return List.of(); // fallback if running from inside a JAR
        }
    }

    public static String buildSidebar(List<String> filenames) {
        StringBuilder sidebar = new StringBuilder("<div class=\"sidebar\"><h3>📄 Docs</h3><ul>");
        for (String name : filenames) {
            sidebar.append(String.format("<li><a href=\"/docs/view/%s\">%s</a></li>", name, name));
        }
        sidebar.append("</ul></div>");
        return sidebar.toString();
    }

    public static String buildIndexContent(List<String> filenames) {
        StringBuilder content = new StringBuilder("<h1>📚 Documentation Index</h1><ul>");
        for (String name : filenames) {
            content.append(String.format("<li><a href=\"/docs/view/%s\">%s</a></li>", name, name));
        }
        content.append("</ul>");
        return content.toString();
    }

    public static String wrapHtml(String sidebar, String contentHtml, boolean includeBacklinks) {
        String backlinks = includeBacklinks ? """
        <div class="backlink">
            <p><a href="/docs/view/prime-algorithms.md">← Back to Prime Algorithms Overview</a></p>
            <p><a href="/docs">← Back to Documentation Index</a></p>
        </div>
    """ : "";

        return """
        <html>
        <head>
            <title>Documentation Viewer</title>
            <style>
                body {
                    margin: 0;
                    font-family: sans-serif;
                    display: flex;
                }
                .sidebar {
                    width: 220px;
                    background-color: #f4f4f4;
                    padding: 1rem;
                    border-right: 1px solid #ddd;
                }
                .sidebar h3 {
                    margin-top: 0;
                }
                .sidebar ul {
                    list-style: none;
                    padding-left: 0;
                }
                .sidebar li {
                    margin: 0.5rem 0;
                }
                .sidebar a {
                    color: #007acc;
                    text-decoration: none;
                }
                .sidebar a:hover {
                    text-decoration: underline;
                }
                .content {
                    flex: 1;
                    padding: 2rem;
                    max-width: 800px;
                }
                h1, h2, h3 {
                    color: #333;
                }
                a {
                    color: #007acc;
                    text-decoration: none;
                }
                a:hover {
                    text-decoration: underline;
                }
                .backlink {
                    margin-top: 3rem;
                    font-size: 0.9rem;
                }
            </style>
        </head>
        <body>
        """ + sidebar + """
        <div class="content">
        """ + contentHtml + backlinks + """
        </div>
        </body>
        </html>
    """;
    }

    public static boolean isValidMarkdownFile(String filename) {
        return HtmlHelper.class.getClassLoader().getResource("docs/" + filename) != null;
    }

    public static String readMarkdown(String filename) throws IOException {
        try (var in = HtmlHelper.class.getClassLoader().getResourceAsStream("docs/" + filename)) {
            if (in == null) throw new IOException("Markdown file not found: " + filename);
            return new String(in.readAllBytes(), java.nio.charset.StandardCharsets.UTF_8);
        }
    }
    public static String convertMarkdownToHtml(String filename, String markdown) {
        String html = MarkdownConverter.toHtml(markdown);
        if (filename.equals("prime-algorithms.md")) {
            html += buildAlgorithmTable();
            html += buildRecentRequestTable();
        }
        return html;
    }


    public static String buildAlgorithmTable() {
        return """
        <h2>📊 Algorithm Comparison</h2>
        <table>
          <thead>
            <tr>
              <th>Algorithm</th>
              <th>Time Complexity</th>
              <th>Use Case Notes</th>
            </tr>
          </thead>
          <tbody>
            <tr>
              <td>Trial Division</td>
              <td>O(√n)</td>
              <td>Simple, slow; good for small inputs or teaching</td>
            </tr>
            <tr>
              <td>Sieve of Eratosthenes</td>
              <td>O(n log log n)</td>
              <td>Fast for generating all primes up to n</td>
            </tr>
            <tr>
              <td>Miller-Rabin</td>
              <td>O(k log³ n)</td>
              <td>Probabilistic; efficient for testing if one number is prime</td>
            </tr>
            <tr>
              <td>Sieve of Atkin</td>
              <td>O(n) (theoretical)</td>
              <td>Complex; faster than Eratosthenes for large n</td>
            </tr>
          </tbody>
        </table>
    """;
    }

    public static String buildRecentRequestTable() {
        List<PrimePayload> recent = PrimeRequestLog.getRecent();
        if (recent.isEmpty()) return "<p>No recent /api/primes requests logged.</p>";

        StringBuilder table = new StringBuilder("""
        <h2>🧮 Recent Prime Requests</h2>
        <table>
            <thead>
                <tr>
                    <th>Algorithm</th>
                    <th>Limit</th>
                    <th>Threads</th>
                    <th>Total</th>
                    <th>Duration (ms)</th>
                </tr>
            </thead>
            <tbody>
    """);

        for (PrimePayload p : recent) {
            table.append(String.format("""
        <tr>
            <td>%s</td>
            <td>%d</td>
            <td>%d</td>
            <td>%d</td>
            <td>%d</td>
        </tr>
        """, p.getAlgorithm(), p.getLimit(), p.getThreads(), p.getTotal(), p.getDurationMs()));
        }

        table.append("</tbody></table>");
        return table.toString();
    }



}
