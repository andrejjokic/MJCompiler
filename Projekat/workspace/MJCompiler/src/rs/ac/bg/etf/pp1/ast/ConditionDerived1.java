// generated with ast extension for cup
// version 0.8
// 6/0/2022 17:18:28


package rs.ac.bg.etf.pp1.ast;

public class ConditionDerived1 extends Condition {

    public ConditionDerived1 () {
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("ConditionDerived1(\n");

        buffer.append(tab);
        buffer.append(") [ConditionDerived1]");
        return buffer.toString();
    }
}
