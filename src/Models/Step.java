package Models;


/**
 * ステップ記述内容
 * (i)ステップタイプ：GOTOのとき
 * gotoStepIdが機能する
 * (i)ステップタイプ：INCLUDEのとき
 * includeUsecaseIdが機能する
 * (i)ステップタイプ：NORMALのとき
 * subjectDomainId,objectDomainId,Eventが機能する
 */
public class Step {
	//StepのId
	public int id;

	//ステップタイプ（NORMAL,GOTO,INCLUDE）
	public StepType stepType;

	public enum StepType {
		NORMAL {
			@Override
			public StepType prev() {
				return INCLUDE;
			}
		},
		GOTO, INCLUDE {
			@Override
			public StepType next() {
				return NORMAL;
			}
		};

		public StepType next() {
			return values()[ordinal() + 1];
		}

		public StepType prev() {
			return values()[ordinal() - 1];
		}

		//StringをSteptypeに
		public static StepType parse(String str) {
			switch (str) {
				case "GOTO":
					return StepType.GOTO;
				case "INCLUDE":
					return StepType.INCLUDE;
			}
			return NORMAL;
		}

		public static String toString(StepType st) {
			switch (st) {
				case GOTO:
					return "GOTO";
				case INCLUDE:
					return "INCLUDE";
			}
			return "NORMAL";
		}
	}

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
