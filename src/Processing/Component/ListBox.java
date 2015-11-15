package Processing.Component;

import processing.core.PApplet;
import processing.core.PConstants;

import java.util.ArrayList;
import java.util.List;

public class ListBox implements Drawable {

	//配色
	private final int COLOR_BACKGROUND;
	private final int COLOR_LINES;
	private final int COLOR_SELECTED;

	//dh:１カラムの幅
	private int x, y, w, h, dh;
	//現在スクロールされている量
	private int scrollIndex;
	private int selectedId;
	//コンテンツ
	private List<ListBoxContent> contents;

	public ListBox(int COLOR_BACKGROUND, int COLOR_LINES, int COLOR_SELECTED) {
		this.COLOR_BACKGROUND = COLOR_BACKGROUND;
		this.COLOR_LINES = COLOR_LINES;
		this.COLOR_SELECTED = COLOR_SELECTED;
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
		pApplet.textAlign(PConstants.LEFT, PConstants.CENTER);
		pApplet.fill(COLOR_LINES);
		pApplet.noFill();
		pApplet.stroke(COLOR_LINES);

		for (int i = 0, j = scrollIndex; j < contents.size() && i * dh < h; i++, j++) {
			if (selectedId == contents.get(j).id) {
				pApplet.noStroke();
				pApplet.fill(COLOR_SELECTED);
				pApplet.rect(x + 2, y + 2 + i * dh, w - 4, dh - 4);
				pApplet.fill(COLOR_BACKGROUND);
			} else {
				pApplet.stroke(COLOR_LINES);
				pApplet.noFill();
				pApplet.strokeWeight(PUtility.mouseIsInRect(x + 2, y + 2 + i * dh, w - 4, dh - 4, pApplet.mouseX, pApplet.mouseY) ? (float) 1.5 : 1);
				pApplet.rect(x + 2, y + 2 + i * dh, w - 4, dh - 4);
				pApplet.strokeWeight(1);
				pApplet.fill(COLOR_LINES);
			}
			pApplet.noStroke();
			pApplet.text(contents.get(j).name, x + 7, y + i * dh, w - 7, dh);
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
