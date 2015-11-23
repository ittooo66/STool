package Swing.Component;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;

/**
 * Created by 66 on 2015/11/23.
 */
public class TitledLabelPanel extends JPanel {
	JLabel jLabel;

	public TitledLabelPanel(String title, int width, int height) {
		jLabel = new JLabel(title);
		jLabel.setPreferredSize(new Dimension(width, height));
		this.add(jLabel);
		this.setBorder(new TitledBorder(new EtchedBorder(), title));
	}

	public void setText(String str) {
		jLabel.setText(str);
	}
}
