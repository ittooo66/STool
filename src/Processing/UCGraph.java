package Processing;

import Swing.SToolEditor;
import Models.Step;
import Models.Usecase;
import processing.core.*;
import processing.event.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 66 on 2015/10/11.
 * UsecaseEditorのProcessing部分のクラス
 */
public class UCGraph extends PApplet {
	//選択中のUsecaseId,StepId,FlowType,FlowIndex
	public int selectedUsecaseId = -1;
	public int selectedStepId = -1;
	/**
	 * -1:未選択,0:main,1:alt,2:Exc
	 */
	public int selectedFlowType = -1;
	public int selectedFlowId = -1;


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
		//usecaseLB中身詰め込み+draw()
		List<ListBoxContent> lbc = new ArrayList<>();
		List<Usecase> usecases = sToolEditor.fgm.getUsecases();
		for (Usecase uc : usecases) lbc.add(new ListBoxContent(uc.id, uc.name));
		usecaseLB.setContents(lbc);
		usecaseLB.adjust(MERGIN, 2 * MERGIN, COLUMN_WIDTH, height - 3 * MERGIN, MERGIN, selectedUsecaseId);
		usecaseLB.draw();

		//Usecase取得
		Usecase uc = sToolEditor.fgm.getUsecaseById(selectedUsecaseId);
		List<List<Step>> altFlowList = new ArrayList<>();
		List<List<Step>> excFlowList = new ArrayList<>();

		//mainFlow記述
		stroke(selectedFlowType == 0 ? COLOR_SELECTED : COLOR_LINES);
		fill(selectedFlowType == 0 ? COLOR_SELECTED : COLOR_LINES);
		noFill();
		rect(2 * MERGIN + COLUMN_WIDTH, MERGIN, COLUMN_WIDTH, MERGIN);
		text("主系列", 2 * MERGIN + COLUMN_WIDTH, MERGIN, COLUMN_WIDTH, MERGIN);

		//altFlow中身詰め込み+draw()
		lbc = new ArrayList<>();
		if (uc != null) altFlowList = uc.getAlternativeFlowList();
		for (int i = 0; i < altFlowList.size(); i++) {
			lbc.add(new ListBoxContent(i, altFlowList.get(i).get(0).condition));
		}
		altFlowLB.setContents(lbc);
		altFlowLB.adjust(2 * MERGIN + COLUMN_WIDTH, 4 * MERGIN, COLUMN_WIDTH, ALT_EXC_HEIGHT, MERGIN, selectedFlowType == 1 ? selectedFlowId : -1);
		altFlowLB.draw();

		//excFlow中身詰め込み+draw()
		lbc = new ArrayList<>();
		if (uc != null) excFlowList = uc.getExceptionalFlowList();
		for (int i = 0; i < excFlowList.size(); i++) {
			lbc.add(new ListBoxContent(i, excFlowList.get(i).get(0).condition));
		}
		excFlowLB.setContents(lbc);
		excFlowLB.adjust(2 * MERGIN + COLUMN_WIDTH, 6 * MERGIN + ALT_EXC_HEIGHT, COLUMN_WIDTH, ALT_EXC_HEIGHT, MERGIN, selectedFlowType == 2 ? selectedFlowId : -1);
		excFlowLB.draw();

		//stepLB中身詰め込み+draw()
		lbc = new ArrayList<>();
		if (uc != null) {
			switch (selectedFlowType) {
				case 0:
					for (Step s : uc.getMainFlow()) {
						if (s.stepType == Step.StepType.ACTION) {
							lbc.add(new ListBoxContent(s.id, "action"));
						} else if (s.stepType == Step.StepType.INCLUDE) {
							lbc.add(new ListBoxContent(s.id, "include"));
						} else if (s.stepType == Step.StepType.GOTO) {
							lbc.add(new ListBoxContent(s.id, "goto"));
						}
						//TODO:ちゃんとした名前を表示できるように
					}
					break;
				case 1:
					for (Step s : uc.getAlternativeFlowList().get(selectedFlowId)) {
						//TODO:ちゃんとした名前を表示できるように
						if (s.stepType == Step.StepType.ACTION) {
							lbc.add(new ListBoxContent(s.id, "action"));
						} else if (s.stepType == Step.StepType.INCLUDE) {
							lbc.add(new ListBoxContent(s.id, "include"));
						} else if (s.stepType == Step.StepType.GOTO) {
							lbc.add(new ListBoxContent(s.id, "goto"));
						}
					}
					break;
				case 2:
					for (Step s : uc.getExceptionalFlowList().get(selectedFlowId)) {
						//TODO:ちゃんとした名前を表示できるように
						if (s.stepType == Step.StepType.ACTION) {
							lbc.add(new ListBoxContent(s.id, "action"));
						} else if (s.stepType == Step.StepType.INCLUDE) {
							lbc.add(new ListBoxContent(s.id, "include"));
						} else if (s.stepType == Step.StepType.GOTO) {
							lbc.add(new ListBoxContent(s.id, "goto"));
						}
					}
					break;
			}
		}
		stepLB.setContents(lbc);
		stepLB.adjust(3 * MERGIN + 2 * COLUMN_WIDTH, 2 * MERGIN, COLUMN_WIDTH, height - 3 * MERGIN, MERGIN, selectedStepId);
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
		 * @param x Processing上のX座標位置
		 * @param y Processing上のY座標位置
		 * @param w こいつの描画幅
		 * @param h こいつの描画高さ
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
		private int selectedId;
		//コンテンツ
		private List<ListBoxContent> contents;

		public ListBox() {
			contents = new ArrayList<>();
		}

		/**
		 * コンテンツ更新
		 *
		 * @param contents ListBoxの表示内容
		 */
		public void setContents(List<ListBoxContent> contents) {
			this.contents = contents;
		}

		public ListBoxContent getContentOnMouse(int mouseX, int mouseY) {
			for (int i = 0, j = scrollIndex; j < contents.size(); i++, j++) {
				if (mouseIsInRect(x, y + i * dh, w, dh, mouseX, mouseY)) return contents.get(j);
			}
			return null;
		}

		/**
		 * BOX項目をスクロール
		 *
		 * @param e +1:scrollDown,-1:scrollUp,0:reset
		 */
		public void scroll(int e) {
			scrollIndex = (scrollIndex + e > 0) ? (scrollIndex + e < contents.size()) ? scrollIndex + e : scrollIndex : 0;
			if (e == 0) scrollIndex = 0;
		}

		/**
		 * ProcessingのWindowサイズ変更に伴う各種値を調整
		 *
		 * @param x  Processing上のX座標位置
		 * @param y  Processing上のY座標位置
		 * @param w  こいつの描画幅
		 * @param h  こいつの描画高さ
		 * @param dh リスト１項目あたりの高さ
		 */
		public void adjust(int x, int y, int w, int h, int dh, int selectedId) {
			this.x = x;
			this.y = y;
			this.w = w;
			this.h = h;
			this.dh = dh;
			this.selectedId = selectedId;
		}

		public void draw() {
			fill(COLOR_LINES);
			noFill();
			stroke(COLOR_LINES);

			for (int i = 0, j = scrollIndex; j < contents.size() && i * dh < h; i++, j++) {
				rect(x + 2, y + 2 + i * dh, w - 4, dh - 4);
				text(contents.get(j).name, x, y + i * dh, w, dh);
			}
			for (int i = 0, j = scrollIndex; j < contents.size(); i++, j++) {
				if (selectedId == contents.get(j).id) {
					stroke(COLOR_SELECTED);
					rect(x + 2, y + 2 + i * dh, w - 4, dh - 4);
					fill(COLOR_SELECTED);
					text(contents.get(j).name, x, y + i * dh, w, dh);
				}
			}

			//はみ出し部分を塗りつぶし
			fill(COLOR_BACKGROUND);
			stroke(COLOR_BACKGROUND);
			rect(x - 2, y + h, w + 4, dh);

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

	private static boolean mouseIsInRect(int x, int y, int w, int h, int mouseX, int mouseY) {
		return (x < mouseX && mouseX < x + w && y < mouseY && mouseY < y + h);
	}

	public void mousePressed() {
		if (usecaseBSF.getButtonIdOnMouse(mouseX, mouseY) != -1) {
			int id = usecaseBSF.getButtonIdOnMouse(mouseX, mouseY);
			if (id == 0) {
				//Usecase移動（上向き）
				sToolEditor.fgm.moveUsecase(selectedUsecaseId, -1);
			} else if (id == 1) {
				//Usecase移動（下向き）
				sToolEditor.fgm.moveUsecase(selectedUsecaseId, 1);
			}
		} else if (altFlowBSF.getButtonIdOnMouse(mouseX, mouseY) != -1) {
			int id = altFlowBSF.getButtonIdOnMouse(mouseX, mouseY);
			if (id == 0 && selectedUsecaseId != -1) {
				//altFlow追加
				Usecase uc = sToolEditor.fgm.getUsecaseById(selectedUsecaseId);
				uc.addAlternativeFlow("代替：" + uc.getAlternativeFlowList().size());
				sToolEditor.fgm.editUsecase(selectedUsecaseId, uc);
			} else if (id == 1 && selectedUsecaseId != -1) {
				//altFlow削除
				if (selectedFlowType == 1 && selectedFlowId != -1) {
					Usecase uc = sToolEditor.fgm.getUsecaseById(selectedUsecaseId);
					uc.removeAlternativeFlow(selectedFlowId);
					selectedFlowId = -1;
					selectedStepId = -1;
					selectedFlowType = -1;
					sToolEditor.fgm.editUsecase(selectedUsecaseId, uc);
				}
			}
		} else if (excFlowBSF.getButtonIdOnMouse(mouseX, mouseY) != -1) {
			int id = excFlowBSF.getButtonIdOnMouse(mouseX, mouseY);
			if (id == 0 && selectedUsecaseId != -1) {
				//excFlow追加
				Usecase uc = sToolEditor.fgm.getUsecaseById(selectedUsecaseId);
				uc.addExceptionalFlow("例外：" + uc.getExceptionalFlowList().size());
				sToolEditor.fgm.editUsecase(selectedUsecaseId, uc);
			} else if (id == 1 && selectedUsecaseId != -1) {
				//excFlow削除
				if (selectedFlowType == 2 && selectedFlowId != -1) {
					Usecase uc = sToolEditor.fgm.getUsecaseById(selectedUsecaseId);
					uc.removeExceptionalFlow(selectedFlowId);
					selectedFlowId = -1;
					selectedStepId = -1;
					selectedFlowType = -1;
					sToolEditor.fgm.editUsecase(selectedUsecaseId, uc);
				}
			}
		} else if (stepBSF.getButtonIdOnMouse(mouseX, mouseY) != -1) {
			//TODO:stepButton押下時処理
			int id = stepBSF.getButtonIdOnMouse(mouseX, mouseY);
			if (id == 0 && selectedUsecaseId != -1 && selectedFlowType != -1) {
				//TODO:step追加
			} else if (id == 1 && selectedUsecaseId != -1) {
				//TODO:step削除
			}
		} else if (mouseIsInRect(2 * MERGIN + COLUMN_WIDTH, MERGIN, COLUMN_WIDTH, MERGIN, mouseX, mouseY)) {
			//mainFlow押下時処理
			if (selectedUsecaseId != -1) {
				selectedFlowType = 0;
				selectedFlowId = 0;
			}
		} else if (usecaseLB.getContentOnMouse(mouseX, mouseY) != null) {
			//UsecaseLB押下時処理
			if (usecaseLB.getContentOnMouse(mouseX, mouseY).id != selectedUsecaseId) {
				selectedUsecaseId = usecaseLB.getContentOnMouse(mouseX, mouseY).id;
			}
			selectedFlowType = -1;
			selectedFlowId = -1;
			selectedStepId = -1;
			altFlowLB.scroll(0);
			excFlowLB.scroll(0);
		} else if (altFlowLB.getContentOnMouse(mouseX, mouseY) != null) {
			//altFlowLB押下時処理
			if (selectedFlowType != 1 || altFlowLB.getContentOnMouse(mouseX, mouseY).id != selectedFlowId) {
				selectedFlowType = 1;
				selectedFlowId = altFlowLB.getContentOnMouse(mouseX, mouseY).id;
				selectedStepId = -1;
			}
		} else if (excFlowLB.getContentOnMouse(mouseX, mouseY) != null) {
			//excFlowLB押下時処理
			if (selectedFlowType != 2 || excFlowLB.getContentOnMouse(mouseX, mouseY).id != selectedFlowId) {
				selectedFlowType = 2;
				selectedFlowId = excFlowLB.getContentOnMouse(mouseX, mouseY).id;
				selectedStepId = -1;
			}
		} else if (stepLB.getContentOnMouse(mouseX, mouseY) != null) {
			//stepLB押下時処理
			selectedStepId = stepLB.getContentOnMouse(mouseX, mouseY).id;
		}
		sToolEditor.redraw();
	}

	public void mouseWheel(MouseEvent event) {
		//カウント取得
		int e = event.getCount() > 0 ? 1 : -1;

		if (mouseIsInRect(MERGIN, 2 * MERGIN, COLUMN_WIDTH, height - 3 * MERGIN, mouseX, mouseY)) {
			usecaseLB.scroll(e);
		} else if (mouseIsInRect(2 * MERGIN + COLUMN_WIDTH, 4 * MERGIN, COLUMN_WIDTH, ALT_EXC_HEIGHT, mouseX, mouseY)) {
			altFlowLB.scroll(e);
		} else if (mouseIsInRect(2 * MERGIN + COLUMN_WIDTH, 6 * MERGIN + ALT_EXC_HEIGHT, COLUMN_WIDTH, ALT_EXC_HEIGHT, mouseX, mouseY)) {
			excFlowLB.scroll(e);
		} else if (mouseIsInRect(3 * MERGIN + 2 * COLUMN_WIDTH, 2 * MERGIN, COLUMN_WIDTH, height - 3 * MERGIN, mouseX, mouseY)) {
			stepLB.scroll(e);
		}

		sToolEditor.redraw();
	}

	public void mouseMoved() {
		redraw();
	}
}
