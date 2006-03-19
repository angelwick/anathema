package net.sf.anathema.campaign.presenter;

import java.awt.Dimension;

import javax.swing.text.DefaultStyledDocument;

import net.sf.anathema.campaign.presenter.view.IBasicItemDescriptionView;
import net.sf.anathema.framework.itemdata.IBasicItemData;
import net.sf.anathema.framework.presenter.TextEditorProperties;
import net.sf.anathema.framework.styledtext.presentation.StyledTextManager;
import net.sf.anathema.lib.resources.IResources;
import net.sf.anathema.lib.workflow.textualdescription.TextualPresentation;

public abstract class AbstractBasicItemPresenter {

  private IBasicItemData item;
  private final IResources resources;

  public AbstractBasicItemPresenter(IResources resources, IBasicItemData item) {
    this.resources = resources;
    this.item = item;
  }

  public abstract void initPresentation();

  protected final void initDescriptionPresentation(String nameLabel, IBasicItemDescriptionView descriptionView) {
    TextualPresentation.initView(descriptionView.addLineTextView(nameLabel), item.getDescription().getName());
    String summaryLabel = resources.getString(getContentLabelKey());
    DefaultStyledDocument document = new DefaultStyledDocument();
    StyledTextManager.initView(new StyledTextManager(document), item.getDescription().getContent());
    descriptionView.addStyledTextView(summaryLabel, document, new Dimension(200, 200), new TextEditorProperties(
        resources));
    descriptionView.initGui(resources.getString(getBorderTitleKey()));
  }

  protected abstract String getBorderTitleKey();

  protected abstract String getContentLabelKey();

  protected abstract String getNameLabelKey();
}