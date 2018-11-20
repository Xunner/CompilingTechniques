import java.lang.reflect.Array;
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
	private String startState;
	private Map<String, Map<String, String>> action = new HashMap<>(); // ACTION表：Map<current_state, Map<input_token, action>>
	private Map<String, Map<String, String>> gotoTable = new HashMap<>(); // GOTO表：Map<current_state, Map<top, goto>>
	private String[] syntaxes;
	private Stack<String> stack = new Stack<>();

	private SyntaxAnalyzer(String analysisFileName, String syntaxFileName) {
		// 加载手写的分析表
		String[] lines = SLUtil.readFile(analysisFileName).split(System.getProperty("line.separator"));
		String[] head = lines[0].split("\\|");
		for (int i = 0; i < head.length; i++) {
			head[i] = head[i].trim();
		}
		System.out.println(Arrays.toString(head));
		for (int i = 2; i < lines.length; i++) {
			String[] blocks = lines[i].split("\\|");
			String state = blocks[1].trim();
			if (i == 2) {
				startState = state;
			}
			Map<String, String> actions = new HashMap<>();
			Map<String, String> gotos = new HashMap<>();
			for (int j = 2; j < blocks.length; j++) {
				String movement = blocks[j].trim();
				if (!movement.isEmpty()) {
					if (movement.equals(ACCEPT)) {
						actions.put(head[j], movement);
					} else if (movement.charAt(0) == 's') {
						actions.put(head[j], movement.substring(1));
					} else if (movement.charAt(0) == 'r') {
						gotos.put(head[j], movement.substring(1));
					} else {
						System.out.println("ERROR IN ANALYSIS  TABLE: " + movement);
					}
				}
			}
			action.put(state, actions);
			gotoTable.put(state, gotos);
		}
		System.out.println(action);
		// 加载语法
		syntaxes = SLUtil.readFile(syntaxFileName).split(System.getProperty("line.separator"));
		System.out.println(Arrays.toString(syntaxes));
	}

	private void analyzeAsSQL(List<Token> tokens) {
		// 初始化
		stack.clear();
		stack.push(startState);
//		System.out.println("<Token类型, 符号>");
//		System.out.println("-----------------");
		for (Token token : tokens) {
			if (token.type == TokenType.ERROR) {
				System.out.println("错误！无法识别：\"" + token.value + '"');
			} else if (token.type == TokenType.COMMENT) {
				System.out.println("剔除注释：" + token.value);
			} else {
				boolean isAccepted;
				try {
					isAccepted = move(stack.peek(), token);
				} catch (Exception e) {
					if (e.getMessage() == null) {
						e.printStackTrace();
					} else {
						System.out.println(e.getMessage());
					}
					return;
				}
				if (isAccepted) {
					break;
				}
//				System.out.println(token);
			}
		}

	}

	private boolean move(String top, Token token) throws Exception {
		String movement = action.get(top).get(token.value);
		if (movement == null) {   // 报错
			throw new Exception("not a legal syntax at: \n\tstack=" + stack + "\n\tcurrent token=" + token);
		} else if (movement.equals(ACCEPT)) {    // 接受
			return true;
		} else if (movement.charAt(0) == 's') {  // 移入
			stack.push(movement.substring(1));
			return false;
		} else if (movement.charAt(0) == 'r') {   // 规约
			String reduction = syntaxes[Integer.valueOf(movement.substring(1))];
			System.out.println(reduction);
			return false;
		} else { // 异常
			throw new Exception("ERROR WHILE ANALYZING: \n\t" + "stack=" + stack + "\n\tcurrent token=" + token);
		}
	}

	public static void main(String args[]) {
		new DFALoader().generateDFA();
		LexicalAnalyzer lexicalAnalyzer = new LexicalAnalyzer();
		SyntaxAnalyzer syntaxAnalyzer = new SyntaxAnalyzer("分析表.txt", "syntax.txt");
		List<Token> tokens = lexicalAnalyzer.analyzeAsSQL(SLUtil.readFile("input.txt"));
		syntaxAnalyzer.analyzeAsSQL(tokens);
	}
}
