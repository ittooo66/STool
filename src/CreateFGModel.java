import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import Models.Domain;
import Models.Domain.DomainType;
import Models.Step;
import Models.Step.StepType;
import Models.Usecase;
/*
 * 
 *@author Kazuaki hirasawa
 *@version  2015-09-12 
 *添付のpythonスクリプトによって生成されるjsonファイルを読んでいます
 */
public class CreateFGModel {

	public static FGModel make() throws IOException, JSONException {
		FGModel fg = new FGModel();
		InputStream input;
		int dmainId = 1;
		HashMap<String , Integer> usecaseName2Id = new HashMap<String, Integer>();
		ArrayList<Usecase> usecases = new ArrayList<Usecase>();
		ArrayList<Domain> domains = new ArrayList<Domain>();
		ArrayList<String> domainNames = new ArrayList<String>();
		Domain temp;
		try{
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

			Iterator iter  = jsonObj.keys();
			while(iter.hasNext()){
				String key = (String) iter.next();
				JSONArray JssonArr =  jsonObj.getJSONArray(key);
				for(int i = 0 ; i<JssonArr.length() ; i++){
					JSONObject stepJson = (JSONObject) JssonArr.get(i);
					//includeでない文のsub obj targetをDomainに追加
					if (!stepJson.getString("type").equals("INCLUDE") && !domainNames.contains(stepJson.getString("sub"))){
						domainNames.add(stepJson.getString("sub"));
					}
					if (!stepJson.getString("type").equals("INCLUDE") && !domainNames.contains(stepJson.getString("obj"))){
						domainNames.add(stepJson.getString("obj"));
					}
					if (!stepJson.getString("type").equals("INCLUDE") && !domainNames.contains(stepJson.getString("target"))){
						domainNames.add(stepJson.getString("target"));
					}
				}

			}
			for(String d : domainNames){
				if (d.length()==0){
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
			fg.setDomains(domains);

			iter  = jsonObj.keys();
			int usecaseId = 1;
			while(iter.hasNext()){
				String key = (String) iter.next();
				usecaseName2Id.put(key, usecaseId ++);     //ユースケース名とユースケースidをヒモづける

			}









/**************************************************************************************************/
			iter = jsonObj.keys();  //各ユースケース名を所得
			while(iter.hasNext()){ //各ユースエースを見るためのループ
				String key = (String) iter.next();
				Usecase uc = new Usecase();
				ArrayList<Step> steps = new ArrayList<Step>();
				uc.name = key;
				uc.id = usecaseName2Id.get(key);
				uc.parentLeafGoalId = -1;
				JSONArray JssonArr =  jsonObj.getJSONArray(key);
				for(int i = 0 ; i<JssonArr.length() ; i++){  //各ユースケース名中の各ステップのjsonObjを読む
					JSONObject stepJson = (JSONObject) JssonArr.get(i);
					Step step = new Step();
					step.subjectDomainId = DomainName2Id(domains, stepJson.getString("sub"));
					stepJson.get("target");
					if (stepJson.get("target").equals("")){
						step.objectDomainId = DomainName2Id(domains, stepJson.getString("obj"));
					}else{
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
					uc.flow = steps;
				}
				usecases.add(uc);
			}
		}catch(FileNotFoundException e){
			e.printStackTrace();
		}
		fg.setUsecases(usecases);
		return fg;
	}
	//ドメイン名,uc名からidを返す、ない場合は-1を返す
	public static int DomainName2Id(ArrayList<Domain> domains, String name){
		for (Domain d : domains){
			if (d.name.equals(name)){
				return d.id;
			}
		}
		return -1;
	}
	public static int UsecaseName2Id(ArrayList<Usecase> usecases,String name){
		for (Usecase uc : usecases){
			if (uc.name.equals(name)){
				return uc.id;
			}
		}
		return -1;
	}
	public static ArrayList<String> distinct(ArrayList<String> slist) {
		return new ArrayList<String>(new LinkedHashSet<String>(slist));
	}
}


