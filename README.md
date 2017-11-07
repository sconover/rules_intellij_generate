Use Intellij in "classic mode". Core intellij project files are generated via your bazel build.

Inspired by [pants idea](https://github.com/pantsbuild/pants/blob/d30cca1e0ecb9cc0e1b7e2cd0ff6e7e077e62a52/src/python/pants/backend/project_info/tasks/idea_gen.py)

Primary Goals:
- plugin-less intellij development
  - from intellij's point of view it's "just a java project" or (say) "just a scala project"
- assume a typical maven/gradle-ish directory layout
  - src/main/java, src/main/test
  - relatively coarse-grained modules
- testing: various real-ish scenarios are present in this project, and usable.
