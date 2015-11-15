package Processing.Component;

import processing.core.PApplet;
import processing.core.PConstants;

public class ListBoxForMetrics extends ListBox {

	public ListBoxForMetrics(int COLOR_BACKGROUND, int COLOR_LINES, int COLOR_SELECTED) {
		super(COLOR_BACKGROUND, COLOR_LINES, COLOR_SELECTED);
	}

	@Override
	public void draw(PApplet pApplet) {
		pApplet.textAlign(PConstants.LEFT, PConstants.CENTER);
		pApplet.fill(COLOR_LINES);
		pApplet.noFill();
		pApplet.stroke(super.COLOR_LINES);

		for (int i = 0, j = scrollIndex; j < contents.size() && i * dh < h; i++, j++) {
			pApplet.stroke(COLOR_LINES);
			pApplet.noFill();

			//pApplet.strokeWeight(PUtility.mouseIsInRect(x + 2, y + 2 + i * dh, w - 4, dh - 4, pApplet.mouseX, pApplet.mouseY) ? (float) 1.5 : 1);
			pApplet.rect(x + 2, y + 2 + i * dh, w - 4, dh - 4);
			//pApplet.strokeWeight(1);

			pApplet.fill(COLOR_LINES);
			pApplet.noStroke();
			pApplet.textAlign(PConstants.LEFT, PConstants.CENTER);
			pApplet.text(contents.get(j).name, x + 7, y + i * dh, w - 7, dh);

			pApplet.textAlign(PConstants.RIGHT, PConstants.CENTER);
			pApplet.text(String.valueOf(contents.get(j).id), x + 7, y + i * dh, w - 14, dh);

		}

		//はみ出し部分を塗りつぶし
		pApplet.fill(COLOR_BACKGROUND);
		pApplet.stroke(COLOR_BACKGROUND);
		pApplet.rect(x - 2, y + h, w + 4, dh);

		//枠線
		pApplet.stroke(COLOR_LINES);
		pApplet.noFill();
		pApplet.rect(x, y, w, h);
	}
}
