package Models;

import java.util.List;

public class Step implements Cloneable {
	//StepのId
	public int id;

	//nullを許さない
	public StepType stepType = StepType.NOP;

	/**
	 * ステップタイプ（ALT_INDEX,EXC_INDEX,GOTO,INCLUDE,ACTION）
	 * <p>
	 * ALT_INDEX,EXC_INDEX:代替・例外系列の頭出し
	 * >sourceStepId:起点となる主系列のステップID
	 * >condition:遷移する理由
	 * GOTO:他系列に飛ばす
	 * >gotoStepId:飛ぶ先のステップ
	 * INCLUDE:他ユースケースに飛ばす
	 * >includeUsecaseId:起動するユースケースのID
	 * ACTION:何らかの動作を行うステップ
	 * >subjectDomainId,objectDomainId,Event:
	 * NOP:空ステップ
	 */
	public enum StepType {
		ACTION, ALT_INDEX, EXC_INDEX, GOTO, INCLUDE, NOP;

		//StringをStepTypeに
		public static StepType parse(String str) {
			if (str == null) return null;
			if (str.equals(getString(ALT_INDEX))) {
				return ALT_INDEX;
			} else if (str.equals(getString(EXC_INDEX))) {
				return EXC_INDEX;
			} else if (str.equals(getString(GOTO))) {
				return GOTO;
			} else if (str.equals(getString(INCLUDE))) {
				return INCLUDE;
			} else if (str.equals(getString(ACTION))) {
				return ACTION;
			} else if (str.equals(getString(NOP))) {
				return NOP;
			}
			return null;
		}

		//StepTypeをStringに
		public static String getString(StepType st) {
			if (st == null) return null;
			switch (st) {
				case ALT_INDEX:
					return "ALT_INDEX";
				case EXC_INDEX:
					return "EXC_INDEX";
				case GOTO:
					return "GOTO";
				case INCLUDE:
					return "INCLUDE";
				case ACTION:
					return "ACTION";
				case NOP:
					return "NOP";
			}
			return null;
		}
	}

	//遷移元の主系列のステップID
	public int sourceStepId;
	//遷移する理由、条件
	public String condition;
	//GOTO先のStepID
	public int gotoStepId;
	//外部のUsecaseID
	public int includeUsecaseId;
	//主格ドメインID（主語）
	public int subjectDomainId;
	//目的格ドメインID（述語）
	public int objectDomainId;
	//イベント記述
	public String Event;

	/**
	 * ステップ名（UCEditorに表示）を出す
	 *
	 * @param fgm 本体の融合ゴールモデル
	 * @param uc  このステップが存在しているUsecase
	 * @return ステップ名
	 */
	public String getStepName(FGModelAdapter fgm, Usecase uc) {
		switch (stepType) {
			case ALT_INDEX:
				return condition;
			case EXC_INDEX:
				return condition;
			case GOTO:
				List<Step> ls = uc.getMainFlow();
				int i = 1;
				for (Step s : ls) {
					if (s.id == this.gotoStepId) {
						return "GOTO:MAIN" + i;
					}
					i++;
				}
				return "WHERE YOU GOTO ?";
			case INCLUDE:
				for (Usecase u : fgm.getUsecases()) {
					if (u.id == includeUsecaseId) {
						return "UC:" + u.name + "を起動";
					}
				}
				return "INCLUDE NOTHING";
			case ACTION:
				Domain obj = fgm.getDomainById(objectDomainId);
				Domain sbj = fgm.getDomainById(subjectDomainId);
				if (obj != null && sbj != null)
					return sbj.name + "-> " + Event + "-> " + obj.name;
				else
					return "NO ACTION";
			case NOP:
				return "NOP";
		}
		return "ILLEGAL STEP TYPE";
	}

	@Override
	public Object clone() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}
}
