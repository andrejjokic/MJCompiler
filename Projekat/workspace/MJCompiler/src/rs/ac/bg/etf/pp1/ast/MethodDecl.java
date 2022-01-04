// generated with ast extension for cup
// version 0.8
// 4/0/2022 10:43:28


package rs.ac.bg.etf.pp1.ast;

public class MethodDecl implements SyntaxNode {

    private SyntaxNode parent;
    private int line;
    private MethodIdent MethodIdent;
    private FormPars FormPars;
    private VarListDeclList VarListDeclList;
    private StatementList StatementList;

    public MethodDecl (MethodIdent MethodIdent, FormPars FormPars, VarListDeclList VarListDeclList, StatementList StatementList) {
        this.MethodIdent=MethodIdent;
        if(MethodIdent!=null) MethodIdent.setParent(this);
        this.FormPars=FormPars;
        if(FormPars!=null) FormPars.setParent(this);
        this.VarListDeclList=VarListDeclList;
        if(VarListDeclList!=null) VarListDeclList.setParent(this);
        this.StatementList=StatementList;
        if(StatementList!=null) StatementList.setParent(this);
    }

    public MethodIdent getMethodIdent() {
        return MethodIdent;
    }

    public void setMethodIdent(MethodIdent MethodIdent) {
        this.MethodIdent=MethodIdent;
    }

    public FormPars getFormPars() {
        return FormPars;
    }

    public void setFormPars(FormPars FormPars) {
        this.FormPars=FormPars;
    }

    public VarListDeclList getVarListDeclList() {
        return VarListDeclList;
    }

    public void setVarListDeclList(VarListDeclList VarListDeclList) {
        this.VarListDeclList=VarListDeclList;
    }

    public StatementList getStatementList() {
        return StatementList;
    }

    public void setStatementList(StatementList StatementList) {
        this.StatementList=StatementList;
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
        if(MethodIdent!=null) MethodIdent.accept(visitor);
        if(FormPars!=null) FormPars.accept(visitor);
        if(VarListDeclList!=null) VarListDeclList.accept(visitor);
        if(StatementList!=null) StatementList.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(MethodIdent!=null) MethodIdent.traverseTopDown(visitor);
        if(FormPars!=null) FormPars.traverseTopDown(visitor);
        if(VarListDeclList!=null) VarListDeclList.traverseTopDown(visitor);
        if(StatementList!=null) StatementList.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(MethodIdent!=null) MethodIdent.traverseBottomUp(visitor);
        if(FormPars!=null) FormPars.traverseBottomUp(visitor);
        if(VarListDeclList!=null) VarListDeclList.traverseBottomUp(visitor);
        if(StatementList!=null) StatementList.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("MethodDecl(\n");

        if(MethodIdent!=null)
            buffer.append(MethodIdent.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(FormPars!=null)
            buffer.append(FormPars.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(VarListDeclList!=null)
            buffer.append(VarListDeclList.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(StatementList!=null)
            buffer.append(StatementList.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [MethodDecl]");
        return buffer.toString();
    }
}
