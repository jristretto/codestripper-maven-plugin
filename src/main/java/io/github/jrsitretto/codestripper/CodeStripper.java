/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package io.github.jrsitretto.codestripper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;
import io.github.jristretto.linesprocessor.ProcessorFactory;

/**
 * strip and zip processor.
 *
 * @author Pieter van den Hombergh {@code <pieter.van.den.hombergh@gmail.com>}
 */
public class CodeStripper {

    public final void strip(String root) throws IOException {
//        var out = Path.of( "target/out" );
        var target = Path.of( "target" );
        var dotgit = Path.of( ".git" );
        try ( Zipper solution = new Zipper( "target/solution.zip" ); //
                  Zipper assignment = new Zipper( "target/assignment.zip" ); ) {
            Files.walk( Path.of( root ), Integer.MAX_VALUE )
                    .filter( f -> !Files.isDirectory( f ) )
                    //                .filter( f -> !f.startsWith( out ) )
                    .filter( f -> !f.startsWith( target ) )
                    .filter( f -> !f.startsWith( dotgit ) )
                    .filter( f -> !f.getFileName().toString().endsWith( "~" ) )
                    .sorted()
                    .forEach( f -> process( f, solution, assignment ) );
        } catch ( Exception ex ) {
            Logger.getLogger( CodeStripper.class.getName() )
                    .log( Level.SEVERE, null, ex );
        }
        // save file names for later zipping.
    }

    private void process(Path javaFile, Zipper solution, Zipper assignment) {
        var out = Path.of( "target/stripper-out" );
        Path targetFile = out.resolve( javaFile );
        var factory = new ProcessorFactory( javaFile );
        try {
            var lines = Files.lines( javaFile ).toList();
            solution.add( javaFile, lines );
            var result = lines.stream()
                    .map( factory::apply )
                    .flatMap( x -> x ) // flatten the result
                    //                    .map( l -> l + System.getProperty( "line.separator" ) )
                    .toList();

            assignment.add( javaFile, result );
            if ( !result.isEmpty() ) {
                Files.createDirectories( targetFile.getParent() );
                Files.write( targetFile, result );
            }
        } catch ( IOException ex ) {
            LOG.severe( ex.getMessage() );
        }
    }

    private static final Logger LOG = Logger.getLogger( CodeStripper.class
            .getName() );

    public static void main(String[] args) throws IOException {
        new CodeStripper().strip( "" );
    }
}
