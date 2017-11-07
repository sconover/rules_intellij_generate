package annotation_processor.text_file_generator;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.JavaFileObject;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Set;

import static java.lang.String.format;

@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes("annotation_processor.class_generator.GenClass")
public class TextFileGeneratorAnnotationProcessor extends AbstractProcessor {
  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    for (TypeElement annotation : annotations) {
      for (Element annotatedElement : roundEnv.getElementsAnnotatedWith(annotation)) {
        GenTextFile genTextFileAnnotation = annotatedElement.getAnnotation(GenTextFile.class);
        if (genTextFileAnnotation == null) {
          continue;
        }
        String genTextFileName = genTextFileAnnotation.genTextFileName();
        Name annotatedClass = annotatedElement.getSimpleName();
        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE,
          format("found GenTextFile annotation on class '%s', " +
            "generating text file '%s'", annotatedClass, genTextFileName));
        try {
          FileObject builderFile = processingEnv.getFiler().createResource(
            StandardLocation.SOURCE_OUTPUT,
            "text_files",
            genTextFileName);
          try (PrintWriter out = new PrintWriter(builderFile.openWriter())) {
            out.write(format("content of text file %s\n", genTextFileName));
          }
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      }
    }
    return false;
  }
}