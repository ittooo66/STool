package Swing.Component;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentListener;
import java.awt.event.KeyListener;

/**
 * Created by 66 on 2015/11/23.
 * TitleつきTextAreaPanel
 */
public class TitledTextAreaPanel extends JPanel {

	private JTextArea textArea;

	public TitledTextAreaPanel(String title, int width, int height) {
		textArea = new JTextArea(height, width);
		JScrollPane scroll = new JScrollPane(textArea, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		this.add(scroll);
		this.setBorder(new TitledBorder(new EtchedBorder(), title));
	}

	public void addDocumentListener(DocumentListener documentListener) {
		textArea.getDocument().addDocumentListener(documentListener);
	}

	public void addKeyListener(KeyListener keyListener) {
		textArea.addKeyListener(keyListener);
	}

	public void setText(String str) {
		textArea.setText(str);
	}

	public String getText() {
		return textArea.getText();
	}

	public boolean hasFocus(){
		return textArea.hasFocus();
	}

}
