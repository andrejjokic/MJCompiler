package rs.ac.bg.etf.pp1;

import rs.ac.bg.etf.pp1.ast.*;
import rs.etf.pp1.mj.runtime.Code;
import rs.etf.pp1.symboltable.concepts.Obj;

public class CodeGenerator extends VisitorAdaptor {
	
	private int mainPC;
	
	public int getMainPC() {
		return mainPC;
	}
	
	//====================================================================================
	//  			Helpers
	//====================================================================================
	
	private void generateConst(Const cnst, int val) {
		Obj con = TabExtended.insert(Obj.Con, "$", cnst.struct);
		con.setLevel(0);
		con.setAdr(val);
		
		Code.load(con);
	}
	
	//====================================================================================
	//  			Visit Methods
	//====================================================================================
	
	//--------------METHOD---------------------------------------------------------------
	
	public void visit(MethodIdent methodIdent) {
		// If it is a main function, save the pc
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
	
	//--------------CONST---------------------------------------------------------------
	
	public void visit(NumConst cnst) {
		generateConst(cnst, cnst.getVal());
	}
	
	public void visit(CharConst cnst) {
		generateConst(cnst, (int)cnst.getVal().charValue());
	}
	
	public void visit(BoolConst cnst) {
		generateConst(cnst, cnst.getVal() ? 1 : 0);
	}
	
	//--------------DESIGNATOR STATEMENT------------------------------------------------
	
	public void visit(DesignatorStmtAssign stmt) {
		Code.store(stmt.getDesignator().obj);
	}
	
	//--------------EXPRESSION----------------------------------------------------------
	
	//--------------TERM----------------------------------------------------------------
	
	//--------------FACTOR--------------------------------------------------------------
}
