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
  })
