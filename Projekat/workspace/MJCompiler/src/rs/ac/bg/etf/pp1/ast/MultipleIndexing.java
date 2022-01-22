// generated with ast extension for cup
// version 0.8
// 22/0/2022 15:1:59


package rs.ac.bg.etf.pp1.ast;

public class MultipleIndexing extends IndexingList {

    private IndexingList IndexingList;
    private Indexing Indexing;

    public MultipleIndexing (IndexingList IndexingList, Indexing Indexing) {
        this.IndexingList=IndexingList;
        if(IndexingList!=null) IndexingList.setParent(this);
        this.Indexing=Indexing;
        if(Indexing!=null) Indexing.setParent(this);
    }

    public IndexingList getIndexingList() {
        return IndexingList;
    }

    public void setIndexingList(IndexingList IndexingList) {
        this.IndexingList=IndexingList;
    }

    public Indexing getIndexing() {
        return Indexing;
    }

    public void setIndexing(Indexing Indexing) {
        this.Indexing=Indexing;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(IndexingList!=null) IndexingList.accept(visitor);
        if(Indexing!=null) Indexing.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(IndexingList!=null) IndexingList.traverseTopDown(visitor);
        if(Indexing!=null) Indexing.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(IndexingList!=null) IndexingList.traverseBottomUp(visitor);
        if(Indexing!=null) Indexing.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("MultipleIndexing(\n");

        if(IndexingList!=null)
            buffer.append(IndexingList.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(Indexing!=null)
            buffer.append(Indexing.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [MultipleIndexing]");
        return buffer.toString();
    }
}
