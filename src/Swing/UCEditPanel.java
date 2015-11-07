package Swing;

import Models.Goal;
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

	//UCEditorコンポーネント
	private JButton jump;
	private JTextArea nameArea, conditionArea;
	private JPanel nameFieldBorder, parentNameLabelBorder, conditionBorder, sourceStepComboBoxBorder, stepType;
	private JRadioButton stepTypeNop, stepTypeAction, stepTypeGoto, stepTypeInclude;
	private JLabel parentGoalNameLabel;
	private JComboBox sourceStepComboBox;
	private List<Integer> altExcFlowComboBoxIdList;

	//Draw中のフラグ
	private boolean isDrawing;

	public UCEditPanel(SToolEditor ste, UCGraph ucg) {
		this.ste = ste;
		this.ucg = ucg;

		this.setLayout(null);
		this.setPreferredSize(new Dimension(200, 0));

		//JumpButton
		jump = new JButton("Jump to Parent Goal");
		jump.addActionListener(e -> jumpButtonPressed());
		jump.setBounds(45, 110, 150, 30);
		this.add(jump);

		//NameTextArea周り
		nameArea = new JTextArea(5, 15);
		nameArea.getDocument().addDocumentListener(this);
		JScrollPane scrollPane = new JScrollPane(nameArea);
		nameFieldBorder = new JPanel();
		nameFieldBorder.add(scrollPane);
		nameFieldBorder.setBorder(new TitledBorder(new EtchedBorder(), "Name"));
		nameFieldBorder.setBounds(5, 180, 193, 120);
		this.add(nameFieldBorder);

		//parentGoalLabel
		parentGoalNameLabel = new JLabel("null");
		parentGoalNameLabel.setPreferredSize(new Dimension(160, 20));
		parentNameLabelBorder = new JPanel();
		parentNameLabelBorder.add(parentGoalNameLabel);
		parentNameLabelBorder.setBorder(new TitledBorder(new EtchedBorder(), "Parent Goal"));
		parentNameLabelBorder.setBounds(5, 50, 193, 60);
		this.add(parentNameLabelBorder);

		//sourceStepComboBox周り
		sourceStepComboBox = new JComboBox();
		sourceStepComboBox.setPreferredSize(new Dimension(160, 20));
		sourceStepComboBox.addActionListener(this);
		sourceStepComboBoxBorder = new JPanel();
		sourceStepComboBoxBorder.add(sourceStepComboBox);
		sourceStepComboBoxBorder.setBorder(new TitledBorder(new EtchedBorder(), "SourceStep"));
		sourceStepComboBoxBorder.setBounds(5, 120, 193, 60);
		this.add(sourceStepComboBoxBorder);
		altExcFlowComboBoxIdList = new ArrayList();

		//Condition
		conditionArea = new JTextArea(2, 15);
		conditionArea.getDocument().addDocumentListener(this);
		scrollPane = new JScrollPane(conditionArea);
		conditionBorder = new JPanel();
		conditionBorder.add(scrollPane);
		conditionBorder.setBorder(new TitledBorder(new EtchedBorder(), "Condition"));
		conditionBorder.setBounds(5, 210, 193, 80);
		this.add(conditionBorder);

		//StepType
		stepTypeNop = new JRadioButton("NOP");
		stepTypeNop.setSelected(true);
		stepTypeNop.addActionListener(this);
		stepTypeAction = new JRadioButton("EVENT");
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
		stepType = new JPanel();
		stepType.add(stepTypeNop);
		stepType.add(stepTypeAction);
		stepType.add(stepTypeGoto);
		stepType.add(stepTypeInclude);
		stepType.setBorder(new TitledBorder(new EtchedBorder(), "StepType"));
		stepType.setBounds(5, 50, 193, 90);
		stepType.setVisible(false);
		this.add(stepType);


		//TODO:もっとコンポーネント追加


	}

	public void redraw() {
		isDrawing = true;

		try {
			Usecase uc = ste.fgm.getUsecaseById(ucg.selectedUsecaseId);
			if (uc == null) return;

			// UCEdit:altExcflowComboBox更新
			altExcFlowComboBoxIdList.clear();
			sourceStepComboBox.removeAllItems();
			List<Step> ls = uc.getMainFlow();
			for (Step s : ls) {
				sourceStepComboBox.addItem(s.getStepName(ste.fgm, uc));
				altExcFlowComboBoxIdList.add(s.id);
			}

			//Usecase名前詰め込んで描画
			if (!nameArea.hasFocus()) nameArea.setText(uc.name);

			//Condition名前詰め込んで描画
			if (!conditionArea.hasFocus()) {
				switch (ucg.selectedFlowType) {
					case 1:
						conditionArea.setText(uc.getAlternativeFlowList().get(ucg.selectedFlowIndex).get(0).condition);
						break;
					case 2:
						conditionArea.setText(uc.getExceptionalFlowList().get(ucg.selectedFlowIndex).get(0).condition);
						break;
				}
			}

			//StepType描画
			Step step = uc.getStepById(ucg.selectedStepId);
			if (step != null) {
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
			}

			//TODO:Alt_Flowとかその辺関連のコンポーネント追加処理

			//親ゴールの名前を表示
			Goal g = ste.fgm.getGoalById(uc.parentLeafGoalId);
			if (g != null) parentGoalNameLabel.setText(g.name);

			//Editorコンポーネント可視性変更
			nameFieldBorder.setVisible(ucg.selectedUsecaseId != -1 && ucg.selectedFlowIndex == -1 && ucg.selectedStepId == -1);
			parentNameLabelBorder.setVisible(ucg.selectedUsecaseId != -1 && ucg.selectedFlowIndex == -1 && ucg.selectedStepId == -1);
			jump.setVisible(ucg.selectedUsecaseId != -1 && ucg.selectedFlowIndex == -1 && ucg.selectedStepId == -1);
			sourceStepComboBoxBorder.setVisible(ucg.selectedFlowType > 0 && ucg.selectedStepId == -1);
			conditionBorder.setVisible(ucg.selectedFlowType > 0 && ucg.selectedStepId == -1);
			stepType.setVisible(ucg.selectedStepId != -1);
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

	private void edit() {
		Usecase uc = ste.fgm.getUsecaseById(ucg.selectedUsecaseId);
		if (ucg.selectedFlowType == -1) {
			//Usecase選択時
			uc.name = nameArea.getText();
		} else if (ucg.selectedStepId == -1) {
			Step s = new Step();
			switch (ucg.selectedFlowType) {
				case 1:
					s = uc.getAlternativeFlowList().get(ucg.selectedFlowIndex).get(0);
					break;
				case 2:
					s = uc.getExceptionalFlowList().get(ucg.selectedFlowIndex).get(0);
			}
			s.condition = conditionArea.getText();
			uc.editStep(s.id, s);
		} else if (ucg.selectedUsecaseId != -1) {
			//TODO:Step選択時
			Step s = uc.getStepById(ucg.selectedStepId);
			s.stepType = stepTypeGoto.isSelected() ? Step.StepType.GOTO
					: stepTypeInclude.isSelected() ? Step.StepType.INCLUDE
					: stepTypeAction.isSelected() ? Step.StepType.ACTION : Step.StepType.NOP;
			uc.editStep(s.id, s);
		}

		//fgm編集
		String str = ste.fgm.editUsecase(ucg.selectedUsecaseId, uc);
		if (str != null) {
			JOptionPane.showMessageDialog(this, str, "ERROR", JOptionPane.ERROR_MESSAGE);
		}

		//再描画
		ste.redraw();
	}

	private void jumpButtonPressed() {
		ste.jumpToGGTab(ste.fgm.getUsecaseById(ucg.selectedUsecaseId).parentLeafGoalId);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (!isDrawing && ucg.selectedUsecaseId != -1) {
			edit();
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
