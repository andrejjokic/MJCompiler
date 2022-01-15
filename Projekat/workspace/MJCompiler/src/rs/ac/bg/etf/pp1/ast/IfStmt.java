// generated with ast extension for cup
// version 0.8
// 15/0/2022 13:4:17


package rs.ac.bg.etf.pp1.ast;

public class IfStmt extends SingleStatement {

    private IfStmtStart IfStmtStart;
    private Statement Statement;

    public IfStmt (IfStmtStart IfStmtStart, Statement Statement) {
        this.IfStmtStart=IfStmtStart;
        if(IfStmtStart!=null) IfStmtStart.setParent(this);
        this.Statement=Statement;
        if(Statement!=null) Statement.setParent(this);
    }

    public IfStmtStart getIfStmtStart() {
        return IfStmtStart;
    }

    public void setIfStmtStart(IfStmtStart IfStmtStart) {
        this.IfStmtStart=IfStmtStart;
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
        if(Statement!=null) Statement.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(IfStmtStart!=null) IfStmtStart.traverseTopDown(visitor);
        if(Statement!=null) Statement.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(IfStmtStart!=null) IfStmtStart.traverseBottomUp(visitor);
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
