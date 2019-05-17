package pipe.gui.widgets;

import net.sourceforge.jpowergraph.defaults.DefaultGraph;
import pipe.calculations.HCA;
import pipe.calculations.SearchHCA;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Vector;

public class HCAFrame extends JFrame {
    JTable table=new JTable();

    ArrayList<HCA> HCAsses =new ArrayList<HCA>();

    DefaultGraph graph=null;

    Vector<Vector<String>> data=new Vector<Vector<String>>();
    Vector<String> colname=new Vector<String>();

    public HCAFrame(DefaultGraph gr)
    {
        setSize(750, 400);
        setLocation(100, 100);

        graph=gr;
        addWindowListener(new WindowAdapter()
        {
            public void windowClosing(WindowEvent wev)
            {
                Window w = wev.getWindow();
                w.setVisible(false);
                w.dispose();
            }
        });
        colname.add("ID");
        colname.add("Hazardous Control Action");
        colname.add("Hazardous states");
        colname.add("Related Harzards");

        //添加表格
        JPanel tablepanel=new JPanel();
        DefaultTableModel dt=new DefaultTableModel(data,colname);
        table=new JTable(dt);
        table.setPreferredScrollableViewportSize(new Dimension(650, 100));
        JScrollPane s= new JScrollPane(table);
        tablepanel.add(s);


        for(int i=0;i<dt.getColumnCount();i++)
        {
            if(i==0)
                table.getColumnModel().getColumn(i).setPreferredWidth(50);
            else
                table.getColumnModel().getColumn(i).setPreferredWidth(200);
        }
        Container contentpane=this.getContentPane();
        contentpane.add(tablepanel,BorderLayout.CENTER);

        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    }
    public void constructHCAtable()
    {
        SearchHCA sh=new SearchHCA(graph);
        data=sh.constrcutTable();
        DefaultTableModel dt=new DefaultTableModel(data,colname);
        table.setModel(dt);
        HCAsses =sh.getHCAsses();
        setVisible(true);

    }
    public Vector<Vector<String>>  getdata()
    {
        return data;
    }

    public ArrayList<HCA> getHCAsses() {
        return HCAsses;
    }
}
