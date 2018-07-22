In an earlier version of this library, Annotation processing had deep integration
with the intellij rules, however this (generating compiler.xml) turned out to be
of modest benefit, and in practice the end-user experience was not great.

In this newer version of the library, the approach is to just provide compiler.xml as
an automatically-copied project file. This scenario + test is kept as a demonstration
of how it works (as simple as it is).
