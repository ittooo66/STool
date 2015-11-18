package Processing.Component;

import processing.core.PApplet;
import processing.core.PConstants;

import java.util.ArrayList;
import java.util.List;

public class ListBox implements Drawable {

	//dh:１カラムの幅
	protected int x, y, w, h, dh;
	//現在スクロールされている量
	protected int scrollIndex;
	protected int selectedId;
	//コンテンツ
	protected List<ListBoxContent> contents;

	public ListBox() {
		contents = new ArrayList<>();
	}

	/**
	 * コンテンツ更新
	 *
	 * @param contents ListBoxの表示内容
	 */
	public void setContents(List<ListBoxContent> contents) {
		this.contents = contents;
	}

	/**
	 * 特定のマウスカーソル上にあるコンテンツを返す
	 * @param mouseX マウスカーソルX座標
	 * @param mouseY マウスカーソルY座標
	 * @return コンテンツ
	 */
	public ListBoxContent getContentOnMouse(int mouseX, int mouseY) {
		for (int i = 0, j = scrollIndex; j < contents.size(); i++, j++) {
			if (PUtility.mouseIsInRect(x, y + i * dh, w, dh, mouseX, mouseY)) return contents.get(j);
		}
		return null;
	}

	/**
	 * BOX項目をスクロール
	 *
	 * @param e +1:scrollDown,-1:scrollUp,0:reset
	 */
	public void scroll(int e) {
		scrollIndex = (scrollIndex + e > 0) ? (scrollIndex + e < contents.size()) ? scrollIndex + e : scrollIndex : 0;
		if (e == 0) scrollIndex = 0;
	}

	/**
	 * このListBoxが特定のマウスカーソル上にあるか
	 *
	 * @param mouseX マウスカーソルX座標
	 * @param mouseY マウスカーソルY座標
	 * @return あるかどうか
	 */
	public boolean isOn(int mouseX, int mouseY) {
		return PUtility.mouseIsInRect(x, y, w, h, mouseX, mouseY);
	}

	/**
	 * ProcessingのWindowサイズ変更に伴う各種値を調整
	 *
	 * @param x  Processing上のX座標位置
	 * @param y  Processing上のY座標位置
	 * @param w  こいつの描画幅
	 * @param h  こいつの描画高さ
	 * @param dh リスト１項目あたりの高さ
	 */
	public void adjust(int x, int y, int w, int h, int dh, int selectedId) {
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
		this.dh = dh;
		this.selectedId = selectedId;
	}

	public void draw(PApplet pApplet) {
		for (int i = 0, j = scrollIndex; j < contents.size() && i * dh < h; i++, j++) {
			if (selectedId == contents.get(j).id) {
				//選択中のコンテンツのとき
				pApplet.noStroke();
				pApplet.fill(COLOR.SELECTED);
				pApplet.rect(x + 2, y + 2 + i * dh, w - 4, dh - 4);
			} else if (contents.get(j).isBold) {
				//太字指定のとき
				pApplet.stroke(COLOR.LINES);
				pApplet.fill(COLOR.FILL);
				pApplet.strokeWeight(PUtility.mouseIsInRect(x + 2, y + 2 + i * dh, w - 4, dh - 4, pApplet.mouseX, pApplet.mouseY) ? (float) 1.5 : 1);
				pApplet.rect(x + 2, y + 2 + i * dh, w - 4, dh - 4);
				pApplet.strokeWeight(1);
			} else {
				//通常の描画
				pApplet.stroke(COLOR.LINES);
				pApplet.noFill();
				pApplet.strokeWeight(PUtility.mouseIsInRect(x + 2, y + 2 + i * dh, w - 4, dh - 4, pApplet.mouseX, pApplet.mouseY) ? (float) 1.5 : 1);
				pApplet.rect(x + 2, y + 2 + i * dh, w - 4, dh - 4);
				pApplet.strokeWeight(1);
			}
			pApplet.noStroke();
			pApplet.textAlign(PConstants.LEFT, PConstants.CENTER);
			pApplet.fill(selectedId == contents.get(j).id ? COLOR.BACKGROUND : COLOR.LINES);
			pApplet.text(contents.get(j).name, x + 7, y + i * dh, w - 7, dh);

			//パラメータを出力（-1のときは無効として扱う）
			pApplet.textAlign(PConstants.RIGHT, PConstants.CENTER);
			if (contents.get(j).param != -1)
				pApplet.text(String.valueOf(contents.get(j).param), x + 7, y + i * dh, w - 14, dh);
		}

		//はみ出し部分を塗りつぶし
		pApplet.fill(COLOR.BACKGROUND);
		pApplet.stroke(COLOR.BACKGROUND);
		pApplet.rect(x - 2, y + h, w + 4, dh);

		//枠線
		pApplet.stroke(COLOR.LINES);
		pApplet.noFill();
		pApplet.rect(x, y, w, h);
	}

}
