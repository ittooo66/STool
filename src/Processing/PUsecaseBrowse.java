package Processing;

import Models.FGModelAdapter;
import Models.Step;
import Models.Usecase;
import Processing.Component.ButtonSetFrame;
import Processing.Component.COLOR;
import Processing.Component.ListBox;
import Processing.Component.ListBoxContent;
import difflib.Delta;
import difflib.DiffUtils;
import difflib.Patch;
import processing.core.PApplet;
import processing.core.PFont;
import processing.event.MouseEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 66 on 2015/11/26.
 * UsecaseDiffBrowser(Processing part)
 */
public class PUsecaseBrowse extends PApplet {

	private FGModelAdapter fgm;

	private ButtonSetFrame bsf1, bsf2;
	private ListBox lb1, lb2;

	private int ucIndex1, ucIndex2;

	public PUsecaseBrowse(FGModelAdapter fgm) {
		this.fgm = fgm;
		ucIndex1 = 0;
		ucIndex2 = 0;
	}

	public void setup() {
		//Font設定。
		PFont font = createFont("メイリオ ボールド", 15, true);
		textFont(font);
		//Smoothに描画
		smooth();

		//ButtonSetFrame,ListBoxをSetup
		bsf1 = new ButtonSetFrame("Usecase 1");
		bsf1.addButton("←");
		bsf1.addButton("→");
		lb1 = new ListBox();
		lb1.setSelectable(false);
		bsf2 = new ButtonSetFrame("Usecase 2");
		bsf2.addButton("←");
		bsf2.addButton("→");
		lb2 = new ListBox();
		lb2.setSelectable(false);
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

		//背景描画
		background(COLOR.BACKGROUND);

		//各種設定値
		int MERGIN = 30;
		int COLUMN_WIDTH = (width - MERGIN * 3) / 2;
		int COLUMN_HEIGHT = (height - MERGIN * 3);

		//Usecase2つ取得
		Usecase usecase1 = null, usecase2 = null;
		List<Usecase> usecaseList = fgm.getUsecases();
		for (int i = 0; i < usecaseList.size(); i++) {
			if (ucIndex1 == i) usecase1 = usecaseList.get(i);
			if (ucIndex2 == i) usecase2 = usecaseList.get(i);
		}
		if (usecase1 == null || usecase2 == null) return;
		//FrameにTitleをSet
		bsf1.setTitle(usecase1.name);
		bsf2.setTitle(usecase2.name);

		//各種lbに詰め込み
		List<ListBoxContent> lb = new ArrayList<>();
		int id = 0;
		for (String str : usecase1.toStringList(fgm)) {
			boolean isDiff = isDiff(usecase1.toStringList(fgm), usecase2.toStringList(fgm), str);
			lb.add(new ListBoxContent(id++, str, isDiff));
		}
		lb1.setContents(lb);

		lb = new ArrayList<>();
		id = 0;
		for (String str : usecase2.toStringList(fgm)) {
			boolean isDiff = isDiff(usecase1.toStringList(fgm), usecase2.toStringList(fgm), str);
			lb.add(new ListBoxContent(id++, str, isDiff));
		}
		lb2.setContents(lb);

		bsf1.adjust(MERGIN, MERGIN, COLUMN_WIDTH, MERGIN);
		bsf1.draw(this);
		bsf2.adjust(2 * MERGIN + COLUMN_WIDTH, MERGIN, COLUMN_WIDTH, MERGIN);
		bsf2.draw(this);
		lb1.adjust(MERGIN, 2 * MERGIN, COLUMN_WIDTH, COLUMN_HEIGHT, MERGIN, -1);
		lb1.draw(this);
		lb2.adjust(2 * MERGIN + COLUMN_WIDTH, 2 * MERGIN, COLUMN_WIDTH, COLUMN_HEIGHT, MERGIN, -1);
		lb2.draw(this);
	}

	private boolean isDiff(List<String> ori, List<String> rev, String line) {
		Patch patch = DiffUtils.diff(ori, rev);
		for (Delta delta : patch.getDeltas()) {
			for (String str : (List<String>) delta.getOriginal().getLines()) {
				if (str.equals(line)) return true;
			}
			for (String str : (List<String>) delta.getRevised().getLines()) {
				if (str.equals(line)) return true;
			}
		}
		return false;
	}

	public void mousePressed() {
		//マウス位置取得
		int x = mouseX;
		int y = mouseY;

		switch (bsf1.getButtonIdOnMouse(x, y)) {
			case 0:
				ucIndex1 = getPrevUcIndex(ucIndex1);
				break;
			case 1:
				ucIndex1 = getNextUcIndex(ucIndex1);
				break;
		}
		switch (bsf2.getButtonIdOnMouse(x, y)) {
			case 0:
				ucIndex2 = getPrevUcIndex(ucIndex2);
				break;
			case 1:
				ucIndex2 = getNextUcIndex(ucIndex2);
				break;
		}
		redraw();
	}

	private int getNextUcIndex(int ucIndex) {
		if (ucIndex < fgm.getUsecases().size() - 1)
			return ucIndex + 1;
		else
			return 0;
	}

	private int getPrevUcIndex(int ucIndex) {
		if (ucIndex > 0)
			return ucIndex - 1;
		else
			return fgm.getUsecases().size() - 1;
	}

	public void mouseWheel(MouseEvent event) {
		//マウス位置取得
		int x = mouseX;
		int y = mouseY;
		//カウント取得
		int e = event.getCount() > 0 ? 1 : -1;
		//スクロール箇所の特定・適用
		if (lb1.isOn(x, y)) lb1.scroll(e);
		if (lb2.isOn(x, y)) lb2.scroll(e);
		//再描画
		redraw();
	}

	public void mouseMoved() {
		redraw();
	}
}
