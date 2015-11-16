package Models;

import java.io.File;
import java.util.List;

/**
 * Created by 66 on 2015/11/17.
 * //TODO:つくりかけなのでちゃんとするかちゃんとしないかする
 */
public interface FGModel {
	void loadXML(File file);

	void saveXML(File file);

	Goal getGoalById(int id);

	List<Goal> getGoals();

	Domain getDomainById(int id);

	List<Domain> getDomains();

	Usecase getUsecaseById(int id);

	List<Usecase> getUsecases();


}
