import Models.Domain;
import processing.core.PApplet;
import processing.core.PFont;

import java.util.ArrayList;
import java.util.List;

/**
 * プロブレムフレームエディタタブで出力するProcessingコンポーネント
 */
public class PFGraph extends PApplet {
	int selectedDomainId = -1;

	SToolEditor sToolEditor;

	public PFGraph(SToolEditor sToolEditor){
		this.sToolEditor=sToolEditor;
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



	public void mousePressed(){
		final int domainMergin = 40;

		//ドメイン選択の一時解除
		selectedDomainId = -1;
		for (Domain d : sToolEditor.fgm.getDomains()) {
			//マウスクリック範囲にドメインがあれば、それを選択
			if (d.x - domainMergin < mouseX && mouseX < d.x + domainMergin &&
					d.y - domainMergin < mouseY && mouseY < d.y + domainMergin) {
				selectedDomainId = d.id;
			}
		}
		redraw();
	}

	public void mouseDragged(){
		if (mouseButton == LEFT) {
			if (selectedDomainId != -1) {
				Domain d = sToolEditor.fgm.getDomain(selectedDomainId);
				if (d != null) sToolEditor.fgm.editDomain(selectedDomainId, d.name, d.domainType, mouseX, mouseY);
			}
		}
		redraw();
	}

}
