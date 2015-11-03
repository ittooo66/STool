package Swing;

import Models.Goal;
import Processing.GGGraph;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 66 on 2015/11/03.
 * GoalGraphEditorの右部分
 */
public class GGEditPanel extends JPanel {

	//各種インスタンスへの参照
	private final SToolEditor ste;
	private final GGGraph ggg;

	//GGEditorコンポーネント
	private JButton add, edit, remove;
	private JRadioButton refineTypeAnd, refineTypeOr, refineTypeLeaf;
	private JRadioButton necessityIsEnable, necessityIsDisable;
	private JTextArea nameArea;
	private JPanel ggEditRefineType, necessity;
	private JComboBox parentComboBox;
	private List<Integer> parentComboBoxIdList;

	public GGEditPanel(SToolEditor ste, GGGraph ggg) {
		this.ste = ste;
		this.ggg = ggg;

		this.setLayout(null);                            //pixel直打ちさせる
		this.setPreferredSize(new Dimension(200, 0));    //幅200で固定

		//RemoveButton
		remove = new JButton("Remove");
		remove.addActionListener(e -> removeButtonPressed());
		remove.setBounds(105, 5, 90, 30);
		remove.setVisible(false);
		this.add(remove);
		//AddButton
		add = new JButton("Add");
		add.addActionListener(e -> addButtonPressed());
		add.setBounds(105, 400, 90, 30);
		this.add(add);
		//EditButton
		edit = new JButton("Edit");
		edit.addActionListener(e -> editButtonPressed());
		edit.setBounds(105, 400, 90, 30);
		edit.setVisible(false);
		this.add(edit);

		//NameTextArea周り
		nameArea = new JTextArea(5, 15);
		JScrollPane scroll = new JScrollPane(nameArea);
		JPanel ggEditNameFieldBorder = new JPanel();
		ggEditNameFieldBorder.add(scroll);
		ggEditNameFieldBorder.setBorder(new TitledBorder(new EtchedBorder(), "Name"));
		ggEditNameFieldBorder.setBounds(5, 50, 193, 120);
		this.add(ggEditNameFieldBorder);

		//parent指定ComboBox周り
		parentComboBox = new JComboBox();
		parentComboBox.setPreferredSize(new Dimension(160, 20));
		JPanel parentComboBoxBorder = new JPanel();
		parentComboBoxBorder.add(parentComboBox);
		parentComboBoxBorder.setBorder(new TitledBorder(new EtchedBorder(), "Parent"));
		parentComboBoxBorder.setBounds(5, 180, 193, 60);
		this.add(parentComboBoxBorder);
		parentComboBoxIdList = new ArrayList();

		//refineType周り
		refineTypeAnd = new JRadioButton("AND");
		refineTypeAnd.setSelected(true);
		refineTypeOr = new JRadioButton("OR");
		refineTypeLeaf = new JRadioButton("LEAF");
		//refineTypeButtonGroup作成
		ButtonGroup ggEditRefineTypeButtonGroup = new ButtonGroup();
		ggEditRefineTypeButtonGroup.add(refineTypeAnd);
		ggEditRefineTypeButtonGroup.add(refineTypeOr);
		ggEditRefineTypeButtonGroup.add(refineTypeLeaf);
		//RefineTyoeグループのラベル（パネル）作成
		ggEditRefineType = new JPanel();
		ggEditRefineType.add(refineTypeAnd);
		ggEditRefineType.add(refineTypeOr);
		ggEditRefineType.add(refineTypeLeaf);
		ggEditRefineType.setBorder(new TitledBorder(new EtchedBorder(), "RefineType"));
		ggEditRefineType.setBounds(5, 250, 193, 60);
		ggEditRefineType.setVisible(false);
		this.add(ggEditRefineType);

		//Necessity周り
		necessityIsEnable = new JRadioButton("Enable");
		necessityIsEnable.setSelected(true);
		necessityIsDisable = new JRadioButton("Disable");
		//NecessityButtonGroup作成
		ButtonGroup necessityButtonGroup = new ButtonGroup();
		necessityButtonGroup.add(necessityIsEnable);
		necessityButtonGroup.add(necessityIsDisable);
		//Necessityグループのラベル（パネル）作成
		necessity = new JPanel();
		necessity.add(necessityIsEnable);
		necessity.add(necessityIsDisable);
		necessity.setBorder(new TitledBorder(new EtchedBorder(), "Necessity"));
		necessity.setBounds(5, 320, 193, 60);
		necessity.setVisible(false);
		this.add(necessity);

	}

	public void redraw() {
		//GGEdit:ComboBox更新
		parentComboBoxIdList.clear();
		parentComboBox.removeAllItems();
		parentComboBox.addItem("TOP");
		parentComboBoxIdList.add(-1);
		for (Goal g : ste.fgm.getGoals()) {
			if (g.id != ggg.selectedGoalId) {
				//ComboBox詰め替え
				parentComboBox.addItem(g.name);
				parentComboBoxIdList.add(g.id);
			}
		}

		//GGEdit:GGGraph.selectedGoalIdに応じたエディタ画面に更新
		if (ggg.selectedGoalId != -1) {

			//選択中のゴールを取得
			Goal selectedGoal = ste.fgm.getGoalById(ggg.selectedGoalId);

			//Text更新
			nameArea.setText(selectedGoal.name);

			//RefineType更新
			if (selectedGoal.childrenType == Goal.ChildrenType.AND) {
				refineTypeAnd.setSelected(true);
			} else if (selectedGoal.childrenType == Goal.ChildrenType.OR) {
				refineTypeOr.setSelected(true);
			} else if (selectedGoal.childrenType == Goal.ChildrenType.LEAF) {
				refineTypeLeaf.setSelected(true);
			}

			//ComboBox選択
			for (int id : parentComboBoxIdList) {
				if (selectedGoal.parentId == id) {
					parentComboBox.setSelectedIndex(parentComboBoxIdList.indexOf(id));
				}
			}

			//TODO:Necessity更新
		}

		//GGEditor各種コンポーネント:表示・非表示切り替え
		add.setVisible(ggg.selectedGoalId == -1);
		remove.setVisible(ggg.selectedGoalId != -1);
		edit.setVisible(ggg.selectedGoalId != -1);
		necessity.setVisible(ggg.selectedGoalId != -1);
		ggEditRefineType.setVisible(ggg.selectedGoalId != -1);

	}


	private void editButtonPressed() {
		//各種コンポーネントからパラメータ取得
		String name = nameArea.getText();
		Goal prevGoal = ste.fgm.getGoalById(ggg.selectedGoalId);
		Goal.ChildrenType ct = refineTypeAnd.isSelected() ? Goal.ChildrenType.AND : refineTypeOr.isSelected() ? Goal.ChildrenType.OR : Goal.ChildrenType.LEAF;
		int parentId = parentComboBoxIdList.get(parentComboBox.getSelectedIndex());

		//fgm編集
		if (!ste.fgm.editGoal(prevGoal.id, name, ct, parentId)) {
			JOptionPane.showMessageDialog(this, "モデルを編集できませんでした", "Error", JOptionPane.ERROR_MESSAGE);
		}

		ste.redraw();
	}

	private void addButtonPressed() {
		//親ゴールID取得
		int parentGoalId = parentComboBoxIdList.get(parentComboBox.getSelectedIndex());

		//名前取得
		String name = nameArea.getText();

		//名前欄にちゃんと中身があるか
		if (name.equals("")) {
			JOptionPane.showMessageDialog(this, "なまえをいれてください", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		} else
			nameArea.setText("");

		//追加
		ste.fgm.addGoal(name, parentGoalId, ggg.width / 2, ggg.height / 2);

		ste.redraw();
	}

	private void removeButtonPressed() {
		//選択中のゴールを削除
		ste.fgm.removeGoal(ggg.selectedGoalId);

		//GoalIdを外す
		ggg.selectedGoalId = -1;

		ste.redraw();
	}

}
