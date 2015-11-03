package Swing;

import Models.Goal;
import Models.Usecase;
import Processing.UCGraph;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;

/**
 * Created by 66 on 2015/11/03.
 * UseCaseEditorの右部分
 */
public class UCEditPanel extends JPanel {
	private final UCGraph ucg;
	private final SToolEditor ste;

	//UCEditorコンポーネント
	private JButton jump, edit;
	private JTextArea nameArea;
	private JPanel nameFieldBorder, parentNameLabelBorder;
	private JLabel parentGoalNameLabel;

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

	}

	private void editButtonPressed() {
		Usecase uc = ste.fgm.getUsecaseById(ucg.selectedUsecaseId);
		uc.name = nameArea.getText();
		ste.fgm.editUsecase(ucg.selectedUsecaseId, uc);
		ste.redraw();
	}

	private void jumpButtonPressed() {
		ste.jumpToGGTab(ste.fgm.getUsecaseById(ucg.selectedUsecaseId).parentLeafGoalId);
	}

	public void redraw() {
		try {
			int id = ucg.selectedUsecaseId;
			if (id != -1) {
				//Usecase名前詰め込んで描画
				nameArea.setText(ste.fgm.getUsecaseById(id).name);

				//親ゴールの名前を表示
				Goal g = ste.fgm.getGoalById(ucg.selectedUsecaseId);
				if (g != null) {
					parentGoalNameLabel.setText(g.name);
				}
			}

			//Editorコンポーネント可視性変更
			nameFieldBorder.setVisible(ucg.selectedUsecaseId != -1 && ucg.selectedFlowId == -1 && ucg.selectedStepId == -1);
			parentNameLabelBorder.setVisible(ucg.selectedUsecaseId != -1 && ucg.selectedFlowId == -1 && ucg.selectedStepId == -1);
			jump.setVisible(ucg.selectedUsecaseId != -1 && ucg.selectedFlowId == -1 && ucg.selectedStepId == -1);
			edit.setVisible(ucg.selectedUsecaseId != -1 && ucg.selectedFlowId == -1 && ucg.selectedStepId == -1);
		} catch (NullPointerException e) {
			//Usecase削除時のNullPointerException対策
			ucg.selectedUsecaseId = -1;
			ucg.selectedFlowId = -1;
			ucg.selectedFlowType = -1;
			ucg.selectedStepId = -1;
			System.out.println("UCEditPanel:redraw()  ガッ");
		}
	}
}
