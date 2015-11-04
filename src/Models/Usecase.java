package Models;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
	private List<Step> flow;

	public Usecase(int id, String name, int parentLeafGoalId) {
		this.id = id;
		this.name = name;
		this.parentLeafGoalId = parentLeafGoalId;
		flow = new ArrayList<>();
	}

	/**
	 * MainFlowを抽出
	 *
	 * @return 主系列
	 */
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

	/**
	 * ExceptionalFlowのリストを抽出
	 *
	 * @return 例外系列リスト
	 */
	public List<List<Step>> getExceptionalFlowList() {
		List<List<Step>> exceptionalFlowList = new ArrayList<>();
		for (int i = 0; i < flow.size(); i++) {
			if (flow.get(i).stepType == Step.StepType.EXC_INDEX) {
				List<Step> exceptionalFlow = new ArrayList<>();
				for (int j = 0; i + j < flow.size(); j++) {
					exceptionalFlow.add(flow.get(i + j));
					if (i + j + 1 < flow.size()) {
						Step s = flow.get(i + j + 1);
						if (s.stepType == Step.StepType.EXC_INDEX || s.stepType == Step.StepType.ALT_INDEX) {
							break;
						}
					}
				}
				exceptionalFlowList.add(exceptionalFlow);
			}
		}
		return exceptionalFlowList;
	}

	/**
	 * AlternativeFlowのリストを抽出
	 *
	 * @return 代替系列
	 */
	public List<List<Step>> getAlternativeFlowList() {
		List<List<Step>> alternativeFlowList = new ArrayList<>();
		for (int i = 0; i < flow.size(); i++) {
			if (flow.get(i).stepType == Step.StepType.ALT_INDEX) {
				List<Step> alternativeFlow = new ArrayList<>();
				for (int j = 0; i + j < flow.size(); j++) {
					alternativeFlow.add(flow.get(i + j));
					if (i + j + 1 < flow.size()) {
						Step s = flow.get(i + j + 1);
						if (s.stepType == Step.StepType.EXC_INDEX || s.stepType == Step.StepType.ALT_INDEX) {
							break;
						}
					}
				}
				alternativeFlowList.add(alternativeFlow);
			}
		}
		return alternativeFlowList;
	}

	/**
	 * AlternativeFlowを削除
	 *
	 * @param index AlternativeFlowListのindex
	 */
	public void removeAlternativeFlow(int index) {
		List<Step> mainFlow = getMainFlow();
		List<List<Step>> altFlowList = getAlternativeFlowList();
		List<List<Step>> excFlowList = getExceptionalFlowList();
		List<Step> newFlow = mainFlow.stream().collect(Collectors.toList());
		for (int i = 0; i < altFlowList.size(); i++) {
			if (index != i) newFlow.addAll(altFlowList.get(i).stream().collect(Collectors.toList()));
		}
		for (List<Step> sList : excFlowList) newFlow.addAll(sList.stream().collect(Collectors.toList()));
		flow = newFlow;
	}

	/**
	 * ExceptionalFlowを削除
	 *
	 * @param index ExceptionalFlowListのindex
	 */
	public void removeExceptionalFlow(int index) {
		List<Step> mainFlow = getMainFlow();
		List<List<Step>> altFlowList = getAlternativeFlowList();
		List<List<Step>> excFlowList = getExceptionalFlowList();
		List<Step> newFlow = mainFlow.stream().collect(Collectors.toList());
		for (List<Step> sList : altFlowList) newFlow.addAll(sList.stream().collect(Collectors.toList()));
		for (int i = 0; i < excFlowList.size(); i++) {
			if (index != i) newFlow.addAll(excFlowList.get(i).stream().collect(Collectors.toList()));
		}
		flow = newFlow;
	}

	/**
	 * AlternativeFlowを追加
	 *
	 * @param condition 遷移条件
	 */
	public void addAlternativeFlow(String condition) {
		Step s = new Step();
		s.stepType = Step.StepType.ALT_INDEX;
		s.condition = condition;
		s.sourceStepId = -1;
		flow.add(s);
	}

	/**
	 * ExceptionalFlowを追加
	 *
	 * @param condition 遷移条件
	 */
	public void addExceptionalFlow(String condition) {
		Step s = new Step();
		s.stepType = Step.StepType.EXC_INDEX;
		s.condition = condition;
		s.sourceStepId = -1;
		flow.add(s);
	}

	/**
	 * Stepを追加
	 *
	 * @param flowType  追加先のフローの種類(0:main,1:alt,2:exc)
	 * @param flowIndex 追加先のフローのindex
	 * @param step      追加するステップ
	 */
	public void addStep(int flowType, int flowIndex, Step step) {
		List<Step> mainFlow = getMainFlow();
		List<List<Step>> altFlowList = getAlternativeFlowList();
		List<List<Step>> excFlowList = getExceptionalFlowList();
		if (flowType == 0) {
			mainFlow.add(step);
		} else if (flowType == 1) {
			altFlowList.get(flowIndex).add(step);
		} else if (flowType == 2) {
			excFlowList.get(flowIndex).add(step);
		}
		flow = new ArrayList<>();
		flow.addAll(mainFlow.stream().collect(Collectors.toList()));
		for (List<Step> ls : altFlowList) {
			flow.addAll(ls.stream().collect(Collectors.toList()));
		}
		for (List<Step> ls : excFlowList) {
			flow.addAll(ls.stream().collect(Collectors.toList()));
		}
	}

	public List<Step> getFlow() {
		return flow;
	}

	@Deprecated
	public void setFlow(List<Step> steps) {
		flow = steps;
	}
}
