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

	public Usecase(String name) {
		this.name = name;
		flow = new ArrayList<>();
	}

	//FlowからMainFlowを抽出
	public List<Step> getMainFlow() {
		List<Step> mainFlow = new ArrayList<>();
		for (int i = 0; i < flow.size(); i++) {
			if (flow.get(i).stepType != Step.StepType.ALT_INDEX && flow.get(i).stepType != Step.StepType.EXC_INDEX) {
				mainFlow.add(flow.get(i));
			} else {
				return mainFlow;
			}
		}
		return mainFlow;
	}

	//FlowからExceptionalFlowのリストを抽出
	public List<List<Step>> getExceptionalFlowList() {
		List<List<Step>> exceptionalFlowList = new ArrayList<>();
		for (int i = 0; i < flow.size(); i++) {
			if (flow.get(i).stepType == Step.StepType.EXC_INDEX) {
				List<Step> exceptionalFlow = new ArrayList<>();
				for (int j = 0; j < flow.size(); j++) {
					exceptionalFlow.add(flow.get(i + j));
					if (flow.get(i + j + 1).stepType == Step.StepType.EXC_INDEX ||
							(flow.get(i + j + 1).stepType == Step.StepType.ALT_INDEX)) {
						break;
					}
				}
				exceptionalFlowList.add(exceptionalFlow);
			}
		}
		return exceptionalFlowList;
	}

	// /FlowからAlternativeFlowのリストを抽出
	public List<List<Step>> getAlternativeFlowList() {
		List<List<Step>> alternativeFlowList = new ArrayList<>();
		for (int i = 0; i < flow.size(); i++) {
			if (flow.get(i).stepType == Step.StepType.ALT_INDEX) {
				List<Step> alternativeFlow = new ArrayList<>();
				for (int j = 0; j < flow.size(); j++) {
					alternativeFlow.add(flow.get(i + j));
					if (flow.get(i + j + 1).stepType == Step.StepType.EXC_INDEX ||
							(flow.get(i + j + 1).stepType == Step.StepType.ALT_INDEX)) {
						break;
					}
				}
				alternativeFlowList.add(alternativeFlow);
			}
		}
		return alternativeFlowList;
	}

}
