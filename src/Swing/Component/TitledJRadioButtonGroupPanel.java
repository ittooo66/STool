package Swing.Component;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

/**
 * Created by 66 on 2015/11/18.
 * 排他的JRadioButtonをグループでまとめるためのClass
 */
public class TitledJRadioButtonGroupPanel extends JPanel {

	private ButtonGroup bg;

	public TitledJRadioButtonGroupPanel(String title) {
		this.setBorder(new TitledBorder(new EtchedBorder(), title));
		bg = new ButtonGroup();
	}

	public void add(JRadioButton jrb) {
		super.add(jrb);
		bg.add(jrb);
	}
}
