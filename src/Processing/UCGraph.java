package Processing;

import Core.SToolEditor;
import processing.core.*;
import processing.event.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 66 on 2015/10/11.
 */
public class UCGraph extends PApplet {

	//ButtonSetFrameとListBox
	private ButtonSetFrame usecaseBSF, altFlowBSF, excFlowBSF, stepBSF;
	private ListBox usecaseLB, altFlowLB, excFlowLB, stepLB;

	//本体
	private SToolEditor sToolEditor;

	//カラーパレット
	private final int COLOR_BACKGROUND = color(28, 28, 28);
	private final int COLOR_LINES = color(123, 144, 210);
	private final int COLOR_SELECTED = color(226, 148, 59);

	//各種描画値（finalじゃないやつは画面サイズで可変）
	private final int MERGIN = 30;
	private int COLUMN_WIDTH;
	private int ALT_EXC_HEIGHT;

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
		ALT_EXC_HEIGHT = (height - 7 * MERGIN) / 2;

		//ButtonSetFrameをSetup
		usecaseBSF = new ButtonSetFrame(MERGIN, MERGIN, COLUMN_WIDTH, MERGIN, "Usecase");
		usecaseBSF.addButton("↑");
		usecaseBSF.addButton("↓");
		altFlowBSF = new ButtonSetFrame(2 * MERGIN + COLUMN_WIDTH, 3 * MERGIN, COLUMN_WIDTH, MERGIN, "AltFlow");
		altFlowBSF.addButton("＋");
		altFlowBSF.addButton("－");
		excFlowBSF = new ButtonSetFrame(2 * MERGIN + COLUMN_WIDTH, 5 * MERGIN + ALT_EXC_HEIGHT, COLUMN_WIDTH, MERGIN, "ExcFlow");
		excFlowBSF.addButton("＋");
		excFlowBSF.addButton("－");
		stepBSF = new ButtonSetFrame(3 * MERGIN + 2 * COLUMN_WIDTH, MERGIN, COLUMN_WIDTH, MERGIN, "Step");
		stepBSF.addButton("＋");
		stepBSF.addButton("－");
	}

	public void draw() {
		background(COLOR_BACKGROUND);
		fill(COLOR_LINES);
		stroke(COLOR_LINES);
		noFill();
		strokeWeight(2);

		COLUMN_WIDTH = (width - 4 * MERGIN) / 3;
		ALT_EXC_HEIGHT = (height - 7 * MERGIN) / 2;

		textAlign(CENTER, CENTER);

		//ButtonSetFrame記述
		usecaseBSF.adjust(MERGIN, MERGIN, COLUMN_WIDTH, MERGIN);
		usecaseBSF.draw();
		altFlowBSF.adjust(2 * MERGIN + COLUMN_WIDTH, 3 * MERGIN, COLUMN_WIDTH, MERGIN);
		altFlowBSF.draw();
		excFlowBSF.adjust(2 * MERGIN + COLUMN_WIDTH, 5 * MERGIN + ALT_EXC_HEIGHT, COLUMN_WIDTH, MERGIN);
		excFlowBSF.draw();
		stepBSF.adjust(3 * MERGIN + 2 * COLUMN_WIDTH, MERGIN, COLUMN_WIDTH, MERGIN);
		stepBSF.draw();

		stroke(COLOR_LINES);

		//ListBox記述
		usecaseLB.adjust(MERGIN, 2 * MERGIN, COLUMN_WIDTH, height - 3 * MERGIN, MERGIN);
		usecaseLB.draw();
		altFlowLB.adjust(2 * MERGIN + COLUMN_WIDTH, 4 * MERGIN, COLUMN_WIDTH, ALT_EXC_HEIGHT, MERGIN);
		altFlowLB.draw();
		excFlowLB.adjust(2 * MERGIN + COLUMN_WIDTH, 6 * MERGIN + ALT_EXC_HEIGHT, COLUMN_WIDTH, ALT_EXC_HEIGHT, MERGIN);
		excFlowLB.draw();
		stepLB.adjust(3 * MERGIN + 2 * COLUMN_WIDTH, 2 * MERGIN, COLUMN_WIDTH, height - 3 * MERGIN, MERGIN);
		stepLB.draw();
	}


	class ButtonSetFrame {
		int x, y, w, h;
		String title;
		List<String> buttonList;

		public ButtonSetFrame(int x, int y, int w, int h, String title) {
			this.x = x;
			this.y = y;
			this.w = w;
			this.h = h;
			this.title = title;
			buttonList = new ArrayList<>();
		}

		public void addButton(String buttonLabel) {
			buttonList.add(buttonLabel);
		}

		/**
		 * ProcessingのWindowサイズ変更に対応するように調整
		 *
		 * @param x
		 * @param y
		 * @param w
		 * @param h
		 */
		public void adjust(int x, int y, int w, int h) {
			this.x = x;
			this.y = y;
			this.w = w;
			this.h = h;
		}

		/**
		 * こいつをクリック、クリックされたボタン番号を返す。
		 *
		 * @return ボタン番号、クリックがずれてるなら -1
		 */
		public int getButtonIdOnMouse(int mouseX, int mouseY) {
			for (int i = 0; i < buttonList.size(); i++) {
				if (x + i * h < mouseX && mouseX < x + i * h + h && y < mouseY && mouseY < y + h) {
					return i;
				}
			}
			return -1;
		}

		/**
		 * 描画
		 */
		public void draw() {
			textAlign(RIGHT);
			text(title, x, y, w, h);
			for (int i = 0; i < buttonList.size(); i++) {
				stroke((getButtonIdOnMouse(mouseX, mouseY) == i) ? COLOR_SELECTED : COLOR_LINES);
				ellipse(x + i * h + h / 2, y + h / 2, h - 4, h - 4);
				textAlign(CENTER, CENTER);
				text(buttonList.get(i), x + i * h, y, h, h);
			}
		}

	}


	//表示するListのクラス
	class ListBox {
		int x, y, w, h;
		int dh;

		public ListBox(int x, int y, int w, int h, int dh) {
			this.x = x;
			this.y = y;
			this.w = w;
			this.h = h;
			this.dh = dh;
		}

		public void draw() {

			//枠線
			rect(x, y, w, h);

		}
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
