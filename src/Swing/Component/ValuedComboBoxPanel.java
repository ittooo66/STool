package Swing.Component;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 66 on 2015/11/19.
 * 値持ちの枠付きコンボボックス
 */
public class ValuedComboBoxPanel extends JPanel {

	private List<Integer> comboBoxParamList;
	private JComboBox<String> comboBox;

	public ValuedComboBoxPanel(String title) {
		this.setBorder(new TitledBorder(new EtchedBorder(), title));
		comboBoxParamList = new ArrayList<>();
		comboBox = new JComboBox<>();
		comboBox.setPreferredSize(new Dimension(160, 20));
		this.add(comboBox);
	}

	public void addActionListenerToComboBox(ActionListener actionListener) {
		comboBox.addActionListener(actionListener);
	}

	public void addItem(String name, int param) {
		//文字列の重複を検知した場合、適宜後ろに空白を詰める
		StringBuilder sb = new StringBuilder(name);
		for (int i = 0; i < comboBox.getItemCount(); i++) {
			if (comboBox.getItemAt(i).equals(sb.toString())) {
				sb.append(" ");
			}
		}

		//重複しない文字列を生成
		String str = new String(sb);

		//詰め込み
		comboBox.addItem(str);
		comboBoxParamList.add(param);
	}

	public void initItem() {
		comboBox.removeAllItems();
		comboBoxParamList.clear();
	}

	public void setSelected(int param) {
		for (int i = 0; i < comboBox.getItemCount(); i++) {
			if (comboBoxParamList.get(i) == param) comboBox.setSelectedIndex(i);
		}
	}

	public int getSelectedParam() {
		if (comboBox.getSelectedIndex() == -1) return -1;
		return comboBoxParamList.get(comboBox.getSelectedIndex());
	}
}
