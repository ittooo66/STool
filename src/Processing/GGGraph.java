package Processing;

import Models.*;
import Processing.Component.COLOR;
import Processing.Component.PUtility;
import Swing.*;
import processing.core.PApplet;
import processing.core.PFont;

import javax.swing.*;


/**
 * ゴールグラフエディタタブで出力するProcessing部分
 */
public class GGGraph extends PApplet {

	//選択中のゴールID（-1なら非選択）
	public int selectedGoalId = -1;

	//本体
	private SToolEditor sToolEditor;

	public GGGraph(SToolEditor sToolEditor) {
		this.sToolEditor = sToolEditor;
	}

	public void setup() {
		//Font設定。
		PFont font = createFont("メイリオ ボールド", 15, true);
		textFont(font);
		//Smoothに描画
		smooth();
	}

	//変更フラグ
	private boolean hasChanges;

	public void redraw() {
		hasChanges = true;
	}

	public void draw() {
		//省力draw()
		if (!hasChanges) return;
		else hasChanges = false;

		//背景描画
		background(COLOR.BACKGROUND);

		//記述の下準備
		stroke(COLOR.LINES);
		strokeWeight(1);
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
						float childR = PUtility.getRadian(parentGoal.x, parentGoal.y, childGoal.x, childGoal.y);

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

				//ゴールカウントが２以上で線引き、ゴール名の行数に応じてArcのマージン変更
				if (countOfChildGoals > 1 && match(parentGoal.name, "\n") == null) {
					PUtility.myArc(this, parentGoal.x, parentGoal.y, textWidth(parentGoal.name) + 40 + 30, 40 + 30, rootR, distR);
				} else {
					PUtility.myArc(this, parentGoal.x, parentGoal.y, textWidth(parentGoal.name) + 40 + 30 + 20 * splitTokens(parentGoal.name, "\n").length, 40 + 30 + 20 * splitTokens(parentGoal.name, "\n").length, rootR, distR);
				}
			}
		}

		//枝を描画
		fill(COLOR.LINES);
		for (Goal childGoal : sToolEditor.fgm.getGoals()) {
			if (childGoal.parentId != -1) {
				//親ゴール取得
				Goal parentGoal = sToolEditor.fgm.getGoalById(childGoal.parentId);
				//子から見た親ゴールの方角
				float childR = PUtility.getRadian(childGoal.x, childGoal.y, parentGoal.x, parentGoal.y);
				//ゴール楕円上のx,y座標（子x,y）
				float x = PUtility.getPointXOnEllipse(childGoal.x, childGoal.y, (int) (textWidth(childGoal.name) + 40), 40, childR);
				float y = PUtility.getPointYOnEllipse(childGoal.x, childGoal.y, (int) (textWidth(childGoal.name) + 40), 40, childR);

				//子ゴールの名前が一行
				if (match(childGoal.name, "\n") == null) {
					//枝引き
					line(parentGoal.x, parentGoal.y, childGoal.x, childGoal.y);
					ellipse(x, y, 10, 10);
				} else {//子ゴールの名前が二行以上
					String[] texts = splitTokens(childGoal.name, "\n");

					//ゴール中心（child.x,child.y）から、ゴール縁のx,y座標を計算
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
					line(parentGoal.x, parentGoal.y, x + childGoal.x, y + childGoal.y);
					//Direction表示用Ellipse
					ellipse(childGoal.x + x, childGoal.y + y, 10, 10);
				}
			}
		}

		//各ゴールを描画
		textAlign(CENTER, CENTER);
		for (Goal g : sToolEditor.fgm.getGoals()) {

			//fill変更(Enableか否か,選択中か否か)
			if (g.id == selectedGoalId) {
				fill(COLOR.SELECTED);
			} else if (sToolEditor.fgm.getVersion() == FGModelAdapter.VERSION.ASIS && g.isEnableForAsIs ||
					sToolEditor.fgm.getVersion() == FGModelAdapter.VERSION.TOBE && g.isEnableForToBe) {
				fill(COLOR.FILL);
			} else {
				fill(COLOR.BACKGROUND);
			}
			//stroke変更(選択中か否か)
			if (g.id == selectedGoalId) noStroke();
			else stroke(COLOR.LINES);

			//ゴール名が１行の場合
			if (match(g.name, "\n") == null) {
				//わっか記述
				strokeWeight(PUtility.mouseIsInEllipse(g.x, g.y, (int) textWidth(g.name) + 40, 40, mouseX, mouseY) ? (float) 1.5 : 1);
				ellipse(g.x, g.y, textWidth(g.name) + 40, 40);
				strokeWeight(1);

				//fill変更(選択中か否か、リーフか否か)
				fill(g.id == selectedGoalId ? COLOR.BACKGROUND : COLOR.LINES);

				//名前の記述
				text(g.name, g.x, g.y - 2);
			} else {//ゴール名が二行以上のとき
				//テキストをSplit
				String[] texts = splitTokens(g.name, "\n");

				//枠記述
				rect(g.x - (textWidth(g.name) + 40) / 2, g.y - texts.length * 8 - 10, textWidth(g.name) + 40, texts.length * 16 + 20, 8);

				//fill変更(選択中か否か、リーフか否か)
				fill(g.id == selectedGoalId ? COLOR.BACKGROUND : COLOR.LINES);

				//名前の記述
				for (int i = 0; i < texts.length; i++) {
					text(texts[i], g.x, (g.y - 2) - texts.length * 8 + 10 + i * 16);
				}
			}
		}
	}

	public void mousePressed() {
		//変更前のゴールIDを保存
		int preSelectedGoalId = selectedGoalId;

		//ゴール選択の一時解除
		selectedGoalId = -1;
		for (Goal g : sToolEditor.fgm.getGoals()) {
			//マウスクリック範囲にゴールがあれば、それを選択
			if (PUtility.mouseIsInEllipse(g.x, g.y, (int) textWidth(g.name) + 40, 40, mouseX, mouseY))
				selectedGoalId = g.id;
		}

		//Shift押下時のみ、枝張りショートカット実行
		if (shiftKeyPressed) {
			Goal preSelectedGoal = sToolEditor.fgm.getGoalById(preSelectedGoalId);
			if (preSelectedGoal != null && sToolEditor.fgm.getGoalById(selectedGoalId) != null) {
				preSelectedGoal.parentId = selectedGoalId;
				String str = sToolEditor.fgm.editGoal(preSelectedGoal.id, preSelectedGoal.name, preSelectedGoal.childrenType, selectedGoalId, preSelectedGoal.isEnableForAsIs, preSelectedGoal.isEnableForToBe);
				if (str != null) {
					JOptionPane.showMessageDialog(this, str, "ERROR", JOptionPane.ERROR_MESSAGE);
				}
			}
		}

		//フォーカスがなければJTextArea初期化
		if (selectedGoalId == -1) sToolEditor.initTextArea();

		sToolEditor.redraw();
	}

	private boolean shiftKeyPressed;

	public void keyPressed() {
		if (keyCode == SHIFT) shiftKeyPressed = true;
	}

	public void keyReleased() {
		if (keyCode == SHIFT) shiftKeyPressed = false;
	}

	public void mouseDragged() {
		if (mouseButton == LEFT && selectedGoalId != -1) {
			Goal g = sToolEditor.fgm.getGoalById(selectedGoalId);
			if (g != null && PUtility.mouseIsInRect(0, 0, width, height, mouseX, mouseY))
				sToolEditor.fgm.moveGoal(selectedGoalId, mouseX, mouseY);
		}
		redraw();
	}

	public void mouseMoved() {
		redraw();
	}
}
