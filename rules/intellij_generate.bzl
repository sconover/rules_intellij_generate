
def _intellij_generate(ctx):
  #output = ctx.new_file("one_class.iml")
  output = ctx.outputs.executable
  foo = Label("//extension:intellij_generate")
  print(dir(foo))
  print(dir(ctx.executable))
  print(foo.package)

  ctx.file_action(
      #inputs = [str(ctx.attr.lib)],
      output = output,
      #$(location //extension:intellij_generate)
      content = "echo '$(location)' > " + output.path,
      #mnemonic = "IntelliJGenerate",
      #progress_message = "Generating Idea File " + str(ctx.attr.lib),
  )

  runfiles = ctx.runfiles(collect_data = True)

  return [DefaultInfo(files=depset([output]), runfiles = runfiles)]

intellij_generate = rule(
    implementation = _intellij_generate,
    attrs = {
        "lib": attr.label(),
    },
    executable=True,
    output_to_genfiles=True,
)