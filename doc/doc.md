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

This section covers the different grammar formats that can be read from files. Different examples are located in directory resources/grammars. They all are plain text files where the file extension indicates the grammar type. Every grammar format is close to the respective formal definition. In every line one component of the grammar tuple is defined. The first symbol in each line determines which component is defined. No component can be defined more than once, but they can occur in every order. Every element is set into quotes. The user can freely use both uppercase and lowercase strings both as terminals and nonterminals and even mixed. Because of the quotes the user can also use symbols like comma or brackets as symbols, if they do not occur with reserved meaning inside of quotes. Generally speaking tokens have to be separated by spaces except if they are not meta symbols, like sequences of terminals or nonterminals. Currently the grammar parser is not very strict. It looks for the first symbol indicating the component and then grabs everything between quotes as one element. This might change in further development. Also there is no check for consistency. The user can declare the same symbol as nonterminal and terminal or use a symbol in a rule that is not declared as either and might get unexpected results. The user is asked to take care of creating grammars that make sense. Currently the only check is for an even amount of quotes per line.

#### CFG

A context-free grammar is the most commonly used grammar type. Most programming languages fall into this category. Generally speaking it can be described as the one that "counts to two", which means it covers languages where there is two times the same amount of terminals, where the outermost symbols belong together. Here is an example:
```
N = {"S"}
T = {"a", "b"}
S = "S"
P = {"S -> a S b", "S -> a b"}
```
Nonterminals, terminals, start symbol and production rules can be defined in any order. Every line consists of a symbol indicating the type of the component: N - nonterminals, T - terminals, S - start symbol and P - production rules. Each production rule must consist of a single nonterminal on the left hand side followed by "->". The tokens of the right hand side must be separated by spaces, other spaces are optional. An empty string on the right hand side can be either represented as ε or by writing nothing.

#### PCFG

A probabilistic CFG is like a CFG where every rule has a probability. The probability is specified in front of each rule before a colon. Otherwise the components are the same as for CFG. Here is an example.
```
N = {"S", "A", "B"}
T = {"0", "1"}
S = "S"
P = {"1 : S -> A B", "0.7 : A -> 1", "0.3 : A -> 0", "0.6 : B -> BB", "0.4 : B -> 0"}
```
The rules with the same nonterminal on the right hand side are supposed to add up to a probability of 1. However, as in most cases there is currently no check if that is the case and it is up to the user to take care of that.

#### TAG

A tree adjoining grammar is a weak context-sensitive formalism with higher complexity as a CFG. While a CFG can "count up to 2" a TAG can "count up to 4". That means it can generate 4 sequences of terminals of the same length. Also it is known to generate the copy language. It can generate some crossing dependencies found in natural languages while generating context-free trees, that are trees without crossing edges. Here is an example: 
```
N = {"S"}
T = {"a", "b", "c", "d"}
S = "S"
I = {"α1 : (S_OA ε)"}
A = {"β : (S_NA a (S b S* c) d)"}
```
A TAG consists of nonterminals, terminals and a start symbol like a CFG. But instead of production rules its rules are two sets of trees: I - initial trees and A - auxiliary trees. The tree format is described below. An initial tree might replace a nonterminal leaf in another tree where that leaf has the same label as the root node of the initial tree. This function is called substitution and is similar to a derivation step of a CFG. An auxiliary tree might replace an inner node in another tree that has the same label as the so called foot and root nodes of that auxiliary tree. This function is called adjunction and gives the TAG a higher expressive power compared to a CFG. Each tree has a name that is specified in front of the colon. Usually initially trees are named α concatenated with a number and auxiliary trees β with a number. However, indeed the user is relatively free to give them any name that is no meta symbol like a colon.

##### Tree format

The context free trees, when not represented as Tree object, can be parsed from and retrieved in bracket format as described by Mitchel 1994 Here is one example:
```
(T_NA (B (T a ε) ) (B_OA T*))
```
In this format each pair of brackets surround a sub tree. Each sub tree is denoted by a list of node labels, where the first label belongs to the parent and every other belongs to its children in that order. The labels may contain subscripts: NA says that no adjunction is possible in that node, while OA says that adjunction in this node is obligatory. An asterix denotes the foot node. Another exception is ε. If a node has label ε, instead it is labeled with the empty string.

#### SRCG

A simple Range Concatenation Grammar is a generalization of a CFG. While a CFG can generate up to 2 sequences of terminals of the same length, a sRCG can generate 2k, where k is the highest dimension of any rule in the grammar, that is the number of separate strings that are generated in a rule. Hence if k = 1, the sRCG is equivalent to a CFG. Compared to a CFG a sRCG consists of the same four components and an additional set of variables V that can be used in every rule. Here is an example.
```
N = {"A", "B", "S"}
T = {"a", "b", "c", "d"}
V = {"X", "Y", "V", "W"}
P = {"S( X V Y W ) -> A(X,Y) B(V,W)", "B(ε,ε) -> ε", "B(b X,d Y) -> B(X,Y)", "A(a,c) -> ε", "A(a X,c Y) -> A(X,Y)"}
S = "S"
```
An empty string on the right hand side can be either represented as ε or by writing nothing.

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


#### Tree format

The same tree class that was created to represent the trees of a TAG is also used for representing derived trees as the result of the parsing process. For representing crossing edges as needed for sRCG parsing it had to be extended. Here is one example of a tree with crossing edges in bracket string format:
```
(S (Comp (dat<0> ))(VP (NP (Jan<1> ))(VP (NP (Piet<2> ))(VP (NP (de-kinderen<3> ))(V (zwemmen<6> )))(V (helpen<5> )))(V (zag<4> ))))
```
This format is similar to the first one, but each leaf is appended by an index in pointed brackets that indicate the real order of the leaf nodes. Hence the representation of crossing edges is limited to the lowest level. 


### Graphical output

## Other important classes and methods

This chapter contains detailed explanations about methods worth mentioning.



### References

Marcus, Mitchell et. al: The Penn Treebank: annotating predicate argument structure. HLT '94 Proceedings of the workshop on Human Language Technology : Plainsboro, NJ, 1994. URL http://dl.acm.org/citation.cfm?id=1075835. – ISBN:1-55860-357-3 p. 114-119