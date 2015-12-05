package Models;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Scenario {
	//Step列(ACTIONのみ)をシナリオとみなす
	private List<Step> stepsAsIs;
	private List<Step> stepsToBe;
	private List<Usecase> rootUsecaseAsIs;
	private List<Usecase> rootUsecaseToBe;
	private boolean isAsIs;

	public Scenario() {
		stepsAsIs = new ArrayList<>();
		stepsToBe = new ArrayList<>();
		rootUsecaseAsIs = new ArrayList<>();
		rootUsecaseToBe = new ArrayList<>();
		isAsIs = true;
	}

	public boolean isAsIs() {
		return isAsIs;
	}

	public void switchVersion() {
		isAsIs = !isAsIs;
	}

	public void addStep(Step s, Usecase uc) {
		if (isAsIs) {
			stepsAsIs.add(s);
			rootUsecaseAsIs.add(uc);
		} else {
			stepsToBe.add(s);
			rootUsecaseToBe.add(uc);
		}
	}

	public boolean removeStep(int index) {
		if (index < 0) return false;
		if (isAsIs) {
			if (index >= stepsAsIs.size()) return false;
			stepsAsIs.remove(index);
			rootUsecaseAsIs.remove(index);
		} else {
			if (index >= stepsToBe.size()) return false;
			stepsToBe.remove(index);
			rootUsecaseToBe.remove(index);
		}
		return true;
	}

	public boolean moveStep(boolean b, int index) {
		if (isAsIs) {
			for (int i = 0; i < stepsAsIs.size(); i++) {
				if (i == index) {
					if (b) {
						if (index < stepsAsIs.size() - 1) {
							Step s = stepsAsIs.get(index + 1);
							stepsAsIs.set(index + 1, stepsAsIs.get(index));
							stepsAsIs.set(index, s);
							Usecase uc = rootUsecaseAsIs.get(index + 1);
							rootUsecaseAsIs.set(index + 1, rootUsecaseAsIs.get(index));
							rootUsecaseAsIs.set(index, uc);
							return true;
						}
					} else {
						if (index > 0) {
							Step s = stepsAsIs.get(index - 1);
							stepsAsIs.set(index - 1, stepsAsIs.get(index));
							stepsAsIs.set(index, s);
							Usecase uc = rootUsecaseAsIs.get(index - 1);
							rootUsecaseAsIs.set(index - 1, rootUsecaseAsIs.get(index));
							rootUsecaseAsIs.set(index, uc);
							return true;
						}
					}
				}
			}
		} else {
			for (int i = 0; i < stepsToBe.size(); i++) {
				if (i == index) {
					if (b) {
						if (index < stepsToBe.size() - 1) {
							Step s = stepsToBe.get(index + 1);
							stepsToBe.set(index + 1, stepsToBe.get(index));
							stepsToBe.set(index, s);
							Usecase uc = rootUsecaseToBe.get(index + 1);
							rootUsecaseToBe.set(index + 1, rootUsecaseToBe.get(index));
							rootUsecaseToBe.set(index, uc);
							return true;
						}
					} else {
						if (index > 0) {
							Step s = stepsToBe.get(index - 1);
							stepsToBe.set(index - 1, stepsToBe.get(index));
							stepsToBe.set(index, s);
							Usecase uc = rootUsecaseToBe.get(index - 1);
							rootUsecaseToBe.set(index - 1, rootUsecaseToBe.get(index));
							rootUsecaseToBe.set(index, uc);
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	public String getStepName(int index, FGModelAdapter fgm) {
		if (isAsIs) {
			return stepsAsIs.get(index).getStepName(fgm, rootUsecaseAsIs.get(index));
		} else {
			return stepsToBe.get(index).getStepName(fgm, rootUsecaseToBe.get(index));
		}
	}

	public int size() {
		if (isAsIs) {
			return stepsAsIs.size();
		} else {
			return stepsToBe.size();
		}
	}

	public List<Step> getSteps() {
		if (isAsIs) {
			return stepsAsIs.stream().map(s -> (Step) s.clone()).collect(Collectors.toList());
		} else {
			return stepsToBe.stream().map(s -> (Step) s.clone()).collect(Collectors.toList());
		}
	}
}
