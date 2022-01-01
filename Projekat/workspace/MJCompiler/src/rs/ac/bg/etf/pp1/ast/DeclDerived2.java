// generated with ast extension for cup
// version 0.8
// 1/0/2022 15:55:22


package rs.ac.bg.etf.pp1.ast;

public class DeclDerived2 extends Decl {

    public DeclDerived2 () {
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
        buffer.append("DeclDerived2(\n");

        buffer.append(tab);
        buffer.append(") [DeclDerived2]");
        return buffer.toString();
    }
}
