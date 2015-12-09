package Swing;

import Models.*;
import Models.FGModelAdapter.VERSION;
import Models.FGModelAdapter.VIEWMODE;
import Processing.*;
import Swing.Component.TitledRadioButtonGroupPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.File;

/**
 * Created by 66 on 2015/10/03.
 * エディタ本体
 */
public class SToolEditor extends JFrame implements ComponentListener {

	//融合ゴールモデル
	public FGModelAdapter fgm;
	//シナリオ
	public Scenario scenario;

	//SToolEditorのComponent
	private TitledRadioButtonGroupPanel versionJRBG, viewmodeJRBG;

	//EditPanel
	private final GGEditPanel ggEditPanel;
	private final PFEditPanel pfEditPanel;
	private final UCEditPanel ucEditPanel;

	//PApplet部分
	private GGGraph ggGraph;
	private PFGraph pfGraph;
	private UCGraph ucGraph;

	//MetricsBrowser,UsecaseDiffBrowser,ScanarioEditor
	private MetricsBrowser metricsBrowser;
	private UsecaseDiffBrowser usecaseDiffBrowser;
	private ScenarioEditor scenarioEditor;

	//Tab
	JTabbedPane tabbedpane;

	/**
	 * コンストラクタがほぼほぼView要素を頑張って書くスタイル
	 */
	SToolEditor() {
		//FGModelAdapter
		fgm = new FGModelAdapter();
		scenario = new Scenario();

		//////////////////////////////下部分共通パネル//////////////////////////////
		JPanel sharedEndPanel = new JPanel();
		sharedEndPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		//version作成
		versionJRBG = new TitledRadioButtonGroupPanel("version");
		versionJRBG.add(new JRadioButton(VERSION.ASIS.toString(), true), e -> {
			fgm.setVersion(VERSION.ASIS);
			deselectAll();
			redraw();
		});
		versionJRBG.add(new JRadioButton(VERSION.TOBE.toString()), e -> {
			fgm.setVersion(VERSION.TOBE);
			deselectAll();
			redraw();
		});
		sharedEndPanel.add(versionJRBG);
		//viewmode作成
		viewmodeJRBG = new TitledRadioButtonGroupPanel("viewmode");
		viewmodeJRBG.add(new JRadioButton(VIEWMODE.ALL.toString(), true), e -> {
			fgm.setViewmode(VIEWMODE.ALL);
			deselectAll();
			redraw();
		});
		viewmodeJRBG.add(new JRadioButton(VIEWMODE.REDUCED.toString()), e -> {
			fgm.setViewmode(VIEWMODE.REDUCED);
			deselectAll();
			redraw();
		});
		sharedEndPanel.add(viewmodeJRBG);
		//差分ブラウザを開く
		JButton diffBrowseButton = new JButton("Usecase Diff Browser");
		diffBrowseButton.addActionListener(e -> usecaseDiffBrowser = new UsecaseDiffBrowser(fgm));
		sharedEndPanel.add(diffBrowseButton);
		//シナリオエディタを開く
		JButton scenarioEditButton = new JButton("Scenario Editor");
		scenarioEditButton.addActionListener(e -> scenarioEditor = new ScenarioEditor(fgm, scenario));
		sharedEndPanel.add(scenarioEditButton);
		//Metricsウィンドウを開く
		JButton metricsBrowseButton = new JButton("Metrics Browser");
		metricsBrowseButton.addActionListener(e -> metricsBrowser = new MetricsBrowser(fgm, scenario));
		sharedEndPanel.add(metricsBrowseButton);
		/////////////////////////////MenuBar作成/////////////////////////////
		JMenuBar jMenuBar = new JMenuBar();
		JMenu fileMenu = new JMenu("File");
		jMenuBar.add(fileMenu);
		JMenuItem fileNewMenuItem = new JMenuItem("New");
		fileNewMenuItem.addActionListener(e -> menuFileNewPressed());
		fileMenu.add(fileNewMenuItem);
		JMenuItem fileOpenMenuItem = new JMenuItem("Open");
		fileOpenMenuItem.addActionListener(e -> menuFileOpenPressed());
		fileMenu.add(fileOpenMenuItem);
		JMenuItem fileSaveAsMenuItem = new JMenuItem("Save As");
		fileSaveAsMenuItem.addActionListener(e -> menuFileSaveAsPressed());
		fileMenu.add(fileSaveAsMenuItem);
		setJMenuBar(jMenuBar);
		//////////////////////////////GGTab部分作成///////////////////////////////
		JPanel ggPanel = new JPanel(new BorderLayout());
		//ggTabのProcessing周り
		ggGraph = new GGGraph(this);
		ggGraph.init();
		//ggTab右のEditor部分
		ggEditPanel = new GGEditPanel(this, ggGraph);
		//パネルに追加
		ggPanel.add(ggEditPanel, BorderLayout.PAGE_START);
		ggPanel.add(ggGraph, BorderLayout.CENTER);
		//////////////////////////////UCTab部分作成//////////////////////////////
		JPanel ucPanel = new JPanel(new BorderLayout());
		//ucEditのProcessingまわり
		ucGraph = new UCGraph(this);
		ucGraph.init();
		//ucEdit右のEditor部分
		ucEditPanel = new UCEditPanel(this, ucGraph);
		//パネルに追加
		ucPanel.add(ucEditPanel, BorderLayout.PAGE_START);
		ucPanel.add(ucGraph, BorderLayout.CENTER);
		//////////////////////////////PFTab部分作成//////////////////////////////
		JPanel pfPanel = new JPanel(new BorderLayout());
		//pfEditのProcessingまわり
		pfGraph = new PFGraph(this);
		pfGraph.init();
		//pfEdit右のEditor部分
		pfEditPanel = new PFEditPanel(this, pfGraph);
		//パネルに追加
		pfPanel.add(pfEditPanel, BorderLayout.PAGE_START);
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

		this.addComponentListener(this);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setBounds(10, 10, 1024, 768);
		this.setTitle("SToolEditor");
		this.setVisible(true);

		//諸々を描画
		redraw();
	}

	private void menuFileOpenPressed() {
		JFileChooser filechooser = new JFileChooser();
		switch (filechooser.showOpenDialog(this)) {
			case JFileChooser.APPROVE_OPTION:
				File file = filechooser.getSelectedFile();
				try {
					fgm.loadXML(file);
				} catch (Exception e) {
					JOptionPane.showMessageDialog(this, "invalid file", "ERROR", JOptionPane.ERROR_MESSAGE);
				}
				break;
			case JFileChooser.ERROR_OPTION:
				JOptionPane.showMessageDialog(this, "WTF", "ERROR", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void menuFileSaveAsPressed() {
		JFileChooser filechooser = new JFileChooser();
		switch (filechooser.showSaveDialog(this)) {
			case JFileChooser.APPROVE_OPTION:
				File file = filechooser.getSelectedFile();
				fgm.saveXML(file);
				break;
			case JFileChooser.ERROR_OPTION:
				JOptionPane.showMessageDialog(this, "WTF", "ERROR", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void menuFileNewPressed() {
		//削除確認
		if (JOptionPane.showConfirmDialog(this,
				"Are you sure you want to create new project ?",
				"Confirm", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) != JOptionPane.YES_OPTION) return;

		fgm = new FGModelAdapter();
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

		//BrowserをRedraw
		if (metricsBrowser != null) metricsBrowser.redraw();
		if (usecaseDiffBrowser != null) usecaseDiffBrowser.redraw();
		if (scenarioEditor != null) scenarioEditor.redraw();

		//ComponentをRedraw
		viewmodeJRBG.setSelected(fgm.getViewmode().toString());
		versionJRBG.setSelected(fgm.getVersion().toString());
	}

	public void initTextArea() {
		ggEditPanel.initTextArea();
		pfEditPanel.initTextArea();
	}

	/**
	 * ProcessingのSelectedを全解除
	 */
	public void deselectAll() {
		ggGraph.selectedGoalId = -1;
		pfGraph.selectedDomainId = -1;
		pfGraph.selectedEventIndex = -1;
		pfGraph.selectedInterfaceIndex = -1;
		ucGraph.selectedUsecaseId = -1;
		ucGraph.selectedStepId = -1;
		ucGraph.selectedFlowIndex = -1;
		ucGraph.selectedFlowType = -1;
	}

	public void jumpToGGTab(int goalId) {
		tabbedpane.setSelectedIndex(0);
		ggGraph.selectedGoalId = goalId;
		redraw();
	}

	public void jumpToUCTab(int usecaseId) {
		tabbedpane.setSelectedIndex(1);
		ucGraph.selectedUsecaseId = usecaseId;
		ucGraph.selectedFlowType = -1;
		ucGraph.selectedFlowIndex = -1;
		ucGraph.selectedStepId = -1;
		redraw();
	}

	public void requestFocusToGGTextArea() {
		ggEditPanel.requestFocusToTextArea();
	}

	@Override
	public void componentResized(ComponentEvent e) {
		redraw();
	}

	@Override
	public void componentMoved(ComponentEvent e) {

	}

	@Override
	public void componentShown(ComponentEvent e) {

	}

	@Override
	public void componentHidden(ComponentEvent e) {

	}

	public static void main(String[] args) {
		new SToolEditor();
	}
}
