import PApplets.GGGraph;
import PApplets.PFGraph;
import PApplets.UCGraph;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by 66 on 2015/10/03.
 */
public class SToolEditor extends JFrame implements ActionListener{

	//使用しているコンポーネント

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


		JButton selectButton = new JButton("select");
		selectButton.addActionListener(this);

		JButton button3 = new JButton("Button3");
		JButton button4 = new JButton("Button4");

		//FlowLayout系テスト
		JPanel p2 = new JPanel();
		p2.setLayout(new FlowLayout(FlowLayout.LEFT));
		p2.add(button3);
		p2.add(button4);

		getContentPane().add(tabbedpane, BorderLayout.CENTER);
		getContentPane().add(p2, BorderLayout.PAGE_END);

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
