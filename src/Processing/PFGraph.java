package Processing;

import Models.*;
import Swing.*;
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
	private final int COLOR_BACKGROUND = color(255, 255, 255);
	private final int COLOR_LINES = color(51, 51, 51);
	private final int COLOR_SELECTED = color(57, 152, 214);

	boolean isDrawing;

	public PFGraph(SToolEditor sToolEditor) {
		this.sToolEditor = sToolEditor;
	}

	public void setup() {
		//とりあえず適当な解像度で初期化
		size(1024, 768);
		//Font設定。
		PFont font = createFont("メイリオ ボールド", 15, true);
		textFont(font);
		//Smoothに描画
		smooth();
		//CPU節約
		noLoop();
	}

	public void draw() {
		isDrawing = true;

		background(COLOR_BACKGROUND);
		stroke(COLOR_LINES);
		strokeWeight(1);

		//TODO:各リレーションを描画(UseCase描画の後)

		//TODO:各イベントを描画(UseCase描画の後)

		//各ドメインを描画
		textAlign(CENTER, CENTER);
		for (Domain d : sToolEditor.fgm.getDomains()) {
			//幅と高さを取得
			float dW = textWidth(d.name) + 30;
			float dH = 40;

			//ドメイン描画
			if (d.id == selectedDomainId) {
				fill(COLOR_SELECTED);
				noStroke();
			} else {
				fill(COLOR_BACKGROUND);
				stroke(COLOR_LINES);
			}
			rect(d.x - dW / 2, d.y - dH / 2, dW, dH);

			//DomainType描画
			stroke(d.id == selectedDomainId ? COLOR_BACKGROUND : COLOR_LINES);
			switch (d.domainType) {
				case SYSTEM:
					line(d.x - dW / 2 + 10, d.y - dH / 2, d.x - dW / 2 + 10, d.y + dH / 2);
				case DESIGNED:
					line(d.x - dW / 2 + 5, d.y - dH / 2, d.x - dW / 2 + 5, d.y + dH / 2);
					break;
				case NONE:
					break;
				default:
					//BIDDABLE or CAUSAL or LEXICAL
					noFill();
					rect(d.x - dW / 2, d.y + 6, 14, 14);
					textAlign(LEFT, BOTTOM);
					textSize(10);
					fill(d.id == selectedDomainId ? COLOR_BACKGROUND : COLOR_LINES);
					text(d.domainType.toString().charAt(0), d.x - dW / 2 + 3, d.y + dH / 2 + 2);
					break;
			}

			//名前描画
			fill(d.id == selectedDomainId ? COLOR_BACKGROUND : COLOR_LINES);
			noStroke();
			textAlign(CENTER, CENTER);
			textSize(15);
			text(d.name, d.x, d.y - 2);
		}

		isDrawing = false;
	}

	public void redraw() {
		if (!isDrawing) super.redraw();
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

		if (selectedDomainId == -1) sToolEditor.initTextArea();

		sToolEditor.redraw();
	}

	public void mouseDragged() {
		if (mouseButton == LEFT && selectedDomainId != -1) {
			Domain d = sToolEditor.fgm.getDomainById(selectedDomainId);
			if (d != null && 0 < mouseX && mouseX < width && 0 < mouseY && mouseY < height)
				sToolEditor.fgm.moveDomain(selectedDomainId, mouseX, mouseY);
		}
		redraw();
	}
}
