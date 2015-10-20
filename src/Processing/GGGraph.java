package Processing;

import Models.*;
import Core.*;
import processing.core.PApplet;
import processing.core.PFont;


/**
 * ゴールグラフエディタタブで出力するProcessing部分
 */
public class GGGraph extends PApplet {

	//選択中のゴールID（-1なら非選択）
	public int selectedGoalId = -1;

	//TODO:PRO MODE
	//public boolean isProMode;

	//本体
	private SToolEditor sToolEditor;

	//カラーパレット
	private final int COLOR_BACKGROUND = color(28, 28, 28);
	private final int COLOR_LINES = color(123, 144, 210);
	private final int COLOR_SELECTED = color(226, 148, 59);

	public GGGraph(SToolEditor sToolEditor) {
		this.sToolEditor = sToolEditor;
	}

	public void setup() {
		//とりあえず適当な解像度で初期化
		size(1024, 768);
		//CPU節約
		noLoop();
		//Font設定。
		PFont font = createFont("メイリオ ボールド", 15, true);
		textFont(font);
	}

	public void draw() {
		//背景描画
		background(COLOR_BACKGROUND);

		//記述の下準備
		stroke(COLOR_LINES);
		strokeWeight(2);
		noFill();

		//ANDArc描画
		for (Goal parentGoal : sToolEditor.fgm.getGoals()) {
			if (parentGoal.childrenType.equals(Goal.ChildrenType.AND)) {

				//子ゴールカウント
				int countOfChildGoals = 0;
				//開始角度・終了角度を用意
				float rootR = 0, distR = PI * 2;

				//AND記述対象の子ゴール捜索
				for (Goal childGoal : sToolEditor.fgm.getGoals()) {
					if (childGoal.parentId == parentGoal.id) {

						//ゴール角度
						float childR = getRadian(parentGoal, childGoal);

						//1st Goalのとき、とりあえずtemp にいれちゃう
						if (countOfChildGoals == 0) {
							rootR = childR;
							distR = childR;
						} else if (rootR > childR) {
							rootR = childR;
						} else if (distR < childR) {
							distR = childR;
						}
						countOfChildGoals++;
					}
				}

				//ゴールカウントが２以上で線引き
				if (countOfChildGoals > 1) {
					arc(parentGoal.x, parentGoal.y, textWidth(parentGoal.name) + 40 + 30, 40 + 30, rootR, distR);
				}
			}
		}

		//枝を描画
		fill(COLOR_LINES);
		for (Goal childGoal : sToolEditor.fgm.getGoals()) {
			if (childGoal.parentId != -1) {
				//親ゴール取得
				Goal parentGoal = sToolEditor.fgm.getGoalById(childGoal.parentId);

				//（子||親）から見た（親||子）ゴールの方角
				float childR = getRadian(childGoal, parentGoal);
				float parentR = getRadian(parentGoal, childGoal);
				//ゴール楕円上のx,y座標（親x,yと子x,y）
				float xC = ((textWidth(childGoal.name) + 40) / 2) * cos(childR);
				float yC = (40 / 2) * sin(childR);
				float xP = ((textWidth(parentGoal.name) + 40) / 2) * cos(parentR);
				float yP = (40 / 2) * sin(parentR);

				if (match(childGoal.name, "\n") == null) {//子ゴールの名前が一行
					//枝引き
					line(parentGoal.x + xP, parentGoal.y + yP, childGoal.x + xC, childGoal.y + yC);
					ellipse(childGoal.x + xC, childGoal.y + yC, 10, 10);
				} else {//子ゴールの名前が二行以上
					String[] texts = splitTokens(childGoal.name, "\n");

					//ゴール中心（child.x,child.y）から、ゴール縁のx,y座標を計算
					float x, y;
					float w = textWidth(childGoal.name) + 40;//ゴールの幅
					float h = texts.length * 16 + 20;//ゴールの高さ

					if (-(h / w) < tan(childR) && tan(childR) < (h / w)) {
						x = w / 2;
						y = (w / 2) * tan(childR);
						if (PI / 2 < childR && childR < 3 * PI / 2) {
							x = -x;
							y = -y;
						}
					} else {
						x = -(h / 2) * tan(PI / 2 - childR);
						y = -h / 2;
						if (0 < childR && childR < PI) {
							x = -x;
							y = -y;
						}
					}

					//枝引き
					line(xP + parentGoal.x, yP + parentGoal.y, x + childGoal.x, y + childGoal.y);
					ellipse(childGoal.x + x, childGoal.y + y, 10, 10);
				}
			}
		}

		//各ゴールを描画
		textAlign(CENTER, CENTER);
		for (Goal g : sToolEditor.fgm.getGoals()) {

			//fill変更(リーフか否か)
			fill(g.childrenType.equals(Goal.ChildrenType.LEAF) ? COLOR_LINES : COLOR_BACKGROUND);
			//stroke変更(選択中か否か)
			stroke(g.id == selectedGoalId ? COLOR_SELECTED : COLOR_LINES);

			//ゴール名が１行の場合
			if (match(g.name, "\n") == null) {
				//わっか記述
				ellipse(g.x, g.y, textWidth(g.name) + 40, 40);

				//fill変更(選択中か否か、リーフか否か)
				fill(g.id == selectedGoalId ? COLOR_SELECTED : g.childrenType.equals(Goal.ChildrenType.LEAF) ? COLOR_BACKGROUND : COLOR_LINES);

				//名前の記述
				text(g.name, g.x, g.y - 2);
			} else {//ゴール名が二行以上のとき
				//テキストをSplit
				String[] texts = splitTokens(g.name, "\n");

				//枠記述
				rect(g.x - (textWidth(g.name) + 40) / 2, g.y - texts.length * 8 - 10, textWidth(g.name) + 40, texts.length * 16 + 20, 8);

				//fill変更(選択中か否か、リーフか否か)
				fill(g.id == selectedGoalId ? COLOR_SELECTED : g.childrenType.equals(Goal.ChildrenType.LEAF) ? COLOR_BACKGROUND : COLOR_LINES);

				//名前の記述
				for (int i = 0; i < texts.length; i++) {
					text(texts[i], g.x, (g.y - 2) - texts.length * 8 + 10 + i * 16);
				}
			}
		}
	}

	/**
	 * ２つのゴールのなす角度を返す
	 *
	 * @param root 基点ゴール
	 * @param dist 終点ゴール
	 * @return rootから見てdistがどこの方面（ラジアン）にあるかを返す。
	 * 東：0 (2*PI)
	 * 南：PI/2
	 * 西：PI
	 * 北：3*PI/2
	 * となる。arc()記述用のラジアンに対応している
	 */
	private float getRadian(Goal root, Goal dist) {
		//枝の刺さる角度
		float radian = (PI / 2) + atan((float) (dist.y - root.y) / (float) (dist.x - root.x));
		//第２、３象限のとき、atan()の都合上修正噛ます
		if (root.x > dist.x) radian += PI;
		if (radian < PI / 2) {
			radian += 3 * PI / 2;
		} else {
			radian -= PI / 2;
		}
		return radian;
	}

	public void mousePressed() {
		final int goalClickMergin = 40;

		//ドメイン選択の一時解除
		selectedGoalId = -1;
		for (Goal g : sToolEditor.fgm.getGoals()) {
			//マウスクリック範囲にドメインがあれば、それを選択
			if (g.x - goalClickMergin < mouseX && mouseX < g.x + goalClickMergin &&
					g.y - goalClickMergin < mouseY && mouseY < g.y + goalClickMergin) {
				selectedGoalId = g.id;
			}
		}
		sToolEditor.redraw();
	}

	public void mouseDragged() {
		if (mouseButton == LEFT && selectedGoalId != -1) {
			Goal g = sToolEditor.fgm.getGoalById(selectedGoalId);
			if (g != null && 0 < mouseX && mouseX < width && 0 < mouseY && mouseY < height)
				sToolEditor.fgm.moveGoal(selectedGoalId, mouseX, mouseY);
		}
		sToolEditor.redraw();
	}
}
