/*
 * PIPEHazardousState.java
 */

package pipe.extensions.jpowergraph;

import net.sourceforge.jpowergraph.painters.node.ShapeNodePainter;
import net.sourceforge.jpowergraph.swtswinginteraction.color.JPowerGraphColor;

import java.util.ArrayList;
import java.util.Vector;


//REMARK: this class extends a jpowergraph's class which is LGPL

/**
 * The node that represents the PIPEHazardousState marking in the reachability graph.
 * @author Pere Bonet
 */
public class PIPEHazardousState
        extends PIPEState {

    // white
    private static final JPowerGraphColor bgColor = new JPowerGraphColor(255, 255, 255);

    // black
    private static final JPowerGraphColor fgColor = new JPowerGraphColor(0, 0, 0);

    // a rectangle
    private static final ShapeNodePainter shapeNodePainter = new ShapeNodePainter(
            ShapeNodePainter.RECTANGLE, bgColor, JPowerGraphColor.RED,
            fgColor);

    private ArrayList<String> Related_Hazards;
    /**
     * Creates the Hazardous state node.
     * @param label    the node id.
     * @param marking  the marking
     */
    public PIPEHazardousState(String label, String marking,ArrayList<String> hazards){
        super(label, marking);
        Related_Hazards=hazards;
    }


    public static ShapeNodePainter getShapeNodePainter(){
        return shapeNodePainter;
    }


    public String getNodeType(){
        return "Hazardous State";
    }

    public String getRelatedHazards()
    {
        String temp="";
        for(int i=0;i<Related_Hazards.size();i++)
        {
            temp+=(Related_Hazards.get(i)+",");
        }
        return temp.substring(0,temp.length()-1);
    }

    public ArrayList<String> getRelatedHazardsList()
    {
        return Related_Hazards;
    }
}
