package(default_visibility = ["//05_annotation_processor:__subpackages__"])

load("@rules_intellij_generate//:def.bzl",
    "intellij_source_java_plugin",
    "MAVEN_STANDARD_JAVA_SOURCE_FOLDER_MAP",
    "MAVEN_STANDARD_RESOURCE_FOLDER")

intellij_source_java_plugin(
    name="annotation_processor",
    source_folder_to_wildcard_map=MAVEN_STANDARD_JAVA_SOURCE_FOLDER_MAP,
    processor_class = "annotation_processor.class_generator.ClassGeneratorAnnotationProcessor",
    resource_folder_to_wildcard_map = {MAVEN_STANDARD_RESOURCE_FOLDER: "META-INF/services/javax.annotation.processing.Processor"},
    generates_api = 1, # must be specified or dependent targets will not see gen'd classfiles
)

load("@rules_intellij_generate//:def.bzl", "intellij_iml")
intellij_iml(
    name = "iml",
    java_source=":annotation_processor",
)
