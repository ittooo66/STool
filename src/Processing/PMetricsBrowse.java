package Processing;

import Models.FGModel;
import processing.core.PApplet;
import processing.core.PFont;

public class PMetricsBrowse extends PApplet {

	public FGModel fgm;

	public PMetricsBrowse(FGModel fgm) {
		this.fgm = fgm;
	}

	public void setup() {
		//Font設定。
		PFont font = createFont("メイリオ ボールド", 15, true);
		textFont(font);
		//Smoothに描画
		smooth();
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

		//TODO:
		background(random(255), random(255), random(255));
		text("W:" + width + ", H:" + height, width / 2, height / 2);
	}

	public void mousePressed() {
		redraw();
	}


}
