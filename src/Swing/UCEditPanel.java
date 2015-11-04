package Swing;

import Models.Goal;
import Models.Step;
import Models.Usecase;
import Processing.UCGraph;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Created by 66 on 2015/11/03.
 * UseCaseEditorの右部分
 */
public class UCEditPanel extends JPanel {
	private final UCGraph ucg;
	private final SToolEditor ste;

	//UCEditorコンポーネント
	private JButton jump, edit;
	private JTextArea nameArea, conditionArea;
	private JPanel nameFieldBorder, parentNameLabelBorder, conditionBorder, sourceStepComboBoxBorder;
	private JLabel parentGoalNameLabel;
	private JComboBox sourceStepComboBox;
	private java.util.List<Integer> altExcFlowComboBoxIdList;

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
		//EditButton
		edit = new JButton("Edit");
		edit.addActionListener(e -> editButtonPressed());
		edit.setBounds(105, 400, 90, 30);
		edit.setVisible(false);
		this.add(edit);

		//NameTextArea周り
		nameArea = new JTextArea(5, 15);
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
		sourceStepComboBoxBorder = new JPanel();
		sourceStepComboBoxBorder.add(sourceStepComboBox);
		sourceStepComboBoxBorder.setBorder(new TitledBorder(new EtchedBorder(), "SourceStep"));
		sourceStepComboBoxBorder.setBounds(5, 120, 193, 60);
		this.add(sourceStepComboBoxBorder);
		altExcFlowComboBoxIdList = new ArrayList();

		//Condition
		conditionArea = new JTextArea(2, 15);
		scrollPane = new JScrollPane(conditionArea);
		conditionBorder = new JPanel();
		conditionBorder.add(scrollPane);
		conditionBorder.setBorder(new TitledBorder(new EtchedBorder(), "Condition"));
		conditionBorder.setBounds(5, 210, 193, 80);
		this.add(conditionBorder);


		//TODO:もっとコンポーネント追加


	}

	private void editButtonPressed() {
		Usecase uc = ste.fgm.getUsecaseById(ucg.selectedUsecaseId);
		if (ucg.selectedFlowType == -1) {
			//Usecase選択時

			uc.name = nameArea.getText();
			ste.fgm.editUsecase(ucg.selectedUsecaseId, uc);
			ste.redraw();
		} else if (ucg.selectedStepId == -1) {
			//Alt_EXC_Flow選択時

			if (ucg.selectedFlowType == 1) {
				Step s = uc.getAlternativeFlowList().get(ucg.selectedFlowId).get(0);
				s.condition = conditionArea.getText();
				uc.editStep(s.id, s);
			} else if (ucg.selectedFlowType == 2) {
				Step s = uc.getExceptionalFlowList().get(ucg.selectedFlowId).get(0);
				s.condition = conditionArea.getText();
				uc.editStep(s.id, s);
			}
		} else if (ucg.selectedUsecaseId != -1) {
			//TODO:Step選択時
		}
	}

	private void jumpButtonPressed() {
		ste.jumpToGGTab(ste.fgm.getUsecaseById(ucg.selectedUsecaseId).parentLeafGoalId);
	}

	public void redraw() {
		try {
			int id = ucg.selectedUsecaseId;
			if (id != -1) {

				// UCEdit:altExcflowComboBox更新
				altExcFlowComboBoxIdList.clear();
				sourceStepComboBox.removeAllItems();
				for (Usecase u : ste.fgm.getUsecases()) {
					if (u.id == id) {
						List<Step> ls = u.getMainFlow();
						for (Step s : ls) {
							sourceStepComboBox.addItem(s.getStepName(ste.fgm, u));
							altExcFlowComboBoxIdList.add(s.id);
						}
					}
				}

				//Usecase名前詰め込んで描画
				nameArea.setText(ste.fgm.getUsecaseById(id).name);

				//親ゴールの名前を表示
				Goal g = ste.fgm.getGoalById(ucg.selectedUsecaseId);
				if (g != null) {
					parentGoalNameLabel.setText(g.name);
				}


			}

			//TODO:Alt_Flowとかその辺関連のコンポーネント追加処理


			//Editorコンポーネント可視性変更
			nameFieldBorder.setVisible(ucg.selectedUsecaseId != -1 && ucg.selectedFlowId == -1 && ucg.selectedStepId == -1);
			parentNameLabelBorder.setVisible(ucg.selectedUsecaseId != -1 && ucg.selectedFlowId == -1 && ucg.selectedStepId == -1);
			jump.setVisible(ucg.selectedUsecaseId != -1 && ucg.selectedFlowId == -1 && ucg.selectedStepId == -1);
			edit.setVisible(ucg.selectedUsecaseId != -1 && (ucg.selectedFlowType != 0 || ucg.selectedStepId != -1));
			sourceStepComboBoxBorder.setVisible(ucg.selectedUsecaseId != -1 && ucg.selectedFlowType > 0 && ucg.selectedStepId == -1);
			conditionBorder.setVisible(ucg.selectedUsecaseId != -1 && ucg.selectedFlowType > 0 && ucg.selectedStepId == -1);

		} catch (NullPointerException e) {
			//Usecase削除時のNullPointerException対策
			ucg.selectedUsecaseId = -1;
			ucg.selectedFlowId = -1;
			ucg.selectedFlowType = -1;
			ucg.selectedStepId = -1;
			System.out.println("UCEditPanel:redraw() avoid NullPointerException");
		}
	}
}
