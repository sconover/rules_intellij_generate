Inspired by `pants idea`.

dotfile in, intellij config out
...plus facilities for adding other intellij prefs.

Primary Goals:
- plugin-less intellij development
  - from intellij's point of view it's "just a java project" or (say) "just a scala project"
- assume a typical maven/gradle-ish directory layout
  - src/main/java, src/main/test
  - relatively coarse-grained modules
- primary input is a dependency graph (dotfile), output is iml's
- allow for generating a subset of iml's, like pants idea
- testing strategy:
  - mini-projects in this repo - real bazel project and real intellij projects - express the expected-actual / want-got
  - perhaps some similar idea for testing java / javac output

Secondary goals:
- If possible, re-use classfiles that are a result of bazel javac's
- If possible, prevent unnecessary intellij re-indexing when switching branches

Implementation:
- java: simple, focused, few-dependency (stdlib + jcommander), well-unit-tested Main classes for making intellij stuff
- however, dotfile-to-iml should be pure-python and (ideally) independent of bazel depenendencies.

Review:
- run it by zundel, cheister, et al

Docs:
- Some short statement of the problem, then (very briefly!) the philosophy of these rules
- List trade-offs of this approach vs use of a plugin.
  - And, even, demonstrate both in example projects
