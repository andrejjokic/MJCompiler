// generated with ast extension for cup
// version 0.8
// 2/0/2022 17:17:31


package rs.ac.bg.etf.pp1.ast;

public class NoVarListDeclarationList extends VarListDeclList {

    public NoVarListDeclarationList () {
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
        buffer.append("NoVarListDeclarationList(\n");

        buffer.append(tab);
        buffer.append(") [NoVarListDeclarationList]");
        return buffer.toString();
    }
}
