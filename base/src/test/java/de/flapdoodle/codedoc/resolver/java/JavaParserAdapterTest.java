package de.flapdoodle.codedoc.resolver.java;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.comments.BlockComment;
import com.github.javaparser.ast.comments.LineComment;
import com.github.javaparser.ast.stmt.TypeDeclarationStmt;
import com.github.javaparser.ast.visitor.GenericVisitor;
import com.github.javaparser.ast.visitor.GenericVisitorAdapter;

import de.flapdoodle.codedoc.common.Either;
import de.flapdoodle.codedoc.common.Error;

public class JavaParserAdapterTest {

	@Test
	public void cutBodyOfNode() {
		final String code = "public class Foo{ void bar() { x=2; }\n\n}";
		Either<CompilationUnit, Error> unit = JavaParserAdapter.parse(code);
		assertTrue(unit.isLeft());
		GenericVisitor<String, String> visitor=new GenericVisitorAdapter<String, String>() {
			@Override
			public String visit(MethodDeclaration n, String arg) {
				System.out.println(" - - > "+JavaParserAdapter.info(n));
				return JavaParserAdapter.bodyOf(code, n);
			}
			
			@Override
			public String visit(ClassOrInterfaceDeclaration n, String arg) {
				System.out.println(" - - > "+JavaParserAdapter.info(n));
				return super.visit(n, arg);
			}
		};
		String result = unit.left().accept(visitor,"foo");
		System.out.println(" --> "+result);
		
		System.out.println(JavaParserAdapter.tree(unit.left(), code, 0));
		
		
//		List<Node> children = unit.left().getChildrenNodes();
//		assertEquals(1,children.size());
//		Node all = children.get(0);
//		System.out.println(" -> "+all);
//		System.out.println(" --> "+all.getChildrenNodes());
	}
}
