import Models.Goal;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Created by 66 on 2015/10/03.
 */
public class SToolEditor extends JFrame {

	//融合ゴールモデル
	FGModel fgm;

	//バージョン指定
	VERSION version;
	enum VERSION{
		ASIS,TOBE;
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
	//ビュー指定
	VIEWMODE viewmode;
	enum VIEWMODE{
		ALL,REDUCED;
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

	//GGEditorコンポーネント群(一応Privateな)
	private JButton ggEditAdd,ggEditEdit,ggEditRemove;
	private JRadioButton ggEditRefineTypeAnd, ggEditRefineTypeOr, ggEditNecessityIsEnable, ggEditNecessityIsDisable;
	private JTextField ggEditNameField;
	private JPanel ggEditRefineType,ggEditNecessity;
	private JComboBox ggEditParentComboBox;
	private List<Integer> ggEditParentComboBoxIdList;


	//PApplet部分(こいつらも一応Privateな)
	private GGGraph ggGraph;
	private PFGraph pfGraph;
	private UCGraph ucGraph;

	/**
	 * コンストラクタがほぼほぼView要素を頑張って書くスタイル
	 */
	SToolEditor(){
		//FGModel
		fgm = new FGModel();
		//FGModelテスト
		fgm.addGoal("testgoal1_root",-1, Goal.ChildrenType.AND,100,100);

		//////////////////////////////下部分共通パネル//////////////////////////////
		JPanel sharedEndPanel = new JPanel();
		sharedEndPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		//version_RadioButton作成
		JRadioButton asIsVer=new JRadioButton("As-Is");
		asIsVer.setSelected(true);
		version=VERSION.ASIS;
		asIsVer.addActionListener(e -> versionAsIsRadioButtonPressed());
		JRadioButton toBeVer=new JRadioButton("To-Be");
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
		JRadioButton viewAll =new JRadioButton("All");
		viewAll.addActionListener(e -> viewmodeAllRadioButtonPressed());
		viewAll.setSelected(true);
		viewmode=VIEWMODE.ALL;
		JRadioButton viewReduced =new JRadioButton("Reduced");
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
		ggEditPanel.setLayout(null);							//pixel直打ちさせる
		ggEditPanel.setPreferredSize(new Dimension(200,0));		//幅200で固定

		//RemoveButton
		ggEditRemove = new JButton("Remove");
		ggEditRemove.addActionListener(e -> ggEditRemoveButtonPressed()	);
		ggEditRemove.setBounds(105,5,90,30);
		ggEditRemove.setVisible(false);
		ggEditPanel.add(ggEditRemove);
		//AddButton
		ggEditAdd = new JButton("Add");
		ggEditAdd.addActionListener(e -> ggEditAddButtonPressed());
		ggEditAdd.setBounds(105,400,90,30);
		ggEditPanel.add(ggEditAdd);
		//EditButton
		ggEditEdit = new JButton("Edit");
		ggEditEdit.addActionListener(e -> ggEditEditButtonPressed()	);
		ggEditEdit.setBounds(105,400,90,30);
		ggEditEdit.setVisible(false);
		ggEditPanel.add(ggEditEdit);

		//NameTextArea周り
		ggEditNameField=new JTextField(14);
		JPanel ggEditNameFieldBorder = new JPanel();
		ggEditNameFieldBorder.add(ggEditNameField);
		ggEditNameFieldBorder.setBorder(new TitledBorder(new EtchedBorder(), "Name"));
		ggEditNameFieldBorder.setBounds(5, 50, 193, 60);
		ggEditPanel.add(ggEditNameFieldBorder);

		//parent指定ComboBox周り
		ggEditParentComboBox = new JComboBox();
		ggEditParentComboBox.setPreferredSize(new Dimension(160, 20));
		JPanel ggEditParentComboBoxBorder = new JPanel();
		ggEditParentComboBoxBorder.add(ggEditParentComboBox);
		ggEditParentComboBoxBorder.setBorder(new TitledBorder(new EtchedBorder(),"Parent"));
		ggEditParentComboBoxBorder.setBounds(5,120,193,60);
		ggEditPanel.add(ggEditParentComboBoxBorder);
		ggEditParentComboBoxIdList = new ArrayList();

		//refineType周り
		ggEditRefineTypeAnd =new JRadioButton("AND");
		ggEditRefineTypeAnd.setSelected(true);
		ggEditRefineTypeOr =new JRadioButton("OR");
		//refineTypeButtonGroup作成
		ButtonGroup ggEditRefineTypeButtonGroup = new ButtonGroup();
		ggEditRefineTypeButtonGroup.add(ggEditRefineTypeAnd);
		ggEditRefineTypeButtonGroup.add(ggEditRefineTypeOr);
		//viewmode_グループのラベル（パネル）作成
		ggEditRefineType = new JPanel();
		ggEditRefineType.add(ggEditRefineTypeAnd);
		ggEditRefineType.add(ggEditRefineTypeOr);
		ggEditRefineType.setBorder(new TitledBorder(new EtchedBorder(), "RefineType"));
		ggEditRefineType.setBounds(5,220,193,60);
		ggEditRefineType.setVisible(false);
		ggEditPanel.add(ggEditRefineType);

		//enable<>disable周り
		ggEditNecessityIsEnable =new JRadioButton("Enable");
		ggEditNecessityIsEnable.setSelected(true);
		ggEditNecessityIsDisable =new JRadioButton("Disable");
		//refineTypeButtonGroup作成
		ButtonGroup ggEditNecessityButtonGroup = new ButtonGroup();
		ggEditNecessityButtonGroup.add(ggEditNecessityIsEnable);
		ggEditNecessityButtonGroup.add(ggEditNecessityIsDisable);
		//Necessityグループのラベル（パネル）作成
		ggEditNecessity = new JPanel();
		ggEditNecessity.add(ggEditNecessityIsEnable);
		ggEditNecessity.add(ggEditNecessityIsDisable);
		ggEditNecessity.setBorder(new TitledBorder(new EtchedBorder(), "Necessity"));
		ggEditNecessity.setBounds(5, 290, 193,60);
		ggEditNecessity.setVisible(false);
		ggEditPanel.add(ggEditNecessity);

		//ggEditエディタパネル追加
		ggPanel.add(ggEditPanel,BorderLayout.LINE_END);
		//ggEditのProcessing周り
		ggGraph = new GGGraph(this);
		ggGraph.init();
		ggPanel.add(ggGraph,BorderLayout.CENTER);

		//////////////////////////////UCTab部分作成//////////////////////////////
		JPanel ucPanel = new JPanel();
		ucPanel.setLayout(new BorderLayout());
		JPanel uceditPanel= new JPanel();
		uceditPanel.setLayout(null);
		ggEditPanel.setPreferredSize(new Dimension(200,0));
		//TODO:ここに各種コンポーネント追加メソッドを記入
		ucPanel.add(uceditPanel,BorderLayout.LINE_END);
		ucGraph = new UCGraph();
		ucGraph.init();
		ucPanel.add(ucGraph,BorderLayout.CENTER);

		//////////////////////////////PFTab部分作成//////////////////////////////
		JPanel pfPanel = new JPanel();
		pfPanel.setLayout(new BorderLayout());
		JPanel pfeditPanel= new JPanel();
		pfeditPanel.setLayout(null);
		ggEditPanel.setPreferredSize(new Dimension(200,0));
		//TODO:ここに各種コンポーネント追加メソッドを記入
		pfPanel.add(pfeditPanel,BorderLayout.LINE_END);
		pfGraph = new PFGraph(this);
		pfGraph.init();
		pfPanel.add(pfGraph,BorderLayout.CENTER);


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
		viewmode =VIEWMODE.REDUCED;
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

	private void ggEditEditButtonPressed() {
		//Goal名前取得
		String name = ggEditNameField.getText();
		//変更中のGoalID取得
		Goal prevGoal = fgm.getGoalById(ggGraph.selectedGoalId);
		//ChildrenType取得
		Goal.ChildrenType ct = ggEditRefineTypeAnd.isSelected() ? Goal.ChildrenType.AND : Goal.ChildrenType.OR;
		//parentId取得
		int parentId = ggEditParentComboBoxIdList.get(ggEditParentComboBox.getSelectedIndex());

		//fgm編集（x,yはそのまま）
		fgm.editGoal(prevGoal.id, name, ct,parentId);

		redraw();
	}

	private void ggEditAddButtonPressed() {
		//TODO:きたない

		String name = ggEditNameField.getText();

		if(name.equals(""))
			return;
		else
			ggEditNameField.setText("");

		int parentGoalId = ggEditParentComboBoxIdList.get(ggEditParentComboBox.getSelectedIndex());

		if(ggEditRefineTypeAnd.isSelected())
			fgm.addGoal(name, parentGoalId, Goal.ChildrenType.AND, ggGraph.width/2, ggGraph.height/2);
		else
			fgm.addGoal(name, parentGoalId, Goal.ChildrenType.OR, ggGraph.width/2, ggGraph.height/2);

		redraw();
	}

	private void ggEditRemoveButtonPressed() {
		//TODO:FGModel諸々に変更をかける処理（Removeボタン押下）
	}

	/**
	 * 各種コンポーネントをまとめて更新
	 */
	public void redraw() {
		//ProcessingをRedraw
		ggGraph.redraw();
		pfGraph.redraw();
		ucGraph.redraw();

		//ComboBox(goalGraph)をRedraw
		ggEditParentComboBoxIdList.clear();
		ggEditParentComboBox.removeAllItems();
		for(Goal g : fgm.goals){
			ggEditParentComboBox.addItem(g.name);
			ggEditParentComboBoxIdList.add(g.id);
		}

		//gggraph.selectedGoalIdに応じたエディタ画面に更新
		if(ggGraph.selectedGoalId != -1){
			ggEditAdd.setVisible(false);
			ggEditRemove.setVisible(true);
			ggEditEdit.setVisible(true);
			ggEditNecessity.setVisible(true);
			ggEditRefineType.setVisible(true);
			Goal g = fgm.getGoalById(ggGraph.selectedGoalId);
			ggEditNameField.setText(g.name);
			if(g.childrenType == Goal.ChildrenType.AND){
				ggEditRefineTypeAnd.setSelected(true);
			}else{
				ggEditRefineTypeAnd.setSelected(false);
			}
			//TODO:このへんもうちょっといろいろ更新いるはず
		}else{
			ggEditAdd.setVisible(true);
			ggEditRemove.setVisible(false);
			ggEditEdit.setVisible(false);
			ggEditNecessity.setVisible(false);
			ggEditRefineType.setVisible(false);
		}
	}

	public static void main(String[] args){
		SToolEditor ste = new SToolEditor();
		ste.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		ste.setBounds(10, 10, 800, 600);
		ste.setTitle("SToolEditor");
		ste.setVisible(true);
	}
}
