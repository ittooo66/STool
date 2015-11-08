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

	private JTextArea nameArea, conditionArea, eventNameArea;
	private JPanel usecaseEditPanel, flowEditPanel, stepTypeBorder, subjectComboBoxBorder, objectComboBoxBorder, eventNameFieldBorder, toComboBoxBorder;
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
		this.setPreferredSize(new Dimension(0,80));

		//Usecase選択時のパネル
		usecaseEditPanel = new JPanel();
		usecaseEditPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		//JumpButton
		JButton jump = new JButton("Jump to Parent Goal");
		jump.addActionListener(e -> ste.jumpToGGTab(ste.fgm.getUsecaseById(ucg.selectedUsecaseId).parentLeafGoalId));
		//NameTextArea
		nameArea = new JTextArea(2, 14);
		nameArea.getDocument().addDocumentListener(this);
		JScrollPane scrollPane = new JScrollPane(nameArea);
		JPanel nameFieldBorder = new JPanel();
		nameFieldBorder.add(scrollPane);
		nameFieldBorder.setBorder(new TitledBorder(new EtchedBorder(), "Name"));
		//parentGoalLabel
		parentGoalNameLabel = new JLabel("null");
		parentGoalNameLabel.setPreferredSize(new Dimension(160, 20));
		JPanel parentNameLabelBorder = new JPanel();
		parentNameLabelBorder.add(parentGoalNameLabel);
		parentNameLabelBorder.setBorder(new TitledBorder(new EtchedBorder(), "Parent Goal"));
		usecaseEditPanel.add(parentNameLabelBorder);
		usecaseEditPanel.add(jump);
		usecaseEditPanel.add(nameFieldBorder);
		this.add(usecaseEditPanel);

		//Flow（ALTまたはEXC）選択時のパネル
		flowEditPanel = new JPanel();
		flowEditPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		//Condition
		conditionArea = new JTextArea(2, 15);
		conditionArea.getDocument().addDocumentListener(this);
		scrollPane = new JScrollPane(conditionArea);
		JPanel conditionBorder = new JPanel();
		conditionBorder.add(scrollPane);
		conditionBorder.setBorder(new TitledBorder(new EtchedBorder(), "Condition"));
		//sourceStepComboBox周り
		sourceStepComboBox = new JComboBox();
		sourceStepComboBox.setPreferredSize(new Dimension(160, 20));
		sourceStepComboBox.addActionListener(this);
		JPanel sourceStepComboBoxBorder = new JPanel();
		sourceStepComboBoxBorder.add(sourceStepComboBox);
		sourceStepComboBoxBorder.setBorder(new TitledBorder(new EtchedBorder(), "SourceStep"));
		flowEditPanel.add(sourceStepComboBoxBorder);
		flowEditPanel.add(conditionBorder);
		this.add(flowEditPanel);
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
		ButtonGroup refineTypeButtonGroup = new ButtonGroup();
		refineTypeButtonGroup.add(stepTypeNop);
		refineTypeButtonGroup.add(stepTypeAction);
		refineTypeButtonGroup.add(stepTypeGoto);
		refineTypeButtonGroup.add(stepTypeInclude);
		//StepTypeグループのラベル（パネル）作成
		stepTypeBorder = new JPanel();
		stepTypeBorder.add(stepTypeNop);
		stepTypeBorder.add(stepTypeAction);
		stepTypeBorder.add(stepTypeGoto);
		stepTypeBorder.add(stepTypeInclude);
		stepTypeBorder.setBorder(new TitledBorder(new EtchedBorder(), "StepType"));
		stepTypeBorder.setVisible(false);
		this.add(stepTypeBorder);

		//subjectComboBox
		subjectComboBox = new JComboBox();
		subjectComboBox.setPreferredSize(new Dimension(160, 20));
		subjectComboBox.addActionListener(this);
		subjectComboBoxBorder = new JPanel();
		subjectComboBoxBorder.add(subjectComboBox);
		subjectComboBoxBorder.setBorder(new TitledBorder(new EtchedBorder(), "Subject"));
		this.add(subjectComboBoxBorder);
		//eventNameArea
		eventNameArea = new JTextArea(2, 15);
		eventNameArea.getDocument().addDocumentListener(this);
		scrollPane = new JScrollPane(eventNameArea);
		eventNameFieldBorder = new JPanel();
		eventNameFieldBorder.add(scrollPane);
		eventNameFieldBorder.setBorder(new TitledBorder(new EtchedBorder(), "Event"));
		this.add(eventNameFieldBorder);
		//objectComboBox
		objectComboBox = new JComboBox();
		objectComboBox.setPreferredSize(new Dimension(160, 20));
		objectComboBox.addActionListener(this);
		objectComboBoxBorder = new JPanel();
		objectComboBoxBorder.add(objectComboBox);
		objectComboBoxBorder.setBorder(new TitledBorder(new EtchedBorder(), "Object"));
		this.add(objectComboBoxBorder);
		objectAndSubjectComboBoxIdList = new ArrayList<>();

		//toComboBox
		toComboBox = new JComboBox();
		toComboBox.setPreferredSize(new Dimension(160, 20));
		toComboBox.addActionListener(this);
		toComboBoxBorder = new JPanel();
		toComboBoxBorder.add(toComboBox);
		toComboBoxBorder.setBorder(new TitledBorder(new EtchedBorder(), "To"));
		this.add(toComboBoxBorder);
		toComboBoxIdList = new ArrayList<>();

	}

	public void redraw() {
		isDrawing = true;

		boolean usecaseSelected = ucg.selectedUsecaseId != -1 && ucg.selectedFlowIndex == -1 && ucg.selectedStepId == -1;
		boolean flowSelected = ucg.selectedUsecaseId != -1 && ucg.selectedFlowType != -1 && ucg.selectedStepId == -1;
		boolean stepSelected = ucg.selectedUsecaseId != -1 && ucg.selectedFlowType != -1 && ucg.selectedStepId != -1;

		try {
			Usecase uc = ste.fgm.getUsecaseById(ucg.selectedUsecaseId);

			if (usecaseSelected) {
				//親ゴールの名前を表示
				parentGoalNameLabel.setText(ste.fgm.getGoalById(uc.parentLeafGoalId).name);

				//Usecase名前詰め込んで描画
				if (!nameArea.hasFocus()) nameArea.setText(uc.name);

			} else if (flowSelected) {
				// UCEdit:altExcflowComboBox更新
				sourceStepComboBoxIdList.clear();
				sourceStepComboBox.removeAllItems();
				List<Step> ls = uc.getMainFlow();
				for (Step s : ls) {
					sourceStepComboBox.addItem(s.getStepName(ste.fgm, uc));
					sourceStepComboBoxIdList.add(s.id);
				}

				//Condition名前詰め込んで描画
				if (!conditionArea.hasFocus()) {
					switch (ucg.selectedFlowType) {
						case 1:
							if (uc.getAlternativeFlowList() != null && uc.getAlternativeFlowList().get(ucg.selectedFlowIndex) != null && uc.getAlternativeFlowList().get(ucg.selectedFlowIndex).get(0) != null)
								conditionArea.setText(uc.getAlternativeFlowList().get(ucg.selectedFlowIndex).get(0).condition);
							break;
						case 2:
							if (uc.getExceptionalFlowList() != null && uc.getExceptionalFlowList().get(ucg.selectedFlowIndex) != null && uc.getExceptionalFlowList().get(ucg.selectedFlowIndex).get(0) != null)
								conditionArea.setText(uc.getExceptionalFlowList().get(ucg.selectedFlowIndex).get(0).condition);
							break;
					}
				}

			} else if (stepSelected) {
				//Step描画
				Step step = uc.getStepById(ucg.selectedStepId);

				//StepType描画
				switch (step.stepType) {
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
				switch (step.stepType) {
					case INCLUDE:
						for (Usecase u : ste.fgm.getUsecases()) {
							toComboBox.addItem(u.name);
							toComboBoxIdList.add(u.id);
						}

						//ComboBox選択
						toComboBoxIdList.stream().filter(id -> id == step.includeUsecaseId).forEach(id -> toComboBox.setSelectedIndex(toComboBoxIdList.indexOf(id)));
						break;
					case GOTO:
						for (Step s : uc.getMainFlow()) {
							toComboBox.addItem(s.getStepName(ste.fgm, uc));
							toComboBoxIdList.add(s.id);
						}

						//ComboBox選択
						toComboBoxIdList.stream().filter(id -> id == step.gotoStepId).forEach(id -> toComboBox.setSelectedIndex(toComboBoxIdList.indexOf(id)));
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
				objectAndSubjectComboBoxIdList.stream().filter(id -> id == step.objectDomainId).forEach(id -> objectComboBox.setSelectedIndex(objectAndSubjectComboBoxIdList.indexOf(id)));
				objectAndSubjectComboBoxIdList.stream().filter(id -> id == step.subjectDomainId).forEach(id -> subjectComboBox.setSelectedIndex(objectAndSubjectComboBoxIdList.indexOf(id)));

				//Text更新
				if (!eventNameArea.hasFocus()) eventNameArea.setText(step.Event);
			}

			//Editorパネル変更
			usecaseEditPanel.setVisible(usecaseSelected);
			flowEditPanel.setVisible(flowSelected && ucg.selectedFlowType != 0);
			stepTypeBorder.setVisible(stepSelected);
			objectComboBoxBorder.setVisible(stepSelected && stepTypeAction.isSelected());
			subjectComboBoxBorder.setVisible(stepSelected && stepTypeAction.isSelected());
			eventNameFieldBorder.setVisible(stepSelected && stepTypeAction.isSelected());
			toComboBoxBorder.setVisible(stepSelected && (stepTypeInclude.isSelected() || stepTypeGoto.isSelected()));

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
		usecase.editStep(s.id, s);
		ste.fgm.editUsecase(usecase.id, usecase);
	}

	private void editExcFlow(Usecase usecase, int flowIndex) {
		Step s = usecase.getExceptionalFlowList().get(flowIndex).get(0);
		s.condition = conditionArea.getText();
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
