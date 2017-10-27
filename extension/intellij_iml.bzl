def _impl(ctx):
    sources_roots_args = []
    for sources_root_attr in ctx.attr.sources_roots:
        sources_roots_args.append("--sources-root")
        sources_roots_args.append(sources_root_attr)

    ctx.action(
        executable = ctx.executable._intellij_generate,
        arguments = [
          "--content-root", ctx.build_file_path.replace("BUILD", ".")] + # consider making this overridable via a ctx.attr
          sources_roots_args + [
          "--iml-path", ctx.outputs.iml_file.path,
        ],
        outputs=[ctx.outputs.iml_file],
        progress_message="Generating intellij iml file: %s" % ctx.outputs.iml_file.path)

intellij_iml = rule(
    implementation=_impl,

    # project layout defaults all follow from the maven standard directory layout for a java project,
    # see: https://maven.apache.org/guides/introduction/introduction-to-the-standard-directory-layout.html
    attrs={
      "_intellij_generate": attr.label(default=Label("//:intellij_generate"), executable=True, cfg="target"),
      "deps": attr.label_list(doc="java targets, whose dependencies will be used to build the library section of the iml."),
      "sources_roots": attr.string_list(default=["src/main/java"], doc="Intellij will mark each of these directories as a 'Sources Root'"),
    },

    outputs={"iml_file": "%{name}.iml"},
)
