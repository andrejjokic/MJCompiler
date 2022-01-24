// generated with ast extension for cup
// version 0.8
// 24/0/2022 11:34:15


package rs.ac.bg.etf.pp1.ast;

public class DoWhileStmt extends SingleStatement {

    private DoStatementStart DoStatementStart;
    private Statement Statement;
    private WhileConditionStart WhileConditionStart;
    private Condition Condition;

    public DoWhileStmt (DoStatementStart DoStatementStart, Statement Statement, WhileConditionStart WhileConditionStart, Condition Condition) {
        this.DoStatementStart=DoStatementStart;
        if(DoStatementStart!=null) DoStatementStart.setParent(this);
        this.Statement=Statement;
        if(Statement!=null) Statement.setParent(this);
        this.WhileConditionStart=WhileConditionStart;
        if(WhileConditionStart!=null) WhileConditionStart.setParent(this);
        this.Condition=Condition;
        if(Condition!=null) Condition.setParent(this);
    }

    public DoStatementStart getDoStatementStart() {
        return DoStatementStart;
    }

    public void setDoStatementStart(DoStatementStart DoStatementStart) {
        this.DoStatementStart=DoStatementStart;
    }

    public Statement getStatement() {
        return Statement;
    }

    public void setStatement(Statement Statement) {
        this.Statement=Statement;
    }

    public WhileConditionStart getWhileConditionStart() {
        return WhileConditionStart;
    }

    public void setWhileConditionStart(WhileConditionStart WhileConditionStart) {
        this.WhileConditionStart=WhileConditionStart;
    }

    public Condition getCondition() {
        return Condition;
    }

    public void setCondition(Condition Condition) {
        this.Condition=Condition;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(DoStatementStart!=null) DoStatementStart.accept(visitor);
        if(Statement!=null) Statement.accept(visitor);
        if(WhileConditionStart!=null) WhileConditionStart.accept(visitor);
        if(Condition!=null) Condition.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(DoStatementStart!=null) DoStatementStart.traverseTopDown(visitor);
        if(Statement!=null) Statement.traverseTopDown(visitor);
        if(WhileConditionStart!=null) WhileConditionStart.traverseTopDown(visitor);
        if(Condition!=null) Condition.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(DoStatementStart!=null) DoStatementStart.traverseBottomUp(visitor);
        if(Statement!=null) Statement.traverseBottomUp(visitor);
        if(WhileConditionStart!=null) WhileConditionStart.traverseBottomUp(visitor);
        if(Condition!=null) Condition.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("DoWhileStmt(\n");

        if(DoStatementStart!=null)
            buffer.append(DoStatementStart.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(Statement!=null)
            buffer.append(Statement.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(WhileConditionStart!=null)
            buffer.append(WhileConditionStart.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(Condition!=null)
            buffer.append(Condition.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [DoWhileStmt]");
        return buffer.toString();
    }
}
