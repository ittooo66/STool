package Processing;

import Core.SToolEditor;
import Models.Usecase;
import processing.core.*;
import processing.event.*;

import java.util.List;

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

	//各種描画値（finalじゃないやつは画面サイズで可変）
	private final int MERGIN = 30;
	private int COLUMN_WIDTH = (width - 4 * MERGIN) / 3;
	private int ALT_EXC_HEIGHT = (height - 5 * MERGIN) / 2;

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

		COLUMN_WIDTH = (width - 4 * MERGIN) / 3;
		ALT_EXC_HEIGHT = (height - 5 * MERGIN) / 2;
	}

	public void draw() {
		background(COLOR_BACKGROUND);
		fill(COLOR_LINES);
		stroke(COLOR_LINES);
		noFill();
		strokeWeight(2);

		COLUMN_WIDTH = (width - 4 * MERGIN) / 3;
		ALT_EXC_HEIGHT = (height - 5 * MERGIN) / 2;

		//Debug用
		text("w:" + width + ",h:" + height +
				", selectedUsecaseId:" + selectedUsecaseId +
				", selectedFlowId:" + selectedFlowId +
				", selectedStepId:" + selectedStepId +
				", firstUsecaseIndex" + firstUsecaseIndex +
				", firstAltFlowIndex" + firstAltFlowIndex +
				", firstExcFlowIndex" + firstExcFlowIndex +
				", firstStepIndex" + firstStepIndex, 10, 15);

		textAlign(CENTER, CENTER);

		//Usecase枠線
		rect(MERGIN, MERGIN, COLUMN_WIDTH, height - 2 * MERGIN);
		//Flow枠線:MAIN
		rect(2 * MERGIN + COLUMN_WIDTH, MERGIN, COLUMN_WIDTH, MERGIN);
		//Flow枠線:Exc,Alt
		rect(2 * MERGIN + COLUMN_WIDTH, 3 * MERGIN, COLUMN_WIDTH, ALT_EXC_HEIGHT);
		rect(2 * MERGIN + COLUMN_WIDTH, 4 * MERGIN + ALT_EXC_HEIGHT, COLUMN_WIDTH, ALT_EXC_HEIGHT);
		//Step枠線
		rect(3 * MERGIN + 2 * COLUMN_WIDTH, MERGIN, COLUMN_WIDTH, height - 2 * MERGIN);

		//Usecase枠コンポーネント(上下ボタン)記述
		stroke(mouseIsInRect(MERGIN, MERGIN, MERGIN, MERGIN) ? COLOR_SELECTED : COLOR_LINES);
		ellipse(3 * MERGIN / 2, 3 * MERGIN / 2, MERGIN - 4, MERGIN - 4);
		text("↑", MERGIN, MERGIN, MERGIN, MERGIN);
		stroke(mouseIsInRect(2 * MERGIN, MERGIN, MERGIN, MERGIN) ? COLOR_SELECTED : COLOR_LINES);
		ellipse(5 * MERGIN / 2, 3 * MERGIN / 2, MERGIN - 4, MERGIN - 4);
		text("↓", 2 * MERGIN, MERGIN, MERGIN, MERGIN);

		//AltFlow:追加・削除ボタン
		stroke(mouseIsInRect(2 * MERGIN + COLUMN_WIDTH, 3 * MERGIN, MERGIN, MERGIN) ? COLOR_SELECTED : COLOR_LINES);
		ellipse(5 * MERGIN / 2 + COLUMN_WIDTH, 7 * MERGIN / 2, MERGIN - 4, MERGIN - 4);
		text("＋", 2 * MERGIN + COLUMN_WIDTH, 3 * MERGIN, MERGIN, MERGIN);
		stroke(mouseIsInRect(3 * MERGIN + COLUMN_WIDTH, 3 * MERGIN, MERGIN, MERGIN) ? COLOR_SELECTED : COLOR_LINES);
		ellipse(7 * MERGIN / 2 + COLUMN_WIDTH, 7 * MERGIN / 2, MERGIN - 4, MERGIN - 4);
		text("－", 3 * MERGIN + COLUMN_WIDTH, 3 * MERGIN, MERGIN, MERGIN);

		//ExcFlow:追加・削除ボタン
		stroke(mouseIsInRect(2 * MERGIN + COLUMN_WIDTH, 4 * MERGIN + ALT_EXC_HEIGHT, MERGIN, MERGIN) ? COLOR_SELECTED : COLOR_LINES);
		ellipse(5 * MERGIN / 2 + COLUMN_WIDTH, 9 * MERGIN / 2 + ALT_EXC_HEIGHT, MERGIN - 4, MERGIN - 4);
		text("＋", 2 * MERGIN + COLUMN_WIDTH, 4 * MERGIN + ALT_EXC_HEIGHT, MERGIN, MERGIN);
		stroke(mouseIsInRect(3 * MERGIN + COLUMN_WIDTH, 4 * MERGIN + ALT_EXC_HEIGHT, MERGIN, MERGIN) ? COLOR_SELECTED : COLOR_LINES);
		ellipse(7 * MERGIN / 2 + COLUMN_WIDTH, 9 * MERGIN / 2 + ALT_EXC_HEIGHT, MERGIN - 4, MERGIN - 4);
		text("－", 3 * MERGIN + COLUMN_WIDTH, 4 * MERGIN + ALT_EXC_HEIGHT, MERGIN, MERGIN);

		//Step:追加・削除ボタン
		stroke(mouseIsInRect(3 * MERGIN + 2 * COLUMN_WIDTH, MERGIN, MERGIN, MERGIN) ? COLOR_SELECTED : COLOR_LINES);
		ellipse(7 * MERGIN / 2 + 2 * COLUMN_WIDTH, 3 * MERGIN / 2, MERGIN - 4, MERGIN - 4);
		text("＋", 3 * MERGIN + 2 * COLUMN_WIDTH, MERGIN, MERGIN, MERGIN);
		stroke(mouseIsInRect(4 * MERGIN + 2 * COLUMN_WIDTH, MERGIN, MERGIN, MERGIN) ? COLOR_SELECTED : COLOR_LINES);
		ellipse(9 * MERGIN / 2 + 2 * COLUMN_WIDTH, 3 * MERGIN / 2, MERGIN - 4, MERGIN - 4);
		text("－", 4 * MERGIN + 2 * COLUMN_WIDTH, MERGIN, MERGIN, MERGIN);

		//Usecase部分記述
		textAlign(LEFT, CENTER);
		stroke(COLOR_LINES);
		for (int i = 0, j = firstUsecaseIndex; j < sToolEditor.fgm.getUsecases().size(); j++, i++) {
			rect(MERGIN, 2 * MERGIN + i * MERGIN, COLUMN_WIDTH, MERGIN);
			text(sToolEditor.fgm.getUsecases().get(j).name, MERGIN, 2 * MERGIN + i * MERGIN, COLUMN_WIDTH, MERGIN);
		}

		//TODO:内容記述（Flow）
		//TODO:内容記述（Step）
	}

	private boolean mouseIsInRect(int x, int y, int w, int h) {
		if (x < mouseX && mouseX < x + w && y < mouseY && mouseY < y + h) {
			return true;
		} else {
			return false;
		}
	}


	public void mousePressed() {


		sToolEditor.redraw();
	}


	//内部コンポーネント押下時の処理：Usecaseカラム、UpButton
	private void usecaseUpButtonPressed() {

	}

	//内部コンポーネント押下時の処理：Usecaseカラム、DownButton
	private void usecaseDownButtonPressed() {

	}

	//内部コンポーネント押下時の処理：AltFlowカラム、AddButton
	private void altFlowAddButtonPressed() {

	}

	//内部コンポーネント押下時の処理：AltFlowカラム、RemoveButton
	private void altFlowRemoveButtonPressed() {

	}

	//内部コンポーネント押下時の処理：ExcFlowカラム、AddButton
	private void excFlowAddButtonPressed() {

	}

	//内部コンポーネント押下時の処理：ExcFlowカラム、RemoveButton
	private void excFlowRemoveButtonPressed() {

	}

	//内部コンポーネント押下時の処理：Stepカラム、AddButton
	private void stepAddButtonPressed() {

	}

	//内部コンポーネント押下時の処理：Stepカラム、RemoveButton
	private void stepRemoveButtonPressed() {

	}


	public void mouseWheel(MouseEvent event) {
		//カウント取得
		int e = event.getCount();

		if (MERGIN < mouseX && mouseX < MERGIN + COLUMN_WIDTH && 2 * MERGIN < mouseY && mouseY < height - MERGIN) {
			//Usecase部分のスクロールの場合
			firstUsecaseIndex = (firstUsecaseIndex + e < 0) ? 0 : (firstUsecaseIndex + e >= sToolEditor.fgm.getUsecases().size()) ? firstUsecaseIndex : firstUsecaseIndex + e;
		} else if (2 * MERGIN + COLUMN_WIDTH < mouseX && mouseX < 2 * MERGIN + 2 * COLUMN_WIDTH) {
			//Flow部分のスクロールの場合
			if (4 * MERGIN < mouseY && mouseY < 4 * MERGIN + ALT_EXC_HEIGHT) {
				//ALT_Flow
				firstAltFlowIndex = (firstAltFlowIndex + e < 0) ? 0 : firstAltFlowIndex + e;
			} else if (6 * MERGIN + ALT_EXC_HEIGHT < mouseY && mouseY < height - MERGIN) {
				//EXC_Flow
				firstExcFlowIndex = (firstExcFlowIndex + e < 0) ? 0 : firstExcFlowIndex + e;
			}
		} else if (3 * MERGIN + 2 * COLUMN_WIDTH < mouseX && mouseX < width - MERGIN && 2 * MERGIN < mouseY && mouseY < height - MERGIN) {
			//Step部分のスクロールの場合
			firstStepIndex = (firstStepIndex + e < 0) ? 0 : firstStepIndex + e;
		}
		sToolEditor.redraw();
	}

	public void mouseMoved() {
		redraw();
	}
}
