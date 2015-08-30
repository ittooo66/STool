package Models;

import java.util.List;

public class Usecase {

	//名前と番号
	public int id;
	public int name;

	//生成元のゴールID
	public int parentLeafGoalId;

	/** ステップ列すべて
	 *  Flowにステップ列をまとめて入れてほしい。（コンテクスト図生成のときに一括でなめれるとうれしいため）
	 *  順番は基本系列＝＞例外系列群＝＞代替系列群とする。以下のインデックス（endOf...）で還元可能にする
	 */
	public List<Step> flow;

	/**
	 * ステップ列の分解用のインデックス
	 * 規則
	 * 		基本系列が、0～endOfMainFlowまで、
	 * 		例外系列群（ExceptionalFlowIndex）がそのあとに入って
	 * 		代替系列群（AlternativeFlowIndex）がそのあと
	 *
	 * e.g. 主系列４step＋例外系列２step×２＋代替系列３step×１のとき
	 * 		List<Step> Flow.size()：11
	 * 		Flow[0,1,2,3]:基本系列
	 * 		Flow[4,5]:例外系列１
	 * 		Flow[6,7]:例外系列２
	 * 		Flow[8,9,10]代替系列１
	 * となり、
	 * 		endOfMainFlowIndex == 4
	 * 		endOfExceptionalFlowIndexes == <6,8>
	 * 		endOfAlternativeFlowIndexes == <11>
	 * とする
	 */
	public int endOfMainFlowIndex;
	public List<Integer> endOfExceptionalFlowIndexes;
	public List<Integer> endOfAlternativeFlowIndexes;


	//FlowからMainFlowを抽出
	public List<Step> getMainFlow(){
		//TODO
		return null;
	}

	//FlowからExceptionalFlowのリストを抽出
	public List<List<Step>> getExceptionalFlowList(){
		//TODO
		return null;
	}

	// /FlowからAlternativeFlowのリストを抽出
	public List<List<Step>> getAlternativeFlowList(){
		//TODO
		return null;
	}

}
