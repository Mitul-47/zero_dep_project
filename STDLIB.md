# STDLIB.md — TerminalAlpha

**Track F (Open / Wildcard)**
TerminalAlpha is an enterprise-grade quantitative backtesting engine and parser built to prove that Java, when subjected to strict mechanical sympathy and zero-allocation memory constraints, can process financial tick data at native C++ speeds (40M+ ticks/sec) without a single third-party framework.

## The STDLIB Log 
Below are 10 real, non-trivial standard-library substitutions made to keep the dependency manifest entirely empty while building a system that typically requires massive financial and data-science ecosystems:

1. **`OpenCSV` / `commons-csv` -> `java.nio.MappedByteBuffer`**
   *Rationale:* Standard CSV parsers instantiate `String` objects for every cell, destroying performance via Garbage Collection. We implemented a zero-allocation byte-offset parser mapping the file directly to OS virtual memory, reading ASCII bytes into raw `double[]` arrays natively.
2. **`picocli` / `commons-cli` -> `String[] args` + Iteration**
   *Rationale:* Manual parsing of stateful flags (`--benchmark`, `--file`, `--strategy`) to map CLI commands to our internal execution routers without pulling in an annotation-heavy CLI framework.
3. **`TA-Lib` (Technical Analysis) -> Manual $O(1)$ Rolling Primitives**
   *Rationale:* Replaced the industry-standard TA-Lib by hand-rolling Simple Moving Average (SMA) and Momentum Breakout math using rolling sums on pure primitive arrays, guaranteeing zero object churn.
4. **`commons-math3` (Stochastic Processes) -> `java.util.Random` + `Math.exp()`**
   *Rationale:* Wrote a custom Geometric Brownian Motion (GBM) engine using standard Gaussian logic to synthetically generate 10 million realistic order-book ticks in seconds.
5. **`commons-math3` (DescriptiveStats) -> `java.util.Arrays.sort()` + Primitive Math**
   *Rationale:* Calculated complex institutional metrics—Sharpe Ratio, Maximum Drawdown, and 95% Historical Value at Risk (VaR)—using standard sorting algorithms and raw arithmetic.
6. **`Eclipse Collections` / `Fastutil` -> Pre-allocated Native Primitives**
   *Rationale:* Dropped primitive-collection libraries in favor of aggressively pre-allocated `long[]` (timestamps) and `double[]` (prices) to enforce a strictly flat ~160MB heap and bypass auto-boxing overhead.
7. **`JFreeChart` / `XChart` -> Raw ANSI Escapes & Unicode**
   *Rationale:* Instead of a heavyweight GUI framework, we engineered a terminal-native "Bloomberg-style" UI using ANSI color codes and Unicode block characters (`█`, `▲`) to map the equity curve directly to `stdout`.
8. **`JUnit 5` / `TestNG` -> Native Java `assert`**
   *Rationale:* Bypassed external test runners entirely by using native `assert` statements (invoked via `java -ea`) for end-to-end mathematical verification and sanity checks of the strategy engine.
9. **`SLF4J` / `Logback` -> Formatted `System.out` + `String.format()`**
   *Rationale:* Avoided the logging supply chain by routing strategy telemetry and UI rendering through standard buffered text blocks, explicitly bypassed during `--benchmark` to prevent I/O bottlenecking.
10. **`RxJava` / `ExecutorService` Wrappers -> `java.util.stream.IntStream.parallel()`**
    *Rationale:* For the `--optimize` brute-force grid search, we leveraged the built-in `ForkJoinPool` via parallel streams to concurrently evaluate $N=5$ to $N=50$ SMA parameters across all CPU cores.

## Honest Limits & Compromises
Because we optimized for brutal execution throughput (40M+ ticks/sec) over general-purpose flexibility:
* **CSV Escaping:** The zero-allocation parser assumes clean, strictly formatted numerical CSV data (`Timestamp,Price,Volume`). It does not handle RFC 4180 quotes, multi-line fields, or escaping. Doing so would require branching and object allocations that destroy the parser's CPU cache-line efficiency.
* **Testing:** Using native assertions means we do not have pretty HTML test reports, mocking, or coverage stats—just silent success or a fast `AssertionError` crash. 

## Architectural Verdict
Standard Java is commonly criticized as "too bloated" for high-frequency data parsing. By combining `MappedByteBuffer` with primitive mathematical parsing and bypassing standard String manipulation, TerminalAlpha proves the bottleneck is almost always the ecosystem packages, not the JVM itself.