IntellijModuleConfig = provider(
    fields = {
        "iml_type": "the type of iml file to use, defined in the iml_types xml file",
        "module_name_override": "optional string that overrides the default module name",
    },
)

def _impl(ctx):
    return [IntellijModuleConfig(
        iml_type = ctx.attr.iml_type,
        module_name_override = ctx.attr.module_name_override,
    )]

intellij_module = rule(
    implementation = _impl,
    attrs = {
        "iml_type": attr.string(
            mandatory = True,
            doc = """
                                The name of a key in the iml types xml file. rules_intellij_generate.py will
                                look up this entry, and use its xml contents as the basis of the Intellij module
                                file for this bazel package/Intellij module.
                                """,
        ),
        "module_name_override": attr.string(
            default = "",
            doc = """
                                            By default, the Intellij module name is the directory name of the package
                                            (a directory contining a BUILD file). If another module name is deisred,
                                            or (in particular), if there would otherwise be duplicate-named modules,
                                            a situation not allowed by Intellij.
                                            """,
        ),
    },
)
