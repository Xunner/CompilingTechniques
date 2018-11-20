import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * 存取工具类
 * <br>
 * created on 2018/11/03
 *
 * @author 巽
 **/
class SLUtil {
	private final static String PATH = "." + File.separator + "lab2" + File.separator + "resources" + File.separator;

	/**
	 * 读取文本，默认编码UTF-8
	 *
	 * @param name 文本名
	 * @return 整个文本
	 */
	static String readFile(String name) {
		File file = new File(PATH + name);
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

	static void saveObject(Object object, String name) {
		try (ObjectOutputStream oos = new ObjectOutputStream(
				new FileOutputStream(new File(PATH + name), false))) {
			oos.writeObject(object);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 加载对象
	 *
	 * @return 对象
	 */
	static Object loadObject(String name) {
		try (ObjectInputStream ois = new ObjectInputStream(
				new FileInputStream(new File(PATH + name)))) {
			return ois.readObject();
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
}
