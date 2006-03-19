package net.sf.anathema.campaign.concrete;

import net.sf.anathema.campaign.concrete.plot.PlotModel;
import net.sf.anathema.campaign.model.ISeries;
import net.sf.anathema.campaign.model.ISeriesContentModel;
import net.sf.anathema.campaign.model.plot.IPlotModel;
import net.sf.anathema.framework.item.IItemType;
import net.sf.anathema.framework.itemdata.IItemData;
import net.sf.anathema.framework.presenter.itemmanagement.PrintNameAdjuster;
import net.sf.anathema.lib.workflow.textualdescription.ISimpleTextualDescription;

public class Series implements ISeries, IItemData {

  private final ISeriesContentModel contentModel;
  private final IPlotModel plotModel = new PlotModel();

  public Series(IItemType[] contentTypes) {
    contentModel = new SeriesContentModel(contentTypes);
  }

  public ISeriesContentModel getContentModel() {
    return contentModel;
  }

  public IPlotModel getPlot() {
    return plotModel;
  }

  public void setPrintNameAdjuster(PrintNameAdjuster adjuster) {
    ISimpleTextualDescription rootName = plotModel.getRootElement().getDescription().getName();
    rootName.addTextChangedListener(adjuster);
  }
}