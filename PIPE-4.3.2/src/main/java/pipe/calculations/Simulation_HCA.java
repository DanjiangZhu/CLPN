package pipe.calculations;

import net.sourceforge.jpowergraph.Edge;
import net.sourceforge.jpowergraph.Node;
import net.sourceforge.jpowergraph.defaults.DefaultGraph;
import net.sourceforge.jpowergraph.defaults.TextEdge;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import pipe.extensions.jpowergraph.PIPEFailureTextEdge;
import pipe.extensions.jpowergraph.PIPEHazardousState;
import pipe.views.PetriNetView;

import javax.swing.*;
import java.awt.*;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

public class Simulation_HCA {
    DefaultGraph _graph;
    Node StartNode;
    PetriNetView _pn;

    ChartPanel frame1;
    XYSeriesCollection seriescollection = new XYSeriesCollection();
    int datanum=0;

    //记录结果路径与对应的时间
    ConcurrentHashMap<Integer, ArrayList<String>> res=new ConcurrentHashMap<Integer, ArrayList<String>>();
    ConcurrentHashMap<Integer,Double> resTime=new ConcurrentHashMap<Integer, Double>();
    Integer sum=0;
    int miss=0;

    //记录每个HCA发生的次数与平均时间
    ConcurrentHashMap<String,Integer> countHca=new ConcurrentHashMap<String, Integer>();
    ConcurrentHashMap<String,Double> countHcaTime=new ConcurrentHashMap<String, Double>();

    //统计用
    ArrayList<Double> statepro=new ArrayList<Double>();
    ArrayList<Integer> statenum=new ArrayList<Integer>();

    ArrayList<ArrayList<Edge>> respath=new ArrayList<ArrayList<Edge>>();

    //线程计数工具
    private final CountDownLatch mStartSignal = new CountDownLatch(10);
    //Double  ppp=1.0;
    //double proppp=1.0;
    //递归深度计算
    //int ooo=0;
    ArrayList<HCA> _model;

    public Simulation_HCA(DefaultGraph graph,  PetriNetView pn,ArrayList<HCA> model,ArrayList<ArrayList<Edge>> rp)
    {
        _graph=graph;
        StartNode=GetStartNode();
        _pn=pn;
        _model=model;
        respath=rp;

    }

    public Simulation_HCA(DefaultGraph graph,  PetriNetView pn,ArrayList<HCA> model)
    {
        _graph=graph;
        StartNode=GetStartNode();
        _pn=pn;
        _model=model;
    }

    public void Simulation()
    {
        sum=0;
        res.clear();
        resTime.clear();
        for(int i=0;i<10;i++) {
             new Thread() {
                public void run() {
                    int j=0;
                    //出现概率约为4/10000，这里跑10*2000000次
                    while (j < 2000000) {
                        star();
                        j++;
                    }
                    datanum++;
                    mStartSignal.countDown();
                }
            }.start();
        }

        try {
            mStartSignal.await();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        //统计模拟结果
        count(20000000);
        paintChart();

        JFrame frame=new JFrame("Java数据统计图");
        frame.setLayout(new GridLayout(2,2,10,10));
        frame.add(frame1);    //添加折线图
        frame.setBounds(50, 50, 800, 600);
        frame.setVisible(true);
    }



    private void star()
    {
        Double t=0.0;
        //记录一次模拟的HCA结果
        ArrayList<String> resHCA=new ArrayList<String>();
        //记录一次模拟的路径
        ArrayList<Edge> path=new ArrayList<Edge>();
        Node now=StartNode;
        t=0.0;
        resHCA.clear();
        path.clear();
        //ppp=1.0;
        //ooo=0;

        GetNextNode(now,resHCA,path,t);
        System.out.println();
        sum++;

//            boolean flag=false;
//            for(ArrayList<Edge> ae:respath)
//            {
//                if(compare(ae,path)) {
//                    System.out.println();
//                    flag=true;
//                    break;
//                }
//            }
//            if(!flag) {
//                System.out.println();
//                miss++;
//            }

//            try {
//                //statepro.add(ppp);
//                //statenum.add(path.size());
//            }
//            catch (Exception e)
//            {
//                System.out.println();
//            }
        }


    private void GetNextNode(Node n, ArrayList<String> resHCA, ArrayList<Edge> path,double t)
    {
        //ooo++;
        //若达到一个Hazard节点或者2h则停止(3h为汽车一次行驶的一般时间，因此不统计大于一次的情况)，这里一次的路径发生概率都在-10此方一下
        if((!(n instanceof PIPEHazardousState))&&t<10800) {
            List<Edge> edges = n.getEdgesFrom();
            if(edges.size()>0) {
                //按权重随机选择一个边
                Edge nextedges = GetRandomEdge(edges,path);
                //加上这个边的时间
                t += GetEdgeTime(nextedges);
                GetNextNode(nextedges.getTo(),resHCA,path,t);
            }
            else {
                resHCA.add(n.getLabel()+"end")
                ;return;
            }
        }
        else {
            ArrayList<String> node_Hazard=IsHCA(n,path,t);
            //ppp/=proppp;
            if(node_Hazard.size() > 0) {
                resHCA = node_Hazard;
                ArrayList<String> temp=(ArrayList<String>) resHCA.clone();
                res.put(sum,temp);
                resTime.put(sum,t);
            }
            return;
        }
    }

    //从一堆边中按权重随机选择一个边
    private Edge GetRandomEdge(List<Edge> edges,ArrayList<Edge> path)
    {
        int index=0;
        Double weightsum=0.0;
        ArrayList<Double> Weight=new ArrayList<Double>();
        ArrayList<Double> WeightTemp=new ArrayList<Double>();
        WeightTemp.add(0.0);
            for (Edge e : edges) {
                Double wei = 1.0 * (_pn.getTransitionById(((TextEdge) e).getText()).get_weight());
                if (e instanceof PIPEFailureTextEdge) wei = wei / 10000;
                weightsum += wei;
                Weight.add(wei);
                WeightTemp.add(weightsum);
            }
            Random r = new Random();
            Double rand = r.nextDouble() * weightsum;

            for (int i = WeightTemp.size() - 2; i >= 0; i--) {
                if (rand >= WeightTemp.get(i)) {
                    index = i;
                    break;
                }
            }
        path.add(edges.get(index));
        //ppp=ppp*(Weight.get(index)/weightsum);
        //proppp=(Weight.get(index)/weightsum);

        return edges.get(index);
    }

    //根据变迁的GraphTime生成一个时间
    private Double GetEdgeTime(Edge e)
    {
        Double time=0.0;
        String TimeFormula=_pn.getTransitionById(((TextEdge)e).getText()).getGraphTime();
        String Formula="0";
        String[] res=TimeFormula.split("@");
        if(res.length==2)
        {
            //瞬时变迁
            if(res[0].equals("0"))
            {
                time=0.0;
            }
            //固定时间的变迁
            else if(res[0].equals("1"))
            {
                time=Double.parseDouble(res[1]);
            }
            //按指数分布的时间变迁,这里直接填的平均时间
            else if(res[0].equals("2"))
            {
                time=-(Double.parseDouble(res[1]))*Math.log(Math.random());
            }
            else
                time=0.0;
        }
        return time;
    }

    //设置M0为初始节点
    private Node GetStartNode()
    {
        List<Node> nodes=_graph.getAllNodes();
        for (Node n:nodes) {
            if(n.getLabel().equals("M0")) return n;
        }
        return null;
    }

    //判断结点是否进入Hazard
    private ArrayList<String> IsHCA(Node node,ArrayList<Edge> path,double t)
    {
        ArrayList<String> res=new ArrayList<String>();
        for(HCA hs:_model)
        {
            if(hs.get_HazardNode().equals(node.getLabel()))
            {
               if(hs.IsMatch2(path,node.getLabel())) {
                   res.add(hs.get_HCANo());
                   if(countHca.containsKey(hs.get_HCANo())) {
                       countHca.put(hs.get_HCANo(), countHca.get(hs.get_HCANo()) + 1);
                       countHcaTime.put(hs.get_HCANo(), countHcaTime.get(hs.get_HCANo()) + t);
                   }
                   else {
                       countHca.put(hs.get_HCANo(), 1);
                       countHcaTime.put(hs.get_HCANo(), t);
                   }
                   }
               }
            }
        return res;
    }

    //统计结果
    private void count(int sumNum)
    {
        int[] sum=new int[37];

        for (Map.Entry<Integer,Double> entry:resTime.entrySet()) {
            Long temp = Math.round(entry.getValue() / 300.0D);
            if (temp < 37)
                sum[temp.intValue()] += 1;
        }
        createDataset(sum, sumNum);

        try{
            FileOutputStream fos = new FileOutputStream("E:\\Sim.txt");
            for(int i=0;i<sum.length;i++)
            fos.write((Double.toString(sum[i]) + "\r\n").getBytes());
            fos.close();
            } catch (Exception e) {
                System.out.println();
            }

        for(HashMap.Entry<String,Integer> entry:countHca.entrySet())
        {
            String tempKey=entry.getKey();
            countHcaTime.put(tempKey,countHcaTime.get(tempKey)/countHca.get(tempKey));
        }
        System.out.println(sum);
    }

    //画统计图
    private  void createDataset(int[] num,int sumNum) {
        XYSeries series = new XYSeries("危险状态分布");
        for(int i=0;i<num.length;i++) {
            series.add(i*300, num[i]*1.0D/(sumNum*1.0D));
        }
        seriescollection.addSeries(series);
    }

    private void paintChart()
    {
        JFreeChart jfreechart = ChartFactory.createXYLineChart("危险状态分布", "时间", "频率",seriescollection, PlotOrientation.VERTICAL, true, true, false);
        XYPlot xyplot = (XYPlot) jfreechart.getPlot();//图标区对象
        ValueAxis dateaxis =  xyplot.getDomainAxis();
        frame1=new ChartPanel(jfreechart,true);
        dateaxis.setLabelFont(new Font("黑体",Font.BOLD,14));  //水平底部标题
        dateaxis.setTickLabelFont(new Font("宋体",Font.BOLD,12));  //垂直标题
        ValueAxis rangeAxis=xyplot.getRangeAxis();//获取柱状
        rangeAxis.setLabelFont(new Font("黑体",Font.BOLD,15));
        jfreechart.getLegend().setItemFont(new Font("黑体", Font.BOLD, 15));
        jfreechart.getTitle().setFont(new Font("宋体",Font.BOLD,20));//设置标题字体

    }

    private boolean compare(ArrayList<Edge> a1,ArrayList<Edge> a2)
    {
        if(a1.size()!=a2.size()) return false;
        else {
            for (int i=0;i<a1.size();i++)
            {
                if(!a1.get(i).equals(a2.get(i))) return false;
            }
        }
        return true;
    }

}
