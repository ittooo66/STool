package Processing.Component;

import processing.core.PApplet;

public class PUtility {

	/**
	 * マウスが矩形内に存在するかどうかを返す
	 *
	 * @param x      X座標
	 * @param y      Y座標
	 * @param w      矩形幅
	 * @param h      矩形高さ
	 * @param mouseX マウスX座標
	 * @param mouseY マウスY座標
	 * @return 存在するならtrue
	 */
	public static boolean mouseIsInRect(int x, int y, int w, int h, int mouseX, int mouseY) {
		return x < mouseX && mouseX < x + w && y < mouseY && mouseY < y + h;
	}

	/**
	 * マウスが楕円内に存在するかどうかを返す
	 *
	 * @param x      X座標
	 * @param y      Y座標
	 * @param w      楕円幅
	 * @param h      楕円高さ
	 * @param mouseX マウスX座標
	 * @param mouseY マウスY座標
	 * @return 存在するならtrue
	 */
	public static boolean mouseIsInEllipse(int x, int y, int w, int h, int mouseX, int mouseY) {
		//TODO:
		return x < mouseX && mouseX < x + w && y < mouseY && mouseY < y + h;
	}

	/**
	 * ２つの座標のなす方角を返す
	 *
	 * @param rX 始点X座標
	 * @param rY 始点Y座標
	 * @param dX 終点X座標
	 * @param dY 終点Y座標
	 * @return root（始点）から見てdist（終点）がどこの方面（ラジアン）にあるかを返す。
	 * 東：0 (2*PI)
	 * 南：PI/2
	 * 西：PI
	 * 北：3*PI/2
	 * となる。PApplet.arc()記述用のラジアンに対応している
	 */
	public static float getRadian(int rX, int rY, int dX, int dY) {
		//枝の刺さる角度
		float radian = (PApplet.PI / 2) + PApplet.atan((float) (dY - rY) / (float) (dX - rX));
		//第２、３象限のとき、atan()の都合上修正噛ます
		if (rX > dX) radian += PApplet.PI;
		if (radian < PApplet.PI / 2) {
			radian += 3 * PApplet.PI / 2;
		} else {
			radian -= PApplet.PI / 2;
		}
		return radian;
	}

}
