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
	private JButton pfEditRemove, pfEditAdd, pfEditEdit;
	private JTextArea pfEditNameArea;
	private JRadioButton pfEditDomainTypeNone, pfEditDomainTypeBiddable, pfEditDomainTypeCausal, pfEditDomainTypeLexical, pfEditDomainTypeSystem, pfEditDomainTypeDesigned;
	private JPanel pfEditDomainType;

	public PFEditPanel(SToolEditor ste, PFGraph pfg) {
		this.ste = ste;
		this.pfg = pfg;

		this.setLayout(null);
		this.setPreferredSize(new Dimension(200, 0));

		//RemoveButton
		pfEditRemove = new JButton("Remove");
		pfEditRemove.addActionListener(e -> pfEditRemoveButtonPressed());
		pfEditRemove.setBounds(105, 5, 90, 30);
		pfEditRemove.setVisible(false);
		this.add(pfEditRemove);
		//AddButton
		pfEditAdd = new JButton("Add");
		pfEditAdd.addActionListener(e -> pfEditAddButtonPressed());
		pfEditAdd.setBounds(105, 400, 90, 30);
		this.add(pfEditAdd);
		//EditButton
		pfEditEdit = new JButton("Edit");
		pfEditEdit.addActionListener(e -> pfEditEditButtonPressed());
		pfEditEdit.setBounds(105, 400, 90, 30);
		pfEditEdit.setVisible(false);
		this.add(pfEditEdit);

		//NameTextArea周り
		pfEditNameArea = new JTextArea(5, 15);
		JScrollPane pfScroll = new JScrollPane(pfEditNameArea);
		JPanel pfEditNameFieldBorder = new JPanel();
		pfEditNameFieldBorder.add(pfScroll);
		pfEditNameFieldBorder.setBorder(new TitledBorder(new EtchedBorder(), "Name"));
		pfEditNameFieldBorder.setBounds(5, 50, 193, 120);
		this.add(pfEditNameFieldBorder);

		//DomainType周り
		pfEditDomainTypeNone = new JRadioButton("NONE");
		pfEditDomainTypeNone.setSelected(true);
		pfEditDomainTypeBiddable = new JRadioButton("BIDDABLE");
		pfEditDomainTypeCausal = new JRadioButton("CAUSAL");
		pfEditDomainTypeLexical = new JRadioButton("LEXICAL");
		pfEditDomainTypeSystem = new JRadioButton("SYSTEM");
		pfEditDomainTypeDesigned = new JRadioButton("DESIGNED");
		//DomainTypeButtonGroup作成
		ButtonGroup pfEditDomainTypeButtonGroup = new ButtonGroup();
		pfEditDomainTypeButtonGroup.add(pfEditDomainTypeNone);
		pfEditDomainTypeButtonGroup.add(pfEditDomainTypeBiddable);
		pfEditDomainTypeButtonGroup.add(pfEditDomainTypeCausal);
		pfEditDomainTypeButtonGroup.add(pfEditDomainTypeLexical);
		pfEditDomainTypeButtonGroup.add(pfEditDomainTypeSystem);
		pfEditDomainTypeButtonGroup.add(pfEditDomainTypeDesigned);
		//DomainTyoeグループのラベル（パネル）作成
		pfEditDomainType = new JPanel();
		pfEditDomainType.add(pfEditDomainTypeNone);
		pfEditDomainType.add(pfEditDomainTypeBiddable);
		pfEditDomainType.add(pfEditDomainTypeCausal);
		pfEditDomainType.add(pfEditDomainTypeLexical);
		pfEditDomainType.add(pfEditDomainTypeSystem);
		pfEditDomainType.add(pfEditDomainTypeDesigned);
		pfEditDomainType.setBorder(new TitledBorder(new EtchedBorder(), "DomainType"));
		pfEditDomainType.setBounds(5, 200, 193, 150);
		pfEditDomainType.setVisible(false);
		this.add(pfEditDomainType);
	}

	public void redraw() {

		//PFEdit:PFGraph.selectedDomainIdに応じたエディタ画面に更新
		if (pfg.selectedDomainId != -1) {
			//選択中のドメインを取得
			Domain selectedDomain = ste.fgm.getDomainById(pfg.selectedDomainId);

			//Text更新
			pfEditNameArea.setText(selectedDomain.name);

			//DomainType更新
			if (selectedDomain.domainType == Domain.DomainType.NONE) {
				pfEditDomainTypeNone.setSelected(true);
			} else if (selectedDomain.domainType == Domain.DomainType.BIDDABLE) {
				pfEditDomainTypeBiddable.setSelected(true);
			} else if (selectedDomain.domainType == Domain.DomainType.CAUSAL) {
				pfEditDomainTypeCausal.setSelected(true);
			} else if (selectedDomain.domainType == Domain.DomainType.DESIGNED) {
				pfEditDomainTypeDesigned.setSelected(true);
			} else if (selectedDomain.domainType == Domain.DomainType.SYSTEM) {
				pfEditDomainTypeSystem.setSelected(true);
			} else if (selectedDomain.domainType == Domain.DomainType.LEXICAL) {
				pfEditDomainTypeLexical.setSelected(true);
			}
		}


		//PFEditor各種コンポーネント：表示・非表示切り替え
		pfEditAdd.setVisible(pfg.selectedDomainId == -1);
		pfEditRemove.setVisible(pfg.selectedDomainId != -1);
		pfEditEdit.setVisible(pfg.selectedDomainId != -1);
		pfEditDomainType.setVisible(pfg.selectedDomainId != -1);

	}


	private void pfEditAddButtonPressed() {
		//名前取得
		String name = pfEditNameArea.getText();

		//名前欄にちゃんと中身があるか
		if (name.equals("")) {
			JOptionPane.showMessageDialog(this, "なまえをいれてください", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		} else {
			pfEditNameArea.setText("");
		}

		//追加
		ste.fgm.addDomain(name, Domain.DomainType.NONE, pfg.width / 2, pfg.height / 2);
		ste.redraw();
	}

	private void pfEditEditButtonPressed() {
		//各種コンポーネントからパラメータ取得
		String name = pfEditNameArea.getText();
		int id = pfg.selectedDomainId;
		Domain.DomainType dt = pfEditDomainTypeBiddable.isSelected() ?
				Domain.DomainType.BIDDABLE : pfEditDomainTypeCausal.isSelected() ?
				Domain.DomainType.CAUSAL : pfEditDomainTypeDesigned.isSelected() ?
				Domain.DomainType.DESIGNED : pfEditDomainTypeLexical.isSelected() ?
				Domain.DomainType.LEXICAL : pfEditDomainTypeSystem.isSelected() ?
				Domain.DomainType.SYSTEM : Domain.DomainType.NONE;

		//fgm編集
		if (!ste.fgm.editDomain(id, name, dt)) {
			JOptionPane.showMessageDialog(this, "モデルを編集できませんでした", "Error", JOptionPane.ERROR_MESSAGE);
		}

		ste.redraw();
	}

	private void pfEditRemoveButtonPressed() {
		//選択中のドメインを削除
		ste.fgm.removeDomain(pfg.selectedDomainId);

		//GoalIdを外す
		pfg.selectedDomainId = -1;

		ste.redraw();
	}

}
