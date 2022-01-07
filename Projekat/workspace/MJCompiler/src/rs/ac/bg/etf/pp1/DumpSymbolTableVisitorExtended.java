package rs.ac.bg.etf.pp1;

import rs.etf.pp1.symboltable.concepts.Obj;
import rs.etf.pp1.symboltable.concepts.Struct;
import rs.etf.pp1.symboltable.visitors.DumpSymbolTableVisitor;

public class DumpSymbolTableVisitorExtended extends DumpSymbolTableVisitor {
	public void visitStructNode(Struct structToVisit) {
		if (structToVisit.getKind() == Struct.Array && structToVisit.getElemType().getKind() == Struct.Bool) {
			output.append("Arr of bool");
			return;
		}
		
		switch (structToVisit.getKind()) {
			case Struct.Bool:
				output.append("bool");
				break;
			default:
				super.visitStructNode(structToVisit);
		}
	}
}
