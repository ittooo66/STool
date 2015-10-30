package Processing;

import Core.SToolEditor;
import processing.core.PApplet;

/**
 * Created by 66 on 2015/10/11.
 */
public class UCGraph extends PApplet {
	//選択中のUsecaseID（-1なら非選択）
	public int selectedUsecaseId = -1;
	public int selectedFlowId = -1;
	public int selectedStepId = -1;

	//本体
	private SToolEditor sToolEditor;

	//カラーパレット
	private final int COLOR_BACKGROUND = color(28, 28, 28);
	private final int COLOR_LINES = color(123, 144, 210);
	private final int COLOR_SELECTED = color(226, 148, 59);

	public UCGraph(SToolEditor sToolEditor) {
		this.sToolEditor = sToolEditor;
	}

	public void draw() {
		background(COLOR_BACKGROUND);
		noFill();
		stroke(COLOR_LINES);
		strokeWeight(2);
		text(width + "," + height, 10, 15);

		//マージン
		int m = 20;
		//カラム幅
		int cw = (width - 4 * m) / 3;

		//Usecase枠線
		rect(m, m, cw, height - 2 * m);
		//Flow枠線:MAIN
		rect(2 * m + cw, m, cw, m);
		//Flow枠線:Exc,Alt
		int alt_exc_height = (height - 5 * m) / 2;
		rect(2 * m + cw, 3 * m, cw, alt_exc_height);
		rect(2 * m + cw, 4 * m + alt_exc_height, cw, alt_exc_height);
		//Step枠線
		rect(3 * m + 2 * cw, m, cw, height - 2 * m);

		//追加・削除ボタン（仮置き）
		//Usecase
		ellipse(3 * m / 2, 3 * m / 2, m, m);
		ellipse(5 * m / 2, 3 * m / 2, m, m);
		//Flow:Alt
		ellipse(5 * m / 2 + cw, 7 * m / 2, m, m);
		ellipse(7 * m / 2 + cw, 7 * m / 2, m, m);
		//Flow:Exc
		ellipse(5 * m / 2 + cw, 9 * m / 2 + alt_exc_height, m, m);
		ellipse(7 * m / 2 + cw, 9 * m / 2 + alt_exc_height, m, m);
		//Step
		ellipse(7 * m / 2 + 2 * cw, 3 * m / 2, m, m);
		ellipse(9 * m / 2 + 2 * cw, 3 * m / 2, m, m);

	}

	public void setup() {
		size(1024, 768);
		noLoop();
	}

	public void mousePressed() {

		redraw();
	}

}
