package Processing;

import Models.*;
import Core.*;
import processing.core.PApplet;
import processing.core.PFont;

/**
 * ゴールグラフエディタタブで出力するProcessing部分
 */
public class GGGraph extends PApplet{

	//選択中のゴールID（-1なら非選択）
	public int selectedGoalId = -1;

	//本体
	private SToolEditor sToolEditor;

	//カラーパレット
	private final int COLOR_BACKGROUND =color(32,32,32);
	private final int COLOR_LINES =color(166,178,195);
	private final int COLOR_SELECTED = color(196,121,51);
	private final int COLOR_ACCENT = color(116,128,145);

	public GGGraph(SToolEditor sToolEditor){
		this.sToolEditor=sToolEditor;
	}

	public void setup(){
		//とりあえず適当な解像度で初期化
		size(1024,768);
		//CPU節約
		noLoop();
		//Font設定。
		PFont font = createFont("メイリオ ボールド",15,true);
		textFont(font);
	}

	public void draw(){
		//背景描画
		background(COLOR_BACKGROUND);

		//デバッグ用
		fill(COLOR_LINES);
		textAlign(LEFT);
		text("Version:"+sToolEditor.getVersion().toString()+", Mode:"+sToolEditor.getViewmode().toString()+", Resolution:"+width+","+height+"(for debugging)",10,20);

		//枝周り記述の下準備
		stroke(COLOR_LINES);
		strokeWeight(1);
		noFill();

		//TODO:AND記述
		//AND記述対象のゴール捜索
		for(Goal parentGoal: sToolEditor.fgm.getGoals()) {
			if (parentGoal.childrenType.equals(Goal.ChildrenType.AND)) {

				//ゴール下のマージン
				final int mergin = 40;
				//子ゴールカウント
				int countOfChildGoals = 0;
				//開始角度・終了角度を用意
				float rootR = 0, distR = PI*2;

				//AND記述対象の子ゴール捜索
				for (Goal childGoal : sToolEditor.fgm.getGoals()) {
					if (childGoal.parentId == parentGoal.id) {

						//子ゴールの角度（0~2PIで４->３->２->１象限の順にまわる）
						float childR = PI / 2 + atan((float) (childGoal.y - parentGoal.y) / (float) (childGoal.x - parentGoal.x));
						System.out.println(childR);
						//第２、３象限のとき、atan()の都合上修正噛ます
						if (childGoal.x < parentGoal.x) childR += PI;

						//1st Goalのとき、とりあえずtemp にいれちゃう
						if (countOfChildGoals == 0) {
							rootR=childR;
							distR=childR;
						} else if(rootR > childR){//それ以外
							rootR=childR;
						}else if(distR < childR){
							distR=childR;
						}
						countOfChildGoals++;
					}


					//ゴールカウントが２以上で線引き
					if (countOfChildGoals > 1) {
						text(rootR + "," + distR, 10, 100);
						arc(parentGoal.x, parentGoal.y, textWidth(parentGoal.name) + 40 + 60, 40 + 50, rootR, distR);
					}
				}
			}
		}


		//枝を描画
		for (Goal g : sToolEditor.fgm.getGoals()) {
			if (g.parentId != -1)
				line(g.x, g.y, sToolEditor.fgm.getGoalById(g.parentId).x, sToolEditor.fgm.getGoalById(g.parentId).y);
		}

		//各ゴールを描画
			textAlign(CENTER, CENTER);
		for (Goal g : sToolEditor.fgm.getGoals()) {
			//背景と同色で塗りつぶし（背面の枝塗りつぶしのため）
			fill(COLOR_BACKGROUND);

			//ゴールの優先度に応じた配色設定
			if (g.id == selectedGoalId) {
				stroke(COLOR_SELECTED);
				strokeWeight(3);
			} else if (g.isEnable) {
				stroke(COLOR_LINES);
				strokeWeight(2);
			} else {
				stroke(COLOR_ACCENT);
				strokeWeight(1);
			}

			//わっか記述
			ellipse(g.x, g.y, textWidth(g.name) + 40, 40);

			//名前の記述
			fill(COLOR_LINES);
			text(g.name, g.x, g.y - 2);
		}
	}


	public void mousePressed(){
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

	public void mouseDragged(){
		if (mouseButton == LEFT && selectedGoalId != -1) {
			Goal g = sToolEditor.fgm.getGoalById(selectedGoalId);
			if (g != null && 0<mouseX && mouseX<width && 0<mouseY && mouseY<height)
				sToolEditor.fgm.editGoal(selectedGoalId, g.name, g.childrenType, g.parentId, mouseX, mouseY);
		}
		sToolEditor.redraw();
	}
}
