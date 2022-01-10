// generated with ast extension for cup
// version 0.8
// 10/0/2022 19:29:38


package rs.ac.bg.etf.pp1.ast;

public class VarListDeclarationList extends VarListDeclList {

    private VarListDeclList VarListDeclList;
    private VarListDecls VarListDecls;

    public VarListDeclarationList (VarListDeclList VarListDeclList, VarListDecls VarListDecls) {
        this.VarListDeclList=VarListDeclList;
        if(VarListDeclList!=null) VarListDeclList.setParent(this);
        this.VarListDecls=VarListDecls;
        if(VarListDecls!=null) VarListDecls.setParent(this);
    }

    public VarListDeclList getVarListDeclList() {
        return VarListDeclList;
    }

    public void setVarListDeclList(VarListDeclList VarListDeclList) {
        this.VarListDeclList=VarListDeclList;
    }

    public VarListDecls getVarListDecls() {
        return VarListDecls;
    }

    public void setVarListDecls(VarListDecls VarListDecls) {
        this.VarListDecls=VarListDecls;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(VarListDeclList!=null) VarListDeclList.accept(visitor);
        if(VarListDecls!=null) VarListDecls.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(VarListDeclList!=null) VarListDeclList.traverseTopDown(visitor);
        if(VarListDecls!=null) VarListDecls.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(VarListDeclList!=null) VarListDeclList.traverseBottomUp(visitor);
        if(VarListDecls!=null) VarListDecls.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("VarListDeclarationList(\n");

        if(VarListDeclList!=null)
            buffer.append(VarListDeclList.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(VarListDecls!=null)
            buffer.append(VarListDecls.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [VarListDeclarationList]");
        return buffer.toString();
    }
}
