package Models;

import java.util.List;

public class Usecase {

	//名前と番号
	public int id;
	public String name;

	//生成元のゴールID
	public int parentLeafGoalId;

	/**
	 * ステップ列すべて
	 * Flowにステップ列をまとめて入れる。
	 * 代替系列どうすんの、とかについてはStep.javaのほう参照
	 */
	public List<Step> flow;

	//FlowからMainFlowを抽出
	public List<Step> getMainFlow() {
		//TODO
		return null;
	}

	//FlowからExceptionalFlowのリストを抽出
	public List<List<Step>> getExceptionalFlowList() {
		//TODO
		return null;
	}

	// /FlowからAlternativeFlowのリストを抽出
	public List<List<Step>> getAlternativeFlowList() {
		//TODO
		return null;
	}

}
