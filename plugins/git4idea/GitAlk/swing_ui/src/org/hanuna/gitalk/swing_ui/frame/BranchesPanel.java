package org.hanuna.gitalk.swing_ui.frame;

import com.google.common.collect.Ordering;
import com.intellij.util.ui.UIUtil;
import org.hanuna.gitalk.commit.Hash;
import org.hanuna.gitalk.refs.Ref;
import org.hanuna.gitalk.swing_ui.render.Print_Parameters;
import org.hanuna.gitalk.swing_ui.render.painters.RefPainter;
import org.hanuna.gitalk.ui.UI_Controller;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Panel with branch labels, above the graph.
 *
 * @author Kirill Likhodedov
 */
public class BranchesPanel extends JPanel {

  private final UI_Controller myUiController;
  private final List<Ref> myRefs;
  private final RefPainter myRefPainter;

  private Map<Integer, Ref> myRefPositions = new HashMap<Integer, Ref>();

  public BranchesPanel(UI_Controller ui_controller, List<Ref> refs) {
    myUiController = ui_controller;
    myRefs = refs;
    myRefPainter = new RefPainter();

    setPreferredSize(new Dimension(-1, Print_Parameters.HEIGHT_CELL + UIUtil.DEFAULT_VGAP));

    addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        Ref ref = findRef(e);
        if (ref != null) {
          Hash commitCash = ref.getCommitHash();
          myUiController.updateVisibleBranches();
          myUiController.jumpToCommit(commitCash);
        }
      }
    });

  }

  @Nullable
  private Ref findRef(MouseEvent e) {
    List<Integer> sortedPositions = Ordering.natural().sortedCopy(myRefPositions.keySet());
    int index = Ordering.natural().binarySearch(sortedPositions, e.getX());
    if (index < 0) {
      index = -index - 2;
    }
    if (index < 0) {
      return null;
    }
    return myRefPositions.get(sortedPositions.get(index));
  }

  @Override
  protected void paintComponent(Graphics g) {
    myRefPositions = myRefPainter.draw((Graphics2D)g, myRefs, UIUtil.DEFAULT_HGAP);
  }

}
