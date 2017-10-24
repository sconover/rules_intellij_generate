def _prepare_library_manifest_file_from_java_runtime_classpath_info(ctx, java_deps, manifest_file_name):
    """Given a bazel rule ctx, a list of java dependency labels,
       find all java jar libraries for the dependencies,
       and list them in a manifest file consisting of two columns:
       column 1: the bazel name for the library
       column 2: the path to the jar file
    """
    library_manifest_content = ""
    for dep in java_deps:
        for cp_item in dep.java.compilation_info.runtime_classpath.to_list():
            if cp_item.is_source:
                # I would LOVE to make aboslute paths to java libs RIGHT HERE, vs figure out the
                # paths via a java env hack, but I literally cannot. I object to this because:
                # a) what I'm doing here is safe - there is no practical problem with allowing outside references to
                #    files under execRoot, because execRoot is stable, and this has nothing to do with anything
                #    hermetically-relevant.
                # b) precedent has been set: genfiles dir, and files_to_run paths are both available in skylark-land.
                # Anyway, see the java code for the super-ugly hack to determine execRoot.
                library_manifest_content += "%s %s\n" % (cp_item.owner, cp_item.path)

    library_manifest_file = ctx.actions.declare_file(manifest_file_name)
    ctx.actions.write(output=library_manifest_file, content=library_manifest_content)
    return library_manifest_file

def _impl(ctx):
    """Based on ctx.attr inputs, invoke the iml-generating executable, and write the result to the designated iml path."""
    # print(ctx.var)

    sources_roots_args = []
    for sources_root_attr in ctx.attr.sources_roots:
        sources_roots_args.append("--sources-root")
        sources_roots_args.append(sources_root_attr)

    test_sources_roots_args = []
    for test_sources_root_attr in ctx.attr.test_sources_roots:
        test_sources_roots_args.append("--test-sources-root")
        test_sources_roots_args.append(test_sources_root_attr)

    main_library_manifest_file = _prepare_library_manifest_file_from_java_runtime_classpath_info(ctx, ctx.attr.source_deps, "main_libraries.manifest")
    test_library_manifest_file = _prepare_library_manifest_file_from_java_runtime_classpath_info(ctx, ctx.attr.test_deps, "test_libraries.manifest")

    ctx.action(
        executable = ctx.executable._intellij_generate,
        arguments = [
          "--content-root", ctx.build_file_path.replace("BUILD", ".")] + # consider making this overridable via a ctx.attr
          sources_roots_args +
          test_sources_roots_args + [
          "--main-libraries-manifest-path", main_library_manifest_file.path,
          "--test-libraries-manifest-path", test_library_manifest_file.path,
          "--iml-path", ctx.outputs.iml_file.path,
        ],
        inputs=[
            main_library_manifest_file,
            test_library_manifest_file
        ],
        outputs=[ctx.outputs.iml_file],
        progress_message="Generating intellij iml file: %s" % ctx.outputs.iml_file.path)

intellij_iml = rule(
    doc="""Generate an intellij iml file, containing:
            - module library entries that point to jar and module dependencies, as known by bazel
            - (user-specified) source and test roots, that default to the standard maven project layout
            (see: https://maven.apache.org/guides/introduction/introduction-to-the-standard-directory-layout.html)
            (TODO: document further)
        """,
    implementation=_impl,

    attrs={
      "_intellij_generate": attr.label(default=Label("//:intellij_generate"), executable=True, cfg="target"),

      "source_deps": attr.label_list(doc="java targets, whose dependencies will be used to build the MAIN library section of the iml."),
      "sources_roots": attr.string_list(default=["src/main/java"], doc="Intellij will mark each of these directories as a 'Sources Root'"),

      "test_deps": attr.label_list(doc="java targets, whose dependencies will be used to build the TEST library section of the iml."),
      "test_sources_roots": attr.string_list(default=["src/test/java"], doc="Intellij will mark each of these directories as a 'Test Sources Root'"),
    },

    outputs={"iml_file": "%{name}.iml"},
)