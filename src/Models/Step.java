package Models;

public class Step {
	//StepのId
	public int id;


	public StepType stepType;

	/**
	 * ステップタイプ（ALT_INDEX,EXCEP_INDEX,GOTO,INCLUDE,ACTION）
	 *
	 * ALT_INDEX,EXCEP_INDEX:代替・例外系列の頭出し
	 * 	>sourceStepId:起点となる主系列のステップID
	 * GOTO:他系列に飛ばす
	 * 	>gotoStepId:飛ぶ先のステップ
	 * INCLUDE:他ユースケースに飛ばす
	 * 	>includeUsecaseId:起動するユースケースのID
	 * ACTION:何らかの動作を行うステップ
	 * 	>subjectDomainId,objectDomainId,Event:
	 */
	public enum StepType {
		ACTION {
			@Override
			public StepType prev() {
				return INCLUDE;
			}
		},
		ALT_INDEX, EXC_INDEX, GOTO, INCLUDE {
			@Override
			public StepType next() {
				return ACTION;
			}
		};

		public StepType next() {
			return values()[ordinal() + 1];
		}

		public StepType prev() {
			return values()[ordinal() - 1];
		}

		//StringをStepTypeに
		public static StepType parse(String str) {
			switch (str) {
				case "ALT_INDEX":
					return StepType.ALT_INDEX;
				case "EXC_INDEX":
					return StepType.EXC_INDEX;
				case "GOTO":
					return StepType.GOTO;
				case "INCLUDE":
					return StepType.INCLUDE;
				case "ACTION":
					return StepType.ACTION;
			}
			return null;
		}
		//StepTypeをStringに
		public static String toString(StepType st) {
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
}
