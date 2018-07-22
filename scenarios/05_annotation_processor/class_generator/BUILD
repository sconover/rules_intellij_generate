package(default_visibility = ["//05_annotation_processor:__subpackages__"])

load("@rules_intellij_generate//:def.bzl", "intellij_module")

java_plugin(
    name="annotation_processor",
    srcs = glob(["src/main/java/**/*.java"]),
    processor_class = "annotation_processor.class_generator.ClassGeneratorAnnotationProcessor",
    resources = ["src/main/resources/META-INF/services/javax.annotation.processing.Processor"],
    generates_api = 1, # must be specified or dependent targets will not see gen'd classfiles
)

intellij_module(name="iml", iml_type="java-maven-style-with-resources")