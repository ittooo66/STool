package Processing;

import Models.*;
import Core.*;
import processing.core.PApplet;
import processing.core.PFont;

/**
 * プロブレムフレームエディタタブで出力するProcessingコンポーネント
 */
public class PFGraph extends PApplet {

	//選択中のドメインID
	public int selectedDomainId = -1;

	//本体
	SToolEditor sToolEditor;

	//カラーパレット
	private final int COLOR_BACKGROUND = color(28, 28, 28);
	private final int COLOR_LINES = color(123, 144, 210);
	private final int COLOR_SELECTED = color(226, 148, 59);

	public PFGraph(SToolEditor sToolEditor) {
		this.sToolEditor = sToolEditor;
	}

	public void setup() {
		//とりあえず適当な解像度で初期化
		size(1024, 768);
		//CPU節約
		noLoop();
		//Font設定。
		PFont font = createFont("メイリオ ボールド", 15, true);
		textFont(font);
	}

	public void draw() {

		background(COLOR_BACKGROUND);
		stroke(COLOR_LINES);
		strokeWeight(2);

		//TODO:各リレーションを描画(UseCase描画の後)

		//TODO:各イベントを描画(UseCase描画の後)

		//各ドメインを描画
		textAlign(CENTER, CENTER);
		for (Domain d : sToolEditor.fgm.getDomains()) {
			//幅と高さを取得
			float dW = textWidth(d.name) + 30;
			float dH = 40;

			//fill変更
			fill(COLOR_BACKGROUND);
			//stroke変更(選択中か否か)
			stroke(d.id == selectedDomainId ? COLOR_SELECTED : COLOR_LINES);
			//ドメイン枠描画
			rect(d.x - dW / 2, d.y - dH / 2, dW, dH);

			// domaintype記述（左端のやつ）
			if (d.domainType == Domain.DomainType.DESIGNED) {
				line(d.x - dW / 2 + 5, d.y - dH / 2, d.x - dW / 2 + 5, d.y + dH / 2);
			} else if (d.domainType == Domain.DomainType.SYSTEM) {
				line(d.x - dW / 2 + 5, d.y - dH / 2, d.x - dW / 2 + 5, d.y + dH / 2);
				line(d.x - dW / 2 + 10, d.y - dH / 2, d.x - dW / 2 + 10, d.y + dH / 2);
			} else if (d.domainType == Domain.DomainType.BIDDABLE) {
				rect(d.x - dW / 2, d.y + 6, 14, 14);
				fill(d.id == selectedDomainId ? COLOR_SELECTED : COLOR_LINES);
				textAlign(LEFT, BOTTOM);
				textSize(10);
				text("B", d.x - dW / 2 + 3, d.y + dH/2 + 2);
			} else if (d.domainType == Domain.DomainType.CAUSAL) {
				rect(d.x - dW / 2, d.y + 6, 14, 14);
				fill(d.id == selectedDomainId ? COLOR_SELECTED : COLOR_LINES);
				textAlign(LEFT, BOTTOM);
				textSize(10);
				text("C", d.x - dW / 2 + 3, d.y + dH/2 + 2);
			} else if (d.domainType == Domain.DomainType.LEXICAL) {
				rect(d.x - dW / 2, d.y + 6, 14, 14);
				fill(d.id == selectedDomainId ? COLOR_SELECTED : COLOR_LINES);
				textAlign(LEFT, BOTTOM);
				textSize(10);
				text("X", d.x - dW / 2 + 3, d.y + dH/2 + 2);
			}

			//名前の記述
			fill(d.id == selectedDomainId ? COLOR_SELECTED : COLOR_LINES);
			textAlign(CENTER, CENTER);
			textSize(15);
			text(d.name, d.x, d.y - 2);
		}
	}

	public void mousePressed() {
		final int domainClickMergin = 40;

		//ドメイン選択の一時解除
		selectedDomainId = -1;
		for (Domain d : sToolEditor.fgm.getDomains()) {
			//マウスクリック範囲にドメインがあれば、それを選択
			if (d.x - domainClickMergin < mouseX && mouseX < d.x + domainClickMergin &&
					d.y - domainClickMergin < mouseY && mouseY < d.y + domainClickMergin) {
				selectedDomainId = d.id;
			}
		}
		sToolEditor.redraw();
	}

	public void mouseDragged() {
		if (mouseButton == LEFT && selectedDomainId != -1) {
			Domain d = sToolEditor.fgm.getDomainById(selectedDomainId);
			if (d != null && 0 < mouseX && mouseX < width && 0 < mouseY && mouseY < height)
				sToolEditor.fgm.moveDomain(selectedDomainId, mouseX, mouseY);
		}
		sToolEditor.redraw();
	}
}
