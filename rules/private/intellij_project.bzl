load(":common.bzl", "install_script_provider", "dot_idea_project_dir_relative_to_workspace_root")

def _impl(ctx):
    """Based on ctx.attr inputs, invoke the modules.xml-generating executable,
       and write the result to the designated modules.xml path."""

    dot_idea_project_dir=dot_idea_project_dir_relative_to_workspace_root(ctx)
    compiler_xml_installer=ctx.attr.intellij_compiler_xml != None

    ctx.actions.expand_template(
        output=ctx.outputs.project_installer_script_file,
        template=ctx.file._install_script_template_file,
        substitutions={
          "{{dot_idea_project_dir}}": dot_idea_project_dir,
          "{{compiler_xml_installer}}": ctx.attr.intellij_compiler_xml[install_script_provider].install_script_file.path if ctx.attr.intellij_compiler_xml != None else "NONE",
          "{{modules_xml_installer}}": ctx.attr.intellij_modules_xml[install_script_provider].install_script_file.path,
        },
        is_executable=True)

    # This needs to be advertised in some form, not sure what's best beyond this...
    print("To set up the '%s' intellij project, run '%s'" % (dot_idea_project_dir, ctx.outputs.project_installer_script_file.path))

    return [install_script_provider(install_script_file=ctx.outputs.project_installer_script_file)]

intellij_project = rule(
    doc="""Defines an intellij project, ultimately manifest as a .idea
    directory containing various intellij xml config files.""",
    implementation=_impl,

    attrs={
        "_install_script_template_file": attr.label(
            default=Label("//private:install_intellij_project.sh.template"), allow_files=True, single_file=True),
        "intellij_modules_xml": attr.label(
          doc="(Required) Target that generates the modules.xml file for this intellij project.",
          mandatory=True,
          providers=[install_script_provider]),
        "intellij_compiler_xml": attr.label(
          doc="(Optional) Target that generates the compiler.xml file, " +
            "which contains annotation processor config, for this intellij project.",
          mandatory=False,
          providers=[install_script_provider]),
    },

    outputs={
        "project_installer_script_file": "install_%{name}_intellij_project.sh",
    },
)
