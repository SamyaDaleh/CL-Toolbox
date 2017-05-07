# CL-Toolbox
A Parsing-as-Deduction system implemented in Java that parses everything and some related tools for educational purposes.

The implementation is based on the slides from Laura Kallmeyer. At the moment it contains:

Parsing as Deduction:
- Top Down for CFG
- Shift Reduce for CFG
- Earley for CFG
- CYK for TAG

It prints out full parsing traces along with applied rules and antecedence items.
Use it to generate examples, play around with grammars etc.

Installation:

For me it worked like this:
- clone
- create new Java Project in Eclipse
- copy files into Java Project
- run test.src.common.CallAllTests.java as Java Application to verify that everything is working. You should see some success messages and parsing outputs.


Usage:

Run it in Eclipse. Or download the jar from folder releases and run it from the command line. Run it without parameters to get a help about how to use it.

Example:

java -jar CL-Toolbox.jar anbn.cfg "a a b b" cfg-topdown
1    [S,0]               axiom               <br>
2    [a S b,0]           predict S -> a S b  1<br>
3    [a b,0]             predict S -> a b    1<br>
4    [S b,1]             scan a              2<br>
5    [b,1]               scan a              3<br>
6    [a b b,1]           predict S -> a b    4<br>
7    [b b,2]             scan a              6<br>
8    [b,3]               scan b              7<br>
9    [,4]                scan b              8<br>
true