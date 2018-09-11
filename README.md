# CL-Toolbox

## Overview
A Parsing-as-Deduction system implemented in Java that parses everything and some related tools for educational purposes.

The implementation is based on the slides from Laura Kallmeyer. At the moment it contains:

Parsing as Deduction:
- CYK for CFG
- Earley for CFG
- Top Down for CFG
- Shift Reduce for CFG
- LR(k) for CFG
- Left Corner for CFG
- Unger for CFG
- A* for PCFG
- CYK for PCFG
- CYK for TAG
- Earley for TAG
- CYK for sRCG
- Earley for sRCG

It prints out full parsing traces along with applied rules and antecedence items.
Use it to generate examples, play around with grammars etc.

## Installation

Download the jar from /releases and that's it. The jar already contains needed third-party libraries. You need Java 8 or higher installed for the jar to run, or else compile it for yourself.

Or if you want to tinker with the code, for me it worked like this:
- use your favorite IDE to clone/checkout the project
- add the dependencies to your classpath/build path
- run the JUnit tests in test.src to verify that everything is working. You should see some success messages and parsing outputs.

### Dependencies

* apache/logging-log4j2 at runtime
* junit-team/junit4 for development

## Usage

Run it in your IDE. Or download the jar from folder releases and run it from the command line. Run it without parameters to get a help about how to use it.

Call with java -Dfile.encoding="UTF-8" -jar ... to correctly display special characters. Or call CL-Toolbox.bat (Windows) or CL-Toolbox.sh (Linux). They work with the same parameters, but you don't have to type in the encoding.

Video series about background and demonstration :
[English](https://www.youtube.com/playlist?list=PLlnJDVO5phqbKLKRvvQcrBK0VQjc6y7BH)
[German](https://www.youtube.com/playlist?list=PLt6jZ7OSaZOXEF-Bj18L8kpxZzt3rzhro)

Examples:

java -jar CL-Toolbox.jar anbn.cfg "a a b b" cfg-topdown

true
<table border="0">
<tr><td>1</td><td>[S,0]</td><td>axiom</td><td>{}</td></tr>
<tr><td>2</td><td>[a b,0]</td><td>predict S -> a b</td><td>{1}</td></tr>
<tr><td>3</td><td>[a S b,0]</td><td>predict S -> a S b</td><td>{1}</td></tr>
<tr><td>4</td><td>[b,1]</td><td>scan</td><td>{2}</td></tr>
<tr><td>5</td><td>[S b,1]</td><td>scan</td><td>{3}</td></tr>
<tr><td>6</td><td>[a b b,1]</td><td>predict S -> a b</td><td>{5}</td></tr>
<tr><td>7</td><td>[a S b b,1]</td><td>predict S -> a S b</td><td>{5}</td></tr>
<tr><td>8</td><td>[b b,2]</td><td>scan</td><td>{6}</td></tr>
<tr><td>9</td><td>[S b b,2]</td><td>scan</td><td>{7}</td></tr>
<tr><td>10</td><td>[b,3]</td><td>scan</td><td>{8}</td></tr>
<tr><td>11</td><td>[a b b b,2]</td><td>predict S -> a b</td><td>{9}</td></tr>
<tr><td>12</td><td>[a S b b b,2]</td><td>predict S -> a S b</td><td>{9}</td></tr>
<tr><td>13</td><td>[ε,4]</td><td>scan</td><td>{10}</td></tr>
</table>

Where anbn.cfg is:
```
N = {S}
T = {a, b}
S = S
P = {S -> a S b, S -> a b}
```

CL-Toolbox.bat ancb.tag "a c b" tag-cyk --success

true
<table border="0">
<tr><td>1</td><td>[β,.1⊤,0,-,-,1]</td><td>lex-scan a</td><td>{}</td></tr>
<tr><td>3</td><td>[α2,.1⊤,1,-,-,2]</td><td>lex-scan c</td><td>{}</td></tr>
<tr><td>7</td><td>[β,.2⊤,1,1,2,2]</td><td>foot-predict</td><td>{}</td></tr>
<tr><td>12</td><td>[α1,.2⊤,2,-,-,3]</td><td>lex-scan b</td><td>{}</td></tr>
<tr><td>15</td><td>[β,ε⊥,0,1,2,2]</td><td>move-binary</td><td>{1, 7}</td></tr>
<tr><td>16</td><td>[α2,ε⊥,1,-,-,2]</td><td>move-unary</td><td>{3}</td></tr>
<tr><td>19</td><td>[β,ε⊤,0,1,2,2]</td><td>null-adjoin</td><td>{15}</td></tr>
<tr><td>21</td><td>[α2,ε⊤,0,-,-,2]</td><td>adjoin α2[ε,β]</td><td>{16, 19}</td></tr>
<tr><td>23</td><td>[α1,.1⊤,0,-,-,2]</td><td>substitute α1[.1,α2]</td><td>{21}</td></tr>
<tr><td>25</td><td>[α1,ε⊥,0,-,-,3]</td><td>move-binary</td><td>{12, 23}</td></tr>
<tr><td>27</td><td>[α1,ε⊤,0,-,-,3]</td><td>null-adjoin</td><td>{25}</td></tr>
</table>

Where ancb.tag is:
```
N = {S, T}
T = {a, b, c}
S = S
I = {α1 : (S T b), α2 : (T c)}
A = {β : (T a T*)}
```

## Features

- prints full traces or only successful traces
- automatically transforms grammars into more expressive formalisms
- can convert grammars to fit the algorithm
- displays derivated trees for CFG Topdown, Shiftreduce, Earley and TAG CYK and Earley, sRCG Earley
- for TAG displays item trees on mouseover
