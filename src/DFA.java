import java.util.HashMap;
import java.util.Map;

/**
 * DFA转换表
 * <br>
 * created on 2018/11/02
 *
 * @author 巽
 **/
public class DFA {
	private String state = "";
	private Map<String, Map<String, String>> map = new HashMap<>();

	public void addState(String state, Map<String, String> map){
		this.map.put(state, map);
	}

	public String transform(String in){
		state = map.get(state).getOrDefault(in, "");
		return state;
	}
}
