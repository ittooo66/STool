package Models;

import Processing.PFGraph;
import processing.core.PApplet;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by 66 on 2015/11/17.
 * ProblemFrame作成用のInterfaceモデル
 */
public class PFInterface {
	public List<PFEvent> eventList;
	public Domain rootDomain;
	public Domain distDomain;
	private int x;
	private int y;

	public int getX(PApplet pApplet) {
		if (rootDomain.id != distDomain.id)
			return x;
		else {
			//セルフループ時用のインターフェースクリック位置ずらし
			return (int) pApplet.textWidth(rootDomain.name)/2 + 35 + x;
		}
	}

	public int getY() {
		if (rootDomain.id != distDomain.id)
			return y;
		else
			//セルフループ時用のインターフェースクリック位置ずらし
			return y + 40;
	}

	public PFInterface(Domain rootDomain, Domain distDomain) {
		eventList = new ArrayList<>();
		this.rootDomain = rootDomain;
		this.distDomain = distDomain;
		this.x = (rootDomain.x + distDomain.x) / 2;
		this.y = (rootDomain.y + distDomain.y) / 2;
	}

	public void add(PFEvent event) {
		eventList.add(event);
	}

	/**
	 * イベントのリストを取得
	 * @param inverse 正方向のイベント＝True、負方向＝False
	 * @return イベントリスト
	 */
	public List<PFEvent> getEvents(boolean inverse) {
		return eventList.stream().filter(pfe -> inverse == pfe.inverse).collect(Collectors.toList());
	}

}
