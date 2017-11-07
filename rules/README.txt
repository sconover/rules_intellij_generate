To set up the intellij project:

- run: bazel build //...
- run the "private/.idea" setup script, that's printed to stdout.
- launch the intellij project, per the instructions printed to stdout.

Sample:

bazel build //...
DEBUG: To set up the 'private/.idea' intellij project, run 'bazel-out/darwin_x86_64-fastbuild/bin/private/install_rulesproj_intellij_project.sh'.
INFO: Found 9 targets...
INFO: Elapsed time: 0.162s, Critical Path: 0.00s

bazel-out/darwin_x86_64-fastbuild/bin/private/install_rulesproj_intellij_project.sh --force
Launch using: idea private
