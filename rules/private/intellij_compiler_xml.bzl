load(":constants.bzl", "GENERATED_SOURCES_SUBDIR", "GENERATED_TEST_SOURCES_SUBDIR")
load(":intellij_iml.bzl", "iml_info_provider") # see https://bazel.build/designs/skylark/declared-providers.html

def _impl(ctx):
    """TODO"""

    args = []

    for dep in ctx.attr.iml_target_to_annotation_profile.keys():
        args.append("--module-to-profile-mapping")
        args.append("%s=%s" % (dep.label.name, ctx.attr.iml_target_to_annotation_profile[dep]))

        # TODO: need motivating use case for test annotations. can/should test/compile be handled generically?

        # examine all dependencies of the module for annotation processors
        # (which come from dependencies on java_plugin's),
        # collect these up.
        all_annotation_processors = depset()
        for compile_lib_dep in (dep[iml_info_provider].compile_lib_deps): # TODO: include transitive module compile libs?
            all_annotation_processors += compile_lib_dep.java.annotation_processing.processor_classnames

        for annotation_processor in all_annotation_processors:
            args.append("--module-to-annotation-processor-mapping")
            args.append("%s=%s" % (dep.label.name, annotation_processor))

    ctx.action(
        executable = ctx.executable._intellij_generate_compiler_xml,
        arguments = args + [
            "--generated-sources-subdir", GENERATED_SOURCES_SUBDIR,
            "--generated-test-sources-subdir", GENERATED_TEST_SOURCES_SUBDIR,
            "--compiler-xml-path", ctx.outputs.compiler_xml_file.path,
        ],
        inputs=[],
        outputs=[
          ctx.outputs.compiler_xml_file,
        ],
        progress_message="Generating intellij compiler.xml file: %s" % ctx.outputs.compiler_xml_file.path)

intellij_compiler_xml = rule(
    doc="""TODO""",
    implementation=_impl,

    attrs={
        "_intellij_generate_compiler_xml": attr.label(default=Label("//private:intellij_generate_compiler_xml"), executable=True, cfg="target"),
        "iml_target_to_annotation_profile": attr.label_keyed_string_dict(doc=""),
    },

    outputs={"compiler_xml_file": "%{name}_compiler.xml"},
)
