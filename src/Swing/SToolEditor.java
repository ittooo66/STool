package Swing;

import Models.*;
import Processing.*;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;

/**
 * Created by 66 on 2015/10/03.
 */
public class SToolEditor extends JFrame {

	//融合ゴールモデル
	public FGModel fgm;

	//バージョン指定
	private VERSION version;

	public enum VERSION {
		ASIS, TOBE
	}

	public VERSION getVersion() {
		return version;
	}

	//ビュー指定
	private VIEWMODE viewmode;

	public enum VIEWMODE {
		ALL, REDUCED
	}

	public VIEWMODE getViewmode() {
		return viewmode;
	}

	//EditPanel
	private final GGEditPanel ggEditPanel;
	private final PFEditPanel pfEditPanel;
	private final UCEditPanel ucEditPanel;

	//PApplet部分
	private GGGraph ggGraph;
	private PFGraph pfGraph;
	private UCGraph ucGraph;

	//Tab
	JTabbedPane tabbedpane;

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
		//////////////////////////////GGTab部分作成///////////////////////////////
		JPanel ggPanel = new JPanel(new BorderLayout());
		//ggTabのProcessing周り
		ggGraph = new GGGraph(this);
		ggGraph.init();
		//ggTab右のEditor部分
		ggEditPanel = new GGEditPanel(this, ggGraph);
		//パネルに追加
		ggPanel.add(ggEditPanel, BorderLayout.LINE_END);
		ggPanel.add(ggGraph, BorderLayout.CENTER);
		//////////////////////////////UCTab部分作成//////////////////////////////
		JPanel ucPanel = new JPanel(new BorderLayout());
		//ucEditのProcessingまわり
		ucGraph = new UCGraph(this);
		ucGraph.init();
		//ucEdit右のEditor部分
		ucEditPanel = new UCEditPanel(this, ucGraph);
		//パネルに追加
		ucPanel.add(ucEditPanel, BorderLayout.LINE_END);
		ucPanel.add(ucGraph, BorderLayout.CENTER);
		//////////////////////////////PFTab部分作成//////////////////////////////
		JPanel pfPanel = new JPanel(new BorderLayout());
		//pfEditのProcessingまわり
		pfGraph = new PFGraph(this);
		pfGraph.init();
		//pfEdit右のEditor部分
		pfEditPanel = new PFEditPanel(this, pfGraph);
		//パネルに追加
		pfPanel.add(pfEditPanel, BorderLayout.LINE_END);
		pfPanel.add(pfGraph, BorderLayout.CENTER);
		/////////////////////////////仕上げ///////////////////////////////////////
		//tabペイン作成
		tabbedpane = new JTabbedPane();
		//TabbedPaneに挿入
		tabbedpane.addTab("GG", ggPanel);
		tabbedpane.addTab("UC", ucPanel);
		tabbedpane.addTab("PF", pfPanel);
		//中身のつまったパネルを追加
		getContentPane().add(tabbedpane, BorderLayout.CENTER);
		getContentPane().add(sharedEndPanel, BorderLayout.PAGE_END);

		//諸々を描画
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

	/**
	 * 各種コンポーネントをまとめて更新
	 */
	public void redraw() {
		//ProcessingをRedraw
		ggGraph.redraw();
		pfGraph.redraw();
		ucGraph.redraw();

		//EditPanelをRedraw
		ggEditPanel.redraw();
		pfEditPanel.redraw();
		ucEditPanel.redraw();
	}

	public static void main(String[] args) {
		SToolEditor ste = new SToolEditor();
		ste.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		ste.setBounds(10, 10, 1024, 768);
		ste.setTitle("SToolEditor");
		ste.setVisible(true);
	}

	public void jumpToGGTab(int parentLeafGoalId) {
		tabbedpane.setSelectedIndex(0);
		ggGraph.selectedGoalId = parentLeafGoalId;
		redraw();
	}
}
