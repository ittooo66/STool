package Swing;

import Models.Domain;
import Models.Step;
import Models.Usecase;
import Processing.UCGraph;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;

public class UCEditPanel extends JPanel implements ActionListener, DocumentListener {
	private final UCGraph ucg;
	private final SToolEditor ste;

	private JButton jump;
	private JTextArea nameArea, conditionArea, eventNameArea;
	private JPanel conditionAreaPanel, sourceStepComboBoxPanel;
	private JPanel parentGoalNameLabelPanel, nameAreaPanel;
	private JPanel stepTypePanel, subjectComboBoxPanel, objectComboBoxPanel, eventNameAreaPanel, toComboBoxPanel;
	private JRadioButton stepTypeNop, stepTypeAction, stepTypeGoto, stepTypeInclude;
	private JLabel parentGoalNameLabel;
	private JComboBox sourceStepComboBox, subjectComboBox, objectComboBox, toComboBox;
	private List<Integer> sourceStepComboBoxIdList, objectAndSubjectComboBoxIdList, toComboBoxIdList;

	//Draw中のフラグ
	private boolean isDrawing;

	public UCEditPanel(SToolEditor ste, UCGraph ucg) {
		this.ste = ste;
		this.ucg = ucg;
		this.setLayout(new FlowLayout(FlowLayout.LEFT));
		this.setPreferredSize(new Dimension(0, 80));
		this.setBorder(new EtchedBorder());

		//NameTextArea
		nameArea = new JTextArea(2, 15);
		nameArea.getDocument().addDocumentListener(this);
		nameAreaPanel = new JPanel();
		nameAreaPanel.add(new JScrollPane(nameArea, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER));
		nameAreaPanel.setBorder(new TitledBorder(new EtchedBorder(), "Usecase Name"));
		this.add(nameAreaPanel);
		//parentGoalLabel
		parentGoalNameLabel = new JLabel("null");
		parentGoalNameLabel.setPreferredSize(new Dimension(160, 20));
		parentGoalNameLabelPanel = new JPanel();
		parentGoalNameLabelPanel.add(parentGoalNameLabel);
		parentGoalNameLabelPanel.setBorder(new TitledBorder(new EtchedBorder(), "Parent Goal"));
		this.add(parentGoalNameLabelPanel);
		//JumpButton
		jump = new JButton("Jump to Parent Goal");
		jump.addActionListener(e -> ste.jumpToGGTab(ste.fgm.getUsecaseById(ucg.selectedUsecaseId).parentLeafGoalId));
		this.add(jump);

		//Condition
		conditionArea = new JTextArea(2, 15);
		conditionArea.getDocument().addDocumentListener(this);
		conditionAreaPanel = new JPanel();
		conditionAreaPanel.add(new JScrollPane(conditionArea, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER));
		conditionAreaPanel.setBorder(new TitledBorder(new EtchedBorder(), "Condition"));
		this.add(conditionAreaPanel);
		//sourceStepComboBox周り
		sourceStepComboBox = new JComboBox();
		sourceStepComboBox.setPreferredSize(new Dimension(160, 20));
		sourceStepComboBox.addActionListener(this);
		sourceStepComboBoxPanel = new JPanel();
		sourceStepComboBoxPanel.add(sourceStepComboBox);
		sourceStepComboBoxPanel.setBorder(new TitledBorder(new EtchedBorder(), "SourceStep"));
		this.add(sourceStepComboBoxPanel);
		sourceStepComboBoxIdList = new ArrayList<>();

		//StepType
		stepTypeNop = new JRadioButton("NOP");
		stepTypeNop.setSelected(true);
		stepTypeNop.addActionListener(this);
		stepTypeAction = new JRadioButton("ACTION");
		stepTypeAction.addActionListener(this);
		stepTypeGoto = new JRadioButton("GOTO");
		stepTypeGoto.addActionListener(this);
		stepTypeInclude = new JRadioButton("INCLUDE");
		stepTypeInclude.addActionListener(this);
		//stepTypeButtonGroup作成
		ButtonGroup stepTypeButtonGroup = new ButtonGroup();
		stepTypeButtonGroup.add(stepTypeNop);
		stepTypeButtonGroup.add(stepTypeAction);
		stepTypeButtonGroup.add(stepTypeGoto);
		stepTypeButtonGroup.add(stepTypeInclude);
		//StepTypeグループのラベル（パネル）作成
		stepTypePanel = new JPanel();
		stepTypePanel.add(stepTypeNop);
		stepTypePanel.add(stepTypeAction);
		stepTypePanel.add(stepTypeGoto);
		stepTypePanel.add(stepTypeInclude);
		stepTypePanel.setBorder(new TitledBorder(new EtchedBorder(), "StepType"));
		stepTypePanel.setVisible(false);
		this.add(stepTypePanel);

		//subjectComboBox
		subjectComboBox = new JComboBox();
		subjectComboBox.setPreferredSize(new Dimension(160, 20));
		subjectComboBox.addActionListener(this);
		subjectComboBoxPanel = new JPanel();
		subjectComboBoxPanel.add(subjectComboBox);
		subjectComboBoxPanel.setBorder(new TitledBorder(new EtchedBorder(), "Subject"));
		this.add(subjectComboBoxPanel);
		//eventNameArea
		eventNameArea = new JTextArea(1, 15);
		eventNameArea.getDocument().addDocumentListener(this);
		eventNameAreaPanel = new JPanel();
		eventNameAreaPanel.add(new JScrollPane(eventNameArea, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER));
		eventNameAreaPanel.setBorder(new TitledBorder(new EtchedBorder(), "Event"));
		this.add(eventNameAreaPanel);
		//objectComboBox
		objectComboBox = new JComboBox();
		objectComboBox.setPreferredSize(new Dimension(160, 20));
		objectComboBox.addActionListener(this);
		objectComboBoxPanel = new JPanel();
		objectComboBoxPanel.add(objectComboBox);
		objectComboBoxPanel.setBorder(new TitledBorder(new EtchedBorder(), "Object"));
		this.add(objectComboBoxPanel);
		objectAndSubjectComboBoxIdList = new ArrayList<>();

		//toComboBox
		toComboBox = new JComboBox();
		toComboBox.setPreferredSize(new Dimension(160, 20));
		toComboBox.addActionListener(this);
		toComboBoxPanel = new JPanel();
		toComboBoxPanel.add(toComboBox);
		toComboBoxPanel.setBorder(new TitledBorder(new EtchedBorder(), "To"));
		this.add(toComboBoxPanel);
		toComboBoxIdList = new ArrayList<>();
	}

	public void redraw() {
		isDrawing = true;

		//選択中フラグ
		boolean usecaseSelected = ucg.selectedUsecaseId != -1 && ucg.selectedFlowIndex == -1 && ucg.selectedStepId == -1;
		boolean flowSelected = ucg.selectedUsecaseId != -1 && ucg.selectedFlowType != -1 && ucg.selectedStepId == -1;
		boolean stepSelected = ucg.selectedUsecaseId != -1 && ucg.selectedFlowType != -1 && ucg.selectedStepId != -1;

		try {
			Usecase selectedUsecase = ste.fgm.getUsecaseById(ucg.selectedUsecaseId);
			if (selectedUsecase != null) {
				Step selectedStep = selectedUsecase.getStepById(ucg.selectedStepId);

				if (usecaseSelected) {
					//親ゴールの名前を表示
					parentGoalNameLabel.setText(ste.fgm.getGoalById(selectedUsecase.parentLeafGoalId).name);
					//Usecase名前詰め込んで描画
					if (!nameArea.hasFocus()) nameArea.setText(selectedUsecase.name);
				}

				if (flowSelected) {
					//sourceStepComboBox更新
					sourceStepComboBoxIdList.clear();
					sourceStepComboBox.removeAllItems();
					List<Step> ls = selectedUsecase.getMainFlow();
					for (Step s : ls) {
						sourceStepComboBox.addItem(s.getStepName(ste.fgm, selectedUsecase));
						sourceStepComboBoxIdList.add(s.id);
					}

					//flowの頭出し用ステップ取得
					Step flowIndex = null;
					List<Step> sl = null;
					switch (ucg.selectedFlowType) {
						case 1:
							sl = selectedUsecase.getAlternativeFlowList().get(ucg.selectedFlowIndex);
							break;
						case 2:
							sl = selectedUsecase.getExceptionalFlowList().get(ucg.selectedFlowIndex);
							break;
					}
					if (sl != null) flowIndex = sl.get(0);

					//sourceStepComboBox選択
					if (flowIndex != null) {
						for (int id : sourceStepComboBoxIdList) {
							if (id == flowIndex.sourceStepId)
								sourceStepComboBox.setSelectedIndex(sourceStepComboBoxIdList.indexOf(id));
						}
					}

					//Condition名前詰め込み
					if (flowIndex != null && !conditionArea.hasFocus()) {
						conditionArea.setText(flowIndex.condition);
					}
				}

				if (stepSelected) {
					//StepType描画
					switch (selectedStep.stepType) {
						case NOP:
							stepTypeNop.setSelected(true);
							break;
						case ACTION:
							stepTypeAction.setSelected(true);
							break;
						case INCLUDE:
							stepTypeInclude.setSelected(true);
							break;
						case GOTO:
							stepTypeGoto.setSelected(true);
							break;
					}

					// GOTO or INCLUDE ComboBox中身更新
					toComboBoxIdList.clear();
					toComboBox.removeAllItems();
					switch (selectedStep.stepType) {
						case INCLUDE:
							for (Usecase u : ste.fgm.getUsecases()) {
								toComboBox.addItem(u.name);
								toComboBoxIdList.add(u.id);
							}

							//ComboBox選択
							toComboBoxIdList.stream().filter(id -> id == selectedStep.includeUsecaseId).forEach(id -> toComboBox.setSelectedIndex(toComboBoxIdList.indexOf(id)));
							break;
						case GOTO:
							for (Step s : selectedUsecase.getMainFlow()) {
								toComboBox.addItem(s.getStepName(ste.fgm, selectedUsecase));
								toComboBoxIdList.add(s.id);
							}

							//ComboBox選択
							toComboBoxIdList.stream().filter(id -> id == selectedStep.gotoStepId).forEach(id -> toComboBox.setSelectedIndex(toComboBoxIdList.indexOf(id)));
							break;
					}

					//Sbj,Obj ComboBox中身更新
					objectAndSubjectComboBoxIdList.clear();
					objectComboBox.removeAllItems();
					subjectComboBox.removeAllItems();
					for (Domain d : ste.fgm.getDomains()) {
						objectAndSubjectComboBoxIdList.add(d.id);
						subjectComboBox.addItem(d.name);
						objectComboBox.addItem(d.name);
					}

					//Object,SubjectComboBox選択
					objectAndSubjectComboBoxIdList.stream().filter(id -> id == selectedStep.objectDomainId).forEach(id -> objectComboBox.setSelectedIndex(objectAndSubjectComboBoxIdList.indexOf(id)));
					objectAndSubjectComboBoxIdList.stream().filter(id -> id == selectedStep.subjectDomainId).forEach(id -> subjectComboBox.setSelectedIndex(objectAndSubjectComboBoxIdList.indexOf(id)));

					//Text更新
					if (!eventNameArea.hasFocus()) eventNameArea.setText(selectedStep.Event);
				}
			}

			//Editorパネル可視性変更
			//Usecase
			jump.setVisible(usecaseSelected);
			parentGoalNameLabelPanel.setVisible(usecaseSelected);
			nameAreaPanel.setVisible(usecaseSelected);
			//flow
			conditionAreaPanel.setVisible(flowSelected && ucg.selectedFlowType != 0);
			sourceStepComboBoxPanel.setVisible(flowSelected && ucg.selectedFlowType != 0);
			//step
			stepTypePanel.setVisible(stepSelected);
			objectComboBoxPanel.setVisible(stepSelected && stepTypeAction.isSelected());
			subjectComboBoxPanel.setVisible(stepSelected && stepTypeAction.isSelected());
			eventNameAreaPanel.setVisible(stepSelected && stepTypeAction.isSelected());
			toComboBoxPanel.setVisible(stepSelected && (stepTypeInclude.isSelected() || stepTypeGoto.isSelected()));
		} catch (NullPointerException e) {
			//Usecase削除時のNullPointerException対策
			ucg.selectedUsecaseId = -1;
			ucg.selectedFlowIndex = -1;
			ucg.selectedFlowType = -1;
			ucg.selectedStepId = -1;
			System.out.println("UCEditPanel:redraw() avoid NullPointerException");
			redraw();
		}

		isDrawing = false;
	}

	private void editUsecase(Usecase usecase) {
		usecase.name = nameArea.getText();
		ste.fgm.editUsecase(usecase.id, usecase);
	}

	private void editAltFlow(Usecase usecase, int flowIndex) {
		Step s = usecase.getAlternativeFlowList().get(flowIndex).get(0);
		s.condition = conditionArea.getText();
		s.sourceStepId = sourceStepComboBoxIdList.get(sourceStepComboBox.getSelectedIndex());
		usecase.editStep(s.id, s);
		ste.fgm.editUsecase(usecase.id, usecase);
	}

	private void editExcFlow(Usecase usecase, int flowIndex) {
		Step s = usecase.getExceptionalFlowList().get(flowIndex).get(0);
		s.condition = conditionArea.getText();
		s.sourceStepId = sourceStepComboBoxIdList.get(sourceStepComboBox.getSelectedIndex());
		usecase.editStep(s.id, s);
		ste.fgm.editUsecase(usecase.id, usecase);
	}

	private void editStep(Usecase usecase, Step step) {
		step.stepType = stepTypeGoto.isSelected() ? Step.StepType.GOTO
				: stepTypeInclude.isSelected() ? Step.StepType.INCLUDE
				: stepTypeAction.isSelected() ? Step.StepType.ACTION : Step.StepType.NOP;

		int toComboBoxSelectedIndex = toComboBox.getSelectedIndex();
		switch (step.stepType) {
			case GOTO:
				if (toComboBoxSelectedIndex != -1) step.gotoStepId = toComboBoxIdList.get(toComboBoxSelectedIndex);
				break;
			case INCLUDE:
				if (toComboBoxSelectedIndex != -1)
					step.includeUsecaseId = toComboBoxIdList.get(toComboBoxSelectedIndex);
				break;
			case ACTION:
				int objectComboBoxSelectedIndex = objectComboBox.getSelectedIndex();
				int subjectComboBoxSelectedIndex = subjectComboBox.getSelectedIndex();
				if (objectComboBoxSelectedIndex != -1)
					step.objectDomainId = objectAndSubjectComboBoxIdList.get(objectComboBoxSelectedIndex);
				if (subjectComboBoxSelectedIndex != -1)
					step.subjectDomainId = objectAndSubjectComboBoxIdList.get(subjectComboBoxSelectedIndex);
				String str = eventNameArea.getText();
				if (str != null) step.Event = str;
				break;
		}

		usecase.editStep(step.id, step);
		String str = ste.fgm.editUsecase(usecase.id, usecase);
		if (str != null) {
			JOptionPane.showMessageDialog(this, str, "ERROR", JOptionPane.ERROR_MESSAGE);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		//描画時の変更でなければ(!isDrawing)アクション来てる
		if (!isDrawing && ucg.selectedUsecaseId != -1) {
			Usecase uc = ste.fgm.getUsecaseById(ucg.selectedUsecaseId);
			//ucgで選択中のタイプ（UC,Flow,Step）を取得
			boolean usecaseSelected = ucg.selectedFlowIndex == -1 && ucg.selectedStepId == -1;
			boolean flowSelected = ucg.selectedFlowType != -1 && ucg.selectedStepId == -1;
			boolean stepSelected = ucg.selectedFlowType != -1 && ucg.selectedStepId != -1;

			if (uc == null) {
				//なにもしない
			} else if (usecaseSelected) {
				editUsecase(uc);
			} else if (flowSelected) {
				switch (ucg.selectedFlowType) {
					case 1:
						editAltFlow(uc, ucg.selectedFlowIndex);
						break;
					case 2:
						editExcFlow(uc, ucg.selectedFlowIndex);
						break;
				}
			} else if (stepSelected) {
				editStep(uc, uc.getStepById(ucg.selectedStepId));
			}

			//再描画
			ste.redraw();
		}
	}

	@Override
	public void insertUpdate(DocumentEvent e) {
		actionPerformed(null);
	}

	@Override
	public void removeUpdate(DocumentEvent e) {
		actionPerformed(null);
	}

	@Override
	public void changedUpdate(DocumentEvent e) {
		actionPerformed(null);
	}
}
