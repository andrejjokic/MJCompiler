// generated with ast extension for cup
// version 0.8
// 25/11/2021 12:58:50


package rs.ac.bg.etf.pp1.ast;

public class ClassDeclaration extends ClassDecl {

    private String className;
    private ExtendsClause ExtendsClause;
    private VarListDeclList VarListDeclList;
    private ClassMethodDecls ClassMethodDecls;

    public ClassDeclaration (String className, ExtendsClause ExtendsClause, VarListDeclList VarListDeclList, ClassMethodDecls ClassMethodDecls) {
        this.className=className;
        this.ExtendsClause=ExtendsClause;
        if(ExtendsClause!=null) ExtendsClause.setParent(this);
        this.VarListDeclList=VarListDeclList;
        if(VarListDeclList!=null) VarListDeclList.setParent(this);
        this.ClassMethodDecls=ClassMethodDecls;
        if(ClassMethodDecls!=null) ClassMethodDecls.setParent(this);
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className=className;
    }

    public ExtendsClause getExtendsClause() {
        return ExtendsClause;
    }

    public void setExtendsClause(ExtendsClause ExtendsClause) {
        this.ExtendsClause=ExtendsClause;
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
        if(ExtendsClause!=null) ExtendsClause.accept(visitor);
        if(VarListDeclList!=null) VarListDeclList.accept(visitor);
        if(ClassMethodDecls!=null) ClassMethodDecls.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(ExtendsClause!=null) ExtendsClause.traverseTopDown(visitor);
        if(VarListDeclList!=null) VarListDeclList.traverseTopDown(visitor);
        if(ClassMethodDecls!=null) ClassMethodDecls.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(ExtendsClause!=null) ExtendsClause.traverseBottomUp(visitor);
        if(VarListDeclList!=null) VarListDeclList.traverseBottomUp(visitor);
        if(ClassMethodDecls!=null) ClassMethodDecls.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("ClassDeclaration(\n");

        buffer.append(" "+tab+className);
        buffer.append("\n");

        if(ExtendsClause!=null)
            buffer.append(ExtendsClause.toString("  "+tab));
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
