package pipe.calculations;


import net.sourceforge.jpowergraph.defaults.DefaultGraph;
import net.sourceforge.jpowergraph.Node;
import net.sourceforge.jpowergraph.Edge;
import net.sourceforge.jpowergraph.defaults.DefaultNode;
import net.sourceforge.jpowergraph.defaults.TextEdge;
import pipe.extensions.jpowergraph.*;
import pipe.views.LogicalTransitionView;
import pipe.views.MarkingView;
import pipe.views.PetriNetView;
import pipe.views.PlaceView;

import java.util.*;

public class ExtensiveReachabilityGraph {
    private DefaultGraph fullGraph;
    private DefaultGraph Graph;
    private PetriNetView pn;

    public ExtensiveReachabilityGraph(PetriNetView sourcepn, DefaultGraph fullGra, DefaultGraph Gra) {
        pn = sourcepn;
        fullGraph = fullGra;
        Graph = Gra;
    }

    //只保留完全可达图中的kback返回的edge与原可达图的弧
    public DefaultGraph ConstructExtensiveReachabilityGraph() {
        List<Node> resnodes = new ArrayList<Node>();
        List<Edge> resedges = new ArrayList<Edge>();

        List<Node> orinodes = Graph.getAllNodes();
        List<Edge> oriedges = Graph.getAllEdges();

        int k=2;
        //保留从Hazard状态倒推的k步所涉及的node与edge
        List<Edge> tempedges= getkStep(fullGraph,k);
        List<Node> tempnode= new ArrayList<Node>();
        for(int i=0;i<tempedges.size();i++)
        {
            Edge e=tempedges.get(i);
            if(!Iscontain(tempnode,e.getFrom())) tempnode.add(e.getFrom());
            if(!Iscontain(tempnode,e.getTo())) tempnode.add(e.getTo());
        }

        orinodes.addAll(tempnode);
        HashMap<String,Integer> nodes_marking=new HashMap<String, Integer>();
        int resnodesum=0;
        for (int i=0;i<orinodes.size();i++)
        {
            Node n= orinodes.get(i);
            String marking=null;
            if(!(n instanceof PIPEHazardousState)){
                marking=((PIPENode)n).getMarking();
                PIPEVanishingState addn=new PIPEVanishingState("M"+resnodesum,marking);
                if(!(Iscontain(resnodes,addn))) {
                    resnodes.add(addn);
                    nodes_marking.put(marking,resnodes.indexOf(addn));
                    resnodesum++;
                }
            }
            else  {
                marking=((PIPENode) n).getMarking();
                PIPEHazardousState addn= new PIPEHazardousState("M"+resnodesum,marking,((PIPEHazardousState) n).getRelatedHazardsList() );
                if(!(Iscontain(resnodes,addn))) {
                    resnodes.add(addn);
                    nodes_marking.put(marking,resnodes.indexOf(addn));
                    resnodesum++;
                }
            }
        }


        oriedges.addAll(tempedges);
        for(int i=0;i<oriedges.size();i++)
        {
            Edge e=oriedges.get(i);
            String marking_from=((PIPENode)e.getFrom()).getMarking();
            String marking_to=((PIPENode)e.getTo()).getMarking();
            String tip=((TextEdge)e).getText();
            Node from=resnodes.get(nodes_marking.get(marking_from));
            Node to=resnodes.get(nodes_marking.get(marking_to));


            if(IsFailure(marking_from,tip))
            {
               PIPEFailureTextEdge temp= new PIPEFailureTextEdge(from,to,tip,((PIPECATextEdge)e).get_actionName());
                if(!(Iscontain(resedges,temp)))
                    resedges.add(temp);
            }
            else {
                if(IsnextMarking(marking_from,marking_to,tip)) {
                    TextEdge temp;
                    if (pn.getTransitionById(tip) instanceof LogicalTransitionView)
                        temp = new PIPECATextEdge(from, to, tip, ((PIPECATextEdge) e).get_actionName());
                    else
                        temp = new TextEdge(from, to, tip);
                    if (!(Iscontain(resedges, temp)))
                        resedges.add(temp);
                }
            }
        }
        DefaultGraph resgra=new DefaultGraph();
        resgra.addElements(resnodes,resedges);

        //清除落单的节点
         List<Node> denode=new ArrayList<Node>();
         List<Edge> deedge=new ArrayList<Edge>();
        for(int i=0;i<resgra.getAllNodes().size();i++)
        {
            Node n=resgra.getAllNodes().get(i);
            if(n.getEdgesFrom().size()==0&&n.getEdgesTo().size()==0)
                denode.add(n);
        }
        resgra.deleteElements(denode,deedge);
        ArrayList<PlaceView> InitialMarkings=pn.getPlacesArrayList();
        int[] Markingviews=new int[pn.getPlacesArrayList().size()];
        for(int i=0;i<Markingviews.length;i++)
        {
            Markingviews[i]=InitialMarkings.get(i).getInitialMarkingView().getFirst().getCurrentMarking();
        }
        pn.setCurrentMarkingVector(Markingviews);
        return  resgra;

    }

    private List<Edge> getkStep(DefaultGraph gra,int k)
    {
        List<Node> nodes=gra.getAllNodes();
        List<Edge> paths=new ArrayList<Edge>();
        for(int i=0;i<nodes.size();i++)
        {
            List<Edge> path=new ArrayList<Edge>();
            if((nodes.get(i) instanceof PIPEHazardousState)&&(Iscontain(Graph.getAllNodes(),nodes.get(i))))
            {
                kback(nodes.get(i),k,path);
            }
            if(path.size()>0) {
                paths.addAll(path);
                System.out.println(path);
            }
        }
        return paths;
    }

    //递归，从某个节点出发深度遍历，k步以内找到原可达图的点为止
    private boolean kback(Node node,int k,List<Edge> tempedge)
    {
        List<Edge>edges=node.getEdgesTo();
        //表示在该层是否找到目标点
        boolean flag=false;
        for(int i=0;i<edges.size();i++) {
            Edge e=edges.get(i);
            Node frontnode=e.getFrom();
            //若该边不在原可达图之内则开始倒推
            if ((!Iscontain(Graph.getAllEdges(),e))) {
                //若k已经为0则停止寻找
                if(k==0)
                {
                    flag=false;
                }
                else {
                    k--;
                    // 若该边的前驱节点为原可达图的点，则找到一条路径，并添加进temp
                    if (Iscontain(Graph.getAllNodes(), frontnode)&&!(frontnode instanceof PIPEHazardousState)) {
                        tempedge.add(edges.get(i));
                        flag=true;
                    }
                    else {
                        //若递归返回的结果是true，则将该层的边加入temp
                        if (kback(frontnode, k, tempedge)) {
                            tempedge.add(edges.get(i));
                            flag=true;
                        }
                    }
                }
            }
        }
        return flag;
    }

    //判断一个变迁在某个marking条件下是否enable，
    private boolean IsFailure(String marking,String tip)
    {
        int[] markings=marking2int(marking);

        pn.setCurrentMarkingVector(markings);
        boolean[] trs= pn.areTransitionsEnabled(pn.getCurrentMarkingVector());
        int tno=pn.getTransitionsArrayList().indexOf(pn.getTransitionById(tip));
        return !trs[tno];
    }

    //判断在marking1条件下在一个变迁fire后marking是否变为marking2
    private boolean IsnextMarking(String marking1,String marking2,String tip)
    {
        int[] markings1=marking2int(marking1);
        int[] markings2=marking2int(marking2);

        pn.setCurrentMarkingVector(markings1);
        pn.fireTransition(pn.getTransitionById(tip));
        int[] resmarking=pn.getCurrentMarkingVectorNum();

        return Arrays.equals(markings2,resmarking);
    }

    //判断一个节点的List是否包含某个节点
    private boolean Iscontain(List<Node> nodes,Node node)
    {
        ArrayList<String> markings=new ArrayList<String>();
        String marking=((PIPENode)node).getMarking();
        for(int i=0;i<nodes.size();i++)
        {
            markings.add(((PIPENode)nodes.get(i)).getMarking());
        }
        return markings.contains(marking);
    }


    //判断一个弧的List是否包含某个弧
    private boolean Iscontain(List<Edge> edges,Edge edge)
    {
        String[] marking=new String[2];
        boolean flag=false;

        marking[0]=((PIPENode)edge.getFrom()).getMarking();
        marking[1]=((PIPENode)edge.getTo()).getMarking();

        for(int i=0;i<edges.size();i++)
        {
            String[] temp=new String[2];
            temp[0]=((PIPENode)edges.get(i).getFrom()).getMarking();
            temp[1]=((PIPENode)edges.get(i).getTo()).getMarking();
            if(temp[0].equals(marking[0])&&temp[1].equals(marking[1])) {flag=true;break;}
        }

        return flag;
    }

    private int[] marking2int(String marking)
    {
        String[] tempmarking=marking.replaceAll("[{}]","").trim().split("[,]");
        int[] markings=new int[tempmarking.length];
        for(int i=0;i<tempmarking.length;i++)
        {
            markings[i]=Integer.parseInt(tempmarking[i].trim());
        }
        return markings;
    }
}
