package Swing;

import Models.FGModelAdapter;
import Processing.PMetricsBrowse;

import javax.swing.*;

public class MetricsBrowser extends JFrame {

	private PMetricsBrowse pMetricsBrowse;

	public MetricsBrowser(FGModelAdapter fgm) {

		//Processing初期化＆追加
		pMetricsBrowse = new PMetricsBrowse(fgm);
		pMetricsBrowse.init();
		this.add(pMetricsBrowse);

		//各種設定してVisible
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setBounds(10, 10, 300, 600);
		this.setTitle("MetricsBrowser");
		this.setVisible(true);

		redraw();
	}

	public void redraw() {
		pMetricsBrowse.redraw();
	}
}
