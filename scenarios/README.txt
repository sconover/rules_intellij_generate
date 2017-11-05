To set up intellij projects:

- run: bazel build //...
- run the "all_scenario_projects" script, that's printed to stdout.
- launch any of the intellij projects, per the instructions printed to stdout.

Sample:

bazel build //...
DEBUG: To set up the '02_one_class_and_one_test/.idea' intellij project, run 'bazel-out/darwin_x86_64-fastbuild/bin/02_one_class_and_one_test/install_02proj_intellij_project.sh'.
DEBUG: To set up the '04_transitive_via_export/.idea' intellij project, run 'bazel-out/darwin_x86_64-fastbuild/bin/04_transitive_via_export/install_04proj_intellij_project.sh'.
DEBUG: To set up the 'scenario_tests/.idea' intellij project, run 'bazel-out/darwin_x86_64-fastbuild/bin/scenario_tests/install_scenariotestsproj_intellij_project.sh'.
DEBUG: To set up the '01_one_class/.idea' intellij project, run 'bazel-out/darwin_x86_64-fastbuild/bin/01_one_class/install_01proj_intellij_project.sh'.
DEBUG: To set up the '05_annotation_processor/.idea' intellij project, run 'bazel-out/darwin_x86_64-fastbuild/bin/05_annotation_processor/install_05proj_intellij_project.sh'.
DEBUG: To set up the '03_basic/.idea' intellij project, run 'bazel-out/darwin_x86_64-fastbuild/bin/03_basic/install_03proj_intellij_project.sh'.
DEBUG: To set up the intellij project group 'all_scenario_projects', run 'bazel-out/darwin_x86_64-fastbuild/bin/install_all_scenario_projects_intellij_project_group.sh'.
INFO: Found 66 targets...
INFO: Elapsed time: 0.273s, Critical Path: 0.00s

bazel-out/darwin_x86_64-fastbuild/bin/install_all_scenario_projects_intellij_project_group.sh --force
Launch using: idea 01_one_class
Launch using: idea 02_one_class_and_one_test
Launch using: idea 03_basic
Launch using: idea 04_transitive_via_export
Launch using: idea 05_annotation_processor
Launch using: idea scenario_tests
