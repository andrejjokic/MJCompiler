package rs.ac.bg.etf.pp1;

import org.apache.log4j.Logger;
import rs.ac.bg.etf.pp1.ast.*;
import rs.etf.pp1.symboltable.concepts.*;
import rs.etf.pp1.symboltable.structure.HashTableDataStructure;

public class SemanticAnalyzer extends VisitorAdaptor {

	Struct currentDeclType = null;				// Contains type, when declaring multiple variables/constants in one line
	int currentVarObjType = Obj.Var;			// If variable is global(default), or field of a class/structure

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
	
	/* Program recognition */
	public void visit(ProgName progName) {
		progName.obj = TabExtended.insert(Obj.Prog, progName.getProgName(), TabExtended.noType);
		TabExtended.openScope();
	}
	
	public void visit(Program program) {
		TabExtended.chainLocalSymbols(program.getProgName().obj);
		TabExtended.closeScope();
	}
	
	/* Check if type is valid */
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
	
	/* Constants Declaration */
	public void visit(ConstDeclType constDeclType) {
		this.currentDeclType = constDeclType.getType().struct;
	}
	
	public void visit(ConstDecl constDecl) {
		TabExtended.insert(Obj.Con, constDecl.getConstName(), this.currentDeclType);
	}
	
	public void visit(ConstListDeclarations constListDeclarations) {
		this.currentDeclType = null;
	}
	
	/* Variable Declaration */
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
		this.currentVarObjType = Obj.Var;
	}
	
	/* Record Declaration */
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
}