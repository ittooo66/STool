package Swing;

import Models.FGModelAdapter;
import Processing.PMetricsBrowse;
import Processing.PUsecaseBrowse;

import javax.swing.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

public class UsecaseDiffBrowser extends JFrame implements ComponentListener {

	private PUsecaseBrowse pUsecaseBrowse;

	public UsecaseDiffBrowser(FGModelAdapter fgm) {

		//Processing初期化＆追加
		pUsecaseBrowse = new PUsecaseBrowse(fgm);
		pUsecaseBrowse.init();
		this.add(pUsecaseBrowse);

		//各種設定してVisible
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setBounds(10, 10, 600, 600);
		this.setTitle("UsecaseDiffBrowser");
		this.setVisible(true);

		this.addComponentListener(this);

		redraw();
	}

	public void redraw() {
		pUsecaseBrowse.redraw();
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
