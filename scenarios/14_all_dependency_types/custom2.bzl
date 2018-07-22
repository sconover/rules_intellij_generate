def _impl(ctx):
    pass

custom2 = rule(
    implementation = _impl,
    attrs = {
        "gorps": attr.label_list(),
    },
    outputs={},
)
