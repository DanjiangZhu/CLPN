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
    }



}
