package(default_visibility = ["//05_annotation_processor:__subpackages__"])

java_plugin(
    name = "annotation_processor",
    srcs = glob(["src/main/java/**/*.java"]),
    processor_class = "annotation_processor.text_file_generator.TextFileGeneratorAnnotationProcessor",
    resources = glob(["src/main/resources/META-INF/services/javax.annotation.processing.Processor"]),
)

load("@rules_intellij_generate//:def.bzl", "intellij_iml")
intellij_iml(
    name = "iml",
    compile_lib_deps = [":annotation_processor"],
)
