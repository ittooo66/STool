package Processing;

import Models.FGModel;
import processing.core.PApplet;
import processing.core.PFont;

public class MetricsBrowse extends PApplet {

	public FGModel fgm;

	public MetricsBrowse(FGModel fgm) {
		this.fgm = fgm;
	}

	public void setup() {
		//Font設定。
		PFont font = createFont("メイリオ ボールド", 15, true);
		textFont(font);
		//Smoothに描画
		smooth();
	}

	private boolean hasChanges;

	public void redraw() {
		hasChanges = true;
	}

	public void draw() {
		if (!hasChanges) return;
		else hasChanges = false;

		//TODO:
	}

	public void mousePressed() {
		background(random(255), random(255), random(255));
		text("W:" + width + ", H:" + height, width / 2, height / 2);
	}


}
