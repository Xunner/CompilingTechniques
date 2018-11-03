import java.io.Serializable;
import java.util.Map;
import java.util.Set;

/**
 * DFA转换表
 * <br>
 * created on 2018/11/02
 *
 * @author 巽
 **/
public class DFA implements Serializable {
	private String startState;
	private String state;
	private Map<String, Map<String, String>> table;
	private Set<String> endStates;

	public DFA(Map<String, Map<String, String>> table, Set<String> endStates, String startState){
		this.table = table;
		this.endStates = endStates;
		this.startState = startState;
		this.state = startState;
	}

	public String transform(String in){
		state = table.get(state).getOrDefault(in, "");
		return state;
	}

	@Override
	public String toString() {
		return "DFA{" +
				"startState='" + startState + '\'' +
				", state='" + state + '\'' +
				", table=" + table +
				", endStates=" + endStates +
				'}';
	}
}
