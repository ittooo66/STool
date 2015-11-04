package Models;

import javax.xml.bind.JAXB;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

//TODO:モデル規則に従うadd,edit,remove(,move)を行うようにすること
//TODO:上記のメソッドをBoolean値を返す形式にすること（true:編集できた,false:できない）
//TODO:Stringでもいいかも？（Null:編集できた、ErrorMessage:できてない理由）

/**
 * FGModel
 * List<Goal> goals
 * List<Usecase> usecases
 * List<Domain> domains
 * からなる融合ゴールモデル
 * <p>
 * loadXML()とsaveXML()でXML入出力を行える
 * <p>
 * モデルのおやくそく（根ゴールは必ず１個、消去不可）とかそういうやつはだいたいここで管理すること
 */
public class FGModel {
	private List<Goal> goals;
	private List<Usecase> usecases;
	private List<Domain> domains;

	//TODO:AS-IS,TO-BEスライスをいい感じにつくる
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

	/**
	 * IDを用いてゴールを取得
	 *
	 * @param id
	 * @return Goal
	 */
	public Goal getGoalById(int id) {
		for (Goal g : goals) {
			if (g.id == id) return g;
		}
		return null;
	}

	/**
	 * ゴールをまとめて取得
	 *
	 * @return
	 */
	public List<Goal> getGoals() {
		return goals;
	}

	/**
	 * 新規ゴールの追加
	 *
	 * @param name      なまえ
	 * @param parent_id 親のID
	 * @param x         座標
	 * @param y         座標
	 */
	public void addGoal(String name, int parent_id, int x, int y) {
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
			if (gl.id == parent_id && gl.childrenType == Goal.ChildrenType.LEAF) {
				gl.childrenType = Goal.ChildrenType.OR;
			}
		}
		//子にはリーフ設定を付与
		g.childrenType = Goal.ChildrenType.LEAF;
		g.x = x;
		g.y = y;
		//追加
		goals.add(g);

		//1:1対応のUsecaseを合わせて追加
		addUsecase(name + "を達成", id);
	}

	/**
	 * ゴールを編集する
	 *
	 * @param id
	 * @param name
	 * @param childrenType
	 * @param parentId     編集できたらtrue,できないならFalse
	 */
	public boolean editGoal(int id, String name, Goal.ChildrenType childrenType, int parentId) {
		//編集対象のゴール取得
		Goal goal = null;
		for (Goal g : goals) {
			if (g.id == id) {
				goal = g;
				break;
			}
		}

		//モデル整合性チェック

		//親ゴールとして自己参照はだめ
		if (id == parentId) return false;

		if (childrenType == Goal.ChildrenType.LEAF) {
			//LEAF設定は子ゴールのみに許される
			for (Goal g : goals) if (g.parentId == id) return false;
		}

		//更新
		if (goal == null) return false;
		goal.name = name;
		goal.childrenType = childrenType;
		goal.parentId = parentId;
		return true;

	}

	/**
	 * ゴールを編集（更新）する
	 *
	 * @param id 編集するゴールID
	 * @param x  X座標
	 * @param y  Y座標
	 *           完全にエディタの座標更新用
	 */
	public void moveGoal(int id, int x, int y) {
		for (Goal g : goals) {
			if (g.id == id) {
				g.x = x;
				g.y = y;
				return;
			}
		}
	}

	/**
	 * ゴールを消去する
	 *
	 * @param id 　消去するゴールID
	 * @return 消去できたか、否か
	 */
	public boolean removeGoal(int id) {
		for (int i = 0; i < goals.size(); i++) {
			Goal removedGoalCandidate = goals.get(i);
			if (removedGoalCandidate.id == id) {

				//ゴール削除（get(i),remove(i)でまわしてることに注意）
				goals.remove(i);
				//取り残された子供の処理
				for (Goal g : goals) {
					if (g.parentId == id) g.parentId = removedGoalCandidate.parentId;
				}
				//関連Usecaseも削除
				for (int j = 0; j < usecases.size(); j++) {
					if (usecases.get(j).parentLeafGoalId == id) usecases.remove(j);
				}
				//TODO:親が子なしになったのであれば親を子にする

				return true;
			}
		}
		return false;
	}

	/**
	 * ドメインをまとめて取得
	 *
	 * @return
	 */
	public List<Domain> getDomains() {
		return domains;
	}

	public Domain getDomainById(int id) {
		for (Domain d : domains) {
			if (d.id == id) return d;
		}
		return null;
	}

	/**
	 * ドメイン追加
	 *
	 * @param name 名前
	 * @param dt   ドメインタイプ
	 * @param x    座標
	 * @param y    座標
	 */
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

	/**
	 * @param id
	 * @param name
	 * @param dt
	 * @return
	 */
	public boolean editDomain(int id, String name, Domain.DomainType dt) {
		for (Domain d : domains) {
			if (d.id == id) {
				d.domainType = dt;
				d.name = name;
				return true;
			}
		}
		return false;
	}

	/**
	 * @param id
	 * @param x
	 * @param y
	 */
	public void moveDomain(int id, int x, int y) {
		for (Domain d : domains) {
			if (d.id == id) {
				d.x = x;
				d.y = y;
				return;
			}
		}
	}

	public void removeDomain(int id) {
		for (int i = 0; i < domains.size(); i++) {
			if (domains.get(i).id == id)
				domains.remove(i);
		}
	}

	public Usecase getUsecaseById(int id) {
		for (Usecase u : usecases) {
			if (u.id == id) return u;
		}
		return null;
	}

	public List<Usecase> getUsecases() {
		return usecases;
	}

	public void addUsecase(String name, int parentGoalId) {
		//新ID生成
		int id = 0;
		for (Usecase u : usecases) {
			if (u.id >= id) {
				id = u.id + 1;
			}
		}
		//インスタンス作成
		Usecase u = new Usecase(id, name, parentGoalId);

		//追加
		usecases.add(u);
	}

	public boolean editUsecase(int id, Usecase usecase) {
		for (int i = 0; i < usecases.size(); i++) {
			if (usecases.get(i).id == id) {
				//TODO:モデル整合性チェック
				//TODO:1.ID確認
				//TODO:2.GOTO,INCLUDE命令の整合性（ALT_EXC系のJump命令先がNullではないか）
				//TODO:妥当でなければreturn false;

				usecases.set(i, usecase);
				return true;
			}
		}
		return false;
	}

	/**
	 * ユースケースのリストを編集（Move）
	 *
	 * @param id        　操作対象のUsecaseId
	 * @param direction 移動方向（+1 or -1）
	 */
	public void moveUsecase(int id, int direction) {
		for (int i = 0; i < usecases.size(); i++) {
			if (usecases.get(i).id == id) {
				Usecase suc = usecases.get(i);
				if (direction == 1 && i != usecases.size() - 1) {
					Usecase duc = usecases.get(i + 1);
					usecases.set(i, duc);
					usecases.set(i + 1, suc);
					return;
				} else if (direction == -1 && i != 0) {
					Usecase duc = usecases.get(i - 1);
					usecases.set(i, duc);
					usecases.set(i - 1, suc);
					return;
				}

			}
		}
	}

	public void removeUsecase(int id) {
		for (Usecase u : usecases) {
			if (u.id == id) {
				removeGoal(u.parentLeafGoalId);
				return;
			}
		}
	}

	/**
	 * CreateFGModelで使用中、モデル整合性を担保できないのであとで消すこと
	 *
	 * @param domains
	 */
	@Deprecated
	public void setDomains(List<Domain> domains) {
		this.domains = domains;
	}

	@Deprecated
	public void setUsecases(List<Usecase> usecases) {
		this.usecases = usecases;
	}

}
