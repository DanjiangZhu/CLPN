package pipe.views;

import parser.ExprEvaluator;
import pipe.controllers.TransitionController;
import pipe.gui.*;
import pipe.gui.widgets.EscapableDialog;
import pipe.gui.widgets.TransitionEditorPanel;
import pipe.handlers.PetriNetObjectHandler;
import pipe.handlers.PlaceTransitionObjectHandler;
import pipe.historyActions.*;
import pipe.models.Marking;
import pipe.models.NormalArc;
import pipe.views.viewComponents.RateParameter;
import pipe.models.Transition;

import javax.swing.*;
import javax.swing.Timer;

import net.sourceforge.jeval.EvaluationException;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.*;

public class LogicalTransitionView extends TransitionView {
    private String _type="LogicalTransition";

    private GeneralPath __path;
    private Shape _proximityTransition;

    private String _formula;

    public String getFormula(){
        return _formula;
    }

    //没有return 一个HistoryItem。原因：变迁的控制逻辑不会改变，没必要记录变化历史。
    public void setFormula(String _formula) {
        this._formula = _formula;
    }

    public LogicalTransitionView(double positionXInput, double positionYInput, String formula){
        this(positionXInput, positionYInput, "", "", Constants.DEFAULT_OFFSET_X, Constants.DEFAULT_OFFSET_Y, false, false, 0, new Transition("", "", "1", 1), formula);
    }

    public LogicalTransitionView(double positionXInput, double positionYInput, String id, String name, double nameOffsetX, double nameOffsetY, boolean timed, boolean infServer, int angleInput, Transition model, String formula){
        super(positionXInput, positionYInput, id, name, nameOffsetX, nameOffsetY, timed,infServer,angleInput, model);
        _formula = formula;
        constructTransition();
    }

    //逻辑变迁的右键编辑菜单
    public void showEditor()
    {
        EscapableDialog guiDialog = new EscapableDialog(ApplicationSettings.getApplicationView(), "PIPE2", true);
        TransitionEditorPanel te = new TransitionEditorPanel(guiDialog.getRootPane(), this, ApplicationSettings.getApplicationView().getCurrentPetriNetView(), ApplicationSettings.getApplicationView().getCurrentTab());
        guiDialog.add(te);
        guiDialog.getRootPane().setDefaultButton(null);
        guiDialog.setResizable(false);
        guiDialog.pack();
        guiDialog.setLocationRelativeTo(null);
        guiDialog.setVisible(true);
        guiDialog.dispose();
    }


    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

//        __path = new GeneralPath();
//        __path.append(new Rectangle2D.Double((_componentWidth - TRANSITION_WIDTH) / 2, 0, TRANSITION_WIDTH/2 , TRANSITION_HEIGHT), false);


        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if(_selected && !_ignoreSelection)
        {
            g2.setColor(Constants.SELECTION_FILL_COLOUR);
        }
        else
        {
            g2.setColor(Constants.ELEMENT_FILL_COLOUR);
        }
        g2.setPaint(Constants.ELEMENT_LINE_COLOUR);
        g2.draw(__path);
        g2.fill(__path);

        g2.setFont(new Font("Arial",Font.BOLD,8));
        g2.drawString("Ca", 22, 8);
        setToolTipText("EFT = " + this.getRate() + "; formula = " + this.getFormula());
    }

    private void constructTransition()
    {
        __path = new GeneralPath();
//        if(type==0)
            __path.append(new Rectangle2D.Double((_componentWidth - TRANSITION_WIDTH) / 2, 0, TRANSITION_WIDTH/2 , TRANSITION_HEIGHT), false);
//        else
//            _path.append(new Rectangle2D.Double((_componentWidth - TRANSITION_WIDTH) / 2, 0, TRANSITION_WIDTH, TRANSITION_HEIGHT), false);
        outlineTransition();
    }

    private void outlineTransition()
    {
        _proximityTransition = (new BasicStroke(Constants.PLACE_TRANSITION_PROXIMITY_RADIUS)).createStrokedShape(__path);
    }
}
