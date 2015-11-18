package Processing;

import Models.FGModelAdapter;
import Models.Goal;
import Processing.Component.*;
import Swing.SToolEditor;
import Models.Step;
import Models.Usecase;
import processing.core.*;
import processing.event.*;

import java.util.ArrayList;
import java.util.List;

public class UCGraph extends PApplet {

	//選択中のUsecaseId,StepId,FlowType,FlowIndex
	public int selectedUsecaseId = -1;
	public int selectedStepId = -1;
	/**
	 * -1:未選択,0:main,1:alt,2:Exc
	 */
	public int selectedFlowType = -1;
	public int selectedFlowIndex = -1;

	//ButtonSetFrameとListBox
	private ButtonSetFrame usecaseBSF, altFlowBSF, excFlowBSF, stepBSF;
	private ListBox usecaseLB, mainFlowLB, altFlowLB, excFlowLB, stepLB;

	//本体
	private SToolEditor sToolEditor;

	//各種描画値（finalじゃないやつは画面サイズで可変）
	private final int MERGIN = 30;
	private int COLUMN_WIDTH;
	private int ALT_EXC_HEIGHT;

	public UCGraph(SToolEditor sToolEditor) {
		this.sToolEditor = sToolEditor;
	}

	public void setup() {
		//Font設定。
		PFont font = createFont("メイリオ ボールド", 15, true);
		textFont(font);
		//Smoothに描画
		smooth();

		//ButtonSetFrameをSetup
		usecaseBSF = new ButtonSetFrame("Usecases");
		usecaseBSF.addButton("↑");
		usecaseBSF.addButton("↓");
		altFlowBSF = new ButtonSetFrame("AlternativeFlows");
		altFlowBSF.addButton("＋");
		altFlowBSF.addButton("－");
		excFlowBSF = new ButtonSetFrame("ExceptionalFlows");
		excFlowBSF.addButton("＋");
		excFlowBSF.addButton("－");
		stepBSF = new ButtonSetFrame("Steps");
		stepBSF.addButton("＋");
		stepBSF.addButton("－");
		stepBSF.addButton("↑");
		stepBSF.addButton("↓");

		//ListBoxをSetup
		usecaseLB = new ListBox();
		mainFlowLB = new ListBox();
		altFlowLB = new ListBox();
		excFlowLB = new ListBox();
		stepLB = new ListBox();
	}

	//変更フラグ
	private boolean hasChanges;

	public void redraw() {
		hasChanges = true;
	}

	public void draw() {
		//省力draw()
		if (!hasChanges) return;
		else hasChanges = false;

		background(COLOR.BACKGROUND);

		COLUMN_WIDTH = (width - 4 * MERGIN) / 3;
		ALT_EXC_HEIGHT = (height - 8 * MERGIN) / 2;

		//ButtonSetFrame記述
		usecaseBSF.adjust(MERGIN, MERGIN, COLUMN_WIDTH, MERGIN);
		usecaseBSF.draw(this);
		altFlowBSF.adjust(2 * MERGIN + COLUMN_WIDTH, 4 * MERGIN, COLUMN_WIDTH, MERGIN);
		altFlowBSF.draw(this);
		excFlowBSF.adjust(2 * MERGIN + COLUMN_WIDTH, 6 * MERGIN + ALT_EXC_HEIGHT, COLUMN_WIDTH, MERGIN);
		excFlowBSF.draw(this);
		stepBSF.adjust(3 * MERGIN + 2 * COLUMN_WIDTH, MERGIN, COLUMN_WIDTH, MERGIN);
		stepBSF.draw(this);

		//usecaseLB中身詰め込み+draw()
		List<ListBoxContent> lbc = new ArrayList<>();
		for (Usecase uc : sToolEditor.fgm.getUsecases()) {
			//詰め込み外対象でなければ詰め込み
			Goal g = sToolEditor.fgm.getGoalById(uc.parentLeafGoalId);
			boolean isEnable = sToolEditor.fgm.getVersion() == FGModelAdapter.VERSION.ASIS ? g.isEnableForAsIs : g.isEnableForToBe;
			if (sToolEditor.fgm.getViewmode() != FGModelAdapter.VIEWMODE.REDUCED || isEnable)
				lbc.add(new ListBoxContent(uc.id, uc.name, isEnable));
		}
		usecaseLB.setContents(lbc);
		usecaseLB.adjust(MERGIN, 2 * MERGIN, COLUMN_WIDTH, height - 3 * MERGIN, MERGIN, selectedUsecaseId);
		usecaseLB.draw(this);

		//Usecase取得
		Usecase uc = sToolEditor.fgm.getUsecaseById(selectedUsecaseId);
		List<List<Step>> altFlowList = new ArrayList<>();
		List<List<Step>> excFlowList = new ArrayList<>();

		//mainFlow記述
		lbc = new ArrayList<>();
		if (selectedUsecaseId != -1) lbc.add(new ListBoxContent(0, "MainFlow"));
		mainFlowLB.setContents(lbc);
		mainFlowLB.adjust(2 * MERGIN + COLUMN_WIDTH, 2 * MERGIN, COLUMN_WIDTH, MERGIN, MERGIN, selectedFlowType);
		mainFlowLB.draw(this);

		//altFlow中身詰め込み+draw()
		lbc = new ArrayList<>();
		if (uc != null) altFlowList = uc.getAlternativeFlowList();
		for (int i = 0; i < altFlowList.size(); i++) {
			lbc.add(new ListBoxContent(i, altFlowList.get(i).get(0).condition));
		}
		altFlowLB.setContents(lbc);
		altFlowLB.adjust(2 * MERGIN + COLUMN_WIDTH, 5 * MERGIN, COLUMN_WIDTH, ALT_EXC_HEIGHT, MERGIN, selectedFlowType == 1 ? selectedFlowIndex : -1);
		altFlowLB.draw(this);

		//excFlow中身詰め込み+draw()
		lbc = new ArrayList<>();
		if (uc != null) excFlowList = uc.getExceptionalFlowList();
		for (int i = 0; i < excFlowList.size(); i++) {
			lbc.add(new ListBoxContent(i, excFlowList.get(i).get(0).condition));
		}
		excFlowLB.setContents(lbc);
		excFlowLB.adjust(2 * MERGIN + COLUMN_WIDTH, 7 * MERGIN + ALT_EXC_HEIGHT, COLUMN_WIDTH, ALT_EXC_HEIGHT, MERGIN, selectedFlowType == 2 ? selectedFlowIndex : -1);
		excFlowLB.draw(this);

		//stepLB中身詰め込み+draw()
		lbc = new ArrayList<>();
		if (uc != null) {
			int i = 0;
			switch (selectedFlowType) {
				case 0:
					for (Step s : uc.getMainFlow()) {
						i++;
						if (s.stepType != Step.StepType.EXC_INDEX && s.stepType != Step.StepType.ALT_INDEX)
							lbc.add(new ListBoxContent(s.id, i + ". " + s.getStepName(sToolEditor.fgm, uc)));
					}
					break;
				case 1:
					for (Step s : uc.getAlternativeFlowList().get(selectedFlowIndex)) {
						if (s.stepType != Step.StepType.EXC_INDEX && s.stepType != Step.StepType.ALT_INDEX)
							lbc.add(new ListBoxContent(s.id, i + ". " + s.getStepName(sToolEditor.fgm, uc)));
						i++;
					}
					break;
				case 2:
					for (Step s : uc.getExceptionalFlowList().get(selectedFlowIndex)) {
						if (s.stepType != Step.StepType.EXC_INDEX && s.stepType != Step.StepType.ALT_INDEX)
							lbc.add(new ListBoxContent(s.id, i + ". " + s.getStepName(sToolEditor.fgm, uc)));
						i++;
					}
					break;
			}
		}
		stepLB.setContents(lbc);
		stepLB.adjust(3 * MERGIN + 2 * COLUMN_WIDTH, 2 * MERGIN, COLUMN_WIDTH, height - 3 * MERGIN, MERGIN, selectedStepId);
		stepLB.draw(this);
	}

	public void mousePressed() {
		//マウス押下位置
		int x = mouseX;
		int y = mouseY;

		//UsecaseLB押下判定
		if (usecaseLB.getContentOnMouse(x, y) != null) {
			selectedUsecaseId = usecaseLB.getContentOnMouse(x, y).id;
			deselectFlow();
		}

		//選択中のUsecaseを取得、選択が不当（非選択）なら終了
		Usecase uc = sToolEditor.fgm.getUsecaseById(selectedUsecaseId);
		if (uc == null) return;

		//MainFlow押下時処理
		if (PUtility.mouseIsInRect(2 * MERGIN + COLUMN_WIDTH, 2 * MERGIN, COLUMN_WIDTH, MERGIN, x, y)) {
			selectedFlowType = 0;
			selectedFlowIndex = 0;
			deselectStep();
		}
		//altFlowLB押下判定
		if (altFlowLB.getContentOnMouse(x, y) != null) {
			selectedFlowIndex = altFlowLB.getContentOnMouse(x, y).id;
			selectedFlowType = 1;
			deselectStep();
		}
		//excFlowLB押下判定
		if (excFlowLB.getContentOnMouse(x, y) != null) {
			selectedFlowIndex = excFlowLB.getContentOnMouse(x, y).id;
			selectedFlowType = 2;
			deselectStep();
		}
		//stepLB押下判定
		if (stepLB.getContentOnMouse(x, y) != null) {
			selectedStepId = stepLB.getContentOnMouse(x, y).id;
		}

		//Usecase:ButtonSetFrame押下判定
		switch (usecaseBSF.getButtonIdOnMouse(x, y)) {
			case 0://Usecase移動（上向き）
				sToolEditor.fgm.moveUsecase(selectedUsecaseId, -1);
				break;
			case 1://Usecase移動（下向き）
				sToolEditor.fgm.moveUsecase(selectedUsecaseId, 1);
				break;
		}
		//AltFlow:ButtonSetFrame押下判定
		switch (altFlowBSF.getButtonIdOnMouse(x, y)) {
			case 0://altFlow追加
				uc.addAlternativeFlow("Alternative:" + (uc.getAlternativeFlowList().size() + 1));
				break;
			case 1://altFlow削除
				if (selectedFlowType == 1) {
					uc.removeAlternativeFlow(selectedFlowIndex);
					deselectFlow();
				}
		}
		//ExcFlow:ButtonSetFrame押下判定
		switch (excFlowBSF.getButtonIdOnMouse(x, y)) {
			case 0://excFlow追加
				uc.addExceptionalFlow("Exception:" + (uc.getExceptionalFlowList().size() + 1));
				break;
			case 1://excFlow削除
				if (selectedFlowType == 2) {
					uc.removeExceptionalFlow(selectedFlowIndex);
					deselectFlow();
				}
		}
		//Step:ButtonSetFrame押下判定
		switch (stepBSF.getButtonIdOnMouse(x, y)) {
			case 0://step追加
				uc.addStep(selectedFlowType, selectedFlowIndex);
				break;
			case 1://step削除
				uc.removeStep(selectedStepId);
				deselectStep();
				break;
			case 2://step上に移動
				uc.moveStep(selectedStepId, -1);
				break;
			case 3://step下に移動
				uc.moveStep(selectedStepId, 1);
		}

		//上記のボタン押下による変更を適用
		sToolEditor.fgm.editUsecase(uc.id, uc);

		sToolEditor.redraw();
	}

	private void deselectFlow() {
		selectedFlowType = -1;
		selectedFlowIndex = -1;
		altFlowLB.scroll(0);
		excFlowLB.scroll(0);
		deselectStep();
	}

	private void deselectStep() {
		selectedStepId = -1;
		stepLB.scroll(0);
	}

	public void mouseWheel(MouseEvent event) {
		//マウス位置取得
		int x = mouseX;
		int y = mouseY;
		//カウント取得
		int e = event.getCount() > 0 ? 1 : -1;
		//スクロール箇所の特定・適用
		if (usecaseLB.isOn(x, y)) usecaseLB.scroll(e);
		if (altFlowLB.isOn(x, y)) altFlowLB.scroll(e);
		if (excFlowLB.isOn(x, y)) excFlowLB.scroll(e);
		if (stepLB.isOn(x, y)) stepLB.scroll(e);
		//再描画
		redraw();
	}

	public void mouseMoved() {
		redraw();
	}
}
