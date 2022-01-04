// generated with ast extension for cup
// version 0.8
// 4/0/2022 10:43:28


package rs.ac.bg.etf.pp1.ast;

public class ClassDeclaration extends ClassDecl {

    private ClassIdent ClassIdent;
    private VarListDeclList VarListDeclList;
    private ClassMethodDecls ClassMethodDecls;

    public ClassDeclaration (ClassIdent ClassIdent, VarListDeclList VarListDeclList, ClassMethodDecls ClassMethodDecls) {
        this.ClassIdent=ClassIdent;
        if(ClassIdent!=null) ClassIdent.setParent(this);
        this.VarListDeclList=VarListDeclList;
        if(VarListDeclList!=null) VarListDeclList.setParent(this);
        this.ClassMethodDecls=ClassMethodDecls;
        if(ClassMethodDecls!=null) ClassMethodDecls.setParent(this);
    }

    public ClassIdent getClassIdent() {
        return ClassIdent;
    }

    public void setClassIdent(ClassIdent ClassIdent) {
        this.ClassIdent=ClassIdent;
    }

    public VarListDeclList getVarListDeclList() {
        return VarListDeclList;
    }

    public void setVarListDeclList(VarListDeclList VarListDeclList) {
        this.VarListDeclList=VarListDeclList;
    }

    public ClassMethodDecls getClassMethodDecls() {
        return ClassMethodDecls;
    }

    public void setClassMethodDecls(ClassMethodDecls ClassMethodDecls) {
        this.ClassMethodDecls=ClassMethodDecls;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(ClassIdent!=null) ClassIdent.accept(visitor);
        if(VarListDeclList!=null) VarListDeclList.accept(visitor);
        if(ClassMethodDecls!=null) ClassMethodDecls.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(ClassIdent!=null) ClassIdent.traverseTopDown(visitor);
        if(VarListDeclList!=null) VarListDeclList.traverseTopDown(visitor);
        if(ClassMethodDecls!=null) ClassMethodDecls.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(ClassIdent!=null) ClassIdent.traverseBottomUp(visitor);
        if(VarListDeclList!=null) VarListDeclList.traverseBottomUp(visitor);
        if(ClassMethodDecls!=null) ClassMethodDecls.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("ClassDeclaration(\n");

        if(ClassIdent!=null)
            buffer.append(ClassIdent.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(VarListDeclList!=null)
            buffer.append(VarListDeclList.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(ClassMethodDecls!=null)
            buffer.append(ClassMethodDecls.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [ClassDeclaration]");
        return buffer.toString();
    }
}
