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
	
	int currentConstVal = 0;								// Value of a current constant declaration(boolean = 1 or 0, char = ASCII code)
	
	Struct currentDeclType = null;							// Contains type, when declaring multiple variables/constants in one line
	int currentVarObjType = Obj.Var;						// If variable is global(default), or field of a class/record
	
	Obj currentMethodDeclaration = null;					// Contains object of a current method declaration
	boolean currentMethodReturned = false;					// If there has been a return statement in a current method declaration
	boolean isCurrentMethodValid = true;					// If declaration of a current method is valid
	
	int loopCnt = 0;										// Number of loops currently open
	
	Obj currentDesignatorObj= null;							// Object of a current designator
	
	List<Struct> actualParamList = new ArrayList<>();		// List containing actual parameters for a function call
	
	Logger log = Logger.getLogger(getClass());


	//====================================================================================
	//  			Helpers
	//====================================================================================
	
	private boolean checkDesignatorIsFunction(Obj designObj, int line) {
		// Check symbol exists
		if (designObj == TabExtended.noObj) {
			report_error("Greska na liniji " + line + " : Simbol ne postoji u tabeli simbola!", null);
			return false;
		}
		
		// Check if symbol is a method
		if (designObj.getKind() != Obj.Meth) {
			report_error("Greska na liniji " + line + " : Simbol " + designObj.getName() + " ne predstavlja funkciju!", null);
			return false;
		}
		
		return true;
	}
	
	private void checkFormalAndActualParams(Obj designObj, int line) {
		// Check formal and actual parameters compatibility
		if (this.actualParamList.size() != designObj.getLevel()) {
			report_error("Greska na liniji " + line +" : Broj stvarnih i formalnih parametara se ne poklapa!", null);
		} else {
			Iterator<Obj> formalPars = designObj.getLocalSymbols().iterator();
			boolean compatible = true;
			
			for (int i = 0; i < this.actualParamList.size(); i++) {
				if (!this.actualParamList.get(i).assignableTo(formalPars.next().getType())) {
					compatible = false;
					break;
				}
			}
			
			if (!compatible) {
				report_error("Greska na liniji " + line + " : Tipovi stvarnih i formalnih parametara nisu kompatibilni!", null);
			}
		}
		
		this.actualParamList.clear();
	}
	
	private boolean checkNameDeclaredInThisScope(String name) {
		return TabExtended.currentScope().findSymbol(name) != null;
	}
	
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
		// Check if main method has been declared
		Obj mainObj = TabExtended.currentScope().findSymbol("main");
		if (mainObj == null || mainObj.getKind() != Obj.Meth || mainObj.getLevel() != 0 || !mainObj.getType().equals(TabExtended.noType)) {
			report_error("Main metoda nije deklarisana!", null);
		}
		
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
			numConst.struct = TabExtended.noType;
		} else {
			this.currentConstVal = numConst.getVal();
			numConst.struct = TabExtended.intType;	
		}
	}
	
	public void visit(CharConst charConst) {
		if (this.currentDeclType != null && !this.currentDeclType.equals(TabExtended.charType)) {
			report_error("Nekompatibilan tip konstante! ", charConst);
			charConst.struct = TabExtended.noType;
		} else {
			this.currentConstVal = (int)charConst.getVal().charValue();
			charConst.struct = TabExtended.charType;	
		}
	}
	
	public void visit(BoolConst boolConst) {
		if (this.currentDeclType != null && !this.currentDeclType.equals(TabExtended.boolType)) {
			report_error("Nekompatibilan tip konstante! ", boolConst);
			boolConst.struct = TabExtended.noType;
		} else {
			this.currentConstVal = boolConst.getVal() ? 1 : 0;
			boolConst.struct = TabExtended.boolType;
		}
	}
	
	public void visit(ConstDeclType constDeclType) {
		this.currentDeclType = constDeclType.getType().struct;
	}
	
	public void visit(ConstDecl constDecl) {
		if (checkNameDeclaredInThisScope(constDecl.getConstName())) {
			report_error("Ime " + constDecl.getConstName() + " je vec deklarisano unutar istog opsega! ", constDecl);
			return;
		}
		
		if (constDecl.getConst().struct != TabExtended.noType) {
			Obj newObj = TabExtended.insert(Obj.Con, constDecl.getConstName(), constDecl.getConst().struct);
			newObj.setAdr(this.currentConstVal);
		}
	}
	
	public void visit(ConstListDeclarations constListDeclarations) {
		this.currentDeclType = null;
	}
	
	//--------------VARIABLE--------------------------------------------------------------
	
	public void visit(VarListDeclsType varListDeclsType) {
		this.currentDeclType = varListDeclsType.getType().struct;
	}
	
	public void visit(VarDeclNoBrackets varDeclNoBrackets) {
		if (checkNameDeclaredInThisScope(varDeclNoBrackets.getVarName())) {
			report_error("Ime " + varDeclNoBrackets.getVarName() + " je vec deklarisano unutar istog opsega! ", varDeclNoBrackets);
			return;
		}
		
		if (this.currentDeclType != TabExtended.noType) {
			TabExtended.insert(this.currentVarObjType, varDeclNoBrackets.getVarName(), currentDeclType);
		}
	}
	
	public void visit(VarDeclBrackets varDeclBrackets) {
		if (checkNameDeclaredInThisScope(varDeclBrackets.getVarName())) {
			report_error("Ime " + varDeclBrackets.getVarName() + " je vec deklarisano unutar istog opsega! ", varDeclBrackets);
			return;
		}
		
		if (this.currentDeclType != TabExtended.noType) {
			TabExtended.insert(this.currentVarObjType, varDeclBrackets.getVarName(), new Struct(Struct.Array, currentDeclType));
		}
	}
	
	public void visit(VarListDeclarations varListDeclarations) {
		this.currentDeclType = null;
	}
	
	//--------------RECORD--------------------------------------------------------------
	
	public void visit(RecordName recordName) {
		if (checkNameDeclaredInThisScope(recordName.getName())) {
			report_error("Ime " + recordName.getName() + " je vec deklarisano unutar istog opsega! ", recordName);
			recordName.struct = TabExtended.noType;
			return;
		}
		
		recordName.struct = new Struct(Struct.Class, new HashTableDataStructure());
		
		TabExtended.insert(Obj.Type, recordName.getName(), recordName.struct);
		TabExtended.openScope();
		
		this.currentVarObjType = Obj.Fld;
	}
	
	public void visit(RecordDeclaration recordDeclaration) {
		if (recordDeclaration.getRecordName().struct != TabExtended.noType) {
			TabExtended.chainLocalSymbols(recordDeclaration.getRecordName().struct);
			TabExtended.closeScope();	
			
			this.currentVarObjType = Obj.Var;
		}
	}
	
	//--------------METHOD--------------------------------------------------------------
	
	public void visit(RetType retType) {
		retType.struct = retType.getType().struct;
	}
	
	public void visit(RetVoid retVoid) {
		retVoid.struct = TabExtended.noType;
	}
	
	public void visit(FormalParameterNoBrackets formalParam) {
		if (this.isCurrentMethodValid) {
			TabExtended.insert(Obj.Var, formalParam.getName(), formalParam.getType().struct);	
		}
	}
	
	public void visit(FormalParameterBrackets formalParam) {
		if (this.isCurrentMethodValid) {
			TabExtended.insert(Obj.Var, formalParam.getName(), new Struct(Struct.Array, formalParam.getType().struct));
		}
	}
	
	public void visit(MethodIdent methodIdent) {
		if (checkNameDeclaredInThisScope(methodIdent.getName())) {
			report_error("Ime " + methodIdent.getName() + " je vec deklarisano unutar istog opsega! ", methodIdent);
			this.isCurrentMethodValid = false;
			return;
		}
		
		methodIdent.obj = TabExtended.insert(Obj.Meth, methodIdent.getName(), methodIdent.getReturnType().struct);
		TabExtended.openScope();
		
		this.currentMethodDeclaration = methodIdent.obj;
	}
	
	public void visit(MethodDecl methodDecl) {
		if (this.isCurrentMethodValid) {
			methodDecl.getMethodIdent().obj.setLevel(TabExtended.currentScope().getnVars());
			TabExtended.chainLocalSymbols(methodDecl.getMethodIdent().obj);
			TabExtended.closeScope();
			
			// Check return type compatibility
			if (!methodDecl.getMethodIdent().getReturnType().struct.equals(TabExtended.noType) && !this.currentMethodReturned) {
				report_error("Metod ne sadrzi return iskaz!", methodDecl);
			}	
		}
		
		this.currentMethodReturned = false;
		this.currentMethodDeclaration = null;
		this.isCurrentMethodValid = true;
	}
	
	//--------------FACTOR--------------------------------------------------------------
	
	public void visit(FactorWithConst factorWithConst) {
		factorWithConst.struct = factorWithConst.getConst().struct;
	}
	
	public void visit(FactorWithParen factorWithParen) {
		factorWithParen.struct = factorWithParen.getExpr().struct;
	}
	
	public void visit(FactorWithNew factorWithNew) {
		if (factorWithNew.getType().struct.getKind() != Struct.Class) {
			report_error("Tip " + factorWithNew.getType().getTypeName() + " nije klasa!", factorWithNew);
		}
		
		factorWithNew.struct = factorWithNew.getType().struct;
	}
	
	public void visit(FactorWithNewArray factorWithNewArray) {
		if (!factorWithNewArray.getExpr().struct.equals(TabExtended.intType)) {
			report_error("Izraz za indeksiranje mora biti tipa int!", factorWithNewArray);
		}
		
		factorWithNewArray.struct = new Struct(Struct.Array, factorWithNewArray.getType().struct);
	}
	
	public void visit(FactorDesignator factorDesignator) {
		factorDesignator.struct = factorDesignator.getDesignator().obj.getType();
	}
	
	public void visit(FactorDesignatorFuncCall factorDesignator) {
		Obj designObj = factorDesignator.getDesignator().obj;
			
		if (checkDesignatorIsFunction(designObj, factorDesignator.getLine())) {
			checkFormalAndActualParams(designObj, factorDesignator.getLine());
		}
		
		factorDesignator.struct = designObj.getType();
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
		
		if (this.currentDesignatorObj == TabExtended.noObj) {
			report_error("Simbol nije pronadjen u tabeli simbola", designatorName);
		}
	}
	
	public void visit(IndexingField indexingField) {
		if (this.currentDesignatorObj.getType().getKind() != Struct.Class) {
			report_error("Tip ne predstavlja klasu! ", indexingField);
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
		if (this.currentDesignatorObj.getType().getKind() != Struct.Array) {
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
			report_error("Nekompatibilan izraz u return iskazu!", retStmt.getParent());
		}
		
		this.currentMethodReturned = true;
	}
	
	public void visit(DoStatementStart doStmtStart) {
		this.loopCnt++;
	}
	
	public void visit(DoWhileStmt doWhileStmt) {
		this.loopCnt--;
	}
	
	public void visit(BreakStmt breakStmt) {
		if (this.loopCnt == 0) {
			report_error("Iskaz break se moze koristiti samo unutar do-while petlje!", breakStmt.getParent());
		}
	}
	
	public void visit(ContinueStmt continueStmt) {
		if (this.loopCnt == 0) {
			report_error("Iskaz continue se moze koristiti samo unutar do-while petlje! ", continueStmt.getParent());
		}
	}
	
	public void visit(ReadStmt readStmt) {
		Obj designObj = readStmt.getDesignator().obj;
		
		if (designObj.getKind() != Obj.Var && designObj.getKind() != Obj.Elem && designObj.getKind() != Obj.Fld) {
			report_error("Simbol " + designObj.getName() + " mora predstavljati promenjivu, element niza ili polje unutar objekta!", readStmt);
			return;
		}
		
		if (!designObj.getType().equals(TabExtended.intType) && !designObj.getType().equals(TabExtended.charType) && !designObj.getType().equals(TabExtended.boolType)) {
			report_error("Simbol " + designObj.getName() + " mora biti tipa int/char/bool!", readStmt);
		}
	}
	
	public void visit(PrintStmt printStmt) {
		Struct paramStruct = printStmt.getPrintPars().struct;
		
		if (!paramStruct.equals(TabExtended.intType) && !paramStruct.equals(TabExtended.charType) && !paramStruct.equals(TabExtended.boolType)) {
			report_error("Izraz unutar print iskaza mora biti tipa int/char/bool! ", printStmt);
		}
	}
	
	//--------------DESIGNATOR STATEMENT---------------------------------------------------
	
	public void visit(DesignatorStmtFuncCall designStmt) {
		if (checkDesignatorIsFunction(designStmt.getDesignator().obj, designStmt.getLine())) {
			checkFormalAndActualParams(designStmt.getDesignator().obj, designStmt.getLine());
		}
	}
	
	public void visit(DesignatorStmtInc designStmt) {
		Obj designObj = designStmt.getDesignator().obj;
		
		if (designObj.getKind() != Obj.Var && designObj.getKind() != Obj.Elem && designObj.getKind() != Obj.Fld) {
			report_error("Simbol " + designObj.getName() + " mora predstavljati promenjivu, element niza ili polje unutar objekta!", designStmt);
			return;
		}
		
		if (!designObj.getType().equals(TabExtended.intType)) {
			report_error("Simbol " + designObj.getName() + " mora biti tipa int!", designStmt);
		}
	}
	
	public void visit(DesignatorStmtDec designStmt) {
		Obj designObj = designStmt.getDesignator().obj;
		
		if (designObj.getKind() != Obj.Var && designObj.getKind() != Obj.Elem && designObj.getKind() != Obj.Fld) {
			report_error("Simbol " + designObj.getName() + " mora predstavljati promenjivu, element niza ili polje unutar objekta!", designStmt);
			return;
		}
		
		if (!designObj.getType().equals(TabExtended.intType)) {
			report_error("Simbol " + designObj.getName() + " mora biti tipa int!", designStmt);
		}
	}
	
	public void visit(DesignatorStmtAssign designStmt) {
		Obj designObj = designStmt.getDesignator().obj;
		
		if (designObj.getKind() != Obj.Var && designObj.getKind() != Obj.Elem && designObj.getKind() != Obj.Fld) {
			report_error("Simbol " + designObj.getName() + " mora predstavljati promenjivu, element niza ili polje unutar objekta!", designStmt);
			return;
		}
		
		if (!designStmt.getExpr().struct.assignableTo(designObj.getType())) {
			report_error("Izrazi nisu kompatibilni pri dodeli!", designStmt);
		}
	}
	
	//--------------PRINT PARAMETERS-------------------------------------------------------
	
	public void visit(PrintParameters printParam) {
		printParam.struct = printParam.getExpr().struct;
	}
	
	public void visit(PrintParametersWithConst printParam) {
		printParam.struct = printParam.getExpr().struct;
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
		
		if (condFact.getExpr().struct.getKind() == Struct.Class || condFact.getExpr1().struct.getKind() == Struct.Class 
				|| condFact.getExpr().struct.getKind() == Struct.Array || condFact.getExpr1().struct.getKind() == Struct.Array) {
			
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
		
		if (condFact.getExpr().struct.getKind() == Struct.Class || condFact.getExpr1().struct.getKind() == Struct.Class 
				|| condFact.getExpr().struct.getKind() == Struct.Array || condFact.getExpr1().struct.getKind() == Struct.Array) {
			
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
		
		if (condFact.getExpr().struct.getKind() == Struct.Class || condFact.getExpr1().struct.getKind() == Struct.Class 
				|| condFact.getExpr().struct.getKind() == Struct.Array || condFact.getExpr1().struct.getKind() == Struct.Array) {
			
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
		
		if (condFact.getExpr().struct.getKind() == Struct.Class || condFact.getExpr1().struct.getKind() == Struct.Class 
				|| condFact.getExpr().struct.getKind() == Struct.Array || condFact.getExpr1().struct.getKind() == Struct.Array) {
			
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
