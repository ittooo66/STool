package Processing;

import Models.*;
import Models.FGModelAdapter.VERSION;
import Models.Step.StepType;
import Processing.Component.ButtonSetFrame;
import Processing.Component.COLOR;
import Processing.Component.ListBox;
import Processing.Component.ListBoxContent;
import processing.core.PApplet;
import processing.event.MouseEvent;

import java.util.ArrayList;
import java.util.List;

public class PScenarioEditor extends PApplet {

	private FGModelAdapter fgm;
	private Scenario scenario;

	//選択中のUsecaseId,StepId,FlowType,FlowIndex
	public int selectedUsecaseId = -1;
	public int selectedStepId = -1;
	/**
	 * -1:未選択,0:main,1:alt,2:Exc
	 */
	public int selectedFlowType = -1;
	public int selectedFlowIndex = -1;
	public int selectedScenarioIndex = -1;

	//ButtonSetFrameとListBox
	private ButtonSetFrame usecaseBSF, altFlowBSF, excFlowBSF, stepBSF, scenarioBSF, addBSF;
	private ListBox usecaseLB, mainFlowLB, altFlowLB, excFlowLB, stepLB, scenarioLB;

	public PScenarioEditor(FGModelAdapter fgm, Scenario scenario) {
		this.fgm = fgm;
		this.scenario = scenario;
	}

	public void setup() {
		//Font設定。
		textFont(createFont("メイリオ ボールド", 15, true));
		//Smoothに描画
		smooth();

		//ButtonSetFrame
		scenarioBSF = new ButtonSetFrame("Scenario");
		scenarioBSF.addButton("－");
		scenarioBSF.addButton("↑");
		scenarioBSF.addButton("↓");
		addBSF = new ButtonSetFrame("");
		addBSF.addButton("↑");
		usecaseBSF = new ButtonSetFrame("Usecases");
		altFlowBSF = new ButtonSetFrame("AlternativeFlows");
		excFlowBSF = new ButtonSetFrame("ExceptionalFlows");
		stepBSF = new ButtonSetFrame("Steps");

		//ListBox
		scenarioLB = new ListBox();
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

		//描画用各種パラメータ
		int MERGIN = 30;
		int SCENARIO_WIDTH = width - 2 * MERGIN;
		int SCENARIO_HEIGHT = height / 2 - 3 * MERGIN;
		int COLUMN_WIDTH = (width - 4 * MERGIN) / 3;
		int ALT_EXC_HEIGHT = (height / 2 - 8 * MERGIN) / 2;

		//コンポーネント位置調整
		addBSF.adjust(width / 2 - MERGIN / 2, height / 2 - MERGIN / 2, 3 * MERGIN, MERGIN);
		scenarioBSF.adjust(MERGIN, MERGIN, SCENARIO_WIDTH, MERGIN);
		usecaseBSF.adjust(MERGIN, MERGIN + height / 2, COLUMN_WIDTH, MERGIN);
		excFlowBSF.adjust(2 * MERGIN + COLUMN_WIDTH, 6 * MERGIN + ALT_EXC_HEIGHT + height / 2, COLUMN_WIDTH, MERGIN);
		stepBSF.adjust(3 * MERGIN + 2 * COLUMN_WIDTH, MERGIN + height / 2, COLUMN_WIDTH, MERGIN);
		altFlowBSF.adjust(2 * MERGIN + COLUMN_WIDTH, 4 * MERGIN + height / 2, COLUMN_WIDTH, MERGIN);

		scenarioLB.adjust(MERGIN, 2 * MERGIN, SCENARIO_WIDTH, SCENARIO_HEIGHT, MERGIN, selectedScenarioIndex);
		usecaseLB.adjust(MERGIN, 2 * MERGIN + height / 2, COLUMN_WIDTH, height / 2 - 3 * MERGIN, MERGIN, selectedUsecaseId);
		mainFlowLB.adjust(2 * MERGIN + COLUMN_WIDTH, 2 * MERGIN + height / 2, COLUMN_WIDTH, MERGIN, MERGIN, selectedFlowType);
		altFlowLB.adjust(2 * MERGIN + COLUMN_WIDTH, 5 * MERGIN + height / 2, COLUMN_WIDTH, ALT_EXC_HEIGHT, MERGIN, selectedFlowType == 1 ? selectedFlowIndex : -1);
		excFlowLB.adjust(2 * MERGIN + COLUMN_WIDTH, 7 * MERGIN + ALT_EXC_HEIGHT + height / 2, COLUMN_WIDTH, ALT_EXC_HEIGHT, MERGIN, selectedFlowType == 2 ? selectedFlowIndex : -1);
		stepLB.adjust(3 * MERGIN + 2 * COLUMN_WIDTH, 2 * MERGIN + height / 2, COLUMN_WIDTH, height / 2 - 3 * MERGIN, MERGIN, selectedStepId);

		//scenarioLB中身詰め込み
		List<ListBoxContent> lbc = new ArrayList<>();
		for (int i = 0; i < scenario.size(); i++) {
			lbc.add(new ListBoxContent(i, scenario.getStepName(i, fgm)));
		}
		scenarioLB.setContents(lbc);

		//usecaseLB中身詰め込み
		lbc = new ArrayList<>();
		for (Usecase uc : fgm.getUsecases()) {
			//詰め込み外対象でなければ詰め込み
			Goal g = fgm.getGoalById(uc.parentLeafGoalId);
			if (fgm.getVersion() == VERSION.ASIS) {
				if (g.isEnableForAsIs)
					lbc.add(new ListBoxContent(uc.id, uc.name, true));
			} else if (fgm.getVersion() == VERSION.TOBE) {
				if (g.isEnableForToBe)
					lbc.add(new ListBoxContent(uc.id, uc.name, true));
			}
		}
		usecaseLB.setContents(lbc);

		//Usecase取得
		Usecase uc = fgm.getUsecaseById(selectedUsecaseId);
		List<List<Step>> altFlowList = new ArrayList<>();
		List<List<Step>> excFlowList = new ArrayList<>();

		//mainFlow記述
		lbc = new ArrayList<>();
		if (selectedUsecaseId != -1) lbc.add(new ListBoxContent(0, "MainFlow"));
		mainFlowLB.setContents(lbc);

		//altFlow中身詰め込み
		lbc = new ArrayList<>();
		if (uc != null) altFlowList = uc.getAlternativeFlowList();
		for (int i = 0; i < altFlowList.size(); i++) {
			lbc.add(new ListBoxContent(i, altFlowList.get(i).get(0).condition));
		}
		altFlowLB.setContents(lbc);

		//excFlow中身詰め込み
		lbc = new ArrayList<>();
		if (uc != null) excFlowList = uc.getExceptionalFlowList();
		for (int i = 0; i < excFlowList.size(); i++) {
			lbc.add(new ListBoxContent(i, excFlowList.get(i).get(0).condition));
		}
		excFlowLB.setContents(lbc);

		//stepLB中身詰め込み
		lbc = new ArrayList<>();
		if (uc != null) {
			int i = 0;
			switch (selectedFlowType) {
				case 0:
					for (Step s : uc.getMainFlow()) {
						i++;
						if (s.stepType != StepType.EXC_INDEX && s.stepType != StepType.ALT_INDEX)
							lbc.add(new ListBoxContent(s.id, i + ". " + s.getStepName(fgm, uc)));
					}
					break;
				case 1:
					for (Step s : uc.getAlternativeFlowList().get(selectedFlowIndex)) {
						if (s.stepType != StepType.EXC_INDEX && s.stepType != StepType.ALT_INDEX)
							lbc.add(new ListBoxContent(s.id, i + ". " + s.getStepName(fgm, uc)));
						i++;
					}
					break;
				case 2:
					for (Step s : uc.getExceptionalFlowList().get(selectedFlowIndex)) {
						if (s.stepType != StepType.EXC_INDEX && s.stepType != StepType.ALT_INDEX)
							lbc.add(new ListBoxContent(s.id, i + ". " + s.getStepName(fgm, uc)));
						i++;
					}
					break;
			}
		}
		stepLB.setContents(lbc);

		//draw
		scenarioBSF.draw(this);
		usecaseBSF.draw(this);
		altFlowBSF.draw(this);
		excFlowBSF.draw(this);
		stepBSF.draw(this);
		scenarioLB.draw(this);
		addBSF.draw(this);
		usecaseLB.draw(this);
		mainFlowLB.draw(this);
		altFlowLB.draw(this);
		excFlowLB.draw(this);
		stepLB.draw(this);
	}

	public void mousePressed() {
		//マウス押下位置
		int x = mouseX;
		int y = mouseY;

		//ScenarioLB押下判定
		if (scenarioLB.getContentOnMouse(x, y) != null) {
			selectedScenarioIndex = scenarioLB.getContentOnMouse(x, y).id;
		}

		//UsecaseLB押下判定
		if (usecaseLB.getContentOnMouse(x, y) != null) {
			selectedUsecaseId = usecaseLB.getContentOnMouse(x, y).id;
			deselectFlow();
		}

		//選択中のUsecaseを取得、選択が不当（非選択）なら終了
		Usecase uc = fgm.getUsecaseById(selectedUsecaseId);
		if (uc == null) return;

		//MainFlow押下時処理
		if (mainFlowLB.isOn(x, y)) {
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

		switch (addBSF.getButtonIdOnMouse(x, y)) {
			case 0://AddButton
				addStepsToScenario();
				break;
		}
		switch (scenarioBSF.getButtonIdOnMouse(x, y)) {
			case 0://removeScenario
				scenario.removeStep(selectedScenarioIndex);
				break;
			case 1://moveUpScenario
				if (scenario.moveStep(false, selectedScenarioIndex))
					selectedScenarioIndex--;
				break;
			case 2://moveDnScenario
				if (scenario.moveStep(true, selectedScenarioIndex))
					selectedScenarioIndex++;
				break;
		}

		redraw();
	}

	/**
	 * 選択されたケースのシナリオを追加
	 */
	private void addStepsToScenario() {
		if (selectedUsecaseId == -1) {
			return;
		} else if (selectedFlowType == -1) {
			//Usecaseまるごと追加
			Usecase uc = fgm.getUsecaseById(selectedUsecaseId);
			if (uc == null) return;
			for (Step s : uc.getAllActionStep()) {
				scenario.addStep(s, uc);
			}
		} else if (selectedStepId == -1) {
			//Flowまるごと追加
			Usecase uc = fgm.getUsecaseById(selectedUsecaseId);
			if (uc == null) return;
			List<Step> stepList = null;
			switch (selectedFlowType) {
				case 0:
					stepList = uc.getMainFlow();
					break;
				case 1:
					stepList = uc.getAlternativeFlowList().get(selectedFlowIndex);
					break;
				case 2:
					stepList = uc.getExceptionalFlowList().get(selectedFlowIndex);
					break;
			}
			if (stepList == null) return;
			for (Step s : stepList) {
				if (s.stepType == StepType.ACTION) scenario.addStep(s, uc);
			}
		} else {
			//Stepをひとつ追加
			Usecase uc = fgm.getUsecaseById(selectedUsecaseId);
			Step s = null;
			if (uc != null)
				s = uc.getStepById(selectedStepId);
			if (s != null)
				scenario.addStep(s, uc);
		}
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
		if (scenarioLB.isOn(x, y)) scenarioLB.scroll(e);
		//再描画
		redraw();
	}

	public void mouseMoved() {
		redraw();
	}


}
