// generated with ast extension for cup
// version 0.8
// 15/0/2022 15:16:17


package rs.ac.bg.etf.pp1.ast;

public class NoIndexing extends IndexingList {

    public NoIndexing () {
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
        buffer.append("NoIndexing(\n");

        buffer.append(tab);
        buffer.append(") [NoIndexing]");
        return buffer.toString();
    }
}
