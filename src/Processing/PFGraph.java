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

	//スクロール位置
	public int scrollX, scrollY;
	private int mouseXBuffer, mouseYBuffer;

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
		//初期スクロール位置
		scrollX = 0;
		scrollY = 0;
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

		//スクロール適用
		translate(scrollX, scrollY);

		//背景描画
		noStroke();
		fill(COLOR.BACKGROUND);
		background(COLOR.LINES);
		rect(0, 0, 2560, 1440);

		//各リレーションを描画
		for (int i = 0; i < sToolEditor.fgm.getPFInterfaceList().size(); i++) {
			PFInterface pfi = sToolEditor.fgm.getPFInterfaceList().get(i);
			Domain rootDomain = pfi.rootDomain;
			Domain distDomain = pfi.distDomain;
			if (rootDomain == null || distDomain == null) break;

			//DomainIDが同じ場合（セルフループ記述）
			if (rootDomain.id == distDomain.id) {
				float dW = textWidth(rootDomain.name) / 2 + 35;
				float dH = 40;
				//ライン描画
				stroke(COLOR.LINES);
				noFill();
				rect(rootDomain.x, rootDomain.y, dW, dH);
			} else {
				//ライン描画
				stroke(COLOR.LINES);
				line(rootDomain.x, rootDomain.y, distDomain.x, distDomain.y);
			}

			//Interface選択Ellipse描画
			if (selectedInterfaceIndex == i) {
				fill(COLOR.SELECTED);
				noStroke();
			} else {
				fill(COLOR.BACKGROUND);
				stroke(COLOR.LINES);
			}
			strokeWeight(PUtility.mouseIsInEllipse(pfi.getX(this) + scrollX, pfi.getY() + scrollY, 10, 10, mouseX, mouseY) ? (float) 1.5 : 1);
			ellipse(pfi.getX(this), pfi.getY(), 10, 10);
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
			strokeWeight(PUtility.mouseIsInRect(d.x - dW / 2, d.y - dH / 2, dW, dH, mouseX - scrollX, mouseY - scrollY) ? (float) 1.5 : 1);
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

		//座標系戻し
		translate(-scrollX, -scrollY);

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

				//左記述かどうか
				boolean isDrawingToRight = pfi.getX(this) + scrollX < width / 2;
				rect(isDrawingToRight ? width / 2 : 20, 20, width / 2 - 20, height - 40);

				textAlign(LEFT, CENTER);
				fill(COLOR.LINES);
				text(pfi.rootDomain.name + "!", isDrawingToRight ? width / 2 + 5 : 25, 15, width / 2 - 10, 40);
				if (selectedEventIndex != -1 && !isInvSelected)
					text("Generated from", isDrawingToRight ? width / 2 + 5 + width / 4 : 25 + width / 4, 15, width / 2 - 10, 40);

				lbc = new ArrayList<>();
				//EventListを詰め込み
				for (int i = 0; i < pfEventListT.size(); i++) {
					lbc.add(new ListBoxContent(i, pfEventListT.get(i).event));
				}
				eventLB.setContents(lbc);
				eventLB.adjust(isDrawingToRight ? width / 2 + 5 : 25, 50, width / 4 - 30, height / 2 - 60, 30, !isInvSelected ? selectedEventIndex : -1);
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
				rootUCEventLB.adjust(isDrawingToRight ? width / 2 + 5 + width / 4 : 25 + width / 4, 50, width / 4 - 30, height / 2 - 60, 30, -1);
				if (selectedEventIndex != -1 && !isInvSelected) rootUCEventLB.draw(this);

				textAlign(LEFT, CENTER);
				fill(COLOR.LINES);
				text(pfi.distDomain.name + "!", isDrawingToRight ? width / 2 + 5 : 25, height / 2 - 5, width / 2 - 10, 40);
				if (selectedEventIndex != -1 && isInvSelected)
					text("Genereted from", isDrawingToRight ? width / 2 + 5 + width / 4 : 25 + width / 4, height / 2 - 5, width / 2 - 10, 40);
				lbc = new ArrayList<>();
				//EventListを詰め込み
				for (int i = 0; i < pfEventListF.size(); i++) {
					lbc.add(new ListBoxContent(i, pfEventListF.get(i).event));
				}
				invEventLB.setContents(lbc);
				invEventLB.adjust(isDrawingToRight ? width / 2 + 5 : 25, height / 2 + 30, width / 4 - 30, height / 2 - 60, 30, isInvSelected ? selectedEventIndex : -1);
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
				invRootUCEventLB.adjust(isDrawingToRight ? width / 2 + 5 + width / 4 : 25 + width / 4, height / 2 + 30, width / 4 - 30, height / 2 - 60, 30, -1);
				if (selectedEventIndex != -1 && isInvSelected) invRootUCEventLB.draw(this);

				//枠まわり
				fill(COLOR.SELECTED);
				noStroke();
				triangle(pfi.getX(this) + scrollX, pfi.getY() + scrollY, width / 2, height / 2 + 20, width / 2, height / 2 - 20);
				noFill();
				stroke(COLOR.LINES);
				rect(isDrawingToRight ? width / 2 : 20, 20, width / 2 - 20, height - 40);
			}
		}
	}

	public void mousePressed() {
		//スクロール用バッファ保持
		mouseXBuffer = mouseX;
		mouseYBuffer = mouseY;
		if (mouseButton == RIGHT) {
			scrollX = 0;
			scrollY = 0;
		}
		//temp
		ListBoxContent lbc;
		//インターフェース選択時
		if (selectedInterfaceIndex != -1) {
			lbc = eventLB.getContentOnMouse(mouseX, mouseY);
			if (lbc != null) {
				selectedEventIndex = lbc.id;
				isInvSelected = false;
				redraw();
				return;
			}
			lbc = invEventLB.getContentOnMouse(mouseX, mouseY);
			if (lbc != null) {
				selectedEventIndex = lbc.id;
				isInvSelected = true;
				redraw();
				return;
			}
		}
		//イベント選択時
		if (selectedEventIndex != -1) {
			lbc = rootUCEventLB.getContentOnMouse(mouseX, mouseY);
			if (lbc != null && !isInvSelected) {
				int selectedUsecaseId = lbc.id;
				sToolEditor.jumpToUCTab(selectedUsecaseId);
			}
			lbc = invRootUCEventLB.getContentOnMouse(mouseX, mouseY);
			if (lbc != null && isInvSelected) {
				int selectedUsecaseId = lbc.id;
				sToolEditor.jumpToUCTab(selectedUsecaseId);
			}
		}

		//選択の一時解除
		selectedDomainId = -1;
		selectedInterfaceIndex = -1;
		selectedEventIndex = -1;
		eventLB.scroll(0);
		invEventLB.scroll(0);
		rootUCEventLB.scroll(0);
		invRootUCEventLB.scroll(0);

		//マウスクリック範囲にドメインがあれば、それを選択
		for (Domain d : sToolEditor.fgm.getDomains()) {
			if (PUtility.mouseIsInRect(scrollX + d.x - (int) textWidth(d.name) / 2 - 15, scrollY + d.y - 20, (int) textWidth(d.name) + 30, 40, mouseX, mouseY)) {
				selectedDomainId = d.id;
				selectedInterfaceIndex = -1;
			}
		}

		//マウスクリック範囲にインターフェースがあれば、それを選択
		for (int i = 0; i < sToolEditor.fgm.getPFInterfaceList().size(); i++) {
			PFInterface pfi = sToolEditor.fgm.getPFInterfaceList().get(i);
			if (PUtility.mouseIsInEllipse(pfi.getX(this) + scrollX, pfi.getY() + scrollY, 10, 10, mouseX, mouseY)) {
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
				sToolEditor.fgm.moveDomain(selectedDomainId, mouseX - scrollX, mouseY - scrollY);
		} else {
			scrollX += mouseX - mouseXBuffer;
			scrollY += mouseY - mouseYBuffer;
			mouseXBuffer = mouseX;
			mouseYBuffer = mouseY;
		}
		redraw();
	}

	public void mouseReleased() {
		//行き過ぎ防止
		if (scrollX > 10) scrollX = 10;
		if (scrollX - width < -2570) scrollX = -2570 + width;
		if (scrollY > 10) scrollY = 10;
		if (scrollY - height < -1450) scrollY = -1450 + height;
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
