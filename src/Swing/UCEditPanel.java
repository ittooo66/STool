package Swing;

import Models.Domain;
import Models.Step;
import Models.Usecase;
import Processing.UCGraph;
import Swing.Component.TitledJRadioButtonGroupPanel;
import Swing.Component.ValuedComboBoxPanel;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class UCEditPanel extends JPanel implements ActionListener, DocumentListener {
	private final UCGraph ucg;
	private final SToolEditor ste;

	private JButton jump;
	private JTextArea nameArea, conditionArea, eventNameArea;
	private JPanel conditionAreaPanel;
	private JPanel parentGoalNameLabelPanel, nameAreaPanel;
	private ValuedComboBoxPanel subjectComboBoxPanel, objectComboBoxPanel, toComboBoxPanel, sourceStepComboBoxPanel;
	private JPanel eventNameAreaPanel;
	private TitledJRadioButtonGroupPanel stepType;
	private JLabel parentGoalNameLabel;

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
		//sourceStepComboBox
		sourceStepComboBoxPanel = new ValuedComboBoxPanel("SourceStep");
		sourceStepComboBoxPanel.addActionListenerToComboBox(this);
		this.add(sourceStepComboBoxPanel);

		//StepType
		stepType = new TitledJRadioButtonGroupPanel("StepType");
		stepType.add(new JRadioButton(Step.StepType.getString(Step.StepType.NOP), true));
		stepType.add(new JRadioButton(Step.StepType.getString(Step.StepType.ACTION)));
		stepType.add(new JRadioButton(Step.StepType.getString(Step.StepType.GOTO)));
		stepType.add(new JRadioButton(Step.StepType.getString(Step.StepType.INCLUDE)));
		stepType.addActionListenerToAll(this);
		this.add(stepType);

		//subjectComboBox
		subjectComboBoxPanel = new ValuedComboBoxPanel("Subject");
		subjectComboBoxPanel.addActionListenerToComboBox(this);
		this.add(subjectComboBoxPanel);
		//eventNameArea
		eventNameArea = new JTextArea(1, 15);
		eventNameArea.getDocument().addDocumentListener(this);
		eventNameAreaPanel = new JPanel();
		eventNameAreaPanel.add(new JScrollPane(eventNameArea, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER));
		eventNameAreaPanel.setBorder(new TitledBorder(new EtchedBorder(), "Event"));
		this.add(eventNameAreaPanel);
		//objectComboBox
		objectComboBoxPanel = new ValuedComboBoxPanel("Object");
		objectComboBoxPanel.addActionListenerToComboBox(this);
		this.add(objectComboBoxPanel);

		//toComboBox
		toComboBoxPanel = new ValuedComboBoxPanel("To");
		toComboBoxPanel.addActionListenerToComboBox(this);
		this.add(toComboBoxPanel);
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
					sourceStepComboBoxPanel.initItem();
					for (Step s : selectedUsecase.getMainFlow())
						sourceStepComboBoxPanel.addItem(s.getStepName(ste.fgm, selectedUsecase), s.id);

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
					if (flowIndex != null) sourceStepComboBoxPanel.setSelected(flowIndex.sourceStepId);

					//Condition名前詰め込み
					if (flowIndex != null && !conditionArea.hasFocus()) {
						conditionArea.setText(flowIndex.condition);
					}
				}

				if (stepSelected) {
					//StepType描画
					stepType.setSelected(selectedStep.stepType.toString());

					// GOTO or INCLUDE ComboBox中身更新
					toComboBoxPanel.initItem();
					switch (selectedStep.stepType) {
						case INCLUDE:
							for (Usecase u : ste.fgm.getUsecases()) {
								toComboBoxPanel.addItem(u.name, u.id);
							}
							//ComboBox選択
							toComboBoxPanel.setSelected(selectedStep.includeUsecaseId);
							break;
						case GOTO:
							for (Step s : selectedUsecase.getMainFlow()) {
								toComboBoxPanel.addItem(s.getStepName(ste.fgm, selectedUsecase), s.id);
							}
							//ComboBox選択
							toComboBoxPanel.setSelected(selectedStep.gotoStepId);
							break;
					}

					//Sbj,Obj ComboBox中身更新
					objectComboBoxPanel.initItem();
					subjectComboBoxPanel.initItem();
					for (Domain d : ste.fgm.getDomains()) {
						objectComboBoxPanel.addItem(d.name, d.id);
						subjectComboBoxPanel.addItem(d.name, d.id);
					}

					//Object,SubjectComboBox選択
					objectComboBoxPanel.setSelected(selectedStep.objectDomainId);
					subjectComboBoxPanel.setSelected(selectedStep.subjectDomainId);

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
			stepType.setVisible(stepSelected);
			objectComboBoxPanel.setVisible(stepSelected && stepType.getSelectedButtonCommand().equals(Step.StepType.getString(Step.StepType.ACTION)));
			subjectComboBoxPanel.setVisible(stepSelected && stepType.getSelectedButtonCommand().equals(Step.StepType.getString(Step.StepType.ACTION)));
			eventNameAreaPanel.setVisible(stepSelected && stepType.getSelectedButtonCommand().equals(Step.StepType.getString(Step.StepType.ACTION)));
			toComboBoxPanel.setVisible(stepSelected && (stepType.getSelectedButtonCommand().equals(Step.StepType.getString(Step.StepType.INCLUDE)) || stepType.getSelectedButtonCommand().equals(Step.StepType.getString(Step.StepType.GOTO))));
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
		s.sourceStepId = sourceStepComboBoxPanel.getSelectedParam();
		usecase.editStep(s.id, s);
		ste.fgm.editUsecase(usecase.id, usecase);
	}

	private void editExcFlow(Usecase usecase, int flowIndex) {
		Step s = usecase.getExceptionalFlowList().get(flowIndex).get(0);
		s.condition = conditionArea.getText();
		s.sourceStepId = sourceStepComboBoxPanel.getSelectedParam();
		usecase.editStep(s.id, s);
		ste.fgm.editUsecase(usecase.id, usecase);
	}

	private void editStep(Usecase usecase, Step step) {
		step.stepType = Step.StepType.parse(stepType.getSelectedButtonCommand());

		switch (step.stepType) {
			case GOTO:
				step.gotoStepId = toComboBoxPanel.getSelectedParam();
				break;
			case INCLUDE:
				step.includeUsecaseId = toComboBoxPanel.getSelectedParam();
				break;
			case ACTION:
				step.objectDomainId = objectComboBoxPanel.getSelectedParam();
				step.subjectDomainId=subjectComboBoxPanel.getSelectedParam();
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
