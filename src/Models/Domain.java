package Models;

public class Domain {

	//IDと名前
	public int id;
	public String name;

	//ドメインタイプ（enumでNONE,BIDDABLE,CAUSAL,LEXICAL,SYSTEM,DESIGNED）
	public DomainType domainType;

	public enum DomainType {
		NONE {
			@Override
			public DomainType prev() {
				return DESIGNED;
			}
		},
		BIDDABLE, CAUSAL, LEXICAL, SYSTEM, DESIGNED {
			@Override
			public DomainType next() {
				return NONE;
			}
		};

		public DomainType next() {
			return values()[ordinal() + 1];
		}

		public DomainType prev() {
			return values()[ordinal() - 1];
		}

		//StringをDomaintypeにパース
		public static DomainType parse(String str) {
			switch (str) {
				case "BIDDABLE":
					return DomainType.BIDDABLE;
				case "CAUSAL":
					return DomainType.CAUSAL;
				case "LEXICAL":
					return DomainType.LEXICAL;
				case "SYSTEM":
					return DomainType.SYSTEM;
				case "DESIGNED":
					return DomainType.DESIGNED;
			}
			return NONE;
		}

		public static String toString(DomainType dt) {
			switch (dt) {
				case BIDDABLE:
					return "BIDDABLE";
				case CAUSAL:
					return "CAUSAL";
				case LEXICAL:
					return "LEXICAL";
				case SYSTEM:
					return "SYSTEM";
				case DESIGNED:
					return "DESIGNED";
			}
			return "NONE";
		}
	}

	/**
	 * ビューア表示の座標(x,y)
	 */
	public int x, y;
}
