package Swing.Component;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 66 on 2015/11/18.
 * 排他的JRadioButtonをグループでまとめてごにょごにょするためのJPanel
 */
public class TitledRadioButtonGroupPanel extends JPanel {

	private ButtonGroup bg;
	private List<JRadioButton> jRadioButtonList;

	public TitledRadioButtonGroupPanel(String title) {
		this.setBorder(new TitledBorder(new EtchedBorder(), title));
		bg = new ButtonGroup();
		jRadioButtonList = new ArrayList<>();
	}

	public void add(JRadioButton jrb) {
		super.add(jrb);
		bg.add(jrb);
		jRadioButtonList.add(jrb);
	}

	public void add(JRadioButton jrb, ActionListener actionListener) {
		jrb.addActionListener(actionListener);
		super.add(jrb);
		bg.add(jrb);
		jRadioButtonList.add(jrb);
	}

	/**
	 * 選択中のボタンコマンド（コンストラクタ中のString値）を返す
	 *
	 * @return 選択中のボタンコマンド
	 */
	public String getSelectedButtonCommand() {
		for (JRadioButton jrb : jRadioButtonList) {
			if (jrb.isSelected()) return jrb.getActionCommand();
		}
		return null;
	}

	/**
	 * ボタンコマンドに合致するボタンを選択する
	 *
	 * @param command コマンド
	 */
	public void setSelected(String command) {
		for (JRadioButton jrb : jRadioButtonList) {
			if (jrb.getActionCommand().equals(command)) {
				jrb.setSelected(true);
			}
		}
	}

	/**
	 * 抱えているJRadioボタン全てにアクションリスナを付与する
	 *
	 * @param actionListener 付与するアクションリスナ
	 */
	public void addActionListenerToAll(ActionListener actionListener) {
		for (JRadioButton jrb : jRadioButtonList) jrb.addActionListener(actionListener);
	}
}
