load(":common.bzl", "install_script_provider")

def _impl(ctx):
    """Based on ctx.attr inputs, invoke the modules.xml-generating executable,
       and write the result to the designated modules.xml path."""

    sh_lines = [
        "#!/bin/bash -e",
        "",
        "if [ ! -f \"$(pwd)/WORKSPACE\" ]; then",
        "    echo \"This script must be run from the workspace root, please change to that directory and re-run.\" > /dev/stderr",
        "    exit 1",
        "fi",
        ""
    ]

    for project_dep in ctx.attr.intellij_projects:
        sh_lines.append(project_dep[install_script_provider].install_script_file.path + " \"$1\"")

    sh_lines.append("")

    ctx.actions.write(
        output=ctx.outputs.project_group_installer_script_file,
        content="\n".join(sh_lines),
        is_executable=True)

    # This needs to be advertised in some form, not sure what's best beyond this...
    print("To set up the intellij project group '%s', run '%s'" % (ctx.attr.name, ctx.outputs.project_group_installer_script_file.path))

intellij_project_group = rule(
    doc="""Convenience script for generating a set of intellij projects.""",
    implementation=_impl,

    attrs={
        "intellij_projects": attr.label_list(
          doc="(Required) Intellij project targets.",
          mandatory=True,
          providers=[install_script_provider]),
    },

    outputs={
        "project_group_installer_script_file": "install_%{name}_intellij_project_group.sh",
    },
)
