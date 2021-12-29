// generated with ast extension for cup
// version 0.8
// 29/11/2021 13:53:28


package rs.ac.bg.etf.pp1.ast;

public class FormParamDerived1 extends FormParam {

    public FormParamDerived1 () {
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
        buffer.append("FormParamDerived1(\n");

        buffer.append(tab);
        buffer.append(") [FormParamDerived1]");
        return buffer.toString();
    }
}
