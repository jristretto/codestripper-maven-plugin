package io.github.jristretto.codestripperplugin;

import io.github.jristretto.codestripperplugin.StrippedCodeValidator;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import static java.util.stream.Collectors.joining;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.project.MavenProject;
import static org.assertj.core.api.Assertions.*;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.*;
import org.mockito.Mock;
import static org.mockito.Mockito.lenient;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 *
 * @author Pieter van den Hombergh {@code <pieter.van.den.hombergh@gmail.com>}
 */
@ExtendWith( MockitoExtension.class )
public class StrippedCodeValidatorTest extends StrippedCodeValidator {

    @Mock
    MavenProject mproject;

    @BeforeEach
    void setup() throws DependencyResolutionRequiredException, IOException {
        List<String> elements = Files.lines( Path.of( "testclasspath.txt" ) )
                .toList();
        lenient().when( mproject.getCompileClasspathElements() )
                .thenReturn( elements );
        lenient().when( mproject.getTestClasspathElements() )
                .thenReturn( elements );
        this.project = mproject;
    }

    //@Disabled("think TDD")
    @Test @DisplayName( "Get source files" )
    public void testGetSourceFiles() {
        String pck = getClass().getPackage().getName();
        Path sourceDir = Path.of( "src" );
        String[] sourceFiles = this.getSourceFiles( sourceDir );
        System.out.println( "sourceFiles = " + Arrays.toString( sourceFiles ) );
        assertThat( sourceFiles ).contains(
                "src/main/java/"
                        + pck.replaceAll("\\.", "/")
                        + "/StrippedCodeValidator.java",
                "src/test/java/"
                        + pck.replaceAll("\\.", "/")
                        + "/StrippedCodeValidatorTest.java"
        );
//        fail( "method SourceFiles reached end. You know what to do." );
    }

    //@Disabled("think TDD")
    @Test @DisplayName( "compiler args " )
    public void testCompilerArgs() throws DependencyResolutionRequiredException, IOException {
        Path sourceDir = Path.of( "src" );
        List<String> classpathElements = project.getCompileClasspathElements();
        String[] args = this.makeCompilerArguments( sourceDir, makeOutDir(),
                classpathElements );
        // cleanup
        this.outDir.toFile().delete();
        System.out.println( Arrays.stream( args ).collect( joining( " " ) ) );
        assertThat( args ).isNotEmpty();
//        fail( "method CompilerArgs reached end. You know what to do." );
    }

    //@Disabled("think TDD")
    @Test @DisplayName( "run the compiler" )
    public void testCompilerRun() throws IOException {

        io.github.jrsitretto.codestripper.CodeStripper.main( new String[]{} );
        ThrowableAssert.ThrowingCallable code = () -> {
            this.execute();
        };

        assertThatCode( code ).doesNotThrowAnyException();
//        fail( "method CompilerRun reached end. You know what to do." );
    }
}
