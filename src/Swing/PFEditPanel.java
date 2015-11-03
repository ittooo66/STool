package Swing;

import Models.Domain;
import Processing.PFGraph;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;

/**
 * Created by 66 on 2015/11/03.
 * ProblemFrameEditorの右部分
 */
public class PFEditPanel extends JPanel {
	//各種インスタンスへの参照
	private final SToolEditor ste;
	private final PFGraph pfg;

	//PFEditorコンポーネント
	private JButton remove, add, edit;
	private JTextArea nameArea;
	private JRadioButton domainTypeNone, domainTypeBiddable, domainTypeCausal, domainTypeLexical, domainTypeSystem, domainTypeDesigned;
	private JPanel domainType;

	public PFEditPanel(SToolEditor ste, PFGraph pfg) {
		this.ste = ste;
		this.pfg = pfg;

		this.setLayout(null);
		this.setPreferredSize(new Dimension(200, 0));

		//RemoveButton
		remove = new JButton("Remove");
		remove.addActionListener(e -> removeButtonPressed());
		remove.setBounds(105, 5, 90, 30);
		remove.setVisible(false);
		this.add(remove);
		//AddButton
		add = new JButton("Add");
		add.addActionListener(e -> addButtonPressed());
		add.setBounds(105, 400, 90, 30);
		this.add(add);
		//EditButton
		edit = new JButton("Edit");
		edit.addActionListener(e -> editButtonPressed());
		edit.setBounds(105, 400, 90, 30);
		edit.setVisible(false);
		this.add(edit);

		//NameTextArea周り
		nameArea = new JTextArea(5, 15);
		JScrollPane pfScroll = new JScrollPane(nameArea);
		JPanel pfEditNameFieldBorder = new JPanel();
		pfEditNameFieldBorder.add(pfScroll);
		pfEditNameFieldBorder.setBorder(new TitledBorder(new EtchedBorder(), "Name"));
		pfEditNameFieldBorder.setBounds(5, 50, 193, 120);
		this.add(pfEditNameFieldBorder);

		//DomainType周り
		domainTypeNone = new JRadioButton("NONE");
		domainTypeNone.setSelected(true);
		domainTypeBiddable = new JRadioButton("BIDDABLE");
		domainTypeCausal = new JRadioButton("CAUSAL");
		domainTypeLexical = new JRadioButton("LEXICAL");
		domainTypeSystem = new JRadioButton("SYSTEM");
		domainTypeDesigned = new JRadioButton("DESIGNED");
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

		//PFEdit:PFGraph.selectedDomainIdに応じたエディタ画面に更新
		if (pfg.selectedDomainId != -1) {
			//選択中のドメインを取得
			Domain selectedDomain = ste.fgm.getDomainById(pfg.selectedDomainId);
			//Text更新
			nameArea.setText(selectedDomain.name);
			//DomainType更新
			if (selectedDomain.domainType == Domain.DomainType.NONE) {
				domainTypeNone.setSelected(true);
			} else if (selectedDomain.domainType == Domain.DomainType.BIDDABLE) {
				domainTypeBiddable.setSelected(true);
			} else if (selectedDomain.domainType == Domain.DomainType.CAUSAL) {
				domainTypeCausal.setSelected(true);
			} else if (selectedDomain.domainType == Domain.DomainType.DESIGNED) {
				domainTypeDesigned.setSelected(true);
			} else if (selectedDomain.domainType == Domain.DomainType.SYSTEM) {
				domainTypeSystem.setSelected(true);
			} else if (selectedDomain.domainType == Domain.DomainType.LEXICAL) {
				domainTypeLexical.setSelected(true);
			}
		}

		//PFEditor各種コンポーネント：表示・非表示切り替え
		add.setVisible(pfg.selectedDomainId == -1);
		remove.setVisible(pfg.selectedDomainId != -1);
		edit.setVisible(pfg.selectedDomainId != -1);
		domainType.setVisible(pfg.selectedDomainId != -1);
	}

	private void addButtonPressed() {
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

	private void editButtonPressed() {
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

	private void removeButtonPressed() {
		//選択中のドメインを削除
		ste.fgm.removeDomain(pfg.selectedDomainId);

		//GoalIdを外す
		pfg.selectedDomainId = -1;

		ste.redraw();
	}

}
