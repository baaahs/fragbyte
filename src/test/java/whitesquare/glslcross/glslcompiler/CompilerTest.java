package whitesquare.glslcross.glslcompiler;

import org.junit.Test;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CompilerTest {
    @Test
    public void testCompilation() {
        GLSLCompiler compiler = new GLSLCompiler();
        Path path = getResource("/glsl/test0.glsl");
        compiler.compile(path, path.getParent());
    }

    private Path getResource(String name) {
        try {
            return Paths.get(CompilerTest.class.getResource(name).toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException("Couldn't load " + name, e);
        }
    }
}
