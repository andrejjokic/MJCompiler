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
	
	private boolean printWidthSpecified = false;			// If the print statement has a NumConst part
	
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
	
	//--------------DESIGNATOR-----------------------------------------------------------
	
	public void visit(Designator designator) {
		SyntaxNode parent = designator.getParent();
		
		if (parent.getClass() == FactorDesignator.class) {		// Part of expression
			Code.load(designator.obj);
		}
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
	
	public void visit(FactorDesignatorFuncCall factor) {
		int offset = factor.getDesignator().obj.getAdr() - Code.pc;
		
		// Generate instruction
		Code.put(Code.call);
		Code.put2(offset);
	}
	
	//--------------DESIGNATOR STATEMENT------------------------------------------------
	
	public void visit(DesignatorStmtAssign stmt) {
		Code.store(stmt.getDesignator().obj);
	}
	
	public void visit(DesignatorStmtFuncCall stmt) {
		int offset = stmt.getDesignator().obj.getAdr() - Code.pc;
		
		// Generate instruction
		Code.put(Code.call);
		Code.put2(offset);
	}
	
	public void visit(DesignatorStmtInc stmt) {
		// Generate instruction
		Code.load(stmt.getDesignator().obj);
		Code.loadConst(1);
		Code.put(Code.add);
		Code.store(stmt.getDesignator().obj);
	}
	
	public void visit(DesignatorStmtDec stmt) {
		// Generate instruction
		Code.load(stmt.getDesignator().obj);
		Code.loadConst(1);
		Code.put(Code.sub);
		Code.store(stmt.getDesignator().obj);
	}
	
	//--------------STATEMENT-----------------------------------------------------------
	
	public void visit(PrintStmt stmt) {
		if (!this.printWidthSpecified) {
			int width = stmt.getPrintPars().struct == Tab.charType ? BYTE_PRINT_WIDTH : INT_PRINT_WIDTH;
			Code.loadConst(width);
		}
		
		// Generate instruction
		if (stmt.getPrintPars().struct == Tab.charType)
			Code.put(Code.bprint);
		else
			Code.put(Code.print);
		
		this.printWidthSpecified = false;
	}
	
	public void visit(PrintParametersWithConst stmt) {
		// Push print width to the stack
		Code.loadConst(stmt.getN1());
		
		this.printWidthSpecified = true;
	}
	
}
