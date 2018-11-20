import java.util.*;

/**
 * 词法分析器
 * <br>
 * created on 2018/10/31
 *
 * @author 巽
 **/
public class LexicalAnalyzer {
	private final static String END_SYMBOL = "∅";   // 结束标记
	private List<String> sqlPreservedWords = new ArrayList<>(); // SQL保留字集合

	LexicalAnalyzer() {
		String[] words = SLUtil.readFile("sql_preserved_words.txt").split("\r\n| |\n");
		sqlPreservedWords.addAll(Arrays.asList(words));
	}

	/**
	 * 分析SQL词法
	 *
	 * @param input SQL语句
	 * @return 分析结果序列
	 */
	List<Token> analyzeAsSQL(String input) {
		DFA dfa = DFALoader.loadDFA();
		assert dfa != null;

		dfa.reset();
		List<Token> ret = new ArrayList<>();
		StringBuilder match = new StringBuilder();  // 当前匹配串
		input = input + END_SYMBOL; // 添加哨兵结束标记
		for (int i = 0; i < input.length(); i++) {
			String state = dfa.transform(input.charAt(i));
			if (state.isEmpty()) {   // 若匹配已停止
				if (dfa.isEndState()) {   // 已匹配成功
					ret.add(matchSQLToken(match.toString()));
					i--;    // 从当前字符开始新的匹配
				} else if (match.length() > 0){    // 若匹配失败
					ret.add(new Token(TokenType.ERROR, match.toString()));
					i--;    // 从当前字符开始新的匹配

//					System.out.println("跳过字符：'" + (input.charAt(i) < 32 ?
//							"\\" + (int) input.charAt(i) : input.charAt(i)) + "'");
				}
				match.setLength(0); // 清空
				dfa.reset();
			} else {    // 继续匹配
				match.append(input.charAt(i));
			}
		}

		return ret;
	}

	/**
	 * 识别切分出来的单词
	 *
	 * @param matched 待识别单词
	 * @return 单词
	 */
	private Token matchSQLToken(String matched) {
		char initial = matched.charAt(0);
		if (Character.isDigit(initial)) {
			return new Token(TokenType.NUMBER, matched);
		} else if (Character.isLetter(initial) || initial == '_') {
			if (sqlPreservedWords.contains(matched)) {
				return new Token(TokenType.RESERVED_WORD, matched);
			} else {
				return new Token(TokenType.IDENTIFIER, matched);
			}
		} else {
			switch (initial) {
				case '`':
					return new Token(TokenType.IDENTIFIER, matched);
				case '<':
				case '>':
				case '=':
				case '+':
				case '-':
				case '&':
				case '*':
				case '.':
				case '^':
					return new Token(TokenType.OPERATOR, matched);
				case '(':
				case ')':
				case '{':
				case '}':
				case ';':
				case ',':
					return new Token(TokenType.SEPARATOR, matched);
				case '"':
				case '\'':
					return new Token(TokenType.STRING, matched);
				case '#':
					return new Token(TokenType.COMMENT, matched);
				case '/':
					if (matched.length() == 1) {
						return new Token(TokenType.SEPARATOR, matched);
					} else {
						return new Token(TokenType.COMMENT, matched);
					}
			}
		}
		System.out.println("错误，未定义的单词：" + matched);
		return null;
	}


	public static void main(String args[]) {
		new DFALoader().generateDFA();
		LexicalAnalyzer lexicalAnalyzer = new LexicalAnalyzer();
		System.out.println("<Token类型, 符号>");
		System.out.println("-----------------");
		for (Token token : lexicalAnalyzer.analyzeAsSQL(SLUtil.readFile("input.txt"))) {
//			if (token.type == TokenType.ERROR) {
//				System.out.println("错误！无法识别：\"" + token.value + '"');
//			} else if (token.type == TokenType.COMMENT) {
//				System.out.println("剔除注释：" + token.value);
//			} else {
				System.out.println(token);
//			}
		}
	}
}
