package Processing.Component;

import processing.core.PApplet;
import processing.core.PConstants;

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

	public static boolean mouseIsInRect(float x, float y, float w, float h, int mouseX, int mouseY) {
		return mouseIsInRect((int) x, (int) y, (int) w, (int) h, mouseX, mouseY);
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
		float a = w / 2;
		float b = h / 2;
		float x0 = mouseX - x;
		float y0 = mouseY - y;
		float D = (x0 * x0) / (a * a) + (y0 * y0) / (b * b) - 1;
		return D < 0;
	}

	public static boolean mouseIsInEllipse(float x, float y, float w, float h, int mouseX, int mouseY) {
		return mouseIsInEllipse((int) x, (int) y, (int) w, (int) h, mouseX, mouseY);
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


	/**
	 * 修正版Arc関数、楕円状のArcに対応
	 *
	 * @param pApplet 記述対象のPApplet
	 * @param x       （だ）円中心のX座標
	 * @param y       （だ）円中心のY座標
	 * @param w       （だ）円の幅
	 * @param h       （だ）円の高さ
	 * @param begin   開始角度（ラジアン）
	 * @param end     終了角度（ラジアン）
	 */
	public static void myArc(PApplet pApplet, float x, float y, float w, float h, float begin, float end) {
		float fai_end;
		float asin_end = PApplet.asin(PApplet.sqrt(1 - (1 / (1 + (w * w * PApplet.tan(end) * PApplet.tan(end)) / (h * h)))));
		if (0 < end && end <= PConstants.PI / 2) fai_end = asin_end;
		else if (PConstants.PI / 2 < end && end <= PConstants.PI) fai_end = PConstants.PI - asin_end;
		else if (PConstants.PI < end && end <= 3 * PConstants.PI / 2) fai_end = PConstants.PI + asin_end;
		else fai_end = 2 * PConstants.PI - asin_end;

		float fai_begin;
		float asin_begin = PApplet.asin(PApplet.sqrt(1 - (1 / (1 + (w * w * PApplet.tan(begin) * PApplet.tan(begin)) / (h * h)))));
		if (0 < begin && begin <= PConstants.PI / 2) fai_begin = asin_begin;
		else if (PConstants.PI / 2 < begin && begin <= PConstants.PI) fai_begin = PConstants.PI - asin_begin;
		else if (PConstants.PI < begin && begin <= 3 * PConstants.PI / 2) fai_begin = PConstants.PI + asin_begin;
		else fai_begin = 2 * PConstants.PI - asin_begin;

		pApplet.arc(x, y, w, h, fai_begin, fai_end);
	}

	/**
	 * 楕円上のR回転時のX座標を返す
	 *
	 * @param x 楕円中心のX座標
	 * @param y 楕円中心のY座標
	 * @param w 楕円幅
	 * @param h 楕円高さ
	 * @param R 楕円回転量（右から下に向かって0=>2PI）
	 * @return 楕円状のX座標
	 */
	public static int getPointXOnEllipse(int x, int y, int w, int h, float R) {
		int a = w / 2;
		int b = h / 2;
		int pX = (int) PApplet.sqrt((a * a * b * b) / (b * b + a * a * PApplet.tan(R) * PApplet.tan(R)));
		if (0 <= R && R <= PConstants.PI / 2 || 3 * PConstants.PI / 2 < R && R <= 2 * PConstants.PI) return pX + x;
		else return -pX + x;
	}

	/**
	 * 楕円上のR回転時のX座標を返す
	 *
	 * @param x 楕円中心のX座標
	 * @param y 楕円中心のY座標
	 * @param w 楕円幅
	 * @param h 楕円高さ
	 * @param R 楕円回転量（右から下に向かって0=>2PI）
	 * @return 楕円状のY座標
	 */
	public static int getPointYOnEllipse(int x, int y, int w, int h, float R) {
		int a = w / 2;
		int b = h / 2;
		int pY = (int) PApplet.sqrt((a * a * b * b * PApplet.tan(R) * PApplet.tan(R)) / (a * a * PApplet.tan(R) * PApplet.tan(R) + b * b));
		if (PConstants.PI < R) return -pY + y;
		else return pY + y;
	}
}
