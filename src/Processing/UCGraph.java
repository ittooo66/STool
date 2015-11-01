package Processing;

import Core.SToolEditor;
import Models.Usecase;
import processing.core.*;
import processing.event.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 66 on 2015/10/11.
 */
public class UCGraph extends PApplet {

	public int selectedUsecaseId = -1;

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

		//ButtonSetFrameをSetup
		usecaseBSF = new ButtonSetFrame("Usecase");
		usecaseBSF.addButton("↑");
		usecaseBSF.addButton("↓");
		altFlowBSF = new ButtonSetFrame("AltFlow");
		altFlowBSF.addButton("＋");
		altFlowBSF.addButton("－");
		excFlowBSF = new ButtonSetFrame("ExcFlow");
		excFlowBSF.addButton("＋");
		excFlowBSF.addButton("－");
		stepBSF = new ButtonSetFrame("Step");
		stepBSF.addButton("＋");
		stepBSF.addButton("－");

		//ListBoxをSetup
		usecaseLB = new ListBox();
		altFlowLB = new ListBox();
		excFlowLB = new ListBox();
		stepLB = new ListBox();
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

		//TODO:ASIS,TOBE,ALL,REDUCEDを考慮した詰め込みにする
		//usecaseLB中身詰め込み
		List<ListBoxContent> lbc = new ArrayList<ListBoxContent>();
		List<Usecase> usecases = sToolEditor.fgm.getUsecases();
		for (Usecase uc : usecases) lbc.add(new ListBoxContent(uc.id, uc.name));
		usecaseLB.setContents(lbc);
		//ListBox記述
		usecaseLB.adjust(MERGIN, 2 * MERGIN, COLUMN_WIDTH, height - 3 * MERGIN, MERGIN);
		usecaseLB.draw();

		//TODO:altFlowLB中身詰め込み
		altFlowLB.adjust(2 * MERGIN + COLUMN_WIDTH, 4 * MERGIN, COLUMN_WIDTH, ALT_EXC_HEIGHT, MERGIN);
		altFlowLB.draw();

		//TODO:excFlowLB中身詰め込み
		excFlowLB.adjust(2 * MERGIN + COLUMN_WIDTH, 6 * MERGIN + ALT_EXC_HEIGHT, COLUMN_WIDTH, ALT_EXC_HEIGHT, MERGIN);
		excFlowLB.draw();

		//TODO:stepLB中身詰め込み
		stepLB.adjust(3 * MERGIN + 2 * COLUMN_WIDTH, 2 * MERGIN, COLUMN_WIDTH, height - 3 * MERGIN, MERGIN);
		stepLB.draw();
	}

	/**
	 * 表示するButtonクラス
	 */
	class ButtonSetFrame {
		private int x, y, w, h;
		private String title;
		private List<String> buttonList;

		public ButtonSetFrame(String title) {
			this.title = title;
			buttonList = new ArrayList<>();
		}

		public void addButton(String buttonLabel) {
			buttonList.add(buttonLabel);
		}

		/**
		 * ProcessingのWindowサイズ変更に対応するように各種値を調整
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

	/**
	 * 表示するListクラス
	 */
	class ListBox {
		//dh:１カラムの幅
		private int x, y, w, h, dh;
		//現在スクロールされている量
		private int scrollIndex;
		//コンテンツ
		private List<ListBoxContent> contents;

		public ListBox() {
			contents = new ArrayList<>();
		}

		/**
		 * コンテンツ更新
		 *
		 * @param contents
		 */
		public void setContents(List<ListBoxContent> contents) {
			this.contents = contents;
		}

		public ListBoxContent getContentOnMouse(int mouseX, int mouseY) {
			for (int i = 0, j = scrollIndex; j < contents.size(); i++, j++) {
				if (mouseIsInRect(x, y + i * dh, w, dh)) return contents.get(j);
			}
			return null;
		}

		public void scroll(int e) {
			scrollIndex = (scrollIndex + e > 0) ? (scrollIndex + e < contents.size()) ? scrollIndex + e : scrollIndex : 0;
		}

		/**
		 * ProcessingのWindowサイズ変更に伴う各種値を調整
		 *
		 * @param x
		 * @param y
		 * @param w
		 * @param h
		 * @param dh
		 */
		public void adjust(int x, int y, int w, int h, int dh) {
			this.x = x;
			this.y = y;
			this.w = w;
			this.h = h;
			this.dh = dh;
		}

		public void draw() {
			fill(COLOR_LINES);
			noFill();
			stroke(COLOR_LINES);

			for (int i = 0, j = scrollIndex; j < contents.size() && i * dh < h; i++, j++) {
				rect(x, y + i * dh, w, dh);
				text(contents.get(j).name, x, y + i * dh, w, dh);
			}
			for (int i = 0, j = scrollIndex; j < contents.size(); i++, j++) {
				if (selectedUsecaseId == contents.get(j).id) {
					stroke(COLOR_SELECTED);
					rect(x, y + i * dh, w, dh);
					fill(COLOR_SELECTED);
					text(contents.get(j).name, x, y + i * dh, w, dh);
				}
			}

			//はみ出し部分を塗りつぶし
			fill(COLOR_BACKGROUND);
			stroke(COLOR_BACKGROUND);
			rect(x-2,y+h,w+4,dh);

			//枠線
			stroke(COLOR_LINES);
			noFill();
			rect(x, y, w, h);
		}
	}

	/**
	 * ListBoxに詰めるコンテンツ
	 */
	class ListBoxContent {
		public int id;
		public String name;

		public ListBoxContent(int id, String name) {
			this.id = id;
			this.name = name;
		}
	}

	private boolean mouseIsInRect(int x, int y, int w, int h) {
		return (x < mouseX && mouseX < x + w && y < mouseY && mouseY < y + h) ? true : false;
	}

	public void mousePressed() {
		if (usecaseBSF.getButtonIdOnMouse(mouseX, mouseY) != -1) {
			System.out.println("use:" + usecaseBSF.getButtonIdOnMouse(mouseX, mouseY));
		} else if (altFlowBSF.getButtonIdOnMouse(mouseX, mouseY) != -1) {
			System.out.println("alt:" + altFlowBSF.getButtonIdOnMouse(mouseX, mouseY));
		} else if (excFlowBSF.getButtonIdOnMouse(mouseX, mouseY) != -1) {
			System.out.println("exc:" + excFlowBSF.getButtonIdOnMouse(mouseX, mouseY));
		} else if (stepBSF.getButtonIdOnMouse(mouseX, mouseY) != -1) {
			System.out.println("ste:" + stepBSF.getButtonIdOnMouse(mouseX, mouseY));
		} else if (2 * MERGIN + COLUMN_WIDTH < mouseX && mouseX < 2 * MERGIN + 2 * COLUMN_WIDTH && MERGIN < mouseY && mouseY < 2 * MERGIN) {
			System.out.println("main:");
		} else if (usecaseLB.getContentOnMouse(mouseX, mouseY) != null) {
			selectedUsecaseId = usecaseLB.getContentOnMouse(mouseX, mouseY).id;
		} else if (altFlowLB.getContentOnMouse(mouseX, mouseY) != null) {
			System.out.println("altFlowLB:" + altFlowLB.getContentOnMouse(mouseX, mouseY));
		} else if (excFlowLB.getContentOnMouse(mouseX, mouseY) != null) {
			System.out.println("excFlowLB:" + excFlowLB.getContentOnMouse(mouseX, mouseY));
		} else if (stepLB.getContentOnMouse(mouseX, mouseY) != null) {
			System.out.println("stepLB:" + stepLB.getContentOnMouse(mouseX, mouseY));
		}
		sToolEditor.redraw();
	}

	public void mouseWheel(MouseEvent event) {
		//カウント取得
		int e = event.getCount() > 0 ? 1 : -1;

		if (mouseIsInRect(MERGIN, 2 * MERGIN, COLUMN_WIDTH, height - 3 * MERGIN)) {
			usecaseLB.scroll(e);
		} else if (mouseIsInRect(2 * MERGIN + COLUMN_WIDTH, 4 * MERGIN, COLUMN_WIDTH, ALT_EXC_HEIGHT)) {
			altFlowLB.scroll(e);
		} else if (mouseIsInRect(2 * MERGIN + COLUMN_WIDTH, 6 * MERGIN + ALT_EXC_HEIGHT, COLUMN_WIDTH, ALT_EXC_HEIGHT)) {
			excFlowLB.scroll(e);
		} else if (mouseIsInRect(3 * MERGIN + 2 * COLUMN_WIDTH, 2 * MERGIN, COLUMN_WIDTH, height - 3 * MERGIN)) {
			stepLB.scroll(e);
		}

		sToolEditor.redraw();
	}

	public void mouseMoved() {
		redraw();
	}
}
