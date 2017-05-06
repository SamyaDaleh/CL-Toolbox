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

I have it as a project in Eclipse. There I tinker with and run the Main.java to call the methods I want.

See the test folder for examples of how to use it.