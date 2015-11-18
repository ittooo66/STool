package Processing.Component;

import processing.core.PApplet;

import java.util.ArrayList;
import java.util.List;

public class ButtonSetFrame {

	private int x, y, w, h;
	private String title;
	private List<String> buttonList;

	public ButtonSetFrame(String title) {
		this.title = title;
		buttonList = new ArrayList<>();
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void addButton(String buttonLabel) {
		buttonList.add(buttonLabel);
	}

	/**
	 * ProcessingのWindowサイズ変更に対応するように各種値を調整
	 *
	 * @param x Processing上のX座標位置
	 * @param y Processing上のY座標位置
	 * @param w こいつの描画幅
	 * @param h こいつの描画高さ
	 */
	public void adjust(int x, int y, int w, int h) {
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
	}

	/**
	 * こいつをクリック、クリックされたボタン番号を返す。
	 *
	 * @return ボタン番号、クリックがずれてるなら -1
	 */
	public int getButtonIdOnMouse(int mouseX, int mouseY) {
		for (int i = 0; i < buttonList.size(); i++) {
			if (PUtility.mouseIsInEllipse(x + h / 2 + i * h, y + h / 2, h - 2, h - 2, mouseX, mouseY)) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * 描画
	 */
	public void draw(PApplet pApplet) {
		pApplet.fill(COLOR.LINES);
		pApplet.textAlign(pApplet.RIGHT);
		pApplet.text(title, x, y, w, h);
		for (int i = 0; i < buttonList.size(); i++) {
			if (getButtonIdOnMouse(pApplet.mouseX, pApplet.mouseY) == i) {
				pApplet.noStroke();
				pApplet.fill(COLOR.SELECTED);
				pApplet.ellipse(x + i * h + h / 2, y + h / 2, h - 4, h - 4);
				pApplet.fill(COLOR.BACKGROUND);
			} else {
				pApplet.stroke(COLOR.LINES);
				pApplet.noFill();
				pApplet.ellipse(x + i * h + h / 2, y + h / 2, h - 4, h - 4);
				pApplet.fill(COLOR.LINES);
			}
			pApplet.textAlign(pApplet.CENTER, pApplet.CENTER);
			pApplet.noStroke();
			pApplet.text(buttonList.get(i), x + i * h + h / 2, y + h / 2 - 2);
		}
	}

}