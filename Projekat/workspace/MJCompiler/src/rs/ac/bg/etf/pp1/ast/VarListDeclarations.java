// generated with ast extension for cup
// version 0.8
// 30/11/2021 12:41:4


package rs.ac.bg.etf.pp1.ast;

public class VarListDeclarations extends VarListDecls {

    private VarListDeclsType VarListDeclsType;
    private VarDeclList VarDeclList;

    public VarListDeclarations (VarListDeclsType VarListDeclsType, VarDeclList VarDeclList) {
        this.VarListDeclsType=VarListDeclsType;
        if(VarListDeclsType!=null) VarListDeclsType.setParent(this);
        this.VarDeclList=VarDeclList;
        if(VarDeclList!=null) VarDeclList.setParent(this);
    }

    public VarListDeclsType getVarListDeclsType() {
        return VarListDeclsType;
    }

    public void setVarListDeclsType(VarListDeclsType VarListDeclsType) {
        this.VarListDeclsType=VarListDeclsType;
    }

    public VarDeclList getVarDeclList() {
        return VarDeclList;
    }

    public void setVarDeclList(VarDeclList VarDeclList) {
        this.VarDeclList=VarDeclList;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(VarListDeclsType!=null) VarListDeclsType.accept(visitor);
        if(VarDeclList!=null) VarDeclList.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(VarListDeclsType!=null) VarListDeclsType.traverseTopDown(visitor);
        if(VarDeclList!=null) VarDeclList.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(VarListDeclsType!=null) VarListDeclsType.traverseBottomUp(visitor);
        if(VarDeclList!=null) VarDeclList.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("VarListDeclarations(\n");

        if(VarListDeclsType!=null)
            buffer.append(VarListDeclsType.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(VarDeclList!=null)
            buffer.append(VarDeclList.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [VarListDeclarations]");
        return buffer.toString();
    }
}
