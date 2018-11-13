/**
 * 单词
 * <br>
 * created on 2018/10/31
 *
 * @author 巽
 **/
public class Token {
	TokenType type; // 单词类型
	String value;   // 单词的值

	Token(TokenType type, String value) {
		this.type = type;
		this.value = value;
	}

	@Override
	public String toString() {
		return "<" + type +
				", " + value +
				'>';
	}
}
