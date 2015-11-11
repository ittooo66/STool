package Processing;

import Models.*;
import Processing.Component.PUtility;
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

	public PFGraph(SToolEditor sToolEditor) {
		this.sToolEditor = sToolEditor;
	}

	public void setup() {
		//Font設定。
		PFont font = createFont("メイリオ ボールド", 15, true);
		textFont(font);
		//Smoothに描画
		smooth();
	}

	//変更フラグ
	private boolean hasChanges;

	public void redraw() {
		hasChanges = true;
	}

	public void draw() {
		//省力draw()
		if (!hasChanges) return;
		else hasChanges = false;

		background(COLOR_BACKGROUND);
		stroke(COLOR_LINES);
		strokeWeight(1);

		//各リレーションを描画
		for (Usecase uc : sToolEditor.fgm.getUsecases()) {
			for (Step s : uc.getAllActionStep()) {
				Domain obj = sToolEditor.fgm.getDomainById(s.objectDomainId);
				Domain sbj = sToolEditor.fgm.getDomainById(s.subjectDomainId);
				if (obj != null && sbj != null) {
					line(obj.x, obj.y, sbj.x, sbj.y);
				}
			}
		}

		//TODO:各イベントを描画(UseCase描画の後)
		fill(COLOR_LINES);
		for (Usecase uc : sToolEditor.fgm.getUsecases()) {
			for (Step s : uc.getAllActionStep()) {
				Domain obj = sToolEditor.fgm.getDomainById(s.objectDomainId);
				Domain sbj = sToolEditor.fgm.getDomainById(s.subjectDomainId);
				if (obj != null && sbj != null) {
					text(sbj.name + "! " + s.Event, (obj.x + sbj.x) / 2, (obj.y + sbj.y) / 2);
				}
			}
		}

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
			strokeWeight(PUtility.mouseIsInRect(d.x - dW / 2, d.y - dH / 2, dW, dH, mouseX, mouseY) ? (float) 1.5 : 1);
			rect(d.x - dW / 2, d.y - dH / 2, dW, dH);
			strokeWeight(1);

			//DomainType描画
			stroke(d.id == selectedDomainId ? COLOR_BACKGROUND : COLOR_LINES);
			char domainChar = d.domainType.toString().charAt(0);
			switch (d.domainType) {
				case SYSTEM:
					line(d.x - dW / 2 + 10, d.y - dH / 2, d.x - dW / 2 + 10, d.y + dH / 2);
				case DESIGNED:
					line(d.x - dW / 2 + 5, d.y - dH / 2, d.x - dW / 2 + 5, d.y + dH / 2);
					break;
				case NONE:
					break;
				case LEXICAL:
					domainChar = 'X';
				default:
					//BIDDABLE or CAUSAL or LEXICAL
					noFill();
					rect(d.x - dW / 2, d.y + 6, 14, 14);
					textAlign(LEFT, BOTTOM);
					textSize(10);
					fill(d.id == selectedDomainId ? COLOR_BACKGROUND : COLOR_LINES);
					text(domainChar, d.x - dW / 2 + 3, d.y + dH / 2 + 2);
					break;
			}

			//名前描画
			fill(d.id == selectedDomainId ? COLOR_BACKGROUND : COLOR_LINES);
			noStroke();
			textAlign(CENTER, CENTER);
			textSize(15);
			text(d.name, d.x, d.y - 2);
		}
	}

	public void mousePressed() {
		//ドメイン選択の一時解除
		selectedDomainId = -1;
		for (Domain d : sToolEditor.fgm.getDomains()) {
			//マウスクリック範囲にドメインがあれば、それを選択
			if (PUtility.mouseIsInRect(d.x - (int) textWidth(d.name) / 2 - 15, d.y - 20, (int) textWidth(d.name) + 30, 40, mouseX, mouseY))
				selectedDomainId = d.id;
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

	public void mouseMoved() {
		redraw();
	}
}
