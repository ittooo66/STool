package Processing;

import processing.core.PApplet;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 66 on 2015/10/11.
 */
public class UCGraph extends PApplet {
	List<Integer> mouseX_temp = new ArrayList();
	List<Integer> mouseY_temp = new ArrayList();


	public void draw() {

		background(0);
		stroke(200, 200, 200);

		text("W:" + width + ",H:" + height + " on UC", mouseX, mouseY);

		for (int x : mouseX_temp) {
			line(x, 10, x, height - 10);
		}

		for (int y : mouseY_temp) {
			line(10, y, width - 10, y);
		}

	}

	public void setup() {
		size(1024, 768);
		noLoop();
	}

	public void mousePressed() {
		mouseX_temp.add(mouseX);
		mouseY_temp.add(mouseY);

		redraw();
	}

}
