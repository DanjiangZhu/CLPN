package pipe.gui.widgets;

import pipe.utilities.math.Matrix;
import pipe.views.PetriNetView;
import pipe.views.PlaceView;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

public class GraphHazardousFrame extends JFrame {
    JTable table=new JTable();
    PetriNetView sourcePetriNet=null;
    JTextField formula_field=new JTextField(20);

    int HazardNum=0;

    Vector<Vector<String>> data=new Vector<Vector<String>>();
    Vector<String> colname=new Vector<String>();

    public GraphHazardousFrame()
    {
        setSize(750, 400);
        setLocation(100, 100);

        addWindowListener(new WindowAdapter()
        {
            public void windowClosing(WindowEvent wev)
            {
                Window w = wev.getWindow();
                w.setVisible(false);
                w.dispose();
            }
        });
        colname.add("HazardName");
        colname.add("HazardState");
        colname.add("HazardMark");

        //添加表格
        JPanel tablepanel=new JPanel();
        DefaultTableModel dt=new DefaultTableModel(data,colname);
        table=new JTable(dt);
        table.setPreferredScrollableViewportSize(new Dimension(650, 100));
        JScrollPane s= new JScrollPane(table);
        tablepanel.add(s);

        //添加按钮
        ButtonBar add=new ButtonBar("add",AddHazard);
        ButtonBar delete=new ButtonBar("delete",DeleteHazard);
        JPanel buttons=new JPanel();
        buttons.add(add,BorderLayout.EAST);
        buttons.add(delete,BorderLayout.WEST);

        //添加文本框
        formula_field.setFont(new Font("Times New Roman", Font.PLAIN, 20));
        JPanel textPanel=new JPanel();
        textPanel.add(formula_field);
        formula_field.setText("P4==1&&P7==1");

        for(int i=0;i<dt.getColumnCount();i++)
        {
            if(i==0)
                table.getColumnModel().getColumn(i).setPreferredWidth(50);
            else
                table.getColumnModel().getColumn(i).setPreferredWidth(200);
        }
        Container contentpane=this.getContentPane();
        contentpane.add(tablepanel,BorderLayout.NORTH);
        contentpane.add(textPanel,BorderLayout.CENTER);
        contentpane.add(buttons,BorderLayout.SOUTH);

        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    }
    public void constructGraphHazardousFrame(PetriNetView pn)
    {
        sourcePetriNet=pn;
        setVisible(true);
    }

    private final ActionListener AddHazard=new ActionListener(){
        public void actionPerformed(ActionEvent e) {
//            String HazardMark=createHazard(sourcePetriNet.getPlacesArrayList(),formula_field.getText());
//            Vector<String> temp=new Vector<String>();
//            temp.add("H"+HazardNum++);
//            temp.add(formula_field.getText());
//            temp.add(HazardMark);
            data.add(w4test("P4==1&&P7==1"));
            data.add(w4test("P4==1&&P0==1"));
            data.add(w4test("P5==1&&P7==1"));
            data.add(w4test("P9==1&&P7==1"));

//            data.add(temp);
            DefaultTableModel dt=new DefaultTableModel(data,colname);
            table.setModel(dt);
        }
    };
    private final Vector<String> w4test(String text)
    {
        String HazardMark=createHazard(sourcePetriNet.getPlacesArrayList(),text);
        Vector<String> temp=new Vector<String>();
        temp.add("H"+HazardNum++);
        temp.add(text);
        temp.add(HazardMark);
        return  temp;
    }
    private final ActionListener DeleteHazard=new ActionListener(){
        public void actionPerformed(ActionEvent e) {
            int selectRow=table.getSelectedRow();
            DefaultTableModel dt=(DefaultTableModel) table.getModel();
            dt.removeRow(selectRow);
            data=dt.getDataVector();
        }
    };

    //析取范式转Hazard判定条件
    public String createHazard(ArrayList<PlaceView> _placeViews,String formula)
    {
        String HazardousMark="";
        StringBuilder sb=new StringBuilder(HazardousMark);

        for (int i=0;i<_placeViews.size();i++) {
            if(i==_placeViews.size()-1) sb.insert(i * 3 , " *}");
            else if(i==0) sb.insert(0,"{*,");
            else sb.insert(i * 3, " *,");
        }
        String[] and = formula.split("\\&\\&");
        if(and.length>0) {
            for (int j=0;j<and.length;j++)
            {
                String PID=and[j].split("==")[0].trim();
                String Num=and[j].split("==")[1].trim();
                PlaceView targetP=null;
                for(PlaceView pv:_placeViews)
                {
                    if(pv.getId().equals(PID)) targetP=pv;
                }
                int index=_placeViews.indexOf(targetP);
                if(index!=-1){
                sb.delete(3*index+1,3*index+2);
                sb.insert(3*index+1,Num);
                }
            }
        }
        return sb.toString();
    }

    public Vector<Vector<String>> getData()
        {
            return data;
        }
}

