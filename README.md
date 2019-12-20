# rules_intellij_generate

Alpha: rule definitions and behavior may change on short notice.

Examines your bazel build and generates a complete set of Intellij project configuration files. This is an alternative 
approach vs [bazelbuild/intellij](https://github.com/bazelbuild/intellij), and akin to the old
[pants/idea](https://github.com/pantsbuild/pants/blob/d30cca1e0ecb9cc0e1b7e2cd0ff6e7e077e62a52/src/python/pants/backend/project_info/tasks/idea_gen.py) integration.

- _`**/*.iml`_: Intellij module files are based on iml templates under your control, to which this rule adds module and 
jar dependency entries based on examination of your bazel build target relationships.
- _`.idea/modules.xml`_: Automatically creates the Intellij file used to identify all modules in your project.
- _`.idea/workspace.xml`_: “component” entries you define will be placed in your workspace.xml file.
- _`.idea/**/*.xml`_: Intellij project-level files, such as run configurations and source formatting rules, may be 
committed to your repo and will be placed under the .idea directory, meaning these may be easily shared across a team.

## Example usage

This repo is split into the [core rules](rules), and a [series of scenarios](scenarios) meant to show how the rules may be typically 
applied, that also form the basis of the [scenario tests](scenarios/scenario_tests/pytest). There is a README in the 
root of each scenario with a brief description of its purpose.

## Background and Motivation

bazel/intellij originates from Google’s internal approach to Intellij integration. It’s based on a plug-in that puts 
bazel operations at the center of how the IDE does its work.

Many people use this plugin happily and successfully, but its bazel-first philosophy is not without its problems, 
very well-expressed in 
[this github issue (recommended reading)](https://github.com/bazelbuild/intellij/issues/179#issuecomment-350295025). 
Bazel/intellij maintainers state that exploitation
 of Intellij’s module system is a non-goal, whereas for this project it’s a key goal.

The rules_intellij_generate philosophy sees bazel and Intellij as equal partners interacting at arm’s length. It 
willingly countenances small compromises away from “bazel purity” so that (for example) the IDE’s code modularity 
features are useful in development. Intellij should work for teams that also use bazel, in the most efficient and 
fully-realized manner as possible.

## Features

- Uses bazel project configuration to generate an intellij project during the bazel build run
- Maps bazel packages to Intellij modules
- Maps bazel-specified jar dependencies to Intellij module jar library dependencies
- Integration point is plain old Intellij config files - there’s no Intellij plugin
- Express any Intellij module configuration/settings via an entry in iml-types.xml 
([example](scenarios/iml_types.xml))
- Support for distribution, from files in your workspace, of project-level Intellij xml config 
([example](scenarios/intellij_project_files))
- Reference to bazel build output locations in Intellij configuration possible via template variables

## Usage

Once `intellij_module` targets are defined, and an `intellij_project` target exists, run the `intellij_project`
target: this generates the intellij configuration files archive for your project, in sandboxed form. You will need
to run the `install_intellij_files_script`, found under `bazel-bin`, in order for these files to be installed
in your workspace.

You may want to try this out using the scenarios. Once installed, any scenario should be loadable into Intellij,
by opening up its directory in the IDE as a new project.

## Python

This project has a dependency on python. For now please make sure you have a python 3 interpreter that's findable 
by the core-bazel [default toolchain support for python](https://github.com/bazelbuild/rules_python/blob/master/proposals/2019-02-12-design-for-a-python-toolchain.md#default-toolchain).

If you are using pyenv, install a modern 3.X interpreter, and make it findable by the bazel/python default
toolchain support using:

```
pyenv global 3.7.0
```

Where "3.7.0" is the version of your python 3 interpreter.
