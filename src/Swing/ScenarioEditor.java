package Swing;

import Models.FGModelAdapter;
import Models.Scenario;
import Processing.PScenarioEditor;

import javax.swing.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;


public class ScenarioEditor extends JFrame implements ComponentListener {
	private PScenarioEditor pScenarioEditor;

	public ScenarioEditor(FGModelAdapter fgm, Scenario scenario) {

		//Processing初期化＆追加
		pScenarioEditor = new PScenarioEditor(fgm, scenario);
		pScenarioEditor.init();
		this.add(pScenarioEditor);

		//各種設定してVisible
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setBounds(10, 10, 1024, 768);
		this.setTitle("ScenarioEditor");
		this.setVisible(true);

		this.addComponentListener(this);

		redraw();
	}

	public void redraw() {
		pScenarioEditor.redraw();
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
