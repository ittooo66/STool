/**
 * Created by shoichiro on 2015/08/19.
 */

import Models.Domain;
import Models.Goal;
import processing.core.PApplet;
import controlP5.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PAppletMain extends PApplet {
	//ControlP5使用
	ControlP5 cp5;
	//ControlP5で使用しているコントローラーリスト
	List<Controller> clist;
	//Controllers（フィールド内容の書き換えに難儀、仕方なくココに置く。）
	Textfield tf_pf, tf_gg;
	DropdownList ddl_gg;
	List<Goal> ddl_gg_list;

	//融合ゴールモデル
	FGModel fgm;

	int selectedDomainId = -1;
	int selectedGoalId = -1;

	public void setup() {
		//初期設定
		size(1024, 768);
		strokeWeight(2);
		cp5 = new ControlP5(this);
		clist = Collections.synchronizedList(new ArrayList<Controller>());
		fgm = new FGModel();

		//タブ初期設定
		cp5.getTab("default").setLabel("GGEDIT");
		cp5.addTab("UCEDIT");
		cp5.addTab("PFEDIT");
		cp5.addTab("METRICS");

		//ボタン定義（PF）
		cp5.addButton("pf_button_add").setLabel("ADD").setBroadcast(false).setPosition(900, 54).setSize(80, 40).setValue(1).setBroadcast(true).moveTo("PFEDIT").getCaptionLabel().align(CENTER, CENTER);
		cp5.addButton("pf_button_remove").setLabel("REMOVE").setBroadcast(false).setPosition(30, 54).setSize(80, 40).setValue(1).setBroadcast(true).moveTo("PFEDIT").getCaptionLabel().align(CENTER, CENTER);
		cp5.addButton("pf_button_type").setLabel("NONE").setBroadcast(false).setPosition(600, 54).setSize(80, 40).setValue(1).setBroadcast(true).moveTo("PFEDIT").getCaptionLabel().align(CENTER, CENTER);
		tf_pf = cp5.addTextfield("pf_textfield_domain").setLabel("DOMAINNAME").setPosition(300, 54).setSize(200, 40).setColor(color(255, 0, 0)).moveTo("PFEDIT");
		//ボタン定義（GG）
		cp5.addButton("gg_button_add").setLabel("ADD").setBroadcast(false).setPosition(900, 54).setSize(80, 40).setValue(1).setBroadcast(true).moveTo("default").getCaptionLabel().align(CENTER, CENTER);
		cp5.addButton("gg_button_remove").setLabel("REMOVE").setBroadcast(false).setPosition(30, 54).setSize(80, 40).setValue(1).setBroadcast(true).moveTo("default").getCaptionLabel().align(CENTER, CENTER);
		cp5.addButton("gg_button_type").setLabel("NONE").setBroadcast(false).setPosition(600, 54).setSize(80, 40).setValue(1).setBroadcast(true).moveTo("default").getCaptionLabel().align(CENTER, CENTER);
		ddl_gg = cp5.addDropdownList("gg_dropdownlist_parent").setLabel("PARENT").setOpen(false).setLabel("Parent").setPosition(150, 54).setBackgroundColor(color(190)).setItemHeight(20).setBarHeight(15).addItem("--------", null).setColorBackground(color(60)).setColorActive(color(255, 128)).moveTo("default");
		tf_gg = cp5.addTextfield("gg_textfield_goal").setLabel("GOALNAME").setPosition(300, 54).setSize(200, 40).setColor(color(255, 0, 0)).moveTo("default");

		//リストに追加
		clist.add(cp5.getController("pf_button_add"));
		clist.add(cp5.getController("pf_button_remove"));
		clist.add(cp5.getController("pf_button_type"));
		clist.add(cp5.getController("pf_textfield_domain"));
		clist.add(cp5.getController("gg_button_add"));
		clist.add(cp5.getController("gg_button_remove"));
		clist.add(cp5.getController("gg_button_type"));
		clist.add(cp5.getController("gg_textfield_goal"));
		clist.add(cp5.getController("gg_dropdownlist_parent"));

		fgm.addGoal("OOMOTO", -1, Goal.ChildrenType.AND, 312, 384);
		fgm.addGoal("CHILD", 0, Goal.ChildrenType.NONE, 512, 384);

		//PFEDIT最前面開始（テスト用）
		//cp5.getTab("PFEDIT").setActive(true);
		//cp5.getTab("default").setActive(false);

		//dropdownlist詰め込み
		ddl_gg.clear();
		ddl_gg_list = new ArrayList<Goal>();
		for (Goal g : fgm.goals) {
			ddl_gg.addItem(g.name, g.id);
			ddl_gg_list.add(g);
		}

	}

	//Button押下時イベント
	public void pf_button_add() {
		if (selectedDomainId == -1) {
			//Domain選択がされていない場合：新規ドメイン作成
			System.out.println(cp5.getController("pf_button_type").getLabel());
			String str = cp5.get(Textfield.class, "pf_textfield_domain").getText();
			System.out.println(str);
			Domain.DomainType dt = Domain.DomainType.parse(cp5.getController("pf_button_type").getLabel());
			System.out.println(dt);
			fgm.addDomain(str, dt, 512, 384);
		} else {
			Domain d = fgm.getDomain(selectedDomainId);
			fgm.editDomain(selectedDomainId, tf_pf.getText(), Domain.DomainType.parse(cp5.getController("pf_button_type").getLabel()), d.x, d.y);
		}
	}

	public void pf_button_remove() {
		if (selectedDomainId != -1) {
			fgm.removeDomain(selectedDomainId);
		}
	}

	public void pf_button_type() {
		cp5.getController("pf_button_type").setLabel(Domain.DomainType.parse(cp5.getController("pf_button_type").getLabel()).next().toString());
	}

	public void gg_button_add() {

		//System.out.println("getValue():"+ddl_gg.getValue());
		//System.out.println("g"+ddl_gg_list.get((int)ddl_gg.getValue()).id);

		if (selectedGoalId == -1) {
			//Goal選択がされていない場合：新規ゴール作成
			String str = cp5.get(Textfield.class, "gg_textfield_goal").getText();
			Goal.ChildrenType ct = Goal.ChildrenType.parse(cp5.getController("gg_button_type").getLabel());
			fgm.addGoal(str, ddl_gg_list.get((int) ddl_gg.getValue()).id, ct, 512, 384);
		} else {
			//選択されている場合：ゴール編集
			Goal g = fgm.getGoal(selectedGoalId);
			fgm.editGoal(selectedGoalId, tf_gg.getText(), Goal.ChildrenType.parse(cp5.getController("gg_button_type").getLabel()), g.x, g.y);
		}

		//dropdownlist詰め込み直し
		ddl_gg.clear();
		ddl_gg_list = new ArrayList<Goal>();
		for (Goal g : fgm.goals) {
			ddl_gg.addItem(g.name, g.id);
			ddl_gg_list.add(g);
		}
	}

	public void gg_button_remove() {
		if (selectedGoalId != -1) {
			fgm.removeGoal(selectedGoalId);
		}
	}

	public void gg_button_type() {
		cp5.getController("gg_button_type").setLabel(Goal.ChildrenType.parse(cp5.getController("gg_button_type").getLabel()).next().toString());
	}

	public void draw() {
		background(128);
		//タブで記述内容切り替え
		if (cp5.getTab("default").isActive()) {
			drawGG();
		} else if (cp5.getTab("UCEDIT").isActive()) {
			drawUC();
		} else if (cp5.getTab("PFEDIT").isActive()) {
			drawPF();
		} else if (cp5.getTab("METRICS").isActive()) {
			drawME();
		}
	}

	void drawGG() {
		//Add/Editボタン記述
		if (selectedGoalId != -1) {
			cp5.getController("gg_button_add").setLabel("edit");
		} else {
			cp5.getController("gg_button_add").setLabel("add");
		}

		//各ゴールを描画
		for (Goal g : fgm.goals) {
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

		}
	}

	void drawUC() {
		//TODO:UseCase
	}

	void drawPF() {

		//Add/Editボタン記述
		if (selectedDomainId != -1) {
			cp5.getController("pf_button_add").setLabel("edit");
		} else {
			cp5.getController("pf_button_add").setLabel("add");
		}

		//各ドメインを描画
		for (Domain d : fgm.domains) {
			noStroke();
			fill(252, 252, 252);
			rect(d.x - 20 - d.name.length() * (float) 3.5, d.y - 20, 40 + d.name.length() * 7, 40);

			if (d.id == selectedDomainId)
				//選択されたドメインは赤
				stroke(255, 0, 0);
			else
				stroke(128, 128, 128);

			rect(d.x - 20 - d.name.length() * (float) 3.5, d.y - 20, 40 + d.name.length() * 7, 40);

			// domaintype記述（左端のやつ）
			if (d.domainType == Domain.DomainType.DESIGNED) {
				line(d.x - 20 - d.name.length() * (float) 3.5 + 5, d.y - 20, d.x - 20 - d.name.length() * (float) 3.5 + 5, d.y + 20);
			} else if (d.domainType == Domain.DomainType.SYSTEM) {
				line(d.x - 20 - d.name.length() * (float) 3.5 + 5, d.y - 20, d.x - 20 - d.name.length() * (float) 3.5 + 5, d.y + 20);
				line(d.x - 20 - d.name.length() * (float) 3.5 + 10, d.y - 20, d.x - 20 - d.name.length() * (float) 3.5 + 10, d.y + 20);
			} else if (d.domainType == Domain.DomainType.BIDDABLE) {
				rect(d.x - 20 - d.name.length() * (float) 3.5, d.y + 6, 20, 14);
				fill(100, 100, 100);
				textAlign(CENTER);
				text("B", d.x - 20 - d.name.length() * (float) 3.5 + 8, d.y + 18);
			} else if (d.domainType == Domain.DomainType.CAUSAL) {
				rect(d.x - 20 - d.name.length() * (float) 3.5, d.y + 6, 20, 14);
				fill(100, 100, 100);
				textAlign(CENTER);
				text("C", d.x - 20 - d.name.length() * (float) 3.5 + 8, d.y + 18);
			} else if (d.domainType == Domain.DomainType.LEXICAL) {
				rect(d.x - 20 - d.name.length() * (float) 3.5, d.y + 6, 20, 14);
				fill(100, 100, 100);
				textAlign(CENTER);
				text("X", d.x - 20 - d.name.length() * (float) 3.5 + 8, d.y + 18);
			}

			//名前の記述
			fill(100, 100, 100);
			textAlign(CENTER);
			text(d.name, d.x, d.y + 2);

		}
	}

	void drawME() {
		//TODO:Metrics
	}

	public void mousePressed() {
		//コントローラーの押下かどうか判断
		boolean controlP5ControllerPressed = false;
		for (Controller c : clist) {
			if (c.isMouseOver()) controlP5ControllerPressed = true;
		}

		//各種コントローラーのクリック操作でなければ各mousePress実行
		if (!controlP5ControllerPressed) {
			if (cp5.getTab("default").isActive()) {
				mousePressOnGGEDIT();
			} else if (cp5.getTab("UCEDIT").isActive()) {
				mousePressOnUCEDIT();
			} else if (cp5.getTab("PFEDIT").isActive()) {
				mousePressOnPFEDIT();
			} else if (cp5.getTab("METRICS").isActive()) {
				mousePressOnMETRICS();
			}
		}
	}

	void mousePressOnGGEDIT() {
		final int goalMergin = 40;

		//ドメイン選択の一時解除
		selectedGoalId = -1;
		for (Goal g : fgm.goals) {
			//マウスクリック範囲にドメインがあれば、それを選択
			if (g.x - goalMergin < mouseX && mouseX < g.x + goalMergin &&
					g.y - goalMergin < mouseY && mouseY < g.y + goalMergin) {
				selectedGoalId = g.id;
				cp5.getController("gg_button_add").setLabel("EDIT");
				cp5.getController("gg_button_type").setLabel(g.childrenType.toString());
				for (int i = 0; i < ddl_gg_list.size(); i++) {
					if (ddl_gg_list.get(i).id == g.parentId) {
						//setValue()がバグってるので正常に機能せず。のちのち変える？
						ddl_gg.setValue(i);
					}
				}
				tf_gg.setText(g.name);
			}
		}

		if (selectedGoalId == -1) {
			tf_gg.setText("");
		}
	}

	void mousePressOnUCEDIT() {
		//TODO:UseCase
	}

	void mousePressOnPFEDIT() {
		final int domainMergin = 40;

		//ドメイン選択の一時解除
		selectedDomainId = -1;
		for (Domain d : fgm.domains) {
			//マウスクリック範囲にドメインがあれば、それを選択
			if (d.x - domainMergin < mouseX && mouseX < d.x + domainMergin &&
					d.y - domainMergin < mouseY && mouseY < d.y + domainMergin) {
				selectedDomainId = d.id;
				cp5.getController("pf_button_add").setLabel("EDIT");
				cp5.getController("pf_button_type").setLabel(d.domainType.toString());
				tf_pf.setText(d.name);
			}
		}

		if (selectedDomainId == -1) {
			tf_pf.setText("");
		}
	}

	void mousePressOnMETRICS() {

	}

	public void mouseDragged() {
		//コントローラーの押下かどうか判断
		boolean controlP5ControllerPressed = false;
		for (Controller c : clist) {
			if (c.isMouseOver() || c.isMousePressed()) controlP5ControllerPressed = true;
		}
		//各種コントローラーのクリック操作でなければ各種mouseDrag実行
		if (!controlP5ControllerPressed) {
			if (cp5.getTab("default").isActive()) {
				mouseDragGG();
			} else if (cp5.getTab("UCEDIT").isActive()) {
				mouseDragUC();
			} else if (cp5.getTab("PFEDIT").isActive()) {
				mouseDragPF();
			} else if (cp5.getTab("METRICS").isActive()) {
				mouseDragME();
			}
		}
	}

	void mouseDragGG() {
		if (mouseButton == LEFT) {
			if (selectedGoalId != -1) {
				Goal g = fgm.getGoal(selectedGoalId);
				if (g != null) fgm.editGoal(selectedGoalId, g.name, g.childrenType, mouseX, mouseY);
			}
		}
	}

	void mouseDragUC() {

	}

	void mouseDragPF() {
		if (mouseButton == LEFT) {
			if (selectedDomainId != -1) {
				Domain d = fgm.getDomain(selectedDomainId);
				if (d != null) fgm.editDomain(selectedDomainId, d.name, d.domainType, mouseX, mouseY);
			}
		}
	}

	void mouseDragME() {
	}

}
