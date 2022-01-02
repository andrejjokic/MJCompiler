// generated with ast extension for cup
// version 0.8
// 2/0/2022 14:34:13


package rs.ac.bg.etf.pp1.ast;

public interface Visitor { 

    public void visit(ReturnType ReturnType);
    public void visit(VarListDecls VarListDecls);
    public void visit(Mulop Mulop);
    public void visit(Indexing Indexing);
    public void visit(Relop Relop);
    public void visit(StatementList StatementList);
    public void visit(Addop Addop);
    public void visit(ExtendsClause ExtendsClause);
    public void visit(RecordDecl RecordDecl);
    public void visit(Factor Factor);
    public void visit(CondTerm CondTerm);
    public void visit(DeclList DeclList);
    public void visit(Term Term);
    public void visit(Condition Condition);
    public void visit(ConstDeclList ConstDeclList);
    public void visit(PrintPars PrintPars);
    public void visit(VarDeclList VarDeclList);
    public void visit(Expr Expr);
    public void visit(IndexingList IndexingList);
    public void visit(ActPars ActPars);
    public void visit(ClassMethodDecls ClassMethodDecls);
    public void visit(DesignatorStatement DesignatorStatement);
    public void visit(Const Const);
    public void visit(VarListDeclList VarListDeclList);
    public void visit(FormParamList FormParamList);
    public void visit(Decl Decl);
    public void visit(Statement Statement);
    public void visit(VarDecl VarDecl);
    public void visit(ClassDecl ClassDecl);
    public void visit(CondFact CondFact);
    public void visit(OptActPars OptActPars);
    public void visit(MethodDeclList MethodDeclList);
    public void visit(SingleStatement SingleStatement);
    public void visit(ConstListDecls ConstListDecls);
    public void visit(FormPars FormPars);
    public void visit(FormParam FormParam);
    public void visit(DoubleCondFact DoubleCondFact);
    public void visit(SingleCondFact SingleCondFact);
    public void visit(SingleCondTerm SingleCondTerm);
    public void visit(MultipleCondTerms MultipleCondTerms);
    public void visit(ConditionDerived1 ConditionDerived1);
    public void visit(SingleCondition SingleCondition);
    public void visit(MultipleConditions MultipleConditions);
    public void visit(DesignatorStmtDec DesignatorStmtDec);
    public void visit(DesignatorStmtInc DesignatorStmtInc);
    public void visit(DesignatorStmtFuncCall DesignatorStmtFuncCall);
    public void visit(DesignatorStatementDerived1 DesignatorStatementDerived1);
    public void visit(DesignatorStmtAssign DesignatorStmtAssign);
    public void visit(PrintParametersWithConst PrintParametersWithConst);
    public void visit(PrintParameters PrintParameters);
    public void visit(LessOrEqualThen LessOrEqualThen);
    public void visit(LessThen LessThen);
    public void visit(GreaterOrEqualThen GreaterOrEqualThen);
    public void visit(GreaterThen GreaterThen);
    public void visit(NotEqualTo NotEqualTo);
    public void visit(EqualTo EqualTo);
    public void visit(Assignop Assignop);
    public void visit(Minus Minus);
    public void visit(Add Add);
    public void visit(Percent Percent);
    public void visit(Div Div);
    public void visit(Mul Mul);
    public void visit(SingleActPars SingleActPars);
    public void visit(MultipleActPars MultipleActPars);
    public void visit(NoOptActPars NoOptActPars);
    public void visit(WithOptActPars WithOptActPars);
    public void visit(IndexingArray IndexingArray);
    public void visit(IndexingField IndexingField);
    public void visit(NoIndexing NoIndexing);
    public void visit(MultipleIndexing MultipleIndexing);
    public void visit(Designator Designator);
    public void visit(FactorDesignatorFuncCall FactorDesignatorFuncCall);
    public void visit(FactorDesignator FactorDesignator);
    public void visit(FactorWithNewArray FactorWithNewArray);
    public void visit(FactorWithNew FactorWithNew);
    public void visit(FactorWithParen FactorWithParen);
    public void visit(FactorWithConst FactorWithConst);
    public void visit(SingleTerm SingleTerm);
    public void visit(MultipleTerm MultipleTerm);
    public void visit(SingleExpr SingleExpr);
    public void visit(SingleExprWithMinus SingleExprWithMinus);
    public void visit(MultipleExpr MultipleExpr);
    public void visit(DoStatementStart DoStatementStart);
    public void visit(PrintStmt PrintStmt);
    public void visit(ReadStmt ReadStmt);
    public void visit(ReturnExprStmt ReturnExprStmt);
    public void visit(ReturnNoExprStmt ReturnNoExprStmt);
    public void visit(GotoStmt GotoStmt);
    public void visit(ContinueStmt ContinueStmt);
    public void visit(BreakStmt BreakStmt);
    public void visit(DesignatorStmt DesignatorStmt);
    public void visit(DoWhileStmt DoWhileStmt);
    public void visit(IfElseStmt IfElseStmt);
    public void visit(IfStmt IfStmt);
    public void visit(Statements Statements);
    public void visit(MultipleStmts MultipleStmts);
    public void visit(SingleStmtNoLabel SingleStmtNoLabel);
    public void visit(SingleStmtWithLabel SingleStmtWithLabel);
    public void visit(NoStmt NoStmt);
    public void visit(Stmts Stmts);
    public void visit(FormParamDerived1 FormParamDerived1);
    public void visit(FormalParameterNoBrackets FormalParameterNoBrackets);
    public void visit(FormalParameterBrackets FormalParameterBrackets);
    public void visit(SingleFormParam SingleFormParam);
    public void visit(MultipleFormParams MultipleFormParams);
    public void visit(NoFormParams NoFormParams);
    public void visit(FormParams FormParams);
    public void visit(RetVoid RetVoid);
    public void visit(RetType RetType);
    public void visit(MethodIdent MethodIdent);
    public void visit(MethodDecl MethodDecl);
    public void visit(NoMethodDeclarations NoMethodDeclarations);
    public void visit(MethodDeclarations MethodDeclarations);
    public void visit(NoClassMethodDeclarations NoClassMethodDeclarations);
    public void visit(ClassMethodDeclarations ClassMethodDeclarations);
    public void visit(RecordName RecordName);
    public void visit(RecordDeclaration RecordDeclaration);
    public void visit(NoVarListDeclarationList NoVarListDeclarationList);
    public void visit(VarListDeclarationList VarListDeclarationList);
    public void visit(NoExtends NoExtends);
    public void visit(Extends Extends);
    public void visit(ClassIdent ClassIdent);
    public void visit(ClassDeclaration ClassDeclaration);
    public void visit(VarDeclNoBrackets VarDeclNoBrackets);
    public void visit(VarDeclBrackets VarDeclBrackets);
    public void visit(SingleVarDecl SingleVarDecl);
    public void visit(MultipleVarDecls MultipleVarDecls);
    public void visit(VarListDeclsType VarListDeclsType);
    public void visit(VarListDeclarations VarListDeclarations);
    public void visit(Label Label);
    public void visit(Type Type);
    public void visit(BoolConst BoolConst);
    public void visit(CharConst CharConst);
    public void visit(NumConst NumConst);
    public void visit(ConstDecl ConstDecl);
    public void visit(SingleConstDecl SingleConstDecl);
    public void visit(MultipleConstDecls MultipleConstDecls);
    public void visit(ConstDeclType ConstDeclType);
    public void visit(ConstListDeclarations ConstListDeclarations);
    public void visit(DeclDerived2 DeclDerived2);
    public void visit(DeclDerived1 DeclDerived1);
    public void visit(DeclRecord DeclRecord);
    public void visit(DeclClass DeclClass);
    public void visit(DeclVar DeclVar);
    public void visit(DeclConst DeclConst);
    public void visit(NoDeclarations NoDeclarations);
    public void visit(Declarations Declarations);
    public void visit(ProgName ProgName);
    public void visit(Program Program);

}
