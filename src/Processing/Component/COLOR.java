package Processing.Component;

/**
 * Created by 66 on 2015/11/18.
 * Color定数まとめ
 */
public class COLOR {
	public static final int BACKGROUND = getColor(255, 255, 255);
	public static final int LINES = getColor(51, 51, 51);
	public static final int SELECTED = getColor(57, 152, 214);
	public static final int FILL = getColor(220, 233, 255);

	/**
	 * Processingで使えるColorを取得
	 *
	 * @param v1 R値(0-255)
	 * @param v2 G値(0-255)
	 * @param v3 B値(0-255)
	 * @return COLOR値
	 */
	public static int getColor(int v1, int v2, int v3) {
		if (v1 > 255) {
			v1 = 255;
		} else if (v1 < 0) {
			v1 = 0;
		}

		if (v2 > 255) {
			v2 = 255;
		} else if (v2 < 0) {
			v2 = 0;
		}

		if (v3 > 255) {
			v3 = 255;
		} else if (v3 < 0) {
			v3 = 0;
		}

		return -16777216 | v1 << 16 | v2 << 8 | v3;
	}
}
