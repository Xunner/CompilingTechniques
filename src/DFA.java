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
	private String startState;  // 初始状态
	private String state;   // 当前状态，空串""代表不存在的转换到达的不存在的状态
	private Map<String, Map<String, String>> table; // 转换表
	private Set<String> endStates;  // 终态集合

	DFA(Map<String, Map<String, String>> table, Set<String> endStates, String startState) {
		this.table = table;
		this.endStates = endStates;
		this.startState = startState;
		this.state = startState;
	}

	String transform(char c) {
		String next = table.get(state).getOrDefault(String.valueOf(c), "");
		if (!next.isEmpty()) {
			state = next;
		}
		return next;
	}

	boolean isEndState() {
		return endStates.contains(state);
	}

	/**
	 * 重置为初始状态
	 */
	void reset() {
		state = startState;
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
