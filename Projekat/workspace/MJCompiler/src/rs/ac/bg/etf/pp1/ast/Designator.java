// generated with ast extension for cup
// version 0.8
// 1/0/2022 15:55:22


package rs.ac.bg.etf.pp1.ast;

public class Designator implements SyntaxNode {

    private SyntaxNode parent;
    private int line;
    private String I1;
    private IndexingList IndexingList;

    public Designator (String I1, IndexingList IndexingList) {
        this.I1=I1;
        this.IndexingList=IndexingList;
        if(IndexingList!=null) IndexingList.setParent(this);
    }

    public String getI1() {
        return I1;
    }

    public void setI1(String I1) {
        this.I1=I1;
    }

    public IndexingList getIndexingList() {
        return IndexingList;
    }

    public void setIndexingList(IndexingList IndexingList) {
        this.IndexingList=IndexingList;
    }

    public SyntaxNode getParent() {
        return parent;
    }

    public void setParent(SyntaxNode parent) {
        this.parent=parent;
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line=line;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(IndexingList!=null) IndexingList.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(IndexingList!=null) IndexingList.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(IndexingList!=null) IndexingList.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("Designator(\n");

        buffer.append(" "+tab+I1);
        buffer.append("\n");

        if(IndexingList!=null)
            buffer.append(IndexingList.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [Designator]");
        return buffer.toString();
    }
}
