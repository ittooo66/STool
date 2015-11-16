package Models;

import java.util.ArrayList;
import java.util.List;

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
	public List<Integer> rootUsecaseId;

	public PFEvent(String event, boolean inverse) {
		this.event = event;
		this.inverse = inverse;
		rootUsecaseId = new ArrayList<>();
	}

	public void setRootUsecase(int usecaseId) {
		rootUsecaseId.add(usecaseId);
	}
}
