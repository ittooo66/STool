package Models;

import javax.xml.bind.JAXB;
import java.io.File;
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

	public void loadXML(File file) {
		FGModel fgm = JAXB.unmarshal(file, FGModel.class);
		this.goals = fgm.goals;
		this.usecases = fgm.usecases;
		this.domains = fgm.domains;
	}

	public void saveXML(File file) {
		JAXB.marshal(this, file);
	}
}
