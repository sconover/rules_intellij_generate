def _prepare_library_manifest_file_from_java_runtime_classpath_info(ctx, java_deps, manifest_file_name):
    library_manifest_content = ""
    for dep in java_deps:
        library_path_prefix = dep.files_to_run.runfiles_manifest.dirname + "/" + ctx.workspace_name
        for cp_item in dep.java.compilation_info.runtime_classpath.to_list():
            if cp_item.is_source:
                library_manifest_content += "%s %s/%s\n" % (cp_item.owner, library_path_prefix, cp_item.path)

    library_manifest_file = ctx.actions.declare_file(manifest_file_name)
    ctx.actions.write(output=library_manifest_file, content=library_manifest_content)
    return library_manifest_file

def _impl(ctx):
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
    implementation=_impl,

    # project layout defaults all follow from the maven standard directory layout for a java project,
    # see: https://maven.apache.org/guides/introduction/introduction-to-the-standard-directory-layout.html
    attrs={
      "_intellij_generate": attr.label(default=Label("//:intellij_generate"), executable=True, cfg="target"),

      "source_deps": attr.label_list(doc="java targets, whose dependencies will be used to build the MAIN library section of the iml."),
      "sources_roots": attr.string_list(default=["src/main/java"], doc="Intellij will mark each of these directories as a 'Sources Root'"),

      "test_deps": attr.label_list(doc="java targets, whose dependencies will be used to build the TEST library section of the iml."),
      "test_sources_roots": attr.string_list(default=["src/test/java"], doc="Intellij will mark each of these directories as a 'Test Sources Root'"),
    },

    outputs={"iml_file": "%{name}.iml"},
)
