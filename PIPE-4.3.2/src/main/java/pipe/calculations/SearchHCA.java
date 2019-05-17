package pipe.calculations;

import net.sourceforge.jpowergraph.Edge;
import net.sourceforge.jpowergraph.Node;
import net.sourceforge.jpowergraph.defaults.DefaultGraph;
import net.sourceforge.jpowergraph.defaults.TextEdge;
import pipe.extensions.jpowergraph.*;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

public class SearchHCA {
    DefaultGraph Graph;
    int index;
    ArrayList<HCA> HCAsses =new ArrayList<HCA>();
    public SearchHCA(DefaultGraph gr)
    {
        Graph=gr;
        index=1;
    }

    public Vector<Vector<String>> constrcutTable()
    {
        Vector<Vector<String>> data=new Vector<Vector<String>>();
        List<Node> hnode=new ArrayList<Node>();
        for(int i=0;i<Graph.getAllNodes().size();i++){
            if(Graph.getAllNodes().get(i) instanceof PIPEHazardousState)
                hnode.add(Graph.getAllNodes().get(i));
        }
        for(int i=0;i<hnode.size();i++)
        {
            mergevector(data,Step1((PIPEHazardousState)hnode.get(i)));
            mergevector(data,Step2((PIPEHazardousState)hnode.get(i)));
            mergevector(data,Step3((PIPEHazardousState)hnode.get(i)));
            mergevector(data,Step4((PIPEHazardousState)hnode.get(i)));
            mergevector(data,Step5((PIPEHazardousState)hnode.get(i)));

        }

        return  data;
    }

    private Vector<Vector<String>> Step1(PIPEHazardousState M3)
    {
        Vector<Vector<String>> step1=new Vector<Vector<String>>();
        List<Edge> to_edges=M3.getEdgesTo();
        //遍历每个以该节点为目的边
        for(int i=0;i<to_edges.size();i++)
        {
            //若该边代表的弧为CA或(ca-f)这里因为caf导致的hzard太多暂且不计 to_edges.get(i) instanceof PIPECATextEdge
            if(to_edges.get(i) instanceof PIPECATextEdge||to_edges.get(i) instanceof PIPEFailureTextEdge) {
                //若有一个CA以该Hazard为目的找到一个P
                //若该边的起始节点（即该Hazard节点的前前一个节点不为Hazard）
                Edge M1_M3=to_edges.get(i);
                Node M1 = M1_M3.getFrom();
                if(!(M1 instanceof  PIPEHazardousState)) {
                    String HCANo="HCA" + String.valueOf(index);
                    //添加一个HCA记录
                    Vector<String> temp_P = new Vector<String>();
                    temp_P.add(HCANo);
                    temp_P.add("("+getactionName(M1_M3)+",P," +((TextEdge)M1_M3).getText() + "," + M1.getLabel() + ")");
                    temp_P.add(M3.getLabel());
                    temp_P.add(M3.getRelatedHazards());
                    step1.add(temp_P);

                    ArrayList<String> cripath=new ArrayList<String>();
                    cripath.add(((TextEdge)M1_M3).getText());
                    HCA temphca=new HCA(HCANo,temp_P,M3.getLabel(),cripath);
                    HCAsses.add(temphca);

                    index++;

                    List<Edge> M1_fromEdge = M1.getEdgesFrom();
                    //遍历该节点出发的边
                    for (int j = 0; j < M1_fromEdge.size(); j++) {
                        //若该边代表的弧为CA
                        if (M1_fromEdge.get(j).getFrom() instanceof PIPECATextEdge){
                            Edge M1_M2=M1_fromEdge.get(j);
                            Node M2 = M1_M2.getTo();
                            //若该节点不为Hazard,则找到一个NP
                            if (!(M2 instanceof PIPEHazardousState)) {
                                String HCANo2="HCA" + String.valueOf(index);
                                //添加一个HCA记录
                                Vector<String> temp_NP = new Vector<String>();
                                temp_NP.add(HCANo2);
                                temp_NP.add("("+getactionName(M1_M2)+",NP," + getactionName(M1_M2)+((TextEdge)M1_M2).getText() + "," + M1.getLabel() + ")");
                                temp_NP.add(M1.getLabel());
                                temp_NP.add(M3.getRelatedHazards());
                                step1.add(temp_NP);

                                ArrayList<String> cripath2=new ArrayList<String>();
                                cripath2.add(((TextEdge)M1_M3).getText());
                                HCA temphca2=new HCA(HCANo2,temp_NP,M3.getLabel(),cripath2);
                                HCAsses.add(temphca2);

                                index++;
                            }
                        }
                    }
                }
            }
        }
        return step1;
    }

    private Vector<Vector<String>> Step2(PIPEHazardousState M3)
    {
        Vector<Vector<String>> step2=new Vector<Vector<String>>();
        List<Edge> to_edges=M3.getEdgesTo();
        //遍历每个以该节点为目的边
        for(int i=0;i<to_edges.size();i++) {
            TextEdge M1_M3=(TextEdge) to_edges.get(i);
            //若该边不为ca或ca-f
           if(!((M1_M3 instanceof PIPECATextEdge)||(M1_M3 instanceof PIPEFailureTextEdge)))
           {
               Node M1 = M1_M3.getFrom();
               //若该点不为Hazard
               if(!(M1 instanceof  PIPEHazardousState)) {
                   List<Edge> from_M1_edges=M1.getEdgesFrom();
                   for(int j=0;j<from_M1_edges.size();j++)
                   {
                       TextEdge M1_M2=(TextEdge)from_M1_edges.get(j);
                       //若该边为ca或ca-f
                       if((M1_M2 instanceof PIPECATextEdge)||(M1_M2 instanceof PIPEFailureTextEdge)) {
                           Node M2 = M1_M2.getTo();
                           //M2不为Hazard
                           if(!(M2 instanceof PIPEHazardousState)) {
                               for (int k = 0; k < M2.getEdgesFrom().size(); k++) {
                                   TextEdge M2_M4=(TextEdge)M2.getEdgesFrom().get(k);
                                   //若M2_M4为M1_M3的相同的变迁且M4不为Hazard，找到一个PTL
                                   if (!(M2_M4.getTo() instanceof PIPEHazardousState) && M2_M4.getText().equals(M1_M3.getText())) {
                                       Vector<String> temp_PTL = new Vector<String>();
                                       temp_PTL.add("HCA" + String.valueOf(index));
                                       temp_PTL.add("("+getactionName(M1_M2)+",PTL," + M1_M2.getText() + "," + M1.getLabel() + ")");
                                       temp_PTL.add(M3.getLabel());
                                       temp_PTL.add(M3.getRelatedHazards());
                                       step2.add(temp_PTL);

                                       //添加一个HCA记录
                                       ArrayList<String> cripath=new ArrayList<String>();
                                       cripath.add((M1_M3).getText());
                                       HCA temphca=new HCA("HCA" + String.valueOf(index),temp_PTL,M3.getLabel(),cripath);
                                       HCAsses.add(temphca);

                                       index++;
                                       break;
                                   }
                               }
                           }
                       }
                   }
                   //搜寻PTE
                   List<Edge> to_M1_edges=M1.getEdgesTo();
                   for(int j=0;j<to_M1_edges.size();j++)
                   {
                       TextEdge M2_M1= (TextEdge) to_M1_edges.get(j);
                       //若该边为ca或ca-f
                       if((M2_M1 instanceof PIPEFailureTextEdge)||(M2_M1 instanceof PIPECATextEdge))
                       {
                           Node M2=to_M1_edges.get(j).getFrom();
                           //若该点不为Hazard
                           if(!(M2 instanceof PIPEHazardousState))
                           {
                               List<Edge> from_M2_edges=M2.getEdgesFrom();
                               for(int k=0;k<M2.getEdgesFrom().size();k++) {
                                   TextEdge M2_M4=(TextEdge)M2.getEdgesFrom().get(k);
                                   //若M2_M4与M1_M3为相同变迁，且M4不为Hazard，找到一个PTE
                                   if(!(M2_M4.getTo() instanceof PIPEHazardousState) && M2_M4.getText().equals(M1_M3.getText()))
                                   {
                                       if(!(from_M2_edges.get(k).getTo() instanceof  PIPEHazardousState))
                                       {
                                           Vector<String> temp_PTE=new Vector<String>();
                                           temp_PTE.add("HCA"+String.valueOf(index));
                                           temp_PTE.add("("+getactionName(M2_M1)+",PTE,"+M2_M1.getText()+","+M2.getLabel()+")");
                                           temp_PTE.add(M3.getLabel());
                                           temp_PTE.add(M3.getRelatedHazards());
                                           step2.add(temp_PTE);

                                           //添加一个HCA记录
                                           ArrayList<String> cripath=new ArrayList<String>();
                                           cripath.add((M1_M3).getText());
                                           cripath.add((M2_M1).getText());
                                           HCA temphca=new HCA("HCA" + String.valueOf(index),temp_PTE,M3.getLabel(),cripath);
                                           HCAsses.add(temphca);

                                           index++;
                                           break;
                                       }
                                   }
                               }
                           }
                       }
                   }

               }

           }
        }

        return  step2;

    }

    private Vector<Vector<String>> Step3(PIPEHazardousState M1)
    {
        Vector<Vector<String>> step3=new Vector<Vector<String>>();
        List<Edge> from_edges=M1.getEdgesFrom();
        for(int i=0;i<from_edges.size();i++)
        {
            if(from_edges.get(i) instanceof PIPECATextEdge)
            {
                if(!(from_edges.get(i).getTo() instanceof PIPEHazardousState))
                {
                    Vector<String> temp_NP=new Vector<String>();
                    temp_NP.add("HCA"+String.valueOf(index));
                    temp_NP.add("("+getactionName(from_edges.get(i))+",NP,"+((TextEdge)from_edges.get(i)).getText()+","+M1.getLabel()+")");
                    temp_NP.add(M1.getLabel());
                    temp_NP.add(M1.getRelatedHazards());
                    step3.add(temp_NP);

                    //添加一个HCA记录
                    ArrayList<String> cripath=new ArrayList<String>();
                    HCA temphca=new HCA("HCA" + String.valueOf(index),temp_NP,M1.getLabel(),cripath);
                    HCAsses.add(temphca);

                    index++;
                }
            }
        }
        return  step3;
    }

    private Vector<Vector<String>> Step4(PIPEHazardousState M3)
    {
        Vector<Vector<String>> step4=new Vector<Vector<String>>();
        List<Edge> to_M3=M3.getEdgesTo();
        for(int i=0;i<to_M3.size();i++)
        {
            TextEdge M4_M3=(TextEdge)to_M3.get(i);
            Node M4=M4_M3.getFrom();
            //若M4_M3为ca或ca-f，且M4不为Hazard
            if((M4_M3 instanceof PIPECATextEdge ||M4_M3 instanceof PIPEFailureTextEdge)&&(!(M4 instanceof PIPEHazardousState)))
            {
                List<Edge> to_M4=M4.getEdgesTo();
                for(int j=0;j<to_M4.size();j++) {
                    TextEdge M1_M4 = (TextEdge) to_M4.get(j);
                    //M1_M4与M4_M2不能为同一个变迁
                    if (!(M1_M4.getText().equals(M4_M3.getText()))) {
                        Node M1 = M1_M4.getFrom();
                        if (IsLink(M1, M4_M3.getText()) != -1) {
                            Edge M1_M5 = M1.getEdgesFrom().get(IsLink(M1, M4_M3.getText()));
                            Node M5 = M1_M5.getTo();
                            if (IsLink(M5, M1_M4.getText()) != -1) {
                                Edge M5_M2 = M5.getEdgesFrom().get(IsLink(M5, M1_M4.getText()));
                                Node M2 = M5_M2.getTo();
                                //找到一个PWS
                                if (!(M2 instanceof PIPEHazardousState)) {
                                    Vector<String> temp_PWS = new Vector<String>();
                                    temp_PWS.add("HCA" + String.valueOf(index));
                                    temp_PWS.add("(,PWS," + "<" + ((TextEdge) M1_M5).getText() + "," + ((TextEdge) M5_M2).getText() + ">" + "," + M1.getLabel() + ")");
                                    temp_PWS.add(M3.getLabel());
                                    temp_PWS.add(M3.getRelatedHazards());
                                    step4.add(temp_PWS);

                                    //添加一个HCA记录
                                    ArrayList<String> cripath=new ArrayList<String>();
                                    cripath.add(M4_M3.getText());
                                    cripath.add(M1_M4.getText());
                                    HCA temphca=new HCA("HCA" + String.valueOf(index),temp_PWS,M3.getLabel(),cripath);
                                    HCAsses.add(temphca);

                                    index++;
                                }
                            }
                        }

                    }
                }

            }
        }
        return  step4;
    }

    private Vector<Vector<String>> Step5(PIPEHazardousState M3) {
        Vector<Vector<String>> step5 = new Vector<Vector<String>>();
        List<Edge> to_edges = M3.getEdgesTo();
        //遍历每个以该节点为目的边
        for (int i = 0; i < to_edges.size(); i++) {
            TextEdge M1_M3 = (TextEdge) to_edges.get(i);
            //若该边为ca或ca-f
            if ((M1_M3 instanceof PIPECATextEdge) || (M1_M3 instanceof PIPEFailureTextEdge)) {
                Node M1 = M1_M3.getFrom();
                //若该点不为Hazard
                if (!(M1 instanceof PIPEHazardousState)) {
                    List<Edge> from_M1_edges = M1.getEdgesFrom();
                    for (int j = 0; j < from_M1_edges.size(); j++) {
                        TextEdge M1_M2 = (TextEdge) from_M1_edges.get(j);
                        //若该边不为ca或ca-f
                        if (!(M1_M2 instanceof PIPECATextEdge) || (M1_M2 instanceof PIPEFailureTextEdge)) {
                            Node M2 = M1_M2.getTo();
                            //M2不为Hazard
                            if (!(M2 instanceof PIPEHazardousState)) {
                                for (int k = 0; k < M2.getEdgesFrom().size(); k++) {
                                    TextEdge M2_M4 = (TextEdge) M2.getEdgesFrom().get(k);
                                    //若M2_M4为M1_M3的相同的变迁且M4不为Hazard，找到一个PTE
                                    if (!(M2_M4.getTo() instanceof PIPEHazardousState) && M2_M4.getText().equals(M1_M3.getText())) {
                                        Vector<String> temp_PTE = new Vector<String>();
                                        temp_PTE.add("HCA" + String.valueOf(index));
                                        temp_PTE.add("("+getactionName(M1_M3)+",PTE," + M1_M3.getText() + "," + M2.getLabel() + ")");
                                        temp_PTE.add(M3.getLabel());
                                        temp_PTE.add(M3.getRelatedHazards());
                                        step5.add(temp_PTE);

                                        //添加一个HCA记录
                                        ArrayList<String> cripath=new ArrayList<String>();
                                        cripath.add(M1_M3.getText());
                                        HCA temphca=new HCA("HCA" + String.valueOf(index),temp_PTE,M3.getLabel(),cripath);
                                        HCAsses.add(temphca);

                                        index++;
                                        break;
                                    }
                                }
                            }
                        }
                    }
                    //搜寻PTL
                    List<Edge> to_M1_edges = M1.getEdgesTo();
                    for (int j = 0; j < to_M1_edges.size(); j++) {
                        TextEdge M2_M1 = (TextEdge) to_M1_edges.get(j);
                        //若该边不为ca或ca-f
                        if (!(M2_M1 instanceof PIPEFailureTextEdge) || (M2_M1 instanceof PIPECATextEdge)) {
                            Node M2 = M2_M1.getFrom();
                            //若该点不为Hazard
                            if (!(M2 instanceof PIPEHazardousState)) {
                                List<Edge> from_M2_edges = M2.getEdgesFrom();
                                for (int k = 0; k < M2.getEdgesFrom().size(); k++) {
                                    TextEdge M2_M4 = (TextEdge) M2.getEdgesFrom().get(k);
                                    //若M2_M4与M1_M3为相同变迁，且M4不为Hazard，找到一个PTE
                                    if (!(M2_M4.getTo() instanceof PIPEHazardousState) && M2_M4.getText().equals(M1_M3.getText())) {
                                        if (!(from_M2_edges.get(k).getTo() instanceof PIPEHazardousState)) {
                                            Vector<String> temp_PTL = new Vector<String>();
                                            temp_PTL.add("HCA" + String.valueOf(index));
                                            temp_PTL.add("("+getactionName(M2_M4)+",PTL," + M2_M4.getText() + "," + M2.getLabel() + ")");
                                            temp_PTL.add(M3.getLabel());
                                            temp_PTL.add(M3.getRelatedHazards());
                                            step5.add(temp_PTL);

                                            //添加一个HCA记录
                                            ArrayList<String> cripath=new ArrayList<String>();
                                            cripath.add(M1_M3.getText());
                                            cripath.add(M2_M1.getText());
                                            HCA temphca=new HCA("HCA" + String.valueOf(index),temp_PTL,M3.getLabel(),cripath);
                                            HCAsses.add(temphca);

                                            index++;
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }

                }

            }
        }
        return step5;
    }

    //合并Vector
    private  void mergevector(Vector<Vector<String>> v1,Vector<Vector<String>> v2)
   {
       Enumeration e=v2.elements();
       while(e.hasMoreElements())
       {
           v1.add((Vector<String>) e.nextElement());
       }
   }

   //判断某个点是否与Edge相连，返回该Edge的位置,若该点为Hazard则直接返回-1
   private Integer IsLink(Node n,String ca)
   {
       List<Edge> from_n=n.getEdgesFrom();
       int res=-1;
       if(!(n instanceof  PIPEHazardousState)) {
           for (int i = 0; i < from_n.size(); i++) {
               String edgename = ((TextEdge) from_n.get(i)).getText();
               if (edgename.equals(ca)) {
                   res = i;
                   break;
               }
           }
       }
       return  res;
   }

   //获得某个边代表的变迁actionName
    private String getactionName(Edge e)
    {
        String temp="";
        String res="";
        if(e instanceof PIPECATextEdge) temp=((PIPECATextEdge)e).get_actionName();
        if(e instanceof PIPEFailureTextEdge) temp=((PIPEFailureTextEdge)e).get_actionName();
        if(temp.length()>0)
        res=temp.split("\\.")[0];
        return res;
    }

    public ArrayList<HCA> getHCAsses() {
        return HCAsses;
    }
}
