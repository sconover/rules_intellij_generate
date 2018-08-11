A package relationship established by any of the three types of dependencies:
  data,
  deps,
  srcs
Will result in the intellij integration determining that there exists a dependency relationship
among the intellij modules that correspond to the bazel packages in question.

https://docs.bazel.build/versions/master/build-ref.html#types_of_dependencies

This also demonstrates how the "depdendency attrs" may be changed / customized.
