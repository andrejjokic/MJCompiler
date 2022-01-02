package rs.ac.bg.etf.pp1;

import org.apache.log4j.Logger;
import rs.ac.bg.etf.pp1.ast.*;
import rs.etf.pp1.symboltable.concepts.*;
import rs.etf.pp1.symboltable.structure.*;

public class SemanticAnalyzer extends VisitorAdaptor {

	Struct currentDeclType = null;							// Contains type, when declaring multiple variables/constants in one line
	int currentVarObjType = Obj.Var;						// If variable is global(default), or field of a class/structure
	boolean errorDetected = false;							// If any semantic/context error is detected
	Struct currentMethodRetType = TabExtended.noType;		// Contains return type of a expression for a current method
	boolean insideLoop = false;								// If current parsing point is in between DO ... WHILE
	Obj currentDesignatorObj= null;							// Object of a current designator
	
	Logger log = Logger.getLogger(getClass());

	public void report_error(String message, SyntaxNode info) {
		StringBuilder msg = new StringBuilder(message);
		int line = (info == null) ? 0: info.getLine();
		
		if (line != 0)
			msg.append (" na liniji ").append(line);
		
		log.error(msg.toString());
	}

	public void report_info(String message, SyntaxNode info) {
		StringBuilder msg = new StringBuilder(message); 
		int line = (info == null) ? 0: info.getLine();
		
		if (line != 0)
			msg.append (" na liniji ").append(line);
		
		log.info(msg.toString());
	}
	
	/* <=========================================================================================
	 *  Program recognition 
	 * =========================================================================================> */
	public void visit(ProgName progName) {
		progName.obj = TabExtended.insert(Obj.Prog, progName.getProgName(), TabExtended.noType);
		TabExtended.openScope();
	}
	
	public void visit(Program program) {
		TabExtended.chainLocalSymbols(program.getProgName().obj);
		TabExtended.closeScope();
	}
	
	/* ==================================================================>
	 * Check if type is valid
	 * <================================================================== */
	public void visit(Type type) {
		// Look for type in symbol table
		Obj typeNode = TabExtended.find(type.getTypeName());
		
		if (typeNode != TabExtended.noObj) {
			if (typeNode.getKind() == Obj.Type) {
				// Symbol found and it is a type
				type.struct = typeNode.getType();
			} else {
				// Symbol found, but it is not a type 
				type.struct = TabExtended.noType;
				report_error("Greska na liniji " + type.getLine() + " : " + type.getTypeName() + " ne predstavlja tip!", null);
			}
		} else {
			// Type not found in symbol table
			type.struct = TabExtended.noType;
			report_error("Greska na liniji " + type.getLine() + " : " + type.getTypeName() + " nije pronadjen u tabeli simbola!", null);
		}
	}
	
	/* ==================================================================>
	 *  Constants Declaration
	 * <================================================================== */
	public void visit(ConstDeclType constDeclType) {
		this.currentDeclType = constDeclType.getType().struct;
	}
	
	public void visit(ConstDecl constDecl) {
		TabExtended.insert(Obj.Con, constDecl.getConstName(), this.currentDeclType);
	}
	
	public void visit(ConstListDeclarations constListDeclarations) {
		this.currentDeclType = null;
	}
	
	/* ==================================================================>
	 *  Variable Declaration
	 * <================================================================== */
	public void visit(VarListDeclsType varListDeclsType) {
		this.currentDeclType = varListDeclsType.getType().struct;
	}
	
	public void visit(VarDeclNoBrackets varDeclNoBrackets) {
		TabExtended.insert(this.currentVarObjType, varDeclNoBrackets.getVarName(), currentDeclType);
	}
	
	public void visit(VarDeclBrackets varDeclBrackets) {
		TabExtended.insert(this.currentVarObjType, varDeclBrackets.getVarName(), new StructExtended(StructExtended.Array, currentDeclType));
	}
	
	public void visit(VarListDeclarations varListDeclarations) {
		this.currentDeclType = null;
	}
	
	/* ==================================================================>
	 *  Record Declaration
	 * <================================================================== */
	public void visit(RecordName recordName) {
		recordName.struct = new StructExtended(StructExtended.Record, new HashTableDataStructure());
		
		TabExtended.insert(Obj.Type, recordName.getName(), recordName.struct);
		TabExtended.openScope();
		
		this.currentVarObjType = Obj.Fld;
	}
	
	public void visit(RecordDeclaration recordDeclaration) {
		TabExtended.chainLocalSymbols(recordDeclaration.getRecordName().struct);
		TabExtended.closeScope();
		
		this.currentVarObjType = Obj.Var;
	}
	
	/* ==================================================================>
	 *  Class Declaration
	 * <================================================================== */
	public void visit(Extends ext) {
		ext.struct = ext.getType().struct;
	}
	
	public void visit(NoExtends ext) {
		ext.struct = null;
	}
	
	public void visit(ClassIdent classIdent) {
		classIdent.struct = new StructExtended(StructExtended.Class, new HashTableDataStructure());
		classIdent.struct.setElementType(classIdent.getExtendsClause().struct);
				
		TabExtended.insert(Obj.Type, classIdent.getName(), classIdent.struct);
		TabExtended.openScope();
		
		this.currentVarObjType = Obj.Fld;
	}
	
	public void visit(ClassDeclaration classDeclaration) {
		TabExtended.chainLocalSymbols(classDeclaration.getClassIdent().struct);
		TabExtended.closeScope();
		
		this.currentVarObjType = Obj.Var;
	}
	
	/* ==================================================================>
	 *  Method Declaration
	 * <================================================================== */
	public void visit(RetType retType) {
		retType.struct = retType.getType().struct;
	}
	
	public void visit(RetVoid retVoid) {
		retVoid.struct = TabExtended.noType;
	}
	
	public void visit(FormalParameterNoBrackets formalParam) {
		TabExtended.insert(Obj.Var, formalParam.getName(), formalParam.getType().struct);
	}
	
	public void visit(FormalParameterBrackets formalParam) {
		TabExtended.insert(Obj.Var, formalParam.getName(), new StructExtended(StructExtended.Array, formalParam.getType().struct));
	}
	
	public void visit(MethodIdent methodIdent) {
		methodIdent.obj = TabExtended.insert(Obj.Meth, methodIdent.getName(), methodIdent.getReturnType().struct);
		TabExtended.openScope();
	}
	
	public void visit(MethodDecl methodDecl) {
		methodDecl.getMethodIdent().obj.setLevel(TabExtended.currentScope().getnVars());
		TabExtended.chainLocalSymbols(methodDecl.getMethodIdent().obj);
		TabExtended.closeScope();
		
		// Check return type compatibility
		if (!methodDecl.getMethodIdent().getReturnType().struct.equals(this.currentMethodRetType)) {
			this.errorDetected = true;
			report_error("Greska na liniji " + methodDecl.getLine() + " : " + "nekompatibilan tip return iskaza! ", null);
		}
		
		this.currentMethodRetType = TabExtended.noType;
	}
	
	/* ==================================================================>
	 *  Check constant declaration type compatibility
	 * <================================================================== */
	public void visit(NumConst numConst) {
		if (this.currentDeclType != null && !this.currentDeclType.equals(TabExtended.intType)) {
			this.errorDetected = true;
			report_error("Greska na liniji " + numConst.getLine() + " : " + "nekompatibilan tip konstante! ", null);
		}
		
		numConst.struct = TabExtended.intType;
	}
	
	public void visit(CharConst charConst) {
		if (this.currentDeclType != null && !this.currentDeclType.equals(TabExtended.charType)) {
			this.errorDetected = true;
			report_error("Greska na liniji " + charConst.getLine() + " : " + "nekompatibilan tip konstante! ", null);
		}
		
		charConst.struct = TabExtended.charType;
	}
	
	public void visit(BoolConst boolConst) {
		if (this.currentDeclType != null && !this.currentDeclType.equals(TabExtended.boolType)) {
			this.errorDetected = true;
			report_error("Greska na liniji " + boolConst.getLine() + " : " + "nekompatibilan tip konstante! ", null);
		}
		
		boolConst.struct = TabExtended.boolType;
	}
	
	/* ==================================================================>
	 *  Factor type propagation 
	 * <================================================================== */
	public void visit(FactorWithConst factorWithConst) {
		factorWithConst.struct = factorWithConst.getConst().struct;
	}
	
	public void visit(FactorWithParen factorWithParen) {
		factorWithParen.struct = factorWithParen.getExpr().struct;
	}
	
	public void visit(FactorWithNew factorWithNew) {
		if (factorWithNew.getType().struct.getKind() != StructExtended.Class) {
			report_error("Greska na liniji " + factorWithNew.getLine() + " : Tip " + factorWithNew.getType().getTypeName() + " nije klasa! ", null);
			this.errorDetected = true;
		}
		
		factorWithNew.struct = factorWithNew.getType().struct;
	}
	
	public void visit(FactorWithNewArray factorWithNewArray) {
		if (!factorWithNewArray.getExpr().struct.equals(TabExtended.intType)) {
			report_error("Greska na liniji " + factorWithNewArray.getLine() + " : Izraz za indeksiranje mora biti tipa int!", null);
			this.errorDetected = true;
		}
		
		factorWithNewArray.struct = new StructExtended(StructExtended.Array, factorWithNewArray.getType().struct);
	}
	
	public void visit(FactorDesignator factorDesignator) {
		factorDesignator.struct = factorDesignator.getDesignator().obj.getType();
	}
	
	public void visit(FactorDesignatorFuncCall factorDesignator) {
		factorDesignator.struct = factorDesignator.getDesignator().obj.getType();
	}
	
	/* ==================================================================>
	 *  Term type propagation 
	 * <================================================================== */
	public void visit(MultipleTerm multipleTerm) {
		if (!multipleTerm.getTerm().struct.equals(TabExtended.intType) || !multipleTerm.getFactor().struct.equals(TabExtended.intType)) {
			report_error("Greska na liniji " + multipleTerm.getLine() + " : Mnozioci moraju biti tipa int! ", null);
			this.errorDetected = true;
			multipleTerm.struct = TabExtended.noType;
			return;
		}
		
		multipleTerm.struct = TabExtended.intType;
	}
	
	public void visit(SingleTerm singleTerm) {
		singleTerm.struct = singleTerm.getFactor().struct;
	}
	
	/* ==================================================================>
	 *  Expression type propagation 
	 * <================================================================== */
	public void visit(MultipleExpr multipleExpr) {
		if (!multipleExpr.getExpr().struct.equals(TabExtended.intType) || !multipleExpr.getTerm().struct.equals(TabExtended.intType)) {
			report_error("Greska na liniji " + multipleExpr.getLine() + " : Sabirci moraju biti tipa int! ", null);
			this.errorDetected = true;
			multipleExpr.struct = TabExtended.noType;
			return;
		}
		
		multipleExpr.struct = TabExtended.intType;
	}
	
	public void visit(SingleExpr singleExpr) {
		singleExpr.struct = singleExpr.getTerm().struct;
	}
	
	public void visit(SingleExprWithMinus singleExprWithMinus) {
		if (!singleExprWithMinus.getTerm().struct.equals(TabExtended.intType)) {
			report_error("Greska na liniji " + singleExprWithMinus.getLine() + " : Operand mora biti tipa int! ", null);
			this.errorDetected = true;
			singleExprWithMinus.struct = TabExtended.noType;
			return;
		}
		
		singleExprWithMinus.struct = TabExtended.intType;
	}
	
	/* ==================================================================>
	 *  Designator type propagation 
	 * <================================================================== */
	public void visit(DesignatorName designatorName) {
		this.currentDesignatorObj = TabExtended.find(designatorName.getName());
	}
	
	public void visit(IndexingField indexingField) {
		if (this.currentDesignatorObj.getType().getKind() != StructExtended.Class) {
			report_error("Greska na liniji " + indexingField.getLine() + " : Tip ne predstavlja unutrasnju klasu! ", null);
			this.errorDetected = true;
			return;
		}
		
		Obj member = this.currentDesignatorObj.getType().getMembersTable().searchKey(indexingField.getIdentName());
		
		if (member == null || (member.getKind() != Obj.Meth && member.getKind() != Obj.Fld)) {
			report_error("Greska na liniji " + indexingField.getLine() + " : " + indexingField.getIdentName() + " ne predstavlja ni metodu ni polje klase! ", null);
			this.errorDetected = true;
			return;
		}
		
		this.currentDesignatorObj = member;
	}
	
	public void visit(IndexingArray indexingArray) {
		if (this.currentDesignatorObj.getType().getKind() != StructExtended.Array) {
			report_error("Greska na liniji " + indexingArray.getLine() + " : " + this.currentDesignatorObj.getName() + " nije niz!", null);
			this.errorDetected = true;
			return;
		}
		
		if (indexingArray.getExpr().struct != TabExtended.intType) {
			report_error("Greska na liniji " + indexingArray.getLine() + " : Izraz za indeksiranje nije tipa int!", null);
			this.errorDetected = true;
			return;
		}
		
		this.currentDesignatorObj = new Obj(Obj.Elem, currentDesignatorObj.getName(), this.currentDesignatorObj.getType().getElemType());
	}
	
	public void visit(Designator designator) {
		designator.obj = this.currentDesignatorObj;
		this.currentDesignatorObj = null;
	}
	
	/* ==================================================================>
	 * Statement type propagation
	 * <================================================================== */	
	public void visit(ReturnExprStmt retStmt) {
		this.currentMethodRetType = retStmt.getExpr().struct;
	}
	
	/* ==================================================================>
	 * Loop statements context check
	 * <================================================================== */
	public void visit(DoStatementStart doStmtStart) {
		this.insideLoop = true;
	}
	
	public void visit(DoWhileStmt doWhileStmt) {
		this.insideLoop = false;
	}
	
	public void visit(BreakStmt breakStmt) {
		if (!this.insideLoop) {
			this.errorDetected = true;
			report_error("Greska na liniji " + breakStmt.getLine() + " : " + "Iskaz break se moze koristiti samo unutar do-while petlje! ", null);
		}
	}
	
	public void visit(ContinueStmt continueStmt) {
		if (!this.insideLoop) {
			this.errorDetected = true;
			report_error("Greska na liniji " + continueStmt.getLine() + " : " + "Iskaz continue se moze koristiti samo unutar do-while petlje! ", null);
		}
	}
	
	/* ==================================================================>
	 * Condition expressions compatibility check and propagation
	 * <================================================================== */
	public void visit(SingleCondFact condFact) {
		if (!condFact.getExpr().struct.equals(TabExtended.boolType)) {
			condFact.struct = TabExtended.noType;
		} else {
			condFact.struct = TabExtended.boolType;
		}
	}
	
	public void visit(EQCondFact condFact) {
		if (!condFact.getExpr().struct.compatibleWith(condFact.getExpr1().struct)) {
			report_error("Greska na liniji " + condFact.getLine() + " : " + "Tipovi moraju biti kompatibilni! ", null);
			this.errorDetected = true;
			condFact.struct = TabExtended.noType;
			return;
		}
		
		condFact.struct = TabExtended.boolType;
	}
	
	public void visit(NEQCondFact condFact) {
		if (!condFact.getExpr().struct.compatibleWith(condFact.getExpr1().struct)) {
			report_error("Greska na liniji " + condFact.getLine() + " : " + "Tipovi moraju biti kompatibilni! ", null);
			this.errorDetected = true;
			condFact.struct = TabExtended.noType;
			return;
		}
		
		condFact.struct = TabExtended.boolType;
	}
	
	public void visit(GTCondFact condFact) {		
		if (!condFact.getExpr().struct.compatibleWith(condFact.getExpr1().struct)) {
			report_error("Greska na liniji " + condFact.getLine() + " : " + "Tipovi moraju biti kompatibilni! ", null);
			this.errorDetected = true;
			condFact.struct = TabExtended.noType;
			return;
		}
		
		if (condFact.getExpr().struct.getKind() == StructExtended.Class || condFact.getExpr1().struct.getKind() == StructExtended.Class 
				|| condFact.getExpr().struct.getKind() == StructExtended.Array || condFact.getExpr1().struct.getKind() == StructExtended.Array) {
			
			report_error("Greska na liniji " + condFact.getLine() + " : " + "Relacioni operator '>' se ne moze koristi za operande tipa Class/Array! ", null);
			this.errorDetected = true;
			condFact.struct = TabExtended.noType;
			return;
		}
		
		condFact.struct = TabExtended.boolType;
	}
	
	public void visit(GTECondFact condFact) {
		if (!condFact.getExpr().struct.compatibleWith(condFact.getExpr1().struct)) {
			report_error("Greska na liniji " + condFact.getLine() + " : " + "Tipovi moraju biti kompatibilni! ", null);
			this.errorDetected = true;
			condFact.struct = TabExtended.noType;
			return;
		}
		
		if (condFact.getExpr().struct.getKind() == StructExtended.Class || condFact.getExpr1().struct.getKind() == StructExtended.Class 
				|| condFact.getExpr().struct.getKind() == StructExtended.Array || condFact.getExpr1().struct.getKind() == StructExtended.Array) {
			
			report_error("Greska na liniji " + condFact.getLine() + " : " + "Relacioni operator '>=' se ne moze koristi za operande tipa Class/Array! ", null);
			this.errorDetected = true;
			condFact.struct = TabExtended.noType;
			return;
		}
		
		condFact.struct = TabExtended.boolType;
	}
	
	public void visit(LTCondFact condFact) {
		if (!condFact.getExpr().struct.compatibleWith(condFact.getExpr1().struct)) {
			report_error("Greska na liniji " + condFact.getLine() + " : " + "Tipovi moraju biti kompatibilni! ", null);
			this.errorDetected = true;
			condFact.struct = TabExtended.noType;
			return;
		}
		
		if (condFact.getExpr().struct.getKind() == StructExtended.Class || condFact.getExpr1().struct.getKind() == StructExtended.Class 
				|| condFact.getExpr().struct.getKind() == StructExtended.Array || condFact.getExpr1().struct.getKind() == StructExtended.Array) {
			
			report_error("Greska na liniji " + condFact.getLine() + " : " + "Relacioni operator '<' se ne moze koristi za operande tipa Class/Array! ", null);
			this.errorDetected = true;
			condFact.struct = TabExtended.noType;
			return;
		}
		
		condFact.struct = TabExtended.boolType;
	}
	
	public void visit(LTECondFact condFact) {
		if (!condFact.getExpr().struct.compatibleWith(condFact.getExpr1().struct)) {
			report_error("Greska na liniji " + condFact.getLine() + " : " + "Tipovi moraju biti kompatibilni! ", null);
			this.errorDetected = true;
			condFact.struct = TabExtended.noType;
			return;
		}
		
		if (condFact.getExpr().struct.getKind() == StructExtended.Class || condFact.getExpr1().struct.getKind() == StructExtended.Class 
				|| condFact.getExpr().struct.getKind() == StructExtended.Array || condFact.getExpr1().struct.getKind() == StructExtended.Array) {
			
			report_error("Greska na liniji " + condFact.getLine() + " : " + "Relacioni operator '<=' se ne moze koristi za operande tipa Class/Array! ", null);
			this.errorDetected = true;
			condFact.struct = TabExtended.noType;
			return;
		}
		
		condFact.struct = TabExtended.boolType;
	}
	
	public void visit(SingleCondTerm condTerm) {
		condTerm.struct = condTerm.getCondFact().struct;
	}
	
	public void visit(MultipleCondTerms condTerm) {
		if (!condTerm.getCondTerm().struct.equals(TabExtended.boolType) || !condTerm.getCondFact().struct.equals(TabExtended.boolType)) {
			condTerm.struct = TabExtended.noType;
		} else {
			condTerm.struct = TabExtended.boolType;
		}
	}
	
	public void visit(SingleCondition condition) {
		condition.struct = condition.getCondTerm().struct;
	}
	
	public void visit(MultipleConditions conditions) {
		if (!conditions.getCondition().struct.equals(TabExtended.boolType) || !conditions.getCondTerm().struct.equals(TabExtended.boolType)) {
			conditions.struct = TabExtended.noType;
		} else {
			conditions.struct = TabExtended.boolType;
		}
	}
}
