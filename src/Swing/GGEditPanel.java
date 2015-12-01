package Swing;

import Models.FGModelAdapter;
import Models.Goal;
import Processing.GGGraph;
import Swing.Component.TitledRadioButtonGroupPanel;
import Swing.Component.TitledTextAreaPanel;
import Swing.Component.TitledComboBoxWithValuePanel;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class GGEditPanel extends JPanel implements ActionListener, DocumentListener, KeyListener {

	//各種インスタンスへの参照
	private final SToolEditor ste;
	private final GGGraph ggg;

	//各種Component
	private TitledTextAreaPanel nameAreaPanel;
	private TitledRadioButtonGroupPanel refineType, necessity;
	private TitledComboBoxWithValuePanel parentGoalComboBoxPanel;
	private JButton addButton, removeButton;

	//Draw中のフラグ
	private boolean isDrawing;

	public GGEditPanel(SToolEditor ste, GGGraph ggg) {
		this.ste = ste;
		this.ggg = ggg;

		this.setLayout(new FlowLayout(FlowLayout.LEFT));
		this.setPreferredSize(new Dimension(0, 80));
		this.setBorder(new EtchedBorder());

		//コンポーネント作成＋追加
		nameAreaPanel = new TitledTextAreaPanel("Goal Name", 15, 2);
		nameAreaPanel.addDocumentListener(this);
		nameAreaPanel.addKeyListener(this);
		this.add(nameAreaPanel);
		addButton = new JButton("Add New Goal");
		addButton.addActionListener(e -> add());
		this.add(addButton);
		parentGoalComboBoxPanel = new TitledComboBoxWithValuePanel("Parent Goal");
		parentGoalComboBoxPanel.addActionListenerToComboBox(this);
		this.add(parentGoalComboBoxPanel);
		refineType = new TitledRadioButtonGroupPanel("RefineType");
		refineType.add(new JRadioButton(Goal.ChildrenType.getString(Goal.ChildrenType.AND), true));
		refineType.add(new JRadioButton(Goal.ChildrenType.getString(Goal.ChildrenType.OR)));
		refineType.add(new JRadioButton(Goal.ChildrenType.getString(Goal.ChildrenType.LEAF)));
		refineType.addActionListenerToAll(this);
		this.add(refineType);
		necessity = new TitledRadioButtonGroupPanel("Necessity");
		necessity.add(new JRadioButton("Enable", true));
		necessity.add(new JRadioButton("Disable"));
		necessity.addActionListenerToAll(this);
		this.add(necessity);
		removeButton = new JButton("Remove Goal");
		removeButton.addActionListener(e -> remove());
		removeButton.setVisible(false);
		this.add(removeButton);
	}

	public void redraw() {
		//draw開始
		isDrawing = true;

		//ComboBox詰め替え
		parentGoalComboBoxPanel.initItem();
		parentGoalComboBoxPanel.addItem("NONE (Top Goal)", -1);
		ste.fgm.getGoals().stream().filter(g -> g.id != ggg.selectedGoalId).forEach(g -> parentGoalComboBoxPanel.addItem(g.name, g.id));

		//GGEdit:GGGraph.selectedGoalIdに応じたエディタ画面に更新
		if (ggg.selectedGoalId != -1) {

			//選択中のゴールを取得
			Goal selectedGoal = ste.fgm.getGoalById(ggg.selectedGoalId);

			//Text更新
			if (!nameAreaPanel.hasFocus()) nameAreaPanel.setText(selectedGoal.name);

			//RefineType更新
			refineType.setSelected(Goal.ChildrenType.getString(selectedGoal.childrenType));

			//ComboBox選択
			parentGoalComboBoxPanel.setSelected(selectedGoal.parentId);

			//Necessity更新
			if (ste.fgm.getVersion() == FGModelAdapter.VERSION.ASIS) {
				if (selectedGoal.isEnableForAsIs) {
					necessity.setSelected("Enable");
				} else {
					necessity.setSelected("Disable");
				}
			} else if (ste.fgm.getVersion() == FGModelAdapter.VERSION.TOBE) {
				if (selectedGoal.isEnableForToBe) {
					necessity.setSelected("Enable");
				} else {
					necessity.setSelected("Disable");
				}
			}
		}

		//Visibility切り替え
		removeButton.setVisible(ggg.selectedGoalId != -1);
		necessity.setVisible(ggg.selectedGoalId != -1);
		refineType.setVisible(ggg.selectedGoalId != -1);
		parentGoalComboBoxPanel.setVisible(ggg.selectedGoalId != -1);
		addButton.setVisible(ggg.selectedGoalId == -1);
		//draw終了
		isDrawing = false;
	}

	private void edit() {
		//各種コンポーネントからパラメータ取得
		String name = nameAreaPanel.getText();
		Goal prevGoal = ste.fgm.getGoalById(ggg.selectedGoalId);
		Goal.ChildrenType ct = Goal.ChildrenType.parse(refineType.getSelectedButtonCommand());
		int parentId = parentGoalComboBoxPanel.getSelectedParam();
		boolean isEnableForAsIs = prevGoal.isEnableForAsIs;
		boolean isEnableForToBe = prevGoal.isEnableForToBe;
		if (ste.fgm.getVersion() == FGModelAdapter.VERSION.ASIS) {
			isEnableForAsIs = necessity.getSelectedButtonCommand().equals("Enable");
		} else if (ste.fgm.getVersion() == FGModelAdapter.VERSION.TOBE) {
			isEnableForToBe = necessity.getSelectedButtonCommand().equals("Enable");
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
		int parentGoalId = parentGoalComboBoxPanel.getSelectedParam();
		String name = nameAreaPanel.getText();

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

		nameAreaPanel.setText("");

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
		nameAreaPanel.setText("");
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
					nameAreaPanel.setText("");
				}
				break;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_SHIFT) shiftKeyPressed = false;
	}

	public void requestFocusToTextArea() {
		nameAreaPanel.requestFocus();
	}
}
