package Models;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

public class Scenario {
	//Step列(ACTIONのみ)をシナリオとみなす
	private List<Step> steps;
	private List<Usecase> rootUsecase;

	public Scenario() {
		steps = new ArrayList<>();
		rootUsecase = new ArrayList<>();
	}

	public void addStep(Step s, Usecase uc) {
		steps.add(s);
		rootUsecase.add(uc);
	}

	public boolean removeStep(int index) {
		if (index < 0) return false;
		if (index >= steps.size()) return false;
		steps.remove(index);
		rootUsecase.remove(index);
		return true;
	}

	public boolean moveStep(boolean b, int index) {
		for (int i = 0; i < steps.size(); i++) {
			if (i == index) {
				if (b) {
					if (index < steps.size() - 1) {
						Step s = steps.get(index + 1);
						steps.set(index + 1, steps.get(index));
						steps.set(index, s);
						Usecase uc = rootUsecase.get(index + 1);
						rootUsecase.set(index + 1, rootUsecase.get(index));
						rootUsecase.set(index, uc);
						return true;
					}
				} else {
					if (index > 0) {
						Step s = steps.get(index - 1);
						steps.set(index - 1, steps.get(index));
						steps.set(index, s);
						Usecase uc = rootUsecase.get(index - 1);
						rootUsecase.set(index - 1, rootUsecase.get(index));
						rootUsecase.set(index, uc);
						return true;
					}
				}
			}
		}
		return false;
	}

	public String getStepName(int index, FGModelAdapter fgm) {
		return steps.get(index).getStepName(fgm, rootUsecase.get(index));
	}

	public int size() {
		return steps.size();
	}
}
