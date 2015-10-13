import Models.Goal;
import processing.core.PApplet;

import java.util.ArrayList;
import java.util.List;

/**
 * ゴールグラフエディタタブで出力するProcessing部分
 */
public class GGGraph extends PApplet{

	int selectedGoalId = -1;

	//本体
	SToolEditor sToolEditor;

	//ゴール選択中か否か
	boolean isGoalSelected;

	public GGGraph(SToolEditor sToolEditor){
		this.sToolEditor=sToolEditor;
	}

	public void draw(){
		background(0);
		stroke(200,200,200);
		noFill();
		text("Version:"+sToolEditor.version.toString()+", Mode:"+sToolEditor.viewmode.toString()+", Resolution:"+width+","+height,10,20);

		/*
		//各ゴールを描画
		for (Goal g : sToolEditor.fgm.goals) {
			noStroke();
			fill(252, 252, 252);
			ellipse(g.x - g.name.length() * (float) 3.5 + 20, g.y, 40 + g.name.length() * 7, 40);

			if (g.id == selectedGoalId)
				//選択されたドメインは赤
				stroke(255, 0, 0);
			else
				stroke(128, 128, 128);

			ellipse(g.x - g.name.length() * (float) 3.5 + 20, g.y, 40 + g.name.length() * 7, 40);

			// AND/OR記述
			//TODO:Describe AND/OR

			//名前の記述
			fill(100, 100, 100);
			textAlign(CENTER);
			text(g.name, g.x, g.y + 2);

		}*/

	}

	public  void setup(){
		size(1024,768);
		noLoop();
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
