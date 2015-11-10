package Swing;

import Models.FGModel;
import Processing.MetricsBrowse;

import javax.swing.*;

public class MetricsBrowser extends JFrame {

	public MetricsBrowser(FGModel fgm) {

		//Processing初期化＆追加
		MetricsBrowse metricsBrowse = new MetricsBrowse(fgm);
		metricsBrowse.init();
		this.add(metricsBrowse);

		//各種設定してVisible
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setBounds(10, 10, 800, 600);
		this.setTitle("MetricsBrowser");
		this.setVisible(true);
	}
}
