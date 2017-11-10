# The subdirectory names for generated sources and generated test sources
# MUST be fixed and shared across iml generation and
# compiler.xml (annotation processor profile) generation,
# if we want these directories to be able to be marked as
# source/test roots.
GENERATED_SOURCES_SUBDIR="generated"
GENERATED_TEST_SOURCES_SUBDIR="generated_tests"

# see https://bazel.build/designs/skylark/declared-providers.html
install_script_provider = provider(
  doc = """Allows a target to provide a convenient installer script,
           to (for example) allow the user to put in place other
           target outputs, outside of the bounds of a bazel build
           run, and outside of the isolated build environment.

           Install scripts must:
             - Not perform desctructive actions by default (without --force specified)
             - Support specifcation of a single argument: --force. This is the
               signal that destructive actions may be performed.
        """,
  fields = {
    "install_script_file": "The install script.",
    "transitive_install_script_files": "Install scripts for parents of this target.",
  })

def path_relative_to_workspace_root(ctx, relative_path):
    return ctx.build_file_path.rstrip("/BUILD") + "/" + relative_path

def dir_relative_to_workspace_root(ctx):
    return ctx.build_file_path.rstrip("/BUILD")

def dot_idea_project_dir_relative_to_workspace_root(ctx):
    subdir = dir_relative_to_workspace_root(ctx)
    if subdir == "":
        return ".idea"
    else:
        return subdir + "/.idea"

def build_dirname(ctx):
    return ctx.build_file_path.rstrip("/BUILD").split("/")[-1]

transitive_iml_provider = provider(
    doc = "TODO",
    fields = {
        "transitive_imls": "TODO",
    }
)

# see https://bazel.build/designs/skylark/declared-providers.html
iml_info_provider = provider(
  doc = """The struct returned as the result of intellij_iml execution.
           Primarily allows "child" modules to get information about
           their "parent" modules, such as what the full set of immediate
           and transitive iml module parents is, and the libs those parents
           depend on.
        """,
  fields = {
    "iml_module_name": "This iml module's name.",
    "transitive_iml_module_names": "Names of all parent iml modules, of this iml module.",

    "iml_path_relative_to_workspace_root": "Path to where the iml file gets symlinked, relative to the workspace root.",
    "transitive_iml_paths_relative_to_workspace_root": "Paths to where the iml file gets symlinked, relative to the workspace root, for all parent iml's.",

    "transitive_imls": "TODO",
  })

intellij_java_source_info_provider = provider(
  doc = """TODO""",
  fields = {
    "source_folders": "TODO",
    "java_dep": "TODO",
    "transitive_intellij_java_sources": "TODO"
  })
