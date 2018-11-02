import java.io.*;
import java.util.*;

/**
 * 词法分析器
 * <br>
 * created on 2018/10/31
 *
 * @author 巽
 **/
public class LexicalAnalyzer {

	/**
	 * 分析SQL词法
	 *
	 * @param input SQL语句
	 * @return 分析结果序列
	 */
	private List<Token> analyzeAsSQL(String input) {
		List<Token> ret = new ArrayList<>();

//		System.out.println(input);

		DfaLoader.tranDFA();
		DFA dfa = DfaLoader.loadDFA();
		return ret;
	}

	/**
	 * 分析SQL词法
	 *
	 * @param txt SQL文本
	 * @return 分析结果序列
	 */
	private List<Token> analyzeAsSQL(File txt) {
		return analyzeAsSQL(DfaLoader.readFile(txt));
	}


	public static void main(String args[]) {
		LexicalAnalyzer lexicalAnalyzer = new LexicalAnalyzer();
		File file = new File(DfaLoader.PATH + "input.txt");
		System.out.println(lexicalAnalyzer.analyzeAsSQL(file));
	}
}
