load(":common.bzl", "iml_info_provider", "transitive_iml_provider", "intellij_java_source_info_provider")

def _impl(ctx):
    """TODO"""

    transitive_imls = []
    for dep in ctx.attr.deps:
        if iml_info_provider in dep:
            transitive_imls.append(dep)
        if transitive_iml_provider in dep:
            transitive_imls += dep[transitive_iml_provider].transitive_imls

    transitive_intellij_java_sources = []
    for dep in ctx.attr.deps:
        if intellij_java_source_info_provider in dep:
            transitive_intellij_java_sources += [dep]
            transitive_intellij_java_sources += dep[intellij_java_source_info_provider].transitive_intellij_java_sources

    # TODO: convert intellij_iml to use JavaInfo instead of the (old) .java
    # This struct makes this rule usable anywhere any java rule can go,
    # e.g. in a list of java_library deps
    return struct(
        providers=[
          ctx.attr.java_dep[JavaInfo],
          intellij_java_source_info_provider(
            source_folders=ctx.attr.source_folders,
            java_dep=ctx.attr.java_dep,
            transitive_intellij_java_sources=transitive_intellij_java_sources,
          ),
          transitive_iml_provider(
              transitive_imls=transitive_imls,
          )
        ],
        java=ctx.attr.java_dep.java
    )

_intellij_java_source = rule(
    doc="""TODO""",
    implementation=_impl,

    attrs={
        "source_folders": attr.string_list(doc=""),
        "java_dep": attr.label(doc=""),
        "deps": attr.label_list(doc=""),
    },
)

def glob_from_intellij_source_folder_to_wildcard_map(source_folder_map):
    globs = []
    for source_folder in source_folder_map.keys():
        glob_wildcard = source_folder_map[source_folder]
        globs.append(source_folder + "/" + glob_wildcard)
    return native.glob(globs)

def intellij_source_java_library(
    name=None,
    source_folder_to_wildcard_map={},
    deps=[],
    exports=[]):

    private_java_library_name = "_" + name
    native.java_library(
        name = private_java_library_name,
        srcs = glob_from_intellij_source_folder_to_wildcard_map(source_folder_to_wildcard_map),
        deps=deps,
        exports=exports,
    )
    _intellij_java_source(
        name=name,
        source_folders=source_folder_to_wildcard_map.keys(),
        java_dep=":" + private_java_library_name,
        deps=deps,
    )

# TODO: factor out duplication in a sensible way when we have like 3 of these
def intellij_source_java_plugin(
    name=None,
    source_folder_to_wildcard_map={},
    deps=[],
    resource_folder_to_wildcard_map={},
    processor_class=None,
    generates_api=0):

    private_java_library_name = "_" + name
    native.java_plugin(
        name = private_java_library_name,
        srcs = glob_from_intellij_source_folder_to_wildcard_map(source_folder_to_wildcard_map),
        resources = glob_from_intellij_source_folder_to_wildcard_map(resource_folder_to_wildcard_map),
        processor_class=processor_class,
        generates_api=generates_api,
        deps=deps,
    )
    #TODO: resources
    _intellij_java_source(
        name=name,
        source_folders=source_folder_to_wildcard_map.keys(),
        java_dep=":" + private_java_library_name,
        deps=deps,
    )

MAVEN_STANDARD_RESOURCE_FOLDER="src/main/resources"

MAVEN_STANDARD_JAVA_SOURCE_FOLDER_MAP={"src/main/java":"**/*.java"}
MAVEN_STANDARD_JAVA_TEST_FOLDER_MAP={"src/test/java":"**/*.java"}

