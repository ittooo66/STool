package Processing;

import Metrics.Metrics;
import Models.Domain;
import Models.FGModelAdapter;
import Models.Usecase;
import Processing.Component.ButtonSetFrame;
import Processing.Component.COLOR;
import Processing.Component.ListBox;
import Processing.Component.ListBoxContent;
import com.sun.deploy.util.ParameterUtil;
import com.sun.deploy.util.StringUtils;
import processing.core.PApplet;
import processing.core.PFont;
import processing.event.MouseEvent;

import java.util.ArrayList;
import java.util.List;

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
		}, UCP {
			public String toString() {
				return "Usecase Point";
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


		public static List<ListBoxContent> getList(MetricsType mt, FGModelAdapter fgm) {
			//詰め込み対象とIDを用意
			List<ListBoxContent> lbc = new ArrayList<>();
			int id = 0;

			//現在のバージョンを退避
			FGModelAdapter.VERSION ver = fgm.getVersion();

			switch (mt) {
				case ACC_ASIS:
					//バージョン設定
					fgm.setVersion(FGModelAdapter.VERSION.ASIS);
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
					fgm.setVersion(FGModelAdapter.VERSION.TOBE);
					for (Usecase uc : fgm.getUsecases()) {
						int param = Metrics.getACC(uc, fgm);
						boolean isEnable = fgm.getGoalById(uc.parentLeafGoalId).isEnableForToBe;
						lbc.add(new ListBoxContent(id++, uc.name, param, isEnable));
					}
					break;
				case ANOS_ASIS:
					fgm.setVersion(FGModelAdapter.VERSION.ASIS);
					for (Usecase uc : fgm.getUsecases()) {
						int param = Metrics.getANOS(uc, fgm);
						boolean isEnable = fgm.getGoalById(uc.parentLeafGoalId).isEnableForAsIs;
						lbc.add(new ListBoxContent(id++, uc.name, param, isEnable));
					}
					break;
				case ANOS_TOBE:
					fgm.setVersion(FGModelAdapter.VERSION.TOBE);
					for (Usecase uc : fgm.getUsecases()) {
						int param = Metrics.getANOS(uc, fgm);
						boolean isEnable = fgm.getGoalById(uc.parentLeafGoalId).isEnableForToBe;
						lbc.add(new ListBoxContent(id++, uc.name, param, isEnable));
					}
					break;
				case NE_ASIS:
					fgm.setVersion(FGModelAdapter.VERSION.ASIS);
					for (Domain d : fgm.getDomains()) {
						String name = Domain.DomainType.getPrefix(d.domainType) + ":" + d.name;
						int param = Metrics.getNE(d, fgm);
						boolean isBold = d.domainType == Domain.DomainType.BIDDABLE;
						lbc.add(new ListBoxContent(id++, name, param, isBold));
					}
					break;
				case NE_TOBE:
					fgm.setVersion(FGModelAdapter.VERSION.TOBE);
					for (Domain d : fgm.getDomains()) {
						String name = Domain.DomainType.getPrefix(d.domainType) + ":" + d.name;
						int param = Metrics.getNE(d, fgm);
						boolean isBold = d.domainType == Domain.DomainType.BIDDABLE;
						lbc.add(new ListBoxContent(id++, name, param, isBold));
					}
					break;
				case NE_DIFF:
					for (Domain d : fgm.getDomains()) {
						fgm.setVersion(FGModelAdapter.VERSION.ASIS);
						int paramAsis = Metrics.getNE(d, fgm);
						fgm.setVersion(FGModelAdapter.VERSION.TOBE);
						int paramTobe = Metrics.getNE(d, fgm);
						String name = Domain.DomainType.getPrefix(d.domainType) + ":" + d.name;
						boolean isBold = d.domainType == Domain.DomainType.BIDDABLE;
						lbc.add(new ListBoxContent(id++, name, paramAsis - paramTobe, isBold));
					}
					break;
				case UCP:
					fgm.setVersion(FGModelAdapter.VERSION.ASIS);
					int paramAsis = Metrics.getUCP(fgm);
					lbc.add(new ListBoxContent(id++, "Usecase Point(As-Is)", paramAsis));
					fgm.setVersion(FGModelAdapter.VERSION.TOBE);
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

		metricsType = MetricsType.ANOS_ASIS;
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

	public void mouseWheel(MouseEvent event) {
		int e = event.getCount() > 0 ? 1 : -1;
		if (lb.isOn(mouseX, mouseY)) lb.scroll(e);
		redraw();
	}

	public void mouseMoved() {
		redraw();
	}

}
