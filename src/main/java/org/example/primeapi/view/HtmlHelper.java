package org.example.primeapi.view;

import org.example.primeapi.algo.Algorithms.AtkinAlgorithm;
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
            var uri = HtmlHelper.class.getClassLoader().getResource("docs").toURI();
            if (uri == null) return List.of();

            if (uri.getScheme().equals("jar")) {
                // Running from JAR (e.g. Render)
                var jarPath = HtmlHelper.class.getProtectionDomain().getCodeSource().getLocation().toURI();
                try (var jarFile = new java.util.jar.JarFile(new File(jarPath))) {
                    return jarFile.stream()
                            .map(java.util.jar.JarEntry::getName)
                            .filter(name -> name.startsWith("docs/") && name.endsWith(".md"))
                            .map(name -> name.substring("docs/".length()))
                            .sorted()
                            .toList();
                }
            } else {
                // Running locally from filesystem
                File docsDir = new File(uri);
                File[] files = docsDir.listFiles((dir, name) -> name.endsWith(".md"));
                if (files == null) return List.of();

                return Arrays.stream(files)
                        .sorted(Comparator.comparing(File::getName))
                        .map(File::getName)
                        .toList();
            }
        } catch (Exception e) {
            return List.of(); // fallback
        }
    }


    public static String buildSidebar(List<String> filenames) {
        StringBuilder sidebar = new StringBuilder();

        sidebar.append("<div class=\"sidebar\">");
        sidebar.append("<h2> Helpful Info </h2>");

        sidebar.append("<div class=\"sidebar-section\">");
        sidebar.append("<h3>📄 Docs</h3><ul>");

        sidebar.append(buildDocAppend("README.md", "README"));
        sidebar.append("<li><a href='/swagger-ui.html' target='_blank'>Swagger UI</a></li>");

        sidebar.append("<h3>Algorithm Info</h3><ul>");
        sidebar.append(buildDocAppend("Prime-Algorithms.md", "Prime Algorithms Overview"));
        sidebar.append(buildDocAppend("Trial.md", "Trial Algorithm"));
        sidebar.append(buildDocAppend("Sieve.md", "Sieve Algorithm"));
        sidebar.append(buildDocAppend("Atkin.md", "Atkin Algorithm"));
        sidebar.append(buildDocAppend("Miller.md", "Miller Algorithm"));

        sidebar.append("<h3>Testing and Performance Info</h3><ul>");
        sidebar.append(buildDocAppend("Testing.md", "Testing"));
        sidebar.append("<li><a href='/jacoco/index.html' target='_blank'>JaCoCo Coverage Report</a></li>");
        sidebar.append(buildDocAppend("Error-Handling.md", "Error Handling"));
        sidebar.append(buildDocAppend("Performance.md", "Performance"));

        sidebar.append("<h3>Other Info</h3><ul>");
        sidebar.append(buildDocAppend("RestApis.md", "RestApis"));
        sidebar.append(buildDocAppend("Technologies-Used.md", "Technologies Used"));



        sidebar.append("</ul></div>");

        // 🧮 Recent requests section
        sidebar.append("<div class=\"sidebar-section recent-requests\" id=\"recentRequests\">");
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

    public static String buildDocAppend(String docPath, String docName){
        return "<li><a href='/docs/view/"+docPath+"'>"+docName+"</a></li>";

    }

    public static String buildRequestForm() {
        return """
<div class="request-form">
    <form id="primeRequestForm" onsubmit="submitPrimeRequest(event)">
        <div class="form-box">
            <label for="algorithm">Algorithm:</label>
            <select id="algorithm" name="algorithm">
                <option value="trial">Trial</option>
                <option value="sieve">Sieve</option>
                <option value="miller">Miller</option>
                <option value="atkin">Atkin</option>
            </select>
        </div>

        <div class="form-box">
            <label for="limit">Limit:</label>
            <input type="number" id="limit" name="limit" value="1000000" required>
        </div>

        <div class="form-box">
            <label for="threads">Threads:</label>
            <input type="number" id="threads" name="threads" value="4" required>
        </div>

        <div class="form-box">
          <label class="checkbox-label">
            <input type="checkbox" id="useCache" name="useCache" checked>
            Use Cache
          </label>
        </div>

        <div class="form-box">
          <label class="checkbox-label">
            <input type="checkbox" id="download" name="download">
            Download response
          </label>
        </div>

        <div class="form-box">
            <label for="format">Format:</label>
            <select id="format" name="format">
                <option value="application/json">JSON</option>
                <option value="application/xml">XML</option>
            </select>
        </div>

        <button type="submit">Run</button>
    </form>
</div>
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
                                    background-color: #f9fafb;
                                    color: #333;
                                    font-family: 'Inter', 'Segoe UI', 'Roboto', sans-serif;
                                  }
            
                                  h1, h2, h3 {
                                    color: #333;
                                  }
                                  h3 {
                                    margin-top: 1.5rem;
                                  }
            
                                  a {
                                    color: #007acc;
                                    text-decoration: none;
                                  }
            
                                  a:hover {
                                    text-decoration: underline;
                                  }
            
                                  .sidebar {
                                    width: 400px;
                                    max-width: 400px;
                                    height: 100vh;
                                    padding: 1rem;
                                    background-color: #ffffff;
                                    border-right: 1px solid #e0e0e0;
                                    box-sizing: border-box;
                                    position: fixed;
                                    top: 0;
                                    left: 0;
                                    overflow-y: auto;
                                    border-radius: 8px;
                                    box-shadow: 0 2px 6px rgba(0,0,0,0.1);
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
            
                                  .sidebar-section h3 {
                                    position: static;
                                    background-color: #fff;
                                    padding: 0.5rem 0;
                                  }
            
                                  @media (max-width: 768px) {
                                    .sidebar {
                                      transform: translateX(-100%);
                                      transition: transform 0.3s ease;
                                    }
            
                                    .sidebar.open {
                                      transform: translateX(0);
                                    }
            
                                    .sidebar-toggle {
                                      position: fixed;
                                      top: 1rem;
                                      left: 1rem;
                                      z-index: 1001;
                                      background: #007acc;
                                      color: white;
                                      padding: 0.5rem;
                                      border-radius: 4px;
                                      cursor: pointer;
                                    }
                                  }
            
                                  .content {
                                    margin-left: 400px;
                                    padding: 2rem;
                                    max-width: 1400px;
                                    box-sizing: border-box;
                                  }
            
                                  .backlink {
                                    margin-top: 3rem;
                                    font-size: 0.9rem;
                                  }
            
                                  .recent-requests,
                                  .request-form {
                                    margin-top: 2rem;
                                    font-size: 0.85em;
                                    border-radius: 8px;
                                    box-shadow: 0 2px 6px rgba(0,0,0,0.1);
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
            
                                  .request-form button:active {
                                    transform: scale(0.98);
                                    box-shadow: inset 0 2px 4px rgba(0,0,0,0.2);
                                  }
            
                                  .form-box {
                                    width: 100%;
                                    display: flex;
                                    flex-direction: column;
                                    margin-bottom: 1rem;
                                  }
            
                                  .form-box label {
                                    margin-bottom: 0.3rem;
                                    font-weight: 500;
                                  }
            
                                  .form-box input,
                                  .form-box select {
                                    padding: 6px;
                                    border: 1px solid #ccc;
                                    border-radius: 4px;
                                    box-sizing: border-box;
                                  }
            
                                  .checkbox-label {
                                    display: block;
                                    padding-left: 15px;
                                    text-indent: -15px;
                                  }
            
                                  .checkbox-label input {
                                    width: 13px;
                                    height: 13px;
                                    padding: 0;
                                    margin: 0;
                                    vertical-align: bottom;
                                    position: relative;
                                    top: -1px;
                                    *overflow: hidden;
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
                                   const format = document.getElementById("format").value;
                                   const download = document.getElementById("download").checked;
            
                                   if (isNaN(limit) || limit < 1) {
                                     alert("Limit must be a positive integer.");
                                     return;
                                   }
            
                                   if (isNaN(threads) || threads < 1 || threads > 128) {
                                     alert("Threads must be between 1 and 128.");
                                     return;
                                   }
            
                                   const url = `/api/primes?algorithm=${algo}&limit=${limit}&threads=${threads}&useCache=${useCache}`;
            
                                   fetch(url, { headers: { "Accept": format } })
                                     .then(res => download ? res.blob() : res.text())
                                     .then(data => {
                                       if (download) {
                                         const ext = format.includes("json") ? "json" : "xml";
                                         const fileUrl = URL.createObjectURL(data);
                                         const a = document.createElement("a");
                                         a.href = fileUrl;
                                         a.download = `primes.${ext}`;
                                         a.click();
                                         URL.revokeObjectURL(fileUrl);
            
                                         // Also open in new tab with parsed content
                                         const reader = new FileReader();
                                         reader.onload = function () {
                                           let formatted = reader.result;
            
                                             if (format.includes("json")) {
                                               try {
                                                 const parsed = JSON.parse(reader.result);
                                                 formatted = JSON.stringify(parsed, null, 2); // Pretty-print JSON
                                               } catch (e) {
                                                 console.warn("Failed to parse JSON:", e);
                                               }
                                             }

                                             const blob = new Blob([formatted], { type: format });
                                             const tabUrl = URL.createObjectURL(blob);
                                             window.open(tabUrl, "_blank");
                                         };
                                         reader.readAsText(data);
                                       } else {
                                         refreshRecentRequests();
            
                                         // Check if response is JSON and pretty-print it
                                         let formatted = data;
                                         if (format.includes("json")) {
                                           try {
                                             const parsed = JSON.parse(data);
                                             formatted = JSON.stringify(parsed, null, 2); // Pretty-print with 2-space indentation
                                           } catch (e) {
                                             console.warn("Failed to parse JSON:", e);
                                           }
                                         }
            
                                         const blob = new Blob([formatted], { type: format });
                                         const tabUrl = URL.createObjectURL(blob);
                                         window.open(tabUrl, "_blank");
                                       }
                                     });
                                 }
            
                                 function escapeHtml(str) {
                                   return str.replace(/[&<>"']/g, m => ({
                                     '&': '&amp;',
                                     '<': '&lt;',
                                     '>': '&gt;',
                                     '"': '&quot;',
                                     "'": '&#39;'
                                   })[m]);
                                 }
            
            function refreshRecentRequests() {
                fetch("/docs/recent-requests-html")
                    .then(res => res.text())
                    .then(html => {
                        document.getElementById("recentRequests").innerHTML = html;
                    });
            }
            
            function escapeHtml(str) {
                return str.replace(/&/g, "&amp;")
                          .replace(/</g, "&lt;")
                          .replace(/>/g, "&gt;");
            }
            
            </script>
            """;


}
