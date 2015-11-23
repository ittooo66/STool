package Swing;

import Models.Domain;
import Processing.PFGraph;
import Swing.Component.TitledJRadioButtonGroupPanel;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import static Processing.Component.PUtility.mouseIsInRect;

public class PFEditPanel extends JPanel implements ActionListener, DocumentListener, KeyListener {
	//各種インスタンスへの参照
	private final SToolEditor ste;
	private final PFGraph pfg;

	//PFEditorコンポーネント
	private JButton remove, add;
	private JTextArea nameArea;
	private TitledJRadioButtonGroupPanel domainType;

	//Draw中のフラグ
	private boolean isDrawing;

	public PFEditPanel(SToolEditor ste, PFGraph pfg) {
		this.ste = ste;
		this.pfg = pfg;

		this.setLayout(new FlowLayout(FlowLayout.LEFT));
		this.setPreferredSize(new Dimension(0, 80));
		this.setBorder(new EtchedBorder());

		//NameTextArea周り
		nameArea = new JTextArea(2, 15);
		nameArea.getDocument().addDocumentListener(this);
		nameArea.addKeyListener(this);
		JScrollPane pfScroll = new JScrollPane(nameArea, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		JPanel pfEditNameFieldBorder = new JPanel();
		pfEditNameFieldBorder.add(pfScroll);
		pfEditNameFieldBorder.setBorder(new TitledBorder(new EtchedBorder(), "Domain Name"));
		this.add(pfEditNameFieldBorder);

		//Add New Domain Button
		add = new JButton("Add New Domain");
		add.addActionListener(e -> add());
		this.add(add);

		//DomainType
		domainType = new TitledJRadioButtonGroupPanel("DomainType");
		domainType.add(new JRadioButton(Domain.DomainType.getString(Domain.DomainType.NONE), true));
		domainType.add(new JRadioButton(Domain.DomainType.getString(Domain.DomainType.BIDDABLE)));
		domainType.add(new JRadioButton(Domain.DomainType.getString(Domain.DomainType.CAUSAL)));
		domainType.add(new JRadioButton(Domain.DomainType.getString(Domain.DomainType.LEXICAL)));
		domainType.add(new JRadioButton(Domain.DomainType.getString(Domain.DomainType.SYSTEM)));
		domainType.add(new JRadioButton(Domain.DomainType.getString(Domain.DomainType.DESIGNED)));
		domainType.addActionListenerToAll(this);
		this.add(domainType);

		//Remove Button
		remove = new JButton("Remove Domain");
		remove.addActionListener(e -> remove());
		remove.setVisible(false);
		this.add(remove);
	}

	public void redraw() {
		isDrawing = true;

		//PFEdit:PFGraph.selectedDomainIdに応じたエディタ画面に更新
		if (pfg.selectedDomainId != -1) {
			//選択中のドメインを取得
			Domain selectedDomain = ste.fgm.getDomainById(pfg.selectedDomainId);
			//Text更新
			if (!nameArea.hasFocus()) nameArea.setText(selectedDomain.name);

			//DomainType更新
			domainType.setSelected(selectedDomain.domainType.toString());
		}

		//PFEditor各種コンポーネント：表示・非表示切り替え
		add.setVisible(pfg.selectedDomainId == -1);
		remove.setVisible(pfg.selectedDomainId != -1);
		domainType.setVisible(pfg.selectedDomainId != -1);

		isDrawing = false;
	}

	private void add() {
		//各種パラメータ取得
		String name = nameArea.getText();

		//Domain追加時の重なり防止
		int h = 30;
		for (int i = 30; i < pfg.height - 30; i += 10) {
			boolean hasDomain = false;
			for (Domain d : ste.fgm.getDomains()) {
				if (mouseIsInRect(0, d.y - 45, 200, 90, d.x, i)) hasDomain = true;
			}
			if (!hasDomain) {
				h = i;
				break;
			}
		}

		//fgm編集
		String str = ste.fgm.addDomain(name, Domain.DomainType.NONE, (int) pfg.textWidth(name) / 2 + 25, h);
		if (str != null) {
			JOptionPane.showMessageDialog(this, str, "ERROR", JOptionPane.ERROR_MESSAGE);
		}

		nameArea.setText("");

		//再描画
		ste.redraw();
	}

	private void edit() {
		//各種パラメータ取得
		String name = nameArea.getText();
		int id = pfg.selectedDomainId;
		Domain.DomainType dt = Domain.DomainType.parse(domainType.getSelectedButtonCommand());

		//fgm編集
		String str = ste.fgm.editDomain(id, name, dt);
		if (str != null) {
			JOptionPane.showMessageDialog(this, str, "Error", JOptionPane.ERROR_MESSAGE);
		}

		//再描画
		ste.redraw();
	}

	private void remove() {
		//fgm編集
		String str = ste.fgm.removeDomain(pfg.selectedDomainId);
		if (str != null) {
			JOptionPane.showMessageDialog(this, str, "Error", JOptionPane.ERROR_MESSAGE);
		}

		//DomainIdを外す
		pfg.selectedDomainId = -1;

		//再描画
		ste.redraw();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (!isDrawing && pfg.selectedDomainId != -1) {
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
