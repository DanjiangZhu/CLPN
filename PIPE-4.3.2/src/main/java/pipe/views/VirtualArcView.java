package pipe.views;

import java.io.Serializable;
import pipe.gui.Constants;
import pipe.gui.ZoomController;
import pipe.models.VirtualArc;
import pipe.utilities.Copier;
import pipe.views.viewComponents.NameLabel;
import java.util.LinkedList;
import java.awt.*;
import java.awt.geom.AffineTransform;

public class VirtualArcView extends ArcView implements Serializable {
    private final static String type = "virtual";
    private final static int OVAL_X = -4;
    private final static int OVAL_Y = -8;
    private final static int OVAL_WIDTH = 8;
    private final static int OVAL_HEIGHT = 8;

    /**
     * Create Petri-Net Arc object
     *
     * @param startPositionXInput Start X-axis Position
     * @param startPositionYInput Start Y-axis Position
     * @param endPositionXInput End X-axis Position
     * @param endPositionYInput End Y-axis Position
     * @param sourceInput Arc source
     * @param targetInput Arc target
     * @param weightInput
     * @param idInput Arc id
     */
    public VirtualArcView(double startPositionXInput, double startPositionYInput, double endPositionXInput, double endPositionYInput, ConnectableView sourceInput, ConnectableView targetInput, LinkedList<MarkingView> weightInput, String idInput, VirtualArc model) {
        super(startPositionXInput, startPositionYInput, endPositionXInput, endPositionYInput,sourceInput,targetInput,weightInput, idInput, model);
    }


    /**
     * Create Petri-Net Arc object
     * @param newSource
     */
    public VirtualArcView(ConnectableView newSource) {
        super(newSource);
    }



    private VirtualArcView(VirtualArcView arcView) {
        weightLabel = new LinkedList<NameLabel>();
        for(int i = 0; i < 100; i++){
            weightLabel.add(new NameLabel(_zoomPercentage));
        }

        for (int i = 0; i <= arcView.myPath.getEndIndex(); i++){
            this.myPath.addPoint(arcView.myPath.getPoint(i).getX(),
                    arcView.myPath.getPoint(i).getY(),
                    arcView.myPath.getPointType(i));
        }
        this.myPath.createPath();
        this.updateBounds();
        this._id = arcView._id;
        this.setSource(arcView.getSource());
        this.setTarget(arcView.getTarget());
        this.setWeight(Copier.mediumCopy(arcView.getWeight()));
    }


    public VirtualArcView paste(double despX, double despY, boolean toAnotherView, PetriNetView model){
        ConnectableView source = this.getSource().getLastCopy();
        ConnectableView target = this.getTarget().getLastCopy();

        if (source == null && target == null) {
            // don't paste an arc with neither source nor target
            return null;
        }

        if (source == null){
            if (toAnotherView) {
                // if the source belongs to another Petri Net, the arc can't be
                // pasted
                return null;
            } else {
                source = this.getSource();
            }
        }

        if (target == null){
            if (toAnotherView) {
                // if the target belongs to another Petri Net, the arc can't be
                // pasted
                return null;
            } else {
                target = this.getTarget();
            }
        }

        VirtualArcView copy = new VirtualArcView((double) 0, (double) 0,(double) 0, (double) 0, source, target,this.getWeight(), source.getId() + " to " + target.getId(), new VirtualArc());

        copy.myPath.delete();
        for (int i = 0; i <= this.myPath.getEndIndex(); i++){
            copy.myPath.addPoint(this.myPath.getPoint(i).getX() + despX,
                    this.myPath.getPoint(i).getY() + despY,
                    this.myPath.getPointType(i));
            //copy.myPath.selectPoint(i);
        }

        source.addOutbound(copy);
        target.addInbound(copy);
        return copy;
    }


    public VirtualArcView copy(){
        return new VirtualArcView(this);
    }


    public String getType(){
        return type;
    }


    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D)g;

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        g2.translate(getComponentDrawOffset() + zoomGrow - myPath.getBounds().getX(),
                getComponentDrawOffset() + zoomGrow - myPath.getBounds().getY());

        if (_selected && !_ignoreSelection){
            g2.setPaint(Constants.SELECTION_LINE_COLOUR);
        } else{
            g2.setPaint(Constants.ELEMENT_LINE_COLOUR);
        }

        Stroke dash = new BasicStroke(0.01f * _zoomPercentage, BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_ROUND, 3.5f, new float[] { 5, 5, },
                0f);

        g2.setStroke(dash);
        g2.draw(myPath);

        g2.translate(myPath.getPoint(myPath.getEndIndex()).getX(),
                myPath.getPoint(myPath.getEndIndex()).getY());

        g2.rotate(myPath.getEndAngle()+Math.PI);
        g2.setColor(Color.WHITE);

        AffineTransform reset = g2.getTransform();
//        g2.transform(ZoomController.getTransform(_zoomPercentage));
//
//        g2.setStroke(new BasicStroke(8f));
//        g2.fillOval(OVAL_X, OVAL_Y, OVAL_WIDTH, OVAL_HEIGHT);

        g2.transform(ZoomController.getTransform(_zoomPercentage));
        g2.setPaint(Constants.ELEMENT_LINE_COLOUR);

        if (_selected && !_ignoreSelection){
            g2.setPaint(Constants.SELECTION_LINE_COLOUR);
        } else{
            g2.setPaint(Constants.ELEMENT_LINE_COLOUR);
        }

//        g2.drawOval(OVAL_X, OVAL_Y, OVAL_WIDTH, OVAL_HEIGHT);
        g2.setStroke(new BasicStroke(0.8f));
        g2.fillPolygon(new Polygon(new int[] { 0, 5, 0, -5 },new int[] { 0, -10, -7, -10 }, 4));

        g2.setTransform(reset);
    }


    public void update()
    {
        repaint();
    }
}
