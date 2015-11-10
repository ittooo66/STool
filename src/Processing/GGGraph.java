package Processing;

import Models.*;
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

	//TODO:PRO MODE
	//public boolean isProMode;

	//本体
	private SToolEditor sToolEditor;

	//カラーパレット
	private final int COLOR_BACKGROUND = color(255, 255, 255);
	private final int COLOR_LINES = color(51, 51, 51);
	private final int COLOR_FILL = color(220, 233, 255);
	private final int COLOR_SELECTED = color(57, 152, 214);

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
		background(COLOR_BACKGROUND);

		//記述の下準備
		stroke(COLOR_LINES);
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
				float childR = PUtility.getRadian(childGoal.x, childGoal.y, parentGoal.x, parentGoal.y);
				float parentR = PUtility.getRadian(parentGoal.x, parentGoal.y, childGoal.x, childGoal.y);
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

			//fill変更(リーフか否か,選択中か否か)
			fill((g.id == selectedGoalId) ? COLOR_SELECTED : (g.childrenType.equals(Goal.ChildrenType.LEAF) ? COLOR_FILL : COLOR_BACKGROUND));
			//stroke変更(選択中か否か)
			if (g.id == selectedGoalId) noStroke();
			else stroke(COLOR_LINES);

			//ゴール名が１行の場合
			if (match(g.name, "\n") == null) {
				//わっか記述
				ellipse(g.x, g.y, textWidth(g.name) + 40, 40);

				//fill変更(選択中か否か、リーフか否か)
				fill(g.id == selectedGoalId ? COLOR_BACKGROUND : COLOR_LINES);

				//名前の記述
				text(g.name, g.x, g.y - 2);
			} else {//ゴール名が二行以上のとき
				//テキストをSplit
				String[] texts = splitTokens(g.name, "\n");

				//枠記述
				rect(g.x - (textWidth(g.name) + 40) / 2, g.y - texts.length * 8 - 10, textWidth(g.name) + 40, texts.length * 16 + 20, 8);

				//fill変更(選択中か否か、リーフか否か)
				fill(g.id == selectedGoalId ? COLOR_BACKGROUND : COLOR_LINES);

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

		//ドメイン選択の一時解除
		selectedGoalId = -1;
		for (Goal g : sToolEditor.fgm.getGoals()) {
			//マウスクリック範囲にドメインがあれば、それを選択
			if (PUtility.mouseIsInEllipse(g.x, g.y, (int) textWidth(g.name) + 40, 40, mouseX, mouseY))
				selectedGoalId = g.id;
		}

		//Shift押下時のみ、枝張りショートカット実行
		if (shiftKeyPressed) {
			Goal preSelectedGoal = sToolEditor.fgm.getGoalById(preSelectedGoalId);
			if (preSelectedGoal != null && sToolEditor.fgm.getGoalById(selectedGoalId) != null) {
				preSelectedGoal.parentId = selectedGoalId;
				String str = sToolEditor.fgm.editGoal(preSelectedGoal.id, preSelectedGoal.name, preSelectedGoal.childrenType, selectedGoalId, preSelectedGoal.isEnable);
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
