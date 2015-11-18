package Models;

public class Goal implements Cloneable {

	//IDと名前
	public int id;
	public String name;

	//このゴールを選択中かどうか
	public boolean isEnableForAsIs;
	public boolean isEnableForToBe;

	//親ゴールのID
	public int parentId;

	/**
	 * 子のタイプ（子のゴール分解のタイプ）
	 * LEAF:葉ゴール（子なし）
	 * AND:下でAND分解をしている、または子ゴール１個
	 * OR:下でOR分解をしている、または子ゴール１個
	 */
	public ChildrenType childrenType;

	public enum ChildrenType {
		LEAF {
			@Override
			public ChildrenType prev() {
				return OR;
			}
		}, AND, OR {
			@Override
			public ChildrenType next() {
				return LEAF;
			}
		};

		public ChildrenType next() {
			return values()[ordinal() + 1];
		}

		public ChildrenType prev() {
			return values()[ordinal() - 1];
		}

		public static ChildrenType parse(String str) {
			if (str == null) return null;
			if (str.equals(getString(AND))) {
				return AND;
			} else if (str.equals(getString(OR))) {
				return OR;
			} else if (str.equals(getString(LEAF))) {
				return LEAF;
			}
			return null;
		}

		public static String getString(ChildrenType ct) {
			if (ct == null) return null;
			switch (ct) {
				case AND:
					return "And";
				case OR:
					return "Or";
				case LEAF:
					return "Leaf";
			}
			return null;
		}

	}

	//ビューア表示時の位置
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
