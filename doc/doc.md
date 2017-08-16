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
Nonterminals, terminals, start symbol and production rules can be defined in any order. Every line consists of a symbol indicating the type of the component: N - nonterminals, T - terminals, S - start symbol and P - production rules. Each production rule must consist of a single nonterminal on the left hand side followed by "->". The tokens of the right hand side must be separated by spaces, other spaces are optional. An empty string on the right hand side can be either represented as ε or by writing nothing. In the Cfg object terminals and nonterminals are represented as lists for better access despite they are sets according to the definition. This holds for all grammar formats where the definitions prescribe sets of anything. A CfgProductionRule is represented as object where the left hand side is a string and the right hand side is an array of strings.

#### PCFG

A probabilistic CFG is like a CFG where every rule has a probability. The probability is specified in front of each rule before a colon. Otherwise the components are the same as for CFG. Here is an example.
```
N = {"S", "A", "B"}
T = {"0", "1"}
S = "S"
P = {"1 : S -> A B", "0.7 : A -> 1", "0.3 : A -> 0", "0.6 : B -> BB", "0.4 : B -> 0"}
```
The rules with the same nonterminal on the right hand side are supposed to add up to a probability of 1. However, as in most cases there is currently no check if that is the case and it is up to the user to take care of that. Internally the Pcfg is an extension of the Cfg with the only difference that each rule has a probability.

#### TAG

A tree adjoining grammar is a weak context-sensitive formalism with higher complexity as a CFG. While a CFG can "count up to 2" a TAG can "count up to 4". That means it can generate 4 sequences of terminals of the same length. Also it is known to generate the copy language. It can generate some crossing dependencies found in natural languages while generating context-free trees, that are trees without crossing edges. Here is an example: 
```
N = {"S"}
T = {"a", "b", "c", "d"}
S = "S"
I = {"α1 : (S_OA ε)"}
A = {"β : (S_NA a (S b S* c) d)"}
```
A TAG consists of nonterminals, terminals and a start symbol like a CFG. But instead of production rules its rules are two sets of trees: I - initial trees and A - auxiliary trees. The tree format is described below. An initial tree might replace a nonterminal leaf in another tree where that leaf has the same label as the root node of the initial tree. This function is called substitution and is similar to a derivation step of a CFG. An auxiliary tree might replace an inner node in another tree that has the same label as the so called foot and root nodes of that auxiliary tree. This function is called adjunction and gives the TAG a higher expressive power compared to a CFG. Each tree has a name that is specified in front of the colon. Usually initially trees are named α concatenated with a number and auxiliary trees β with a number. However, indeed the user is relatively free to give them any name that is no meta symbol like a colon. Internally the initial and auxiliary trees are stored in two separate maps with the tree names as keys. To represent trees there are different approaches. One considered was to build up a tree recursively of having a root label and a list of children which are trees or subtrees themselves. However, this implementation stays close to the definition, which states that a tree consists of sets of vertexes, directed edges between two vertexes each and one root node. An auxiliary tree also has one foot node. Here each tree also holds a list of nodes that are labeled "no adjoin" or "obligatory adjoin". Each vertex remembers its label and gorn address.

##### Tree format

The context free trees, when not represented as Tree object, can be parsed from and retrieved in bracket format as described by Mitchel (1994) Here is one example:
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
An empty string can be either represented as ε or by writing nothing. Internally the Srcg object contains the sets (lists) of terminals, nonterminals, variables and clauses. A clause consists of predicates. Similar to a CfgProductionRule a clause has one predicate on the left hand side and a list of predicates on the right hand side. A predicate is made up of one nonterminal and arguments consisting of elements, here represented as a two dimensional string.

## Program Workflow

This chapter explains the different processing stages that occur when the CL-Toolbox is requested.

### Main

The user request is send to the main method in the class cli.Main. This method sets the flags, --success at the Deduction object and --please at the GrammarToGrammarConverter. This methods calls all parts of the workflow in order. The following sections cover each part in the order of the data flow.

### GrammarParser

The main method has checked for the file extension of the grammar file and called the matching function for that file type. All functions nearly work the same: They read the file line by line while creating the grammar. They check for the component to create, indicated by the first char that is not a space. If the component already exist for that grammar because it is declared twice in a note is dropped and nothing is returned. If it not exists, every other content in that line that is between quotes is returned as a token each. Nonterminals, terminals, variables or the start symbol can be directly set in the grammar. Production rules or clauses are added to the grammar, which internally passes the string representation to the respective constructor.

### GrammarToGrammarConverter

This converter serves different purposes. One is that it checks if the grammar provided by the user can be handled by the parsing algorithm the user requests. If the grammar is appropriate and the algorithm has the same complexity (e. g. the grammar is a CFG and the algorithm is CFG) the grammar is simply returned. If the algorithm has a higher complexity than the grammar (e. g. the grammar is a CFG and the algorithm is for parsing TAGs), the grammar is converted to the higher formalism if possible. This happens regardless of --please is set or not. The idea is that automatic conversion happens if a conversion obviously needs be made, which is the case as the grammar file has a file extension that specifies its type as so does the algorithm. Other conversions are only made if the user explicitly sets the --please flag. If this is set and the grammar is not appropriate to be parsed with an algorithm of the same complexity, conversion is performed. For instance the common CYK algorithm for CFG demands a grammar in Chomsky Normal Form. If a grammar is provided that is not in CNF and the --please flag is set, the grammar is converted to CNF. If a grammar has both not the same complexity and is not appropriate for an algorithm, in case the --please flag is set all conversions are performed to make the grammar fitting for the algorithm. If a conversion is not possible, for instance if the --please flag is not set or the grammar can't be converted to the complexity of the algorithm, the process fails with a note to the user.

#### Conversions in detail

This section covers how grammars are transformed into other equivalent grammars. For details on which transformation is performed for which parsing algorithm see the respective algorithm in section Deduction.

##### CFG to PCFG

For converting a CFG to a PCFG the number of rules with the same left hand symbol are determined and the probabilities are divided equally among them so they add up to 1.

##### CFG to TAG

To get an equivalent TAG for a CFG each production rule is converted to a tree where the left hand symbol is the root and the right hand side symbols are the children in the same order. Each tree becomes an initial tree in the new TAG. Hence each derivation step is now performed as substitution in the TAG.

##### CFG to SRCG

Each CfgProductionRule is converted to a clause. Each nonterminal symbol becomes the nonterminal symbol of a new predicate and new variables are added up to the amount of right hand side nonterminals, so all strings can be appended to one string in the left hand side. Each terminal in the right hand side is left away and put directly into the string of the left hand side.

##### PCFG to CFG

The conversion of a PCFG to a CFG is nothing else than tossing away all probabilities.

##### CFG Binarization

When a CFG is binarized all rules with a right hand side of length greater than 2 are altered. The first symbol on the right hand side is kept and all others are replaced by a new nonterminal which is not yet in the grammar. It is named "X" and a number which is incremented until the symbol has not been seen before. A new rule with that new symbol on the left hand side and all remaining symbols on the right hand side is added to the grammar. This process repeats until no rule is altered anymore.

##### CFG Removal of Chain Rules

##### CFG Removal of Empty Productions

##### CFG Removal of Left Recursion

##### CFG have either terminals or nonterminals on the Right Hand Side

##### CFG Removal of Useless Symbols

##### TAG Binarization

##### SRCG Binarization

##### SRCG Removal of Empty Productions

##### SRCG Ordering

### GrammarToDeductionRulesConverter

If it is sure that the grammar is appropriate for the requested parsing algorithm, the grammar is send to the GrammarToDeductionRulesConverter which takes care of calling the right function from the other converter classes. There is one class for each grammar type that has a function for each implemented algorithm to create a parsing scheme with the rules for that algorithm depending on he parser. Each ParsingSchema object contains different deduction rules. Each deduction rule contains different items. An item is syntactically a string array and semantically it represents a configuration of the parsing process and the meaning of its entries highly depends on the algorithm. A parsing schema contains one or more goal items which have to be created in the deduction process iff the input string is generated by the grammar. A deduction rule consists a list of zero or more antecedence items which have to be found in order to use a rule, and a list of one or more consequence items, which are added to the set of items that can be used if all antecedence items were found. The parsing schema contains axioms, a list of deduction rules, here represented as so called StaticDeductionRules, because they don't change at runtime. Axioms have zero antecedence items and can be applied if there are no items yet. Also a parsing schema contains a list of rules, here of type DynamicDeductionRule. By definition they contain both antecedence and consequence items. However, in this implementation they change at runtime. When trying out a rule a list of antecedence items is passed to them and the rule tries to generate consequence items of them. There is one class for each kind of rule that belongs to a parsing algorithm. On creation of the parsing schema one or more instances of a rule are added to the parsing schema. The aim is that each rule creates one kind of consequence items while staying close to the definitions. For instance for CFG CYK parsing there could be one axiom that just contains every generateable item, which would work and only need the creation of one rule, but this rule would be far from the definition. The definition says that if there is a rule A -> a from the set of production rules and a terminal in the input string at position i is a, the rule can be applied and one consequence item can be derived. Hence to stay close to the definition one rule instance is created for every matching production rule and input symbol, so each rule generates exactly one item. For dynamic production rules the decisions are the same although far less obvious, because they receive their antecedences at runtime. Still every instance of a dynamic production rule is responsible to generate one kind of output and no rule can generate different consequences from the same antecedence items. Every rule has a name that may gives details about what its exact purpose is. Every rule has a string representation close to the formal definition where known parts may are filled out. That means if for instance a formal definition asks for any production rulle of form A -> a the string representation may contains the real nonterminal and terminal instead if the deduction rule is only responsible for that one production rule.

### Deduction

The Deduction object takes a parsing schema as input for performing the parsing process, but not the input string. This is because the input string is either reflected by the generated deduction rules or is stored in the rules that need to consider it. The deduction object contains a chart that holds all generated items. A agenda that holds new items which will be used for creating new items. A list of backpointers that say from which items a new item was generated and is empty for items derived with axioms. And a list of rule names that contains the names of the rules that were used to derive that item. According to the definition chart and agenda are sets, but they were implemented as lists to handle them in order and to make use of their indices. The item in chart at position i was derived with the backpointers from the backpointers list at position i with the rule in the rule list at position i. In the parsing process first all lists are empty and all axiom rules are considered. New items are added to chart and agenda. After that initialization only the dynamic rules are used. One item is retrieved and removed from the agenda, this item is tried out with every dynamic rule. For every rule the number of antecedence items is retrieved and all possible combinations of the item and items from the chart of the number of antecedences are passed to the rule and the consequences are retrieved. If consequences could be generated, it is checked if the new items are already in the chart. New items are added to chart and agenda along with the respective entries of backpointers and applied rule. If an item already exists in the chart, it is not added to the agenda. Usually new backpointers and the rule name are added to the existing entry. But for probabilistic algorithms that seek to find the best item, the new item might replace the old one and the backpointers and rule names are overwritten instead. The deduction process can look for the best item in terms of probability, that means the maximum, or the best item in terms of costs, that is the minimum. This is specified by setting the replace flag to "h" for high to get the maximum or to "l" for low to get the minimum. Items are retrieved and tried out from the agenda until the agenda is empty. Then the chart is checked if it contains one of the goal items and true is returned if one was found, else false. The deduction object still holds all information and can be requested to return the trace. The function printTrace serves two purposes, which currently are both used at the same time so it is no need to separate them. One is that it goes through chart, backpointers and rules and pretty prints out either the full trace or the successful trace depending on wheather the flag is set or not. The other purpose is that it returns the data as two dimensional array, so it can be passed to the graphical output.

#### Parsing Algorithms

This section covers the currently implemented parsing algorithm. See here which properties an appropriate grammar must have in order to work with this algorithm, the working of the algorithm in general and its different rules.

##### CFG Bottom Up

##### CFG CYK

##### CFG CYK Extended

##### CFG CYK General

##### CFG Earley

##### CFG Left Corner

##### CFG Left Corner Chart

##### CFG Top Down

##### CFG Unger

##### PCFG A*

##### TAG CYK

##### TAG CYK Extended

##### TAG Earley

##### TAG Earley Prefix Valid

##### SRCG CYK

##### SRCG CYK Extended

##### SRCG Earley

### ChartToTreeConverter

The ChartToTreeConverter retrieves the derivated trees from a Deduction object. Currently not from every parsing trace the derivation tree can be retrieved. In general the converter searches for the goal item and retrieves the steps that lead to this goal, while considering only the first sets of backpointers in each case, hence the first found tree is generated. Because the search starts with the goal item the steps are retrieved in reversed order. Depending on the algorithm the steps are considered either in the given or in reversed (hence flipped again) order. The names of the steps contain the applied rule. The rules are converted to trees and applied to each other in the respective order, in this way the whole derivated tree is build up and returned in bracket format.


#### Tree format

The same tree class that was created to represent the trees of a TAG is also used for representing derived trees as the result of the parsing process. For representing crossing edges as needed for sRCG parsing it had to be extended. Here is one example of a tree with crossing edges in bracket string format:
```
(S (Comp (dat<0> ))(VP (NP (Jan<1> ))(VP (NP (Piet<2> ))(VP (NP (de-kinderen<3> ))(V (zwemmen<6> )))(V (helpen<5> )))(V (zag<4> ))))
```
This format is similar to the first one, but each leaf is appended by an index in pointed brackets that indicate the real order of the leaf nodes. Hence the representation of crossing edges is limited to the lowest level. 

### Graphical output

Currently the graphical output consists of two classes: The ParsingTraceTable displays the same information that has been printed to the command line. It is a table containing all chart items, backpointers and applied rules' names, but contrary to the command line it can be resized and scrolled no matter the length. There is one feature for outputs of TAG algorithms only: If the cursor hovers a chart item, the corresponding tree rules is displayed with the same class that is used for the overall derived trees.

#### DisplayTree

This class takes a tree in bracket format as parameter and draws it on the canvas of a new window. Based on the number of brackets the highest depth of the tree is determined, which is used for a rough estimate of the window size. The tree object is parsed from the string representation. It starts to draw the tree with the root on top in the middle of the canvas. For each drawn node the coordinates where it was drawn are stored. For each child node of the current node the max width, that is the greatest number of nodes in one level below that node are retrieved and the horizontal space is divided according to this. Roughly speaking that means if a node has one child with 9 children itself (and no grand children) and one other child with one child, the first one gets 9/10 of the horizontal space, is drawn in the middle of that 9/10 and the remaining horizontal space is divided in the same way between its children. While the second child gets the remaining 1/10 of space. The vertical space is divided equally between the levels of the tree. When the position of a node that is not the root has been determined and its label was drawn, a line is drawn from above the label to below the parent node, which coordinates have already been stored. The trees of TAG rules are drawn in the same way, but along with their tree representation the string representation of their item is passed as second parameter. From the item form additional information is retrieved and drawn: Start and end indices of that item are displayed left and right of the canvas. Foot node indices are written below. A dot indicating a position in the tree is drawn at the respective node label, roughly considering the size of the label: For the CYK algorithm the dot can be below or above a node and for the Earley algorithms it can be at one of the four corners around a node label. For the derived tree of a sRCG algorithm which can have crossing edges the drawing of the tree is slightly different. If a leaf is encountered, it is not yet drawn, instead all leaves are drawn at last on the bottom level in the order their indices determine and lines are drawn from them to their parent. Because for the other nodes the space between them has been divided according to the widths of the subtrees below them, the changed order of the leaves may leave skewed trees behind.

### References

Marcus, Mitchell et. al: The Penn Treebank: annotating predicate argument structure. HLT '94 Proceedings of the workshop on Human Language Technology : Plainsboro, NJ, 1994. URL http://dl.acm.org/citation.cfm?id=1075835. – ISBN:1-55860-357-3 p. 114-119