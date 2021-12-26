// generated with ast extension for cup
// version 0.8
// 25/11/2021 12:58:50


package rs.ac.bg.etf.pp1.ast;

public class RecordDeclaration extends RecordDecl {

    private String I1;
    private VarListDeclList VarListDeclList;

    public RecordDeclaration (String I1, VarListDeclList VarListDeclList) {
        this.I1=I1;
        this.VarListDeclList=VarListDeclList;
        if(VarListDeclList!=null) VarListDeclList.setParent(this);
    }

    public String getI1() {
        return I1;
    }

    public void setI1(String I1) {
        this.I1=I1;
    }

    public VarListDeclList getVarListDeclList() {
        return VarListDeclList;
    }

    public void setVarListDeclList(VarListDeclList VarListDeclList) {
        this.VarListDeclList=VarListDeclList;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(VarListDeclList!=null) VarListDeclList.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(VarListDeclList!=null) VarListDeclList.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(VarListDeclList!=null) VarListDeclList.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("RecordDeclaration(\n");

        buffer.append(" "+tab+I1);
        buffer.append("\n");

        if(VarListDeclList!=null)
            buffer.append(VarListDeclList.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [RecordDeclaration]");
        return buffer.toString();
    }
}
