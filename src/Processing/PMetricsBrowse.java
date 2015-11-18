package Processing;

import Metrics.Metrics;
import Models.Domain;
import Models.FGModelAdapter;
import Models.Usecase;
import Processing.Component.ButtonSetFrame;
import Processing.Component.COLOR;
import Processing.Component.ListBox;
import Processing.Component.ListBoxContent;
import processing.core.PApplet;
import processing.core.PFont;

import java.util.ArrayList;
import java.util.List;

public class PMetricsBrowse extends PApplet {

	//バージョン指定
	private MetricsType metricsType;

	public enum MetricsType {
		ACC {
			@Override
			public MetricsType prev() {
				return NE;
			}
		}, ANOS, UCP, NE {
			@Override
			public MetricsType next() {
				return ACC;
			}
		};

		public MetricsType next() {
			return values()[ordinal() + 1];
		}

		public MetricsType prev() {
			return values()[ordinal() - 1];
		}


		public static List<ListBoxContent> getList(MetricsType mt, FGModelAdapter fgm) {
			List<ListBoxContent> lbc = new ArrayList<>();
			int id = 0;
			switch (mt) {
				case ACC:
					for (Usecase uc : fgm.getUsecases()) {
						int param = Metrics.getACC(uc, fgm);
						boolean isEnable = fgm.getVersion() == FGModelAdapter.VERSION.ASIS ? fgm.getGoalById(uc.parentLeafGoalId).isEnableForAsIs : fgm.getGoalById(uc.parentLeafGoalId).isEnableForToBe;
						lbc.add(new ListBoxContent(id++, uc.name, param, isEnable));
					}
					break;
				case ANOS:
					for (Usecase uc : fgm.getUsecases()) {
						int param = Metrics.getANOS(uc, fgm);
						boolean isEnable = fgm.getVersion() == FGModelAdapter.VERSION.ASIS ? fgm.getGoalById(uc.parentLeafGoalId).isEnableForAsIs : fgm.getGoalById(uc.parentLeafGoalId).isEnableForToBe;
						lbc.add(new ListBoxContent(id++, uc.name, param, isEnable));
					}
					break;
				case NE:
					for (Domain d : fgm.getDomains()) {
						int param = Metrics.getNE(d, fgm);
						lbc.add(new ListBoxContent(id++, Domain.DomainType.getPrefix(d.domainType) + ":" + d.name, param));
					}
					break;
				case UCP:
					int param = Metrics.getUCP(fgm);
					lbc.add(new ListBoxContent(id++, "Usecase Point", param));
			}
			return lbc;
		}
	}

	public FGModelAdapter fgm;

	private ButtonSetFrame bsf;
	private ListBox lb;

	public PMetricsBrowse(FGModelAdapter fgm) {
		this.fgm = fgm;
	}

	public void setup() {
		//Font設定。
		PFont font = createFont("メイリオ ボールド", 15, true);
		textFont(font);
		//Smoothに描画
		smooth();

		//ButtonSetFrame,ListBoxをSetup
		bsf = new ButtonSetFrame("Metrics");
		bsf.addButton("←");
		bsf.addButton("→");
		lb = new ListBox();

		metricsType = MetricsType.ANOS;
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

		background(255);

		//Buttonsetframe更新して記述
		bsf.setTitle(metricsType.toString());
		bsf.adjust(20, 20, width - 60, 30);
		bsf.draw(this);
		//ListBox詰め込んで記述
		List<ListBoxContent> lbc = MetricsType.getList(metricsType, fgm);
		lb.setContents(lbc);
		lb.adjust(20, 50, width - 40, height - 70, 30, -1);
		lb.draw(this);
	}

	public void mousePressed() {
		//マウス押下位置
		int x = mouseX;
		int y = mouseY;

		//ButtonSetFrame押下判定
		switch (bsf.getButtonIdOnMouse(x, y)) {
			case 0://左移動
				metricsType = metricsType.prev();
				break;
			case 1://右移動
				metricsType = metricsType.next();
				break;
		}
		redraw();
	}

	public void mouseMoved() {
		redraw();
	}

}
