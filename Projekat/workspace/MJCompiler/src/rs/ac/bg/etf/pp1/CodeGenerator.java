package rs.ac.bg.etf.pp1;

import rs.ac.bg.etf.pp1.ast.*;
import rs.etf.pp1.mj.runtime.Code;
import rs.etf.pp1.symboltable.Tab;
import rs.etf.pp1.symboltable.concepts.Obj;

public class CodeGenerator extends VisitorAdaptor {
	
	private final static int BYTE_LENGTH = 1;
	private final static int WORD_LENGTH = 4;
	
	private final static int INT_PRINT_WIDTH = 5;
	private final static int BYTE_PRINT_WIDTH = 1;
	
	private int mainPC;
	
	public int getMainPC() {
		return mainPC;
	}
	
	//====================================================================================
	//  			Helpers
	//====================================================================================
	
	//====================================================================================
	//  			Visit Methods
	//====================================================================================
	
	//--------------CONST---------------------------------------------------------------
	
	public void visit(NumConst cnst) {
		Code.loadConst(cnst.getVal());
	}
	
	public void visit(CharConst cnst) {
		Code.loadConst((int)cnst.getVal().charValue());
	}
	
	public void visit(BoolConst cnst) {
		Code.loadConst(cnst.getVal() ? 1 : 0);
	}
	
	//--------------DESIGNATOR-----------------------------------------------------------
	
	public void visit(Designator designator) {
		SyntaxNode parent = designator.getParent();
		
		if (parent.getClass() == FactorDesignator.class) {		// Part of expression
			Code.load(designator.obj);
		}
	}
	
	//--------------METHOD---------------------------------------------------------------
	
	public void visit(MethodIdent methodIdent) {
		// If it is a main function, save the PC
		if (methodIdent.obj.getName().equals("main")) {
			this.mainPC = Code.pc;
		}
		
		// Set missing address field
		methodIdent.obj.setAdr(Code.pc);
		
		// Find parameters and locals count
		int paramCnt = methodIdent.obj.getLevel();
		int paramAndLocalCnt = methodIdent.obj.getLocalSymbols().size();
		
		// Generate instruction
		Code.put(Code.enter);
		Code.put(paramCnt);
		Code.put(paramAndLocalCnt);
	}
	
	public void visit(MethodDecl methodDecl) {
		// Generate instruction
		Code.put(Code.exit);
		Code.put(Code.return_);
	}
	
	//--------------STATEMENT-----------------------------------------------------------
	
	public void visit(PrintStmt stmt) {
		// Generate instruction
		if (stmt.getPrintPars().struct == Tab.charType) {	// Print char
			Code.loadConst(BYTE_PRINT_WIDTH);
			Code.put(Code.bprint);
		} else {											// Print integer/boolean
			Code.loadConst(INT_PRINT_WIDTH);
			Code.put(Code.print);
		}
	}
	
	//--------------DESIGNATOR STATEMENT------------------------------------------------
	
	public void visit(DesignatorStmtAssign stmt) {
		Code.store(stmt.getDesignator().obj);
	}
	
	//--------------EXPRESSION----------------------------------------------------------
	
	//--------------TERM----------------------------------------------------------------
	
	//--------------FACTOR--------------------------------------------------------------
	
	public void visit(FactorWithNew factor) {
		// Generate instruction
		Code.put(Code.new_);
		Code.put2(WORD_LENGTH * factor.getType().struct.getNumberOfFields());
	}
	
	public void visit(FactorWithNewArray factor) {
		int b = factor.getType().struct == TabExtended.charType ? 0 : 1;
		
		// Generate instruction
		Code.put(Code.newarray);
		Code.put(b);
	}
}
