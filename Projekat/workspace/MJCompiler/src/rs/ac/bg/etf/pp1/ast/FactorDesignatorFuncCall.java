// generated with ast extension for cup
// version 0.8
// 24/0/2022 18:24:23


package rs.ac.bg.etf.pp1.ast;

public class FactorDesignatorFuncCall extends Factor {

    private FuncCallStart FuncCallStart;
    private OptActPars OptActPars;

    public FactorDesignatorFuncCall (FuncCallStart FuncCallStart, OptActPars OptActPars) {
        this.FuncCallStart=FuncCallStart;
        if(FuncCallStart!=null) FuncCallStart.setParent(this);
        this.OptActPars=OptActPars;
        if(OptActPars!=null) OptActPars.setParent(this);
    }

    public FuncCallStart getFuncCallStart() {
        return FuncCallStart;
    }

    public void setFuncCallStart(FuncCallStart FuncCallStart) {
        this.FuncCallStart=FuncCallStart;
    }

    public OptActPars getOptActPars() {
        return OptActPars;
    }

    public void setOptActPars(OptActPars OptActPars) {
        this.OptActPars=OptActPars;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(FuncCallStart!=null) FuncCallStart.accept(visitor);
        if(OptActPars!=null) OptActPars.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(FuncCallStart!=null) FuncCallStart.traverseTopDown(visitor);
        if(OptActPars!=null) OptActPars.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(FuncCallStart!=null) FuncCallStart.traverseBottomUp(visitor);
        if(OptActPars!=null) OptActPars.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("FactorDesignatorFuncCall(\n");

        if(FuncCallStart!=null)
            buffer.append(FuncCallStart.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(OptActPars!=null)
            buffer.append(OptActPars.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [FactorDesignatorFuncCall]");
        return buffer.toString();
    }
}
