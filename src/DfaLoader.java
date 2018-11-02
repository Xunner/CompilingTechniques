import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DFA加载器，可以读取手写的DFA.txt并转换为DFA转换表存起来，可以加载DFA转换表
 * <br>
 * created on 2018/11/02
 *
 * @author 巽
 **/
class DfaLoader {
	static String PATH = "." + File.separator + "resources" + File.separator;

	/**
	 * 读取文本，默认编码UTF-8
	 *
	 * @param file 文本名
	 * @return 整个文本
	 */
	static String readFile(File file) {
		Long fileLength = file.length();
		byte[] fileContent = new byte[fileLength.intValue()];
		try (FileInputStream in = new FileInputStream(file)) {
			int read = in.read(fileContent);
			assert read == -1;
		} catch (IOException e) {
			e.printStackTrace();
			return "";
		}
		return new String(fileContent, StandardCharsets.UTF_8);
	}

	/**
	 * 读取手写的DFA.txt并转换为DFA转换表存起来
	 */
	static void tranDFA() {
		String dfa = readFile(new File(PATH + "dfa.txt"));
		String[][] ret = new String[93][];    // ASCII码\32~\126 + \t + \n共93个字符
		List<Character> chars = new ArrayList<>(); // 字符全集
		for (int i = 32; i < 127; i++) {
			chars.add((char) i);
		}
		chars.add('\t');
		chars.add('\n');

//		System.out.println(chars);
		for (String line : dfa.split("\n")) {
			String[] string = line.split("\\|");
			boolean isEnd = (string[0].charAt(0) == '*');
			String state = string[0].substring(1).trim();
			String[] changes = string[1].split("<br>");
			Map<String, String> map = new HashMap<>();
			for (String change : changes) {
				String[] sides = change.split("→");
				if (sides.length > 1) {
					map.put(sides[0].trim(), sides[1].trim());
				}
			}
			System.out.println(state + ", isEnd: " + isEnd + map);
		}
	}

	/**
	 * 加载DFA转换表
	 *
	 * @return DFA转换表
	 */
	static DFA loadDFA() {
		return null;
	}
}
