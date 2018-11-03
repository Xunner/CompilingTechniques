/**
 * 单词
 * <br>
 * created on 2018/10/31
 *
 * @author 巽
 **/
public class Token {
	private TokenType type;
	private String value;

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
