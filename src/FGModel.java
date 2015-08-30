import Models.Domain;
import Models.Goal;
import Models.Usecase;

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
 */
public class FGModel {
	public List<Goal> goals;
	public List<Usecase> usecases;
	public List<Domain> domains;

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

	public Goal getGoal(int id) {
		for (Goal g : goals) {
			if (g.id == id) return g;
		}
		return null;
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
		g.childrenType = Goal.ChildrenType.NONE;
		g.x = x;
		g.y = y;
		//追加
		goals.add(g);
	}

	public void editGoal(int id, String name, Goal.ChildrenType ct, int x, int y) {
		for (Goal g : goals) {
			if (g.id == id) {
				g.name = name;
				g.childrenType = ct;
				g.x = x;
				g.y = y;
			}
		}
	}

	/**
	 * ゴールを無効化（非選択）する。
	 * @param id
	 */
	public void disableGoal(int id) {
		for (Goal g : goals) {
			if (g.id == id) g.isEnable = false;
		}
	}

	/**
	 * ゴールを有効化（選択）する。
	 * @param id
	 */
	public void enableGoal(int id) {
		for (Goal g : goals) {
			if (g.id == id) g.isEnable = true;
		}
	}

	public void removeGoal(int id) {
		for (int i = 0; i < goals.size(); i++) {
			if (goals.get(i).id == id)
				goals.remove(i);
		}
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



	public Usecase getUsecase(int id) {
		for (Usecase u : usecases) {
			if (u.id == id) return u;
		}
		return null;
	}

	public void addUsecase() {
		return;
	}

	public void editUsecase() {
		return;
	}

	public void removeUsecase(int id) {
		return;
	}

}
