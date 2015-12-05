package Metrics;

import java.util.List;

/**
 * Created by 66 on 2015/12/05.
 * CEモデル
 */
public class CE {
	public String event;
	public int subjectDomainId;
	public int objectDomainId;
	public int count;

	public CE(String event, int rootDomainId, int distDomainId) {
		this.event = event;
		this.subjectDomainId = rootDomainId;
		this.objectDomainId = distDomainId;
		this.count = 1;
	}

	/**
	 * 同一の内容を持つCEをListから抽出する
	 *
	 * @param root
	 * @param ces
	 * @return
	 */
	public static CE extractCE(CE root, List<CE> ces) {
		CE ce = null;
		for (CE c : ces) {
			if (c.event.equals(root.event) &&
					c.objectDomainId == root.objectDomainId &&
					c.subjectDomainId == root.subjectDomainId) {
				ce = c;
			}
		}
		return ce;
	}
}
