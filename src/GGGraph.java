import Models.Goal;
import processing.core.PApplet;
import processing.core.PFont;

/**
 * ゴールグラフエディタタブで出力するProcessing部分
 */
public class GGGraph extends PApplet{

	//選択中のゴールID（-1なら非選択）
	int selectedGoalId = -1;

	//本体
	SToolEditor sToolEditor;

	public GGGraph(SToolEditor sToolEditor){
		this.sToolEditor=sToolEditor;
	}


	public void draw(){
		background(32,32,32);
		stroke(166,178,195);
		fill(166,178,195);
		textAlign(LEFT);
		text("Version:"+sToolEditor.version.toString()+", Mode:"+sToolEditor.viewmode.toString()+", Resolution:"+width+","+height,10,20);
		noFill();

		//各ゴールを描画
		for (Goal g : sToolEditor.fgm.goals) {

			//TODO:Describe AND/OR
			//TODO:Describe Relations

			//ゴールわっか描画
			if (g.id == selectedGoalId) {
				//選択されているゴールなら強調描画
				stroke(196, 121, 52);
				strokeWeight(3);
			}else if (g.isEnable){
				stroke(166,178,195);
				strokeWeight(2);
			}else{
				stroke(116,128,145);
				strokeWeight(1);
			}
			ellipse(g.x, g.y, textWidth(g.name)+40, 40);

			//名前の記述
			fill(166,178,195);
			textAlign(CENTER,CENTER);
			text(g.name, g.x, g.y-2);
			fill(32,32,32);

		}
	}

	public  void setup(){
		size(1024,768);
		noLoop();

		//Font設定。
		PFont font = createFont("メイリオ ボールド",15,true);
		textFont(font);
	}

	public void mousePressed(){
		final int goalMergin = 40;

		//ドメイン選択の一時解除
		selectedGoalId = -1;
		for (Goal g : sToolEditor.fgm.goals) {
			//マウスクリック範囲にドメインがあれば、それを選択
			if (g.x - goalMergin < mouseX && mouseX < g.x + goalMergin &&
					g.y - goalMergin < mouseY && mouseY < g.y + goalMergin) {
				selectedGoalId = g.id;
			}
		}
		if(selectedGoalId == -1)
			sToolEditor.ggEditChangeEditMode(false);
		else
			sToolEditor.ggEditChangeEditMode(true);
		redraw();
	}

	public void mouseDragged(){
		if (mouseButton == LEFT) {
			if (selectedGoalId != -1) {
				Goal g = sToolEditor.fgm.getGoal(selectedGoalId);
				if (g != null) sToolEditor.fgm.editGoal(selectedGoalId, g.name, g.childrenType, mouseX, mouseY);
			}
		}
		redraw();
	}
}
