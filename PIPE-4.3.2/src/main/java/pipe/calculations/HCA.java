package pipe.calculations;

import net.sourceforge.jpowergraph.Edge;
import net.sourceforge.jpowergraph.Node;
import net.sourceforge.jpowergraph.defaults.TextEdge;

import java.util.ArrayList;
import java.util.Vector;

public class HCA {
    private String _HCANo;

    private Vector<String> _HCAString;

    private String _HazardNode;
    //用于匹配一条路径的HCA的记录
    private ArrayList<String> _crpath=new ArrayList<String>();

    HCA(String HCANo, Vector<String> ve, String HazardNode, ArrayList<String> crpath)
    {
        _HCANo=HCANo;
        _HCAString=ve;
        _HazardNode=HazardNode;
        _crpath=crpath;
    }

    public boolean IsMatch(ArrayList<String> path,String HazardNode)
    {
        boolean flag=false;
        if(HazardNode.equals(_HazardNode)){
            if(_crpath.size()==0) flag=true;
            else
            {
                for(int i=0;i<_crpath.size();i++)
                {
                    if(!_crpath.get(i).equals(path.get(path.size()-i-1))) return false;
                }
                flag=true;
            }
        }
        return flag;
    }

    public boolean IsMatch2(ArrayList<Edge> path, String HazardNode)
    {
        boolean flag=false;
        if(HazardNode.equals(_HazardNode)){
            if(_crpath.size()==0) flag=true;
            else
            {
                for(int i=0;i<_crpath.size();i++) {
                    if (_crpath.get(i).length()==0) {
                        System.out.println();
                    } else {
                        try {
                            if (!_crpath.get(i).equals(((TextEdge) path.get(path.size() - i - 1)).getText()))
                                return false;
                        }
                        catch (NullPointerException e)
                        {
                            System.out.println();
                        }
                    }
                }
                flag=true;
            }
        }
        return flag;
    }

    public Vector<String> get_HCAString() {
        return _HCAString;
    }

    public String get_HazardNode() {
        return _HazardNode;
    }

    public String get_HCANo() {
        return _HCANo;
    }

    public static ArrayList<Edge> getpath(ArrayList<Node> pathNode)
    {
        ArrayList<Edge> path=new ArrayList<Edge>();
        for(int i=0;i<pathNode.size()-1;i++)
        {
            path.add(getedge(pathNode.get(i),pathNode.get(i+1)));
        }
        return path;

    }

    private static Edge getedge(Node n1,Node n2)
    {
        for(Edge e:n1.getEdgesFrom()) {
            if (e.getTo().equals(n2))
                return e;
        }
        return null;

    }
}
