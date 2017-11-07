package(default_visibility = ["//05_annotation_processor:__subpackages__"])

java_plugin(
    name = "class_generator_java_plugin",
    srcs = glob(["src/main/java/**/*.java"]),
    processor_class = "annotation_processor.class_generator.ClassGeneratorAnnotationProcessor",
    resources = glob(["src/main/resources/META-INF/services/javax.annotation.processing.Processor"]),
    generates_api = 1, # must be specified or dependent targets will not see gen'd classfiles
)

load("@rules_intellij_generate//:def.bzl", "intellij_iml")
intellij_iml(
    name = "idea_class_generator_module",
    compile_lib_deps = [":class_generator_java_plugin"],
)
