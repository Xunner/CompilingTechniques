import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * 词法分析器
 * <br>
 * created on 2018/10/31
 *
 * @author 巽
 **/
public class LexicalAnalyzer {

	public List<Token> analyzeAsSQL(String input) {
		List<Token> ret = new ArrayList<>();

		input.split("\\*");

		return ret;
	}

	public List<Token> analyzeAsSQL(File txt) {
		Long fileLength = txt.length();
		byte[] fileContent = new byte[fileLength.intValue()];
		try (FileInputStream in = new FileInputStream(txt)) {
			in.read(fileContent);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return analyzeAsSQL(new String(fileContent, StandardCharsets.UTF_8));
	}

	public static void main(String args[]) {
		LexicalAnalyzer lexicalAnalyzer = new LexicalAnalyzer();
		File file = new File("." + File.separator + "resources" + File.separator + "input.txt");
		lexicalAnalyzer.analyzeAsSQL(file);
	}
}
