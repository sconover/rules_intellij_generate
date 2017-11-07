load(":common.bzl", "GENERATED_SOURCES_SUBDIR", "GENERATED_TEST_SOURCES_SUBDIR", "install_script_provider")
load(":intellij_iml.bzl", "iml_info_provider") # see https://bazel.build/designs/skylark/declared-providers.html

def _impl(ctx):
    """Based on ctx.attr inputs, invoke the compiler.xml-generating executable,
       and write the result to the designated compiler.xml path."""

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

    shell_script_content = \
"""#!/bin/bash -e

if [ "$1" = "--force" ]; then
    rm -f compiler.xml
else
    if [ -f compiler.xml ]; then
        echo "Refusing to overwrite '$(pwd)/compiler.xml', use --force to override" > /dev/stderr
        exit 0
    fi
fi

ln -sf $(dirname $0)/%s compiler.xml
""" % ctx.outputs.compiler_xml_file.basename

    ctx.actions.write(
        output=ctx.outputs.compiler_xml_installer_script_file,
        content=shell_script_content,
        is_executable=True)

    return [install_script_provider(install_script_file=ctx.outputs.compiler_xml_installer_script_file)]

intellij_compiler_xml = rule(
    doc="""Given a mapping of iml module targets to profile names, generate an intellij compiler.xml file.

           This file contains annotation processor settings, which are visible in Preferences under
           "Build, Execution, Deployment">"Compiler">"Annotation Processors".

           The main challenge in this is taking the DAG of annotation processor information and
           mapping it to the way intellij represents annotation processor settings. It's best
           to go try out the aforementioned settings pane to understand this, but in short:

           - Intellij allows for definition of multiple (named) annotation processor "profiles".
           - Each profile may have one or more associated iml modules
             - An iml module may only be contained within one profile
           - Each profile may have many annotation processors. These are the processors that
             will be invoked for usages (e.g. annotated classes) in the given set of modules.

           This rule bridges java_plugin usages to intellij config - it does not provide any
           other help in making annotation processing work (e.g. as AutoService does).
           """,
    implementation=_impl,

    attrs={
        "_intellij_generate_compiler_xml": attr.label(default=Label("//private:intellij_generate_compiler_xml"), executable=True, cfg="target"),
        "iml_target_to_annotation_profile": attr.label_keyed_string_dict(doc=""),
    },

    outputs={
        "compiler_xml_file": "%{name}_compiler.xml",
        "compiler_xml_installer_script_file": "install_%{name}_intellij_compiler_xml.sh",
    },
)
