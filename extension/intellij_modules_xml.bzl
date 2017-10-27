def _impl(ctx):
    """TODO"""

    # TODO: only consider certain modules and their deps, like pants idea. perhaps use --define's as in
    # https://groups.google.com/forum/#!topic/bazel-discuss/bC-D__OJkgk
    #
    # "--define=<a 'name=value' assignment> multiple uses are accumulated
    #  Each --define option specifies an assignment for a build variable."

    # collect up all runfiles from providers from targets this target depends on
    transitive_runfiles = depset()
    for dep in ctx.attr.deps:
        transitive_runfiles += dep[DefaultInfo].files

    # TODO: select only iml files (?)

    iml_path_args = []
    for f in transitive_runfiles:
        iml_path_args += ["--iml-path", f.path]

    ctx.action(
        executable = ctx.executable._intellij_generate_modules_xml,
        arguments = iml_path_args + ["--modules-xml-path", ctx.outputs.modules_xml_file.path],
        inputs=transitive_runfiles,
        outputs=[
          ctx.outputs.modules_xml_file,
        ],
        progress_message="Generating intellij modules.xml file: %s" % ctx.outputs.modules_xml_file.path)

    ctx.file_action(
        output=ctx.outputs.executable,
        content="#!/bin/bash -e\necho HELLO\n",
        executable=True
    )

# this just auto-builds an xml based on the set of targets you specify at build time
# so, should it's just the result of some bazel build invocation (however you want to run that...
# multiple modules etc)
# this is how we will in effect achieve "pants idea module1 module2 etc..."

# TODO: naming: also needs to generate misc.xml, probably...

intellij_modules_xml_executable = rule(
    doc="""Put all iml files in the dependency graph in an intellij modules.xml 'project' file.""",
    executable=True,
    implementation=_impl,

    attrs={
        "_intellij_generate_modules_xml": attr.label(default=Label("//:intellij_generate_modules_xml"), executable=True, cfg="target"),
        "deps": attr.label_list(doc="dependencies which will be walked all the way back, " +
          "to discover intellij_iml targets, in order to generate the modules.xml content. " +
          "This should typically be a single intellij_iml -based target (the 'root module'), " +
          "especially in small-to-medium sized projects."),
    },

    outputs={"modules_xml_file": "%{name}_modules.xml"},
)
