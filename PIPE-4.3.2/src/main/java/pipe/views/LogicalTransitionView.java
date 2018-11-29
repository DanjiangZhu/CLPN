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
import pipe.utilities.math.Matrix;
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

    private Matrix VCA;
    private  int VCA_colum;
    private int VCA_fire_colum;

    public String getFormula(){
        return _formula;
    }

    public String getType(){
        return _type;
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

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if(_selected && !_ignoreSelection)
        {
            g2.setColor(Constants.SELECTION_FILL_COLOUR);
        }
        else
        {
            g2.setColor(Constants.ELEMENT_FILL_COLOUR);
        }
        g2.setPaint(Constants.ELEMENT_FILL_COLOUR);


        g2.setFont(new Font("Arial",Font.BOLD,8));
        g2.drawString("Ca", 22, 8);
        setToolTipText("EFT = " + this.getRate() + "; formula = " + this.getFormula());
    }

    //析取范式转VCA
    public void createVirtualMatrix(ArrayList<PlaceView> _placeViews)
    {
        String[] or=_formula.split("\\|\\|");
        if(or.length>0)
        {
            VCA=new Matrix(_placeViews.size(),or.length);
            VCA_colum=or.length;
            for (int i = 0; i < or.length; i++) {
                //赋初值，为了判断方便，*设为-1，运算的时候再改过来。
                for(int k=0;k<_placeViews.size();k++) VCA.set(k,i,-1);
                String[] and = or[i].split("\\&\\&");
                if(and.length>0) {
                    for (int j=0;j<and.length;j++)
                    {
                        String temp=and[j].trim().replace("(","");
                        temp=temp.replace(")","");
                        String PID=temp.split("==")[0].trim();
                        String Num=temp.split("==")[1].trim();
                        PlaceView targetP=null;
                        for(PlaceView pv:_placeViews)
                        {
                            if(pv.getId().equals(PID)) targetP=pv;
                        }
                        VCA.set(_placeViews.indexOf(targetP),i,Integer.parseInt(Num));
                    }
                }

            }
        }
    }

    public  int getVCA_colum()
    {
        return  VCA_colum;
    }

    public  Matrix getVCA()
    {
        return  VCA;
    }


    public  int getVCA_fire_colum()
    {

        return  VCA_fire_colum;
    }

    public  void setVCA_fire_colum(int i)
    {

        VCA_fire_colum=i;
    }
}
