package annotation_processor.class_generator;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Set;

import static java.lang.String.format;

@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes("annotation_processor.class_generator.GenClass")
public class ClassGeneratorAnnotationProcessor extends AbstractProcessor {
  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    for (TypeElement annotation : annotations) {
      for (Element annotatedElement : roundEnv.getElementsAnnotatedWith(annotation)) {
        GenClass genClassAnnotation = annotatedElement.getAnnotation(GenClass.class);
        if (genClassAnnotation == null) {
          continue;
        }
        String genClassName = genClassAnnotation.genClassName();
        Name annotatedClass = annotatedElement.getSimpleName();
        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE,
          format("found GenClass annotation on class '%s', " +
            "generating class '%s'", annotatedClass, genClassName));
        try {
          JavaFileObject builderFile = processingEnv.getFiler().createSourceFile("somepackage." + genClassName);
          try (PrintWriter out = new PrintWriter(builderFile.openWriter())) {
            out.write("package somepackage;\n");
            out.write("\n");
            out.write(format("public class %s {\n", genClassName));
            out.write("  public int foo = 77;\n");
            out.write("}\n");
          }
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      }
    }
    return false;
  }
}