package Swing;

import Models.FGModelAdapter;
import Models.Goal;
import Processing.GGGraph;
import Swing.Component.TitledJRadioButtonGroupPanel;

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
	private JPanel parentComboBoxBorder;
	private TitledJRadioButtonGroupPanel refineType, necessity;
	private JComboBox<String> parentComboBox;
	private List<Integer> parentComboBoxIdList;

	//Draw中のフラグ
	private boolean isDrawing;

	public GGEditPanel(SToolEditor ste, GGGraph ggg) {
		this.ste = ste;
		this.ggg = ggg;

		this.setLayout(new FlowLayout(FlowLayout.LEFT));
		this.setPreferredSize(new Dimension(0, 80));
		this.setBorder(new EtchedBorder());

		//NameTextArea周り
		nameArea = new JTextArea(2, 15);
		nameArea.getDocument().addDocumentListener(this);
		nameArea.addKeyListener(this);
		JScrollPane scroll = new JScrollPane(nameArea, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		JPanel nameAreaBorder = new JPanel();
		nameAreaBorder.add(scroll);
		nameAreaBorder.setBorder(new TitledBorder(new EtchedBorder(), "Goal Name"));
		this.add(nameAreaBorder);

		//Add New Goal Button
		add = new JButton("Add New Goal");
		add.addActionListener(e -> add());
		this.add(add);

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
		//RefineTyoeグループのパネル作成
		refineType = new TitledJRadioButtonGroupPanel("RefineType");
		refineType.add(refineTypeAnd);
		refineType.add(refineTypeOr);
		refineType.add(refineTypeLeaf);
		this.add(refineType);

		//Necessity周り
		necessityIsEnable = new JRadioButton("Enable");
		necessityIsEnable.setSelected(true);
		necessityIsEnable.addActionListener(this);
		necessityIsDisable = new JRadioButton("Disable");
		necessityIsDisable.addActionListener(this);
		//Necessityグループのパネル作成
		necessity = new TitledJRadioButtonGroupPanel("Necessity");
		necessity.add(necessityIsEnable);
		necessity.add(necessityIsDisable);
		this.add(necessity);

		//RemoveButton
		remove = new JButton("Remove Goal");
		remove.addActionListener(e -> remove());
		remove.setVisible(false);
		this.add(remove);
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
			if (ste.fgm.getVersion() == FGModelAdapter.VERSION.ASIS) {
				if (selectedGoal.isEnableForAsIs) {
					necessityIsEnable.setSelected(true);
				} else {
					necessityIsDisable.setSelected(true);
				}
			} else if (ste.fgm.getVersion() == FGModelAdapter.VERSION.TOBE) {
				if (selectedGoal.isEnableForToBe) {
					necessityIsEnable.setSelected(true);
				} else {
					necessityIsDisable.setSelected(true);
				}
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
		boolean isEnableForAsIs = prevGoal.isEnableForAsIs;
		boolean isEnableForToBe = prevGoal.isEnableForToBe;
		if (ste.fgm.getVersion() == FGModelAdapter.VERSION.ASIS) {
			isEnableForAsIs = necessityIsEnable.isSelected();
		} else if (ste.fgm.getVersion() == FGModelAdapter.VERSION.TOBE) {
			isEnableForToBe = necessityIsEnable.isSelected();
		}

		//fgm編集
		String str = ste.fgm.editGoal(prevGoal.id, name, ct, parentId, isEnableForAsIs, isEnableForToBe);
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

		//Goal追加時の重なり防止
		int h = 30;
		for (int i = 30; i < ggg.height - 30; i += 10) {
			boolean hasGoal = false;
			for (Goal g : ste.fgm.getGoals()) {
				if (0 < g.x && g.x < 200 && g.y - 45 < i && i < g.y + 45) hasGoal = true;
			}
			if (!hasGoal) {
				h = i;
				break;
			}
		}

		//fgm編集
		String str = ste.fgm.addGoal(name, parentGoalId, (int) ggg.textWidth(name) / 2 + 25, h);
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
				"Are you sure you want to remove this goal AND ASSOCIATED USECASE ?",
				"OK", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) != JOptionPane.YES_OPTION) return;

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
