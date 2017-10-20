# load(
#     "//skylib:path.bzl",
#     "runfile",
# )

def _impl(ctx):
  # You may use print for debugging.
  print("This rule does nothing")
  # print_foo()

  # ctx.actions.run(executable=ctx.executable.foo_bin)

intellij_generate = rule(implementation=_impl)

# https://github.com/bazelbuild/examples/tree/master/rules

# def _impl2(ctx):
#   print(dir(ctx))
#   f = ctx.new_file("works.txt")
#   ctx.action(
#       command = "echo 'hello' > %s" % (f),
#       outputs = [f]
#   )
#   return [DefaultInfo(files=depset([f]))]

# print_hello = rule(
#     implementation=_impl2,
#     executable=True,
#     output_to_genfiles = True,
# )


  # native.sh_binary(
  #     name = name,
  #     args = [native.package_name()],
  #     srcs = ["//rules:make-egg.sh"],
  #     data = data,
  # )

# def _execute(rctx, command_string, quiet):
#     return rctx.execute(["bash", "-c", command_string], timeout = rctx.attr._timeout, quiet = quiet)

# def _transitive_maven_jar_impl(rctx):
#     _validate_coordinates(rctx)
#     arguments = _create_arguments(rctx)
#     quiet = rctx.attr.quiet

#     jar_path = rctx.path(rctx.attr._generate_workspace_tool)

#     # execute the command
#     result = _execute(rctx, "java -jar %s %s" % (jar_path, arguments), quiet)
#     rctx.file('%s/BUILD' % rctx.path(''), '', False)

# transitive_maven_jar = repository_rule(
#         implementation = _transitive_maven_jar_impl,
#         attrs = {
#             "artifacts" : attr.string_list(default = [], mandatory = True),
#             "quiet" : attr.bool(default = False, mandatory = False),
#             "_timeout" : attr.int(default = MAX_TIMEOUT),
# 			"_generate_workspace_tool" : attr.label(executable = True, allow_files = True, cfg = "host", default = Label("//transitive_maven_jar:generate_workspace_deploy.jar"))
#         },
#         local = False,
# )

def _impl3(ctx):
  # output = ctx.outputs.out
  # input = ctx.file.file
  # The command may only access files declared in inputs.
  # ctx.actions.run_shell(
  #     # inputs=[input],
  #     outputs=ctx.outputs.executable,
  #     progress_message="Run Hello",
  #     command="echo 'hello hello from echo' > %s" % (output.path))

  # jar_path = rctx.path(rctx.attr._generate_workspace_tool)
  # print("files: %s" % ctx.files)
  # print(attr.label(executable = True, allow_files = True, cfg = "host", default = Label("//transitive_maven_jar:generate_workspace_deploy.jar")))

  ctx.file_action(
      output=ctx.outputs.executable,
      content="echo 'hello hello from echo'",
      executable=True
  )

print_hello = rule(
    implementation=_impl3,
    executable=True,
    # attrs={"foo":attr.label(executable = True, allow_files = True, cfg = "host", default = Label("//intellij_generate"))},
    # attrs={"file": attr.label(mandatory=True, allow_files=True, single_file=True)},
    # outputs={"out": "%{name}.hello"},
)