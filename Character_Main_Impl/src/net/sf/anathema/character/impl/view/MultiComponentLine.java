package net.sf.anathema.character.impl.view;

import net.disy.commons.swing.layout.grid.GridDialogLayoutData;
import net.sf.anathema.character.model.IIntegerDescription;
import net.sf.anathema.character.view.IIntegerView;
import net.sf.anathema.character.view.IMultiComponentLine;
import net.sf.anathema.lib.gui.widgets.IntegerSpinner;
import net.sf.anathema.lib.workflow.textualdescription.ITextView;
import net.sf.anathema.lib.workflow.textualdescription.view.LineTextView;

import javax.swing.GroupLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.GridLayout;
import java.util.List;

import static java.lang.Short.MAX_VALUE;
import static javax.swing.GroupLayout.Alignment.CENTER;
import static javax.swing.GroupLayout.DEFAULT_SIZE;
import static javax.swing.LayoutStyle.ComponentPlacement.RELATED;
import static javax.swing.LayoutStyle.ComponentPlacement.UNRELATED;

public class MultiComponentLine implements IMultiComponentLine {
  private static final int FIELD_COLUMNS = 6;
  private final JPanel content;
  private final List<JPanel> buttonPanels;
  private GroupLayout.SequentialGroup horizontal;
  private GroupLayout.ParallelGroup vertical;
  private GroupLayout layout;
  private JPanel fieldPanel;
  private int numberOfViews = 0;

  public MultiComponentLine(JPanel content, List<JPanel> buttonPanels) {
    this.content = content;
    this.buttonPanels = buttonPanels;
  }

  @Override
  public void init() {
    content.add(new JPanel());
    fieldPanel = new JPanel();
    layout = new GroupLayout(fieldPanel);
    fieldPanel.setLayout(layout);
    horizontal = layout.createSequentialGroup();
    vertical = layout.createParallelGroup(CENTER);
    layout.setHorizontalGroup(horizontal);
    layout.setVerticalGroup(vertical);
    GridDialogLayoutData panelLayout = new GridDialogLayoutData();
    panelLayout.setGrabExcessHorizontalSpace(true);
    content.add(fieldPanel, panelLayout);

    JPanel buttonPanel = new JPanel(new GridLayout(1, 0));
    buttonPanels.add(buttonPanel);
    content.add(buttonPanel);
  }

  @Override
  public ITextView addFieldsView(String labelText) {
    LineTextView view = new LineTextView(FIELD_COLUMNS);
    addLabeledComponent(labelText, view.getComponent());
    numberOfViews++;
    return view;
  }

  @Override
  public IIntegerView addIntegerView(String labelText, IIntegerDescription description) {
    IntegerSpinner spinner = new IntegerSpinner(description.getValue());
    spinner.setPreferredWidth(48);
    spinner.setStepSize(5);
    JComponent component = spinner.getComponent();
    addLabeledComponent(labelText, component);
    return spinner;
  }

  private void addLabeledComponent(String text, JComponent component) {
    if (numberOfViews > 0) {
      horizontal.addPreferredGap(UNRELATED, DEFAULT_SIZE, MAX_VALUE);
    }
    JLabel label = new JLabel(text);
    horizontal.addComponent(label);
    horizontal.addPreferredGap(RELATED, DEFAULT_SIZE, MAX_VALUE);
    horizontal.addComponent(component);
    vertical.addComponent(label);
    vertical.addComponent(component);
  }
}
