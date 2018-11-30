package pipe.gui.widgets;

import parser.ExprEvaluator;
import pipe.gui.ApplicationSettings;
import pipe.gui.PetriNetTab;
import pipe.views.ArcView;
import pipe.views.LogicalTransitionView;
import pipe.views.PetriNetView;
import pipe.views.TransitionView;
import pipe.views.viewComponents.RateParameter;

import javax.swing.*;
import javax.swing.event.CaretListener;

import net.sourceforge.jeval.EvaluationException;

import java.awt.*;
import java.util.Enumeration;
import java.util.Iterator;


/**
 *
 * @author  pere
 * @author yufei wang
 */
public class TransitionEditorPanel
        extends javax.swing.JPanel {
   
   /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
private final TransitionView _transitionView;
   private final boolean attributesVisible;
   private final boolean timed;
   private final boolean infiniteServer;
   private Integer priority = 0;
   private final double rate;
   private final String name;
   private final RateParameter rParameter;
   private final PetriNetView _pnmlData;
   private final PetriNetTab _view;
   private final JRootPane rootPane;
   
   
   /**
    * Creates new form PlaceEditor
    * @param _rootPane
    * @param _transitionView
    * @param _pnmlData
    * @param _view
    */
   public TransitionEditorPanel(JRootPane _rootPane, TransitionView _transitionView,
           PetriNetView _pnmlData, PetriNetTab _view) {
      this._transitionView = _transitionView;
      this._pnmlData = _pnmlData;
      this._view = _view;
      rParameter = this._transitionView.getRateParameter();
      name = this._transitionView.getName();
      timed = this._transitionView.isTimed();
      infiniteServer = this._transitionView.isInfiniteServer();
      rootPane = _rootPane;
      
      initComponents();
      
      this.serverLabel.setVisible(true);
      this.serverPanel.setVisible(true);
      
      rootPane.setDefaultButton(okButton);

      attributesVisible = this._transitionView.getAttributesVisible();
      
      rate = this._transitionView.getRate();
         
      if (timed){
         timedTransition();
      } else {
         immediateTransition();
         priority = _transitionView.getPriority();
      }
      
      if (infiniteServer) {
         infiniteServerRadioButton.setSelected(true);
      } else {
         singleServerRadioButton.setSelected(true);
      }
      
      
      if (rParameter != null){
         for (int i = 1; i < rateComboBox.getItemCount(); i++) {
            if (rParameter == rateComboBox.getItemAt(i)){
               rateComboBox.setSelectedIndex(i);
            }
         }
      }      
    }
   
   
   private void timedTransition(){
      timedRadioButton.setSelected(true);
      
      
//      rateLabel.setText("Constant Rate:");
//      functionalratelabel.setText("Functional Rate:");
//      if(_transitionView.isConst()){
//    	  rateTextField.setText("" + _transitionView.getRate());
//    	  rateTextField.setEnabled(true);
//    	  functionalratebutton.setEnabled(false);
//    	  functionalratebutton.setText("Expression editor");
//    	  functionalRateCheckbox.setSelected(false);
//    	  constantRateCheckbox.setSelected(true);
//      }else{
//    	  rateTextField.setEnabled(false);
//    	  functionalratebutton.setEnabled(true);
//    	  functionalratebutton.setText("Expression editor");
//    	  functionalRateCheckbox.setSelected(true);
//    	  constantRateCheckbox.setSelected(false);
//      }
      rateLabel.setText("Rate:");
      if(_transitionView.isInfiniteServer()){
    	  rateTextField.setText("ED(" + _transitionView.getName()+")");    	 
    	  rateTextField.setEditable(false);
      }else{
    	  rateTextField.setText("" + _transitionView.getRateExpr());
    	  rateTextField.setEditable(true);
      }
      rateTextField.setEnabled(true);
      functionalratebutton.setEnabled(true);
      functionalratebutton.setText("Rate Expression editor");

      
      prioritySlider.setEnabled(false);
      priorityTextField.setText("0");
      
      Enumeration buttons = semanticsButtonGroup.getElements();
      while (buttons.hasMoreElements()){
         ((AbstractButton)buttons.nextElement()).setEnabled(true);
      }      
      
      priorityLabel.setEnabled(false);
      priorityPanel.setEnabled(false);

      RateParameter[] rates = _pnmlData.markingRateParameters();
      if (rates.length > 0) {
         rateComboBox.addItem("");
          for(RateParameter rate1 : rates)
          {
              rateComboBox.addItem(rate1);
          }
      } else {
         rateComboBox.setEnabled(false);
      }      
   }
   
   
   private void immediateTransition(){
      immediateRadioButton.setSelected(true); 
      

      
//      rateLabel.setText("Constant Weight:");
//      functionalratelabel.setText("Functional Weight:");
//      if(_transitionView.isConst()){
//    	  rateTextField.setText("" + _transitionView.getRate());
//    	  rateTextField.setEnabled(true);
//    	  functionalratebutton.setEnabled(false);
//    	  functionalratebutton.setText("Weight editor");
//    	  functionalRateCheckbox.setSelected(false);
//    	  constantRateCheckbox.setSelected(true);
//      }else{
//    	  rateTextField.setEnabled(false);
//    	  functionalratebutton.setEnabled(true);
//    	  functionalratebutton.setText("Weight editor");
//    	  functionalRateCheckbox.setSelected(true);
//    	  constantRateCheckbox.setSelected(false);
//      }
      rateLabel.setText("Weight:");
      //functionalratelabel.setText("Functional Weight:");
      if(_transitionView.isInfiniteServer()){
    	  rateTextField.setText("ED(" + _transitionView.getName()+")");    
    	  rateTextField.setEditable(false);
      }else{
    	  rateTextField.setText("" + _transitionView.getRate());
    	  rateTextField.setEditable(true);
      }
    	  rateTextField.setEnabled(false);
    	  functionalratebutton.setEnabled(true);
    	  functionalratebutton.setText("Weight expression editor");

      
      prioritySlider.setEnabled(true);
      priorityTextField.setText("" + _transitionView.getPriority());
      
      priorityLabel.setEnabled(true);
      priorityPanel.setEnabled(true);   
      

      RateParameter[] rates = _pnmlData.markingRateParameters();
      if (rates.length > 0) {
         rateComboBox.addItem("");
          for(RateParameter rate1 : rates)
          {
              rateComboBox.addItem(rate1);
          }
      } else {
         rateComboBox.setEnabled(false);
      }            
   }

    /**
     *该方法相当于是一个GridBagConstraints的构造函数，只是少了几个参数而已。
     * @param gridx
     * @param gridy
     * @param gridwidth
     * @param anchor
     * @param fill
     * @return
     */
   private GridBagConstraints gridBagConstraintsGEN(int gridx, int gridy, int gridwidth, int anchor, int fill){
        GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = gridx;
        gridBagConstraints.gridy = gridy;
        gridBagConstraints.gridwidth = gridwidth;//默认值为1
        gridBagConstraints.anchor = anchor;//默认值为CENTER
        gridBagConstraints.fill = fill;//默认值为NONE，不调整组件大小
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        return gridBagConstraints;
    }


   /** This method is called from within the constructor to
    * initialize the form.
    * WARNING: Do NOT modify this code. The content of this method is
    * always regenerated by the Form Editor.
    */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        JPanel transitionEditorPanel = new JPanel();

        JLabel nameLabel = new JLabel();
        nameTextField = new JTextField();

        rateLabel = new javax.swing.JLabel();
        rateTextField = new javax.swing.JTextField();
        rateComboBox = new javax.swing.JComboBox();

        functionalratebutton=new javax.swing.JButton();

        serverLabel = new javax.swing.JLabel();
        serverPanel = new javax.swing.JPanel();
        semanticsButtonGroup = new javax.swing.ButtonGroup();
        singleServerRadioButton = new javax.swing.JRadioButton();
        infiniteServerRadioButton = new javax.swing.JRadioButton();

        JLabel timingLabel = new JLabel();
        JPanel timingPanel = new JPanel();
        ButtonGroup timingButtonGroup = new ButtonGroup();
        timedRadioButton = new javax.swing.JRadioButton();
        immediateRadioButton = new javax.swing.JRadioButton();

        priorityLabel = new javax.swing.JLabel();
        priorityPanel = new javax.swing.JPanel();
        prioritySlider = new javax.swing.JSlider();
        priorityTextField = new javax.swing.JTextField();

        JLabel rotationLabel = new JLabel();
        rotationComboBox = new javax.swing.JComboBox();

        attributesCheckBox = new javax.swing.JCheckBox();

        JPanel buttonPanel = new JPanel();
        JButton cancelButton = new JButton();
        okButton = new javax.swing.JButton();

        setLayout(new java.awt.GridBagLayout());


        //布局控制
        int nameLabelGridX = 0, nameLabelGridY = 0;//nameLabel在grid中的位置是（0,0）
        int nameTextFieldGridX = nameLabelGridX + 1, nameTextFieldGridY = nameLabelGridY;


        int timingGridX = 0;//timingLabel的位置
        int timingGridY = 0;
        int timgingPanelGridX = 0;
        int timgingPanelGridY = 0;

        int rateGridX = 0, rateGridY = 0;//rateLabel的位置
        int rateTextFieldGridX = 0, rateTextFieldGridY = 0;
        int rateComboBoxGridX = 0,rateComboBoxGridY = 0;;
        int editorGridX = 0, editorGridY = 0;

        //transitionEditorPanel布局
        transitionEditorPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Transition Editor"));
        transitionEditorPanel.setLayout(new java.awt.GridBagLayout());

        GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        add(transitionEditorPanel, gridBagConstraints);

        //nameLabel的布局
        nameLabel.setText("Name:");

        GridBagConstraints nameLabelgridBagConstraints = gridBagConstraintsGEN(nameLabelGridX,nameLabelGridY,1,GridBagConstraints.EAST,GridBagConstraints.NONE);
        transitionEditorPanel.add(nameLabel, nameLabelgridBagConstraints);

        //nameTextField布局
        nameTextField.setText(_transitionView.getName());
        nameTextField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                nameTextFieldFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                nameTextFieldFocusLost(evt);
            }
        });

        GridBagConstraints nameTextFieldgridBagConstraints = gridBagConstraintsGEN(nameTextFieldGridX,nameTextFieldGridY,3,GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL);
        transitionEditorPanel.add(nameTextField, nameTextFieldgridBagConstraints);


        if(timed || (_transitionView instanceof LogicalTransitionView)){
            timingGridY = 1;
            timgingPanelGridX = timingGridX + 1;
            timgingPanelGridY = timingGridY;

            rateGridY = 1;
            rateTextFieldGridX = rateGridX + 1;
            rateTextFieldGridY = rateGridY;
            rateComboBoxGridX = rateTextFieldGridX + 2;
            rateComboBoxGridY = rateGridY;
            editorGridX = rateTextFieldGridX;
            editorGridY = 1;

if(_transitionView instanceof LogicalTransitionView) {
    //timingLabel布局
    timingLabel.setText("Timing:");

    GridBagConstraints timingLabelGBC = gridBagConstraintsGEN(timingGridX, timingGridY, 1, GridBagConstraints.NORTHEAST, GridBagConstraints.NONE);

    transitionEditorPanel.add(timingLabel, timingLabelGBC);


    //timingPanel布局
    timingPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
    timingPanel.setLayout(new java.awt.GridLayout(1, 0));


    GridBagConstraints timingPanelGBC = gridBagConstraintsGEN(timgingPanelGridX, timgingPanelGridY, 3, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL);

    transitionEditorPanel.add(timingPanel, timingPanelGBC);


    //TimedRadioButton布局
    timingButtonGroup.add(timedRadioButton);
    timedRadioButton.setText("Timed");
    timedRadioButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
    timedRadioButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
    timedRadioButton.setMaximumSize(new java.awt.Dimension(90, 15));
    timedRadioButton.setMinimumSize(new java.awt.Dimension(90, 15));
    timedRadioButton.setPreferredSize(new java.awt.Dimension(90, 15));
    timedRadioButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            timedRadioButtonActionPerformed(evt);
        }
    });
    timingPanel.add(timedRadioButton);


    //immediateRadioButton布局
    timingButtonGroup.add(immediateRadioButton);
    immediateRadioButton.setText("Immediate");
    immediateRadioButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
    immediateRadioButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
    immediateRadioButton.setMaximumSize(new java.awt.Dimension(90, 15));
    immediateRadioButton.setMinimumSize(new java.awt.Dimension(90, 15));
    immediateRadioButton.setPreferredSize(new java.awt.Dimension(90, 15));
    immediateRadioButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            immediateRadioButtonActionPerformed(evt);
        }
    });
    timingPanel.add(immediateRadioButton);
}

            //rateLabel布局
            rateLabel.setText("Constant Rate:");

            GridBagConstraints rateGBC = gridBagConstraintsGEN(rateGridX,(rateGridY + timingGridY),1,GridBagConstraints.EAST,GridBagConstraints.NONE);
            transitionEditorPanel.add(rateLabel, rateGBC);


            //rateTextField布局
            rateTextField.setMaximumSize(new java.awt.Dimension(40, 19));
            rateTextField.setMinimumSize(new java.awt.Dimension(40, 19));
            rateTextField.setPreferredSize(new java.awt.Dimension(40, 19));
            rateTextField.addCaretListener(new javax.swing.event.CaretListener() {
                public void caretUpdate(javax.swing.event.CaretEvent evt) {
                    rateTextFieldCaretUpdate(evt);
                }
            });
            rateTextField.addFocusListener(new java.awt.event.FocusAdapter() {
                public void focusGained(java.awt.event.FocusEvent evt) {
                    rateTextFieldFocusGained(evt);
                }
                public void focusLost(java.awt.event.FocusEvent evt) {
                    rateTextFieldFocusLost(evt);
                }
            });

            GridBagConstraints rateTextFieldGBC = gridBagConstraintsGEN(rateTextFieldGridX,(rateTextFieldGridY + timingGridY),1,GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL);
            transitionEditorPanel.add(rateTextField, rateTextFieldGBC);

            //rateComboBox布局
            rateComboBox.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    rateComboBoxActionPerformed(evt);
                }
            });

            GridBagConstraints rateComboBoxGBC = gridBagConstraintsGEN(rateComboBoxGridX,(rateComboBoxGridY + timingGridY),2,GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL);
            transitionEditorPanel.add(rateComboBox, rateComboBoxGBC);

            //Rate Expression Editor布局
            functionalratebutton.setText("Editor");

            GridBagConstraints editorGBC = gridBagConstraintsGEN(editorGridX,(editorGridY + rateTextFieldGridY + timingGridY),1,GridBagConstraints.EAST,GridBagConstraints.NONE);
            transitionEditorPanel.add(functionalratebutton, editorGBC);

            functionalratebutton.addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    createWindow();
                }

            });
        }


        int serverLabelGridX = 0, serverLabelGridY = 0;
        int serverPanelGridX = 0, servedrPanelGridY = 0;

        //infiniteServer为true时，才显示server相关的组件
        if(infiniteServer){
            serverLabelGridY = 1;
            serverPanelGridX = serverLabelGridX + 1;
            servedrPanelGridY = serverLabelGridY;

            //serverLabel布局
            serverLabel.setText("Server:");

            GridBagConstraints serverLabelGBC = gridBagConstraintsGEN(serverLabelGridX,(serverLabelGridY + editorGridY + rateTextFieldGridY + timingGridY),1,GridBagConstraints.NORTHWEST,GridBagConstraints.HORIZONTAL);

            transitionEditorPanel.add(serverLabel, serverLabelGBC);

            //serverPanel布局
            serverPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
            serverPanel.setLayout(new java.awt.GridLayout(1, 0));


            GridBagConstraints serverPanelGBC = gridBagConstraintsGEN(serverPanelGridX,(servedrPanelGridY + editorGridY + rateTextFieldGridY + timingGridY),1,GridBagConstraints.NORTHWEST,GridBagConstraints.HORIZONTAL);

            transitionEditorPanel.add(serverPanel, serverPanelGBC);

            //singleRadioButton布局
            semanticsButtonGroup.add(singleServerRadioButton);
            singleServerRadioButton.setText("Single");
            singleServerRadioButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
            singleServerRadioButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
            singleServerRadioButton.setMaximumSize(new java.awt.Dimension(90, 15));
            singleServerRadioButton.setMinimumSize(new java.awt.Dimension(90, 15));
            singleServerRadioButton.setPreferredSize(new java.awt.Dimension(90, 15));
            singleServerRadioButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    serverRadioButtonActionPerformed(evt);
                }
            });

            serverPanel.add(singleServerRadioButton);

            //InfiniteRadioButton布局
            semanticsButtonGroup.add(infiniteServerRadioButton);
            infiniteServerRadioButton.setSelected(true);
            infiniteServerRadioButton.setText("Infinite");
            infiniteServerRadioButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
            infiniteServerRadioButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
            infiniteServerRadioButton.setMaximumSize(new java.awt.Dimension(90, 15));
            infiniteServerRadioButton.setMinimumSize(new java.awt.Dimension(90, 15));
            infiniteServerRadioButton.setPreferredSize(new java.awt.Dimension(90, 15));
            infiniteServerRadioButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    serverRadioButtonActionPerformed(evt);
                }
            });
            serverPanel.add(infiniteServerRadioButton);
        }



        int logicLabelGridX = 0, logicLabelGridY;
        int logicPanelGridX, logicPanelGridY = 0;
        if(_transitionView instanceof LogicalTransitionView){
            logicLabelGridY = 1;
            logicPanelGridX = 0;
            logicPanelGridY = 1;

            JLabel logicLabel = new JLabel("Control Logic:");//todo-Dj JLbael的布局
            GridBagConstraints logicLabelGBC = gridBagConstraintsGEN(logicLabelGridX,(logicLabelGridY + servedrPanelGridY + editorGridY + rateTextFieldGridY + timingGridY),1,GridBagConstraints.NORTHWEST,GridBagConstraints.NONE);
            transitionEditorPanel.add(logicLabel,logicLabelGBC);

            //控制逻辑相关输入
            logicPanel = new JPanel();
            logicExpressionText = new JTextArea();
            logicExpressionText.setLineWrap(true);//自动换行
            logicExpressionText.setRows(3);//todo-Dj: 宽度还没改好
            logicExpressionText.setText(((LogicalTransitionView) _transitionView).getFormula());

            JScrollPane logicScrollPane = new JScrollPane(logicExpressionText);
            logicScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
            logicScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
//            logicScrollPane.setBorder(javax.swing.BorderFactory.createTitledBorder("Logic input:"));//todo-DJ 需实现逻辑公式编辑功能

//            logicPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
//            logicPanel.setLayout(new java.awt.GridLayout(1,0));
            logicPanel.add(logicScrollPane);

            GridBagConstraints logicPanelGBC = gridBagConstraintsGEN(logicPanelGridX,(logicPanelGridY + servedrPanelGridY + editorGridY + rateTextFieldGridY + timingGridY),3,GridBagConstraints.WEST,GridBagConstraints.BOTH);


//            logicPanelGridY = 3;

            transitionEditorPanel.add(logicPanel,logicPanelGBC);
        }


        //priorityPanel布局
        int priorityPanelGridX = 1, priorityPanelGridY = logicPanelGridY + servedrPanelGridY + editorGridY + rateTextFieldGridY + timingGridY + 1;
        GridBagConstraints priorityPanelGBC = gridBagConstraintsGEN(priorityPanelGridX,priorityPanelGridY,3,GridBagConstraints.CENTER,GridBagConstraints.NONE);

        transitionEditorPanel.add(priorityPanel, priorityPanelGBC);

        //PriorityLabel布局
        priorityLabel.setText("Priority:");

        int priorityGridX = 0;
        int priorityGridY = priorityPanelGridY;
        GridBagConstraints priorityGBC  = gridBagConstraintsGEN(priorityGridX,priorityGridY,1,GridBagConstraints.EAST,GridBagConstraints.NONE);

        transitionEditorPanel.add(priorityLabel, priorityGBC);


        //prioritySlider布局
        prioritySlider.setMajorTickSpacing(50);
        prioritySlider.setMaximum(127);
        prioritySlider.setMinimum(1);
        prioritySlider.setMinorTickSpacing(1);
        prioritySlider.setSnapToTicks(true);
        prioritySlider.setToolTipText("1: lowest priority; 127: highest priority");
        prioritySlider.setValue(_transitionView.getPriority());
        prioritySlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                prioritySliderStateChanged(evt);
            }
        });
        priorityPanel.add(prioritySlider);


        //priorityTextField布局
        priorityTextField.setEditable(false);
        priorityTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        priorityTextField.setText("1");
        priorityTextField.setMaximumSize(new java.awt.Dimension(36, 19));
        priorityTextField.setMinimumSize(new java.awt.Dimension(36, 19));
        priorityTextField.setPreferredSize(new java.awt.Dimension(36, 19));
        priorityTextField.setText(""+ _transitionView.getPriority());
        priorityPanel.add(priorityTextField);


        //RotationLabel布局
        rotationLabel.setText("Rotation:");
        int rotationGridX = 0, rotationGridY = priorityGridY + 1;
        GridBagConstraints rotationGBC = gridBagConstraintsGEN(rotationGridX,rotationGridY,1,GridBagConstraints.NORTH,GridBagConstraints.NONE);

        transitionEditorPanel.add(rotationLabel, rotationGBC);


        //rotationComboBox布局
        rotationComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "", "+45\u00B0", "+90\u00B0", "-45\u00B0" }));
        rotationComboBox.setMaximumSize(new java.awt.Dimension(70, 20));
        rotationComboBox.setMinimumSize(new java.awt.Dimension(70, 20));
        rotationComboBox.setPreferredSize(new java.awt.Dimension(70, 20));

        int rotationComboBoxGridX = rotationGridX + 1, rotationComboBoxGridY = rotationGridY;
        GridBagConstraints rotationComboBoxGBC = gridBagConstraintsGEN(rotationComboBoxGridX,rotationComboBoxGridY,1,GridBagConstraints.NORTHWEST,GridBagConstraints.NONE);

        transitionEditorPanel.add(rotationComboBox, rotationComboBoxGBC);


        //Show transition attributes布局
        attributesCheckBox.setSelected(_transitionView.getAttributesVisible());
        attributesCheckBox.setText("Show transition attributes");
        attributesCheckBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        attributesCheckBox.setMargin(new java.awt.Insets(0, 0, 0, 0));

        int showTransAttrGridX = 1;
        int showTransAttrGridY = rotationGridY + 1;
        GridBagConstraints showTransAttrGBC  = gridBagConstraintsGEN(showTransAttrGridX,showTransAttrGridY,2,GridBagConstraints.NORTHWEST,GridBagConstraints.NONE);

        transitionEditorPanel.add(attributesCheckBox, showTransAttrGBC);


        //buttonPanel布局
        buttonPanel.setLayout(new java.awt.GridBagLayout());

        int buttonPanelGridX = 0, buttonPanelGridY = 1;
        GridBagConstraints buttonPanelGBC = gridBagConstraintsGEN(buttonPanelGridX,buttonPanelGridY,1,GridBagConstraints.EAST,GridBagConstraints.NONE);

        buttonPanelGBC.insets = new java.awt.Insets(5, 0, 8, 3);
         add(buttonPanel, buttonPanelGBC);


        //OKbutton布局
        okButton.setText("OK");
        okButton.setMaximumSize(new java.awt.Dimension(75, 25));
        okButton.setMinimumSize(new java.awt.Dimension(75, 25));
        okButton.setPreferredSize(new java.awt.Dimension(75, 25));
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonHandler(evt);
            }
        });
        okButton.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                okButtonKeyPressed(evt);
            }
        });

        int okButtonGridX = 0, okButtonGridY = 1;
        GridBagConstraints okButtonGBC = gridBagConstraintsGEN(okButtonGridX,okButtonGridY,1,GridBagConstraints.EAST,GridBagConstraints.NONE);
        /*        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;*/

        buttonPanel.add(okButton, okButtonGBC);


        //CancelButton布局
        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                cancelButtonHandler(evt);
            }
        });

        int cancelButtonGridX = 1, cancelButtonGridY = 1;
        GridBagConstraints cancelButtonGBC = gridBagConstraintsGEN(cancelButtonGridX,cancelButtonGridY,1,GridBagConstraints.WEST,GridBagConstraints.NONE);
        //        gridBagConstraints.gridx = 1;
//        gridBagConstraints.gridy = 1;

        buttonPanel.add(cancelButton, cancelButtonGBC);

    }// </editor-fold>//GEN-END:initComponents
    
    private void createWindow(){
    	EscapableDialog guiDialog = new EscapableDialog(ApplicationSettings.getApplicationView(), "PIPE2", true);
	    	 TransitionFunctionEditor feditor = new TransitionFunctionEditor(this, guiDialog,_pnmlData,_transitionView);
	    	 guiDialog.add(feditor);
	    	 guiDialog.setSize(270, 230);
	    	 guiDialog.setLocationRelativeTo(ApplicationSettings.getApplicationView());
	         guiDialog.setVisible(true);
	         guiDialog.dispose();
    }

   private void rateTextFieldCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_rateTextFieldCaretUpdate
      try {
         if ((rateComboBox.getSelectedIndex() > 0) &&
            (((RateParameter)rateComboBox.getSelectedItem()).getValue()
                    != Double.parseDouble(rateTextField.getText()))){
            rateComboBox.setSelectedIndex(0);
         }
      } catch (NumberFormatException nfe){
         if (!nfe.getMessage().equalsIgnoreCase("empty String")) {
            System.out.println("NumberFormatException (not Empty String): \n" +
                    nfe.getMessage());
         }
      } catch (Exception e){
         System.out.println(e.toString());
      }
   }//GEN-LAST:event_rateTextFieldCaretUpdate

   private void rateTextFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_rateTextFieldFocusLost
      focusLost(rateTextField);
   }//GEN-LAST:event_rateTextFieldFocusLost

   private void nameTextFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_nameTextFieldFocusLost
      focusLost(nameTextField);
   }//GEN-LAST:event_nameTextFieldFocusLost

   private void nameTextFieldFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_nameTextFieldFocusGained
      focusGained(nameTextField);
   }//GEN-LAST:event_nameTextFieldFocusGained

   private void rateTextFieldFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_rateTextFieldFocusGained
      focusGained(rateTextField);
   }//GEN-LAST:event_rateTextFieldFocusGained

   
   
   private void focusGained(javax.swing.JTextField textField){
      textField.setCaretPosition(0);
      textField.moveCaretPosition(textField.getText().length());
   }
   
   private void focusLost(javax.swing.JTextField textField){
      textField.setCaretPosition(0);
   }   
   
   
   private void rateComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rateComboBoxActionPerformed
      int index = rateComboBox.getSelectedIndex();
      if (index > 0){
         rateTextField.setText(_pnmlData.markingRateParameters()[index-1].getValue().toString());
      }
   }//GEN-LAST:event_rateComboBoxActionPerformed

   
   private void timedRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_timedRadioButtonActionPerformed
      if (timedRadioButton.isSelected()){
         timedTransition();
      } else {
         immediateTransition();
      }
   }//GEN-LAST:event_timedRadioButtonActionPerformed
   
   private void serverRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_timedRadioButtonActionPerformed
	      if (singleServerRadioButton.isSelected()){
	    	  functionalratebutton.setEnabled(true);
	    	  singleServerRadioButton.setSelected(true);
	    	  infiniteServerRadioButton.setSelected(false);
	    	  rateTextField.setEditable(true);
	    	  rateTextField.setText("" + _transitionView.getRate());
	      } else {
	    	  if(checkIfArcsAreFunctional()){
	    		  String message = "Infinite server cannot be connect directly to \r\n"+
	    	  "arcs with functional weights for this version";
	   		   String title = "Error";
	   		   JOptionPane.showMessageDialog(null, message, title, JOptionPane.YES_NO_OPTION);
	   		   functionalratebutton.setEnabled(true);
	    	  singleServerRadioButton.setSelected(true);
	    	  infiniteServerRadioButton.setSelected(false);
	   		   
	   		   return;
	    	  }
	    	  rateTextField.setEditable(false);
	    	  singleServerRadioButton.setSelected(false);
	    	  infiniteServerRadioButton.setSelected(true);
	    	  functionalratebutton.setEnabled(false);
	    	  rateTextField.setText("ED(" + _transitionView.getName()+")");
	      }
	   }//GEN-LAST:event_timedRadioButtonActionPerformed

   private boolean checkIfArcsAreFunctional(){
	   Iterator to =  _transitionView.getConnectToIterator();
	   while (to.hasNext()) {
		   ArcView arcTo = ((ArcView) to.next());
		   arcTo.checkIfFunctionalWeightExists();
		   if(arcTo.isWeightFunctional()){
			   return true;
		   }
	   }
	   return false;
   }
   private void immediateRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_immediateRadioButtonActionPerformed
      if (immediateRadioButton.isSelected()){
         immediateTransition();
      } else {
         timedTransition();
      }
   }//GEN-LAST:event_immediateRadioButtonActionPerformed

   private void prioritySliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_prioritySliderStateChanged
      priorityTextField.setText("" +prioritySlider.getValue());
   }//GEN-LAST:event_prioritySliderStateChanged

   
   private void okButtonKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_okButtonKeyPressed
      if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
         okButtonHandler(new java.awt.event.ActionEvent(this,0,""));
      }
   }//GEN-LAST:event_okButtonKeyPressed


   private final CaretListener caretListener = new javax.swing.event.CaretListener() {
      public void caretUpdate(javax.swing.event.CaretEvent evt) {
         JTextField textField = (JTextField)evt.getSource();
         textField.setBackground(new Color(255,255,255));
         //textField.removeChangeListener(this);
      }
   };   
   
       
   private void okButtonHandler(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonHandler
	   
	   if (infiniteServerRadioButton.isSelected() != infiniteServer) {
	         _view.getHistoryManager().addEdit(
	                 _transitionView.setInfiniteServer(!infiniteServer));
	         _transitionView.repaint();
	         exit();
	         return;
	      }   
	   
	   
	   //if(functionalRateCheckbox.isSelected()){
	   //if(_transitionView.getRate()==-1){
	   ExprEvaluator parser = new ExprEvaluator();
	   double r=0;
	try {
		r = parser.parseAndEvalExprForTransition(rateTextField.getText());
	} catch (EvaluationException e1) {
		// TODO Auto-generated catch block
		//e1.printStackTrace();
	}
	   if(r==-1){
		   // }
		   //if( _transitionView.get_functionalRateExpr()==null||_transitionView.get_functionalRateExpr().equals("")){
		   String message = " Functional rate expression is invalid. Please check your function.";
		   String title = "Error";
		   JOptionPane.showMessageDialog(null, message, title, JOptionPane.YES_NO_OPTION);
		   return;
	   }
	   //  }

      _view.getHistoryManager().newEdit(); // new "transaction""
       
      String newName = nameTextField.getText();
      if (!newName.equals(name)){
         if (_pnmlData.checkTransitionIDAvailability(newName)){
            _view.getHistoryManager().addEdit(_transitionView.setPNObjectName(newName));
         } else{
            // aquest nom no est disponible...
            JOptionPane.showMessageDialog(null,
                    "There is already a transition named " + newName, "Error",
                                JOptionPane.WARNING_MESSAGE);
            return;
         }
      }

      if (timedRadioButton.isSelected() != timed) {
         _view.getHistoryManager().addEdit(
                 _transitionView.setTimed(!timed));
      }
      

         
      
      int newPriority = prioritySlider.getValue();
      if (newPriority != priority && !_transitionView.isTimed()) {
         _view.getHistoryManager().addEdit(_transitionView.setPriority(newPriority));
      }
      
    //first decide whether constant rate or functional rate is given
	  //if(functionalRateCheckbox.isSelected()){
		  //if(_transitionView.getFRateExpr().equals("null")){
		  if(_transitionView.getRateExpr().equals("")){
				String message = "Functional rate expression is empty. Please check.";
                String title = "Error";
                JOptionPane.showMessageDialog(null, message, title, JOptionPane.YES_NO_OPTION);
                return;
		  }else{
			//  _transitionView.setRateExpr(_transitionView.get_functionalRateExpr());
			 // _transitionView.setRateType("F");
		  }
	 // }else{
		  if (rateComboBox.getSelectedIndex() > 0) {
		         // There's a rate parameter selected
		         RateParameter parameter = 
		                 (RateParameter)rateComboBox.getSelectedItem() ;
		         if (parameter != rParameter){

		            if (rParameter != null) {
		               // The rate parameter has been changed
		               _view.getHistoryManager().addEdit(_transitionView.changeRateParameter(
		                       (RateParameter)rateComboBox.getSelectedItem()));
		            } else {
		               // The rate parameter has been changed
		               _view.getHistoryManager().addEdit(_transitionView.setRateParameter(
		                       (RateParameter)rateComboBox.getSelectedItem()));
		            }
					//  _transitionView.setRateType("C");
		         }
		      } else {
		    	  
		    	 // }else{//constant rate given
		    		// There is no rate parameter selected
		    	         if (rParameter != null) {
		    	            // The rate parameter has been changed
		    	            _view.getHistoryManager().addEdit(_transitionView.clearRateParameter());
		    	         }
		    	         try{
		    	        	 if(singleServerRadioButton.isSelected()){
		    	        		// Double newRate = Double.parseDouble(rateTextField.getText());
				    	            if (!(r==rate)) {
				    	               _view.getHistoryManager().addEdit(_transitionView.setRate(rateTextField.getText()));
				    	 			//  _transitionView.setRateType("C");
				    	            } 
		    	        	 }
		    	         } catch (NumberFormatException nfe){
		    	            rateTextField.setBackground(new Color(255,0,0));
		    	            rateTextField.addCaretListener(caretListener);
		    	            return;
		    	         } catch (Exception e){
		    	            System.out.println(":" + e);
		    	         }    		  
		    	 // }
		      } 
	//  }
      
     

      if (attributesVisible != attributesCheckBox.isSelected()){
         _transitionView.toggleAttributesVisible();
      }      
            
      Integer rotationIndex = rotationComboBox.getSelectedIndex();
      if (rotationIndex > 0) {
         int angle = 0;
         switch (rotationIndex) {
            case 1:
               angle = 45;
               break;
            case 2:
               angle = 90;
               break;
            case 3:
               angle = 135; //-45
               break;
            default:
               break;               
         }
         if (angle != 0) {
            _view.getHistoryManager().addEdit(_transitionView.rotate(angle));
         }
      }

      //按ok button，将逻辑公式写入到LogicTransitionView的属性中
      if(_transitionView instanceof LogicalTransitionView){
          String logicText = logicExpressionText.getText();
          //todo-Dj:公式正确性的检验
          ((LogicalTransitionView) _transitionView).setFormula(logicText);
      }
      
      _transitionView.repaint();
      
      exit();
   }//GEN-LAST:event_okButtonHandler

   private void exit() {
      rootPane.getParent().setVisible(false);
   }
   
   
   private void cancelButtonHandler(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonHandler
      //Provisional!
      exit();
   }//GEN-LAST:event_cancelButtonHandler
      
   
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox attributesCheckBox;
    private javax.swing.JRadioButton immediateRadioButton;
    private javax.swing.JRadioButton infiniteServerRadioButton;
    private javax.swing.JTextField nameTextField;
    private javax.swing.JButton okButton;
    private javax.swing.JLabel priorityLabel;
    private javax.swing.JPanel priorityPanel;
    private javax.swing.JSlider prioritySlider;
    private javax.swing.JTextField priorityTextField;
    private javax.swing.JComboBox rateComboBox;
    private javax.swing.JLabel rateLabel;
    private javax.swing.JButton functionalratebutton;
   // private javax.swing.JLabel functionalratelabel;
    private javax.swing.JTextField rateTextField;
    private javax.swing.JComboBox rotationComboBox;
    private javax.swing.ButtonGroup semanticsButtonGroup;
    private javax.swing.JLabel serverLabel;
    private javax.swing.JPanel serverPanel;
    private javax.swing.JRadioButton singleServerRadioButton;
    private javax.swing.JRadioButton timedRadioButton;
    private JPanel logicPanel;
    private JTextArea logicExpressionText;
   // private JCheckBox constantRateCheckbox;
    //private JCheckBox functionalRateCheckbox;
    // End of variables declaration//GEN-END:variables


	public void setRate(String func) {
		this.rateTextField.setText(func);
		
	}
   
}
