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

public class PFEditPanel extends JPanel implements ActionListener, DocumentListener {
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

		this.setLayout(null);
		this.setPreferredSize(new Dimension(200, 0));

		//Remove Button
		remove = new JButton("Remove");
		remove.addActionListener(e -> remove());
		remove.setBounds(105, 5, 90, 30);
		remove.setVisible(false);
		this.add(remove);

		//Add New Domain Button
		add = new JButton("Add New Domain");
		add.addActionListener(e -> add());
		add.setBounds(65, 200, 130, 30);
		this.add(add);

		//NameTextArea周り
		nameArea = new JTextArea(5, 15);
		nameArea.getDocument().addDocumentListener(this);
		JScrollPane pfScroll = new JScrollPane(nameArea);
		JPanel pfEditNameFieldBorder = new JPanel();
		pfEditNameFieldBorder.add(pfScroll);
		pfEditNameFieldBorder.setBorder(new TitledBorder(new EtchedBorder(), "Name"));
		pfEditNameFieldBorder.setBounds(5, 50, 193, 120);
		this.add(pfEditNameFieldBorder);

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
		domainType.setBounds(5, 200, 193, 150);
		domainType.setVisible(false);
		this.add(domainType);
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
		//名前取得
		String name = nameArea.getText();

		//名前欄にちゃんと中身があるか
		if (name.equals("")) {
			JOptionPane.showMessageDialog(this, "なまえをいれてください", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		} else {
			nameArea.setText("");
		}

		//追加
		ste.fgm.addDomain(name, Domain.DomainType.NONE, pfg.width / 2, pfg.height / 2);
		ste.redraw();
	}

	private void edit() {
		//各種コンポーネントからパラメータ取得
		String name = nameArea.getText();
		int id = pfg.selectedDomainId;
		Domain.DomainType dt = domainTypeBiddable.isSelected() ?
				Domain.DomainType.BIDDABLE : domainTypeCausal.isSelected() ?
				Domain.DomainType.CAUSAL : domainTypeDesigned.isSelected() ?
				Domain.DomainType.DESIGNED : domainTypeLexical.isSelected() ?
				Domain.DomainType.LEXICAL : domainTypeSystem.isSelected() ?
				Domain.DomainType.SYSTEM : Domain.DomainType.NONE;

		//fgm編集
		if (!ste.fgm.editDomain(id, name, dt)) {
			JOptionPane.showMessageDialog(this, "モデルを編集できませんでした", "Error", JOptionPane.ERROR_MESSAGE);
		}

		ste.redraw();
	}

	private void remove() {
		//選択中のドメインを削除
		ste.fgm.removeDomain(pfg.selectedDomainId);

		//GoalIdを外す
		pfg.selectedDomainId = -1;

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
}
