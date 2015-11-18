package Processing.Component;

/**
 * ListBoxに詰めるコンテンツ
 */
public class ListBoxContent {
	public int id;
	public String name;

	//塗りつぶすかどうか
	public boolean isBold;

	//こいつのパラメータ
	public int param;

	public ListBoxContent(int id, String name) {
		this.id = id;
		this.name = name;
		this.isBold = false;
		this.param = -1;
	}

	public ListBoxContent(int id, String name, boolean isBold) {
		this.id = id;
		this.name = name;
		this.isBold = isBold;
		this.param = -1;
	}

	public ListBoxContent(int id, String name, int param) {
		this.id = id;
		this.name = name;
		this.isBold = false;
		this.param = param;
	}
}