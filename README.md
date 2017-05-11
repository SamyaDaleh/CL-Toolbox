# CL-Toolbox
A Parsing-as-Deduction system implemented in Java that parses everything and some related tools for educational purposes.

The implementation is based on the slides from Laura Kallmeyer. At the moment it contains:

Parsing as Deduction:
- Top Down for CFG
- Shift Reduce for CFG
- Earley for CFG
- Left Corner for CFG
- CYK for TAG
- Earley for TAG

It prints out full parsing traces along with applied rules and antecedence items.
Use it to generate examples, play around with grammars etc.

Installation:

Download the jar from /releases and that's it.

Or if you want to tinker with the code, for me it worked like this:
- clone
- create new Java Project in Eclipse
- copy files into Java Project
- run test.src.common.CallAllTests.java as Java Application to verify that everything is working. You should see some success messages and parsing outputs.


Usage:

Run it in Eclipse. Or download the jar from folder releases and run it from the command line. Run it without parameters to get a help about how to use it.

Example:

(Output is pretty-printed similar to this, but without the fancy borders and coloring.)

Call with java -Dfile.encoding="UTF-8" -jar ... to correctly display special characters.

java -jar CL-Toolbox.jar anbn.cfg "a a b b" cfg-topdown
<table border="0">
<tr><td>1</td><td>[S,0]</td><td>axiom</td><td></td></tr>
<tr><td>2</td><td>[a S b,0]</td><td>predict S -> a S b</td><td>1</td></tr>
<tr><td>3</td><td>[a b,0]</td><td>predict S -> a b</td><td>1</td></tr>
<tr><td>4</td><td>[S b,1]</td><td>scan a</td><td>2</td></tr>
<tr><td>5</td><td>[b,1]</td><td>scan a</td></td><td>3</tr>
<tr><td>6</td><td>[a b b,1]</td><td>predict S -> a b</td><td>4</td></tr>
<tr><td>7</td><td>[b b,2]</td><td>scan a</td><td>6</td></tr>
<tr><td>8</td><td>[b,3]</td><td>scan b</td><td>7</td></tr>
<tr><td>9</td><td>[,4]</td><td>scan b</td><td>8</td></tr>
</table>
true