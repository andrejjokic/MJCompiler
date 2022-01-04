package rs.ac.bg.etf.pp1;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import rs.ac.bg.etf.pp1.ast.*;
import rs.etf.pp1.symboltable.concepts.*;
import rs.etf.pp1.symboltable.structure.*;

public class SemanticAnalyzer extends VisitorAdaptor {

	boolean errorDetected = false;							// If any semantic/context error is detected
	
	Struct currentDeclType = null;							// Contains type, when declaring multiple variables/constants in one line
	Struct currentClassDeclaration = null;					// If current method declaration is part of a class
	int currentVarObjType = Obj.Var;						// If variable is global(default), or field of a class/record
	
	Obj currentMethodDeclaration = null;					// Contains object of a current method declaration
	boolean currentMethodReturned = false;					// If there has been a return statement in a current method declaration
	
	boolean insideLoop = false;								// If current parsing point is in between DO ... WHILE
	
	Obj currentDesignatorObj= null;							// Object of a current designator
	
	List<Struct> actualParamList = new ArrayList<>();		// List containing actual parameters for a function call
	
	Logger log = Logger.getLogger(getClass());


	//====================================================================================
	//  			Helpers
	//====================================================================================
	
	
	//====================================================================================
	//  			Reports
	//====================================================================================
	
	public void report_error(String message, SyntaxNode info) {
		StringBuilder msg = new StringBuilder();
		int line = (info == null) ? 0: info.getLine();
		
		if (line != 0)
			msg.append ("Greska na liniji ").append(line).append(" : ");
		
		msg.append(message);
		
		log.error(msg.toString());
		this.errorDetected = true;
	}

	public void report_info(String message, SyntaxNode info) {
		StringBuilder msg = new StringBuilder(); 
		int line = (info == null) ? 0: info.getLine();
		
		if (line != 0)
			msg.append ("Upozorenje na liniji ").append(line).append(" : ");
		
		msg.append(message);
		
		log.info(msg.toString());
	}
		 	
	//====================================================================================
	//  			Visit Methods
	//====================================================================================
	
	//--------------PROGRAM---------------------------------------------------------------
	
	public void visit(ProgName progName) {
		progName.obj = TabExtended.insert(Obj.Prog, progName.getProgName(), TabExtended.noType);
		TabExtended.openScope();
	}
	
	public void visit(Program program) {
		TabExtended.chainLocalSymbols(program.getProgName().obj);
		TabExtended.closeScope();
	}
	
	//--------------TYPE------------------------------------------------------------------
	
	public void visit(Type type) {
		Obj typeNode = TabExtended.find(type.getTypeName());
		
		if (typeNode != TabExtended.noObj) {
			if (typeNode.getKind() == Obj.Type) {
				// Symbol found and it is a type
				type.struct = typeNode.getType();
			} else {
				// Symbol found, but it is not a type 
				report_error(type.getTypeName() + " ne predstavlja tip!", type);
				type.struct = TabExtended.noType;
			}
		} else {
			// Type not found in symbol table
			report_error(type.getTypeName() + " nije pronadjen u tabeli simbola!", type);
			type.struct = TabExtended.noType;
		}
	}
	
	//--------------CONSTANT----------------------------------------------------------------
	
	public void visit(NumConst numConst) {
		if (this.currentDeclType != null && !this.currentDeclType.equals(TabExtended.intType)) {
			report_error("Nekompatibilan tip konstante! ", numConst);
		}
		
		numConst.struct = TabExtended.intType;
	}
	
	public void visit(CharConst charConst) {
		if (this.currentDeclType != null && !this.currentDeclType.equals(TabExtended.charType)) {
			report_error("Nekompatibilan tip konstante! ", charConst);
		}
		
		charConst.struct = TabExtended.charType;
	}
	
	public void visit(BoolConst boolConst) {
		if (this.currentDeclType != null && !this.currentDeclType.equals(TabExtended.boolType)) {
			report_error("Nekompatibilan tip konstante! ", boolConst);
		}
		
		boolConst.struct = TabExtended.boolType;
	}
	
	public void visit(ConstDeclType constDeclType) {
		this.currentDeclType = constDeclType.getType().struct;
	}
	
	public void visit(ConstDecl constDecl) {
		TabExtended.insert(Obj.Con, constDecl.getConstName(), this.currentDeclType);
	}
	
	public void visit(ConstListDeclarations constListDeclarations) {
		this.currentDeclType = null;
	}
	
	//--------------VARIABLE--------------------------------------------------------------
	
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
	
	//--------------RECORD--------------------------------------------------------------
	
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
	
	//--------------CLASS----------------------------------------------------------------
	
	public void visit(Extends ext) {
		ext.struct = ext.getType().struct;
	}
	
	public void visit(NoExtends ext) {
		ext.struct = TabExtended.noType;
	}
	
	public void visit(ClassIdent classIdent) {
		classIdent.struct = new StructExtended(StructExtended.Class, new HashTableDataStructure());
		classIdent.struct.setElementType(classIdent.getExtendsClause().struct);
				
		TabExtended.insert(Obj.Type, classIdent.getName(), classIdent.struct);
		TabExtended.openScope();
		
		// If class is derived, add all the parent's fields/methods to the child class
		if (classIdent.struct.getElemType() != TabExtended.noType) {
			
			classIdent.struct.getElemType().getMembers().forEach(o -> {		
				Obj inherited = TabExtended.insert(o.getKind(), o.getName(), o.getType());
				
				inherited.setAdr(o.getAdr());
				inherited.setLevel(o.getLevel());
				
				// If it is a method, add it's local parameters to the locals field
				if (o.getKind() == Obj.Meth) {
					TabExtended.openScope();
					o.getLocalSymbols().forEach(l -> {
						TabExtended.insert(l.getKind(), l.getName(), l.getType());	
					});
					TabExtended.chainLocalSymbols(inherited);
					TabExtended.closeScope();
				}
			});
		}
		
		this.currentVarObjType = Obj.Fld;
		this.currentClassDeclaration = classIdent.struct;
	}
	
	public void visit(ClassDeclaration classDeclaration) {
		TabExtended.chainLocalSymbols(classDeclaration.getClassIdent().struct);
		TabExtended.closeScope();
		
		this.currentVarObjType = Obj.Var;
		this.currentClassDeclaration = null;
	}
	
	//--------------METHOD--------------------------------------------------------------
	
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
		
		// Add implicit first argument => this if it is a class method
		if (this.currentClassDeclaration != null) {
			TabExtended.insert(Obj.Var, "this", this.currentClassDeclaration);
		}
		
		this.currentMethodDeclaration = methodIdent.obj;
	}
	
	public void visit(MethodDecl methodDecl) {
		methodDecl.getMethodIdent().obj.setLevel(TabExtended.currentScope().getnVars());
		TabExtended.chainLocalSymbols(methodDecl.getMethodIdent().obj);
		TabExtended.closeScope();
		
		// Check return type compatibility
		if (!methodDecl.getMethodIdent().getReturnType().struct.equals(TabExtended.noType) && !this.currentMethodReturned) {
			report_error("Metod ne sadrzi return iskaz!", methodDecl);
		}
		
		this.currentMethodReturned = false;
		this.currentMethodDeclaration = null;
	}
	
	//--------------FACTOR--------------------------------------------------------------
	
	public void visit(FactorWithConst factorWithConst) {
		factorWithConst.struct = factorWithConst.getConst().struct;
	}
	
	public void visit(FactorWithParen factorWithParen) {
		factorWithParen.struct = factorWithParen.getExpr().struct;
	}
	
	public void visit(FactorWithNew factorWithNew) {
		if (factorWithNew.getType().struct.getKind() != StructExtended.Class) {
			report_error("Tip " + factorWithNew.getType().getTypeName() + " nije klasa! ", factorWithNew);
		}
		
		factorWithNew.struct = factorWithNew.getType().struct;
	}
	
	public void visit(FactorWithNewArray factorWithNewArray) {
		if (!factorWithNewArray.getExpr().struct.equals(TabExtended.intType)) {
			report_error("Izraz za indeksiranje mora biti tipa int!", factorWithNewArray);
		}
		
		factorWithNewArray.struct = new StructExtended(StructExtended.Array, factorWithNewArray.getType().struct);
	}
	
	public void visit(FactorDesignator factorDesignator) {
		factorDesignator.struct = factorDesignator.getDesignator().obj.getType();
	}
	
	public void visit(FactorDesignatorFuncCall factorDesignator) {
		Obj designObj = factorDesignator.getDesignator().obj;
		
		factorDesignator.struct = designObj.getType();
			
		// Check symbol exists
		if (designObj == TabExtended.noObj) {
			report_error("Simbol ne postoji u tabeli simbola!", factorDesignator);
			return;
		}
		
		// Check if symbol is a method
		if (designObj.getKind() != Obj.Meth) {
			report_error("Simbol " + designObj.getName() + " ne predstavlja funkciju!", factorDesignator);
			return;
		}
		
		// Add implicit this parameter if it is a class method
		if (designObj.getLevel() > 0) {
			Obj firstParam = designObj.getLocalSymbols().iterator().next();
			
			if (firstParam.getType().getKind() == StructExtended.Class && firstParam.getName().equals("this")) {
				this.actualParamList.add(0, firstParam.getType());
			}
		}

		// Check formal and actual parameters compatibility
		if (this.actualParamList.size() != designObj.getLevel()) {
			report_error("Broj stvarnih i formalnih parametara se ne poklapa!", factorDesignator);
		} else {
			Iterator<Obj> formalPars = designObj.getLocalSymbols().iterator();
			boolean compatible = true;
			
			for (int i = 0; i < this.actualParamList.size(); i++) {
				if (!this.actualParamList.get(i).compatibleWith(formalPars.next().getType())) {
					compatible = false;
					break;
				}
			}
			
			if (!compatible) {
				report_error("Tipovi stvarnih i formalnih parametara nisu kompatibilni!", factorDesignator);
			}
		}
		
		this.actualParamList.clear();
	}
	
	//--------------TERM--------------------------------------------------------------
	
	public void visit(MultipleTerm multipleTerm) {
		if (!multipleTerm.getTerm().struct.equals(TabExtended.intType) || !multipleTerm.getFactor().struct.equals(TabExtended.intType)) {
			report_error("Mnozioci moraju biti tipa int! ", multipleTerm);
			multipleTerm.struct = TabExtended.noType;
			return;
		}
		
		multipleTerm.struct = TabExtended.intType;
	}
	
	public void visit(SingleTerm singleTerm) {
		singleTerm.struct = singleTerm.getFactor().struct;
	}
	
	//--------------EXPRESSION--------------------------------------------------------------
	
	public void visit(MultipleExpr multipleExpr) {
		if (!multipleExpr.getExpr().struct.equals(TabExtended.intType) || !multipleExpr.getTerm().struct.equals(TabExtended.intType)) {
			report_error("Sabirci moraju biti tipa int! ", multipleExpr);
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
			report_error("Operand mora biti tipa int! ", singleExprWithMinus);
			singleExprWithMinus.struct = TabExtended.noType;
			return;
		}
		
		singleExprWithMinus.struct = TabExtended.intType;
	}
	
	//--------------DESIGNATOR--------------------------------------------------------------
	
	public void visit(DesignatorName designatorName) {
		this.currentDesignatorObj = TabExtended.find(designatorName.getName());
	}
	
	public void visit(IndexingField indexingField) {
		if (this.currentDesignatorObj.getType().getKind() != StructExtended.Class) {
			report_error("Tip ne predstavlja unutrasnju klasu! ", indexingField);
			return;
		}
		
		Obj member = this.currentDesignatorObj.getType().getMembersTable().searchKey(indexingField.getIdentName());
		
		if (member == null || (member.getKind() != Obj.Meth && member.getKind() != Obj.Fld)) {
			report_error(indexingField.getIdentName() + " ne predstavlja ni metodu ni polje klase!", indexingField);
			return;
		}
		
		this.currentDesignatorObj = member;
	}
	
	public void visit(IndexingArray indexingArray) {
		if (this.currentDesignatorObj.getType().getKind() != StructExtended.Array) {
			report_error(this.currentDesignatorObj.getName() + " nije niz!", indexingArray);
			return;
		}
		
		if (indexingArray.getExpr().struct != TabExtended.intType) {
			report_error("Izraz za indeksiranje nije tipa int!", indexingArray);
			return;
		}
		
		this.currentDesignatorObj = new Obj(Obj.Elem, currentDesignatorObj.getName(), this.currentDesignatorObj.getType().getElemType());
	}
	
	public void visit(Designator designator) {
		designator.obj = this.currentDesignatorObj;
		this.currentDesignatorObj = null;
	}
	
	//--------------STATEMENT--------------------------------------------------------------
	
	public void visit(ReturnExprStmt retStmt) {
		if (this.currentMethodDeclaration == null) { 
			report_error("Return iskaz ne moze postojati van tela metode!", retStmt);
			return;
		}
		
		if (!retStmt.getExpr().struct.equals(this.currentMethodDeclaration.getType())) {
			report_error("Nekompatibilan izraz u return iskazu!", retStmt);
		}
		
		this.currentMethodReturned = true;
	}
	
	public void visit(ReturnNoExprStmt retStmt) {
		if (this.currentMethodDeclaration == null) { 
			report_error("Return iskaz ne moze postojati van tela metode!", retStmt);
			return;
		}
		
		if (!this.currentMethodDeclaration.getType().equals(TabExtended.noType)) {
			report_error("Nekompatibilan izraz u return iskazu!", retStmt);
		}
		
		this.currentMethodReturned = true;
	}
	
	public void visit(DoStatementStart doStmtStart) {
		this.insideLoop = true;
	}
	
	public void visit(DoWhileStmt doWhileStmt) {
		this.insideLoop = false;
	}
	
	public void visit(BreakStmt breakStmt) {
		if (!this.insideLoop) {
			report_error("Iskaz break se moze koristiti samo unutar do-while petlje!", breakStmt);
		}
	}
	
	public void visit(ContinueStmt continueStmt) {
		if (!this.insideLoop) {
			report_error("Iskaz continue se moze koristiti samo unutar do-while petlje! ", continueStmt);
		}
	}
	
	//--------------CONDITION--------------------------------------------------------------
	
	public void visit(SingleCondFact condFact) {
		if (!condFact.getExpr().struct.equals(TabExtended.boolType)) {
			report_error("Uslovni izraz mora biti tipa bool! ", condFact);
			condFact.struct = TabExtended.noType;
		} else {
			condFact.struct = TabExtended.boolType;
		}
	}
	
	public void visit(EQCondFact condFact) {
		if (!condFact.getExpr().struct.compatibleWith(condFact.getExpr1().struct)) {
			report_error("Tipovi moraju biti kompatibilni! ", condFact);
			condFact.struct = TabExtended.noType;
			return;
		}
		
		condFact.struct = TabExtended.boolType;
	}
	
	public void visit(NEQCondFact condFact) {
		if (!condFact.getExpr().struct.compatibleWith(condFact.getExpr1().struct)) {
			report_error("Tipovi moraju biti kompatibilni! ", condFact);
			condFact.struct = TabExtended.noType;
			return;
		}
		
		condFact.struct = TabExtended.boolType;
	}
	
	public void visit(GTCondFact condFact) {		
		if (!condFact.getExpr().struct.compatibleWith(condFact.getExpr1().struct)) {
			report_error("Tipovi moraju biti kompatibilni! ", condFact);
			condFact.struct = TabExtended.noType;
			return;
		}
		
		if (condFact.getExpr().struct.getKind() == StructExtended.Class || condFact.getExpr1().struct.getKind() == StructExtended.Class 
				|| condFact.getExpr().struct.getKind() == StructExtended.Array || condFact.getExpr1().struct.getKind() == StructExtended.Array) {
			
			report_error("Relacioni operator '>' se ne moze koristi za operande tipa Class/Array! ", condFact);
			condFact.struct = TabExtended.noType;
			return;
		}
		
		condFact.struct = TabExtended.boolType;
	}
	
	public void visit(GTECondFact condFact) {
		if (!condFact.getExpr().struct.compatibleWith(condFact.getExpr1().struct)) {
			report_error("Tipovi moraju biti kompatibilni! ", condFact);
			condFact.struct = TabExtended.noType;
			return;
		}
		
		if (condFact.getExpr().struct.getKind() == StructExtended.Class || condFact.getExpr1().struct.getKind() == StructExtended.Class 
				|| condFact.getExpr().struct.getKind() == StructExtended.Array || condFact.getExpr1().struct.getKind() == StructExtended.Array) {
			
			report_error("Relacioni operator '>=' se ne moze koristi za operande tipa Class/Array! ", condFact);
			condFact.struct = TabExtended.noType;
			return;
		}
		
		condFact.struct = TabExtended.boolType;
	}
	
	public void visit(LTCondFact condFact) {
		if (!condFact.getExpr().struct.compatibleWith(condFact.getExpr1().struct)) {
			report_error("Tipovi moraju biti kompatibilni! ", condFact);
			condFact.struct = TabExtended.noType;
			return;
		}
		
		if (condFact.getExpr().struct.getKind() == StructExtended.Class || condFact.getExpr1().struct.getKind() == StructExtended.Class 
				|| condFact.getExpr().struct.getKind() == StructExtended.Array || condFact.getExpr1().struct.getKind() == StructExtended.Array) {
			
			report_error("Relacioni operator '<' se ne moze koristi za operande tipa Class/Array! ", condFact);
			condFact.struct = TabExtended.noType;
			return;
		}
		
		condFact.struct = TabExtended.boolType;
	}
	
	public void visit(LTECondFact condFact) {
		if (!condFact.getExpr().struct.compatibleWith(condFact.getExpr1().struct)) {
			report_error("Tipovi moraju biti kompatibilni! ", condFact);
			condFact.struct = TabExtended.noType;
			return;
		}
		
		if (condFact.getExpr().struct.getKind() == StructExtended.Class || condFact.getExpr1().struct.getKind() == StructExtended.Class 
				|| condFact.getExpr().struct.getKind() == StructExtended.Array || condFact.getExpr1().struct.getKind() == StructExtended.Array) {
			
			report_error("Relacioni operator '<=' se ne moze koristi za operande tipa Class/Array! ", condFact);
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
	
	//--------------ACTUAL PARAMETERS----------------------------------------------------
	public void visit(MultipleActPars actPars) {
		this.actualParamList.add(actPars.getExpr().struct);
	}
	
	public void visit(SingleActPars actPars) {
		this.actualParamList.add(0, actPars.getExpr().struct);
	}
}
