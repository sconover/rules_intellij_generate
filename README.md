Use Intellij in "classic mode". Core intellij project files are generated via your bazel build.

Inspired by [pants idea](https://github.com/pantsbuild/pants/blob/d30cca1e0ecb9cc0e1b7e2cd0ff6e7e077e62a52/src/python/pants/backend/project_info/tasks/idea_gen.py)

Primary Goals:
- plugin-less intellij development
  - from intellij's point of view it's "just a java project" or (say) "just a scala project"
- assume a typical maven/gradle-ish directory layout
  - src/main/java, src/main/test
  - relatively coarse-grained modules
- testing: various real-ish scenarios are present in this project, and usable.

Bazel Target Naming Conventions

A lot of these targets are repeated across modules, and bazel module scoping
provides enough namespacing information that it's redundant and arguably
confusing to repeat the namespace in such target names. This system
also has the benefits of being consistent horizontally and therefore
is easily grep-able and so on.

- main code for module: "lib" (should it be java_lib?)
- test code for module: "test_lib" (should it be java_test_lib?)
- executable test tasks: "tests"
- proto: "the_module_name_proto": the one exception to not over-specifying targets. The practical consequence
here is that you will have meaningful-looking proto jar dependencies when viewing them in IDE module settings.
- java proto codegen: "java_proto"
- grpc: "java_grpc"
- idea iml module: "iml"
- idea modules.xml: "modules_xml"
- idea compiler.xml: "compiler_xml"
- idea project: "idea_project"

CI happen on Google Container Builder, to request access to the [Build History](https://console.cloud.google.com/gcr/builds?project=rules-intellij-generate) and build output, please contact the project maintainers directly, or file a Github Issue requesting access.
