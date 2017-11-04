load(":constants.bzl", "GENERATED_SOURCES_SUBDIR", "GENERATED_TEST_SOURCES_SUBDIR")

# see https://bazel.build/designs/skylark/declared-providers.html
iml_info_provider = provider(
  doc = """The struct returned as the result of intellij_iml execution.
           Primarily allows "child" modules to get information about
           their "parent" modules, such as what the full set of immediate
           and transitive iml module parents is, and the libs those parents
           depend on.
        """,
  fields = {
    "compile_lib_deps": "This iml module's complie library dependencies.",
    "transitive_compile_lib_deps": "All complie library dependencies of all parent iml modules, of this iml module.",

    "iml_module_name": "This iml module's name.",
    "transitive_iml_module_names": "Names of all parent iml modules, of this iml module.",

    "iml_module_file": "This iml module's file.",
    "transitive_iml_module_files": "Files of all parent iml modules, of this iml module.",
  })

def _prepare_library_manifest_file_from_java_runtime_classpath_info(ctx, scope_to_java_deps, manifest_file_name, debug_log_lines):
    """Given a bazel rule ctx, a list of java dependency labels,
       find all java jar libraries for the dependencies,
       and list them in a manifest file consisting of two columns:
       column 1: the bazel name for the library
       column 2: the path to the jar file
       column 3: the library scope, either COMPILE or TEST
    """
    debug_log_lines.append("prepare library manifest file: '%s'" % manifest_file_name)
    library_manifest_content = ""
    for scope in scope_to_java_deps.keys():
        debug_log_lines.append("  process scope: '%s'" % scope)
        for dep in scope_to_java_deps[scope]:
            debug_log_lines.append("    process dependency: '%s'" % dep.label)
            if len(dep.java.compilation_info.compilation_classpath.to_list())>0:
                debug_log_lines.append("      java compilation classpath: '%s'" % dep.java.compilation_info.compilation_classpath.to_list())
            if len(dep.java.compilation_info.runtime_classpath.to_list())>0:
                debug_log_lines.append("      java runtime classpath: '%s'" % dep.java.compilation_info.runtime_classpath.to_list())
            if len(dep.java.transitive_deps.to_list()) > 0:
                debug_log_lines.append("      java transitive deps: '%s'" % dep.java.transitive_deps.to_list())
            if len(dep.java.transitive_exports.to_list()) > 0:
                debug_log_lines.append("      java transitive exports: '%s'" % dep.java.transitive_exports.to_list())
            if len(dep.java.transitive_runtime_deps.to_list()) > 0:
                debug_log_lines.append("      java transitive runtime deps: '%s'" % dep.java.transitive_runtime_deps.to_list())
            for cp_item in (dep.java.compilation_info.runtime_classpath + dep.java.transitive_runtime_deps).to_list():
                if cp_item.is_source:
                    debug_log_lines.append("      add classpath item: '%s'" % cp_item.path)
                    # I would LOVE to make aboslute paths to java libs RIGHT HERE, vs figure out the
                    # paths via a java env hack, but I literally cannot. I object to this because:
                    # a) what I'm doing here is safe - there is no practical problem with allowing outside references to
                    #    files under execRoot, because execRoot is stable, and this has nothing to do with anything
                    #    hermetically-relevant.
                    # b) precedent has been set: genfiles dir, and files_to_run paths are both available in skylark-land.
                    # Anyway, see the java code for the super-ugly hack to determine execRoot.
                    library_manifest_content += "%s %s %s\n" % (cp_item.owner, cp_item.path, scope)
                else:
                    debug_log_lines.append("      not adding classpath item, because is_source is False: '%s'" % cp_item.path)

    library_manifest_file = ctx.actions.declare_file(manifest_file_name)
    ctx.actions.write(output=library_manifest_file, content=library_manifest_content)
    debug_log_lines.append("  wrote %d bytes to '%s'" % (len(library_manifest_content), library_manifest_file.path))
    return library_manifest_file

def _prepare_module_manifest_file(ctx, scope_to_idea_module_deps, manifest_file_name, debug_log_lines):
    """Given a mapping of scope (COMPILE/TEST) to idea iml targets,
       find all .iml files that those targets have,
       and make a manifest file with two columns:
       column 1: the idea module name
         (idea module names are returned from the intellij_iml rule,
          by way of the iml_info_provider struct)
       column 2: the scope (e.g. COMPILE)
       """
    debug_log_lines.append("prepare module manifest file: '%s'" % manifest_file_name)
    module_manifest_content = ""
    for scope in scope_to_idea_module_deps.keys():
        debug_log_lines.append("  process scope: '%s'" % scope)
        for dep in scope_to_idea_module_deps[scope]:
            debug_log_lines.append("    process dependency: '%s'" % dep.label)

            all_iml_modules = dep[iml_info_provider].transitive_iml_module_names + [dep[iml_info_provider].iml_module_name]
            for iml_module_name in all_iml_modules:
                debug_log_lines.append("      including iml module: '%s'" % iml_module_name)
                module_manifest_content += "%s %s\n" % (iml_module_name, scope)

    module_manifest_file = ctx.actions.declare_file(manifest_file_name)
    ctx.actions.write(output=module_manifest_file, content=module_manifest_content)
    debug_log_lines.append("  wrote %d bytes to '%s'" % (len(module_manifest_content), module_manifest_file.path))
    return module_manifest_file

def _impl(ctx):
    """Based on ctx.attr inputs, invoke the iml-generating executable,
       and write the result to the designated iml path."""
    debug_log_lines = []
    debug_log_lines.append("START IML")
    debug_log_lines.append("ctx output iml_file: '%s'" % ctx.outputs.iml_file.path)
    debug_log_lines.append("ctx output iml_gen_debug_log: '%s'" % ctx.outputs.iml_gen_debug_log.path)

    sources_roots_args = []
    for sources_root_attr in ctx.attr.sources_roots:
        sources_roots_args.append("--sources-root")
        sources_roots_args.append(sources_root_attr)

    test_sources_roots_args = []
    for test_sources_root_attr in ctx.attr.test_sources_roots:
        test_sources_roots_args.append("--test-sources-root")
        test_sources_roots_args.append(test_sources_root_attr)

    resources_roots_args = []
    for resources_root_attr in ctx.attr.resources_roots:
        resources_roots_args.append("--resources-root")
        resources_roots_args.append(resources_root_attr)

    module_manifest_file = _prepare_module_manifest_file(ctx, {"COMPILE": ctx.attr.compile_module_deps}, "modules.manifest", debug_log_lines)

    library_manifest_file = _prepare_library_manifest_file_from_java_runtime_classpath_info(
        ctx,
        {"COMPILE": ctx.attr.compile_lib_deps, "TEST": ctx.attr.test_lib_deps},
        "libraries.manifest",
        debug_log_lines)

    # kwargs broken out from invocation site, for auditing purposes.
    kwargs = {
        "executable": ctx.executable._intellij_generate_iml,
        "arguments": [
            "--content-root", ctx.build_file_path.replace("BUILD", "."),
            "--production-output-dir", ctx.attr.production_output_dir,
            "--test-output-dir", ctx.attr.test_output_dir,
            "--generated-sources-dir", GENERATED_SOURCES_SUBDIR,
            "--generated-test-sources-dir", GENERATED_TEST_SOURCES_SUBDIR] + # consider making this overridable via a ctx.attr
            sources_roots_args +
            test_sources_roots_args +
            resources_roots_args + [
            "--modules-manifest-path", module_manifest_file.path,
            "--libraries-manifest-path", library_manifest_file.path,
            "--iml-path", ctx.outputs.iml_file.path,
        ],
        "inputs": [
            module_manifest_file,
            library_manifest_file,
        ],
        "outputs": [ctx.outputs.iml_file],
        "progress_message": "Generating intellij iml file: %s" % ctx.outputs.iml_file.path,
    }

    debug_log_lines.append("Invoke executble: %s" % kwargs["executable"].path)
    debug_log_lines.append("  Arguments:")
    for arg in kwargs["arguments"]:
        debug_log_lines.append("     " + arg)
    debug_log_lines.append("  Inputs:")
    for i in kwargs["inputs"]:
        debug_log_lines.append("     " + i.path)
    debug_log_lines.append("  Outputs:")
    for o in kwargs["outputs"]:
        debug_log_lines.append("     " + o.path)

    ctx.action(**kwargs)

    debug_log_lines.append("FINISHED IML")
    ctx.actions.write(output=ctx.outputs.iml_gen_debug_log, content="\n".join(debug_log_lines) + "\n")


    # == prepare and return iml_info_provider result ==

    transitive_compile_lib_deps = []
    for dep in ctx.attr.compile_module_deps:
        transitive_compile_lib_deps += dep[iml_info_provider].compile_lib_deps
        transitive_compile_lib_deps += dep[iml_info_provider].transitive_compile_lib_deps

    transitive_iml_module_names = []
    for dep in ctx.attr.compile_module_deps:
        transitive_iml_module_names += [dep[iml_info_provider].iml_module_name]
        transitive_iml_module_names += dep[iml_info_provider].transitive_iml_module_names

    transitive_iml_module_files = []
    for dep in ctx.attr.compile_module_deps:
        transitive_iml_module_files += [dep[iml_info_provider].iml_module_file]
        transitive_iml_module_files += dep[iml_info_provider].transitive_iml_module_files

    return [
        iml_info_provider(
            compile_lib_deps=ctx.attr.compile_lib_deps,
            transitive_compile_lib_deps=transitive_compile_lib_deps,

            iml_module_name=ctx.attr.name,
            transitive_iml_module_names=transitive_iml_module_names,

            iml_module_file=ctx.outputs.iml_file,
            transitive_iml_module_files=transitive_iml_module_files,
        )]

intellij_iml = rule(
    doc="""Generate an intellij iml file, containing:
            - module library entries that point to jar and module dependencies, as known by bazel
            - (user-specified) source, test and resource roots, that default to the standard maven project layout
              (see: https://maven.apache.org/guides/introduction/introduction-to-the-standard-directory-layout.html)
            - production and test output directories, that default to the intellij locations, and also
              contain generated-source subdirectories, that are marked as source roots (naming of these is
              fixed for reasons stated in constants.bzl)
        """,
    implementation=_impl,

    attrs={
        "_intellij_generate_iml": attr.label(
            default=Label("//private:intellij_generate_iml"),
            executable=True,
            cfg="target"),

        "compile_module_deps": attr.label_list(doc="inteliij_iml targets, which will become COMPILE module dependencies in idea."),
        "compile_lib_deps": attr.label_list(doc="java targets, whose dependencies will be used to build the COMPILE library section of the iml."),
        "sources_roots": attr.string_list(
            default=["src/main/java"],
            doc="Intellij will mark each of these directories as a 'Sources Root'"),

        "test_lib_deps": attr.label_list(doc="java targets, whose dependencies will be used to build the TEST library section of the iml."),
        "test_sources_roots": attr.string_list(
            default=["src/test/java"],
            doc="Intellij will mark each of these directories as a 'Test Sources Root'"),

        "resources_roots": attr.string_list(
            default=["src/main/resources", "src/test/resources"],
            doc="Intellij will mark each of these directories as a 'Resources Root'"),

        "production_output_dir": attr.string(
            default="out/production",
            doc="Intellij output directory for production/main classes and resources. Path is relative to the module content root."),
        "test_output_dir": attr.string(
            default="out/test",
            doc="Intellij output directory for test classes and resources. Path is relative to the module content root."),
    },

    outputs={"iml_file": "%{name}.iml", "iml_gen_debug_log": "iml_gen_debug.log"},
)