package net.sf.anathema.platform.tree.view;

import net.sf.anathema.platform.tree.view.draw.FilledPolygon;
import net.sf.anathema.platform.tree.view.draw.FlexibleArrow;
import net.sf.anathema.platform.tree.view.draw.InteractiveGraphicsElement;
import org.junit.Before;
import org.junit.Test;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import static java.awt.RenderingHints.KEY_ANTIALIASING;
import static java.awt.RenderingHints.VALUE_ANTIALIAS_ON;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

public class PolygonPanelTest {

  private PolygonPanel polygonPanel = new PolygonPanel();
  private Graphics2D graphics = mock(Graphics2D.class);

  @Before
  public void setUp() throws Exception {
    when(graphics.create()).thenReturn(graphics);
  }

  @Test
  public void hasIdentityTransformationInitially() throws Exception {
    polygonPanel.paintComponent(graphics);
    verify(graphics).transform(new AffineTransform());
  }

  @Test
  public void scalesGraphics() throws Exception {
    polygonPanel.scale(5);
    polygonPanel.paintComponent(graphics);
    verify(graphics).transform(AffineTransform.getScaleInstance(0.5, 0.5));
  }

  @Test
  public void hasIdentityTransformationAfterReset() throws Exception {
    polygonPanel.scale(5);
    polygonPanel.resetTransformation();
    polygonPanel.paintComponent(graphics);
    verify(graphics).transform(new AffineTransform());
  }

  @Test
  public void activatesAntiAliasing() throws Exception {
    polygonPanel.paintComponent(graphics);
    verify(graphics).setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);
  }

  @Test
  public void paintsPolygon() throws Exception {
    InteractiveGraphicsElement polygon = mock(FilledPolygon.class);
    polygonPanel.add(polygon);
    polygonPanel.paintComponent(graphics);
    verify(polygon).paint(graphics);
  }

  @Test
  public void paintsArrow() throws Exception {
    FlexibleArrow arrow = mock(FlexibleArrow.class);
    polygonPanel.add(arrow);
    polygonPanel.paintComponent(graphics);
    verify(arrow).paint(graphics);
  }

  @Test
  public void paintsAllPolygon() throws Exception {
    InteractiveGraphicsElement firstPolygon = mock(FilledPolygon.class);
    InteractiveGraphicsElement secondPolygon = mock(FilledPolygon.class);
    polygonPanel.add(firstPolygon);
    polygonPanel.add(secondPolygon);
    polygonPanel.paintComponent(graphics);
    verify(firstPolygon).paint(graphics);
    verify(secondPolygon).paint(graphics);
  }

  @Test
  public void hasNoElementsAfterClear() throws Exception {
    InteractiveGraphicsElement element = mock(FilledPolygon.class);
    polygonPanel.add(element);
    polygonPanel.clear();
    polygonPanel.paintComponent(graphics);
    verifyZeroInteractions(element);
  }
}