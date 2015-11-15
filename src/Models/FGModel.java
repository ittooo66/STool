package Models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * FGModel本体、JAXBマーシャル用に全Publicで定義してるのでAdapter噛ましてつかうこと
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
}
