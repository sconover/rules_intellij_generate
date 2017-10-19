def _impl(ctx):
  # You may use print for debugging.
  print("This rule does nothing")

intellij_generate = rule(implementation=_impl)
