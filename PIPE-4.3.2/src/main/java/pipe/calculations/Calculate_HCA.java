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
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.w3c.dom.ls.LSException;
import pipe.extensions.jpowergraph.PIPEFailureTextEdge;
import pipe.extensions.jpowergraph.PIPEHazardousState;
import pipe.views.PetriNetView;
import pipe.views.TransitionView;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.math.BigDecimal;
import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Calculate_HCA {
    DefaultGraph graph;
    ArrayList<HCA> _model;
    PetriNetView _pn;
    Double comp=new Double("1E-9");

    ChartPanel frame1;
    XYSeriesCollection seriescollection = new XYSeriesCollection();

    //PetriNet中变迁与时间的对应关系
    HashMap<String,String> Tr_Time=new HashMap<String, String>();


    final int MAX_THREADS = 12; //定义线程数最大值
    ExecutorService executorService = Executors.newFixedThreadPool(MAX_THREADS);


    //结果,到达每个HCA的路径
    HashMap<String,ArrayList<ArrayList<Edge>>> res=new HashMap<String, ArrayList<ArrayList<Edge>>>();
    //结果所有到达HCA的路径
    ArrayList<ArrayList<Edge>> res_path=new ArrayList<ArrayList<Edge>>();
    //对应的概率
    ArrayList<Double> res_pro=new ArrayList<Double>();

    //每条路径的分布概率与发生概率
    ConcurrentHashMap<Integer,double[]> pathF=new ConcurrentHashMap<Integer, double[]>();
    ConcurrentHashMap<Integer,Double> pathpro=new ConcurrentHashMap<Integer, Double>();

    //存入stack的节点
    ArrayList<Node> backlist=new ArrayList<Node>();

    public Calculate_HCA(DefaultGraph gr,ArrayList<HCA> model,PetriNetView pn)
    {
        graph=gr;
        _model=model;
        _pn=pn;
        for(TransitionView tr:_pn.getTransitionsArrayList())
        {
            Tr_Time.put(tr.getId(),tr.getGraphTime());
        }

    }

    public void getHcaRoad()
    {
        Node star=graph.getAllNodes().get(0);
        //获取所有路径
        //getAllRoad(star,1.0);
        readPath();
        //导出路径到文本文档
        //outputPath();

        System.out.println("s");

        double[] ans=getconvres(res_path,res_pro);
        //计算曲线
        calSeries(ans);
        //设置统计表参数
        paintChart();
        //显示统计表
        showframe();

        System.out.println();
    }

    private void getAllRoad(Node star,Double pro) {
        backlist.add(star);
        //以star为起点开始探索
        //求该点出发的权重之和,计算路径概率用
        Double weightsum = 0.0;
        for (Edge e : star.getEdgesFrom()) {
            Double wei = 1.0 * (_pn.getTransitionById(((TextEdge) e).getText()).get_weight());
            if (e instanceof PIPEFailureTextEdge) wei = wei / 10000;
            weightsum += wei;
        }

        //深度优先搜索
        for (Edge e : star.getEdgesFrom()) {
            Double wei = 1.0 * (_pn.getTransitionById(((TextEdge) e).getText()).get_weight());
            if (e instanceof PIPEFailureTextEdge) wei = wei / 10000;
            //选择该路径后的发生概率
            pro = pro * (wei / weightsum);
            //若该该路径发生的概率大于1的-9次方，则向开始检查该路径
            if (pro.compareTo(comp) == 1) {
                //找到一条有效路径
                if (e.getTo() instanceof PIPEHazardousState) {
                    backlist.add(e.getTo());
                    ArrayList<Edge> path = HCA.getpath(backlist);
                    //搜集所有路径以及对应概率
                    //Iscontain(path);
                    res_path.add(path);
                    res_pro.add(pro);
                    System.out.println(res_path.size()+"    "+pro.toString());

                    for (String hca : IsHCA(e.getTo(), path)) {
                        if (res.containsKey(hca)) res.get(hca).add(path);
                        else {
                            ArrayList<ArrayList<Edge>> temp = new ArrayList<ArrayList<Edge>>();
                            temp.add(path);
                            res.put(hca, temp);
                            //搜集所有的路径
                        }
                    }
                    //记录完一条路径后移除并将路径选择概率除以选择该路径的概率
                    backlist.remove(backlist.size() - 1);
                    pro = pro / (wei / weightsum);
                    //mt-=temp_t;
                    continue;
                }
                //若不为一个结果则向下继续遍历
                getAllRoad(e.getTo(), pro);
            }
            //若无法沿着该路径继续则将pro,mt还原，继续循环
            pro = pro / (wei / weightsum);
        }
        backlist.remove(backlist.size() - 1);
    }

    private void outputPath()
    {
        File file=new File("Cal_Res.txt");
        try {
            FileOutputStream fos = new FileOutputStream(file);
            for (int i = 0; i < res_path.size(); i++) {
                StringBuilder temppath=new StringBuilder();
                temppath.append(res_pro.get(i)+" ");
                ArrayList<Edge> temp=res_path.get(i);
                for(int j=0;j<temp.size();j++)
                {
                    temppath.append(((TextEdge)temp.get(j)).getText()+" ");
                }
                fos.write(temppath.toString().getBytes());
                fos.write("\r\n".getBytes());
            }
            fos.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void readPath()
    {
        res_path.clear();
        res_pro.clear();
        try {
            FileReader fr=new FileReader("Cal_Res.txt");
            BufferedReader br=new BufferedReader(fr);
            String line="";
            String[] arrs=null;
            while ((line=br.readLine())!=null) {
                arrs=line.split(" ");
                if(arrs.length>1) {
                    res_pro.add(Double.parseDouble(arrs[0]));
                    res_path.add(getEdges(arrs));
                    System.out.println(line);
                }
            }
            br.close();
            fr.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }


    }

    private ArrayList<Edge>  getEdges(String[] arrs)
    {
        ArrayList<Edge> Temppath=new ArrayList<Edge>();
        if(arrs.length>1) {
            for (int i = 1; i < arrs.length; i++) {
                Temppath.add(getEdgebyText(arrs[i]));
            }
        }
        return Temppath;
    }

    //多个独立同分布的服从lamada参数的随机变量之和服从Erlang分布，利用这个计算
    private double[] getconvres(final ArrayList<ArrayList<Edge>> path, ArrayList<Double> pro )
    {
        //每1s采样一次,长度取到3h即可，因为最后的结果只到3h，而卷积运算，第n项的结果只和两个向量的前n项有关。
        double[] ans=new double[10800];//结果
        //线程计数工具
        final CountDownLatch mStartSignal = new CountDownLatch(10);
        final Integer[] intervel=new Integer[11];
        for(int i=0;i<11;i++)
        {
            intervel[i]=path.size()*i/10;
        }
        for(int j=0;j<10;j++) {
            final int count=j;
            new Thread() {
                public void run() {
                    for (int i = intervel[count]; i < intervel[count+1]; i++) {
                        final ArrayList<Edge> es = path.get(i);
                        final int index = i;

                        int count1 = 0;//lamada为1800
                        int count2 = 0;//lamada为3600
                        int countfix = 0;//固定时延

                        double[] tempans = new double[10800];//一条路径结果
                        for (int j = 0; j < es.size(); j++) {
                            Edge e = es.get(j);
                            String TimeFormula = Tr_Time.get(((TextEdge) e).getText());
                            String[] res = TimeFormula.split("@");

                            if (res.length == 2) {
                                //固定时间的变迁
                                if (res[0].equals("1")) {
                                    if (res[1].equals("1"))
                                        countfix++;
                                    else if (res[1].equals("2"))
                                        countfix += 2;
                                }
                                //按指数分布的时间变迁
                                else if (res[0].equals("2")) {
                                    if (res[1].equals("1800"))
                                        count1++;
                                    else if (res[1].equals("3600"))
                                        count2++;
                                }
                            }
                        }
                        tempans[countfix] = 1;
                        tempans = conv(tempans, Erlang(1.0 / 1800, count1));
                        tempans = conv(tempans, Erlang(1.0 / 3600, count2));

                        double[] sum=new double[37];
                        //每300s一段统计概率和
                        for (int k = 0; k < 10800;k++) {
                            int count = (int) Math.round(1.0D * k / 300.0D);
                            sum[count] += tempans[k];
                        }
                        pathF.put(index, sum);
                        pathpro.put(index, new Double(res_pro.get(index)));

                        System.out.println(pathF.size());
                    }
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

        for(Map.Entry<Integer,double[]> entry:pathF.entrySet()) {
            ans = diradd(ans, entry.getValue(), pathpro.get(entry.getKey()));
        }

        return ans;
    }

    //生成Erlang分布的double数组
    private  double[] Erlang(double lamada,int n)
    {
        double[] ans = new double[10800];
        if(n>0) {
            double a = Math.pow(lamada, 1.0D * n) / factorial(n - 1) * 1.0D;
            for (int i = 1; i < 10800; i++) {
                ans[i] = a * Math.pow(1.0D * i, 1.0D * (n - 1)) * Math.exp(-1.0D * lamada * i);
            }
        }
        else
        {
            ans[0]=1;
        }
        return ans;
    }

    //阶乘
    private int factorial(int number) {
        if (number <= 1)
            return 1;
        else
            return number * factorial(number - 1);
    }

    //卷积
    private double[] conv(double[] a,double[] b)
    {
        double[] sss=new double[10800];
        for (int n = 0; n < 10800; n++) {
            for (int m = 0; m <=n; m++) {
                sss[n] += (a[m] * b[n - m]);
            }
        }
        return  sss;
    }

    //为固定时延变迁写的卷积，实际上就是往右i位
    private double[] conv(double[] a,int i)
    {
        double[] sss=new double[10800];
        for (int n = 10799; n >=i; n--) {
                sss[n] = a[n-i] ;
        }
        return  sss;
    }

    private double[] diradd(double[] a,double[] b,Double pro)
    {
        double[] sss=new double[37];
        for (int n = 0; n < 37; n++) {
            sss[n] = a[n] + b[n]*pro;
        }
        return  sss;

    }

    public HashMap<String, ArrayList<ArrayList<Edge>>> getRes() {
        return res;
    }

    public ArrayList<ArrayList<Edge>> getRes_path() {
        return res_path;
    }

    public void reset()
    {
        res.clear();
        backlist.clear();
    }

    //判断结点是否进入Hazard
    private ArrayList<String> IsHCA(Node node,ArrayList<Edge> path)
    {
        ArrayList<String> res=new ArrayList<String>();
        for(HCA hs:_model)
        {
            if(hs.get_HazardNode().equals(node.getLabel()))
            {
                if(hs.IsMatch2(path,node.getLabel())) {
                    res.add(hs.get_HCANo());
                }
            }
        }
        return res;
    }

    private  boolean check(Edge e1,Edge e2)
    {
        if(e1.getTo().equals(e2.getFrom())) return true;
        else  return false;
    }

    private boolean Iscontain(List<Edge> path)
    {
        boolean flag=false;
        for (int i= 0; i<res_path.size() ; i++) {
            if (path.size() == res_path.get(i).size()) {
                for (int j = 0; j < path.size(); j++) {
                    if (!path.get(j).equals(res_path.get(i).get(j))) break;
                    else {
                        if (j == path.size() - 1) flag = true;
                    }
                }
            }
        }
        if(flag)
            System.out.println("Iscontain false");
        return flag;
    }

    private Edge getEdgebyText(String text)
    {
        List<Edge> allEdge=graph.getAllEdges();
        for(Edge e:allEdge)
        {
            if(((TextEdge)e).getText().equals(text)) return e;
        }
        return null;
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

    //计算曲线
    private void  calSeries(double[] sum)
    {
        XYSeries series = new XYSeries("危险状态分布");
        try {
            FileOutputStream fos = new FileOutputStream("E:\\Cal.txt");

            for(int i=0;i<sum.length;i++) {
                series.add(i*300, sum[i]);
                fos.write((Double.toString(sum[i]) + "\r\n").getBytes());
            }

            seriescollection.addSeries(series);
            fos.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void showframe()
    {
        JFrame frame=new JFrame("Java数据统计图");
        frame.setLayout(new GridLayout(2,2,10,10));
        frame.add(frame1);    //添加折线图
        frame.setBounds(50, 50, 800, 600);
        frame.setVisible(true);
    }
}
