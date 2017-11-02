This scenario simulates the issue I ran across in the course of using the
appengine bazel rules, in which the java servlet library is exported:

https://github.com/bazelbuild/rules_appengine/blob/master/appengine/BUILD#L2

This causes the servlet library to be available to AppEngine servlets. In
order to build a working intellij module, we therefore need that exported
jar's classes to be available to module code.

Also see the java_library documentation on exports:
https://docs.bazel.build/versions/master/be/java.html#java_library.exports

Open question: should java_library exports be exposed as intellij dependency exports,
or (current state) be expressed as simple library dependencies in child modules?
