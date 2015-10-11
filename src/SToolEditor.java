import PApplets.GGGraph;
import PApplets.PFGraph;
import PApplets.UCGraph;

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


	SToolEditor(){
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
		ggGraph = new GGGraph();
		ggGraph.init();
		ggPanel.add(ggGraph,BorderLayout.CENTER);

		//UCTab部分生成
		ucPanel = new JPanel();
		ucPanel.add(new JLabel("Name:"));
		ucPanel.add(new JTextField("", 10));
		//TODO: UC周りをつくる


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




		//下部分共通パネル
		JPanel sharedEndPanel = new JPanel();
		sharedEndPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

		//version_RadioButton作成
		JRadioButton asIs=new JRadioButton("As-Is");
		JRadioButton toBe=new JRadioButton("To-Be");
		//version_ButtonGroup作成
		ButtonGroup versionGroup = new ButtonGroup();
		versionGroup.add(asIs);
		versionGroup.add(toBe);
		//version_グループのラベル（パネル）作成
		JPanel version = new JPanel();
		version.add(asIs);
		version.add(toBe);
		version.setBorder(new TitledBorder(new EtchedBorder(), "version"));
		sharedEndPanel.add(version);

		//viewmode_RadioButton作成
		JRadioButton viewAll =new JRadioButton("All");
		JRadioButton viewReduced =new JRadioButton("Reduced");
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



		//中身のつまったパネルを追加
		getContentPane().add(tabbedpane, BorderLayout.CENTER);
		getContentPane().add(sharedEndPanel, BorderLayout.PAGE_END);
	}

	public static void main(String[] args){
		SToolEditor ste = new SToolEditor();

		ste.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		ste.setBounds(10, 10, 300, 200);
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
