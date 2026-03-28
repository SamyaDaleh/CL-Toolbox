# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

CL-Toolbox is a **Parsing-as-Deduction** system implemented in Java. It parses strings using multiple formal language formalisms (CFG, TAG, SRCG/LCFRS, CCG, LAG, PCFG) and multiple algorithms per formalism, outputting full parsing traces with applied rules and antecedence items for educational purposes.

## Build & Test Commands

```bash
# Build the project
gradle build

# Run all tests
gradle test

# Run a specific test class
gradle test --tests "*BinarizationTest"

# Run a single test method
gradle test --tests "*BinarizationTest.testBinarization"

# Create executable distribution
gradle installDist

# Clean build artifacts
gradle clean
```

The main entry point after `installDist` is in `build/install/cl-toolbox/bin/`. Invocation: `cl-toolbox <grammar_file> <input_string> <algorithm>`.

**Java target:** 8 (built with JDK 17). **Test framework:** JUnit 4.

## Architecture

### Core Flow

```
CLI args → Grammar Parser → [Grammar Converter] → Deduction Rules Converter → Deduction Engine → Output
```

1. **`cli/Main.java`** — Parses args, detects formalism from file extension (`.cfg`, `.tag`, `.srcg`, `.pcfg`, `.ccg`, `.lag`), and orchestrates the pipeline.

2. **`common/parser/`** — Reads grammar files into grammar objects (`Cfg`, `Tag`, `Srcg`, `Pcfg`, `Ccg`, `Lag` in `common/`).

3. **`chartparsing/converter/GrammarToDeductionRulesConverter`** — Converts a grammar + input string + algorithm name into a `ParsingSchema` (set of axioms, deduction rules, and goal conditions). Grammar-to-grammar conversions happen here too (e.g., CFG binarization before CYK).

4. **`chartparsing/Deduction.java`** — Agenda-based deduction engine. Iterates until the agenda is empty, applying `DynamicDeductionRuleInterface` rules to chart items. Maintains backpointers (`deductedFrom`) and rule names (`appliedRule`) for trace output.

5. **`chartparsing/ParsingSchema.java`** — Holds axioms (as `StaticDeductionRule`), dynamic rules (as `DynamicDeductionRuleInterface`), and goal item specifications.

6. **`gui/`** — `ParsingTraceTable` displays the step-by-step derivation; `DisplayTree` renders parse trees.

### Key Interfaces

- **`ChartItemInterface`** — A single chart item (position, symbol, dot, etc.)
- **`DynamicDeductionRuleInterface`** — A parsing rule with antecedents → consequent logic
- **`StaticDeductionRule`** — Axioms (no antecedents)

### Algorithm Packages

Each formalism has its own subdirectory under `chartparsing/`:
- `cfg/` — topdown, cyk, earley, leftcorner, shiftreduce, unger, lr-k; `cfg/cyk/astar/` for A*
- `tag/` — cyk-extended, earley, earley-prefixvalid
- `lcfrs/` — cyk-extended, earley (also handles srcg)
- `ccg/`, `lag/`

### Grammar Conversions

`chartparsing/converter/` contains converters that transform grammars before parsing (e.g., CFG → CNF for CYK, CFG → TAG for tag-based algorithms). These are separate from the file-format parsers in `common/parser/`.

### Test Resources

Grammar files used in tests live in `src/test/resources/grammars/{cfg,pcfg,tag,srcg,ccg,lag}/`.
