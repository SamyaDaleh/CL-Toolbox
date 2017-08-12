# Documentation

The CL-Toolbox is an open source implementation of the parsing-as-deduction approach. It takes a grammar file, a string containing the input sequence and a string that determines the parsing algorithm and puts out the trace of steps of how the input was processed by that algorithm. According to the deduction approach a set of rules are used to derive a goal item. The deductions happens for every implemented parsing algorithm, only the underlying rules are changed. This documentation gives details about the functionality of the CL-Toolbox. It is aimed at users who seek a deeper understanding of how the different parts work together.

## Command line parameters

This chapter explains all command line parameters that can be used when requesting the CL-Toolbox. Usually the Cl-Toolbox.jar file is used that is provided in the folder releases. A basic call can be this:
```
java -jar CL-Toolbox.jar anbn.cfg "a a b b" cfg-topdown
```
java -jar is the common call to a jar file. After the jar file three parameters are passed. If less parameters are passed, some help text is printed. The grammar file is a plain text file whose extension specifies the grammar type. See the sections below for details on the grammar formats. The input string to be parsed contains of space separated tokens and has to be put into quotes in order to be recognized as one string. The name of the parsing algorithm starts with the name of the formalism, a hyphen and the name of the algorithm itself. That is because for instance the Earley algorithm for CFG is something else than the one for TAG and the one for SRCG. Instead of just applying the algorithm of the same complexity as the grammar this approach allows the user to be specific about the algorithm and they can choose for example to let a CFG grammar be parsed by a SRCG algorithm for comparison or if no fitting grammar is at hand for the more complex algorithm. However, calling the jar directly in this way outputs question marks in place of special characters as they are used by some algorithms. To display special characters correctly, the jar has to be called like this:
```
java -Dfile.encoding="UTF-8" -jar CL-Toolbox.jar anbn.cfg "a a b b" cfg-topdown
```
To reduce the typing to a minimum the user can instead use the CL-Toolbox.bat (Windows) or the CL-Toolbox.sh (*nix) scripts. They can be requested similar to the jar with the same parameters and they pass on the request to the jar along with the encoding. Hence the call above is equivalent to the following:
```
.\CL-Toolbox.bat anbn.cfg "a a b b" cfg-topdown
```
Currently this only works if the user is located in the same folder as the jar and the script. The following is a list of the currently supported parsing algorithms. See later sections on details about what each one of them means and how they work.
* cfg-cyk
* cfg-cyk-extended
* cfg-cyk-general
* cfg-earley
* cfg-leftcorner
* cfg-leftcorner-chart
* cfg-topdown
* cfg-shiftreduce
* cfg-unger
* pcfg-astar
* tag-cyk
* tag-earley
* tag-earley-prefixvalid
* srcg-cyk
* srcg-cyk-extended
* srcg-earley
Additionally two flags can be added to the call. --success instead of printing the full trace it prints only items that lead to a goal item. If the parsing fails because the input sequence is not in the language of the grammar no steps are displayed. If steps are displayed they keep their original numbering, leaving gaps in the sequence of indices where items are left out. The other parameter is --please. When set and a grammar is not obviously not fitting for an algorithm, it is converted. Obvious conversions are performed regardless if the flag is set or not. That means for instance if a grammar is a CFG and the algorithm is for TAG, the grammar is trivially converted, no matter if the flag is set or not. If the grammar is CFG and the algorithm is for CFG the grammar still may not be appropriate. The CYK algorithm works only for grammars in Chomsky Normal Form. If the grammar is not in Chomsky Normal Form but shall be parsed with cfg-cyk, if the flag is not set the user will be informed that the grammar is not appropriate. If --please is set, the grammar is converted to Chomsky Normal Form before further steps are performed. See section GrammarToGrammarConverter in chapter Program Workflow for more details.

### Grammar formats

#### CFG

#### PCFG

#### TAG

#### SRCG

## Program Workflow

This chapter explains the different processing stages that occur when the CL-Toolbox is requested.

### Main

The user request is send to the main method in the class cli.Main. This method sets the flags, --success at the Deduction object and --please at the GrammarToGrammarConverter. This methods calls all parts of the workflow in order. The following sections cover each part in the order of the data flow.

### GrammarParser

### GrammarToGrammarConverter

This converter serves different purposes. One is that it checks if the grammar provided by the user can be handled by the parsing algorithm the user requests. If the grammar is appropriate and the algorithm has the same complexity (e. g. the grammar is a CFG and the algorithm is CFG) the grammar is simply returned. If the algorithm has a higher complexity than the grammar (e. g. the grammar is a CFG and the algorithm is for parsing TAGs), the grammar is converted to the higher formalism if possible. This happens regardless of --please is set or not. The idea is that automatic conversion happens if a conversion obviously needs be made, which is the case as the grammar file has a file extension that specifies its type as so does the algorithm. Other conversions are only made if the user explicitly sets the --please flag. If this is set and the grammar is not appropriate to be parsed with an algorithm of the same complexity, conversion is performed. For instance the common CYK algorithm for CFG demands a grammar in Chomsky Normal Form. If a grammar is provided that is not in CNF and the --please flag is set, the grammar is converted to CNF. If a grammar has both not the same complexity and is not appropriate for an algorithm, in case the --please flag is set all conversions are performed to make the grammar fitting for the algorithm. If a conversion is not possible, for instance if the --please flag is not set or the grammar can't be converted to the complexity of the algorithm, the process fails with a note to the user.

### GrammarToDeductionRulesConverter

### Deduction

### ChartToTreeConverter

The ChartToTreeConverter retrieves the derivated trees from a Deduction object. Currently not from every parsing trace the derivation tree can be retrieved. In general the converter searches for the goal item and retrieves the steps that lead to this goal, while considering only the first sets of backpointers in each case, hence the first found tree is generated. Because the search starts with the goal item the steps are retrieved in reversed order. Depending on the algorithm the steps are considered either in the given or in reversed (hence flipped again) order. The names of the steps contain the applied rule. The rules are converted to trees and applied to each other in the respective order, in this way the whole derivated tree is build up and returned in bracket format.

### Graphical output

## Other important classes and methods

This chapter contains detailed explanations about methods worth mentioning.

### Tree format

The context free trees, when not represented as Tree object, can be parsed from and retrieved in bracket format as described by Mitchel 1994 Here is one example:
```
(T_NA (B (T a ε) ) (B_OA T*))
```
In this format each pair of brackets surround a sub tree. Each sub tree is denoted by a list of node labels, where the first label belongs to the parent and every other belongs to its children in that order. The labels may contain subscripts: NA says that no adjunction is possible in that node, while OA says that adjunction in this node is obligatory. An asterix denotes the foot node. Another exception is ε. If a node has label ε, instead it is labeled with the empty string. The Tree class can also represent some trees with crossing edges and also represent them as strings. Here is one example:
```
(S (Comp (dat<0> ))(VP (NP (Jan<1> ))(VP (NP (Piet<2> ))(VP (NP (de-kinderen<3> ))(V (zwemmen<6> )))(V (helpen<5> )))(V (zag<4> ))))
```
This format is similar to the first one, but each leaf is appended by an index in pointed brackets that indicate the real order of the leaf nodes. Hence the representation of crossing edges is limited to the lowest level. 

### References

Marcus, Mitchell et. al: The Penn Treebank: annotating predicate argument structure. HLT '94 Proceedings of the workshop on Human Language Technology : Plainsboro, NJ, 1994. URL http://dl.acm.org/citation.cfm?id=1075835. – ISBN:1-55860-357-3 p. 114-119