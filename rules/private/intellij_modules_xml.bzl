load(":intellij_iml.bzl", "iml_info_provider")

def _impl(ctx):
    """Based on ctx.attr inputs, invoke the modules.xml-generating executable,
       and write the result to the designated modules.xml path."""

    # TODO: only consider certain modules and their deps, like pants idea. perhaps use --define's as in
    # https://groups.google.com/forum/#!topic/bazel-discuss/bC-D__OJkgk
    #
    # "--define=<a 'name=value' assignment> multiple uses are accumulated
    #  Each --define option specifies an assignment for a build variable."

    # collect up all iml files from all parent modules
    iml_files = depset()
    for dep in ctx.attr.deps:
        iml_files += [dep[iml_info_provider].iml_module_file]
        for f in dep[iml_info_provider].transitive_iml_module_files:
            iml_files += dep[iml_info_provider].transitive_iml_module_files

    iml_path_args = []
    for f in iml_files:
        iml_path_args += ["--iml-path", f.path]

    ctx.action(
        executable=ctx.executable._intellij_generate_modules_xml,
        arguments=iml_path_args + ["--modules-xml-path", ctx.outputs.modules_xml_file.path],
        inputs=iml_files,
        outputs=[
          ctx.outputs.modules_xml_file,
        ],
        progress_message="Generating intellij modules.xml file: %s" % ctx.outputs.modules_xml_file.path)

    shell_script_content = \
"""#!/bin/bash -e

if [ "$1" = "--force" ]; then
    rm -f modules.xml
else
    if [ -f modules.xml ]; then
        echo "Refusing to overwrite $(pwd)/modules.xml" > /dev/stderr
        exit 0
    fi
fi

ln -s $(dirname $0)/%s modules.xml
""" % ctx.outputs.modules_xml_file.basename

    ctx.actions.write(
        output=ctx.outputs.modules_xml_installer_script_file,
        content=shell_script_content,
        is_executable=True)

# this just auto-builds an xml based on the set of targets you specify at build time
# so, should it's just the result of some bazel build invocation (however you want to run that...
# multiple modules etc)
# this is how we will in effect achieve "pants idea module1 module2 etc..."

# TODO: naming: also needs to generate misc.xml, probably...

intellij_modules_xml = rule(
    doc="""Put all iml files in the dependency graph in an intellij modules.xml 'project' file.""",
    implementation=_impl,

    attrs={
        "_intellij_generate_modules_xml": attr.label(default=Label("//private:intellij_generate_modules_xml"), executable=True, cfg="target"),
        "deps": attr.label_list(doc="dependencies which will be walked all the way back, " +
          "to discover intellij_iml targets, in order to generate the modules.xml content."),
    },

    outputs={
        "modules_xml_file": "%{name}_modules.xml",
        "modules_xml_installer_script_file": "install_%{name}_modules_xml.sh",
    },
)
