import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import Models.*;
import Models.Domain.DomainType;
import Models.Step.StepType;

/*
 *
 *@author Kazuaki hirasawa
 *@version  2015-09-12
 *添付のpythonスクリプトによって生成されるjsonファイルを読んでいます
 */
public class CreateFGModel {

	public static void main(String[] args) {
		try {
			CreateFGModel.make().saveXML(new File("res/test.txt"));
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public static FGModelCore make() throws IOException, JSONException {
		FGModelCore fg = new FGModelCore();
		InputStream input;
		int dmainId = 1;
		HashMap<String, Integer> usecaseName2Id = new HashMap<String, Integer>();
		ArrayList<Goal> goals = new ArrayList<>();
		ArrayList<Usecase> usecases = new ArrayList<>();
		ArrayList<Domain> domains = new ArrayList<>();
		ArrayList<String> domainNames = new ArrayList<>();
		Domain temp;
		try {
			input = new FileInputStream("res/UsecaseParse.json");//生成したjsonファイルのパス
			int size = input.available();
			byte[] buffer = new byte[size];
			input.read(buffer);
			input.close();

			String json = new String(buffer);
			JSONObject jsonObj = new JSONObject(json);
			/*
			 * TODO Domainに値を入れる所を作る
			 *
			 */

			Iterator iter = jsonObj.keys();
			while (iter.hasNext()) {
				String key = (String) iter.next();
				JSONArray JssonArr = jsonObj.getJSONArray(key);
				for (int i = 0; i < JssonArr.length(); i++) {
					JSONObject stepJson = (JSONObject) JssonArr.get(i);
					//includeでない文のsub obj targetをDomainに追加
					if (!stepJson.getString("type").equals("INCLUDE") && !domainNames.contains(stepJson.getString("sub"))) {
						domainNames.add(stepJson.getString("sub"));
					}
					if (!stepJson.getString("type").equals("INCLUDE") && !domainNames.contains(stepJson.getString("obj"))) {
						domainNames.add(stepJson.getString("obj"));
					}
					if (!stepJson.getString("type").equals("INCLUDE") && !domainNames.contains(stepJson.getString("target"))) {
						domainNames.add(stepJson.getString("target"));
					}
				}

			}
			for (String d : domainNames) {
				if (d.length() == 0) {
					continue;
				}
				temp = new Domain();
				temp.name = d;
				temp.id = dmainId++;
				temp.domainType = DomainType.NONE;
				temp.x = 0;
				temp.y = 0;
				domains.add(temp);
			}
			fg.domains = domains;

			iter = jsonObj.keys();
			int usecaseId = 1;
			while (iter.hasNext()) {
				String key = (String) iter.next();
				usecaseName2Id.put(key, usecaseId++);     //ユースケース名とユースケースidをヒモづける

			}


/**************************************************************************************************/
			//各ユースケース名を取得
			iter = jsonObj.keys();

			//Goal用のID
			int id = 0;

			//各ユースケースを見るためのループ
			while (iter.hasNext()) {

				//Usecase初期設定
				String key = (String) iter.next();
				Usecase uc = new Usecase();
				uc.init(usecaseName2Id.get(key), key, id);

				//関連ゴール初期設定
				Goal g = new Goal();
				g.isEnableForAsIs = false;
				g.isEnableForToBe = false;
				g.id = id;
				g.childrenType = Goal.ChildrenType.OR;
				g.parentId = -1;
				g.name = key;
				g.x = 0;
				g.y = 0;

				//Step作成
				ArrayList<Step> steps = new ArrayList<>();
				JSONArray JssonArr = jsonObj.getJSONArray(key);

				//各ユースケース名中の各ステップのjsonObjを読む
				for (int i = 0; i < JssonArr.length(); i++) {
					JSONObject stepJson = (JSONObject) JssonArr.get(i);
					Step step = new Step();
					step.subjectDomainId = DomainName2Id(domains, stepJson.getString("sub"));
					stepJson.get("target");
					if (stepJson.get("target").equals("")) {
						step.objectDomainId = DomainName2Id(domains, stepJson.getString("obj"));
					} else {
						step.objectDomainId = DomainName2Id(domains, stepJson.getString("target"));
					}
					step.id = stepJson.getInt("step_id");
					step.Event = stepJson.getString("event");
					switch (stepJson.getString("type")) {
						case "ACTION":
							step.stepType = StepType.ACTION;

							break;
						case "GOTO":
							step.stepType = StepType.GOTO;
							step.gotoStepId = Integer.valueOf(stepJson.getString("target"));
							break;
						case "INCLUDE":
							step.stepType = StepType.INCLUDE;
							step.includeUsecaseId = usecaseName2Id.get(stepJson.getString("target"));
							break;
						case "ALT_INDEX":
							step.stepType = StepType.ALT_INDEX;
							step.condition = stepJson.getString("sentence");
							break;
						case "EXC_INDEX":
							step.stepType = StepType.EXC_INDEX;
							step.condition = stepJson.getString("sentence");
							break;


						default:
							break;
					}
					steps.add(step);
					uc.setFlow(steps);
				}
				usecases.add(uc);
				goals.add(g);
				id++;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		fg.usecases = usecases;
		fg.goals = goals;
		return fg;
	}

	//ドメイン名,uc名からidを返す、ない場合は-1を返す
	public static int DomainName2Id(ArrayList<Domain> domains, String name) {
		for (Domain d : domains) {
			if (d.name.equals(name)) {
				return d.id;
			}
		}
		return -1;
	}

	public static int UsecaseName2Id(ArrayList<Usecase> usecases, String name) {
		for (Usecase uc : usecases) {
			if (uc.name.equals(name)) {
				return uc.id;
			}
		}
		return -1;
	}

	public static ArrayList<String> distinct(ArrayList<String> slist) {
		return new ArrayList<String>(new LinkedHashSet<String>(slist));
	}
}


