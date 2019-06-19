package whitesquare.glslcross.glslcompiler;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;

import static com.google.common.truth.Truth.assertThat;

public class CompilerTest {
    private GLSLCompiler compiler;

    @Before
    public void setUp() throws Exception {
        compiler = new GLSLCompiler();
    }

    @Test
    public void test0() {
        verify("/glsl/test0.glsl");
    }

    @Test
    public void test1() {
        verify("/glsl/test1.glsl");
    }

    @Test
    public void test2() {
        verify("/glsl/test2.glsl");
    }

    @Test
    public void test3() {
        verify("/glsl/test3.glsl");
    }

    @Test
    public void test4() {
        verify("/glsl/test4.glsl");
    }

    @Test
    public void test5() {
        verify("/glsl/test5.glsl");
    }

    private void verify(String s) {
        try {
            Path srcFile = getResource(s);

            String bytecodeName = srcFile.getFileName().toString().replace(".glsl", ".byte");
            List<String> expected = Files.readAllLines(srcFile.resolveSibling(bytecodeName));

            Path tempDir = Files.createTempDirectory("test");
            try {
                compiler.compile(srcFile, tempDir);

                List<String> actual = Files.readAllLines(tempDir.resolve(bytecodeName));

                assertThat(String.join("\n", actual)).isEqualTo(String.join("\n", expected));
            } finally {
                Files.walk(tempDir)
                        .sorted(Comparator.reverseOrder())
                        .map(Path::toFile)
                        .forEach(File::delete);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Path getResource(String name) {
        try {
            URL resource = CompilerTest.class.getResource(name);
            if (resource == null) {
                throw new RuntimeException(new FileNotFoundException(name));
            }
            return Paths.get(resource.toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException("Couldn't load " + name, e);
        }
    }
}
