package Core;

import Models.*;
import Processing.*;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 66 on 2015/10/03.
 */
public class SToolEditor extends JFrame {

	//融合ゴールモデル
	public FGModel fgm;

	//バージョン指定
	private VERSION version;

	public enum VERSION {
		ASIS, TOBE;

		public static VERSION parse(String str) {
			switch (str) {
				case "ASIS":
					return VERSION.ASIS;
				case "TOBE":
					return VERSION.TOBE;
			}
			return null;
		}

		public static String toString(VERSION v) {
			switch (v) {
				case ASIS:
					return "ASIS";
				case TOBE:
					return "TOBE";
			}
			return null;
		}
	}

	public VERSION getVersion() {
		return version;
	}

	//ビュー指定
	private VIEWMODE viewmode;

	public enum VIEWMODE {
		ALL, REDUCED;

		public static VIEWMODE parse(String str) {
			switch (str) {
				case "ALL":
					return VIEWMODE.ALL;
				case "REDUCED":
					return VIEWMODE.REDUCED;
			}
			return null;
		}

		public static String toString(VIEWMODE v) {
			switch (v) {
				case ALL:
					return "ALL";
				case REDUCED:
					return "REDUCED";
			}
			return null;
		}
	}

	public VIEWMODE getViewmode() {
		return viewmode;
	}

	//GGEditorコンポーネント
	private JButton ggEditAdd, ggEditEdit, ggEditRemove;
	private JRadioButton ggEditRefineTypeAnd, ggEditRefineTypeOr, ggEditRefineTypeLeaf;
	private JRadioButton ggEditNecessityIsEnable, ggEditNecessityIsDisable;
	private JTextArea ggEditNameArea;
	private JPanel ggEditRefineType, ggEditNecessity;
	private JComboBox ggEditParentComboBox;
	private List<Integer> ggEditParentComboBoxIdList;

	//PFEditorコンポーネント
	private JButton pfEditRemove, pfEditAdd, pfEditEdit;
	private JTextArea pfEditNameArea;
	private JRadioButton pfEditDomainTypeNone, pfEditDomainTypeBiddable, pfEditDomainTypeCausal, pfEditDomainTypeLexical, pfEditDomainTypeSystem, pfEditDomainTypeDesigned;
	private JPanel pfEditDomainType;

	//UCEditorコンポーネント
	private JButton ucEditRemove, ucEditAdd, ucEditEdit;
	private JTextArea ucEditUsecaseName;
	private JComboBox ucEditGoalComboBox;

	//PApplet部分
	private GGGraph ggGraph;
	private PFGraph pfGraph;
	private UCGraph ucGraph;

	/**
	 * コンストラクタがほぼほぼView要素を頑張って書くスタイル
	 */
	SToolEditor() {
		//FGModel
		fgm = new FGModel();
		//FGModelテスト
		fgm.addGoal("root", -1, 100, 100);


		//////////////////////////////下部分共通パネル//////////////////////////////
		JPanel sharedEndPanel = new JPanel();
		sharedEndPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		//version_RadioButton作成
		JRadioButton asIsVer = new JRadioButton("As-Is");
		asIsVer.setSelected(true);
		version = VERSION.ASIS;
		asIsVer.addActionListener(e -> versionAsIsRadioButtonPressed());
		JRadioButton toBeVer = new JRadioButton("To-Be");
		toBeVer.addActionListener(e -> versionToBeRadioButtonPressed());
		//version_ButtonGroup作成
		ButtonGroup versionGroup = new ButtonGroup();
		versionGroup.add(asIsVer);
		versionGroup.add(toBeVer);
		//version_グループのラベル（パネル）作成
		JPanel version = new JPanel();
		version.add(asIsVer);
		version.add(toBeVer);
		version.setBorder(new TitledBorder(new EtchedBorder(), "version"));
		sharedEndPanel.add(version);
		//viewmode_RadioButton作成
		JRadioButton viewAll = new JRadioButton("All");
		viewAll.addActionListener(e -> viewmodeAllRadioButtonPressed());
		viewAll.setSelected(true);
		viewmode = VIEWMODE.ALL;
		JRadioButton viewReduced = new JRadioButton("Reduced");
		viewReduced.addActionListener(e -> viewmodeReducedRadioButtonPressed());
		//viewmode_ButtonGroup作成
		ButtonGroup viewModeGroup = new ButtonGroup();
		viewModeGroup.add(viewAll);
		viewModeGroup.add(viewReduced);
		//viewmode_グループのラベル（パネル）作成
		JPanel viewMode = new JPanel();
		viewMode.add(viewAll);
		viewMode.add(viewReduced);
		viewMode.setBorder(new TitledBorder(new EtchedBorder(), "viewmode"));
		sharedEndPanel.add(viewMode);
		//差分ブラウザを開く
		JButton diffBrouseButton = new JButton("Open Diff Browser");
		diffBrouseButton.addActionListener(e -> diffBrowseButtonPressed());
		sharedEndPanel.add(diffBrouseButton);

		//////////////////////////////こっから個別部分///////////////////////////////////////////////
		//tabペイン作成
		JTabbedPane tabbedpane = new JTabbedPane();

		//////////////////////////////GGTab部分作成////////////////////////////////////////////////
		JPanel ggPanel = new JPanel();
		ggPanel.setLayout(new BorderLayout());
		JPanel ggEditPanel = new JPanel();
		ggEditPanel.setLayout(null);                            //pixel直打ちさせる
		ggEditPanel.setPreferredSize(new Dimension(200, 0));        //幅200で固定

		//RemoveButton
		ggEditRemove = new JButton("Remove");
		ggEditRemove.addActionListener(e -> ggEditRemoveButtonPressed());
		ggEditRemove.setBounds(105, 5, 90, 30);
		ggEditRemove.setVisible(false);
		ggEditPanel.add(ggEditRemove);
		//AddButton
		ggEditAdd = new JButton("Add");
		ggEditAdd.addActionListener(e -> ggEditAddButtonPressed());
		ggEditAdd.setBounds(105, 400, 90, 30);
		ggEditPanel.add(ggEditAdd);
		//EditButton
		ggEditEdit = new JButton("Edit");
		ggEditEdit.addActionListener(e -> ggEditEditButtonPressed());
		ggEditEdit.setBounds(105, 400, 90, 30);
		ggEditEdit.setVisible(false);
		ggEditPanel.add(ggEditEdit);

		//NameTextArea周り
		ggEditNameArea = new JTextArea(5, 15);
		JScrollPane scroll = new JScrollPane(ggEditNameArea);
		JPanel ggEditNameFieldBorder = new JPanel();
		ggEditNameFieldBorder.add(scroll);
		ggEditNameFieldBorder.setBorder(new TitledBorder(new EtchedBorder(), "Name"));
		ggEditNameFieldBorder.setBounds(5, 50, 193, 120);
		ggEditPanel.add(ggEditNameFieldBorder);

		//parent指定ComboBox周り
		ggEditParentComboBox = new JComboBox();
		ggEditParentComboBox.setPreferredSize(new Dimension(160, 20));
		JPanel ggEditParentComboBoxBorder = new JPanel();
		ggEditParentComboBoxBorder.add(ggEditParentComboBox);
		ggEditParentComboBoxBorder.setBorder(new TitledBorder(new EtchedBorder(), "Parent"));
		ggEditParentComboBoxBorder.setBounds(5, 180, 193, 60);
		ggEditPanel.add(ggEditParentComboBoxBorder);
		ggEditParentComboBoxIdList = new ArrayList();

		//refineType周り
		ggEditRefineTypeAnd = new JRadioButton("AND");
		ggEditRefineTypeAnd.setSelected(true);
		ggEditRefineTypeOr = new JRadioButton("OR");
		ggEditRefineTypeLeaf = new JRadioButton("LEAF");
		//refineTypeButtonGroup作成
		ButtonGroup ggEditRefineTypeButtonGroup = new ButtonGroup();
		ggEditRefineTypeButtonGroup.add(ggEditRefineTypeAnd);
		ggEditRefineTypeButtonGroup.add(ggEditRefineTypeOr);
		ggEditRefineTypeButtonGroup.add(ggEditRefineTypeLeaf);
		//RefineTyoeグループのラベル（パネル）作成
		ggEditRefineType = new JPanel();
		ggEditRefineType.add(ggEditRefineTypeAnd);
		ggEditRefineType.add(ggEditRefineTypeOr);
		ggEditRefineType.add(ggEditRefineTypeLeaf);
		ggEditRefineType.setBorder(new TitledBorder(new EtchedBorder(), "RefineType"));
		ggEditRefineType.setBounds(5, 250, 193, 60);
		ggEditRefineType.setVisible(false);
		ggEditPanel.add(ggEditRefineType);

		//Necessity周り
		ggEditNecessityIsEnable = new JRadioButton("Enable");
		ggEditNecessityIsEnable.setSelected(true);
		ggEditNecessityIsDisable = new JRadioButton("Disable");
		//NecessityButtonGroup作成
		ButtonGroup ggEditNecessityButtonGroup = new ButtonGroup();
		ggEditNecessityButtonGroup.add(ggEditNecessityIsEnable);
		ggEditNecessityButtonGroup.add(ggEditNecessityIsDisable);
		//Necessityグループのラベル（パネル）作成
		ggEditNecessity = new JPanel();
		ggEditNecessity.add(ggEditNecessityIsEnable);
		ggEditNecessity.add(ggEditNecessityIsDisable);
		ggEditNecessity.setBorder(new TitledBorder(new EtchedBorder(), "Necessity"));
		ggEditNecessity.setBounds(5, 320, 193, 60);
		ggEditNecessity.setVisible(false);
		ggEditPanel.add(ggEditNecessity);

		//ggEditエディタパネル追加
		ggPanel.add(ggEditPanel, BorderLayout.LINE_END);
		//ggEditのProcessing周り
		ggGraph = new GGGraph(this);
		ggGraph.init();
		ggPanel.add(ggGraph, BorderLayout.CENTER);

		//////////////////////////////UCTab部分作成//////////////////////////////
		JPanel ucPanel = new JPanel();
		ucPanel.setLayout(new BorderLayout());
		JPanel ucEditPanel = new JPanel();
		ucEditPanel.setLayout(null);
		ggEditPanel.setPreferredSize(new Dimension(200, 0));
		//TODO:ここに各種コンポーネント追加メソッドを記入


		ucPanel.add(ucEditPanel, BorderLayout.LINE_END);
		ucGraph = new UCGraph(this);
		ucGraph.init();
		ucPanel.add(ucGraph, BorderLayout.CENTER);

		//////////////////////////////PFTab部分作成//////////////////////////////
		JPanel pfPanel = new JPanel();
		pfPanel.setLayout(new BorderLayout());
		JPanel pfEditPanel = new JPanel();
		pfEditPanel.setLayout(null);
		pfEditPanel.setPreferredSize(new Dimension(200, 0));

		//RemoveButton
		pfEditRemove = new JButton("Remove");
		pfEditRemove.addActionListener(e -> pfEditRemoveButtonPressed());
		pfEditRemove.setBounds(105, 5, 90, 30);
		pfEditRemove.setVisible(false);
		pfEditPanel.add(pfEditRemove);
		//AddButton
		pfEditAdd = new JButton("Add");
		pfEditAdd.addActionListener(e -> pfEditAddButtonPressed());
		pfEditAdd.setBounds(105, 400, 90, 30);
		pfEditPanel.add(pfEditAdd);
		//EditButton
		pfEditEdit = new JButton("Edit");
		pfEditEdit.addActionListener(e -> pfEditEditButtonPressed());
		pfEditEdit.setBounds(105, 400, 90, 30);
		pfEditEdit.setVisible(false);
		pfEditPanel.add(pfEditEdit);

		//NameTextArea周り
		pfEditNameArea = new JTextArea(5, 15);
		JScrollPane pfScroll = new JScrollPane(pfEditNameArea);
		JPanel pfEditNameFieldBorder = new JPanel();
		pfEditNameFieldBorder.add(pfScroll);
		pfEditNameFieldBorder.setBorder(new TitledBorder(new EtchedBorder(), "Name"));
		pfEditNameFieldBorder.setBounds(5, 50, 193, 120);
		pfEditPanel.add(pfEditNameFieldBorder);

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
		pfEditPanel.add(pfEditDomainType);

		pfPanel.add(pfEditPanel, BorderLayout.LINE_END);
		pfGraph = new PFGraph(this);
		pfGraph.init();
		pfPanel.add(pfGraph, BorderLayout.CENTER);

		/////////////////////////////仕上げ///////////////////////////////////////
		//TabbedPaneに挿入
		tabbedpane.addTab("GG", ggPanel);
		tabbedpane.addTab("UC", ucPanel);
		tabbedpane.addTab("PF", pfPanel);
		//中身のつまったパネルを追加
		getContentPane().add(tabbedpane, BorderLayout.CENTER);
		getContentPane().add(sharedEndPanel, BorderLayout.PAGE_END);

		redraw();
	}

	private void diffBrowseButtonPressed() {
		//TODO:DiffBrowser作成（Priorityは低め）
	}

	private void viewmodeReducedRadioButtonPressed() {
		viewmode = VIEWMODE.REDUCED;
		redraw();
	}

	private void viewmodeAllRadioButtonPressed() {
		viewmode = VIEWMODE.ALL;
		redraw();
	}

	private void versionAsIsRadioButtonPressed() {
		version = VERSION.ASIS;
		redraw();
	}

	private void versionToBeRadioButtonPressed() {
		version = VERSION.TOBE;
		redraw();
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
		fgm.addDomain(name, Domain.DomainType.NONE, pfGraph.width / 2, pfGraph.height / 2);
		redraw();
	}

	private void pfEditEditButtonPressed() {
		//各種コンポーネントからパラメータ取得
		String name = pfEditNameArea.getText();
		int id = pfGraph.selectedDomainId;
		Domain.DomainType dt = pfEditDomainTypeBiddable.isSelected() ?
				Domain.DomainType.BIDDABLE : pfEditDomainTypeCausal.isSelected() ?
				Domain.DomainType.CAUSAL : pfEditDomainTypeDesigned.isSelected() ?
				Domain.DomainType.DESIGNED : pfEditDomainTypeLexical.isSelected() ?
				Domain.DomainType.LEXICAL : pfEditDomainTypeSystem.isSelected() ?
				Domain.DomainType.SYSTEM : Domain.DomainType.NONE;

		//fgm編集
		if (!fgm.editDomain(id, name, dt)) {
			JOptionPane.showMessageDialog(this, "モデルを編集できませんでした", "Error", JOptionPane.ERROR_MESSAGE);
		}

		redraw();
	}

	private void pfEditRemoveButtonPressed() {
		//選択中のドメインを削除
		fgm.removeDomain(pfGraph.selectedDomainId);

		//GoalIdを外す
		pfGraph.selectedDomainId = -1;

		redraw();
	}

	private void ggEditEditButtonPressed() {
		//各種コンポーネントからパラメータ取得
		String name = ggEditNameArea.getText();
		Goal prevGoal = fgm.getGoalById(ggGraph.selectedGoalId);
		Goal.ChildrenType ct = ggEditRefineTypeAnd.isSelected() ? Goal.ChildrenType.AND : ggEditRefineTypeOr.isSelected() ? Goal.ChildrenType.OR : Goal.ChildrenType.LEAF;
		int parentId = ggEditParentComboBoxIdList.get(ggEditParentComboBox.getSelectedIndex());

		//fgm編集
		if (!fgm.editGoal(prevGoal.id, name, ct, parentId)) {
			JOptionPane.showMessageDialog(this, "モデルを編集できませんでした", "Error", JOptionPane.ERROR_MESSAGE);
		}

		redraw();
	}

	private void ggEditAddButtonPressed() {
		//親ゴールID取得
		int parentGoalId = ggEditParentComboBoxIdList.get(ggEditParentComboBox.getSelectedIndex());

		//名前取得
		String name = ggEditNameArea.getText();

		//名前欄にちゃんと中身があるか
		if (name.equals("")) {
			JOptionPane.showMessageDialog(this, "なまえをいれてください", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		} else
			ggEditNameArea.setText("");

		//追加
		fgm.addGoal(name, parentGoalId, ggGraph.width / 2, ggGraph.height / 2);

		redraw();
	}

	private void ggEditRemoveButtonPressed() {
		//選択中のゴールを削除
		fgm.removeGoal(ggGraph.selectedGoalId);

		//GoalIdを外す
		ggGraph.selectedGoalId = -1;

		redraw();
	}

	/**
	 * 各種コンポーネントをまとめて更新
	 */
	public void redraw() {
		//ProcessingをRedraw
		ggGraph.redraw();
		pfGraph.redraw();
		ucGraph.redraw();

		//GGEdit:ComboBox更新
		ggEditParentComboBoxIdList.clear();
		ggEditParentComboBox.removeAllItems();
		ggEditParentComboBox.addItem("TOP");
		ggEditParentComboBoxIdList.add(-1);
		for (Goal g : fgm.getGoals()) {
			if (g.id != ggGraph.selectedGoalId) {
				//ComboBox詰め替え
				ggEditParentComboBox.addItem(g.name);
				ggEditParentComboBoxIdList.add(g.id);
			}
		}

		//PFEdit:PFGraph.selectedDomainIdに応じたエディタ画面に更新
		if (pfGraph.selectedDomainId != -1) {
			//選択中のドメインを取得
			Domain selectedDomain = fgm.getDomainById(pfGraph.selectedDomainId);

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

		//GGEdit:GGGraph.selectedGoalIdに応じたエディタ画面に更新
		if (ggGraph.selectedGoalId != -1) {

			//選択中のゴールを取得
			Goal selectedGoal = fgm.getGoalById(ggGraph.selectedGoalId);

			//Text更新
			ggEditNameArea.setText(selectedGoal.name);

			//RefineType更新
			if (selectedGoal.childrenType == Goal.ChildrenType.AND) {
				ggEditRefineTypeAnd.setSelected(true);
			} else if (selectedGoal.childrenType == Goal.ChildrenType.OR) {
				ggEditRefineTypeOr.setSelected(true);
			} else if (selectedGoal.childrenType == Goal.ChildrenType.LEAF) {
				ggEditRefineTypeLeaf.setSelected(true);
			}

			//ComboBox選択
			for (int id : ggEditParentComboBoxIdList) {
				if (selectedGoal.parentId == id) {
					ggEditParentComboBox.setSelectedIndex(ggEditParentComboBoxIdList.indexOf(id));
				}
			}

			//TODO:Necessity更新
		}

		//GGEditor各種コンポーネント:表示・非表示切り替え
		ggEditAdd.setVisible(ggGraph.selectedGoalId == -1);
		ggEditRemove.setVisible(ggGraph.selectedGoalId != -1);
		ggEditEdit.setVisible(ggGraph.selectedGoalId != -1);
		ggEditNecessity.setVisible(ggGraph.selectedGoalId != -1);
		ggEditRefineType.setVisible(ggGraph.selectedGoalId != -1);

		//PFEditor各種コンポーネント：表示・非表示切り替え
		pfEditAdd.setVisible(pfGraph.selectedDomainId == -1);
		pfEditRemove.setVisible(pfGraph.selectedDomainId != -1);
		pfEditEdit.setVisible(pfGraph.selectedDomainId != -1);
		pfEditDomainType.setVisible(pfGraph.selectedDomainId != -1);
	}

	public static void main(String[] args) {
		SToolEditor ste = new SToolEditor();
		ste.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		ste.setBounds(10, 10, 800, 600);
		ste.setTitle("SToolEditor");
		ste.setVisible(true);
	}
}
