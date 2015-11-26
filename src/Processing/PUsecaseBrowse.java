package Processing;

import Models.FGModelAdapter;
import Models.Step;
import Models.Usecase;
import Processing.Component.ButtonSetFrame;
import Processing.Component.ListBox;
import Processing.Component.ListBoxContent;
import processing.core.PApplet;
import processing.core.PFont;

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

	private int ucindex1, ucindex2;

	public PUsecaseBrowse(FGModelAdapter fgm) {
		this.fgm = fgm;
		ucindex1 = 0;
		ucindex2 = 0;
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

	public void draw() {

		//Usecase2つ取得
		Usecase usecase1 = null, usecase2 = null;
		List<Usecase> usecaseList = fgm.getUsecases();
		for (int i = 0; i < usecaseList.size(); i++) {
			if (ucindex1 == i) usecase1 = usecaseList.get(i);
			if (ucindex2 == i) usecase2 = usecaseList.get(i);
		}
		if (usecase1 == null || usecase2 == null) return;

		//lb詰め込み
		List<ListBoxContent> lb = new ArrayList<>();
		int id = 0;
		for (Step s : usecase1.getAllActionStep()) {
			lb.add(new ListBoxContent(id++, s.getStepName(fgm, usecase1)));
		}
		lb1.setContents(lb);

		lb = new ArrayList<>();
		id = 0;
		for (Step s : usecase2.getAllActionStep()) {
			lb.add(new ListBoxContent(id++, s.getStepName(fgm, usecase1)));
		}
		lb2.setContents(lb);


		bsf1.draw(this);
		bsf2.draw(this);
		lb1.draw(this);
		lb2.draw(this);
	}

}
