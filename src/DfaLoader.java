import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * DFA加载器，可以读取手写的DFA.txt并转换为DFA转换表存起来，可以加载DFA转换表
 * <br>
 * created on 2018/11/02
 *
 * @author 巽
 **/
class DfaLoader {
	static String PATH = "." + File.separator + "resources" + File.separator;
	/** 字符全集 */
	private Set<String> as = new HashSet<>();
	/** \w集合 */
	private Set<String> ws = new HashSet<>();
	/** \d集合 */
	private Set<String> ds = new HashSet<>();
	/** 自定义\z集合，等价于[a-zA-Z_] */
	private Set<String> zs = new HashSet<>();

	public DfaLoader() {
		// ds=\d
		for (int i = 0; i < 10; i++) {
			ds.add(String.valueOf(i));
		}

		// zs=[a-zA-Z_]
		zs.add("_");
		for (int i = 0; i < 26; i++) {
			zs.add(String.valueOf((char) (i + 'a')));
			zs.add(String.valueOf((char) (i + 'A')));
		}

		// ws=\w
		ws.addAll(ds);
		ws.addAll(zs);

		// as=all
		for (int i = 32; i < 127; i++) {
			as.add(String.valueOf((char) i));
		}
		as.add("\t");
		as.add("\n");

//		System.out.println("ds: " + ds);
//		System.out.println("zs: " + zs);
//		System.out.println("ws: " + ws);
//		System.out.println("as: " + as);

//		Set<String> tmp = new HashSet<>(ds);
	}

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
	 * 读取手写的DFA.txt转换表并转换为DFA存起来
	 */
	void tranDFA() {
		String dfaTable = readFile(new File(PATH + "dfaTable.txt"));

		Map<String, Map<String, String>> table = new HashMap<>();
		String startState = "";
		Set<String> endStates = new HashSet<>();
		for (String line : dfaTable.split("\n")) {
			String[] string = line.split("\\|");
			String state = string[0].substring(1).trim();   // 状态名称
			if (string[0].charAt(0) == '*') {    // 是否为终态
				endStates.add(state);
			} else if (string[0].charAt(0) == '>') {    // 是否为初态
				startState = state;
			}
			String[] changes = string[1].split("<br>"); // 可走的转换
			Map<String, String> map = new HashMap<>();
			for (String change : changes) {
				String[] sides = change.split("→"); // 一条（手写）转换的两边
				if (sides.length > 1) {
					String left = sides[0].trim();  // 符号
					String right = sides[1].trim(); // 目标状态
					if (left.charAt(0) == '\\') {  // \w等转义
						switch (left.charAt(1)) {
							case 'w':
								for (String character : ws) {
									map.put(character, right);
								}
								break;
							case 'd':
								for (String character : ds) {
									map.put(character, right);
								}
								break;
							case 'z':
								for (String character : zs) {
									map.put(character, right);
								}
								break;
							default:
								System.out.println("错误，未定义转义符号：" + left);
								break;
						}
					} else if (left.charAt(0) == '[') {   // [a-A]、[^*]等形式
						Set<String> chars = analyzeBracket(left);
						for (String character : chars) {
							map.put(character, right);
						}
					} else {   // 单一符号
						map.put(left, right);
					}
				}
			}
			table.put(state, map);
		}

		DFA dfa = new DFA(table, endStates, startState);
		saveDFA(dfa);
	}

	private Set<String> analyzeBracket(String s){
		s = s.substring(1, s.length() - 1);    // 去掉首尾的'['、']'
		Set<String> chars = new HashSet<>();
		if (s.charAt(0) == '^') {  // 处理[^...]形式
			chars.addAll(as);
			for (int i = 1; i < s.length(); i++) {
				if (s.charAt(i) == '\\') { // 处理转义，默认只有\n
					chars.remove(String.valueOf('\n'));
					i++;
				} else {
					chars.remove(String.valueOf(s.charAt(i)));
				}
			}
		} else {  // 处理正常[...]形式
			for (int i = 0; i < s.length(); i++) {
				if (s.charAt(i) == '\\') { // 处理转义，默认只有\n
					chars.add(String.valueOf('\n'));
					i++;
				} else {
					chars.add(String.valueOf(s.charAt(i)));
				}
			}
		}
		return chars;
	}

	private static void saveDFA(DFA dfa) {
		try (ObjectOutputStream oos = new ObjectOutputStream(
				new FileOutputStream(new File(PATH + "dfa.txt"), false))) {
			oos.writeObject(dfa);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 加载DFA转换表
	 *
	 * @return DFA转换表
	 */
	static DFA loadDFA() {
		try (ObjectInputStream ois = new ObjectInputStream(
				new FileInputStream(new File(PATH + "dfa.txt")))) {
			return (DFA) ois.readObject();
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
}
