package pipe.models;


public class LogicalTransition extends Transition{
    private String _formula;//约束逻辑

    public LogicalTransition (String id, String name, String formula){
        super(id,name);
        this._formula = formula;
    }

    public LogicalTransition (String id, String name, String formula, String rateExpr, int priority){
        super(id, name, rateExpr, priority);
        this._formula = formula;
    }

    public String getFormula(){
        return _formula;
    }

    public void setFormula(String _formula) {
        this._formula = _formula;
    }

}
