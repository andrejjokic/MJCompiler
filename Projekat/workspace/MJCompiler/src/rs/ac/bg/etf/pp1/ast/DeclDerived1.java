// generated with ast extension for cup
// version 0.8
// 2/0/2022 15:58:33


package rs.ac.bg.etf.pp1.ast;

public class DeclDerived1 extends Decl {

    public DeclDerived1 () {
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
        buffer.append("DeclDerived1(\n");

        buffer.append(tab);
        buffer.append(") [DeclDerived1]");
        return buffer.toString();
    }
}
