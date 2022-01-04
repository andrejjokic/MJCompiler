// generated with ast extension for cup
// version 0.8
// 4/0/2022 16:26:44


package rs.ac.bg.etf.pp1.ast;

public class DeclVar extends Decl {

    private VarListDecls VarListDecls;

    public DeclVar (VarListDecls VarListDecls) {
        this.VarListDecls=VarListDecls;
        if(VarListDecls!=null) VarListDecls.setParent(this);
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
        if(VarListDecls!=null) VarListDecls.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(VarListDecls!=null) VarListDecls.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(VarListDecls!=null) VarListDecls.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("DeclVar(\n");

        if(VarListDecls!=null)
            buffer.append(VarListDecls.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [DeclVar]");
        return buffer.toString();
    }
}
