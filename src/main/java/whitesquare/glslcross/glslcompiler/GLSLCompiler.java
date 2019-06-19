package whitesquare.glslcross.glslcompiler;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import whitesquare.glslcross.ast.Unit;
import whitesquare.glslcross.ast.Variable;
import whitesquare.glslcross.ast.optimizers.ASTOptimizer;
import whitesquare.glslcross.ast.optimizers.ConstantFoldingOptimizer;
import whitesquare.glslcross.ast.optimizers.ConstantVariableInliner;
import whitesquare.glslcross.ast.optimizers.OrderOptimizer;
import whitesquare.glslcross.bytecode.Program;
import whitesquare.glslcross.bytecode.analyzer.StackAnalyzer;
import whitesquare.glslcross.bytecode.optimizers.*;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class GLSLCompiler {

	private void optimizeAST(Unit ast) {
		List<ASTOptimizer> astOptimizers = new ArrayList<>();
		
		astOptimizers.add(new OrderOptimizer());
		astOptimizers.add(new ConstantVariableInliner());
		astOptimizers.add(new ConstantFoldingOptimizer(ast.getType("int"), ast.getType("float")));
		
		ASTOptimizer orderOptimizer = new OrderOptimizer();
		orderOptimizer.optimize(ast);
		
		for (int i = 0; i < 32; i++) {
			boolean changes = false;
			for (ASTOptimizer astOptimizer : astOptimizers)
				changes |= astOptimizer.optimize(ast);

			System.out.println("After phase " + i);
			if (!changes) break;
		}
	}
	
	private void optimize(Program program) {
		List<BytecodeOptimizer> bytecodeOptimizers = new ArrayList<>();
		
		bytecodeOptimizers.add(new StoreLoadOptimizer());
		bytecodeOptimizers.add(new UnusedSlotOptimizer());
		bytecodeOptimizers.add(new StackOptimizer());
		bytecodeOptimizers.add(new CombinerOptimizer());
		bytecodeOptimizers.add(new BlockOptimizer());
		
		System.out.println("Before optimization: " + program.instructions.size() + " instr - " + program.maxSlots + " slots");
		
		for (int i = 0; i < 32; i++) {
			boolean changes = false;
			for (BytecodeOptimizer bytecodeOptimizer : bytecodeOptimizers) {
				changes |= bytecodeOptimizer.optimize(program);
			
				StackAnalyzer stackAnalyzer = new StackAnalyzer(false);
				if (!stackAnalyzer.analyze(program)) {
					System.out.println("Resulting program is invalid!!! (Phase " + i + " : " + BytecodeOptimizer.class.getName() + ")");
					changes = true;
					break;
				}
			}
			
			System.out.println("After phase " + i + ": " + program.instructions.size() + " instr - " + program.maxSlots + " slots");
			if (!changes) break;
		}
	}
	
	public void compile(Path srcFile) {
		compile(srcFile, srcFile.getParent());
	}

	public void compile(Path srcFile, Path destDir) {
		System.out.println("Compiling: " + srcFile);
		
		LogWriter log = new LogWriter();
		
		try {
			CharStream stream = CharStreams.fromPath(srcFile);
			GLSLLexer lexer = new GLSLLexer(stream);
			GLSLParser parser = new GLSLParser(new CommonTokenStream(lexer));
			parser.setLog(log);
			parser.glsl();
			Unit unit = parser.getUnit();
			
			if (log.errors > 0) {
				System.out.println("Errors were found");
				return;
			}
			Variables variables = parser.getVariables();
			Variable tempVar = variables.add("__tempf", unit.getType("vec4"), false);
			
			unit.print(">");
			
			//optimizeAST(unit);
			
			BytecodeWriter bytecodeWriter = new BytecodeWriter();
			BytecodeVisitor visitor = new BytecodeVisitor(bytecodeWriter, log, tempVar);
			unit.visit(visitor);
			
			bytecodeWriter.getProgram().setMaxSlots(variables.size());
			
			Program program = bytecodeWriter.getProgram();
			program.writeOut(destDir.resolve(srcFile.getFileName() + "_pre.byte"));
			
			StackAnalyzer stackAnalyzer = new StackAnalyzer(true);
			if (!stackAnalyzer.analyze(program)) {
				System.out.println("Resulting program is invalid!!! (Before optimization)");
				return;
			}

			program.setMaxStack(stackAnalyzer.maxStack);
			program.writeOut(destDir.resolve(srcFile.getFileName() + "_orig.byte"));
			
			optimize(program);
			
			StackAnalyzer stackAnalyzerOpt = new StackAnalyzer(true);
			boolean valid = stackAnalyzerOpt.analyze(program);
			
			System.out.println("Final output : " + program.instructions.size() + " instr - " + program.maxSlots + " slots");
			
			if (!valid) {
				System.out.println("Resulting program is invalid!!!");
			} else {
				program.setMaxStack(stackAnalyzerOpt.maxStack);
				program.writeOut(destDir.resolve(srcFile.getFileName().toString().replace(".glsl", ".byte")));
			}
			
			System.out.println("Done");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		if (args.length == 0) {
			System.err.println("Usage: GLSLCompiler input-file.glsl [...]");
			System.exit(1);
		}

		GLSLCompiler compiler = new GLSLCompiler();

		for (String arg : args) {
			compiler.compile(Paths.get(arg));
		}
	}
}
