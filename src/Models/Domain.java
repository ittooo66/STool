package Models;

public class Domain implements Cloneable {

	//IDと名前
	public int id;
	public String name;

	//ドメインタイプ（enumでNONE,BIDDABLE,CAUSAL,LEXICAL,SYSTEM,DESIGNED）
	public DomainType domainType = DomainType.NONE;

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

		public static DomainType parse(String str) {
			if (str == null) return null;
			if (str.equals(getString(BIDDABLE))) {
				return BIDDABLE;
			} else if (str.equals(getString(CAUSAL))) {
				return CAUSAL;
			} else if (str.equals(getString(LEXICAL))) {
				return LEXICAL;
			} else if (str.equals(getString(SYSTEM))) {
				return SYSTEM;
			} else if (str.equals(getString(DESIGNED))) {
				return DESIGNED;
			} else if (str.equals(getString(NONE))) {
				return NONE;
			}
			return null;
		}

		public static String getString(DomainType dt) {
			if (dt == null) return null;
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
				case NONE:
					return "NONE";
			}
			return null;
		}

		public static char getPrefix(DomainType dt) {
			switch (dt) {
				case BIDDABLE:
					return 'B';
				case CAUSAL:
					return 'C';
				case LEXICAL:
					return 'X';
				case SYSTEM:
					return 'S';
				case DESIGNED:
					return 'D';
			}
			return 'N';
		}
	}

	/**
	 * ビューア表示の座標(x,y)
	 */
	public int x, y;

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
