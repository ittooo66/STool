package Models;

import javax.xml.bind.JAXB;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * FGModel
 * List<Goal> goals
 * List<Usecase> usecases
 * List<Domain> domains
 * からなる融合ゴールモデル
 *
 * loadXML()とsaveXML()でXML入出力を行える
 *
 * モデルのおやくそく（根ゴールは必ず１個、消去不可）とかそういうやつはだいたいここで管理すること
 *
 */
public class FGModel {
	private List<Goal> goals;
	private List<Usecase> usecases;
	private List<Domain> domains;

	//enable,disableのセット
	private List<List<Boolean>> enableGoalSet;

	public FGModel() {
		goals = Collections.synchronizedList(new ArrayList<Goal>());
		usecases = Collections.synchronizedList(new ArrayList<Usecase>());
		domains = Collections.synchronizedList(new ArrayList<Domain>());
	}

	public void loadXML(File file) {
		JAXB.unmarshal(file, FGModel.class);
	}

	public void saveXML(File file) {
		JAXB.marshal(this, file);
	}

	public Goal getGoalById(int id) {
		for (Goal g : goals) {
			if (g.id == id) return g;
		}
		return null;
	}

	public List<Goal> getGoals(){
		return goals;
	}

	public void addGoal(String name, int parent_id, Goal.ChildrenType parent_children_type, int x, int y) {
		//新ID生成
		int id = 0;
		for (Goal g : goals) {
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
		for (Goal gl : goals) {
			if (gl.id == parent_id) {
				gl.childrenType = parent_children_type;
			}
		}
		//子にはリーフ設定を付与
		g.childrenType = Goal.ChildrenType.LEAF;
		g.x = x;
		g.y = y;
		//追加
		goals.add(g);
	}


	public void editGoal(int id, String name, Goal.ChildrenType ct,int parentId) {
		for (Goal g : goals) {
			if (g.id == id) {
				g.name = name;
				g.childrenType = ct;
				g.parentId = parentId;
			}
		}
	}

	public void editGoal(int id, String name, Goal.ChildrenType ct,int parentId, int x, int y) {
		for (Goal g : goals) {
			if (g.id == id) {
				g.name = name;
				g.childrenType = ct;
				g.parentId =parentId;
				g.x = x;
				g.y = y;
			}
		}
	}

	/**
	 * ゴールを消去する
	 * @param id　消去するゴールID
	 * @return 消去できたか、否か
	 */
	public boolean removeGoal(int id) {
		for (int i = 0; i < goals.size(); i++) {
			Goal removedGoalCandidate = goals.get(i);
			if (removedGoalCandidate.id == id) {
				//根ゴールは削除不能
				if(removedGoalCandidate.id==0) return false;

				//ゴール削除（get(i),remove(i)でまわしてることに注意）
				goals.remove(i);
				//取り残された子供の処理
				for(Goal g : goals){
					if(g.parentId == removedGoalCandidate.id)g.parentId=removedGoalCandidate.parentId;
				}
				return true;
			}
		}
		return false;
	}

	public List<Domain> getDomains(){
		return domains;
	}

	public Domain getDomain(int id) {
		for (Domain d : domains) {
			if (d.id == id) return d;
		}
		return null;
	}

	public void addDomain(String name, Domain.DomainType dt, int x, int y) {
		//新ID生成
		int id = 0;
		for (Domain d : domains) {
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
		domains.add(d);
	}

	public void editDomain(int id, String name, Domain.DomainType dt, int x, int y) {
		for (Domain d : domains) {
			if (d.id == id) {
				d.domainType = dt;
				d.name = name;
				d.x = x;
				d.y = y;
			}
		}
	}

	public void removeDomain(int id) {
		for (int i = 0; i < domains.size(); i++) {
			if (domains.get(i).id == id)
				domains.remove(i);
		}
	}

	public void setDomains(List<Domain> domains){
		this.domains = domains;
	}

	public void setUsecases(List<Usecase> usecases){
		this.usecases = usecases;
	}

	public Usecase getUsecase(int id) {
		for (Usecase u : usecases) {
			if (u.id == id) return u;
		}
		return null;
	}

	public void addUsecase() {
		//TODO
		return;
	}

	public void editUsecase() {
		//TODO
		return;
	}

	public void removeUsecase(int id) {
		//TODO
		return;
	}

}
