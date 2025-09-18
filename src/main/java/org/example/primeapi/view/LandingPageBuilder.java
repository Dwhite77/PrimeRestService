package org.example.primeapi.view;

public class LandingPageBuilder {

    public static String getHtml() {
        return """
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8">
                <title>Prime API Landing Page</title>
                <style>
                    body { font-family: Arial, sans-serif; margin: 2rem; background-color: #f9f9f9; }
                    h1 { color: #2c3e50; }
                    code { background-color: #eef; padding: 2px 6px; border-radius: 4px; }
                    a { color: #2980b9; text-decoration: none; }
                    a:hover { text-decoration: underline; }
                    .section { margin-bottom: 1.5rem; }
                </style>
            </head>
            <body>
                <h1>🚀 Welcome to the Prime API</h1>

                <div class="section">
                    <p>This API returns prime numbers using selectable algorithms:</p>
                    <ul>
                        <li><code>trial</code> – simplest but slowest</li>
                        <li><code>sieve</code> – efficient for mid-range inputs</li>
                        <li><code>atkin</code> – optimized for large ranges</li>
                        <li><code>miller</code> – probabilistic primality testing</li>
                    </ul>
                </div>

                <div class="section">
                    <h2>📘 Usage</h2>
                    <p>Send a GET request to:</p>
                    <pre><code>/api/primes?limit=1000&algorithm=sieve&threads=4</code></pre>
                    <p>Parameters:</p>
                    <ul>
                        <li><code>limit</code>: upper bound for prime generation (≥ 0)</li>
                        <li><code>algorithm</code>: one of <code>trial</code>, <code>sieve</code>, <code>atkin</code>, <code>miller</code></li>
                        <li><code>threads</code>: number of threads to use (≥ 1)</li>
                    </ul>
                </div>

                <div class="section">
                    <h2>📄 Documentation</h2>
                    <p>See the docs pages for more info on the algorithms and error handling:</p>
                    <p><a href="/docs" target="_blank">Documentation</a></p>
                </div>

                <div class="section">
                    <h2>🧪 Try It</h2>
                    <p>Visit <a href="/swagger-ui/index.html">Swagger UI</a> for interactive API testing.</p>
                </div>
            </body>
            </html>
            """;
    }
}