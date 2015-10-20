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
	int selectedDomainId = -1;

	//本体
	SToolEditor sToolEditor;

	//カラーパレット
	private final int COLOR_BACKGROUND = color(28, 28, 28);
	private final int COLOR_LINES = color(123, 144, 210);
	private final int COLOR_SELECTED = color(226, 148, 59);

	public PFGraph(SToolEditor sToolEditor) {
		this.sToolEditor = sToolEditor;
	}

	public  void setup(){
		size(1024,768);
		noLoop();

		//Font設定。
		PFont font = createFont("メイリオ ボールド",15,true);
		textFont(font);
	}

	public void draw(){

		background(0);
		stroke(200,200,200);

		text("W:"+width+",H:"+height+" on PF",mouseX,mouseY);


		//各ドメインを描画
		for (Domain d : sToolEditor.fgm.getDomains()) {
			noStroke();
			fill(252, 252, 252);
			rect(d.x - 20 - d.name.length() * (float) 3.5, d.y - 20, 40 + d.name.length() * 7, 40);

			if (d.id == selectedDomainId)
				//選択されたドメインは赤
				stroke(255, 0, 0);
			else
				stroke(128, 128, 128);

			rect(d.x - 20 - d.name.length() * (float) 3.5, d.y - 20, 40 + d.name.length() * 7, 40);

			// domaintype記述（左端のやつ）
			if (d.domainType == Domain.DomainType.DESIGNED) {
				line(d.x - 20 - d.name.length() * (float) 3.5 + 5, d.y - 20, d.x - 20 - d.name.length() * (float) 3.5 + 5, d.y + 20);
			} else if (d.domainType == Domain.DomainType.SYSTEM) {
				line(d.x - 20 - d.name.length() * (float) 3.5 + 5, d.y - 20, d.x - 20 - d.name.length() * (float) 3.5 + 5, d.y + 20);
				line(d.x - 20 - d.name.length() * (float) 3.5 + 10, d.y - 20, d.x - 20 - d.name.length() * (float) 3.5 + 10, d.y + 20);
			} else if (d.domainType == Domain.DomainType.BIDDABLE) {
				rect(d.x - 20 - d.name.length() * (float) 3.5, d.y + 6, 20, 14);
				fill(100, 100, 100);
				textAlign(CENTER);
				text("B", d.x - 20 - d.name.length() * (float) 3.5 + 8, d.y + 18);
			} else if (d.domainType == Domain.DomainType.CAUSAL) {
				rect(d.x - 20 - d.name.length() * (float) 3.5, d.y + 6, 20, 14);
				fill(100, 100, 100);
				textAlign(CENTER);
				text("C", d.x - 20 - d.name.length() * (float) 3.5 + 8, d.y + 18);
			} else if (d.domainType == Domain.DomainType.LEXICAL) {
				rect(d.x - 20 - d.name.length() * (float) 3.5, d.y + 6, 20, 14);
				fill(100, 100, 100);
				textAlign(CENTER);
				text("X", d.x - 20 - d.name.length() * (float) 3.5 + 8, d.y + 18);
			}

			//名前の記述
			fill(100, 100, 100);
			textAlign(CENTER);
			text(d.name, d.x, d.y + 2);
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
