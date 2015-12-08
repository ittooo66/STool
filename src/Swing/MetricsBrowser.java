package Swing;

import Models.FGModelAdapter;
import Models.Scenario;
import Processing.PMetricsBrowse;

import javax.swing.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

public class MetricsBrowser extends JFrame implements ComponentListener {

	private PMetricsBrowse pMetricsBrowse;

	public MetricsBrowser(FGModelAdapter fgm, Scenario scenario) {

		//Processing初期化＆追加
		pMetricsBrowse = new PMetricsBrowse(fgm, scenario);
		pMetricsBrowse.init();
		this.add(pMetricsBrowse);

		//各種設定してVisible
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setBounds(10, 10, 800, 600);
		this.setTitle("MetricsBrowser");
		this.setVisible(true);

		this.addComponentListener(this);

		redraw();
	}

	public void redraw() {
		pMetricsBrowse.redraw();
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
}
