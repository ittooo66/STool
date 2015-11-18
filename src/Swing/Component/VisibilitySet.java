package Swing.Component;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 66 on 2015/11/18.
 * 可視性セット、各コンポーネントのSetVisibleを更新するグループ
 */
public class VisibilitySet {
	public List<JComponent> jComponents;

	public VisibilitySet() {
		jComponents = new ArrayList<>();
	}

	public void add(JComponent jc) {
		jComponents.add(jc);
	}

	public void setVisible(boolean isVisible) {
		for (JComponent jc : jComponents) {
			jc.setVisible(isVisible);
		}
	}
}
