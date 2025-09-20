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
        StringBuilder sidebar = new StringBuilder();

        sidebar.append("<div class=\"sidebar\">");

        // 📄 Docs section
        sidebar.append("<div class=\"sidebar-section\">");
        sidebar.append("<h3>📄 Docs</h3><ul>");
        for (String name : filenames) {
            sidebar.append(String.format("<li><a href=\"/docs/view/%s\">%s</a></li>", name, name));
        }
        sidebar.append("</ul></div>");

        // 🧮 Recent requests section
        sidebar.append("<div class=\"sidebar-section recent-requests\">");
        sidebar.append("<h3>🧮 Recent Requests</h3>");
        sidebar.append(buildRecentRequestTable());
        sidebar.append("</div>");

        // ⚙️ Prime request form section
        sidebar.append("<div class=\"sidebar-section request-form\">");
        sidebar.append("<h3>⚙️ Run Prime Request</h3>");
        sidebar.append(buildRequestForm());
        sidebar.append("</div>");

        sidebar.append("</div>");
        return sidebar.toString();
    }

    public static String buildRequestForm() {
        return """
    <form id="primeRequestForm" onsubmit="submitPrimeRequest(event)">
        <label for="algorithm">Algorithm:</label>
        <select id="algorithm" name="algorithm">
            <option value="trial">Trial</option>
            <option value="sieve">Sieve</option>
            <option value="miller">Miller</option>
            <option value="atkin">Atkin</option>
        </select>

        <label for="limit">Limit:</label>
        <input type="number" id="limit" name="limit" min="1" required>

        <label for="threads">Threads:</label>
        <input type="number" id="threads" name="threads" min="1" max="128" required>

        <label for="useCache">
            <input type="checkbox" id="useCache" name="useCache" checked>
            Use Cache
        </label>

        <button type="submit">Run</button>
    </form>
    <div id="requestResult" style="margin-top: 1rem; font-size: 0.9em;"></div>
    """;
    }

    public static String buildIndexContent(List<String> filenames) {
        List<String> algorithmDocs = List.of(
                "Trial.md", "Sieve.md", "Atkin.md", "Miller.md"
        );

        List<String> infoDocs = List.of(
                 "Error-Handling.md", "Testing.md", "RestApis.md", "Performance.md"
        );

        StringBuilder content = new StringBuilder("<h1>📚 Documentation Index</h1>");

        content.append("<h2><a href=\"/docs/view/Prime-Algorithms.md\">🧮 Algorithms</a></h2><ul>");
        for (String name : filenames) {
            if (algorithmDocs.contains(name)) {
                content.append(String.format("<li><a href=\"/docs/view/%s\">%s</a></li>", name, name));
            }
        }
        content.append("</ul>");

        content.append("<h2>📘 Informational</h2><ul>");
        for (String name : filenames) {
            if (infoDocs.contains(name)) {
                content.append(String.format("<li><a href=\"/docs/view/%s\">%s</a></li>", name, name));
            }
        }
        content.append("</ul>");

        content.append("<h2>📦 Miscellaneous</h2><ul>");
        for (String name : filenames) {
            if (!algorithmDocs.contains(name) && !infoDocs.contains(name)) {
                content.append(String.format("<li><a href=\"/docs/view/%s\">%s</a></li>", name, name));
            }
        }
        content.append("</ul>");

        return content.toString();
    }

    public static String wrapHtml(String sidebar, String contentHtml, boolean includeBacklinks) {
        String backlinks = includeBacklinks ? """
        <div class="backlink">
            <p><a href="/docs/view/Prime-Algorithms.md">← Back to Prime Algorithms Overview</a></p>
            <p><a href="/docs">← Back to Documentation Index</a></p>
        </div>
    """ : "";

        return """
        <html>
        <head>
            <title>Documentation Viewer</title>
            """+styleBlock+"""
        </head>
        """+jsBlock+"""
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
        if (filename.equals("Prime-Algorithms.md")) {
            html += buildAlgorithmTable();
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



    private static String styleBlock = """
                <style>
                body {
                    margin: 0;
                    font-family: sans-serif;
                    display: flex;
                }
                .sidebar {
                    width: 220px;
                    max-width: 220px;
                    background-color: #f4f4f4;
                    padding: 1rem;
                    border-right: 1px solid #ddd;
                    overflow-x: hidden;
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
                .recent-requests {
                            margin-top: 2rem;
                            font-size: 0.85em;
                        }
                
                        .recent-requests table {
                                        width: 100%;
                                        table-layout: fixed;
                                        word-wrap: break-word;
                                        font-size: 0.8em;
                                    }
                
                        .recent-requests th,
                                     .recent-requests td {
                                         padding: 4px;
                                         text-align: left;
                                         overflow: hidden;
                                         text-overflow: ellipsis;
                                         white-space: nowrap;
                                     }
                
                        .recent-requests tr:hover {
                            background-color: #f5f5f5;
                        }
                        .request-form {
                                    margin-top: 2rem;
                                    font-size: 0.85em;
                                }
                
                                .request-form label {
                                    display: block;
                                    margin-top: 0.5rem;
                                }
                
                                .request-form input,
                                .request-form select {
                                    width: 100%;
                                    padding: 4px;
                                    margin-top: 0.2rem;
                                    box-sizing: border-box;
                                }
                
                                .request-form button {
                                    margin-top: 0.8rem;
                                    padding: 6px 12px;
                                    background-color: #007acc;
                                    color: white;
                                    border: none;
                                    cursor: pointer;
                                }
                
                                .request-form button:hover {
                                    background-color: #005fa3;
                                }
            </style>
""";

    private static String jsBlock = """
            <script>
            function submitPrimeRequest(event) {
                event.preventDefault();
            
                const algo = document.getElementById("algorithm").value;
                const limit = parseInt(document.getElementById("limit").value, 10);
                const threads = parseInt(document.getElementById("threads").value, 10);
                const useCache = document.getElementById("useCache").checked;
            
                if (isNaN(limit) || limit < 1) {
                    alert("Limit must be a positive integer.");
                    return;
                }
            
                if (isNaN(threads) || threads < 1 || threads > 128) {
                    alert("Threads must be between 1 and 128.");
                    return;
                }
            
                const url = `/api/primes?algorithm=${algo}&limit=${limit}&threads=${threads}&useCache=${useCache}`;
                fetch(url)
                    .then(res => res.json())
                    .then(data => {
                        const resultDiv = document.getElementById("requestResult");
                        resultDiv.innerHTML = `<strong>Result:</strong> ${data.total} primes in ${data.durationMs}ms`;
                    })
                    .catch(err => {
                        document.getElementById("requestResult").innerHTML = `<span style="color:red;">Error: ${err}</span>`;
                    });
            }
            </script>
            """;


}
