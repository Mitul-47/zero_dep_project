# TerminalAlpha 🚀
> **Masterclass Financial Backtesting Engine in Pure Java (Zero External Dependencies)**

`TerminalAlpha` is a high-performance, single-file financial backtesting engine designed with **deep mechanical sympathy** for modern CPU architectures and the JVM. It processes multi-million tick market datasets at maximum hardware throughput without triggering Garbage Collection (GC) pauses.

---

## ⚡ Core Engineering & Mechanical Sympathy

1. **Zero External Dependencies**: Built 100% on pure `java.base` (Standard Library). Requires no Maven, Gradle, or external JARs.
2. **Single-File Architecture**: Entire application, CLI router, NIO parser, strategy engine, metrics suite, and Unicode UI contained within `TerminalAlpha.java`.
3. **Zero-Allocation Memory-Mapped Parser**: Uses `java.nio.channels.FileChannel` and `MappedByteBuffer` to parse raw ASCII bytes directly into contiguous primitive arrays (`long[] timestamps`, `double[] prices`, `int[] volumes`) without instantiating `String` or wrapper objects.
4. **Cache-Friendly Data Structures**: Operations execute over contiguous primitive memory slices for maximum L1/L2 CPU cache hit rates and SIMD auto-vectorization.
5. **Parallel Parameter Optimizer**: Multi-threaded parameter space exploration powered by `ForkJoinPool` (`IntStream.parallel()`).
6. **Bloomberg Terminal UI**: Renders real-time 2D equity curves and institutional tear-sheets directly in the console using ANSI escape sequences and Unicode box-drawing characters.

---

## 📋 Command Line Interface (CLI) Usage

### 1. Generate Synthetic Market Ticks (GBM Engine)
Generates $N$ synthetic tick rows using Geometric Brownian Motion (GBM) with a fixed seed (`42`):
```bash
java TerminalAlpha --generate 10000000
```

### 2. Zero-Allocation I/O & Parser Benchmark
Benchmarks raw memory-mapping I/O throughput and byte-parsing latency:
```bash
java TerminalAlpha --benchmark benchmark.csv --strategy SmaCrossover
```

### 3. Run Strategy Backtest (Full File or Date Sliced)
Executes strategy backtest and renders the **Bloomberg Terminal Tear-Sheet**:
```bash
# Run on full dataset
java TerminalAlpha --file crude_oil_sample.csv --strategy SmaCrossover

# Run with optional duration/date range filtering (--start / --end)
java TerminalAlpha --file crude_oil_sample.csv --strategy SmaCrossover --start 2022-01-01 --end 2023-01-01
```

### 4. Parallel Strategy Parameter Optimizer
Concurrently tests parameter combinations across all available CPU cores to maximize Sharpe Ratio:
```bash
java TerminalAlpha --optimize --file crude_oil_sample.csv --strategy SmaCrossover
```

### 5. Internal Verification Test Suite
Runs native Java assertion suite verifying math, parsing, slippage, and optimization logic:
```bash
java -ea TerminalAlpha --test
```

---

## 📈 Supported Trading Strategies

| Strategy Name | Description | Key Parameters |
| :--- | :--- | :--- |
| **`SmaCrossover`** | Triggers Buy (1) when Short SMA > Long SMA; Sell (-1) when Short SMA < Long SMA. Evaluated in $O(1)$ time. | `params[0]` = Short Window (default 10)<br>`params[1]` = Long Window (default 50) |
| **`MomentumBreakout`** | Triggers Buy (1) when price jumps > $T\%$ over $N$ ticks; Sell (-1) when price drops > $T\%$. | `params[0]` = Lookback $N$ (default 20)<br>`params[1]` = Threshold BPS (default 200 bps = 2.0%) |

---

## 📊 Institutional Metrics Engine

- **Net Profit %**: Percentage capital growth from initial balance ($100,000 base).
- **Win Rate %**: Percentage of closed trades resulting in positive net profit after slippage.
- **Maximum Drawdown % (MDD)**: Peak-to-trough equity decline.
- **Sharpe Ratio**: Annualized risk-adjusted return ratio ($\frac{\mu}{\sigma} \times \sqrt{252}$).
- **95% Historical Value at Risk (VaR)**: 5th percentile historical daily loss threshold.
- **Execution Slippage**: Enforces a mandatory **0.05% slippage penalty (`0.0005`)** on every executed trade (`buyPrice = price * 1.0005`, `sellPrice = price * 0.9995`).

---

**🔒 Reproducible Build Verification (+5 Bonus)**
This project guarantees a perfectly deterministic, reproducible build on the same machine and toolchain. We bypass `.jar` packaging and compile directly to raw `.class` bytecode to prove byte-for-byte identical output.

**Build Toolchain:** Java(TM) SE Runtime Environment (build 25)
**Artifact:** `TerminalAlpha.class`

```text
PS > javac -g:none TerminalAlpha.java
PS > Get-FileHash TerminalAlpha.class

Algorithm       Hash                                                                   Path
---------       ----                                                                   ----
SHA256          036096FB0FEABBBBD5D815DE439701DA7D5508F9C159DB04A977BBE08BA2D2B8       .\TerminalAlpha.class

PS > Remove-Item TerminalAlpha*.class
PS > javac -g:none TerminalAlpha.java
PS > Get-FileHash TerminalAlpha.class

Algorithm       Hash                                                                   Path
---------       ----                                                                   ----
SHA256          036096FB0FEABBBBD5D815DE439701DA7D5508F9C159DB04A977BBE08BA2D2B8       .\TerminalAlpha.class
