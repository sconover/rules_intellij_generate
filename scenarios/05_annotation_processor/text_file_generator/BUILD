package(default_visibility = ["//05_annotation_processor:__subpackages__"])

java_plugin(
    name = "text_file_generator_java_plugin",
    srcs = glob(["src/main/java/**/*.java"]),
    processor_class = "annotation_processor.text_file_generator.TextFileGeneratorAnnotationProcessor",
    resources = glob(["src/main/resources/META-INF/services/javax.annotation.processing.Processor"]),
)

load("@rules_intellij_generate//:def.bzl", "intellij_iml")
intellij_iml(
    name = "idea_text_file_generator_module",
    compile_lib_deps = [":text_file_generator_java_plugin"],
)
