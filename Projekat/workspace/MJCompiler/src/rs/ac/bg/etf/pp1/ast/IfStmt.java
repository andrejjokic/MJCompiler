// generated with ast extension for cup
// version 0.8
// 15/0/2022 14:48:28


package rs.ac.bg.etf.pp1.ast;

public class IfStmt extends SingleStatement {

    private IfStmtStart IfStmtStart;
    private ThenStmtStart ThenStmtStart;
    private Statement Statement;

    public IfStmt (IfStmtStart IfStmtStart, ThenStmtStart ThenStmtStart, Statement Statement) {
        this.IfStmtStart=IfStmtStart;
        if(IfStmtStart!=null) IfStmtStart.setParent(this);
        this.ThenStmtStart=ThenStmtStart;
        if(ThenStmtStart!=null) ThenStmtStart.setParent(this);
        this.Statement=Statement;
        if(Statement!=null) Statement.setParent(this);
    }

    public IfStmtStart getIfStmtStart() {
        return IfStmtStart;
    }

    public void setIfStmtStart(IfStmtStart IfStmtStart) {
        this.IfStmtStart=IfStmtStart;
    }

    public ThenStmtStart getThenStmtStart() {
        return ThenStmtStart;
    }

    public void setThenStmtStart(ThenStmtStart ThenStmtStart) {
        this.ThenStmtStart=ThenStmtStart;
    }

    public Statement getStatement() {
        return Statement;
    }

    public void setStatement(Statement Statement) {
        this.Statement=Statement;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(IfStmtStart!=null) IfStmtStart.accept(visitor);
        if(ThenStmtStart!=null) ThenStmtStart.accept(visitor);
        if(Statement!=null) Statement.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(IfStmtStart!=null) IfStmtStart.traverseTopDown(visitor);
        if(ThenStmtStart!=null) ThenStmtStart.traverseTopDown(visitor);
        if(Statement!=null) Statement.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(IfStmtStart!=null) IfStmtStart.traverseBottomUp(visitor);
        if(ThenStmtStart!=null) ThenStmtStart.traverseBottomUp(visitor);
        if(Statement!=null) Statement.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("IfStmt(\n");

        if(IfStmtStart!=null)
            buffer.append(IfStmtStart.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(ThenStmtStart!=null)
            buffer.append(ThenStmtStart.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(Statement!=null)
            buffer.append(Statement.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [IfStmt]");
        return buffer.toString();
    }
}
