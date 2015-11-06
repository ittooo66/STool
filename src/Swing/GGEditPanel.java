package Swing;

import Models.Goal;
import Processing.GGGraph;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class GGEditPanel extends JPanel implements ActionListener, DocumentListener {

	//各種インスタンスへの参照
	private final SToolEditor ste;
	private final GGGraph ggg;

	//GGEditorコンポーネント
	private JButton add, remove;
	private JRadioButton refineTypeAnd, refineTypeOr, refineTypeLeaf, necessityIsEnable, necessityIsDisable;
	private JTextArea nameArea;
	private JPanel ggEditRefineType, necessity;
	private JComboBox parentComboBox;
	private List<Integer> parentComboBoxIdList;

	//Draw中のフラグ
	private boolean isDrawing;

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

		//NameTextArea周り
		nameArea = new JTextArea(5, 15);
		nameArea.getDocument().addDocumentListener(this);
		JScrollPane scroll = new JScrollPane(nameArea);
		JPanel ggEditNameFieldBorder = new JPanel();
		ggEditNameFieldBorder.add(scroll);
		ggEditNameFieldBorder.setBorder(new TitledBorder(new EtchedBorder(), "Name"));
		ggEditNameFieldBorder.setBounds(5, 50, 193, 120);
		this.add(ggEditNameFieldBorder);

		//parent指定ComboBox周り
		parentComboBox = new JComboBox();
		parentComboBox.setPreferredSize(new Dimension(160, 20));
		parentComboBox.addActionListener(this);
		JPanel parentComboBoxBorder = new JPanel();
		parentComboBoxBorder.add(parentComboBox);
		parentComboBoxBorder.setBorder(new TitledBorder(new EtchedBorder(), "Parent"));
		parentComboBoxBorder.setBounds(5, 180, 193, 60);
		this.add(parentComboBoxBorder);
		parentComboBoxIdList = new ArrayList();

		//refineType周り
		refineTypeAnd = new JRadioButton("AND");
		refineTypeAnd.setSelected(true);
		refineTypeAnd.addActionListener(this);
		refineTypeOr = new JRadioButton("OR");
		refineTypeOr.addActionListener(this);
		refineTypeLeaf = new JRadioButton("LEAF");
		refineTypeLeaf.addActionListener(this);
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
		necessityIsEnable.addActionListener(this);
		necessityIsDisable = new JRadioButton("Disable");
		necessityIsDisable.addActionListener(this);
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
		//draw開始
		isDrawing = true;

		//GGEdit:ComboBox更新
		parentComboBoxIdList.clear();
		parentComboBox.removeAllItems();
		parentComboBox.addItem("NONE (Top Goal)");
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
			if (!nameArea.hasFocus()) nameArea.setText(selectedGoal.name);

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
		necessity.setVisible(ggg.selectedGoalId != -1);
		ggEditRefineType.setVisible(ggg.selectedGoalId != -1);

		//draw終了
		isDrawing = false;
	}


	private void edit() {
		//各種コンポーネントからパラメータ取得
		String name = nameArea.getText();
		Goal prevGoal = ste.fgm.getGoalById(ggg.selectedGoalId);
		Goal.ChildrenType ct = refineTypeAnd.isSelected() ? Goal.ChildrenType.AND : refineTypeOr.isSelected() ? Goal.ChildrenType.OR : Goal.ChildrenType.LEAF;
		int parentId = parentComboBoxIdList.get(parentComboBox.getSelectedIndex());

		//fgm編集
		String str = ste.fgm.editGoal(prevGoal.id, name, ct, parentId);
		if (str != null) {
			JOptionPane.showMessageDialog(this, str, "ERROR", JOptionPane.ERROR_MESSAGE);
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
		int option = JOptionPane.showConfirmDialog(this, "このゴールに関連するユースケースも削除されます。実行しますか？",
				"確認", JOptionPane.YES_NO_OPTION,
				JOptionPane.WARNING_MESSAGE);
		if (option == JOptionPane.YES_OPTION) {
			//選択中のゴールを削除
			ste.fgm.removeGoal(ggg.selectedGoalId);

			//GoalIdを外す
			ggg.selectedGoalId = -1;

			ste.redraw();
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (!isDrawing && ggg.selectedGoalId != -1) {
			edit();
		}
	}

	private void nameAreaUpdate() {
		if (!isDrawing && ggg.selectedGoalId != -1) {
			edit();
		}
	}

	@Override
	public void insertUpdate(DocumentEvent e) {
		nameAreaUpdate();
	}

	@Override
	public void removeUpdate(DocumentEvent e) {
		nameAreaUpdate();
	}

	@Override
	public void changedUpdate(DocumentEvent e) {
		nameAreaUpdate();
	}
}
