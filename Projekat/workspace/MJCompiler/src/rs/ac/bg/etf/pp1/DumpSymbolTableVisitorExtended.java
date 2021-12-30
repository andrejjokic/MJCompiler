package rs.ac.bg.etf.pp1;

import rs.etf.pp1.symboltable.concepts.Obj;
import rs.etf.pp1.symboltable.concepts.Struct;
import rs.etf.pp1.symboltable.visitors.DumpSymbolTableVisitor;

public class DumpSymbolTableVisitorExtended extends DumpSymbolTableVisitor {
	public void visitStructNode(Struct structToVisit) {
		if (structToVisit.getKind() == StructExtended.Array && structToVisit.getElemType().getKind() == StructExtended.Record) {
			output.append("Arr of Record");
			return;
		}
		
		switch (structToVisit.getKind()) {
			case StructExtended.Bool:
				output.append("bool");
				break;
			case StructExtended.Record:
				output.append("Record [");
				for (Obj obj : structToVisit.getMembers()) {
					obj.accept(this);
				}
				output.append("]");
				break;
			default:
				super.visitStructNode(structToVisit);
		}
	}
}
