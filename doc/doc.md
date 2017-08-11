# Documentation

This documentation gives details about the functionality of the CL-Toolbox. It is aimed at users who seek a deeper understanding of how the different parts work together.

## Command line parameters

This chapter explains all command line parameters that can be used when requesting the CL-Toolbox.

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
In this format each pair of brackets surround a sub tree. Each sub tree is denoted by a list of node labels, where the first label belongs to the parent and every other belongs to its children in that order. The labels may contain subscripts: NA says that no adjunction is possible in that node, while OA says that adjunction in this node is obligatory. An asterix denotes the foot node. Another exception is ε. If a node has label ε, instead it is labeled with the empty string.
The Tree class can also represent some trees with crossing edges and also represent them as strings. Here is one example:
```
(S (Comp (dat<0> ))(VP (NP (Jan<1> ))(VP (NP (Piet<2> ))(VP (NP (de-kinderen<3> ))(V (zwemmen<6> )))(V (helpen<5> )))(V (zag<4> ))))
```
This format is similar to the first one, but each leaf is appended by an index in pointed brackets that indicate the real order of the leaf nodes. Hence the representation of crossing edges is limited to the lowest level. 

### References

Marcus, Mitchell et. al: The Penn Treebank: annotating predicate argument structure. HLT '94 Proceedings of the workshop on Human Language Technology : Plainsboro, NJ, 1994. URL http://dl.acm.org/citation.cfm?id=1075835. – ISBN:1-55860-357-3 p. 114-119