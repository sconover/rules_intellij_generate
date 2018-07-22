package(default_visibility = ["//05_annotation_processor:__subpackages__"])

load("@rules_intellij_generate//:def.bzl", "intellij_module")

java_plugin(
    name="annotation_processor",
    srcs = glob(["src/main/java/**/*.java"]),
    processor_class = "annotation_processor.text_file_generator.TextFileGeneratorAnnotationProcessor",
    resources = ["src/main/resources/META-INF/services/javax.annotation.processing.Processor"],
)

intellij_module(name="iml", iml_type="java-maven-style-with-resources")