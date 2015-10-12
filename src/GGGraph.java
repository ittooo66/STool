import processing.core.PApplet;

import java.util.ArrayList;
import java.util.List;

/**
 * ゴールグラフエディタタブで出力するProcessing部分
 */
public class GGGraph extends PApplet{

	int mouseX_temp,mouseY_temp;

	//本体
	SToolEditor sToolEditor;

	//ゴール選択中か否か
	boolean isGoalSelected;

	public GGGraph(SToolEditor sToolEditor){
		this.sToolEditor=sToolEditor;
	}

	public void draw(){
		background(0);
		stroke(200,200,200);

		text("Version:"+sToolEditor.version.toString()+", Mode:"+sToolEditor.viewmode.toString()+", Resolution:"+width+","+height,10,20);

		line(mouseX_temp,10,mouseX_temp,height-10);
		line(10,mouseY_temp,width-10,mouseY_temp);

	}

	public  void setup(){
		size(1024,768);
		noLoop();
	}

	public void mousePressed(){
		mouseX_temp=mouseX;
		mouseY_temp=mouseY;
		redraw();
	}

}
