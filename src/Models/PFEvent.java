package Models;

/**
 * Created by 66 on 2015/11/17.
 * ProblemFrame用のEventモデル
 */
public class PFEvent {
	public String event;

	/**
	 * 所属しているInterfaceに対して順方向(true)か、逆方向(false)か
	 */
	public boolean inverse;
	public int rootUsecaseId;

	public PFEvent(String event, boolean inverse, int rootUsecaseId) {
		this.event = event;
		this.inverse = inverse;
		this.rootUsecaseId = rootUsecaseId;
	}
}
