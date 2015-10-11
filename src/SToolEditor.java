import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by 66 on 2015/10/03.
 */
public class SToolEditor extends JFrame implements ActionListener{

	//使用しているコンポーネントたちである
	//タブペイン
	JTabbedPane tabbedpane;
	//各種エディタのパネル全体、PAppletとエディタ部分を含む
	JPanel ggPanel,ucPanel,pfPanel;
	//エディット部分
	JPanel ggeditPanel,uceditPanel,pfeditPanel;
	//PApplet部分
	GGGraph ggGraph;
	PFGraph pfGraph;
	UCGraph ucGraph;

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

	SToolEditor(){
		//下部分共通パネル
		JPanel sharedEndPanel = new JPanel();
		sharedEndPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

		//version_RadioButton作成
		JRadioButton asIsVer=new JRadioButton("As-Is");
		asIsVer.setSelected(true);
		version=VERSION.ASIS;
		asIsVer.addActionListener(
				e -> {
					version = VERSION.ASIS;
					redraw_graphs();
				}
		);
		JRadioButton toBeVer=new JRadioButton("To-Be");
		toBeVer.addActionListener(
				e -> {
					version = VERSION.TOBE;
					redraw_graphs();
				}
		);
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
		viewAll.addActionListener(
				e -> {
					viewmode = VIEWMODE.ALL;
					redraw_graphs();
				}
		);
		viewAll.setSelected(true);
		viewmode=VIEWMODE.ALL;
		JRadioButton viewReduced =new JRadioButton("Reduced");
		viewReduced.addActionListener(
				e -> {
					viewmode =VIEWMODE.REDUCED;
					redraw_graphs();
				}
		);
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
		sharedEndPanel.add(diffBrouseButton);


		//tabペイン
		tabbedpane = new JTabbedPane();

		//GGTab部分生成
		ggPanel = new JPanel();
		ggPanel.setLayout(new BorderLayout());
		ggeditPanel = new JPanel();
		ggeditPanel.setLayout(new FlowLayout());
		//TODO: ここに各種コンポーネント追加メソッドを記入
/*これは後で消します*/ggeditPanel.add(new JButton("buttonGG"));
		ggPanel.add(ggeditPanel,BorderLayout.LINE_END);
		ggGraph = new GGGraph(this);
		ggGraph.init();
		ggPanel.add(ggGraph,BorderLayout.CENTER);

		//UCTab部分生成
		ucPanel = new JPanel();
		ucPanel.setLayout(new BorderLayout());
		uceditPanel= new JPanel();
		uceditPanel.setLayout(new FlowLayout());
		//TODO:ここに各種コンポーネント追加メソッドを記入
/*これは後で消します*/uceditPanel.add(new JButton("buttonUC"));
		ucPanel.add(uceditPanel,BorderLayout.LINE_END);
		ucGraph = new UCGraph();
		ucGraph.init();
		ucPanel.add(ucGraph,BorderLayout.CENTER);

		//PFTab部分生成
		pfPanel = new JPanel();
		pfPanel.setLayout(new BorderLayout());
		pfeditPanel= new JPanel();
		pfeditPanel.setLayout(new FlowLayout());
		//TODO:ここに各種コンポーネント追加メソッドを記入
/*これは後で消します*/pfeditPanel.add(new JButton("buttonPF"));
		pfPanel.add(pfeditPanel,BorderLayout.LINE_END);
		pfGraph = new PFGraph();
		pfGraph.init();
		pfPanel.add(pfGraph,BorderLayout.CENTER);

		//TabbedPaneに挿入
		tabbedpane.addTab("GG", ggPanel);
		tabbedpane.addTab("UC", ucPanel);
		tabbedpane.addTab("PF", pfPanel);

		//中身のつまったパネルを追加
		getContentPane().add(tabbedpane, BorderLayout.CENTER);
		getContentPane().add(sharedEndPanel, BorderLayout.PAGE_END);
	}

	private void redraw_graphs() {
		ggGraph.redraw();
		pfGraph.redraw();
		ucGraph.redraw();
	}

	public static void main(String[] args){
		SToolEditor ste = new SToolEditor();
		ste.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		ste.setBounds(10, 10, 800, 600);
		ste.setTitle("SToolEditor");
		ste.setVisible(true);
	}

	public void actionPerformed(ActionEvent e){

	}

	/**
	 * Processing側の変更があった時に呼ぶこととする。
	 */
	public void reload() {

	}
}
