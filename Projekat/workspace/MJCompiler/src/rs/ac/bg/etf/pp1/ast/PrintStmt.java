// generated with ast extension for cup
// version 0.8
// 30/11/2021 12:41:4


package rs.ac.bg.etf.pp1.ast;

public class PrintStmt extends SingleStatement {

    private PrintPars PrintPars;

    public PrintStmt (PrintPars PrintPars) {
        this.PrintPars=PrintPars;
        if(PrintPars!=null) PrintPars.setParent(this);
    }

    public PrintPars getPrintPars() {
        return PrintPars;
    }

    public void setPrintPars(PrintPars PrintPars) {
        this.PrintPars=PrintPars;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(PrintPars!=null) PrintPars.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(PrintPars!=null) PrintPars.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(PrintPars!=null) PrintPars.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("PrintStmt(\n");

        if(PrintPars!=null)
            buffer.append(PrintPars.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [PrintStmt]");
        return buffer.toString();
    }
}
