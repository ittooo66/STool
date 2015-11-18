package Processing;

import Models.*;
import Processing.Component.COLOR;
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

	//本体
	private SToolEditor sToolEditor;

	//Event表示用ListBox
	private ListBox eventLB, invEventLB, rootUCEventLB, invRootUCEventLB;

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
		eventLB = new ListBox();
		invEventLB = new ListBox();
		rootUCEventLB = new ListBox();
		invRootUCEventLB = new ListBox();
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

		background(COLOR.BACKGROUND);
		stroke(COLOR.LINES);
		strokeWeight(1);
		noFill();

		//各リレーションを描画
		for (int i = 0; i < sToolEditor.fgm.getPFInterfaceList().size(); i++) {
			PFInterface pfi = sToolEditor.fgm.getPFInterfaceList().get(i);
			Domain rootDomain = sToolEditor.fgm.getDomainById(pfi.rootDomainId);
			Domain distDomain = sToolEditor.fgm.getDomainById(pfi.distDomainId);

			//ライン描画
			stroke(COLOR.LINES);
			line(rootDomain.x, rootDomain.y, distDomain.x, distDomain.y);

			if (selectedInterfaceIndex == i) {
				fill(COLOR.SELECTED);
				noStroke();
			} else {
				fill(COLOR.BACKGROUND);
				stroke(COLOR.LINES);
			}
			strokeWeight(PUtility.mouseIsInEllipse(pfi.x, pfi.y, 10, 10, mouseX, mouseY) ? (float) 1.5 : 1);
			ellipse(pfi.x, pfi.y, 10, 10);
			strokeWeight(1);
		}

		//各ドメインを描画
		for (Domain d : sToolEditor.fgm.getDomains()) {
			//Reduced時、関連ドメインでなければ記述対象から除外
			if (sToolEditor.fgm.getViewmode() == FGModelAdapter.VIEWMODE.REDUCED && !sToolEditor.fgm.hasRelatedEvent(d.id))
				continue;

			//幅と高さを取得
			float dW = textWidth(d.name) + 30;
			float dH = 40;

			//ドメイン描画
			if (d.id == selectedDomainId) {
				fill(COLOR.SELECTED);
				noStroke();
			} else {
				fill(COLOR.BACKGROUND);
				stroke(COLOR.LINES);
			}
			strokeWeight(PUtility.mouseIsInRect(d.x - dW / 2, d.y - dH / 2, dW, dH, mouseX, mouseY) ? (float) 1.5 : 1);
			rect(d.x - dW / 2, d.y - dH / 2, dW, dH);
			strokeWeight(1);

			//DomainType描画
			stroke(d.id == selectedDomainId ? COLOR.BACKGROUND : COLOR.LINES);
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
					fill(d.id == selectedDomainId ? COLOR.BACKGROUND : COLOR.LINES);
					text(Domain.DomainType.getPrefix(d.domainType), d.x - dW / 2 + 3, d.y + dH / 2 + 2);
					break;
			}

			//名前描画
			fill(d.id == selectedDomainId ? COLOR.BACKGROUND : COLOR.LINES);
			noStroke();
			textAlign(CENTER, CENTER);
			textSize(15);
			text(d.name, d.x, d.y - 2);
		}

		//インターフェース、イベント描画
		for (int interfaceIndex = 0; interfaceIndex < sToolEditor.fgm.getPFInterfaceList().size(); interfaceIndex++) {
			PFInterface pfi = sToolEditor.fgm.getPFInterfaceList().get(interfaceIndex);
			if (selectedInterfaceIndex == interfaceIndex) {
				//pfEvent描画
				List<PFEvent> pfEventListT = pfi.getEvents(true);
				List<PFEvent> pfEventListF = pfi.getEvents(false);
				List<ListBoxContent> lbc;

				//背景ぬりつぶし
				fill(COLOR.BACKGROUND);
				noStroke();
				rect(pfi.x < width / 2 ? width / 2 : 20, 20, width / 2 - 20, height - 40);

				textAlign(LEFT, CENTER);
				fill(COLOR.LINES);
				text(sToolEditor.fgm.getDomainById(pfi.rootDomainId).name + "!", pfi.x < width / 2 ? width / 2 + 5 : 25, 15, width / 2 - 10, 40);
				if (selectedEventIndex != -1 && !isInvSelected)
					text("Generated from", pfi.x < width / 2 ? width / 2 + 5 + width / 4 : 25 + width / 4, 15, width / 2 - 10, 40);

				lbc = new ArrayList<>();
				//EventListを詰め込み
				for (int i = 0; i < pfEventListT.size(); i++) {
					lbc.add(new ListBoxContent(i, pfEventListT.get(i).event));
				}
				eventLB.setContents(lbc);
				eventLB.adjust(pfi.x < width / 2 ? width / 2 + 5 : 25, 50, width / 4 - 30, height / 2 - 60, 30, !isInvSelected ? selectedEventIndex : -1);
				eventLB.draw(this);

				lbc = new ArrayList<>();
				//Event発生元の各Usecaseを詰め込み
				if (selectedEventIndex != -1 && pfEventListT.size() > selectedEventIndex) {
					PFEvent pfe = pfEventListT.get(selectedEventIndex);
					for (int i = 0; i < pfe.rootUsecaseId.size(); i++) {
						Usecase uc = sToolEditor.fgm.getUsecaseById(pfe.rootUsecaseId.get(i));
						if (uc != null) {
							lbc.add(new ListBoxContent(uc.id, uc.name));
						}
					}
				}
				rootUCEventLB.setContents(lbc);
				rootUCEventLB.adjust(pfi.x < width / 2 ? width / 2 + 5 + width / 4 : 25 + width / 4, 50, width / 4 - 30, height / 2 - 60, 30, -1);
				if (selectedEventIndex != -1 && !isInvSelected) rootUCEventLB.draw(this);

				textAlign(LEFT, CENTER);
				fill(COLOR.LINES);
				text(sToolEditor.fgm.getDomainById(pfi.distDomainId).name + "!", pfi.x < width / 2 ? width / 2 + 5 : 25, height / 2 - 5, width / 2 - 10, 40);
				if (selectedEventIndex != -1 && isInvSelected)
					text("Genereted from", pfi.x < width / 2 ? width / 2 + 5 + width / 4 : 25 + width / 4, height / 2 - 5, width / 2 - 10, 40);
				lbc = new ArrayList<>();
				//EventListを詰め込み
				for (int i = 0; i < pfEventListF.size(); i++) {
					lbc.add(new ListBoxContent(i, pfEventListF.get(i).event));
				}
				invEventLB.setContents(lbc);
				invEventLB.adjust(pfi.x < width / 2 ? width / 2 + 5 : 25, height / 2 + 30, width / 4 - 30, height / 2 - 60, 30, isInvSelected ? selectedEventIndex : -1);
				invEventLB.draw(this);

				lbc = new ArrayList<>();
				//Event発生元の各Usecaseを詰め込み
				if (selectedEventIndex != -1 && pfEventListF.size() > selectedEventIndex) {
					PFEvent pfe = pfEventListF.get(selectedEventIndex);
					for (int i = 0; i < pfe.rootUsecaseId.size(); i++) {
						Usecase uc = sToolEditor.fgm.getUsecaseById(pfe.rootUsecaseId.get(i));
						if (uc != null) {
							lbc.add(new ListBoxContent(uc.id, uc.name));
						}
					}
				}
				invRootUCEventLB.setContents(lbc);
				invRootUCEventLB.adjust(pfi.x < width / 2 ? width / 2 + 5 + width / 4 : 25 + width / 4, height / 2 + 30, width / 4 - 30, height / 2 - 60, 30, -1);
				if (selectedEventIndex != -1 && isInvSelected) invRootUCEventLB.draw(this);

				//枠まわり
				fill(COLOR.SELECTED);
				noStroke();
				triangle(pfi.x, pfi.y, width / 2, height / 2 + 20, width / 2, height / 2 - 20);
				noFill();
				stroke(COLOR.LINES);
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
		//イベント選択時
		if (selectedEventIndex != -1) {
			if (rootUCEventLB.getContentOnMouse(mouseX, mouseY) != null && !isInvSelected) {
				int selectedUsecaseId = rootUCEventLB.getContentOnMouse(mouseX, mouseY).id;
				sToolEditor.jumpToUCTab(selectedUsecaseId);
			}
			if (invRootUCEventLB.getContentOnMouse(mouseX, mouseY) != null && isInvSelected) {
				int selectedUsecaseId = invRootUCEventLB.getContentOnMouse(mouseX, mouseY).id;
				sToolEditor.jumpToUCTab(selectedUsecaseId);
			}
		}

		//選択の一時解除
		selectedDomainId = -1;
		selectedInterfaceIndex = -1;
		selectedEventIndex = -1;

		//マウスクリック範囲にドメインがあれば、それを選択
		for (Domain d : sToolEditor.fgm.getDomains()) {
			if (PUtility.mouseIsInRect(d.x - (int) textWidth(d.name) / 2 - 15, d.y - 20, (int) textWidth(d.name) + 30, 40, mouseX, mouseY)) {
				selectedDomainId = d.id;
				selectedInterfaceIndex = -1;
			}
		}

		//マウスクリック範囲にインターフェースがあれば、それを選択
		for (int i = 0; i < sToolEditor.fgm.getPFInterfaceList().size(); i++) {
			PFInterface pfi = sToolEditor.fgm.getPFInterfaceList().get(i);
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

		//
		PFInterface pfi = null;
		for (int interfaceIndex = 0; interfaceIndex < sToolEditor.fgm.getPFInterfaceList().size(); interfaceIndex++)
			if (selectedInterfaceIndex == interfaceIndex)
				pfi = sToolEditor.fgm.getPFInterfaceList().get(interfaceIndex);
		if (pfi == null) return;

		//スクロール箇所の特定・適用
		if (eventLB.isOn(mouseX, mouseY)) eventLB.scroll(e);
		if (invEventLB.isOn(mouseX, mouseY)) invEventLB.scroll(e);
		if (rootUCEventLB.isOn(mouseX, mouseY)) rootUCEventLB.scroll(e);
		if (invRootUCEventLB.isOn(mouseX, mouseY)) invRootUCEventLB.scroll(e);
		redraw();
	}

	public void mouseMoved() {
		redraw();
	}
}
