package pipe.extensions.jpowergraph;

import net.sourceforge.jpowergraph.Node;
import net.sourceforge.jpowergraph.defaults.TextEdge;

public class PIPECATextEdge extends TextEdge {
    private String _actionName;
    public PIPECATextEdge(Node from, Node to, String theText,String actionName) {
        super(from, to,theText);
        _actionName=actionName;
    }

    public String get_actionName() {
        return _actionName;
    }
}
