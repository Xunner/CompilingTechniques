# Compiling Techniques Lab 2

## Motivation/Aim 

​	编写、调试⼀个简易SQL语法分析程序，对基础SQL Token序列进⾏语法分析，从⽽更好理解语法分析原理；深⼊体会LR(1)文法转化为GOTO图、GOTO图转化为语法分析表、语法分析表转化为后续代码的过程。 

## Content description

​	程序⽤Java编写。程序接收Lab 1的词法分析程序传来的Token序列，对其进⾏语法分析。此程序实现了对SQL语言的简单语法识别，可识别并去除注释，识别字符串、数字、保留字（或标识符）、操作符和分隔符，并输出格式为`S→A   [0, 2, 5]→[0, 2]`的规约序列和栈变化过程。

## Ideas/Methods

1. 针对要识别的SQL语法写出文法；
2. 根据文法画出LR(1)的GOTO图；
3. 根据GOTO图写出语法分析表；
4. 基于语法分析表编写代码；
5. 遍历词法分析程序传来的Token序列，将其依次输入LR(1)语法分析器的有限自动状态机模型，进入接受状态则完成语法分析，产生异常则判断输入非法。

## Assumptions

1. 保留字的识别优先级⾼于标识符
2. 输入文件的编码为UTF-8
3. 字符全集为`\t`、`\n`以及ASCII码中的所有可打印字符，输⼊⽂件内容也仅由这些字符组成
4. 未被反引号`` ` ``括起的标识符不以数字开头
5. 运算符`op` 以及`.`、`and`、`or`，分隔符`;`和`,`均为左结合
6. `and`的优先级高于`or`

## Related FA descriptions

#### 文法

```
r1: S'→S
r2: S→S;S
r3: S→sNfT
r4: S→sNfTwC
r5: N→N,N
r6: N→i.i
r7: N→i(i)
r8: N→i
r9: C→CandC
r10: C→CorC
r11: C→DopD
r12: D→str|i|num
r13: T→T,T
r14: T→ii
r15: T→i
```

其中，`s`代表`select`，`f`代表`from`，`w`代表`where`，`i`代表标识符，`op`代表运算符

#### GOTO图

![GOTO图](resources/GOTO.png)

#### 分析表

| 状态 |select| from | where| id   | str  | num  | and  | or   | op   | ;    | .    | ,    | (    | )    | $    | S    | N    | T    | C    | D    |
| ---- | ---- | ---- | ---- | ---- | ---- | ---- | ---- | ---- | ---- | ---- | ---- | ---- | ---- | ---- | ---- | ---- | ---- | ---- | ---- | ---- |
| 0    | s2   |      |      |      |      |      |      |      |      |      |      |      |      |      |      | s1   |      |      |      |      |
| 1    |      |      |      |      |      |      |      |      |      | s3   |      |      |      |      | acc  |      |      |      |      |      |
| 2    |      |      |      | s5   |      |      |      |      |      |      |      |      |      |      |      |      | s4   |      |      |      |
| 3    | s2   |      |      |      |      |      |      |      |      |      |      |      |      |      |      | s6   |      |      |      |      |
| 4    |      | s7   |      |      |      |      |      |      |      |      |      | s8   |      |      |      |      |      |      |      |      |
| 5    |      | r8   |      |      |      |      |      |      |      |      | s9   | r8   | s10  |      |      |      |      |      |      |      |
| 6    |      |      |      |      |      |      |      |      |      | r2   |      |      |      |      | r2   |      |      |      |      |      |
| 7    |      |      |      | s12  |      |      |      |      |      |      |      |      |      |      |      |      |      | s11  |      |      |
| 8    |      |      |      | s5   |      |      |      |      |      |      |      |      |      |      |      |      | s13  |      |      |      |
| 9    |      |      |      | s14  |      |      |      |      |      |      |      |      |      |      |      |      |      |      |      |      |
| 10   |      |      |      | s15  |      |      |      |      |      |      |      |      |      |      |      |      |      |      |      |      |
| 11   |      |      | s16  |      |      |      |      |      |      | r3   |      | s17  |      |      | r3   |      |      |      |      |      |
| 12   |      |      | r15  | s18  |      |      |      |      |      | r15  |      | r15  |      |      | r15  |      |      |      |      |      |
| 13   |      | r5   |      |      |      |      |      |      |      |      |      | s19  |      |      |      |      |      |      |      |      |
| 14   |      | r6   |      |      |      |      |      |      |      |      |      | r6   |      |      |      |      |      |      |      |      |
| 15   |      |      |      |      |      |      |      |      |      |      |      |      |      | s20  |      |      |      |      |      |      |
| 16   |      |      |      | s23  | s23  | s23  |      |      |      |      |      |      |      |      |      |      |      |      | s21  | s22  |
| 17   |      |      |      | s12  |      |      |      |      |      |      |      |      |      |      |      |      |      | s24  |      |      |
| 18   |      |      | r14  |      |      |      |      |      |      | r14  |      | r14  |      |      | r14  |      |      |      |      |      |
| 19   |      |      |      | s5   |      |      |      |      |      |      |      |      |      |      |      |      | s25  |      |      |      |
| 20   |      | r7   |      |      |      |      |      |      |      |      |      | r7   |      |      |      |      |      |      |      |      |
| 21   |      |      |      | s23  | s23  | s23  | s26  | s27  | s28  | r4   |      |      |      |      | r4   |      |      |      |      |      |
| 22   |      |      |      |      |      |      |      |      | s28  |      |      |      |      |      |      |      |      |      |      |      |
| 23   |      |      |      |      |      |      |      |      | r12  |      |      |      |      |      |      |      |      |      |      |      |
| 24   |      |      | r13  |      |      |      |      |      |      | r13  |      | r13  |      |      | r13  |      |      |      |      |      |
| 25   |      | r5   |      |      |      |      |      |      |      |      |      | r5   |      |      |      |      |      |      |      |      |
| 26   |      |      |      | s23  | s23  | s23  |      |      |      |      |      |      |      |      |      |      |      |      | s29  | s22  |
| 27   |      |      |      | s23  | s23  | s23  |      |      |      |      |      |      |      |      |      |      |      |      | s30  | s22  |
| 28   |      |      |      | s32  | s32  | s32  |      |      |      |      |      |      |      |      |      |      |      |      |      | s31  |
| 29   |      |      |      |      |      |      | r9   | r9   |      | r9   |      |      |      |      | r9   |      |      |      |      |      |
| 30   |      |      |      |      |      |      | s26  | r10  |      | r10  |      |      |      |      | r10  |      |      |      |      |      |
| 31   |      |      |      |      |      |      | r11  | r11  |      | r11  |      |      |      |      | r11  |      |      |      |      |      |
| 32   |      |      |      |      |      |      | r11  | r11  |      | r11  |      |      |      |      | r11  |      |      |      |      |      |

## Description of important Data Structures

###  SyntaxAnalyzer

```java
public class SyntaxAnalyzer {
	private final static String ACCEPT = "acc";
	private final static String END = "$";
	private String startState;	// 初始状态
	private Map<String, Map<String, String>> analysis = new HashMap<>(); // 分析表：Map<current_state, Map<input_token, analysis>>
	private String[] syntaxes;	// 文法集合
	private Stack<String> stack = new Stack<>();	// 状态序列栈
}
```

​	语法分析器类，同时也是LR(1)语法的有限自动状态机，接受Lab 1中的词法分析程序传来的Token序列，逐个尝试进行转换，直到出现接受状态`acc`；转换不存在则说明其发现了一个语法错误。

### Token

```java
public class Token {
	TokenType type; // 单词类型，如保留字、数字
	String value;   // 单词的值，如"select"、"123"
}
```

​	单词类，即词法分析出的单词。

​	另：单词类型包括注释（COMMENT）和错误（ERROR），这两类“单词”留待词法分析结束后删去或由语法分析器处理。

## Description of core Algorithms

1. 语法分析器`SyntaxAnalyzer`从头扫描Token序列，逐一输入有限状态机；
2. 状态机检查当前Token和栈顶的状态，依据分析表尝试移入或规约动作；
3. 遇到接受状态`acc`则完成分析；
4. 遇到不存在的转换则提示语法不合法，抛出异常，退出分析。

## Use cases on running

​	参见`SyntaxAnalyzer`类中的`main()`以及`lab2/resources`文件夹中的`input.txt`。

## Problems occurred and related solutions

1. GOTO图遗漏向前看字符的传播(x2)：老实从头修改……
2. 运算符集合较大导致的手写语法分析表困难：使用`op`标记所有运算符，程序中将所有`TokenType`为`OPERATOR`的Token与其匹配。详见`SyntaxAnalyzer`类的构造函数和`分析表.txt`。

## Your feelings and comments

​	编写类似YACC的程序，由计算机完成文法到有限状态机代码的转换，真是非常实实在在的本事，也是对计算机界非常实实在在的贡献。