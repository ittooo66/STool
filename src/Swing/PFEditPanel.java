package Swing;

import Models.Domain;
import Processing.PFGraph;

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

public class PFEditPanel extends JPanel implements ActionListener, DocumentListener, KeyListener {
	//各種インスタンスへの参照
	private final SToolEditor ste;
	private final PFGraph pfg;

	//PFEditorコンポーネント
	private JButton remove, add;
	private JTextArea nameArea;
	private JRadioButton domainTypeNone, domainTypeBiddable, domainTypeCausal, domainTypeLexical, domainTypeSystem, domainTypeDesigned;
	private JPanel domainType;

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

		//DomainType周り
		domainTypeNone = new JRadioButton("NONE");
		domainTypeNone.setSelected(true);
		domainTypeNone.addActionListener(this);
		domainTypeBiddable = new JRadioButton("BIDDABLE");
		domainTypeBiddable.addActionListener(this);
		domainTypeCausal = new JRadioButton("CAUSAL");
		domainTypeCausal.addActionListener(this);
		domainTypeLexical = new JRadioButton("LEXICAL");
		domainTypeLexical.addActionListener(this);
		domainTypeSystem = new JRadioButton("SYSTEM");
		domainTypeSystem.addActionListener(this);
		domainTypeDesigned = new JRadioButton("DESIGNED");
		domainTypeDesigned.addActionListener(this);
		//DomainTypeButtonGroup作成
		ButtonGroup domainTypeButtonGroup = new ButtonGroup();
		domainTypeButtonGroup.add(domainTypeNone);
		domainTypeButtonGroup.add(domainTypeBiddable);
		domainTypeButtonGroup.add(domainTypeCausal);
		domainTypeButtonGroup.add(domainTypeLexical);
		domainTypeButtonGroup.add(domainTypeSystem);
		domainTypeButtonGroup.add(domainTypeDesigned);
		//DomainTyoeグループのラベル（パネル）作成
		domainType = new JPanel();
		domainType.add(domainTypeNone);
		domainType.add(domainTypeBiddable);
		domainType.add(domainTypeCausal);
		domainType.add(domainTypeLexical);
		domainType.add(domainTypeSystem);
		domainType.add(domainTypeDesigned);
		domainType.setBorder(new TitledBorder(new EtchedBorder(), "DomainType"));
		domainType.setVisible(false);
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
			switch (selectedDomain.domainType) {
				case NONE:
					domainTypeNone.setSelected(true);
					break;
				case BIDDABLE:
					domainTypeBiddable.setSelected(true);
					break;
				case CAUSAL:
					domainTypeCausal.setSelected(true);
					break;
				case DESIGNED:
					domainTypeDesigned.setSelected(true);
					break;
				case SYSTEM:
					domainTypeSystem.setSelected(true);
					break;
				case LEXICAL:
					domainTypeLexical.setSelected(true);
			}
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

		//fgm編集
		String str = ste.fgm.addDomain(name, Domain.DomainType.NONE, pfg.width / 2, pfg.height / 2);
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
		Domain.DomainType dt = domainTypeBiddable.isSelected() ?
				Domain.DomainType.BIDDABLE : domainTypeCausal.isSelected() ?
				Domain.DomainType.CAUSAL : domainTypeDesigned.isSelected() ?
				Domain.DomainType.DESIGNED : domainTypeLexical.isSelected() ?
				Domain.DomainType.LEXICAL : domainTypeSystem.isSelected() ?
				Domain.DomainType.SYSTEM : Domain.DomainType.NONE;

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
