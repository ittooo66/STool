package Models;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by 66 on 2015/11/17.
 * ProblemFrame作成用のInterfaceモデル
 */
public class PFInterface {
	public List<PFEvent> eventList;
	public int rootDomainId;
	public int distDomainId;
	public int x;
	public int y;

	public PFInterface(Domain rootDomain, Domain distDomain) {
		eventList = new ArrayList<>();
		this.rootDomainId = rootDomain.id;
		this.distDomainId = distDomain.id;
		this.x = (rootDomain.x + distDomain.x) / 2;
		this.y = (rootDomain.y + distDomain.y) / 2;
	}

	public void add(PFEvent event) {
		eventList.add(event);
	}

	public List<PFEvent> getEvents(boolean inverse) {
		return eventList.stream().filter(pfe -> inverse == pfe.inverse).collect(Collectors.toList());
	}

}
