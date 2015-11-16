package Models;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * FGModelAdapter: 融合ゴールモデル、loadXML()とsaveXML()でXML入出力を行える<p>
 * String add~(),String edit~(),String remove~()の仕様：<p>
 * ・null値：編集操作成功<p>
 * ・文字列：エラーメッセージ
 */
public class FGModelAdapter implements FGModel {
	private FGModelCore fgm;

	//TODO:AS-IS,TO-BEスライスをいい感じにつくる
	//enable,disableのセット
	private List<List<Boolean>> enableGoalSet;

	public FGModelAdapter() {
		fgm = new FGModelCore();
	}

	public void loadXML(File file) {
		fgm.loadXML(file);
	}

	public void saveXML(File file) {
		fgm.saveXML(file);
	}

	public Goal getGoalById(int id) {
		for (Goal g : fgm.goals) {
			if (g.id == id) return (Goal) g.clone();
		}
		return null;
	}

	public Domain getDomainById(int id) {
		for (Domain d : fgm.domains) {
			if (d.id == id) return (Domain) d.clone();
		}
		return null;
	}

	public Usecase getUsecaseById(int id) {
		for (Usecase u : fgm.usecases) {
			if (u.id == id) return (Usecase) u.clone();
		}
		return null;
	}

	public List<Goal> getGoals() {
		return fgm.goals.stream().map(g -> (Goal) g.clone()).collect(Collectors.toList());
	}

	public List<Domain> getDomains() {
		return fgm.domains.stream().map(d -> (Domain) d.clone()).collect(Collectors.toList());
	}

	public List<Usecase> getUsecases() {
		return fgm.usecases.stream().map(u -> (Usecase) u.clone()).collect(Collectors.toList());
	}

	public String addGoal(String name, int parent_id, int x, int y) {
		//名前のnull-check
		if (name == null) {
			return "GOAL NAME MUST BE NON-NULL";
		}

		//ParentIDをバリデート（－1以外）
		boolean hasParent = false;
		for (Goal g : fgm.goals) if (g.id == parent_id) hasParent = true;
		if (!hasParent && parent_id != -1) {
			return "PARENT_ID IS INVALID";
		}

		//新ID生成
		int id = 0;
		for (Goal g : fgm.goals) {
			if (g.id >= id) {
				id = g.id + 1;
			}
		}
		//インスタンス作成
		Goal g = new Goal();
		g.id = id;
		g.name = name;
		g.parentId = parent_id;
		//トップでなければ、親のchildrentypeを変更
		for (Goal gl : fgm.goals) {
			if (gl.id == parent_id && gl.childrenType == Goal.ChildrenType.LEAF) {
				gl.childrenType = Goal.ChildrenType.OR;
			}
		}
		//子にはリーフ設定を付与
		g.childrenType = Goal.ChildrenType.LEAF;
		g.x = x;
		g.y = y;
		//追加
		fgm.goals.add(g);

		//1:1対応のUsecaseを合わせて追加
		addUsecase("Achieve:" + name, id);

		return null;
	}

	public String addDomain(String name, Domain.DomainType dt, int x, int y) {
		if (name == null) return "DOMAINNAME MUST BE NON-NULL";

		//新ID生成
		int id = 0;
		for (Domain d : fgm.domains) {
			if (d.id >= id) {
				id = d.id + 1;
			}
		}
		//インスタンス作成
		Domain d = new Domain();
		d.id = id;
		d.name = name;
		d.domainType = dt;
		d.x = x;
		d.y = y;
		//追加
		fgm.domains.add(d);
		return null;
	}

	private void addUsecase(String name, int parentGoalId) {
		//新ID生成
		int id = 0;
		for (Usecase u : fgm.usecases) {
			if (u.id >= id) {
				id = u.id + 1;
			}
		}
		//インスタンス作成
		Usecase u = new Usecase();
		u.init(id, name, parentGoalId);
		u.addStep(0, 0);

		//追加
		fgm.usecases.add(u);
	}

	public String editGoal(int id, String name, Goal.ChildrenType childrenType, int parentId, boolean isEnable) {
		//編集対象のゴール取得
		Goal goal = null;
		for (Goal g : fgm.goals) {
			if (g.id == id) {
				goal = g;
				break;
			}
		}
		if (goal == null) return "COULD NOT FIND A GOAL THAT HAS ID:" + id;

		//モデル整合性チェック
		if (name.equals(null)) return "GOAL NAME MUST BE NON-NULL !";

		//親ゴールとして自己参照はだめ
		if (id == parentId) return "YOU ARE NOT YOUR PARENT !";

		if (childrenType == Goal.ChildrenType.LEAF) {
			//LEAF設定は子ゴールのみに許される
			for (Goal g : fgm.goals) if (g.parentId == id) return "LEAF GOAL IS ONLY ALLOWED TO A CHILD";
		}

		//更新
		fgm.goals.stream().filter(g -> g.id == parentId && g.childrenType == Goal.ChildrenType.LEAF).forEach(g -> g.childrenType = Goal.ChildrenType.OR);
		goal.name = name;
		goal.childrenType = childrenType;
		goal.parentId = parentId;
		goal.isEnable = isEnable;
		return null;

	}

	public String editDomain(int id, String name, Domain.DomainType dt) {
		if (name == null) return "DOMAIN NAME MUST BE NON-NULL";

		for (Domain d : fgm.domains) {
			if (d.id == id) {
				d.domainType = dt;
				d.name = name;
				return null;
			}
		}
		return "COULD NOT FIND A DOMAIN THAT HAS ID:" + id;
	}

	public String editUsecase(int id, Usecase usecase) {
		for (int i = 0; i < fgm.usecases.size(); i++) {
			if (fgm.usecases.get(i).id == id) {
				//TODO:モデル整合性チェック
				//TODO:1.ID確認
				//TODO:2.GOTO,INCLUDE命令の整合性（ALT_EXC系のJump命令先がNullではないか）
				//TODO:妥当でなければreturn false;

				for (Step s : usecase.getMainFlow()) {
					if (s.stepType == Step.StepType.GOTO) return "MAIN FLOW CANNOT HAVE A GOTO STEP";
				}

				fgm.usecases.set(i, usecase);
				return null;
			}
		}
		return "COULD NOT FIND A USECASE THAT HS ID:" + id;
	}

	public void moveGoal(int id, int x, int y) {
		for (Goal g : fgm.goals) {
			if (g.id == id) {
				g.x = x;
				g.y = y;
				return;
			}
		}
	}

	public void moveDomain(int id, int x, int y) {
		for (Domain d : fgm.domains) {
			if (d.id == id) {
				d.x = x;
				d.y = y;
				return;
			}
		}
	}

	public void moveUsecase(int id, int direction) {
		for (int i = 0; i < fgm.usecases.size(); i++) {
			if (fgm.usecases.get(i).id == id) {
				switch (direction) {
					case 1:
						if (i != fgm.usecases.size() - 1) swap(fgm.usecases, i, i + 1);
						return;
					case -1:
						if (i != 0) swap(fgm.usecases, i, i - 1);
						return;
				}
			}
		}
	}

	private static <t> void swap(List<t> list, int index1, int index2) {
		t tmp = list.get(index1);
		list.set(index1, list.get(index2));
		list.set(index2, tmp);
	}

	public String removeGoal(int id) {
		for (int i = 0; i < fgm.goals.size(); i++) {
			Goal removedGoalCandidate = fgm.goals.get(i);
			if (removedGoalCandidate.id == id) {

				//ゴール削除（get(i),remove(i)でまわしてることに注意）
				fgm.goals.remove(i);
				//取り残された子供の処理
				for (Goal g : fgm.goals) {
					if (g.parentId == id) g.parentId = removedGoalCandidate.parentId;
				}
				//関連Usecaseも削除
				for (int j = 0; j < fgm.usecases.size(); j++) {
					if (fgm.usecases.get(j).parentLeafGoalId == id) fgm.usecases.remove(j);
				}
				//TODO:親が子なしになったのであれば親を子にする

				return null;
			}
		}
		return "COULD NOT FIND A GOAL THAT HAS ID:" + id;
	}

	public String removeDomain(int id) {
		for (int i = 0; i < fgm.domains.size(); i++) {
			if (fgm.domains.get(i).id == id) {
				fgm.domains.remove(i);
				return null;
			}
		}
		return "COULD NOT FIND A DOMAIN THAT HAS ID:" + id;
	}

	public List<PFInterface> getPFInterfaceList() {
		List<PFInterface> interfaces = new ArrayList<>();
		for (Usecase usecase : getUsecases()) {
			for (Step step : usecase.getAllActionStep()) {
				//追加フラグ
				boolean flag = false;
				//現存Interfaceから該当を走査
				for (PFInterface i : interfaces) {
					int r = i.rootDomainId;
					int d = i.distDomainId;
					int s = step.subjectDomainId;
					int o = step.objectDomainId;
					if (r == s && d == o) {
						//順方向合致
						i.add(new PFEvent(step.Event, true, usecase.id));
						flag = true;
						break;
					} else if (r == o && d == s) {
						//逆方向合致
						i.add(new PFEvent(step.Event, false, usecase.id));
						flag = true;
						break;
					}
				}

				//現存Interfaceに適合のない場合：Interface作成
				if (!flag) {
					PFInterface pfi = new PFInterface(getDomainById(step.subjectDomainId), getDomainById(step.objectDomainId));
					pfi.add(new PFEvent(step.Event, true, usecase.id));
					interfaces.add(pfi);
				}
			}
		}
		return interfaces;
	}
}
