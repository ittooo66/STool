package Models;

public class Goal {

	//IDと名前
	public int id;
	public String name;

	//このゴールを選択中かどうか
	public boolean isEnable;

	//親ゴールのID
	public int parentId;

	/**
	 * 子のタイプ（子のゴール分解のタイプ）
	 * NONE:子がいない（葉ゴール）
	 * AND:下でAND分解をしている、または子ゴール１個
	 * OR:下でOR分解をしている、または子ゴール１個
	 */
	public ChildrenType childrenType;

	public enum ChildrenType {
		NONE {
			@Override
			public ChildrenType prev() {
				return OR;
			}
		}, AND, OR {
			@Override
			public ChildrenType next() {
				return NONE;
			}
		};

		public ChildrenType next() {
			return values()[ordinal() + 1];
		}

		public ChildrenType prev() {
			return values()[ordinal() - 1];
		}

		public static ChildrenType parse(String str){
			switch (str){
				case "AND":
					return AND;
				case "OR":
					return OR;
			}
			return NONE;
		}

		public static String toString(ChildrenType ct){
			switch (ct){
				case AND:
					return "AND";
				case OR:
					return "OR";
			}
			return "NONE";
		}

	}

	//ビューア表示時の位置
	public int x, y;
}
