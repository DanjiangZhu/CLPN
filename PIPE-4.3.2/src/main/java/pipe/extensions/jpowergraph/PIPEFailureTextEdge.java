package pipe.extensions.jpowergraph;

import net.sourceforge.jpowergraph.defaults.TextEdge;
import net.sourceforge.jpowergraph.Node;

public class PIPEFailureTextEdge extends TextEdge {
    private String _actionName;
    public PIPEFailureTextEdge(Node from, Node to, String theText,String actionName) {
        super(from, to,theText);
        _actionName=actionName;
    }

    public String get_actionName() {
        return _actionName;
    }
}
