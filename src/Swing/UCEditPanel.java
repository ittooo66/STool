package Swing;

import Processing.UCGraph;

import javax.swing.*;
import java.awt.*;

/**
 * Created by 66 on 2015/11/03.
 * UseCaseEditor�̉E����
 */
public class UCEditPanel extends JPanel {
	private final UCGraph ucg;
	private final SToolEditor ste;

	//UCEditor�R���|�[�l���g
	private JButton ucEditRemove, ucEditEdit;
	private JTextArea ucEditUsecaseName;
	private JComboBox ucEditGoalComboBox;

	public UCEditPanel(SToolEditor ste, UCGraph ucg) {
		this.ste = ste;
		this.ucg = ucg;

		this.setLayout(null);
		this.setPreferredSize(new Dimension(200, 0));
		//TODO:�����Ɋe��R���|�[�l���g�ǉ����\�b�h���L��

	}

	public void redraw() {

	}
}
