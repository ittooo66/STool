/**
 * Created by shoichiro on 2015/08/19.
 */

import Models.Domain;
import Models.Goal;
import processing.core.PApplet;
import controlP5.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * TODO:各種PApplet（GGGraph,PFGraph...）に移行させてこいつを消す
 */
public class PAppletMain extends PApplet {
	//融合ゴールモデル
	FGModel fgm;


	//選択中のタブ
	SelectedTab selectedTab;
	enum  SelectedTab{//タブ一覧
		GGEDIT,UCEDIT,PFEDIT,METRICS
	}

	int selectedDomainId = -1;


	public void setup() {
		//初期設定
		size(1024, 768);
		strokeWeight(2);
		fgm = new FGModel();
	}

	public void draw() {
		background(128);
		//タブで記述内容切り替え
		if (selectedTab == SelectedTab.UCEDIT) {
			drawUC();
		} else if (selectedTab == SelectedTab.PFEDIT) {
			drawPF();
		}
	}

	void drawUC() {
		//TODO:UseCase
	}

	void drawPF() {
		//各ドメインを描画
		for (Domain d : fgm.domains) {
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
		//タブで記述内容切り替え
		if (selectedTab == SelectedTab.UCEDIT) {
			mousePressOnUCEDIT();
		} else if (selectedTab == SelectedTab.PFEDIT) {
			mousePressOnPFEDIT();
		}
	}

	void mousePressOnUCEDIT() {
		//TODO:UseCase
	}

	void mousePressOnPFEDIT() {
		final int domainMergin = 40;

		//ドメイン選択の一時解除
		selectedDomainId = -1;
		for (Domain d : fgm.domains) {
			//マウスクリック範囲にドメインがあれば、それを選択
			if (d.x - domainMergin < mouseX && mouseX < d.x + domainMergin &&
					d.y - domainMergin < mouseY && mouseY < d.y + domainMergin) {
				selectedDomainId = d.id;
			}
		}
	}

	public void mouseDragged() {
		//各種mouseDrag実行
		if (selectedTab == SelectedTab.GGEDIT) {
			mouseDragGG();
		} else if (selectedTab == SelectedTab.UCEDIT) {
			mouseDragUC();
		} else if (selectedTab == SelectedTab.PFEDIT) {
			mouseDragPF();
		} else if (selectedTab == SelectedTab.METRICS) {
			mouseDragME();
		}
	}

	void mouseDragGG() {

	}

	void mouseDragUC() {

	}

	void mouseDragPF() {
		if (mouseButton == LEFT) {
			if (selectedDomainId != -1) {
				Domain d = fgm.getDomain(selectedDomainId);
				if (d != null) fgm.editDomain(selectedDomainId, d.name, d.domainType, mouseX, mouseY);
			}
		}
	}

	void mouseDragME() {
	}

}
