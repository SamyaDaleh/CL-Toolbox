# Documentation

The CL-Toolbox is an open source implementation of the parsing-as-deduction 
approach. It takes a grammar file, a string containing the input sequence and a 
string that determines the parsing algorithm and puts out the trace of steps of
how the input was processed by that algorithm. According to the deduction 
approach a set of rules are used to derive a goal item. The deductions happens 
for every implemented parsing algorithm, only the underlying rules are changed.
This documentation gives details about the functionality of the CL-Toolbox. It 
is aimed at users who seek a deeper understanding of how the different parts 
work together.

## Folder Structure

As state of the art the folder `src` contains the source code of the 
application while `test` contains the JUnit tests that ensure everything works 
correctly. In folder `releases` the exported, runable jar is located which is 
replaced sometimes as well as the wrapper scripts explained in the next 
section. Finally the folder `resources` contains some example grammars that can
be used to parse inputs with this toolbox.

## Command line parameters

This chapter explains all command line parameters that can be used when 
requesting the CL-Toolbox. Usually the Cl-Toolbox.jar file is used that is 
provided in the folder releases. A basic call can be this:
```
java -jar CL-Toolbox.jar anbn.cfg "a a b b" cfg-topdown
```
java -jar is the common call to a jar file. After the jar file three parameters
are passed. If less parameters are passed, some help text is printed. The 
grammar file is a plain text file whose extension specifies the grammar type. 
See the sections below for details on the grammar formats. The input string to 
be parsed contains of space separated tokens and has to be put into quotes in 
order to be recognized as one string. The name of the parsing algorithm starts 
with the name of the formalism, a hyphen and the name of the algorithm itself. 
That is because for instance the Earley algorithm for CFG is something else 
than the one for TAG and the one for SRCG. Instead of just applying the 
algorithm of the same complexity as the grammar this approach allows the user 
to be specific about the algorithm and they can choose for example to let a CFG
grammar be parsed by a SRCG algorithm for comparison or if no fitting grammar 
is at hand for the more complex algorithm. However, calling the jar directly in
this way outputs question marks in place of special characters as they are used
by some algorithms. To display special characters correctly, the jar has to be 
called like this:
```
java -Dfile.encoding="UTF-8" -jar CL-Toolbox.jar anbn.cfg "a a b b" cfg-topdown
```
To reduce the typing to a minimum the user can instead use the CL-Toolbox.bat 
(Windows) or the CL-Toolbox.sh (*nix) scripts. They can be requested similar to
the jar with the same parameters and they pass on the request to the jar along 
with the encoding. Hence the call above is equivalent to the following:
```
.\CL-Toolbox.bat anbn.cfg "a a b b" cfg-topdown
```
Currently this only works if the user is located in the same folder as the jar 
and the script. The following is a list of the currently supported parsing 
algorithms. See later sections on details about what each one of them means and
how they work.
* ccg-deduction
* cfg-cyk
* cfg-cyk-extended
* cfg-cyk-general
* cfg-earley
* cfg-earley-bottomup
* cfg-earley-passive
* cfg-leftcorner
* cfg-leftcorner-chart
* cfg-topdown
* cfg-shiftreduce
* cfg-lr-k     (with k >=0)
* cfg-unger
* pcfg-astar
* tag-cyk
* tag-cyk-general
* tag-earley
* tag-earley-prefixvalid
* srcg-cyk
* srcg-cyk-extended
* srcg-earley

Additionally some optional flags can be added to the call. --success instead of 
printing the full trace it prints only items that lead to a goal item. If the 
parsing fails because the input sequence is not in the language of the grammar 
no steps are displayed. If steps are displayed they keep their original 
numbering, leaving gaps in the sequence of indices where items are left out. 
The second parameter is --please. When set and a grammar is not obviously not 
fitting for an algorithm, it is converted. Obvious conversions are performed 
regardless if the flag is set or not. That means for instance if a grammar is a
CFG and the algorithm is for TAG, the grammar is trivially converted, no matter
if the flag is set or not. If the grammar is CFG and the algorithm is for CFG 
the grammar still may not be appropriate. The CYK algorithm works only for 
grammars in Chomsky Normal Form. If the grammar is not in Chomsky Normal Form 
but shall be parsed with cfg-cyk, if the flag is not set the user will be 
informed that the grammar is not appropriate. If --please is set, the grammar 
is converted to Chomsky Normal Form before further steps are performed. See 
section GrammarToGrammarConverter in chapter Program Workflow for more details.
If --please is set and the grammar already fits the algorithm, it is not 
altered. The last parameter --latex prints the deduction table in a text format
instead of the space formatted one. --latex-graph prints the graph of 
computations in a format for tikz-dependency for some algorithms.

### Grammar formats

This section covers the different grammar formats that can be read from files. 
Different examples are located in directory resources/grammars. They all are 
plain text files where the file extension indicates the grammar type. Every 
grammar format is close to the respective formal definition. Ever component of 
the grammar tuple is defined. They are allowed to include the defintion of the 
tuple as well as it is displayed in the string representation, but it is 
ignored for parsing. The first symbol determines which component 
is defined. The components can be defined in every order and line breaks are 
allowed between any tokens. Production rules can be defined more than once in 
which case the new ones will be added to the existing once. No other component 
can be defined more than once. The different formalisms use different special 
characters, which are always treated as separate tokens and don't need to be 
separated by spaces from each other or from other grammar symbols. All other 
tokens (like terminals and nonterminals etc.) must be separated by spaces or 
commas from each other. The user can freely use both uppercase and lowercase 
strings both as terminals and nonterminals and even mixed. Using special 
characters in terminals or nonterminals is not possible at the moment. If 
parsing the grammar was successful a check for consistency is performed.

#### CCG

A Combinatory Categorial Grammar consists of only a lexicon with words and their
respective categories. The categories specify how they can be combined with one
another. Here is an example:
```
Trip	NP
merengue	NP
likes	(S\NP)/NP
certainly	(S\NP)/(S\NP)
```
The / specifies that the other category has to be found to the right, \ that it
expects it to the left. In this example ``likes`` demands a NP to the right and
afterwards another NP to the left to form a sentence. No other elements of the
grammar are specified. The algorithm expects ``S`` as start symbol and therefore
as last entry for the goal item.

#### CFG

A context-free grammar is the most commonly used grammar type. Most programming
languages fall into this category. Generally speaking it can be described as 
the one that "counts to two", which means it covers languages where there is 
two times the same amount of terminals, where the outermost symbols belong 
together. Here is an example:
```
N = {S}
T = {a, b}
S = S
P = {S -> a S b, S -> a b}
```
Nonterminals, terminals, start symbol and production rules can be defined in 
any order. Every line consists of a symbol indicating the type of the 
component: N - nonterminals, T - terminals, S - start symbol and P - production
rules. Each production rule must consist of a single nonterminal on the left 
hand side followed by "->". The tokens of the right hand side must be separated
by spaces, other spaces are optional. An empty string on the right hand side 
can be either represented as ε or by writing nothing. In the Cfg object 
terminals and nonterminals are represented as lists for better access despite 
they are sets according to the definition. This holds for all grammar formats 
where the definitions prescribe sets of anything. A CfgProductionRule is 
represented as object where the left hand side is a string and the right hand 
side is an array of strings. For CFG these special characters are defined: 
`-`, `>`, `{`, `}`, `,`, `|`, `=`. The test after the grammar parsing checks if
the following properties hold, however this does not mean that there is no way 
the grammar could break even after this point:
* all categories N, T, S and P have been specified
* no symbol is declared as both terminal and nonterminal
* each LHS symbol is declared as nonterminal
* each RHS symbol is declared either as terminal or nonterminal
The pipe symbol can be used to separate different RHS sides from each other 
that belong to the same LHS.

#### PCFG

A probabilistic CFG is like a CFG where every rule has a probability. The 
probability is specified in front of each rule before a colon. Otherwise the 
components are the same as for CFG. Here is an example.
```
N = {S, A, B}
T = {0, 1}
S = S
P = {1 : S -> A B, 0.7 : A -> 1, 0.3 : A -> 0, 0.6 : B -> BB, 0.4 : B -> 0}
```
The rules with the same nonterminal on the right hand side are supposed to add 
up to a probability of 1. However, as in most cases there is currently no check
if that is the case and it is up to the user to take care of that. Internally 
the Pcfg is an extension of the Cfg with the only difference that each rule has
a probability. The special characters are: `-`, `>`, `{`, `}`, `,`, `|`, `=`, 
`:`. The consistency check checks for the same properties.

#### TAG

A tree adjoining grammar is a weak context-sensitive formalism with higher 
complexity as a CFG. While a CFG can "count up to 2" a TAG can "count up to 4".
That means it can generate 4 sequences of terminals of the same length. Also it
is known to generate the copy language. It can generate some crossing 
dependencies found in natural languages while generating context-free trees, 
that are trees without crossing edges. Here is an example: 
```
N = {S}
T = {a, b, c, d}
S = S
I = {α1 : (S_OA ε)}
A = {β : (S_NA a (S b S* c) d)}
```
A TAG consists of nonterminals, terminals and a start symbol like a CFG. But 
instead of production rules its rules are two sets of trees: I - initial trees 
and A - auxiliary trees. The tree format is described below. An initial tree 
might replace a nonterminal leaf in another tree where that leaf has the same 
label as the root node of the initial tree. This function is called 
substitution and is similar to a derivation step of a CFG. An auxiliary tree 
might replace an inner node in another tree that has the same label as the so 
called foot and root nodes of that auxiliary tree. This function is called 
adjunction and gives the TAG a higher expressive power compared to a CFG. Each 
tree has a name that is specified in front of the colon. Usually initially 
trees are named α concatenated with a number and auxiliary trees β with a 
number. However, indeed the user is relatively free to give them any name that 
is no meta symbol like a colon. Internally the initial and auxiliary trees are 
stored in two separate maps with the tree names as keys. To represent trees 
there are different approaches. One considered was to build up a tree 
recursively of having a root label and a list of children which are trees or 
subtrees themselves. However, this implementation stays close to the 
definition, which states that a tree consists of sets of vertexes, directed 
edges between two vertexes each and one root node. An auxiliary tree also has 
one foot node. Here each tree also holds a list of nodes that are labeled "no 
adjoin" or "obligatory adjoin". Each vertex remembers its label and gorn 
address. The special characters for the overall TAG definition are: `-`, `>`, 
`{`, `}`, `,`, `=`, `:` and for the tree strings which use a different parser: 
`(`, `)`, `*`, `_`, `<`, `>`. The consistency check checks for the following:
* all categories N, T, S and I have been specified
* no symbol is declared as both terminal and nonterminal
* all tree labels are either declared as terminal, nonterminal or are epsilon
* nodes that are not leaves have no label that is declared as terminal.

##### Tree format

The context free trees, when not represented as Tree object, can be parsed from
and retrieved in bracket format as described by Mitchel (1994) Here is one 
example:
```
(T_NA (B (T a ε) ) (B_OA T*))
```
In this format each pair of brackets surround a sub tree. Each sub tree is 
denoted by a list of node labels, where the first label belongs to the parent 
and every other belongs to its children in that order. The labels may contain 
subscripts: NA says that no adjunction is possible in that node, while OA says 
that adjunction in this node is obligatory. An asterix denotes the foot node. 
Another exception is ε. If a node has label ε, instead it is labeled with the 
empty string.

#### SRCG

A simple Range Concatenation Grammar is a generalization of a CFG. While a CFG 
can generate up to 2 sequences of terminals of the same length, a sRCG can 
generate 2k, where k is the highest dimension of any rule in the grammar, that 
is the number of separate strings that are generated in a rule. Hence if k = 1,
the sRCG is equivalent to a CFG. Compared to a CFG a sRCG consists of the same 
four components and an additional set of variables V that can be used in every 
rule. Here is an example.
```
N = {A, B, S}
T = {a, b, c, d}
V = {X, Y, V, W}
P = {S( X V Y W ) -> A(X,Y) B(V,W), B(ε,ε) -> ε, B(b X,d Y) -> B(X,Y), A(a,c) -> ε, A(a X,c Y) -> A(X,Y)}
S = S
```
An empty string can be either represented as ε or by writing nothing. 
Internally the Srcg object contains the sets (lists) of terminals, 
nonterminals, variables and clauses. A clause consists of predicates. Similar 
to a CfgProductionRule a clause has one predicate on the left hand side and a 
list of predicates on the right hand side. A predicate is made up of one 
nonterminal and arguments consisting of elements, here represented as a two 
dimensional string. The special characters are: `-`, `>`, `{`, `}`, `,`, `|`, 
`=`, `<`, `(`, `)`. The consistency check checks for these properties:
* all categories N, T, V, S and P have been specified
* no symbol is declared as both terminal and nonterminal
* symbols used as nonterminals in clauses are declared as nonterminals
* symbols used in predicates are either declared as terminals, nonterminals or 
are epsilon.

## Program Workflow

This chapter explains the different processing stages that occur when the 
CL-Toolbox is requested.

### Main

The user request is send to the main method in the class cli.Main. This method 
sets the flag --success at the Deduction object. This methods calls all parts 
of the workflow in order. The following sections cover each part in the order 
of the data flow.

### GrammarParsers

They take a reader object which can be from a string or file or any other input 
source, creates a BufferedReader and passes that to the constructor the 
respective formalism to parse. Afterwards it performs the consistency check and
prints errors if some were found. Each constructor works in a similar way: 
they use the reader to read the next line, hence they can even handle large 
files. The line is tokenized according to the respective special characters and
they are treated or stored according to their meaning. If all tokens of a line 
were treated the next line is read if it exists.

### GrammarToGrammarConverter

This converter serves different purposes. One is that it checks if the grammar 
provided by the user can be handled by the parsing algorithm the user requests.
If the grammar is appropriate and the algorithm has the same complexity (e. g. 
the grammar is a CFG and the algorithm is CFG) the grammar is simply returned. 
If the algorithm has a higher complexity than the grammar (e. g. the grammar is
a CFG and the algorithm is for parsing TAGs), the grammar is converted to the 
higher formalism if possible. This happens regardless of --please is set or 
not. The idea is that automatic conversion happens if a conversion obviously 
needs be made, which is the case as the grammar file has a file extension that 
specifies its type as so does the algorithm. Other conversions are only made if
the user explicitly sets the --please flag. If this is set and the grammar is 
not appropriate to be parsed with an algorithm of the same complexity, 
conversion is performed. For instance the common CYK algorithm for CFG demands 
a grammar in Chomsky Normal Form. If a grammar is provided that is not in CNF 
and the --please flag is set, the grammar is converted to CNF. If a grammar has
both not the same complexity and is not appropriate for an algorithm, in case 
the --please flag is set all conversions are performed to make the grammar 
fitting for the algorithm. If a conversion is not possible, for instance if the
--please flag is not set or the grammar can't be converted to the complexity of
the algorithm, the process fails with a note to the user.

#### Conversions in Detail

This section covers how grammars are transformed into other equivalent 
grammars. For details on which transformation is performed for which parsing 
algorithm see the respective algorithm in section Deduction.

##### CFG to PCFG

For converting a CFG to a PCFG the number of rules with the same left hand 
symbol are determined and the probabilities are divided equally among them so 
they add up to 1.

##### CFG to TAG

To get an equivalent TAG for a CFG each production rule is converted to a tree 
where the left hand symbol is the root and the right hand side symbols are the 
children in the same order. Each tree becomes an initial tree in the new TAG. 
Hence each derivation step is now performed as substitution in the TAG.

##### CFG to SRCG

Each CfgProductionRule is converted to a clause. Each nonterminal symbol 
becomes the nonterminal symbol of a new predicate and new variables are added 
up to the amount of right hand side nonterminals, so all strings can be 
appended to one string in the left hand side. Each terminal in the right hand 
side is left away and put directly into the string of the left hand side.

##### PCFG to CFG

The conversion of a PCFG to a CFG is nothing else than tossing away all 
probabilities.

##### TAG to SRCG

The converion to an sRCG preserves the string language, but the derivated trees
change. Tree names in the TAG become nonterminal symbols in the sRCG. 
Nonterminals in the TAG are dismissed. The initial tree with start symbol as 
root becomes the start symbol in the sRCG. If there are several trees that 
qualify for the new start symbol, a new start symbol is introduced and clauses
of the form `S1(X) -> S(X)` are added for each initial tree where is name is S.
For each tree at least one clause is added, maybe more dependending on how many
and what adjunctions are possible in the tree and how many substitutions. 
Terminal symbols are simply included in the lhs of a clause in the same order as 
they appear in the tree. A substitution node can be seen as unknown material 
that will be provided by another tree. At this position a variable is included
with a corresponding predicate on the rhs where the nonterminal is the tree name
that can be substituted here and the same variable in the argument. One clause
like this is added for every possible tree that can be substituted here.
Similarly an adjunction can be seen as two points where unknown material from
another tree will be included, one left of the span below, one right of it. The
first variable is added to the lhs and the first half of the predicate is pushed
on a stack, afterwards whatever is in the span is handled first. When a node is
reached that is no child of the adjunction node, a second variable is added to
the lhs which is also used to complete the predicate that was pushed onto the 
stack. If adjunction is obligatory, only the clause with those variables is 
created. If adjunction is disallowed, no variables are added. If adjunction is
optional, both a clause with and one without variables are created to cover both
possibilities. If a foot node is encountered, hence the parts left and right of
it will not be consecutive to each other, a comma is added to the lhs to handle
the part right of the foot node in a new predicate. If a tree has neither
substitution nodes nor is any adjunction possible, no rhs predicates are created
hence it becomes an epsilon production.

Example: Consider a TAG with the following trees: `α: (S a (A b c) D)`, 
`β: (A a A* a)`, `ɣ: (D d)` In α in node A is an adjunction possible, hence 
there might or might not be other material before b and after c. In D a 
substitution is oligatory. In β in the root also an adjunction is possible. From
this TAG the following clauses of an sRCG are generated: `α(a b c X3) -> γ(X3)`,
`α(a X1 b c X2 X3) -> β(X1,X2) γ(X3)`, `β(a,a) -> ε`, `β(X1 a,a X2) -> β(X1,X2)`, 
`γ(d) -> ε`

##### CFG Binarization

When a CFG is binarized all rules with a right hand side of length greater than
2 are altered. The first symbol on the right hand side is kept and all others 
are replaced by a new nonterminal which is not yet in the grammar. It is named 
"X" and a number which is incremented until the symbol has not been seen 
before. A new rule with that new symbol on the left hand side and all remaining
symbols on the right hand side is added to the grammar. This process repeats 
until no rule is altered anymore.

##### CFG Removal of Chain Rules

First the set (list) of all unit pairs is determined, that are pairs of 
nonterminals that can be derived to the other by some derivation steps A ->+ B.
Then for each pair all right hand rule sides of B that are no chain rule are 
added as rules with A as left hand side.

##### CFG Removal of Empty Productions

All rules with an empty right hand side are removed. If it was the only rule of
the left hand side symbol, it is removed completely from all other right hand 
sides where it occurs. If it was not, then for every right hand side where this
nonterminal occurs a new rule is added with the same right hand side except 
without that symbol. The only empty production that is allowed to remain is the
one with the start symbol on the left hand side. If this rule exists and the 
start symbol is part of any right hand side, a new start symbol is created. Two
new rules are added to the grammar, one chain rule from the new start symbol to
the old one and a rule with the new start symbol as left hand side and the 
empty string as right hand side.

##### CFG Removal of Left Recursion

The algorithm removes both direct and indirect left recursion. For direct 
recursion it treats every rule where the first symbol on the right hand side is
the same as the one on the left hand side except if it is a unary production. 
For each nonterminal that is the left hand side of a left recursive rule it 
adds a new nonterminal appended to the rules without left recursion. For 
instance a grammar has the rules `S -> S a` which is left recursive and 
`S -> b` which is not. Then for each terminating rule a new rule like 
`S -> b S1` using a new nonterminal symbol is added, also `S1 -> ε`. For each 
recursive rule a new like `S1 -> a S1` is added. The former left recursive 
rules are removed. If a nonterminal is associated with only left recursive 
rules, an error message is printed and no conversion is performed.

For removing indirect left recursion epsilon productions have to be removed 
first. The algorithm assumes an order between the nonterminals. For each 
nonterminal, that is the lhs of a rule whose first rhs symbol is a previous 
nonterminal, it is replaced by all rhs of rules with those nonterminal as lhs. 
For example a grammar has the rules `S -> A a, S -> b, A -> S a` where the 
declared order of nonterminals is `[S, A]`. `S` is declared before `A`, hence 
rule `A -> S a` is handled and removed. The rhs without  the nonterminal, in 
this case `a`´, is appended to the rhs' of S-productions to form the new rules 
`A -> A a a, A -> b a`. One nonterminal is handled like this until the rules 
don't change anymore. Afterwards remaining direct left recursive rules are 
removed for that nonterminal. 

##### CFG have either one Terminal or Nonterminals on the Right Hand Side

Internally called `doReplaceTerminals` it is one of the steps needed for 
converting a grammar into Chomsky Normal Form: For each rule whose right hand 
side is longer than 1, all terminals are replaced by a new nonterminal for each
terminal and a new rule like `X1 -> a` is added. The algorithm is smart enough 
to use the same nonterminal for every occurence of the same terminal.

##### CFG Removal of Useless Symbols

The removal of useless symbols consists of two steps: The preferred first step 
is to remove all non-generating symbols. Generating symbols are initially all 
terminals and recursively all nonterminals whose right hand sides consist of 
only generating symbols, which are then declared as generating as well. Useless
nonterminals and rules using those are removed. If the start symbol happens to 
be non-generating, an error message is printed and null is returned. In a 
second step all non-reachable symbols can be removed. Reachable symbols 
initially include the start symbol and recursively all symbols that occur in 
right hand sides of rules where a generating symbol is on the left hand side. 
All rules with non-reachable symbols as left hand side and those symbols itself
are removed from the grammar.

##### TAG Binarization

The binarization of a Tree Adjoining Grammar changes the tree language, hence 
the resulting grammar generates the same string language, but not the same tree
language. A grammar is binarized similar to how it is done for a context-free 
grammar: For each node that has more than two children a new nonterminal node 
is introduced with a label that is not one of the existing nonterminals. This 
node becomes second child of the parent node and all its childen from 2 to n 
become children of the new node. This process is repeated for each tree until 
all trees are binarized.

##### SRCG Removal of Useless Rules

Similar to CFG Useless rules in sRCG are the ones that either do not lead to a 
terminating sequence of that can not be reached from the start symbol. Similar 
to CFG those respective sets (internally lists) of symbols are retrieved 
recursively until the set does not change anymore. In two steps, one for the 
nonterminating and one for the nongenerating symbols, a new sRCG is created 
where the useless rules are not copied to. Additionally, only used variables 
are copied and if a symbol within a predicate is not declared a variable, it is
added as a terminal. If one of the two steps does not leave any rules, null is 
returned. This test is called along with other conversion steps for sRCG when 
the please flag is set without checking if it's needed or not.

##### SRCG Binarization

The binarizaion of a simple range concatenation grammar follows an optimized 
approach. Similar to the binarization of a context free grammar for each clause
with more than 2 left hand side predicates a new predicate with a new 
nonterminal is introduced and replacing all but one predicate. However, which 
predicate is replaced depends on which solution creates the least arity and 
variables. For each clause that is altered a new reduced left hand side 
predicate replaces the old one. In a reduced rule the variables of a taken out 
predicate are removed and appearing or disappearing arguments have to be 
considered. Example: `A(a X1, X2, b X3) -> B(X2) C(X1) D(X3)` shall be 
binarized. The evaluation gives that it doesn't matter which predicate is used 
for reduction, so the first one is used. X2 stays in this clause, while the 
other variables also move to a new clause. The two clauses that replace this 
one are: `A(X1, X2, b X3) -> B(X2) B1(X1, X3)` and `B1(X1, X3) -> C(X1) D(X3)`.
For rhs with length greater than 2 it checks if two predicates can be reduced 
at the same time to get an even better performance.

##### SRCG Removal of Empty Productions

The removal of empty productions in simple range concatenation grammars can 
greatly increase the size of a grammar. First all epsilon candidates are 
determined, that are nonterminals followed by a vector that indicates which 
arguments can become epsilon. For instance `A(ε,a) -> ε` will yield the 
candidate `["A", "01"]`, because the first argument can be empty and the second
not, indicated by 0 and 1 respectively. In a next step all rules with the 
nonterminal of one on the candidates in the left hand side are altered so that 
the respective elements are empty. If a nonterminal is part of more than one 
candidate like `["A", "01"]` and `["A", "10"]` for each possibility new rules 
are created. If all arguments are always empty, the nonterminal and its 
occurences are removed completely. The epsilon rule itself is replaced by a one
without epsilon using the new nonterminal, in this example `A^01(a) -> ε`.

##### SRCG Ordering

In an ordered simple range concatenation grammar all variables in one right 
hand side predicate have to appear in the same order in the left hand side. All
nonterminals are extended with a vector of the length of their arguments that 
indicate the position, initially incrasing from 1 to the number of arguments. 
For each clause that is not ordered according to one right hand side predicate 
the positions of the arguments are swapped according to the order in the left 
hand side predicate. The new predicate in the right hand side also obtains a 
new label with respect to the previous order, for instance if in predicate 
`A^<1,2>(Y,X)` the arguments are swapped, the new predicate becomes 
`A<2,1>(X,Y)`. For each clause that has in this case `A^<1,2>` as left hand 
side nonterminal a new rule is added where those nonterminal is replaced by the
new label that indicates the order. This process repeats until the grammar does
not change anymore.

### GrammarToDeductionRulesConverter

If it is sure that the grammar is appropriate for the requested parsing 
algorithm, the grammar is send to the GrammarToDeductionRulesConverter which 
takes care of calling the right function from the other converter classes. 
There is one class for each grammar type that has a function for each 
implemented algorithm to create a parsing scheme with the rules for that 
algorithm depending on he parser. Each ParsingSchema object contains different 
deduction rules. Each deduction rule contains different items. An item is 
syntactically a string array and semantically it represents a configuration of 
the parsing process and the meaning of its entries highly depends on the 
algorithm. A parsing schema contains one or more goal items which have to be 
created in the deduction process iff the input string is generated by the 
grammar. A deduction rule consists a list of zero or more antecedence items 
which have to be found in order to use a rule, and a list of one or more 
consequence items, which are added to the set of items that can be used if all 
antecedence items were found. The parsing schema contains axioms, a list of 
deduction rules, here represented as so called StaticDeductionRules, because 
they don't change at runtime. Axioms have zero antecedence items and can be 
applied if there are no items yet. Also a parsing schema contains a list of 
rules, here of type DynamicDeductionRule. By definition they contain both 
antecedence and consequence items. However, in this implementation they change 
at runtime. When trying out a rule a list of antecedence items is passed to 
them and the rule tries to generate consequence items of them. There is one 
class for each kind of rule that belongs to a parsing algorithm. On creation of
the parsing schema one or more instances of a rule are added to the parsing 
schema. The aim is that each rule creates one kind of consequence items while 
staying close to the definitions. For instance for CFG CYK parsing there could 
be one axiom that just contains every generateable item, which would work and 
only need the creation of one rule, but this rule would be far from the 
definition. The definition says that if there is a rule A -> a from the set of 
production rules and a terminal in the input string at position i is a, the 
rule can be applied and one consequence item can be derived. Hence to stay 
close to the definition one rule instance is created for every matching 
production rule and input symbol, so each rule generates exactly one item. For 
dynamic production rules the decisions are the same although far less obvious, 
because they receive their antecedences at runtime. Still every instance of a 
dynamic production rule is responsible to generate one kind of output and no 
rule can generate different consequences from the same antecedence items. Every
rule has a name that may gives details about what its exact purpose is. Every 
rule has a string representation close to the formal definition where known 
parts may are filled out. That means if for instance a formal definition asks 
for any production rulle of form A -> a the string representation may contains 
the real nonterminal and terminal instead if the deduction rule is only 
responsible for that one production rule.

### Deduction

The Deduction object takes a parsing schema as input for performing the parsing
process, but not the input string. This is because the input string is either 
reflected by the generated deduction rules or is stored in the rules that need 
to consider it. The deduction object contains a chart that holds all generated 
items. A agenda that holds new items which will be used for creating new items.
A list of backpointers that say from which items a new item was generated and 
is empty for items derived with axioms. And a list of rule names that contains 
the names of the rules that were used to derive that item. According to the 
definition chart and agenda are sets, but they were implemented as lists to 
handle them in order and to make use of their indices. The item in chart at 
position i was derived with the backpointers from the backpointers list at 
position i with the rule in the rule list at position i. In the parsing process
first all lists are empty and all axiom rules are considered. New items are 
added to chart and agenda. After that initialization only the dynamic rules are
used. One item is retrieved and removed from the agenda, this item is tried out
with every dynamic rule. For every rule the number of antecedence items is 
retrieved and all possible combinations of the item and items from the chart of
the number of antecedences are passed to the rule and the consequences are 
retrieved. If consequences could be generated, it is checked if the new items 
are already in the chart. New items are added to chart and agenda along with 
the respective entries of backpointers and applied rule. If an item already 
exists in the chart, it is not added to the agenda. Usually new backpointers 
and the rule name are added to the existing entry. But for probabilistic 
algorithms that seek to find the best item, the new item might replace the old 
one and the backpointers and rule names are overwritten instead. The deduction 
process can look for the best item in terms of probability, that means the 
maximum, or the best item in terms of costs, that is the minimum. This is 
specified by setting the replace flag to "h" for high to get the maximum or to 
"l" for low to get the minimum. Items are retrieved and tried out from the 
agenda until the agenda is empty. Then the chart is checked if it contains one 
of the goal items and true is returned if one was found, else false. The 
deduction object still holds all information and can be requested to return the
trace. The function printTrace serves two purposes, which currently are both 
used at the same time so it is no need to separate them. One is that it goes 
through chart, backpointers and rules and pretty prints out either the full 
trace or the successful trace depending on wheather the flag is set or not. The
other purpose is that it returns the data as two dimensional array, so it can 
be passed to the graphical output.

#### Parsing Algorithms

This section covers the currently implemented parsing algorithm. See here which
properties an appropriate grammar must have in order to work with this 
algorithm, the working of the algorithm in general and its different rules.

##### CCG Deduction
This is the only algorithm for CCG and therefore just called "deduction", but
there are varieties that use different set of rules. This implementation uses
these deduction rules:

Axioms:<br/>
______________ where X ∈ f(w<sub>i+l</sub>)<br/>
[X, i, i + 1]<br/>
instantiated for every lexicon entry for each matching input token.

Forward Application:<br/>
[X/Y, i, j], [Y, j, k]<br/>
_________________<br/>
[X, i, k]<br/>
instantiated once.

Backward Application:<br/>
[Y, i, j], [X\Y, j, k]<br/>
______________<br/>
[X, i, k]<br/>
instantiated once.

Forward Composition 1:<br/>
[X/Y, i, j], [Y/Z, j, k]<br/>
__________________<br/>
[X/Z, i, k]<br/>
instantiated once.

Forward Composition 2:<br/>
[X/Y, i, j], [Y\Z, j, k] <br/>
__________________<br/>
[X\Z, i, k]<br/>
instantiated once.

Backward Composition 1:
[Y/Z, i, j], [X\Y, j, k]<br/>
_______________<br/>
[X/Z, i, k]<br/>
instantiated once.

Backward Composition 2:<br/>
[Y\Z, i, j] [X\Y, j, k] <br/>
_______________<br/>
[X\Z, i, k] <br/>
instantiated once.

Goal item: [S, O, n]

##### CFG Shift Reduce

Shift reduce or bottom-up parsing demands a grammar without epsilon 
productions. If a conversion occurs beside removing empty productions also 
useless symbols are removed. The algorithm starts from the input string and 
tries to combine the symbols to get the left hand side of the rules until the 
start symbol is left. It starts with an empty string and an index indicating 
how many input symbols have been shifted. That means if a "shift" occurs, one 
more input symbol is moved onto the stack and the index is increased. The other
rule called "reduce" takes stack symbol from the left or rather top if they 
match the complete right hand side of a production rule and replaces them by 
its left hand side. The outcome of the algorithm is a rightmost derivation. The 
deduction rules are:

Axiom:<br/>
_____<br/>
[ε, 0]<br/>
Instantiated once.

Shift:<br/>
[Γ, i]<br/>
_____ w<sub>i+1</sub> = a<br/>
[Γ a, i + 1]<br/>
Instantiated once.

Reduce:<br/>
[Γ α, i]<br/>
_____ A -> α ∈ P<br/>
[Γ A, i]<br/>
Instantiated for every production rule.

Goal item: [S, n]<br/>
Where n is the number of tokens in the input.

##### CFG LR(k)

LR(k) parsing with a k >=0 works like Shift Reduce parsing with a precombiled 
table that tells the algorithm which shift or reduce step to take next. k is 
the lookahead, the number of symbols following a rule to consider in the 
decision. The algorithm is deterministic, but works only with grammars that 
allow for deterministic LR(k) parsing with the respective k. If a conflict is 
detected when creating the parse table, the process is stopped. No grammar 
conversion is performed. The deduction rules are:

Initialize:<br/>
[q<sub>0</sub>, 0]

And one dynamic rules that performs lookups in the parse table and behaves like
one of the following rules:

Shift:<br/>
[Γ, i]<br/>
_____ w<sub>i+1</sub> = a<br/>
[Γ a q<sub>n</sub>, i + 1]

Reduce:<br/>
[Γ α<sub>1</sub> q<sub>m</sub> α<sub>2</sub> q<sub>m+1</sub> ... , i]<br/>
___________________ A -> α ∈ P<br/>
[Γ A q<sub>n</sub>, i]

Goal item: [q<sub>0</sub> S q<sub>n</sub>, n] <br/>
where S is the start symbol, qn an accepting state and n the length of the input. 

##### CFG CYK

For CYK parsing the grammar needs to be in Chomsky Normal Form. This means that
all rules either have exactly one terminal or two nonterminals as right hand 
side. No empty productions, chain rules, mixed or longer right hand sides are 
allowed. The algorithm follows a bottom-up approach, starting from the input 
string and moving upwards until the start symbol. With axiomatic scan rules 
production rules with one terminal are considered to reduce the input symbols. 
Then with complete rules and production rules with two nonterminals on the 
right hand side are used to combine two nonterminals and replacing them by the 
left hand side of the matching production rule. The items include the 
respective nonterminal, the start index of the span and its length. The 
deduction rules are:

Scan:<br/>
_________ A -> w<sub>i</sub> ∈ P<br/>
[A, i, 1]<br/>
Instantiated for every input token and every applying production rule.

Complete:<br/>
[B, i, l<sub>1</sub>], [C, i + l<sub>1</sub>, l<sub>2</sub>]<br/>
________________ A -> B C ∈ P<br/>
[A, i, l<sub>1</sub> + l<sub>2</sub>]<br/>
Instantiated for each production rule of that form.

Goal item: [S, 0, n]<br/>
Where S is the start symbol and n the length of the input.

##### CFG CYK Extended

This algorithm is an extension to the common CYK algorithm and works similar to
that one. The grammar is also allowed to contain chain rules, that are rules 
with one nonterminal on the right hand side. Because the removal of chain rules
increases the grammar size exponentially, this algorithm has the same parsing 
complexity regarding the input, but is much cheaper regarding grammar size. The
deduction rules are the same as CYK plus the following:

Complete unary:<br/>
[B, i, j]<br/>
_____ A -> B ∈ P<br/>
[A, i, j]<br/>
Instantiated for each production rule of that form.

##### CFG CYK General

CYK General works similar to the previously mentioned CYK algorithms, but it 
can handle any input grammar without restriction, which comes with worse 
performance, as the handling of any number of items for completing the right 
hand side of a rule is costly. The deduction rules include the same scan rule 
and goal item as for CYK plus:

Scan ε:<br/>
______ A -> ε ∈ P, i ∈ [0...n]<br/>
[A, i, 0]<br/>
instantiated for every i.

Complete:<br/>
[A<sub>1</sub>, i<sub>1</sub>, l<sub>1</sub>] ... [A<sub>k</sub>, i<sub>k</sub>, l<sub>k</sub>]<br/>
__________________________ A -> A<sub>1</sub> ... A<sub>k</sub> ∈ P, l = l<sub>1</sub> + ... + l<sub>k</sub>, 
i<sub>j</sub> = i<sub>j-1</sub> + l<sub>j-1</sub><br/>
[A, i<sub>1</sub>, l]<br/>
instantiated for every production rule.

##### CFG Earley

The early algorithm does not demand any properties of the grammar. It follows a
top-down approach where it predicts derivations and tries to match the symbols 
with the input starting from the beginning. It has a predict rule to try out 
production rules, a scan rule to match predicted terminals with symbols from 
the input string and a complete rule that matches the nonterminal of a rule 
whose right hand side has been completely seen with the nonterminal in another 
right hand side yet to see. Its items encode the rule where a dot marks up 
until which point the right hand side has been seen, an index saying where this
item starts and another index saying where the dot is located regarding the 
input string. The deduction rules are:

Axiom:<br/>
________________ S -> α ∈ P<br/>
[S -> •α, 0, 0]<br/>
instantiated for every production rule with the start symbol as lhs.

Scan:<br/>
[A -> α •a β, i, j]<br/>
_______________________ w<sub>j+1</sub> = a<br/>
[A -> α a •β, i, j + 1]<br/>
instantiated once.

Predict:<br/>
[A -> α •B β, i, j]<br/>
__________________ B -> γ ∈ P<br/>
[B -> •γ, j, j]<br/>
instantiated for every production rule.

Complete:<br/>
[A → α •B β, i, j], [B → γ•, j, k]<br/>
________________________________<br/>
[A → α B •β, i, k]<br/>
instantiated once.

Goal items: [S -> α •, 0, n]<br/>
for every production rule with the start symbol as lhs.

##### CFG Earley Bottom-up

This version processes the input from left to right like Earley, but starts at
the input string. Deduction rules are Scan, Complete and Goal item like the
usual Earley plus:

Initialize:<br/>
__________ A -> α ∈ P<br/>
[A -> •α, i, i]<br/>
instantiated for every production rule at every index 0 <= i < n.

##### CFG Earley Passive

This works like Earley with the difference that it uses passive items that are 
complete items that don't care which rule they are derived from. They are used 
in a new complete rule instead if the item with the dot at the end. Also this 
algorithm has a new convert rule that converts items with a dot at the end into
passive items. Deduction rules are Axiom, Scan and Predict like Earley plus:

Convert:<br/>
[B → γ •, j, k]<br/>
_________<br/>
[B, j, k]<br/>
instantiated once.

Complete:<br/>
[A → α •B β, i, j], [B, j, k]<br/>
_____________________<br/>
[A → α B •β, i, k]<br/>
instantiated once.

Goal item: [S, 0, n]

##### CFG Left Corner

For this version of left corner the grammar must not contain empty productions 
or loops, also called recursions. It combines a top-down with a bottom-up 
approach. Its items contain the symbols that are left to be bottom-up 
completed, starting with the input string, a second stack with symbols that are
left for top-down predictions containing the remaining right hand sides without
the left corners, starting with the start symbols and a third stack of left 
hand sides that are waiting to be completed, starting empty. A reduce step can 
be applied when the first symbol on the right hand side of a rule is the 
leftmost symbol of the first stack. The symbol is removed, the remaining symbol
of the right hand side are put onto the second stack and the left hand side 
symbol is put onto the third stack. Whenever symbols of a new production rule 
are added to the second step a dollar symbol is added first, which separates 
different right hand sides from each other.  A reduce step removes the topmost 
symbols on first and second stack if they both are the same. A move step can be
applied when a dollar sign is on top or better to say leftmost on the second 
stack, that means a complete right hand side of a rule has been seen and the 
left hand side nonterminal which is on top of the third stack is moved to the 
first stack. Deduction rules are:

Axiom:<br/>
____________<br/>
[w, S, ε]<br/>
instantiated once.

Reduce:<br/>
[X<sub>1</sub> α, B β, γ]<br/>
_______________________A → X<sub>1</sub> X<sub>2</sub> ... X<sub>k</sub> ∈ P, B ≠ $<br/>
[α, X<sub>2</sub> ... X<sub>k</sub> $ B β, A γ]<br/>
instantiated for every production rule.

Move:<br/>
[α, $ β, A γ]<br/>
_____________A ∈ N<br/>
[A α, β, γ]<br/>
instantiated once.

Remove:<br/>
[X α, X β, γ]<br/>
___________<br/>
[α, β, γ]<br/>
instantiated once.

Goal item: [ε, ε, ε]

##### CFG Left Corner Chart

This version of left corner works with any grammar and similar to the 
previously mentioned version, but its items look different. There are two types
of items: Passive items contain a symbol that has completely been seen while 
active items contain a production rule where a dot indicates up to which entry 
of the right hand side the rule has been seen. Both items also contain an index
that says where the item starts and its length. First all input symbols are 
turned to passive items including all possible empty strings. A reduce step now
is applied when the left corner of a production rule matches any passive item 
and produces an active item with the dot after that first item. Remove can be 
applied on matching passive and active items to move the dot over another 
symbol in the right hand side. Whenever a dot is located at the end of a right 
hand side, the whole rule is completed and can be turned to a passive item with
the left hand side symbol as entry. Deduction rules are:

Scan:<br/>
___________0 ≤ i ≤ n-1<br/>
[w<sub>i+1</sub>, i, 1]<br/>
instantiated for every token in the input.

Reduce:<br/>
[X, i, l]<br/>
____________A → X α ∈ P<br/>
[A → X •α, i, l]<br/>
instantiated for every production rule.

Remove:<br/>
[A → α •X β, i, l<sub>1</sub>], [X, j, l<sub>2</sub>]<br/>
__________________________j = i + l<sub>1</sub><br/>
[A → α X •β, i, l<sub>1</sub> + l<sub>2</sub>]<br/>
instantiated once.

Move:<br/>
[A → α X •, i, l]<br/>
______________<br/>
[A, i, l]<br/>
instantiated once.

Goal item: [S, 1, n]<br/>
with |w| = n.

##### CFG Top Down

For top down parsing a grammar must not contain empty production rules. Top 
down parsing is the simplest approach, following the definition of derivation, 
which begins with a start symbol and replaces each left hand side symbol that 
occurs in that string by the right hand side of a corresponding rule. The items
consist of two elements which are the working string and the length of the 
input that was already matched. Starting with the start symbol and length 0 a 
predict step does the derivation and replaces the nonterminal. The scan step 
matches the first symbol in the working string with the input in which case the
terminal is removed from the working string and the length is increased by 1.
Deduction rules are:

Axiom:<br/>
________<br/>
[S, 0]

Scan:<br/>
[a α, i]<br/>
__________w<sub>i+1</sub> = a<br/>
[α, i + 1]<br/>
instantiated once.

Predict:<br/>
[A α, i]<br/>
_________A → γ ∈ P<br/>
[γ α, i]<br/>
instantiated for every production rule.

Goal: [ε, n] with |w| = n

##### CFG Unger

This implementation of the Unger algorithm can not handle empty productions or 
recursion. The algorithm itself is costly with a factorial runtime. The 
algorithm follows a top down approach. Beginning with the start symbol when 
performing a prediction it tries out every possible separation to divide the 
span of the left hand side symbol among all symbols on the right hand side. 
However, it is smart enough to predict a length of 1 for a terminal symbol. The
items contain a single symbol along with a dot that indicates if the symbol has
been completely seen in which case the dot is after the symbol, else not and 
the dot is before. Also the items maintain an index indicating its start and 
its length. A predict step returns all items that can result from any 
separation, breaking with the assumption that a derivation rule shall only 
return one kind of items. On items that contain a terminal symbol the rule scan
can be applied. If the terminal matches the input string at the respective 
position the dot can be moved behind the terminal. Similar if for a production 
rule matching items with dots at their ends for every right hand side symbol 
are present the rule complete can move the dot after the corresponding 
nonterminal item with a matching span. The deduction rules are:

Axiom:<br/>
__________ |w| = n<br/>
[•S, 0, n]<br/>
instantiated once.

Scan:<br/>
[•a, i, i + 1]<br/>
_________w<sub>i+1</sub> = a<br/>
[a•, i, i + 1 ]<br/>
instantiated once.

Predict:<br/>
[•A, i<sub>0</sub>, i<sub>k</sub>]<br/>
_________________________________A → A<sub>1</sub> ... A<sub>k</sub> ∈ P, i<sub>j</sub> < i<sub>j+1</sub><br/>
[•A<sub>1</sub>, i<sub>0</sub>, i<sub>1</sub>], ... , [•A<sub>k</sub>, i<sub>k−1</sub>, i<sub>k</sub>]<br/>
instantiated for every production rule. It produces all possible separations as 
consequences.

Complete:<br/>
[•A, i<sub>0</sub>, i<sub>k</sub>], [A<sub>1</sub> •, i<sub>0</sub>, i<sub>1</sub>], ... , [A<sub>k</sub> •, i<sub>k−1</sub>, i<sub>k</sub>]<br/>
___________________________________________A → A<sub>1</sub> ... A<sub>k</sub> ∈ P<br/>
[A•, i<sub>0</sub>, i<sub>k</sub>]<br/>
instantiated for every production rule.

Goal item: [S •, 0, n] where n = |w|

##### PCFG A*

A* or A Star parsing is based on probabilistic CYK parsing. Hence it needs a 
probabilistic grammar in Chomsky Normal Form. Because probabilities are used a 
not fitting grammar can not be converted into a fitting one. The algorithm 
works like CYK parsing described above, differences affect the handling of the 
probabilities. The probabilities here are handled as weights instead, that is 
the absolute value of the logarithm of the probability. The main idea of the 
algorithm is beside the actual weight of the applied rules also to include an 
estimation of the outside weight. For comparison of the weights the overall 
weight of an item is considered while when deriving a new item only the rule 
weights are further used and the own outside weight of the new item is added to
that. The items beside the weight and the nonterminal like in CYK include the 
from and to indices rather then the length of the span. Deduction rules are:

Scan:<br/>
_______________________p : A → w<sub>i</sub><br/>
|log(p)| + out(A, i − 1, 1, n − i) : [A, i − 1, i]<br/>
instantiated for every token in the input and every matching production rule.

Complete:<br/>
x<sub>1</sub> + out(B, i, j − i, n − j) : [B, i, j], x<sub>2</sub> + out(C, j, k − j, n − k) : [C, j, k]<br/>
____________________________________________where p : A → B C ∈ P <br/>
x<sub>1</sub> + x<sub>2</sub> + |log(p)| + out(A, i, k − i, n − k) : [A, i, k]<br/>
instantiated for every matching production rule.

Goal item is the same as for PCFG CYK.

##### PCFG CYK

This is directly the probabilistic implementation of the CYK algorithm and 
needs a grammar in Chomsky normal form. Like A Star parsing the items include a
weight rather than the probability and items encode start and end indices.
Deduction rules are:

Scan:<br/>
__________________p : A → w<sub>i</sub><br/>
|log(p)| : [A, i − 1, i]<br/>
instantiated for every token in the input and every matching production rule.

Complete:<br/>
x<sub>1</sub> : [B, i, j], x<sub>2</sub> : [C, j, k]<br/>
________________p : A → B C<br/>
x<sub>1</sub> + x<sub>2</sub> + |log(p)| : [A, i, k]<br/>
instantiated for every matching production rule.

Goal item: [S, 0, n] with lowest weight.

##### TAG CYK

As a removal of chain rules for TAG would remove possible nodes for adjunction 
and therefore change the string language CYK Extended algorithm is used instead
when the user invokes CYK parsing.

##### TAG CYK Extended

The CYK algorithm for TAG needs a grammar loosely similar to Chomsky Normal 
Form but with some differences since this is a tree grammar: Every node is 
supposed to contain at most two child nodes. Beside that the algorithm handles 
leaf nodes labeled with the empty string and a terminal node might be the 
sibling of a nonterminal node. Like the CYK algorithm for CFG this one starts 
at the input string, following a bottom-up approach until it reaches the root 
node of the sentence. In lex-scan steps one item is created for every leaf node 
in the grammar whose label matches any of the input symbols. Similarly eps-scan
matches an epsilon label with any position in the input. For every foot node 
all possible spans over the input string each trigger a new item. A move-unary 
or move-binary moves up from one respectively two child nodes to their parent. 
With substitute the algorithm moves from the root node of one tree to the 
substitution node in another tree that has the same label. Adjoin, because of 
the bottom-up approach, is so to say following an adjunction backwards: When 
there is an auxiliary tree item in the root node and another item where the 
position is in a node with the same label and if the foot node indices match 
the span of the other item, it assumes that an adjunction has occured, adding 
the outer span of the auxiliary tree item to the other item. Contrary 
null-adjoin assumes that no adjunction has occured, only setting the state of 
the applied item marking that nothing else is left to do. The items include the
name of the tree they represent, a gorn address with some position marker 
either in the top (⊤) or bottom (⊥) position of a node, the beginning index of 
the items span, two indices of beginning and end of the foot node's span which 
is not set if no range has been determined or no foot node exists and finally 
the last index of the overall item span. Deduction rules are:

Lex-scan:<br/>
___________________l(γ, p) = w<sub>i+1</sub><br/>
[γ, p<sub>⊤</sub>, i, −, −, i + 1]<br/>
instantiated for every token in the input and every leaf in every tree where the
leaf has the same label as the token.

Eps-scan:<br/>
________________l(γ, p) = ε<br/>
[γ, p<sub>⊤</sub>, i, −, −, i]<br/>
instantiated for every leaf with an empty label in every tree at every position 
in the input.

Foot-predict:<br/>
___________________β ∈ A, p foot node address in β, i ≤ j<br/>
[β, p<sub>⊤</sub>, i, i, j, j]<br/>
instantiated for every auxiliary tree over every possible input span.

Move-unary:<br/>
[γ,(p · 1)<sub>⊤</sub>, i, f<sub>1</sub>, f<sub>2</sub>, j]<br/>
_________________________node address p · 2 does not exist in γ<br/>
[γ, p<sub>⊥</sub>, i, f<sub>1</sub>, f<sub>2</sub>, j]<br/>
instantiated once.

Move-binary:<br/>
[γ,(p · 1)<sub>⊤</sub>, i, f<sub>1</sub>, f<sub>2</sub>, k], [γ,(p · 2)<sub>⊤</sub>, k, f'<sub>1</sub>, f'<sub>2</sub>, j]<br/>
_______________________<br/>
[γ, p<sub>⊥</sub>, i, f<sub>1</sub> ⊕ f'<sub>1</sub>, f<sub>2</sub> ⊕ f'<sub>2</sub>, j]<br/>
instantiated once.

Null-adjoin:<br/>
[γ, p<sub>⊥</sub>, i, f<sub>1</sub>, f<sub>2</sub>, j]<br/>
________________________f<sub>OA</sub>(γ, p) = 0<br/>
[γ, p<sub>⊤</sub>, i, f<sub>1</sub>, f<sub>2</sub>, j]<br/>
instantiated once.

Substitute:<br/>
[α, ε<sub>⊤</sub>, i, −, −, j]<br/>
__________________l(α, ε) = l(γ, p)<br/>
[γ, p<sub>⊤</sub>, i, −, −, j]<br/>
instantiated once.

Adjoin:<br/>
[β, ε<sub>⊤</sub>, i, f<sub>1</sub>, f<sub>2</sub>, j], [γ, p<sub>⊥</sub>, f<sub>1</sub>, f'<sub>1</sub>, f'<sub>2</sub>, f<sub>2</sub>]<br/>
___________________β ∈ f<sub>SA</sub>(γ, p)<br/>
[γ, p<sub>⊤</sub>, i, f'<sub>1</sub>, f'<sub>2</sub>, j]<br/>
instantiated once.

Goal items: [α, ε<sub>⊤</sub>, 0, −, −, n] where α ∈ I<br/>
instantiated for every initial tree.

##### TAG CYK General

Works similar to the previous one, except that it handles arbitrary many child 
nodes by creating one instance of the rule for every number of child nodes 
found in the grammar. This also makes it less efficient. Deduction rules are the
same as for TAG CYK Extended, except move unary and move binary are replaced by:

Move general:<br/>
[γ,(p · 1)<sub>⊤</sub>, i<sub>1</sub>, f<sub>1</sub>, f<sub>2</sub>, k<sub>1</sub>], ... , 
[γ,(p · j)<sub>⊤</sub>, i<sub>j</sub>, f'<sup>j</sup><sub>1</sub>, f'<sup>j</sup><sub>2</sub>, k<sub>j</sub>]<br/>
__________________________________________________i<sub>l</sub> = i<sub>l-1</sub> + k<sub>l-1</sub><br/>
[γ, p<sub>⊥</sub>, i, f<sub>1</sub> ⊕ ... ⊕ f'<sup>j</sup><sub>1</sub>, f<sub>2</sub> ⊕ ... ⊕ f'<sup>j</sup><sub>2</sub>, j]<br/>
which is instantiated for every number of child nodes in any tree.

##### TAG Earley

The Earley algorithm like the one for CFG works with arbitrary grammars and 
follows a top-down approach while trying to match the input from the beginning 
to the end. However, this version of the algorithm also generates some unused 
items from other parts of the input, because it does not check if triggering 
items have been completed yet. It starts with initializing every initial tree 
that has the start symbol as root as item. The items consist of tree name, gorn
address of the current node, a position indicator being either la, lb, rb or 
ra, meaning "left above", "right below" etc. indices for the beginning of the 
item's span, the beginning of the foot node span, the end of the foot node span
and the end of the item span also an adjunction flag being either 0 or 1. The 
initial items start with position la. If no adjunction is performed, the 
position is just changed to lb. If adjunction is possible, a new item of the 
auxiliary tree that can be adjoined is created, initialized at its root node 
with position la. When an item has position lb with the rule move down a new 
item located in the first child node at position la can be created. When an 
item represents the lb position in a substitution node, a substitution can be 
predicted deriving a new item located in the root of the initial tree that can 
be substituted. An item that is located in la of a terminal leaf can trigger a 
scan step that moves the position to ra in case the leaf node's label matches 
the terminal in the respective position indicated by the end item span index, 
which is increased by 1. When an item represents a location in ra of a leaf 
that has a right sibling, rule move right derives an item for a position la in 
that sibling. When the process has continued until rb in a root node of any 
tree it may can be combined with an auxiliary tree in lb position of the foot 
node to complete the foot, add the indices of the foot span and to move to rb 
in those foot node. Items representing the same node in the same tree with 
matching indices and one in la while the other is in rb can be combined with 
complete node to get an ra item in the same node. With a tree item in root node
rb and an auxiliary tree item in root node ra adjoin can be completed and the 
span of the auxiliary tree item is added to the indices of the other tree item.
Finally, with an initial tree item in root node rb a substitution can be 
completed and a new item located in a substitution node in rb can be derived.
Deduction rules are:

Initialize:<br/>
_______________________α ∈ I, l(α, ε) = S<br/>
[α, ε, la, 0, −, −, 0, 0]<br/>
instantiated for every initial tree.

ScanTerm:<br/>
[γ, dot, la, i, j, k, l, 0]<br/>
__________________________l(γ, dot) = w<sub>l+1</sub><br/>
[γ, dot, ra, i, j, k, l + 1, 0]<br/>
instantiated for every token in the input and every leaf in every tree where the
leaf has the same label as the token.

Scan-ε:<br/>
[γ, dot, la, i, j, k, l, 0]<br/>
_________________________l(γ, dot) = ε<br/>
[γ, dot, ra, i, j, k, l, 0]<br/>
instantiated for every leaf with an empty label in every tree at every position 
in the input.

PredictAdjoinable:<br/>
[γ, dot, la, i, j, k, l, 0]<br/>
__________________________β ∈ f<sub>SA</sub>(γ, dot)<br/>
[β, ε, la, l, −, −, l, 0]<br/>
instantiated for every auxiliary tree.

PredictNoAdj:<br/>
[γ, dot, la, i, j, k, l, 0]<br/>
__________________________f<sub>OA</sub>(γ, dot) = 0<br/>
[γ, dot, lb, l, −, −, l, 0]<br/>
instantiated once.

PredictAdjoined:<br/>
[β, dot, lb, l, −, −, l, 0]<br/>
_______________________dot = foot(β), β ∈ f<sub>SA</sub>(γ, dot')<br/>
[γ, dot', lb, l, −, −, l, 0]<br/>
instantiated for every node in every tree.

CompleteFoot:<br/>
[γ, dot, rb, i, j, k, l, 0], [β, dot', lb, i, −, −, i, 0]<br/>
________________________________dot' = foot(β), β ∈ f<sub>SA</sub>(γ,dot')<br/>
[β, dot', rb, i, i, l, l, 0]<br/>
instantiated once.

CompleteNode:<br/>
[γ, dot, la, f, g, h, i, 0], [γ, dot, rb, i, j, k, l, sat?]<br/>
__________________________________l(β, dot) ∈ N<br/>
[γ, dot, ra, f, g ⊕ j, h ⊕ k, l, 0]
instantiated once.

Adjoin:<br/>
[β, ε, ra, i, j, k, l, 0], [γ, dot,rb, j, p, q, k, 0]<br/>
_________________________________________β ∈ f<sub>SA</sub>(γ, p)<br/>
[γ, dot, rb, i, p, q, l, 1]<br/>
instantiated once.

MoveDown: [γ, dot, lb, i, j, k, l, 0]<br/>
_______________________________γ(dot · 1) is defined<br/>
[γ, dot · 1, la, i, j, k, l, 0]<br/>
instantiated once.

MoveRight:<br/>
[γ, dot, ra, i, j, k, l, 0]<br/>
______________________γ(dot + 1) is defined<br/>
[γ, dot + 1, la, i, j, k, l, 0]<br/>
instantiated once.

MoveUp:<br/>
[γ, dot · m, ra, i, j, k, l, 0]<br/>
_______________________________γ(dot · m + 1) is not defined<br/>
[γ, dot, rb, i, j, k, l, 0]<br/>
instantiated once.

PredictSubst:<br/>
[γ, p, lb, i, −, −, i, 0]<br/>
________________________γ(p) a substitution node, α ∈ I, l(γ, p) = l(α, ε)<br/>
[α, ε, la, i, −, −, i, 0]<br/>
instantiated for every initial tree.

Substitute:<br/>
[α, ε, ra, i, −, −, j, 0]<br/>
______________________γ(p) a substitution node, α ∈ I, l(γ, p) = l(α, ε)<br/>
[γ, p, rb, i, −, −, j, 0]<br/>
instantiated for every substitution node in every tree.

Goal items: [α, ε, ra, 0, −, −, n, 0], α ∈ I<br/>
initialized for every initial tree where the root is labeled with the start 
symbol.

##### TAG Earley Prefix Valid

This prefix valid version works similar to the previous one but will not 
generate items that have not been predicted before. Its items contain an 
additional index before the start index which says at which position the 
adjunction was triggered. Also most index values can be `~` which stands for 
"does not matter". Most of the previous rules work the same despite handling 
the new index. Predict adjoined now takes two items instead of one while 
complete foot and adjoin now need three: Now they also need an item in la with 
most indices irrelevant that has triggered the process. Also some new rules 
were added: Convert la1 takes any item with position la and turns the start 
index and both foot node indices to `~` Convert la2 takes any item with 
position la and turns the new predict flag and all but the last indices to `~`.
The last new rule is called Convert rb and turns the predict flag and both foot
node indices to `~`. Deduction rules are:

Initialize:<br/>
____________________________ α ∈ I, l(α, ε) = S<br/>
[α, ε, la, 0, 0, -, -, 0, 0]<br/>
initialized for every intial tree where the root is labeled with the start 
symbol.

ScanTerm:<br/>
[γ, p, la, i<sub>γ</sub>, i, j, k, l, 0]<br/>
___________________________________ l(γ, p<sub>γ</sub> · p) = w<sub>l+1</sub><br/>
[γ, p, ra, i<sub>0</sub>, i, j, k, l + 1, 0]<br/>
initialized once.

Scan-ε:
[γ, p, la, i<sub>γ</sub>, i, j, k, l, 0]<br/>
_______________________________ l(γ, p<sub>γ</sub> · p) = ε<br/>
[γ, p, ra, i<sub>γ</sub>, i, j, k, l, 0]<br/>
initialized once.

Convert-rb:<br/>
[γ, p, rb, ~, i, j, k, l, 0]<br/>
________________________________<br/>
[γ, p, rb, ~, i, ~, ~, l, 0]<br/>
initialized once.

Convert-la I:<br/>
[γ, p, la, i<sub>γ</sub>, i, j, k, l, 0]<br/>
_______________________________<br/>
[γ, p, la, i<sub>γ</sub>, ~, ~, ~, l, 0]<br/>
initialized once.

Convert-la II:<br/>
[γ, p, la, i<sub>γ</sub>, i, j, k, l, 0]<br/>
_______________________________<br/>
[γ, p, la, ~, ~, ~, ~, l, 0]<br/>
initialized once.

PredictNoAdj:<br/>
[γ, p, la, i<sub>γ</sub>, i, j, k, l, 0]<br/>
_______________________________ f<sub>OA</sub>(γ, p) = 0<br/>
[γ, p, lb, i<sub>γ</sub>, l, -, -, l, 0]<br/>
initialized once.

PredictAdjoinable:<br/>
[γ, p, la, ~, ~, ~, ~, l, 0]<br/>
_____________________________ β ∈ f<sub>SA</sub>(γ, p)<br/>
[β, ε, la, l, l, -, -, l, 0]<br/>
initialized for every auxiliry tree.

PredictAdjoined:<br/>
[β, p<sub>f</sub>, la, i<sub>β</sub>, i, -, -, m, 0], [γ, p, la, i<sub>γ</sub>, ~, ~, ~, i<sub>β</sub>, 0]<br/>
__________________________________________________________________ β(p_f) foot node, β ∈ f_SA(γ, p)
[γ, p, lb, i<sub>γ</sub>, m, -, -, m, 0]<br/>
initialized once.

CompleteFoot:<br/>
[γ, p, rb, ~, ~, i, ~, ~, l, 0], [β, p<sub>f</sub>, la, i<sub>β</sub>, m, -, -, i], [γ, p, la, ~, ~, ~, ~, i<sub>β</sub>, 0]<br/>
_____________________________________________________________________________________________ β(p<sub>f</sub>) foot node, β ∈ f<sub>SA</sub> (γ, p)<br/>
[β, p<sub>f</sub>, rb, ~, m, i, l, l, 0]<br/>
initialzed once.

Adjoin:<br/>
[β, ε, ra, i<sub>β</sub>, i<sub>β</sub>, j, k, l, 0], [γ, p, rb, ~, j, g, h, k, 0], [γ, p, la, ~, ~, ~, ~, i<sub>β</sub>, 0]<br/>
___________________________________________________________________________________________ β ∈ f<sub>SA</sub>(γ, p)<br/>
[γ, p, rb, ~, i<sub>β</sub>, g, h, l, 1]<br/>
initialized once.

CompleteNode:<br/>
[γ, p, la, i<sub>γ</sub>, f, g, h, i, 0], [γ, p, rb, ~, i, j, k, l, adj]<br/>
_____________________________________________________________ l(β, p) ∈ N<br/>
[γ, p, ra, i<sub>γ</sub>, f, g ⊕ j, h ⊕ k, l, 0]<br/>
initialized once.

MoveDown:<br/>
[γ, p, lb, i<sub>γ</sub>, i, j, k, l, 0]<br/>
_________________________________<br/>
[γ, p · 1, la, i<sub>γ</sub>, i, j, k, l, 0]<br/>
initialized once.

MoveRight:<br/>
[γ, p, ra, i<sub>γ</sub>, i, j, k, l, 0]<br/>
______________________________<br/>
[γ, p + 1, la, i<sub>γ</sub>, i, j, k, l, 0]<br/>
initialized once.

MoveUp:<br/>
[γ, p · m, ra, i<sub>γ</sub>, i, j, k, l, 0]<br/>
__________________________________ γ(p · m + 1) is not defined<br/>
[γ, p, rb, ~, i, j, k, l, 0]<br/>
initialized once.

PredictSubstituted:<br/>
[γ, p, la, ~, ~, ~, ~, i, 0]<br/>
_____________________________ α ∈ I, γ(p) substitution node, l(γ, p) = l(α, ε)<br/>
[α, ε, la, i, i, -, -, i, 0]<br/>
initialized for every initial tree.

Substitute:<br/>
[γ, p, la, ~, ~, ~, ~, i, 0], [α, ε, ra, i, i, -, -, j, 0]<br/>
________________________________________________________ α ∈ I, γ(p) substitution node, l(γ, p) = l(α, ε)<br/>
[γ, p, rb, ~, i, -, -, j, 0]<br/>
initialized once.

Goal items: [α, ε, ra, 0, 0, -, -, n, 0] <br/>
for every initial tree with start symbol as root.

##### SRCG CYK

As chain rules in sRCG may add to the word and removing chain rules would 
change the string language CYK Extended algorithm is used instead when the user
invokes CYK parsing.

##### SRCG CYK Extended

Similar to the CYK algorithm for CFG this algorithm demands that no clause in 
the grammar has more than two predicates on the right hand side, also no 
epsilon rules are allowed, that are rules with an empty right hand side and the
empty string as one or more left hand side predicate arguments. The working is 
also the same as for every CYK variant: Scan steps map terminating rules with 
strings from the input, with the difference that here different strings in one 
predicate might not precede each other. After that complete steps combine items
that match the right hand sides of a rule to derive an item that represents the
left hand side of the rule. The items consist of a nonterminal and a span 
vector that stores start and end span of every argument of the left hand side.
Deduction rules are:

Scan:<br/>
______________A(w<sub>i+1</sub>) → ε ∈ P<br/>
[A, <<i, i + 1>>]<br/>
instantiated for every clause with empty rhs over all possible spans.

Unary:<br/>
[B, ρ-vec]<br/>
___________A(α-vec) → B(α-vec) ∈ P<br/>
[A, ρ-vec]<br/>
instantiated for every unary production rule.

Binary:<br/>
[B, ρ<sub>B</sub>-vec], [C, ρ<sub>C</sub>-vec]<br/>
___________________A(ρ<sub>A</sub>-vec) → B(ρ<sub>B</sub>-vec) C(ρ<sub>C</sub>-vec) is a range instantiated rule<br/>
[A, ρ<sub>A</sub>-vec]<br/>
instantiated for every binary production rule.

Goal item: [S, <<0, n>>]

##### SRCG CYG General

This algorithm works the same as the previous one except that it handles any 
grammar that contains no rules with empty right hand side arguments and thus is
very expensive. Deduction rules are:

Axioms (scan):<br/>
_____________A(ρ-vec) → ε a range instantiated rule<br/>
[A, ρ-vec]<br/>
instantiated for every clause with empty rhs.

Complete:<br/>
[A<sub>1</sub>, ρ<sub>1</sub>-vec], ... , [A<sub>m</sub>, ρ<sub>m</sub>-vec]<br/>
_______________________A(ρ-vec) → A<sub>1</sub>(ρ<sub>1</sub>-vec), ... , 
A<sub>m</sub>(ρ<sub>m</sub>-vec) a range instantiated rule<br/>
[A, ρ-vec]<br/>
instantiated for every production rule where the rhs is not empty.

Goal item is the same as for SRCG CYK Extended.

##### SRCG Earley

Unlike the previously seen Earley algorithms this implementation can not handle
empty productions and assumes that the grammar is ordered. Despite that it 
still works top-down matching the input string starting at the beginning, with 
the difference that an item has to be suspended whenever the end of an argument
is reached, so the string in between can be parsed before the next argument. 
There are two kinds of items: active and passive ones. Active items consist of 
a clause where a dot in the left hand side marks up to which position the 
arguments have been matched with the input string. One index says how much of 
the input has been matched. Two more indices say where the dot is located in
the lhs. After that a range vector which is as long as the number of elements 
in the lhs times 2 say for each lhs element which span it has. Passive items 
consist of a nonterminal whose span has fully been seen and two indices per 
argument give the span for each argument. As usual the chart is initialized 
with every rule that has the start symbol as lhs nonterminal with the dot 
located at the beginning and no ranges are known. If a dot is located before a
variable a predict step triggers a new item for each production rule whose lhs
nonterminal is the same as for the rhs predicate where the variable belongs 
to. If the dot is located before a terminal, a scan step moves the dot over 
the terminal and updates the respective indices. If the dot reaches the end of
an argument that is not the last one, the item is suspended and the parsing 
process returns to the clause that triggered the prediction, moving the dot 
over the variable that has been matched with the input. A resume step with 
both the suspended item and the one that now wants to process the variable 
-returns to the next argument in the lower item. If a dot reaches the end of 
the last argument of a lhs, the items has been fully seen and is converted to 
a passive item while the spans for each element are converted to spans for 
each argument. Finally a complete step combines a passive item with an active 
item to move the dot over the last missing variable belonging to that 
nonterminal. Deduction rules are:

Initialize:<br/>
____________________________S(φ-vec) → Φ-vec ∈ P<br/>
[S(φ-vec) → Φ-vec, 0, <1, 0>, ρ<sub>init</sub>-vec]<br/>
instantiated for every production rule with the start symbol as nonterminal in 
the lhs.

Scan:<br/>
[A(φ-vec) → Φ-vec, pos, <i, j>, ρ-vec]<br/>
______________________________________φ-vec(i, j + 1) = w<sub>pos+1</sub><br/>
[A(φ-vec) → Φ-vec, pos + 1, <i, j + 1>, ρ'-vec]<br/>
instantiated once.

Predict:<br/>
[A(φ-vec) → ... B(X, ...) ... , pos, <i, j>, ρ<sub>A</sub>-vec]<br/>
___________________________________where φ-vec(i, j + 1) = X, B(ψ-vec) → Ψ-vec ∈ P<br/>
[B(ψ-vec) → Ψ-vec, pos, <1, 0>, ρ<sub>init</sub>-vec]<br/>
instantiated for every clause where the rhs is not empty.

Suspend:<br/>
[B(ψ-vec) → Ψ-vec, pos', <i, j>, ρ<sub>B</sub>-vec], [A(φ-vec) → ... B(ξ-vec) ... , pos, <k, l>, ρ<sub>A</sub>-vec]<br/>
_________________________________________________________________<br/>
[A(φ-vec) → ... B(ξ-vec) ... , pos', <k, l + 1>, ρ-vec]<br/>
instantiated once.

Convert:<br/>
[B(ψ-vec) → Ψ-vec, pos, <i, j>, ρ<sub>B</sub>-vec]<br/>
__________________________________|ψ-vec(i)| = j, |ψ-vec| = i, ρ<sub>B</sub>(ψ-vec) = ρ<br/>
[B, ρ]<br/>
instantiated once.

Resume:<br/>
[A(φ-vec) → ... B(ξ-vec) ... , pos, <i, j>, ρ<sub>A</sub>-vec], [B(ψ-vec) → Ψ-vec, pos', <k − 1, l>, ρ<sub>B</sub>-vec]
____________________________________________<br/>
[B(ψ-vec) → Ψ-vec, pos, <k, 0>, ρ<sub>B</sub>-vec]<br/>
instantiated once.

Goal Item: [S(φ-vec) → Φ-vec, n, <1, j>, ψ] with |φ-vec(1)| = j (i.e., dot at 
the end of lhs argument).


### Graphical output

Currently the graphical output consists of two classes: The ParsingTraceTable 
displays the same information that has been printed to the command line. It is 
a table containing all chart items, backpointers and applied rules' names, but 
contrary to the command line it can be resized and scrolled no matter the 
length. There is one feature for outputs of TAG algorithms only: If the cursor 
hovers a chart item, the corresponding tree rules is displayed with the same 
class that is used for the overall derived trees.

#### Tree format

The same tree class that was created to represent the trees of a TAG is also 
used for representing derived trees as the result of the parsing process. For 
representing crossing edges as needed for sRCG parsing it had to be extended. 
Here is one example of a tree with crossing edges in bracket string format:
```
(S (Comp (dat<0> ))(VP (NP (Jan<1> ))(VP (NP (Piet<2> ))(VP (NP (de-kinderen<3> ))(V (zwemmen<6> )))(V (helpen<5> )))(V (zag<4> ))))
```
This format is similar to the first one, but each leaf is appended by an index 
in pointed brackets that indicate the real order of the leaf nodes. Hence the 
representation of crossing edges is limited to the lowest level. 

#### DisplayTree

This class takes a tree in bracket format as parameter and draws it on the 
canvas of a new window. Based on the number of brackets the highest depth of 
the tree is determined, which is used for a rough estimate of the window size. 
The tree object is parsed from the string representation. It starts to draw the
tree with the root on top in the middle of the canvas. For each drawn node the 
coordinates where it was drawn are stored. For each child node of the current 
node the max width, that is the greatest number of nodes in one level below 
that node are retrieved and the horizontal space is divided according to this. 
Roughly speaking that means if a node has one child with 9 children itself (and
no grand children) and one other child with one child, the first one gets 9/10 
of the horizontal space, is drawn in the middle of that 9/10 and the remaining 
horizontal space is divided in the same way between its children. While the 
second child gets the remaining 1/10 of space. The vertical space is divided 
equally between the levels of the tree. When the position of a node that is not
the root has been determined and its label was drawn, a line is drawn from 
above the label to below the parent node, which coordinates have already been 
stored. The trees of TAG rules are drawn in the same way, but along with their 
tree representation the string representation of their item is passed as second
parameter. From the item form additional information is retrieved and drawn: 
Start and end indices of that item are displayed left and right of the canvas. 
Foot node indices are written below. A dot indicating a position in the tree is
drawn at the respective node label, roughly considering the size of the label: 
For the CYK algorithm the dot can be below or above a node and for the Earley 
algorithms it can be at one of the four corners around a node label. For the 
derived tree of a sRCG algorithm which can have crossing edges the drawing of 
the tree is slightly different. If a leaf is encountered, it is not yet drawn, 
instead all leaves are drawn at last on the bottom level in the order their 
indices determine and lines are drawn from them to their parent. Because for 
the other nodes the space between them has been divided according to the widths
of the subtrees below them, the changed order of the leaves may leave skewed 
trees behind.

## Development

This chapter mentions the steps necessary to extend different parts of the 
Toolbox.

### Adding a new Parsing Algorithm

How to add a new parsing algorithm to the Toolbox:
1. You might need to create a grammar parser first if you use a new formalism. 
For this you need to make up your mind about grammar format and internal data 
structure. The data structure shall closely resemble the definition and use its
vocabulary.
2. Create deduction rule classes (without axioms):
    1. Create a new package for the algorithm at a reasonable position within 
    `chartparsing` to keep the Toolbox well-structured.
    2. Create new classes:
        1. They must extent `AbstractDynamicDeductionRule` in one or the other 
        way.
        2. If the rule has two antecendences prefer to extend 
        `AbstractDynamicDecutionRuleTwoAntecedences` instead.
        3. Create a public constructor. It must set `name` to identify the rule
        and `antNeeded` to tell the calling deduction class how many antecendes
        this rule needs. You can pass the constructor more data that the rule 
        needs.
        4. Implement `getConsequences()`, this shall calculate the consequence 
        items based on the antecedences the rule currently has. The 
        consequences will be an empty list when `getConsequences()` is called, 
        so if no consequences can be calculated return those empty list.
        5. If the rule needs several antecendences, prefer to implement 
        `calculateConsequences()` that takes items as arguments and can be 
        called by `getConsequences()` several times with different item order. 
        If you implement `AbstractDynamicDecutionRuleTwoAntecedences` this is 
        already done for you and you just need to implement 
        `calculateConsequences()` that stores generated consequences in the 
        `consequences` list.
        6. Override `public String toString()` to include the definition of the
        rule. This is very useful in debug mode where you can directly see what
        the rule does instead of an abstract memory pointer. Also it might help
        you when you implement the calculation of consequences, so feel free to
        add actual information the rule has to its string representation, like 
        the production rule it handles.
        7. If you create the consequence items create any objects of type 
        `ChartItemInterface`. `DeductionChartItem` is an alrounder for symbolic
        parsing and `PcfgCykItem` is currently the only implementation of 
        `ProbabilisticChartItemInterface` for probabilistic parsing.
2. In the respective `ToDeductionRulesConverter` create a method that takes 
your grammar formalism and the input string as input and returns a 
`ParsingSchema` object. Create `DynamicDeductionRule` objects and add them as 
rules. Create `StaticDeductionRule` objects, give them a name and add them as 
axioms. Add an Item as Goal.
3. Add the new algorithm to `GrammarToDeductionRulesConverter` with a name it 
should be callable in the command line interface.
4. Add the algorithm with the same name to `GrammarToGrammarConverter`. If 
there are restrictions on the grammar format check for them here and add a 
conversion strategy for the please-switch.
5. In `Main` if you implemented a new formalism add a call to the grammar 
parser and calls to the respective convert methods here. In any case update the
`printHelp()` method to inform the user about the new option.
6. In `MainTest` add a call to the new algorithm.
7. If possible extend items to keep track of the trees derived so far. 
Deduction still works without them, but you will not see a derived tree at the 
end.


## References

### A* for PCFG

Kallmeyer, Laura: A* Parsing (Parsing). Düsseldorf, Wintersemester 16/17. URL 
[https://user.phil.hhu.de/~kallmeyer/Parsing/a-star-parsing.pdf](https://user.phil.hhu.de/~kallmeyer/Parsing/a-star-parsing.pdf) – last checked 2017-05-27, p. 18

### CFG: Removing left recursion

Moore, Robert C. (2000): Removing left recursion from context-free grammars. 
In: Proceedings of the 1st North American chapter of the Association for 
Computational Linguistics conference. Association for Computational Linguistics, 
p. 249–255. URL [https://www.aclweb.org/anthology/A00-2033](https://www.aclweb.org/anthology/A00-2033) - last checked 2019-06-03

### CFG: Removing useless symbols, Removing epsilon productions, removing unary productions, replacing terminals, removing chain rules

Hopcroft, John E. ; Motwani, Rajeev ; Ullman, Jeffrey D.: Einführung in die 
Automatentheorie, formale Sprachen und Komplexitätstheorie. 2nd Ed. München 
[u.a.] : Pearson Studium, 2002 (Informatik). -- ISBN 3-8273-7020-5, p. 298-313

### CYK for CFG

Kallmeyer, Laura: Cocke Younger Kasami (Parsing). Düsseldorf, Wintersemester 
16/17. URL [https://user.phil.hhu.de/~kallmeyer/Parsing/cyk.pdf](https://user.phil.hhu.de/~kallmeyer/Parsing/cyk.pdf) – last checked 2017-05-27, p. 12

### CYK for sRCG

Kallmeyer, Laura: LCFRS Parsing (Parsing Beyond Context-Free Grammars). 
Düsseldorf, Sommersemester 2016. URL [https://user.phil.hhu.de/~kallmeyer/ParsingBeyondCFG/lcfrs-parsing.pdf](https://user.phil.hhu.de/~kallmeyer/ParsingBeyondCFG/lcfrs-parsing.pdf) – last checked 2017-27-05 p. 8

### CYK for TAG

Kallmeyer, Laura: Parsing beyond context-free grammars. Heidelberg : Springer, 
2010 (Cognitive technologies). – ISBN 978-3-642-14845-3 S. 78-82

Kallmeyer, Laura: Tree Adjoining Grammar Parsing (Parsing Beyond Context-Free 
Grammars). Düsseldorf, Sommersemester 2016. URL [https://user.phil.hhu.de/~kallmeyer/ParsingBeyondCFG/tag-parsing.pdf](https://user.phil.hhu.de/~kallmeyer/ParsingBeyondCFG/tag-parsing.pdf) – last checked 2017-27-05 p. 11-19

### Converting a TAG to SRCG

Pierre Boullier. On TAG and Multicomponent TAG Parsing. [Research Report] RR-3668, INRIA.
1999 <inria-00073004> 
URL [https://hal.inria.fr/inria-00073004/document](https://hal.inria.fr/inria-00073004/document)

### Deduction for CCG

Stuart M. Shieber, Yves Schabes, Fernando C.N. Pereira,
Principles and implementation of deductive parsing,
The Journal of Logic Programming,
Volume 24, Issues 1–2,
1995,
Pages 3-36,
ISSN 0743-1066,
https://doi.org/10.1016/0743-1066(95)00035-I.
(http://www.sciencedirect.com/science/article/pii/074310669500035I)

### Earley for CFG

Kallmeyer, Laura: Earley Parsing (Parsing). Düsseldorf, Wintersemester 16/17. 
URL [https://user.phil-fak.uni-duesseldorf.de/~kallmeyer/Parsing/earley.pdf](https://user.phil-fak.uni-duesseldorf.de/~kallmeyer/Parsing/earley.pdf) – last checked 2017-05-27, p. 6-8

### Earley Bottom-up

Sikkel, Klaas:Parsing schemata : a framework for specification and analysis of 
parsing algorithms. Berlin : Springer, 
1997 (Texts in theo­re­ti­cal com­pu­ter sci­ence). – ISBN 978-3-540-61650-4 p. 79

### Earley for sRCG

Kallmeyer, Laura: Parsing beyond context-free grammars. Heidelberg : Springer, 
2010 (Cognitive technologies). – ISBN 978-3-642-14845-3 p. 151-153

Kallmeyer, Laura: LCFRS Parsing (Parsing Beyond Context-Free Grammars). 
Düsseldorf, Sommersemester 2016. URL [https://user.phil.hhu.de/~kallmeyer/ParsingBeyondCFG/lcfrs-parsing.pdf](https://user.phil.hhu.de/~kallmeyer/ParsingBeyondCFG/lcfrs-parsing.pdf) – last checked 2017-27-05 p. 16-22

### Earley for TAG

Kallmeyer, Laura: Parsing beyond context-free grammars. Heidelberg : Springer, 
2010 (Cognitive technologies). – ISBN 978-3-642-14845-3 S. 85-90

Kallmeyer, Laura: Tree Adjoining Grammar Parsing (Parsing Beyond Context-Free 
Grammars). Düsseldorf, Sommersemester 2016. URL [https://user.phil.hhu.de/~kallmeyer/ParsingBeyondCFG/tag-parsing.pdf](https://user.phil.hhu.de/~kallmeyer/ParsingBeyondCFG/tag-parsing.pdf) – last checked 2017-27-05 p. 28-36

### Extended CYK for CFG

Satta, Giorgio: Part III: CKY Parsing (Everything you always wanted to know 
about parsing). Düsseldorf, August 2013, p. 32-33

### Left Corner for CFG

Kallmeyer, Laura: Left-Corner Parsing (Parsing). Düsseldorf, Wintersemester 
16/17. URL [https://user.phil.hhu.de/~kallmeyer/Parsing/left-corner.pdf](https://user.phil.hhu.de/~kallmeyer/Parsing/left-corner.pdf) – last checked 2017-05-27, p. 6-8

### LR(k) for CFG

Kallmeyer, Laura: LR Parsing (Parsing). Düsseldorf, Wintersemester 2017/18. 
URL [https://user.phil-fak.uni-duesseldorf.de/~kallmeyer/Parsing/lr.pdf](https://user.phil-fak.uni-duesseldorf.de/~kallmeyer/Parsing/lr.pdf) – last checked 2018-08-19

### Shift-Reduce for CFG

Kallmeyer, Laura: Shift Reduce Parsing (Parsing). Düsseldorf, Wintersemester 
16/17. URL [https://user.phil.hhu.de/~kallmeyer/Parsing/shift-reduce.pdf](https://user.phil.hhu.de/~kallmeyer/Parsing/shift-reduce.pdf) – last checked 2017-05-27, p. 12

### Top-Down for CFG

Kallmeyer, Laura: Parsing as Deduction (Parsing). Düsseldorf, Wintersemester 
16/17. URL [https://user.phil.hhu.de/~kallmeyer/Parsing/deduction.pdf](https://user.phil.hhu.de/~kallmeyer/Parsing/deduction.pdf) – last checked 2017-05-27, p. 18-19

### Tree String Format

Marcus, Mitchell et. al: The Penn Treebank: annotating predicate argument 
structure. HLT '94 Proceedings of the workshop on Human Language Technology : 
Plainsboro, NJ, 1994. URL http://dl.acm.org/citation.cfm?id=1075835. – 
ISBN:1-55860-357-3 p. 114-119

### Unger for CFG

Kallmeyer, Laura: Parsing as Deduction (Parsing). Düsseldorf, Wintersemester 
16/17. URL [https://user.phil.hhu.de/~kallmeyer/Parsing/deduction.pdf](https://user.phil.hhu.de/~kallmeyer/Parsing/deduction.pdf) – last checked 2017-06-15, p. 12-13
