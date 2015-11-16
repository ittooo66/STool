package Processing;

import Models.*;
import Processing.Component.ListBox;
import Processing.Component.ListBoxContent;
import Processing.Component.PUtility;
import Swing.*;
import processing.core.PApplet;
import processing.core.PFont;
import processing.event.MouseEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * プロブレムフレームエディタタブで出力するProcessingコンポーネント
 */
public class PFGraph extends PApplet {

	//選択中のドメインID
	public int selectedDomainId = -1;
	//選択中のインターフェースID
	public int selectedInterfaceIndex = -1;
	//選択中のイベントID
	public boolean isInvSelected = false;
	public int selectedEventIndex = -1;
	public int selectedUsecaseId = -1;

	//本体
	private SToolEditor sToolEditor;

	//Event表示用ListBox
	private ListBox eventLB, invEventLB, rootUCEventLB, invRootUCEventLB;

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

		//ListBox初期化
		eventLB = new ListBox(COLOR_BACKGROUND, COLOR_LINES, COLOR_SELECTED);
		invEventLB = new ListBox(COLOR_BACKGROUND, COLOR_LINES, COLOR_SELECTED);
		rootUCEventLB = new ListBox(COLOR_BACKGROUND, COLOR_LINES, COLOR_SELECTED);
		invRootUCEventLB = new ListBox(COLOR_BACKGROUND, COLOR_LINES, COLOR_SELECTED);
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
		noFill();

		//各リレーションを描画
		for (int i = 0; i < sToolEditor.fgm.getPFInterfaceList(sToolEditor.getViewmode()).size(); i++) {
			PFInterface pfi = sToolEditor.fgm.getPFInterfaceList(sToolEditor.getViewmode()).get(i);
			Domain rootDomain = sToolEditor.fgm.getDomainById(pfi.rootDomainId);
			Domain distDomain = sToolEditor.fgm.getDomainById(pfi.distDomainId);

			//ライン描画
			stroke(COLOR_LINES);
			line(rootDomain.x, rootDomain.y, distDomain.x, distDomain.y);

			if (selectedInterfaceIndex == i) {
				fill(COLOR_SELECTED);
				noStroke();
			} else {
				fill(COLOR_BACKGROUND);
				stroke(COLOR_LINES);
			}
			strokeWeight(PUtility.mouseIsInEllipse(pfi.x, pfi.y, 10, 10, mouseX, mouseY) ? (float) 1.5 : 1);
			ellipse(pfi.x, pfi.y, 10, 10);
			strokeWeight(1);
		}

		//各ドメインを描画
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
			switch (d.domainType) {
				case SYSTEM:
					line(d.x - dW / 2 + 10, d.y - dH / 2, d.x - dW / 2 + 10, d.y + dH / 2);
				case DESIGNED:
					line(d.x - dW / 2 + 5, d.y - dH / 2, d.x - dW / 2 + 5, d.y + dH / 2);
					break;
				case NONE:
					break;
				default:
					noFill();
					rect(d.x - dW / 2, d.y + 6, 14, 14);
					textAlign(LEFT, BOTTOM);
					textSize(10);
					fill(d.id == selectedDomainId ? COLOR_BACKGROUND : COLOR_LINES);
					text(Domain.DomainType.getPrefix(d.domainType), d.x - dW / 2 + 3, d.y + dH / 2 + 2);
					break;
			}

			//名前描画
			fill(d.id == selectedDomainId ? COLOR_BACKGROUND : COLOR_LINES);
			noStroke();
			textAlign(CENTER, CENTER);
			textSize(15);
			text(d.name, d.x, d.y - 2);
		}

		//TODO:インターフェース、イベント描画
		for (int interfaceIndex = 0; interfaceIndex < sToolEditor.fgm.getPFInterfaceList(sToolEditor.getViewmode()).size(); interfaceIndex++) {
			PFInterface pfi = sToolEditor.fgm.getPFInterfaceList(sToolEditor.getViewmode()).get(interfaceIndex);
			if (selectedInterfaceIndex == interfaceIndex) {
				System.out.println(selectedInterfaceIndex);
				//pfEvent描画
				List<PFEvent> pfEventListT = pfi.getEvents(true);
				List<PFEvent> pfEventListF = pfi.getEvents(false);
				List<ListBoxContent> lbc;

				//背景ぬりつぶし
				fill(COLOR_BACKGROUND);
				noStroke();
				rect(pfi.x < width / 2 ? width / 2 : 20, 20, width / 2 - 20, height - 40);

				textAlign(LEFT, CENTER);
				fill(COLOR_LINES);
				text(sToolEditor.fgm.getDomainById(pfi.rootDomainId).name + "!", pfi.x < width / 2 ? width / 2 + 5 : 25, 15, width / 2 - 10, 40);
				lbc = new ArrayList<>();
				for (int i = 0; i < pfEventListT.size(); i++) {
					lbc.add(new ListBoxContent(i, pfEventListT.get(i).event));
				}
				eventLB.setContents(lbc);
				eventLB.adjust(pfi.x < width / 2 ? width / 2 + 5 : 25, 50, width / 4 - 30, height / 2 - 60, 30, !isInvSelected ? selectedEventIndex : -1);
				eventLB.draw(this);

				//TODO:適切な詰め込み
				lbc = new ArrayList<>();
				rootUCEventLB.setContents(lbc);
				rootUCEventLB.adjust(pfi.x < width / 2 ? width / 2 + 5 + width / 4 : 25 + width / 4, 50, width / 4 - 30, height / 2 - 60, 30, !isInvSelected ? selectedUsecaseId : -1);
				if (selectedEventIndex != -1 && !isInvSelected) rootUCEventLB.draw(this);

				textAlign(LEFT, CENTER);
				fill(COLOR_LINES);
				text(sToolEditor.fgm.getDomainById(pfi.distDomainId).name + "!", pfi.x < width / 2 ? width / 2 + 5 : 25, height / 2 - 5, width / 2 - 10, 40);
				lbc = new ArrayList<>();
				for (int i = 0; i < pfEventListF.size(); i++) {
					lbc.add(new ListBoxContent(i, pfEventListF.get(i).event));
				}
				invEventLB.setContents(lbc);
				invEventLB.adjust(pfi.x < width / 2 ? width / 2 + 5 : 25, height / 2 + 30, width / 4 - 30, height / 2 - 60, 30, isInvSelected ? selectedEventIndex : -1);
				invEventLB.draw(this);

				//TODO:適切な詰め込み
				lbc = new ArrayList<>();
				invRootUCEventLB.setContents(lbc);
				invRootUCEventLB.adjust(pfi.x < width / 2 ? width / 2 + 5 + width / 4 : 25 + width / 4, height / 2 + 30, width / 4 - 30, height / 2 - 60, 30, isInvSelected ? selectedUsecaseId : -1);
				if (selectedEventIndex != -1 && isInvSelected) invRootUCEventLB.draw(this);

				//枠まわり
				fill(COLOR_SELECTED);
				noStroke();
				triangle(pfi.x, pfi.y, width / 2, height / 2 + 20, width / 2, height / 2 - 20);
				noFill();
				stroke(COLOR_LINES);
				rect(pfi.x < width / 2 ? width / 2 : 20, 20, width / 2 - 20, height - 40);
			}
		}
	}

	public void mousePressed() {
		//インターフェース選択時
		if (selectedInterfaceIndex != -1) {
			if (eventLB.getContentOnMouse(mouseX, mouseY) != null) {
				selectedEventIndex = eventLB.getContentOnMouse(mouseX, mouseY).id;
				isInvSelected = false;
				redraw();
				return;
			}
			if (invEventLB.getContentOnMouse(mouseX, mouseY) != null) {
				selectedEventIndex = invEventLB.getContentOnMouse(mouseX, mouseY).id;
				isInvSelected = true;
				redraw();
				return;
			}
		}

		//選択の一時解除
		selectedDomainId = -1;
		selectedInterfaceIndex = -1;
		selectedEventIndex = -1;
		selectedUsecaseId = -1;

		//マウスクリック範囲にドメインがあれば、それを選択
		for (Domain d : sToolEditor.fgm.getDomains()) {
			if (PUtility.mouseIsInRect(d.x - (int) textWidth(d.name) / 2 - 15, d.y - 20, (int) textWidth(d.name) + 30, 40, mouseX, mouseY)) {
				selectedDomainId = d.id;
				selectedInterfaceIndex = -1;
			}
		}

		//マウスクリック範囲にインターフェースがあれば、それを選択
		for (int i = 0; i < sToolEditor.fgm.getPFInterfaceList(sToolEditor.getViewmode()).size(); i++) {
			PFInterface pfi = sToolEditor.fgm.getPFInterfaceList(sToolEditor.getViewmode()).get(i);
			if (PUtility.mouseIsInEllipse(pfi.x, pfi.y, 10, 10, mouseX, mouseY)) {
				selectedDomainId = -1;
				selectedInterfaceIndex = i;
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

	public void mouseWheel(MouseEvent event) {
		//カウント取得
		int e = event.getCount() > 0 ? 1 : -1;

		PFInterface pfi = null;
		for (int interfaceIndex = 0; interfaceIndex < sToolEditor.fgm.getPFInterfaceList(sToolEditor.getViewmode()).size(); interfaceIndex++)
			if (selectedInterfaceIndex == interfaceIndex)
				pfi = sToolEditor.fgm.getPFInterfaceList(sToolEditor.getViewmode()).get(interfaceIndex);
		if (pfi == null) return;

		//スクロール箇所の特定・適用
		if (PUtility.mouseIsInRect(pfi.x < width / 2 ? width / 2 + 5 : 25, 50, width / 4 - 30, height / 2 - 60, mouseX, mouseY)) {
			eventLB.scroll(e);
		} else if (PUtility.mouseIsInRect(pfi.x < width / 2 ? width / 2 + 5 : 25, height / 2 + 30, width / 4 - 30, height / 2 - 60, mouseX, mouseY)) {
			invEventLB.scroll(e);
		} else if (PUtility.mouseIsInRect(pfi.x < width / 2 ? width / 2 + 5 + width / 4 : 25 + width / 4, 50, width / 4 - 30, height / 2 - 60, mouseX, mouseY)) {
			rootUCEventLB.scroll(e);
		} else if (PUtility.mouseIsInRect(pfi.x < width / 2 ? width / 2 + 5 + width / 4 : 25 + width / 4, height / 2 + 30, width / 4 - 30, height / 2 - 60, mouseX, mouseY)) {
			invRootUCEventLB.scroll(e);
		}
		redraw();
	}

	public void mouseMoved() {
		redraw();
	}
}
