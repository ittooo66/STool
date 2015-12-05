package Metrics;

import Models.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Metrics計測クラス
 */
public class Metrics {

	/**
	 * CEのリストを取得
	 *
	 * @param scenario
	 * @return
	 */
	public static List<CE> getCEs(Scenario scenario) {
		List<CE> ces = new ArrayList<>();
		for (Step s : scenario.getSteps()) {
			//cesに存在するか判定
			boolean isInCes = false;
			for (CE ce : ces) {
				if (ce.event.equals(s.Event) &&
						ce.objectDomainId == s.objectDomainId &&
						ce.subjectDomainId == s.subjectDomainId) {
					//あったならカウントアップ
					ce.count++;
					isInCes = true;
				}
			}
			//なかったなら新規CE生成
			if (!isInCes) ces.add(new CE(s.Event, s.subjectDomainId, s.objectDomainId));
		}
		return ces;
	}

	public static int getANOS(Usecase uc, FGModelAdapter fgm) {
		int ANOS = 0;
		try {
			for (Step s : uc.getAllActionStep()) {
				if (fgm.getDomainById(s.objectDomainId).domainType == Domain.DomainType.BIDDABLE ||
						fgm.getDomainById(s.subjectDomainId).domainType == Domain.DomainType.BIDDABLE)
					ANOS++;
			}

			//Include先のユースケースのANOSも含める
			for (Step s : uc.getAllIncludeStep()) {
				ANOS += getANOS(fgm.getUsecaseById(s.includeUsecaseId), fgm);
			}
		} catch (Exception e) {
			//モデルが不正
			e.printStackTrace();
			return -1;
		}
		return ANOS;
	}

	public static int getACC(Usecase uc, FGModelAdapter fgm) {
		int ACC = 0;
		try {
			List<Step> mainFlow = uc.getMainFlow();
			for (Step s : mainFlow) {
				if (s.stepType == Step.StepType.ACTION) {
					if (fgm.getDomainById(s.subjectDomainId).domainType == Domain.DomainType.BIDDABLE ||
							fgm.getDomainById(s.objectDomainId).domainType == Domain.DomainType.BIDDABLE) {
						ACC++;
						break;
					} else if (s.stepType == Step.StepType.INCLUDE) {
						//Include先のユースケースのACCも含める
						ACC += getACC(fgm.getUsecaseById(s.includeUsecaseId), fgm);
					}
				}
			}

			List<List<Step>> altFlowList = uc.getAlternativeFlowList();
			for (List<Step> stepList : altFlowList) {
				for (Step s : stepList) {
					if (s.stepType == Step.StepType.ACTION) {
						if (fgm.getDomainById(s.subjectDomainId).domainType == Domain.DomainType.BIDDABLE ||
								fgm.getDomainById(s.objectDomainId).domainType == Domain.DomainType.BIDDABLE) {
							ACC++;
							break;
						}
					} else if (s.stepType == Step.StepType.INCLUDE) {
						//Include先のユースケースのACCも含める
						ACC += getACC(fgm.getUsecaseById(s.includeUsecaseId), fgm);
					}
				}
			}

			List<List<Step>> excFlowList = uc.getExceptionalFlowList();
			for (List<Step> stepList : excFlowList) {
				for (Step s : stepList) {
					if (s.stepType == Step.StepType.ACTION) {
						if (fgm.getDomainById(s.subjectDomainId).domainType == Domain.DomainType.BIDDABLE ||
								fgm.getDomainById(s.objectDomainId).domainType == Domain.DomainType.BIDDABLE) {
							ACC++;
							break;
						}
					} else if (s.stepType == Step.StepType.INCLUDE) {
						//Include先のユースケースのACCも含める
						ACC += getACC(fgm.getUsecaseById(s.includeUsecaseId), fgm);
					}
				}
			}
		} catch (Exception e) {
			//モデルが不正
			e.printStackTrace();
			return -1;
		}
		return ACC;
	}

	public static int getNE(Domain d, FGModelAdapter fgm) {
		List<String> eventListOfThisDomain = new ArrayList<>();
		List<Integer> subjectIdListOfThisDomain = new ArrayList<>();
		List<Integer> objectIdListOfThisDomain = new ArrayList<>();

		try {
			//fgmodel中のすべてのActionStepを取得
			List<Step> allActionStepList = new ArrayList<>();
			for (Usecase uc : fgm.getUsecases()) {
				//カウント対象でなければContinure
				if (fgm.getVersion() == FGModelAdapter.VERSION.ASIS && !fgm.getGoalById(uc.parentLeafGoalId).isEnableForAsIs)
					continue;
				if (fgm.getVersion() == FGModelAdapter.VERSION.TOBE && !fgm.getGoalById(uc.parentLeafGoalId).isEnableForToBe)
					continue;

				allActionStepList.addAll(uc.getAllActionStep().stream().collect(Collectors.toList()));
			}

			for (Step s : allActionStepList) {
				//Actionの片側以上が自分のドメインだった場合
				if (s.subjectDomainId == d.id || s.objectDomainId == d.id) {
					//一旦新イベントとしてみなす
					boolean isNewEvent = true;

					//重複していればfalseに
					for (int i = 0; i < eventListOfThisDomain.size(); i++) {
						if (s.Event.equals(eventListOfThisDomain.get(i)) &&
								subjectIdListOfThisDomain.get(i) == s.subjectDomainId &&
								objectIdListOfThisDomain.get(i) == s.objectDomainId)
							isNewEvent = false;
					}

					//重複してなかった場合
					if (isNewEvent) {
						eventListOfThisDomain.add(s.Event);
						subjectIdListOfThisDomain.add(s.subjectDomainId);
						objectIdListOfThisDomain.add(s.objectDomainId);
					}
				}
			}
		} catch (Exception e) {
			//モデルが不正
			e.printStackTrace();
			return -1;
		}

		return eventListOfThisDomain.size();
	}

	public static int getUCP(FGModelAdapter fgm) {
		int ucp = 0;
		for (Usecase uc : fgm.getUsecases()) {
			//カウント対象でなければContinue
			if (fgm.getVersion() == FGModelAdapter.VERSION.ASIS && !fgm.getGoalById(uc.parentLeafGoalId).isEnableForAsIs)
				continue;
			if (fgm.getVersion() == FGModelAdapter.VERSION.TOBE && !fgm.getGoalById(uc.parentLeafGoalId).isEnableForToBe)
				continue;

			for (Step s : uc.getAllActionStep()) {
				//Biddable Domainの関与するイベントのみ３ポイントでほかは１ポイント。
				//両端で計測するので２倍
				if (fgm.getDomainById(s.subjectDomainId).domainType == Domain.DomainType.BIDDABLE) {
					ucp += 2;
				}
				if (fgm.getDomainById(s.objectDomainId).domainType == Domain.DomainType.BIDDABLE) {
					ucp += 2;
				}
				ucp += 2;
			}
		}
		return ucp;
	}
}
