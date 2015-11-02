package Models;

import java.util.ArrayList;
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
		List<List<Step>> exceptionalFlowList = new ArrayList<>();
		//TODO
		return exceptionalFlowList;
	}

	// /FlowからAlternativeFlowのリストを抽出
	public List<List<Step>> getAlternativeFlowList() {
		List<List<Step>> alternativeFlowList = new ArrayList<>();
		//TODO
		return alternativeFlowList;
	}

}
