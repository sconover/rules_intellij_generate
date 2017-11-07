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
    return dir_relative_to_workspace_root(ctx) + "/.idea"

def build_dirname(ctx):
    return ctx.build_file_path.rstrip("/BUILD").split("/")[-1]
