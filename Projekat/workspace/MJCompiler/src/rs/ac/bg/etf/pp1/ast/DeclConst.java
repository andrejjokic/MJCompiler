// generated with ast extension for cup
// version 0.8
// 12/0/2022 16:30:28


package rs.ac.bg.etf.pp1.ast;

public class DeclConst extends Decl {

    private ConstListDecls ConstListDecls;

    public DeclConst (ConstListDecls ConstListDecls) {
        this.ConstListDecls=ConstListDecls;
        if(ConstListDecls!=null) ConstListDecls.setParent(this);
    }

    public ConstListDecls getConstListDecls() {
        return ConstListDecls;
    }

    public void setConstListDecls(ConstListDecls ConstListDecls) {
        this.ConstListDecls=ConstListDecls;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(ConstListDecls!=null) ConstListDecls.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(ConstListDecls!=null) ConstListDecls.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(ConstListDecls!=null) ConstListDecls.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("DeclConst(\n");

        if(ConstListDecls!=null)
            buffer.append(ConstListDecls.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [DeclConst]");
        return buffer.toString();
    }
}
