package Processing;

import Metrics.Metrics;
import Metrics.CE;
import Models.*;
import Models.FGModelAdapter.VERSION;
import Processing.Component.ButtonSetFrame;
import Processing.Component.ListBox;
import Processing.Component.ListBoxContent;
import processing.core.PApplet;
import processing.core.PFont;
import processing.event.MouseEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

public class PMetricsBrowse extends PApplet {

	//バージョン指定
	private MetricsType metricsType;

	public enum MetricsType {
		ACC_ASIS {
			@Override
			public MetricsType prev() {
				return NE_DIFF;
			}

			public String toString() {
				return "ACC(As-Is)";
			}
		}, ACC_TOBE {
			public String toString() {
				return "ACC(To-Be)";
			}
		}, ANOS_ASIS {
			public String toString() {
				return "ANOS(As-Is)";
			}
		}, ANOS_TOBE {
			public String toString() {
				return "ANOS(To-Be)";
			}
		}, NOS_ASIS {
			public String toString() {
				return "NOS(As-Is)";
			}
		}, NOS_TOBE {
			public String toString() {
				return "NOS(To-Be)";
			}
		}, ASPG_ASIS {
			public String toString() {
				return "ASPG(As-Is)";
			}
		}, ASPG_TOBE {
			public String toString() {
				return "ASPG(To-Be)";
			}
		}, ASPG_DIFF {
			public String toString() {
				return "ASPG Diff(AsIs-ToBe)";
			}
		}, SPG_ASIS {
			public String toString() {
				return "SPG(As-Is)";
			}
		}, SPG_TOBE {
			public String toString() {
				return "SPG(To-Be)";
			}
		}, SPG_DIFF {
			public String toString() {
				return "SPG(AsIs-ToBe)";
			}
		}, UCP {
			public String toString() {
				return "Usecase Point";
			}
		}, CE_ASIS {
			public String toString() {
				return "CE(As-Is)";
			}
		}, CE_TOBE {
			public String toString() {
				return "CE(To-Be)";
			}
		}, CE_DIFF {
			public String toString() {
				return "CE Diff(AsIs-ToBe)";
			}
		}, NE_ASIS {
			public String toString() {
				return "NE(As-Is)";
			}
		}, NE_TOBE {
			public String toString() {
				return "NE(To-Be)";
			}
		}, NE_DIFF {
			@Override
			public MetricsType next() {
				return ACC_ASIS;
			}

			public String toString() {
				return "NE Diff (AsIs-ToBe)";
			}
		};

		public MetricsType next() {
			return values()[ordinal() + 1];
		}

		public MetricsType prev() {
			return values()[ordinal() - 1];
		}


		public static List<ListBoxContent> getList(MetricsType mt, FGModelAdapter fgm, Scenario scenario) {
			//詰め込み対象とIDを用意
			List<ListBoxContent> lbc = new ArrayList<>();
			int id = 0;

			//現在のバージョンを退避
			VERSION ver = fgm.getVersion();

			switch (mt) {
				case ACC_ASIS:
					//バージョン設定
					fgm.setVersion(VERSION.ASIS);
					for (Usecase uc : fgm.getUsecases()) {
						//ACC値取得
						int param = Metrics.getACC(uc, fgm);
						//Enable値取得
						boolean isEnable = fgm.getGoalById(uc.parentLeafGoalId).isEnableForAsIs;
						//追加
						lbc.add(new ListBoxContent(id++, uc.name, param, isEnable));
					}
					break;
				case ACC_TOBE:
					fgm.setVersion(VERSION.TOBE);
					for (Usecase uc : fgm.getUsecases()) {
						int param = Metrics.getACC(uc, fgm);
						boolean isEnable = fgm.getGoalById(uc.parentLeafGoalId).isEnableForToBe;
						lbc.add(new ListBoxContent(id++, uc.name, param, isEnable));
					}
					break;
				case ANOS_ASIS:
					fgm.setVersion(VERSION.ASIS);
					for (Usecase uc : fgm.getUsecases()) {
						int param = Metrics.getANOS(uc, fgm);
						boolean isEnable = fgm.getGoalById(uc.parentLeafGoalId).isEnableForAsIs;
						lbc.add(new ListBoxContent(id++, uc.name, param, isEnable));
					}
					break;
				case ANOS_TOBE:
					fgm.setVersion(VERSION.TOBE);
					for (Usecase uc : fgm.getUsecases()) {
						int param = Metrics.getANOS(uc, fgm);
						boolean isEnable = fgm.getGoalById(uc.parentLeafGoalId).isEnableForToBe;
						lbc.add(new ListBoxContent(id++, uc.name, param, isEnable));
					}
					break;
				case NOS_ASIS:
					fgm.setVersion(VERSION.ASIS);
					for (Usecase uc : fgm.getUsecases()) {
						int param = Metrics.getNOS(uc, fgm);
						boolean isEnable = fgm.getGoalById(uc.parentLeafGoalId).isEnableForAsIs;
						lbc.add(new ListBoxContent(id++, uc.name, param, isEnable));
					}
					break;
				case NOS_TOBE:
					fgm.setVersion(VERSION.TOBE);
					for (Usecase uc : fgm.getUsecases()) {
						int param = Metrics.getNOS(uc, fgm);
						boolean isEnable = fgm.getGoalById(uc.parentLeafGoalId).isEnableForToBe;
						lbc.add(new ListBoxContent(id++, uc.name, param, isEnable));
					}
					break;
				case SPG_ASIS:
					fgm.setVersion(VERSION.ASIS);
					for (Goal g : fgm.getGoals()) {
						int param = Metrics.getSPG(g, fgm);
						boolean isEnable = g.isEnableForAsIs;
						lbc.add(new ListBoxContent(id++, g.name, param, isEnable));
					}
					break;
				case SPG_TOBE:
					fgm.setVersion(VERSION.TOBE);
					for (Goal g : fgm.getGoals()) {
						int param = Metrics.getSPG(g, fgm);
						boolean isEnable = g.isEnableForToBe;
						lbc.add(new ListBoxContent(id++, g.name, param, isEnable));
					}
					break;
				case SPG_DIFF:
					for (Goal g : fgm.getGoals()) {
						fgm.setVersion(VERSION.ASIS);
						int paramAsis = Metrics.getSPG(g, fgm);
						fgm.setVersion(VERSION.TOBE);
						int paramTobe = Metrics.getSPG(g, fgm);
						boolean isEnable = fgm.getVersion() == VERSION.ASIS ? g.isEnableForAsIs : g.isEnableForToBe;
						lbc.add(new ListBoxContent(id++, g.name, paramAsis - paramTobe, isEnable));
					}
					break;
				case ASPG_ASIS:
					fgm.setVersion(VERSION.ASIS);
					for (Goal g : fgm.getGoals()) {
						int param = Metrics.getASPG(g, fgm);
						boolean isEnable = g.isEnableForAsIs;
						lbc.add(new ListBoxContent(id++, g.name, param, isEnable));
					}
					break;
				case ASPG_TOBE:
					fgm.setVersion(VERSION.TOBE);
					for (Goal g : fgm.getGoals()) {
						int param = Metrics.getASPG(g, fgm);
						boolean isEnable = g.isEnableForToBe;
						lbc.add(new ListBoxContent(id++, g.name, param, isEnable));
					}
					break;
				case ASPG_DIFF:
					for (Goal g : fgm.getGoals()) {
						fgm.setVersion(VERSION.ASIS);
						int paramAsis = Metrics.getASPG(g, fgm);
						fgm.setVersion(VERSION.TOBE);
						int paramTobe = Metrics.getASPG(g, fgm);
						boolean isEnable = fgm.getVersion() == VERSION.ASIS ? g.isEnableForAsIs : g.isEnableForToBe;
						lbc.add(new ListBoxContent(id++, g.name, paramAsis - paramTobe, isEnable));
					}
					break;
				case NE_ASIS:
					fgm.setVersion(VERSION.ASIS);
					for (Domain d : fgm.getDomains()) {
						String name = Domain.DomainType.getPrefix(d.domainType) + ":" + d.name;
						int param = Metrics.getNE(d, fgm);
						boolean isBold = d.domainType == Domain.DomainType.BIDDABLE;
						lbc.add(new ListBoxContent(id++, name, param, isBold));
					}
					break;
				case NE_TOBE:
					fgm.setVersion(VERSION.TOBE);
					for (Domain d : fgm.getDomains()) {
						String name = Domain.DomainType.getPrefix(d.domainType) + ":" + d.name;
						int param = Metrics.getNE(d, fgm);
						boolean isBold = d.domainType == Domain.DomainType.BIDDABLE;
						lbc.add(new ListBoxContent(id++, name, param, isBold));
					}
					break;
				case NE_DIFF:
					for (Domain d : fgm.getDomains()) {
						fgm.setVersion(VERSION.ASIS);
						int paramAsis = Metrics.getNE(d, fgm);
						fgm.setVersion(VERSION.TOBE);
						int paramTobe = Metrics.getNE(d, fgm);
						String name = Domain.DomainType.getPrefix(d.domainType) + ":" + d.name;
						boolean isBold = d.domainType == Domain.DomainType.BIDDABLE;
						lbc.add(new ListBoxContent(id++, name, paramAsis - paramTobe, isBold));
					}
					break;
				case CE_ASIS:
					//AsIsにスイッチ
					boolean sw = false;
					if (!scenario.isAsIs()) {
						scenario.switchVersion();
						sw = true;
					}

					//CEList取得して詰め込み
					for (CE ce : Metrics.getCEs(scenario)) {
						String name = fgm.getDomainById(ce.subjectDomainId).name + " -> " + ce.event + " -> " + fgm.getDomainById(ce.objectDomainId).name;
						lbc.add(new ListBoxContent(id++, name, ce.count, false));
					}

					//変更した場合もとにもどす
					if (sw) scenario.switchVersion();

					break;
				case CE_TOBE:
					//ToBeにスイッチ
					sw = false;
					if (scenario.isAsIs()) {
						scenario.switchVersion();
						sw = true;
					}

					//CEList取得して詰め込み
					for (CE ce : Metrics.getCEs(scenario)) {
						String name = fgm.getDomainById(ce.subjectDomainId).name + " -> " + ce.event + " -> " + fgm.getDomainById(ce.objectDomainId).name;
						lbc.add(new ListBoxContent(id++, name, ce.count, false));
					}

					//変更した場合もとにもどす
					if (sw) scenario.switchVersion();

					break;
				case CE_DIFF:
					//AsIs,ToBeのCE取得
					List<CE> ceListAsIs = null, ceListToBe = null;
					if (scenario.isAsIs()) {
						ceListAsIs = Metrics.getCEs(scenario);
						scenario.switchVersion();
						ceListToBe = Metrics.getCEs(scenario);
					} else {
						ceListToBe = Metrics.getCEs(scenario);
						scenario.switchVersion();
						ceListAsIs = Metrics.getCEs(scenario);
					}
					//変更したのでもとにもどす
					scenario.switchVersion();

					//CEList取得して詰め込み
					for (CE ce : ceListAsIs) {
						//名前生成
						String name = fgm.getDomainById(ce.subjectDomainId).name + " -> " + ce.event + " -> " + fgm.getDomainById(ce.objectDomainId).name;

						//CEの差分取得
						int diffCount = ce.count;
						CE diffCE = CE.extractCE(ce, ceListToBe);
						if (diffCE != null) {
							diffCount -= diffCE.count;
						}

						//追加
						lbc.add(new ListBoxContent(id++, name, diffCount, false));
					}
					//CEList取得して詰め込み（AsIsになかったやつを負数として追加）
					for (CE ce : ceListToBe) {
						//名前生成
						String name = fgm.getDomainById(ce.subjectDomainId).name + " -> " + ce.event + " -> " + fgm.getDomainById(ce.objectDomainId).name;
						//CE差分
						int diffCount = -ce.count;
						//追加
						lbc.add(new ListBoxContent(id++, name, diffCount, false));
					}

					break;
				case UCP:
					fgm.setVersion(VERSION.ASIS);
					int paramAsis = Metrics.getUCP(fgm);
					lbc.add(new ListBoxContent(id++, "Usecase Point(As-Is)", paramAsis));
					fgm.setVersion(VERSION.TOBE);
					int paramTobe = Metrics.getUCP(fgm);
					lbc.add(new ListBoxContent(id++, "Usecase Point(To-Be)", paramTobe));
					int paramDiff = paramAsis - paramTobe;
					lbc.add(new ListBoxContent(id++, "Usecase Point Diff(AsIs - ToBe)", paramDiff));
					break;
			}

			//バージョン戻し
			fgm.setVersion(ver);

			return lbc;
		}

		public static MetricsType parse(String name) {
			MetricsType mt = ACC_ASIS;
			do {
				if (mt.toString().equals(name)) return mt;
				mt = mt.next();
			} while (mt != ACC_ASIS);
			return null;
		}
	}

	public FGModelAdapter fgm;
	public Scenario scenario;

	private ButtonSetFrame bsf;
	private ListBox metricsSelectorLb, lb;

	private SortType sortType = SortType.SYOUJUN;

	private enum SortType {
		SYOUJUN {
			public void sort(List<ListBoxContent> lbc) {
				lbc.sort((o1, o2) -> o1.param - o2.param);
			}
		}, KOUJUN {
			public void sort(List<ListBoxContent> lbc) {
				lbc.sort((o1, o2) -> o2.param - o1.param);
			}
		}, NAMAEJUN {
			public void sort(List<ListBoxContent> lbc) {
				lbc.sort((o1, o2) -> o1.name.compareTo(o2.name));
			}
		};

		public void sort(List<ListBoxContent> lbc) {
			return;
		}
	}

	public PMetricsBrowse(FGModelAdapter fgm, Scenario scenario) {
		this.fgm = fgm;
		this.scenario = scenario;
	}

	public void setup() {
		//Font設定。
		PFont font = createFont("メイリオ ボールド", 15, true);
		textFont(font);
		//Smoothに描画
		smooth();

		//ButtonSetFrame,ListBoxをSetup
		bsf = new ButtonSetFrame("Metrics");
		bsf.addButton("↑");
		bsf.addButton("↓");
		bsf.addButton("N");

		lb = new ListBox();
		lb.setActiveOfParams(true);
		lb.setSelectable(false);
		metricsSelectorLb = new ListBox();

		//metricsType初期値設定
		metricsType = MetricsType.ANOS_ASIS;

		redraw();
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

		List<ListBoxContent> lbc = new ArrayList<>();
		//MetricsTypeをlbcにつめこみ
		MetricsType mt = MetricsType.ACC_ASIS;
		int id = 0, selectedId = 0;
		do {
			lbc.add(new ListBoxContent(id, mt.toString()));
			if (mt.equals(metricsType)) selectedId = id;
			mt = mt.next();
			id++;
		} while (mt != MetricsType.ACC_ASIS);
		metricsSelectorLb.setContents(lbc);
		metricsSelectorLb.adjust(20, 50, 200, height - 70, 30, selectedId);
		metricsSelectorLb.draw(this);

		//ListBox取得してソート
		lbc = MetricsType.getList(metricsType, fgm, scenario);
		sortType.sort(lbc);

		//ListBox詰め込んでDraw
		lb.setContents(lbc);
		lb.adjust(20 + 200, 50, width - 240, height - 70, 30, -1);
		lb.draw(this);
	}

	public void mousePressed() {
		switch (bsf.getButtonIdOnMouse(mouseX, mouseY)) {
			case 0:
				sortType = SortType.SYOUJUN;
				break;
			case 1:
				sortType = SortType.KOUJUN;
				break;
			case 2:
				sortType = SortType.NAMAEJUN;
				break;
		}

		ListBoxContent lbc = metricsSelectorLb.getContentOnMouse(mouseX, mouseY);
		if (lbc != null) metricsType = MetricsType.parse(lbc.name);
		redraw();
	}

	public void mouseWheel(MouseEvent event) {
		int e = event.getCount() > 0 ? 1 : -1;
		if (lb.isOn(mouseX, mouseY)) lb.scroll(e);
		if (metricsSelectorLb.isOn(mouseX, mouseY)) metricsSelectorLb.scroll(e);
		redraw();
	}

	public void mouseMoved() {
		redraw();
	}

}
