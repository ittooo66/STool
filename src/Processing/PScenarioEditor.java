package Processing;

import Models.FGModelAdapter;
import Models.Scenario;
import Processing.Component.COLOR;
import processing.core.PApplet;

public class PScenarioEditor extends PApplet {

	private FGModelAdapter fgm;
	private Scenario scenario;

	public PScenarioEditor(FGModelAdapter fgm, Scenario scenario) {
		this.fgm = fgm;
		this.scenario = scenario;
	}

	public void setup() {

	}

	//変更フラグ
	private boolean hasChanges;

	public void redraw() {
		hasChanges = true;
	}

	public void draw() {
		//省力draw()
		if (!hasChanges) return;
		else hasChanges = false;

		background(COLOR.BACKGROUND);
	}


}
