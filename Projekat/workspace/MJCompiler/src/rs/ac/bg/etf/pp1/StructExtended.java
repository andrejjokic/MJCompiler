package rs.ac.bg.etf.pp1;

import rs.etf.pp1.symboltable.concepts.Obj;
import rs.etf.pp1.symboltable.concepts.Struct;
import rs.etf.pp1.symboltable.structure.SymbolDataStructure;

public class StructExtended extends Struct {
	
	public static final int Record = 8;
	
	public StructExtended(int kind) {
		super(kind);
	}

	public StructExtended(int kind, Struct elemType) {
		super(kind, elemType);
	}

	public StructExtended(int kind, SymbolDataStructure members) {
		super(kind, members);
	}
	
	public boolean isRefType() {
		return super.isRefType() || getKind() == Record;
	}
	
	public boolean equals(Struct other) {
		if (getKind() == Record) 
			return other.getKind() == Record && getNumberOfFields() == other.getNumberOfFields()
				&& Obj.equalsCompleteHash(getMembersTable(), other.getMembersTable());
		
		return super.equals(other);
	}
}
