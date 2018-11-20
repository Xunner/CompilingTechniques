import java.util.*;

/**
 * 语法分析器
 * <br>
 * created on 2018/11/19
 *
 * @author 巽
 **/
public class SyntaxAnalyzer {
	private final static String ACCEPT = "acc";
	private final static String END = "$";
	private String startState;	// 初始状态
	private Map<String, Map<String, String>> analysis = new HashMap<>(); // 分析表：Map<current_state, Map<input_token, analysis>>
	private String[] syntaxes;	// 文法集合
	private Stack<String> stack = new Stack<>();	// 状态序列栈

	private SyntaxAnalyzer(String analysisFileName, String syntaxFileName) {
		// 加载手写的分析表
		String[] lines = SLUtil.readFile(analysisFileName).split(System.getProperty("line.separator"));
		String[] head = lines[0].split("\\|");
		for (int i = 0; i < head.length; i++) {
			head[i] = head[i].trim();
		}
		for (int i = 2; i < lines.length; i++) {
			String[] blocks = lines[i].split("\\|");
			String state = blocks[1].trim();
			if (i == 2) {
				startState = state;
			}
			Map<String, String> actions = new HashMap<>();
			for (int j = 2; j < blocks.length; j++) {
				String movement = blocks[j].trim();
				if (!movement.isEmpty()) {
					if (movement.equals(ACCEPT)) {
						actions.put(head[j], movement);
					} else if (movement.charAt(0) == 's') {
						actions.put(head[j], movement);
					} else if (movement.charAt(0) == 'r') {
						actions.put(head[j], movement);
					} else {
						System.out.println("ERROR IN ANALYSIS  TABLE: " + movement);
					}
				}
			}
			analysis.put(state, actions);
		}
//		System.out.println("analysis: " + analysis);
		// 加载语法
		syntaxes = SLUtil.readFile(syntaxFileName).split(System.getProperty("line.separator"));
//		System.out.println("syntaxes: " + Arrays.toString(syntaxes));
	}

	private void analyzeAsSQL(List<Token> tokens) {
		// 初始化
		stack.clear();
		stack.push(startState);
		boolean isAccepted = false;
		tokens.add(new Token(TokenType.RESERVED_WORD, END));    // 加上终止符
		System.out.println("选择第三种输出方法：规约序列");
		System.out.println(String.format("%-31s", "reduction sequence") + "stack translation");
		for (Token token : tokens) {
			if (token.type != TokenType.ERROR && token.type != TokenType.COMMENT) {
				try {
					isAccepted = move(stack.peek(), token); // 状态机尝试转换
				} catch (Exception e) {
					if (e.getMessage() == null) {
						e.printStackTrace();
					} else {
						System.out.println(e.getMessage());
					}
					return;
				}
			}
		}
		if (isAccepted) {
			System.out.println("接受，分析成功，是合法语法");
		}
	}

	private boolean move(String top, Token token) throws Exception {
//		System.out.println("stack=" + stack);
//		System.out.println("current token=" + token);
		String movement = analysis.get(top).get(token.value);
//		System.out.println("movement=" + movement);
		if (movement == null) {
			movement = analysis.get(top).get(token.type.value);
		}
		if (movement == null) {   // 报错
			throw new Exception("not a legal syntax at: \n\tstack=" + stack + "\n\tcurrent token=" + token);
		} else if (movement.equals(ACCEPT)) {    // 接受
			return true;
		} else if (movement.charAt(0) == 's') {  // 移入
			stack.push(movement.substring(1));
			return false;
		} else if (movement.charAt(0) == 'r') {   // 规约
			String reduction = syntaxes[Integer.valueOf(movement.substring(1))];
			String[] sides = reduction.split("→");
			String nonTerminal = sides[0];
			String[] states = sides[1].split(" ");
			System.out.print(String.format("%-30s", reduction) + stack);
			for (String ignored : states) {
				stack.pop();
			}
			System.out.println(" → " + stack);
//			System.out.println(reduction + " | " + stack.peek());
			stack.push(analysis.get(stack.peek()).get(nonTerminal).substring(1));
			return move(stack.peek(), token);
		} else { // 异常
			throw new Exception("ERROR WHILE ANALYZING: \n\t" + "stack=" + stack + "\n\tcurrent token=" + token);
		}
	}

	public static void main(String args[]) {
		new DFALoader().generateDFA();
		LexicalAnalyzer lexicalAnalyzer = new LexicalAnalyzer();
		SyntaxAnalyzer syntaxAnalyzer = new SyntaxAnalyzer("分析表.txt", "syntax.txt");
		List<Token> tokens = lexicalAnalyzer.analyzeAsSQL(SLUtil.readFile("input.txt"));    // 词法分析
		syntaxAnalyzer.analyzeAsSQL(tokens);    // 语法分析
	}
}
