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
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;

public class GGEditPanel extends JPanel implements ActionListener, DocumentListener, KeyListener {

	//各種インスタンスへの参照
	private final SToolEditor ste;
	private final GGGraph ggg;

	//GGEditorコンポーネント
	private JButton add, remove;
	private JRadioButton refineTypeAnd, refineTypeOr, refineTypeLeaf, necessityIsEnable, necessityIsDisable;
	private JTextArea nameArea;
	private JPanel refineType, necessity, parentComboBoxBorder;
	private JComboBox<String> parentComboBox;
	private List<Integer> parentComboBoxIdList;

	//Draw中のフラグ
	private boolean isDrawing;

	public GGEditPanel(SToolEditor ste, GGGraph ggg) {
		this.ste = ste;
		this.ggg = ggg;

		this.setLayout(new FlowLayout(FlowLayout.LEFT));
		this.setPreferredSize(new Dimension(0, 80));

		//RemoveButton
		remove = new JButton("Remove Goal");
		remove.addActionListener(e -> remove());
		remove.setVisible(false);
		this.add(remove);

		//Add New Goal Button
		add = new JButton("Add New Goal");
		add.addActionListener(e -> add());
		this.add(add);

		//NameTextArea周り
		nameArea = new JTextArea(2, 15);
		nameArea.getDocument().addDocumentListener(this);
		nameArea.addKeyListener(this);
		JScrollPane scroll = new JScrollPane(nameArea, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		JPanel nameAreaBorder = new JPanel();
		nameAreaBorder.add(scroll);
		nameAreaBorder.setBorder(new TitledBorder(new EtchedBorder(), "Name"));
		this.add(nameAreaBorder);

		//parent指定ComboBox周り
		parentComboBox = new JComboBox<>();
		parentComboBox.addActionListener(this);
		parentComboBoxBorder = new JPanel();
		parentComboBoxBorder.add(parentComboBox);
		parentComboBoxBorder.setBorder(new TitledBorder(new EtchedBorder(), "Parent Goal"));
		this.add(parentComboBoxBorder);
		parentComboBoxIdList = new ArrayList<>();

		//refineType周り
		refineTypeAnd = new JRadioButton("AND");
		refineTypeAnd.setSelected(true);
		refineTypeAnd.addActionListener(this);
		refineTypeOr = new JRadioButton("OR");
		refineTypeOr.addActionListener(this);
		refineTypeLeaf = new JRadioButton("LEAF");
		refineTypeLeaf.addActionListener(this);
		//refineTypeButtonGroup作成
		ButtonGroup refineTypeButtonGroup = new ButtonGroup();
		refineTypeButtonGroup.add(refineTypeAnd);
		refineTypeButtonGroup.add(refineTypeOr);
		refineTypeButtonGroup.add(refineTypeLeaf);
		//RefineTyoeグループのラベル（パネル）作成
		refineType = new JPanel();
		refineType.add(refineTypeAnd);
		refineType.add(refineTypeOr);
		refineType.add(refineTypeLeaf);
		refineType.setBorder(new TitledBorder(new EtchedBorder(), "RefineType"));
		refineType.setVisible(false);
		this.add(refineType);

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
		//ComboBox詰め替え
		ste.fgm.getGoals().stream().filter(g -> g.id != ggg.selectedGoalId).forEach(g -> {
			parentComboBox.addItem(g.name);
			parentComboBoxIdList.add(g.id);
		});

		//GGEdit:GGGraph.selectedGoalIdに応じたエディタ画面に更新
		if (ggg.selectedGoalId != -1) {

			//選択中のゴールを取得
			Goal selectedGoal = ste.fgm.getGoalById(ggg.selectedGoalId);

			//Text更新
			if (!nameArea.hasFocus()) nameArea.setText(selectedGoal.name);

			//RefineType更新
			switch (selectedGoal.childrenType) {
				case AND:
					refineTypeAnd.setSelected(true);
					break;
				case OR:
					refineTypeOr.setSelected(true);
					break;
				case LEAF:
					refineTypeLeaf.setSelected(true);
					break;
			}

			//ComboBox選択
			for (int id : parentComboBoxIdList) {
				if (selectedGoal.parentId == id) {
					parentComboBox.setSelectedIndex(parentComboBoxIdList.indexOf(id));
					break;
				}
			}

			//Necessity更新
			if (selectedGoal.isEnable) {
				necessityIsEnable.setSelected(true);
			} else {
				necessityIsDisable.setSelected(true);
			}
		}

		//GGEditor各種コンポーネント:表示・非表示切り替え
		add.setVisible(ggg.selectedGoalId == -1);
		remove.setVisible(ggg.selectedGoalId != -1);
		necessity.setVisible(ggg.selectedGoalId != -1);
		refineType.setVisible(ggg.selectedGoalId != -1);
		parentComboBoxBorder.setVisible(ggg.selectedGoalId != -1);

		//draw終了
		isDrawing = false;
	}

	private void edit() {
		//各種コンポーネントからパラメータ取得
		String name = nameArea.getText();
		Goal prevGoal = ste.fgm.getGoalById(ggg.selectedGoalId);
		Goal.ChildrenType ct = refineTypeAnd.isSelected() ? Goal.ChildrenType.AND : refineTypeOr.isSelected() ? Goal.ChildrenType.OR : Goal.ChildrenType.LEAF;
		int parentId = parentComboBoxIdList.get(parentComboBox.getSelectedIndex());
		boolean isEnable = necessityIsEnable.isSelected();

		//fgm編集
		String str = ste.fgm.editGoal(prevGoal.id, name, ct, parentId, isEnable);
		if (str != null) {
			JOptionPane.showMessageDialog(this, str, "ERROR", JOptionPane.ERROR_MESSAGE);
		}

		//再描画
		ste.redraw();
	}

	private void add() {
		//各種コンポーネントからパラメータ取得
		int parentGoalId = parentComboBoxIdList.get(parentComboBox.getSelectedIndex());
		String name = nameArea.getText();

		//fgm編集
		String str = ste.fgm.addGoal(name, parentGoalId, ggg.width / 2, ggg.height / 2);
		if (str != null) {
			JOptionPane.showMessageDialog(this, str, "ERROR", JOptionPane.ERROR_MESSAGE);
		}

		nameArea.setText("");

		//再描画
		ste.redraw();
	}

	private void remove() {
		//削除確認
		if (JOptionPane.showConfirmDialog(this,
				"このゴールに関連するユースケースも削除されます。実行しますか？",
				"確認", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) != JOptionPane.YES_OPTION) return;

		//fgm編集
		String str = ste.fgm.removeGoal(ggg.selectedGoalId);
		if (str != null) {
			JOptionPane.showMessageDialog(this, str, "ERROR", JOptionPane.ERROR_MESSAGE);
		}

		//GoalIdを外す
		ggg.selectedGoalId = -1;

		//再描画
		ste.redraw();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (!isDrawing && ggg.selectedGoalId != -1) {
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

	//JTextArea初期化
	public void initTextArea() {
		isDrawing = true;
		nameArea.setText("");
		isDrawing = false;
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	private boolean shiftKeyPressed;

	@Override
	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
			case KeyEvent.VK_SHIFT:
				shiftKeyPressed = true;
				break;
			case KeyEvent.VK_ENTER:
				if (shiftKeyPressed) {
					add();
					nameArea.setText("");
				}
				break;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_SHIFT) shiftKeyPressed = false;
	}
}
