// generated with ast extension for cup
// version 0.8
// 22/0/2022 15:1:59


package rs.ac.bg.etf.pp1.ast;

public class IfElseStmt extends SingleStatement {

    private IfStmtStart IfStmtStart;
    private ThenStmtStart ThenStmtStart;
    private Statement Statement;
    private ElseStmtStart ElseStmtStart;
    private Statement Statement1;

    public IfElseStmt (IfStmtStart IfStmtStart, ThenStmtStart ThenStmtStart, Statement Statement, ElseStmtStart ElseStmtStart, Statement Statement1) {
        this.IfStmtStart=IfStmtStart;
        if(IfStmtStart!=null) IfStmtStart.setParent(this);
        this.ThenStmtStart=ThenStmtStart;
        if(ThenStmtStart!=null) ThenStmtStart.setParent(this);
        this.Statement=Statement;
        if(Statement!=null) Statement.setParent(this);
        this.ElseStmtStart=ElseStmtStart;
        if(ElseStmtStart!=null) ElseStmtStart.setParent(this);
        this.Statement1=Statement1;
        if(Statement1!=null) Statement1.setParent(this);
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

    public ElseStmtStart getElseStmtStart() {
        return ElseStmtStart;
    }

    public void setElseStmtStart(ElseStmtStart ElseStmtStart) {
        this.ElseStmtStart=ElseStmtStart;
    }

    public Statement getStatement1() {
        return Statement1;
    }

    public void setStatement1(Statement Statement1) {
        this.Statement1=Statement1;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(IfStmtStart!=null) IfStmtStart.accept(visitor);
        if(ThenStmtStart!=null) ThenStmtStart.accept(visitor);
        if(Statement!=null) Statement.accept(visitor);
        if(ElseStmtStart!=null) ElseStmtStart.accept(visitor);
        if(Statement1!=null) Statement1.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(IfStmtStart!=null) IfStmtStart.traverseTopDown(visitor);
        if(ThenStmtStart!=null) ThenStmtStart.traverseTopDown(visitor);
        if(Statement!=null) Statement.traverseTopDown(visitor);
        if(ElseStmtStart!=null) ElseStmtStart.traverseTopDown(visitor);
        if(Statement1!=null) Statement1.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(IfStmtStart!=null) IfStmtStart.traverseBottomUp(visitor);
        if(ThenStmtStart!=null) ThenStmtStart.traverseBottomUp(visitor);
        if(Statement!=null) Statement.traverseBottomUp(visitor);
        if(ElseStmtStart!=null) ElseStmtStart.traverseBottomUp(visitor);
        if(Statement1!=null) Statement1.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("IfElseStmt(\n");

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

        if(ElseStmtStart!=null)
            buffer.append(ElseStmtStart.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(Statement1!=null)
            buffer.append(Statement1.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [IfElseStmt]");
        return buffer.toString();
    }
}
