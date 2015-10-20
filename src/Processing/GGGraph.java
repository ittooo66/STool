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

		//枝周り記述の下準備
		stroke(COLOR_LINES);
		strokeWeight(2);
		noFill();

		//AND記述対象のゴール捜索
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

					//ゴールカウントが２以上で線引き
					if (countOfChildGoals > 1) {
						//arcずれは円形arcで一旦対処
						arc(parentGoal.x, parentGoal.y, textWidth(parentGoal.name) + 40 + 30, 40 + 30, rootR, distR);
					}
				}
			}
		}

		//枝を描画
		fill(COLOR_LINES);
		for (Goal childGoal : sToolEditor.fgm.getGoals()) {
			if (childGoal.parentId != -1) {
				//親ゴール取得
				Goal parentGoal = sToolEditor.fgm.getGoalById(childGoal.parentId);

				//ゴールの方角
				float childR = getRadian(childGoal, parentGoal);
				float parentR = getRadian(parentGoal, childGoal);

				//x,y座標系
				float xC = ((textWidth(childGoal.name) + 40) / 2) * cos(childR);
				float yC = (40 / 2) * sin(childR);
				float xP = ((textWidth(parentGoal.name) + 40) / 2) * cos(parentR);
				float yP = (40 / 2) * sin(parentR);

				line(xP + parentGoal.x, yP + parentGoal.y, xC + childGoal.x, yC + childGoal.y);
				ellipse(xC + childGoal.x, yC + childGoal.y, 10, 10);
			}
		}

		//各ゴールを描画
		textAlign(CENTER, CENTER);
		for (Goal g : sToolEditor.fgm.getGoals()) {

			fill(COLOR_BACKGROUND);
			stroke(COLOR_LINES);

			//ゴールの優先度に応じた配色設定
			if (g.id == selectedGoalId) {
				stroke(COLOR_SELECTED);
				strokeWeight(3);
			}

			//葉っぱかどうかで変わる配色設定
			if (g.childrenType.equals(Goal.ChildrenType.LEAF)) fill(COLOR_LINES);

			//わっか記述
			ellipse(g.x, g.y, textWidth(g.name) + 40, 40);

			fill(COLOR_LINES);

			//葉っぱかどうかで変わる配色設定
			if (g.childrenType.equals(Goal.ChildrenType.LEAF)) fill(COLOR_BACKGROUND);

			//選択ゴール時
			if (g.id == selectedGoalId) fill(COLOR_SELECTED);

			//名前の記述
			text(g.name, g.x, g.y - 2);
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
	 * となる
	 */
	private float getRadian(Goal root, Goal dist) {
		//枝の刺さる角度
		float alpha = (PI / 2) + atan((float) (dist.y - root.y) / (float) (dist.x - root.x));
		//第２、３象限のとき、atan()の都合上修正噛ます
		if (root.x > dist.x) alpha += PI;
		if (alpha < PI / 2) {
			alpha += 3 * PI / 2;
		} else {
			alpha -= PI / 2;
		}
		return alpha;
	}

	public void mousePressed() {
		final int goalMergin = 40;

		//ドメイン選択の一時解除
		selectedGoalId = -1;
		for (Goal g : sToolEditor.fgm.getGoals()) {
			//マウスクリック範囲にドメインがあれば、それを選択
			if (g.x - goalMergin < mouseX && mouseX < g.x + goalMergin &&
					g.y - goalMergin < mouseY && mouseY < g.y + goalMergin) {
				selectedGoalId = g.id;
			}
		}

		sToolEditor.redraw();
	}

	public void mouseDragged() {
		if (mouseButton == LEFT && selectedGoalId != -1) {
			Goal g = sToolEditor.fgm.getGoalById(selectedGoalId);
			if (g != null && 0 < mouseX && mouseX < width && 0 < mouseY && mouseY < height)
				sToolEditor.fgm.editGoal(selectedGoalId, mouseX, mouseY);
		}
		sToolEditor.redraw();
	}
}
