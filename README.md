# CL-Toolbox

## Overview
A Parsing-as-Deduction system implemented in Java that parses everything and 
some related tools for educational purposes.

Check the documentation in `/doc/doc.md` for the list of currently supported 
algorithms and formalisms and all implementation details.

It prints out full parsing traces along with applied rules and antecedence items.
Use it to generate examples, play around with grammars etc.

## Without Installation

If you just want to get parsing traces of the most common algorithms, please 
visit http://cl-taskbox.de/. You can insert grammar and input for all algorithms 
listed there, afterwards click Help > Solve Task to get the full trace.

## Installation

If you want to tinker with the code, for me it worked like this:
- use your favorite IDE to clone/checkout the project
- add the dependencies to your classpath/build path, preferable with the 
build.gradle file
- run the JUnit tests in src.test to verify that everything is working. 
You should see some success messages and parsing outputs.

Logged-in GitHub users can download the latest jar from the Action tab.

### Dependencies

See build.gradle.

## Usage

Run it in your IDE with <grammar> <input> <algorithm> passed to main.

Or call `gradle installDist` and one of the examples from below.

Video series about background and demonstration :
[English](https://www.youtube.com/playlist?list=PLlnJDVO5phqbKLKRvvQcrBK0VQjc6y7BH)
[German](https://www.youtube.com/playlist?list=PLt6jZ7OSaZOXEF-Bj18L8kpxZzt3rzhro)

Examples:

./build/install/cl-toolbox/bin/cl-toolbox.bat ./src/test/resources/grammars/cfg/anbn.cfg "a a b b" cfg-topdown

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

./build/install/cl-toolbox/bin/cl-toolbox.bat ./src/test/resources/grammars/tag/ancb.tag "a c b" tag-cyk --success

(Windows: May call `chcp 65001` if special characters are not displayed correctly.)

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
- displays derived trees for all algorithms
- for TAG displays item trees on mouseover
