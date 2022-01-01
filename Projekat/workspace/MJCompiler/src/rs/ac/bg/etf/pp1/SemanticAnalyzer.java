package rs.ac.bg.etf.pp1;

import org.apache.log4j.Logger;
import rs.ac.bg.etf.pp1.ast.*;
import rs.etf.pp1.symboltable.Tab;
import rs.etf.pp1.symboltable.concepts.*;
import rs.etf.pp1.symboltable.structure.HashTableDataStructure;

public class SemanticAnalyzer extends VisitorAdaptor {

	Struct currentDeclType = null;							// Contains type, when declaring multiple variables/constants in one line
	int currentVarObjType = Obj.Var;						// If variable is global(default), or field of a class/structure
	boolean errorDetected = false;							// If any semantic/context error is detected
	Struct currentMethodRetType = TabExtended.noType;		// Contains return type of a expression for a current method

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
			}
		} else {
			// Type not found in symbol table
			type.struct = TabExtended.noType;
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
		factorWithNew.struct = factorWithNew.getType().struct;
	}
	
	public void visit(FactorWithNewArray factorWithNewArray) {
		factorWithNewArray.struct = new StructExtended(StructExtended.Array, factorWithNewArray.getType().struct);
	}
	
	/* ==================================================================>
	 *  Term type propagation 
	 * <================================================================== */
	public void visit(MultipleTerm multipleTerm) {
		if (!multipleTerm.getTerm().struct.equals(multipleTerm.getFactor().struct)) {
			// Different types
			return;
		}
		
		multipleTerm.struct = multipleTerm.getFactor().struct;
	}
	
	public void visit(SingleTerm singleTerm) {
		singleTerm.struct = singleTerm.getFactor().struct;
	}
	
	/* ==================================================================>
	 *  Expression type propagation 
	 * <================================================================== */
	public void visit(MultipleExpr multipleExpr) {
		if (!multipleExpr.getExpr().struct.equals(multipleExpr.getTerm().struct)) {
			// Different types
			return;
		}
		
		multipleExpr.struct = multipleExpr.getTerm().struct;
	}
	
	public void visit(SingleExpr singleExpr) {
		singleExpr.struct = singleExpr.getTerm().struct;
	}
	
	public void visit(SingleExprWithMinus singleExprWithMinus) {
		singleExprWithMinus.struct = singleExprWithMinus.getTerm().struct;
	}
	
	/* ==================================================================>
	 * Statement type propagation
	 * <================================================================== */	
	public void visit(ReturnExprStmt retStmt) {
		this.currentMethodRetType = retStmt.getExpr().struct;
	}
}
