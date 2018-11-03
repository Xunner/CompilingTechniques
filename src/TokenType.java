/**
 * 单词的类型
 * <br>
 * created on 2018/11/03
 *
 * @author 巽
 **/
public enum TokenType {
	RESERVED_WORD("pres"), IDENTIFIER("id"), STRING("str"), COMMENT("com"), NUMBER("num"), OPERATOR("op"),
	SEPARATOR("sep"), ERROR("error");

	String value;

	TokenType(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return this.value;
	}
}
