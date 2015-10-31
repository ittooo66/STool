package Processing;

import Core.SToolEditor;
import processing.core.*;
import processing.data.*;
import processing.event.*;

/**
 * Created by 66 on 2015/10/11.
 */
public class UCGraph extends PApplet {
	//選択中のUsecaseID（-1なら非選択）
	public int selectedUsecaseId = -1;
	public int selectedFlowId = -1;
	public int selectedStepId = -1;

	//リスト内の最初に記述される各種Index
	private int firstUsecaseIndex = 0;
	private int firstAltFlowIndex = 0;
	private int firstExcFlowIndex = 0;
	private int firstStepIndex = 0;

	//本体
	private SToolEditor sToolEditor;

	//カラーパレット
	private final int COLOR_BACKGROUND = color(28, 28, 28);
	private final int COLOR_LINES = color(123, 144, 210);
	private final int COLOR_SELECTED = color(226, 148, 59);

	//枠のマージン値
	private final int MERGIN = 20;

	//現在のカラム幅を返す
	private int column_width() {
		return (width - 4 * MERGIN) / 3;
	}

	//現在のALT,EXCカラム高さを返す
	private int alt_exc_column_height() {
		return (height - 5 * MERGIN) / 2;
	}

	public UCGraph(SToolEditor sToolEditor) {
		this.sToolEditor = sToolEditor;
	}

	public void setup() {
		//とりあえず適当な解像度で初期化
		size(1024, 768);
		//CPU節約
		noLoop();
		//Font設定。
		PFont font = createFont("メイリオ ボールド", 15, true);
		textFont(font);
	}

	public void draw() {
		background(COLOR_BACKGROUND);
		noFill();
		stroke(COLOR_LINES);
		strokeWeight(2);
		text("width:" + width + ",height:" + height +
				", selectedUsecaseId:" + selectedUsecaseId +
				", selectedFlowId:" + selectedFlowId +
				", selectedStepId:" + selectedStepId +
				", firstUsecaseIndex" + firstUsecaseIndex +
				", firstAltFlowIndex" + firstAltFlowIndex +
				", firstExcFlowIndex" + firstExcFlowIndex +
				", firstStepIndex" + firstStepIndex, 10, 15);

		//Usecase枠線
		rect(MERGIN, MERGIN, column_width(), height - 2 * MERGIN);
		//Flow枠線:MAIN
		rect(2 * MERGIN + column_width(), MERGIN, column_width(), MERGIN);
		//Flow枠線:Exc,Alt
		int alt_exc_height = (height - 5 * MERGIN) / 2;
		rect(2 * MERGIN + column_width(), 3 * MERGIN, column_width(), alt_exc_height);
		rect(2 * MERGIN + column_width(), 4 * MERGIN + alt_exc_height, column_width(), alt_exc_height);
		//Step枠線
		rect(3 * MERGIN + 2 * column_width(), MERGIN, column_width(), height - 2 * MERGIN);

		//追加・削除ボタン（仮置き）
		//Usecase
		ellipse(3 * MERGIN / 2, 3 * MERGIN / 2, MERGIN, MERGIN);
		ellipse(5 * MERGIN / 2, 3 * MERGIN / 2, MERGIN, MERGIN);
		//Flow:Alt
		ellipse(5 * MERGIN / 2 + column_width(), 7 * MERGIN / 2, MERGIN, MERGIN);
		ellipse(7 * MERGIN / 2 + column_width(), 7 * MERGIN / 2, MERGIN, MERGIN);
		//Flow:Exc
		ellipse(5 * MERGIN / 2 + column_width(), 9 * MERGIN / 2 + alt_exc_height, MERGIN, MERGIN);
		ellipse(7 * MERGIN / 2 + column_width(), 9 * MERGIN / 2 + alt_exc_height, MERGIN, MERGIN);
		//Step
		ellipse(7 * MERGIN / 2 + 2 * column_width(), 3 * MERGIN / 2, MERGIN, MERGIN);
		ellipse(9 * MERGIN / 2 + 2 * column_width(), 3 * MERGIN / 2, MERGIN, MERGIN);
		
		//TODO:内容記述（Usecase）
		//TODO:内容記述（Flow）
		//TODO:内容記述（Step）
	}


	public void mousePressed() {

		sToolEditor.redraw();
	}

	public void mouseWheel(MouseEvent event) {
		//カウント取得
		float e = event.getCount();

		if (MERGIN < mouseX && mouseX < MERGIN + column_width() && 2 * MERGIN < mouseY && mouseY < height - MERGIN) {
			//Usecase部分のスクロールの場合
			firstUsecaseIndex += e;
		} else if (2 * MERGIN + column_width() < mouseX && mouseX < 2 * MERGIN + 2 * column_width()) {
			//Flow部分のスクロールの場合
			if (4 * MERGIN < mouseY && mouseY < 4 * MERGIN + alt_exc_column_height()) {
				//ALT_Flow
				firstAltFlowIndex += e;
			} else if (6 * MERGIN + alt_exc_column_height() < mouseY && mouseY < height - MERGIN) {
				//EXC_Flow
				firstExcFlowIndex += e;
			}
		} else if (3 * MERGIN + 2 * column_width() < mouseX && mouseX < width - MERGIN && 2 * MERGIN < mouseY && mouseY < height - MERGIN) {
			//Step部分のスクロールの場合
			firstStepIndex += e;
		}
		sToolEditor.redraw();
	}
}
