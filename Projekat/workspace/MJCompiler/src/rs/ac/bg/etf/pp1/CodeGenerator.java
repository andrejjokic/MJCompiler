package rs.ac.bg.etf.pp1;

import java.util.LinkedList;
import java.util.List;

import rs.ac.bg.etf.pp1.ast.*;
import rs.etf.pp1.mj.runtime.Code;
import rs.etf.pp1.symboltable.Tab;
import rs.etf.pp1.symboltable.concepts.Obj;

public class CodeGenerator extends VisitorAdaptor {
	
	private final static int INT_PRINT_WIDTH = 5;
	private final static int BYTE_PRINT_WIDTH = 1;
	
	private int mainPC;
	
	private boolean printWidthSpecified = false;						// If the print statement has a NumConst part	
	private Obj currentDesignatorObj = null;							// Object of a current designator in array/class indexing
	
	private List<ConditionTree> conditionTreeStack = new LinkedList<>();	// Condition tree stack - for nested IFs
	
	public int getMainPC() {
		return mainPC;
	}
	
	//====================================================================================
	//  			Helpers
	//====================================================================================
	
	private int getOpInstrCode(Class op) {
		if (op == Mul.class) return Code.mul;
		if (op == Div.class) return Code.div;
		if (op == Percent.class) return Code.rem;
		if (op == Add.class) return Code.add;
		if (op == Minus.class) return Code.sub;
		
		return -1;
	}
	
	private int getRelOpInstrCode(Class op) {
		if (op == SingleCondFact.class) return Code.gt;
		if (op == EQCondFact.class) return Code.eq;
		if (op == NEQCondFact.class) return Code.ne;
		if (op == GTCondFact.class) return Code.gt;
		if (op == GTECondFact.class) return Code.ge;
		if (op == LTCondFact.class) return Code.lt;
		if (op == LTECondFact.class) return Code.le;
		
		return -1;
	}
	
	private void swapTop2ValuesOnStack() {
		Code.put(Code.dup_x1);
		Code.put(Code.pop);
	}
	
	private void pushToConditionStack() {
		this.conditionTreeStack.add(new ConditionTree());
	}
	
	private ConditionTree peekConditionStack() {
		return this.conditionTreeStack.get(this.conditionTreeStack.size() - 1);
	}
	
	private ConditionTree popConditionStack() {
		return this.conditionTreeStack.remove(this.conditionTreeStack.size() - 1);
	}
	
	private void addConditionFactor(Class relOp) {
		int opCode = getRelOpInstrCode(relOp);
		
		Code.putFalseJump(opCode, 0);
		peekConditionStack().addFactor(Code.pc - 3, Code.inverse[opCode]);
	}
	
	private void addConditionTerm() {
		peekConditionStack().addTerm();
	}
	
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
			Code.load(this.currentDesignatorObj);
		}
		
		designator.obj = this.currentDesignatorObj;
		this.currentDesignatorObj = null;
	}
	
	public void visit(DesignatorName designatorName) {
		// Save designator object
		this.currentDesignatorObj = designatorName.obj;
	}
	
	public void visit(IndexingArray indexing) {
		/*
		 *  If it is a class field, getfield instruction takes 1 argument from stack,
		 *  which is now under the array size constant on stack, so swap them
		 */
		if (this.currentDesignatorObj.getKind() == Obj.Fld) {
			swapTop2ValuesOnStack();
		}
		
		// Load designator's value (if it is a reference, it will be the pointer's value)
		Code.load(this.currentDesignatorObj);
		
		// Designator's value will now be on top of array size constant on stack, so swap them
		swapTop2ValuesOnStack();
		
		this.currentDesignatorObj = new Obj(Obj.Elem, currentDesignatorObj.getName(), this.currentDesignatorObj.getType().getElemType());
	}
	
	public void visit(IndexingField indexing) {
		// Load designator's value (if it is a reference, it will be the pointer's value)
		Code.load(this.currentDesignatorObj);
		
		this.currentDesignatorObj = this.currentDesignatorObj.getType().getMembersTable().searchKey(indexing.getIdentName());
	}
	
	//--------------EXPRESSION----------------------------------------------------------
	
	public void visit(MultipleExpr expr) {
		int code = getOpInstrCode(expr.getAddop().getClass());
		
		// Generate instruction
		Code.put(code);
	}
	
	public void visit(SingleExprWithMinus expr) {
		// Generate instruction
		Code.put(Code.neg);
	}
	
	//--------------TERM----------------------------------------------------------------
	
	public void visit(MultipleTerm term) {
		int code = getOpInstrCode(term.getMulop().getClass());
		
		// Generate instruction
		Code.put(code);
	}
	
	//--------------FACTOR--------------------------------------------------------------
	
	public void visit(FactorWithNew factor) {
		// Generate instruction
		Code.put(Code.new_);
		Code.put2(4 * factor.getType().struct.getNumberOfFields());
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
	
	public void visit(ReadStmt stmt) {
		// Generate instruction
		Code.put(Code.read);
		Code.store(stmt.getDesignator().obj);
	}
	
	public void visit(IfStmtStart stmtStart) {
		pushToConditionStack();
	}
	
	public void visit(ThenStmtStart stmtStart) {
		peekConditionStack().setIfStartAdr();
	}
	
	public void visit(ElseStmtStart stmtStart) {
		peekConditionStack().setElseStartAdr();
	}
	
	public void visit(IfStmt stmt) {
		ConditionTree topCondition = popConditionStack();
		
		topCondition.setStmtEndAdr();
		topCondition.endCondition();
		topCondition.fixCondition();
	}
	
	public void visit(IfElseStmt stmt) {
		ConditionTree topCondition = popConditionStack();
		
		topCondition.setStmtEndAdr();
		topCondition.endCondition();
		topCondition.fixCondition();
	}
	
	//--------------CONDITION------------------------------------------------------------
	
	public void visit(SingleCondFact condFact) {
		Code.loadConst(0);
		addConditionFactor(condFact.getClass());
	}
	
	public void visit(EQCondFact condFact) {
		addConditionFactor(condFact.getClass());
	}
	
	public void visit(NEQCondFact condFact) {
		addConditionFactor(condFact.getClass());
	}
	
	public void visit(GTCondFact condFact) {
		addConditionFactor(condFact.getClass());
	}
	
	public void visit(GTECondFact condFact) {
		addConditionFactor(condFact.getClass());
	}
	
	public void visit(LTCondFact condFact) {
		addConditionFactor(condFact.getClass());
	}
	
	public void visit(LTECondFact condFact) {
		addConditionFactor(condFact.getClass());
	}	
	
	public void visit(SingleCondition condition) {
		addConditionTerm();
	}
	
	public void visit(MultipleConditions condition) {
		addConditionTerm();
	}
}
