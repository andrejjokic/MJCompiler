package rs.ac.bg.etf.pp1;

import java.util.ArrayList;
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
	
	private Stack<Obj> designatorObjStack = new Stack<>();				// Stack for designator indexing
	private Stack<ConditionTree> conditionTreeStack = new Stack<>();	// Condition tree stack - for nested IFs
	
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
	
	private void addConditionFactor(Class relOp) {
		int opCode = getRelOpInstrCode(relOp);
		
		Code.putFalseJump(opCode, 0);
		conditionTreeStack.peek().addFactor(Code.pc - 3, Code.inverse[opCode]);
	}
	
	private void addConditionTerm() {
		conditionTreeStack.peek().addTerm();
	}
	
	private void addBreak() {
		Code.putJump(0);
		
		// Find outer do while loop
		List<ConditionTree> condTreeList = conditionTreeStack.getStackList();
		
		for (int i = condTreeList.size() - 1; i >= 0; i--) {
			if (condTreeList.get(i).isDoWhileStmt()) {
				condTreeList.get(i).addBreak();
				break;
			}
		}
	}
	
	private void addContinue() {
		Code.putJump(0);
		
		// Find outer do while loop
		List<ConditionTree> condTreeList = conditionTreeStack.getStackList();
		
		for (int i = condTreeList.size() - 1; i >= 0; i--) {
			if (condTreeList.get(i).isDoWhileStmt()) {
				condTreeList.get(i).addContinue();
				break;
			}
		}
	}
	
	private void defineChrAndOrdFunctions() {
		 Obj chrObj = TabExtended.find("chr");
		 Obj ordObj = TabExtended.find("ord");
	     chrObj.setAdr(Code.pc);
	     ordObj.setAdr(Code.pc);

	     /* Entry */
	     Code.put(Code.enter);
	     Code.put(1);
	     Code.put(1);
	     
	     /* Body */
	     Code.put(Code.load_n + 0);		// Load first and only parameter to stack
	     
	     /* Exit */
	     Code.put(Code.exit);
	     Code.put(Code.return_);
	}

	private void defineLenFunction() {
		 Obj chrObj = TabExtended.find("len");
	     chrObj.setAdr(Code.pc);

	     /* Entry */
	     Code.put(Code.enter);
	     Code.put(1);
	     Code.put(1);
	     
	     /* Body */
	     Code.put(Code.load_n + 0);		// Load first and only parameter to stack
	     Code.put(Code.arraylength);
	     
	     /* Exit */
	     Code.put(Code.exit);
	     Code.put(Code.return_);
	}
	
	//====================================================================================
	//  			Visit Methods
	//====================================================================================
	
	//--------------PROGRAM--------------------------------------------------------------
	
	public void visit(ProgName progName) {
		/* Define predefined functions */
		defineLenFunction();
		defineChrAndOrdFunctions();
	}
	
	//--------------CONST----------------------------------------------------------------
	
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
	
	public void visit(DesignatorName designatorName) {
		// Save designator object
		designatorObjStack.push(designatorName.obj);
	}
	
	public void visit(IndexingArray indexing) {
		/*
		 *  If it is a class field, getfield instruction takes 1 argument from stack,
		 *  which is now under the array size constant on stack, so swap them
		 */
		if (designatorObjStack.peek().getKind() == Obj.Fld) {
			swapTop2ValuesOnStack();
		}
		
		// Load designator's value (if it is a reference, it will be the pointer's value)
		Code.load(designatorObjStack.peek());
		
		// Designator's value will now be on top of array size constant on stack, so swap them
		swapTop2ValuesOnStack();
		
		Obj elem = designatorObjStack.pop();
		elem = new Obj(Obj.Elem, elem.getName(), elem.getType().getElemType());
		
		designatorObjStack.push(elem);
	}
	
	public void visit(IndexingField indexing) {
		// Load designator's value (if it is a reference, it will be the pointer's value)
		Code.load(designatorObjStack.peek());
		
		designatorObjStack.push(designatorObjStack.pop().getType().getMembersTable().searchKey(indexing.getIdentName()));
	}
	
	public void visit(Designator designator) {
		SyntaxNode parent = designator.getParent();
		
		if (parent.getClass() == FactorDesignator.class) {		// Part of expression
			Code.load(designatorObjStack.peek());
		}
		
		designator.obj = designatorObjStack.pop();
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
		int offset = factor.getFuncCallStart().getDesignator().obj.getAdr() - Code.pc;
		
		// Generate instruction
		Code.put(Code.call);
		Code.put2(offset);
	}
	
	//--------------DESIGNATOR STATEMENT------------------------------------------------
	
	public void visit(DesignatorStmtAssign stmt) {
		Code.store(stmt.getDesignator().obj);
	}
	
	public void visit(DesignatorStmtFuncCall stmt) {
		int offset = stmt.getFuncCallStart().getDesignator().obj.getAdr() - Code.pc;
		
		// Generate instruction
		Code.put(Code.call);
		Code.put2(offset);
		
		// If function has return value, pop it from the stack because no one will use it
		if (stmt.getFuncCallStart().getDesignator().obj.getType() != TabExtended.noType)
			Code.put(Code.pop);
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
		if (stmt.getDesignator().obj.getType() == TabExtended.charType)
			Code.put(Code.bread);
		else
			Code.put(Code.read);
		
		Code.store(stmt.getDesignator().obj);
	}
	
	public void visit(IfStmtStart stmtStart) {
		conditionTreeStack.push(new ConditionTree(false));
	}
	
	public void visit(ThenStmtStart stmtStart) {;
		conditionTreeStack.peek().setIfStartAdr();
	}
	
	public void visit(ElseStmtStart stmtStart) {
		conditionTreeStack.peek().setElseStartAdr();
	}
	
	public void visit(IfStmt stmt) {
		ConditionTree topCondition = conditionTreeStack.pop();
		
		topCondition.setStmtEndAdr();
		topCondition.endCondition();
		topCondition.fixCondition();
	}
	
	public void visit(IfElseStmt stmt) {
		ConditionTree topCondition = conditionTreeStack.pop();
		
		topCondition.setStmtEndAdr();
		topCondition.endCondition();
		topCondition.fixCondition();
	}
	
	public void visit(DoStatementStart stmtStart) {
		conditionTreeStack.push(new ConditionTree(true));
		conditionTreeStack.peek().setIfStartAdr();
	}
	
	public void visit(WhileConditionStart stmtStart) {
		conditionTreeStack.peek().setCondStartAdr();
	}
	
	public void visit(DoWhileStmt stmt) {
		ConditionTree topCondition = conditionTreeStack.pop();
		
		topCondition.setStmtEndAdr();
		topCondition.endCondition();
		topCondition.fixCondition();
	}
	
	public void visit(BreakStmt stmt) {
		addBreak();
	}
	
	public void visit(ContinueStmt stmt) {
		addContinue();
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
