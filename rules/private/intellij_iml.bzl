def _prepare_library_manifest_file_from_java_runtime_classpath_info(ctx, scope_to_java_deps, manifest_file_name):
    """Given a bazel rule ctx, a list of java dependency labels,
       find all java jar libraries for the dependencies,
       and list them in a manifest file consisting of two columns:
       column 1: the bazel name for the library
       column 2: the path to the jar file
       column 3: the library scope, either COMPILE or TEST
    """
    library_manifest_content = ""
    for scope in scope_to_java_deps.keys():
        for dep in scope_to_java_deps[scope]:
            for cp_item in dep.java.compilation_info.runtime_classpath.to_list():
                if cp_item.is_source:
                    # I would LOVE to make aboslute paths to java libs RIGHT HERE, vs figure out the
                    # paths via a java env hack, but I literally cannot. I object to this because:
                    # a) what I'm doing here is safe - there is no practical problem with allowing outside references to
                    #    files under execRoot, because execRoot is stable, and this has nothing to do with anything
                    #    hermetically-relevant.
                    # b) precedent has been set: genfiles dir, and files_to_run paths are both available in skylark-land.
                    # Anyway, see the java code for the super-ugly hack to determine execRoot.
                    library_manifest_content += "%s %s %s\n" % (cp_item.owner, cp_item.path, scope)

    library_manifest_file = ctx.actions.declare_file(manifest_file_name)
    ctx.actions.write(output=library_manifest_file, content=library_manifest_content)
    return library_manifest_file

def _prepare_module_manifest_file(ctx, scope_to_idea_module_deps, manifest_file_name):
    """TODO"""

    module_manifest_content = ""
    for scope in scope_to_idea_module_deps.keys():
        for dep in scope_to_idea_module_deps[scope]:
            # intellij rule: module name is always the name of the iml file.
            # use iml file list to build module name list
            #
            # Note: this makes use of the provider runfiles we return from the intellij_iml rule impl
            for f in dep[DefaultInfo].default_runfiles.files.to_list():
                if f.basename.endswith(".iml"):
                    intellij_module_name = f.basename.rstrip(".iml")
                    module_manifest_content += "%s %s\n" % (intellij_module_name, scope)

    module_manifest_file = ctx.actions.declare_file(manifest_file_name)
    ctx.actions.write(output=module_manifest_file, content=module_manifest_content)
    return module_manifest_file

def _impl(ctx):
    """Based on ctx.attr inputs, invoke the iml-generating executable, and write the result to the designated iml path."""
    sources_roots_args = []
    for sources_root_attr in ctx.attr.sources_roots:
        sources_roots_args.append("--sources-root")
        sources_roots_args.append(sources_root_attr)

    test_sources_roots_args = []
    for test_sources_root_attr in ctx.attr.test_sources_roots:
        test_sources_roots_args.append("--test-sources-root")
        test_sources_roots_args.append(test_sources_root_attr)

    module_manifest_file = _prepare_module_manifest_file(ctx, {"COMPILE": ctx.attr.compile_module_deps}, "modules.manifest")

    library_manifest_file = _prepare_library_manifest_file_from_java_runtime_classpath_info(
        ctx,
        {"COMPILE": ctx.attr.compile_lib_deps, "TEST": ctx.attr.test_lib_deps},
        "libraries.manifest")

    ctx.action(
        executable = ctx.executable._intellij_generate_iml,
        arguments = [
            "--content-root", ctx.build_file_path.replace("BUILD", ".")] + # consider making this overridable via a ctx.attr
            sources_roots_args +
            test_sources_roots_args + [
            "--modules-manifest-path", module_manifest_file.path,
            "--libraries-manifest-path", library_manifest_file.path,
            "--iml-path", ctx.outputs.iml_file.path,
        ],
        inputs=[
            module_manifest_file,
            library_manifest_file,
        ],
        outputs=[ctx.outputs.iml_file],
        progress_message="Generating intellij iml file: %s" % ctx.outputs.iml_file.path)

    # prepare all parent (compile-module-dependency) runfiles to be returned.
    # we will use this list in child modules to build a complete module dependency list, for
    # those iml's.
    parent_iml_files = []
    for dep in ctx.attr.compile_module_deps:
        parent_iml_files += dep[DefaultInfo].default_runfiles.files.to_list()

    # see: https://docs.bazel.build/versions/master/skylark/rules.html#runfiles
    # also make sure to read about providers: https://docs.bazel.build/versions/master/skylark/rules.html#providers
    runfiles = ctx.runfiles(
        # Add some files manually.
        files = parent_iml_files + [ctx.outputs.iml_file],
        # Collect runfiles from the common locations: transitively from srcs,
        # deps and data attributes.
        collect_default = True,
    )
    return [DefaultInfo(runfiles=runfiles)]

intellij_iml = rule(
    doc="""Generate an intellij iml file, containing:
            - module library entries that point to jar and module dependencies, as known by bazel
            - (user-specified) source and test roots, that default to the standard maven project layout
            (see: https://maven.apache.org/guides/introduction/introduction-to-the-standard-directory-layout.html)
            (TODO: document further)
        """,
    implementation=_impl,

    attrs={
        "_intellij_generate_iml": attr.label(default=Label("//private:intellij_generate_iml"), executable=True, cfg="target"),

        "compile_module_deps": attr.label_list(doc="inteliij_iml targets, which will become COMPILE module dependencies in idea."),
        "compile_lib_deps": attr.label_list(doc="java targets, whose dependencies will be used to build the COMPILE library section of the iml."),
        "sources_roots": attr.string_list(default=["src/main/java"], doc="Intellij will mark each of these directories as a 'Sources Root'"),

        "test_lib_deps": attr.label_list(doc="java targets, whose dependencies will be used to build the TEST library section of the iml."),
        "test_sources_roots": attr.string_list(default=["src/test/java"], doc="Intellij will mark each of these directories as a 'Test Sources Root'"),
    },

    outputs={"iml_file": "%{name}.iml"},
)
