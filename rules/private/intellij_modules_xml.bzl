load(":intellij_iml.bzl", "iml_info_provider")
load(":common.bzl", "install_script_provider", "path_relative_to_workspace_root",
    "dot_idea_project_dir_relative_to_workspace_root", "dir_relative_to_workspace_root")

def _impl(ctx):
    """Based on ctx.attr inputs, invoke the modules.xml-generating executable,
       and write the result to the designated modules.xml path."""

    all_iml_paths_relative_to_workspace_root = []
    for dep in ctx.attr.deps:
        all_iml_paths_relative_to_workspace_root.append(dep[iml_info_provider].iml_path_relative_to_workspace_root)
        all_iml_paths_relative_to_workspace_root.extend(dep[iml_info_provider].transitive_iml_paths_relative_to_workspace_root)

    iml_paths_relative_to_workspace_root = []
    for iml_path in all_iml_paths_relative_to_workspace_root:
        if iml_path not in iml_paths_relative_to_workspace_root:
            iml_paths_relative_to_workspace_root.append(iml_path)

    idea_project_dir = dir_relative_to_workspace_root(ctx)
    iml_path_args = []
    for p in iml_paths_relative_to_workspace_root:
        # use replace, not lstrip - lstrip is over-aggressive, and appears to have a bug (and as of this writing, the lstrip example also has a bug)
        iml_path_relative_to_project_dir = p if idea_project_dir == "" else p.replace(idea_project_dir + "/", "", 1)
        iml_path_args += ["--iml-path", iml_path_relative_to_project_dir]

    iml_installer_script_files = []
    for dep in ctx.attr.deps:
        iml_installer_script_files.append(dep[install_script_provider].install_script_file)
        iml_installer_script_files.extend(dep[install_script_provider].transitive_install_script_files)

    ctx.action(
        executable=ctx.executable._intellij_generate_modules_xml,
        arguments=iml_path_args + ["--modules-xml-path", ctx.outputs.modules_xml_file.path],
        inputs=iml_installer_script_files,
        outputs=[
          ctx.outputs.modules_xml_file,
        ],
        progress_message="Generating intellij modules.xml file: %s" % ctx.outputs.modules_xml_file.path)

    shell_script_lines = [
        "#!/bin/bash -e",
        "",
        "if [ ! -f \"$(pwd)/WORKSPACE\" ]; then",
        "    echo \"This script must be run from the workspace root, please change to that directory and re-run.\" > /dev/stderr",
        "    exit 1",
        "fi",
        "",
        "# Install symlinks to all gen'd iml files:" ] + [s.path + " \"$1\"" for s in iml_installer_script_files] + [
        "",
        "original_path=$(pwd)",
        "this_dir=$(dirname $0)",
        "trap \"{ cd ${original_path}; }\" EXIT",
        "",
        "cd %s" % dot_idea_project_dir_relative_to_workspace_root(ctx),
        "",
        "if [ \"$1\" = \"--force\" ]; then",
        "    rm -f modules.xml",
        "else",
        "    if [ -f modules.xml ]; then",
        "        echo \"Refusing to overwrite '$(pwd)/modules.xml', use --force to override\" > /dev/stderr",
        "        exit 0",
        "    fi",
        "fi",
        "",
        "ln -sf $original_path/$this_dir/%s modules.xml" % ctx.outputs.modules_xml_file.basename,
        "",
        "trap - EXIT"
    ]

    ctx.actions.write(
        output=ctx.outputs.modules_xml_installer_script_file,
        content="\n".join(shell_script_lines) + "\n",
        is_executable=True)

    return [install_script_provider(install_script_file=ctx.outputs.modules_xml_installer_script_file)]
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
        "modules_xml_installer_script_file": "install_%{name}_intellij_modules_xml.sh",
    },
)
