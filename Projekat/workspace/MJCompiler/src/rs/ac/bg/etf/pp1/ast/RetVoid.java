// generated with ast extension for cup
// version 0.8
// 23/0/2022 17:49:21


package rs.ac.bg.etf.pp1.ast;

public class RetVoid extends ReturnType {

    public RetVoid () {
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
        buffer.append("RetVoid(\n");

        buffer.append(tab);
        buffer.append(") [RetVoid]");
        return buffer.toString();
    }
}
