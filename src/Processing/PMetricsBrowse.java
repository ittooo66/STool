package Processing;

import Metrics.Metrics;
import Models.Domain;
import Models.FGModel;
import Models.Step;
import Models.Usecase;
import Processing.Component.ButtonSetFrame;
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
		}, ANOS, NE {
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

		public static MetricsType parse(String str) {
			switch (str) {
				case "ACC":
					return ACC;
				case "ANOS":
					return ANOS;
				case "NE":
					return NE;
			}
			return null;
		}

		public static String toString(MetricsType mt) {
			switch (mt) {
				case ACC:
					return "ACC";
				case ANOS:
					return "ANOS";
				case NE:
					return "NE";
			}
			return null;
		}

		public static List<ListBoxContent> getList(MetricsType mt, FGModel fgm) {
			List<ListBoxContent> lbc = new ArrayList<>();
			switch (mt) {
				case ACC:
					for (Usecase uc : fgm.getUsecases()) {
						int i = Metrics.getACC(uc, fgm);
						lbc.add(new ListBoxContent(-1, uc.name + ":" + i));
					}
					break;
				case ANOS:
					for (Usecase uc : fgm.getUsecases()) {
						int i = Metrics.getANOS(uc, fgm);
						lbc.add(new ListBoxContent(-1, uc.name + ":" + i));
					}
					break;
				case NE:
					for (Domain d : fgm.getDomains()) {
						int i = Metrics.getNE(d, fgm);
						lbc.add(new ListBoxContent(-1, d.name + ":" + i));
					}
					break;
			}
			return lbc;
		}
	}

	public FGModel fgm;

	private ButtonSetFrame bsf;
	private ListBox lb;

	//カラーパレット
	private final int COLOR_BACKGROUND = color(255, 255, 255);
	private final int COLOR_LINES = color(51, 51, 51);
	private final int COLOR_SELECTED = color(57, 152, 214);

	public PMetricsBrowse(FGModel fgm) {
		this.fgm = fgm;
	}

	public void setup() {
		//Font設定。
		PFont font = createFont("メイリオ ボールド", 15, true);
		textFont(font);
		//Smoothに描画
		smooth();

		//ButtonSetFrame,ListBoxをSetup
		bsf = new ButtonSetFrame("Metrics", COLOR_BACKGROUND, COLOR_LINES, COLOR_SELECTED);
		bsf.addButton("←");
		bsf.addButton("→");
		lb = new ListBox(COLOR_BACKGROUND, COLOR_LINES, COLOR_SELECTED);

		metricsType = metricsType.ANOS;
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
		bsf.adjust(30, 30, width - 60, 30);
		bsf.draw(this);
		//ListBox詰め込んで記述
		List<ListBoxContent> lbc = MetricsType.getList(metricsType, fgm);
		lb.setContents(lbc);
		lb.adjust(30, 60, width - 60, height - 120, 30, -1);
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
