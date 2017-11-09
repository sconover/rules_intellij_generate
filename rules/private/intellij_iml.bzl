load(":common.bzl", "GENERATED_SOURCES_SUBDIR", "GENERATED_TEST_SOURCES_SUBDIR",
    "install_script_provider", "path_relative_to_workspace_root", "dir_relative_to_workspace_root", "build_dirname")

load(":common.bzl", "iml_info_provider", "transitive_iml_provider", "intellij_java_source_info_provider")

def _intellij_source_deps_including_transitive(all_deps):
    results = []
    for dep in all_deps:
        if intellij_java_source_info_provider in dep:
            if dep not in results:
                results.append(dep)
            for tdep in dep[intellij_java_source_info_provider].transitive_intellij_java_sources:
                if tdep not in results:
                    results.append(tdep)
    return results

def _intellij_iml_deps_including_transitive(dep):
    results = []
    if transitive_iml_provider in dep:
        for tdep in dep[transitive_iml_provider].transitive_imls:
            if tdep not in results:
                results.append(tdep)
    return results

def _compile_module_deps(ctx):
    if ctx.attr.java_source != None:
        return _intellij_iml_deps_including_transitive(ctx.attr.java_source)
    else:
        return []

def _prepare_transitive_iml_provider(ctx):
    if ctx.attr.java_source != None:
        return transitive_iml_provider(transitive_imls=_intellij_iml_deps_including_transitive(ctx.attr.java_source))
    else:
        return transitive_iml_provider(transitive_imls=[])

# with respect to intellij_java_source information, an iml module acts just like its java_source
def _prepare_intellij_java_source_info_provider(ctx):
    return ctx.attr.java_source[intellij_java_source_info_provider]

# should subtract out jars based on immediate compile-lib deps, and jars based on module deps
# perhaps more generally: if the project (source roots) contains all the source files for a jar dep, ignore than dep
# (This is an ongoing challenge, the solution in place probably isn't quite general enough,
#  it may well lead to unforeseen [bad] surprises. See code comments below.)
def _prepare_library_manifest_file_from_java_runtime_classpath_info(ctx, scope_to_java_deps, manifest_file_name, debug_log_lines):
    """Given a bazel rule ctx, a list of java dependency labels,
       find all java jar libraries for the dependencies,
       and list them in a manifest file consisting of two columns:
       column 1: the bazel name for the library
       column 2: the path to the jar file
       column 3: the library scope, either COMPILE or TEST
    """

    java_sources_label_set = {}
    for scope in scope_to_java_deps.keys():
        for dep in _intellij_source_deps_including_transitive(scope_to_java_deps[scope]):
            java_sources_label_set[dep[intellij_java_source_info_provider].java_dep.label] = dep.label

    debug_log_lines.append("prepare library manifest file: '%s'" % manifest_file_name)
    library_manifest_content = ""
    for scope in scope_to_java_deps.keys():
        debug_log_lines.append("  process scope: '%s'" % scope)
        for dep in scope_to_java_deps[scope]:
            debug_log_lines.append("    process dependency: '%s'" % dep.label)

            debug_log_lines.append("      is intellij java source?: " + str(dep[intellij_java_source_info_provider]!=None))

            all_cp_items = []
            if dep.java.compilation_info == None:
                debug_log_lines.append("      (no java compilation classpath found)")
            else:
                if len(dep.java.compilation_info.compilation_classpath.to_list())>0:
                    debug_log_lines.append("      java compilation classpath: '%s'" % dep.java.compilation_info.compilation_classpath.to_list())
                if len(dep.java.compilation_info.runtime_classpath.to_list())>0:
                    debug_log_lines.append("      java runtime classpath: '%s'" % dep.java.compilation_info.runtime_classpath.to_list())

                all_cp_items += dep.java.compilation_info.runtime_classpath.to_list()

            if len(dep.java.transitive_deps.to_list()) > 0:
                debug_log_lines.append("      java transitive deps: '%s'" % dep.java.transitive_deps.to_list())
            if len(dep.java.transitive_exports.to_list()) > 0:
                debug_log_lines.append("      java transitive exports: '%s'" % dep.java.transitive_exports.to_list())
            if len(dep.java.transitive_runtime_deps.to_list()) > 0:
                debug_log_lines.append("      java transitive runtime deps: '%s'" % dep.java.transitive_runtime_deps.to_list())

            all_cp_items += dep.java.transitive_runtime_deps.to_list()

            dependency_cp_items = depset()
            for cp_item in all_cp_items:
                if cp_item.owner in java_sources_label_set:
                    # This guards againt making the jar containing the classes
                    # that are source files in the module in question, part of
                    # the module's jar dependency list.
                    debug_log_lines.append("      not adding classpath item, because it is generated by an intellij java source dependency: '%s' dep='%s'" % (cp_item.path, java_sources_label_set[cp_item.owner]))
                else:
                    debug_log_lines.append("      add classpath item to depset (which will de-dup): '%s'" % cp_item.path)
                    dependency_cp_items += [cp_item]

            for cp_item in dependency_cp_items:
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

    all_iml_modules = []
    for scope in scope_to_idea_module_deps.keys():
        debug_log_lines.append("  process scope: '%s'" % scope)
        for dep in scope_to_idea_module_deps[scope]:
            debug_log_lines.append("    process dependency: '%s'" % dep.label)

            for m in (dep[iml_info_provider].transitive_iml_module_names + [dep[iml_info_provider].iml_module_name]):
                if m not in all_iml_modules:
                    all_iml_modules.append(m)

    for iml_module_name in all_iml_modules:
        debug_log_lines.append("      including iml module: '%s'" % iml_module_name)
        module_manifest_content += "%s %s\n" % (iml_module_name, scope)

    module_manifest_file = ctx.actions.declare_file(manifest_file_name)
    ctx.actions.write(output=module_manifest_file, content=module_manifest_content)
    debug_log_lines.append("  wrote %d bytes to '%s'" % (len(module_manifest_content), module_manifest_file.path))
    return module_manifest_file

def _iml_path_relative_to_workspace_root(ctx):
    return path_relative_to_workspace_root(ctx, _symlink_iml_name(ctx) + ".iml")

def _prepare_iml_info_provider_result(ctx):
    """This is the way parent iml module information is known by child modules,
       so that module and library dependencies can be properly generated for
       intellij files (i.e. the .iml's).
    """
    transitive_iml_module_names = []
    for dep in _compile_module_deps(ctx):
        transitive_iml_module_names += [dep[iml_info_provider].iml_module_name]
        transitive_iml_module_names += dep[iml_info_provider].transitive_iml_module_names

    transitive_iml_paths_relative_to_workspace_root = []
    for dep in _compile_module_deps(ctx):
        transitive_iml_paths_relative_to_workspace_root += [dep[iml_info_provider].iml_path_relative_to_workspace_root]
        transitive_iml_paths_relative_to_workspace_root += dep[iml_info_provider].transitive_iml_paths_relative_to_workspace_root

    return iml_info_provider(
        iml_module_name=_symlink_iml_name(ctx),
        transitive_iml_module_names=transitive_iml_module_names,

        iml_path_relative_to_workspace_root=_iml_path_relative_to_workspace_root(ctx),
        transitive_iml_paths_relative_to_workspace_root=transitive_iml_paths_relative_to_workspace_root,
    )

def _symlink_iml_name(ctx):
    return build_dirname(ctx) if ctx.attr.use_directory_name_as_symlink_name else ctx.attr.name

def _create_install_script_and_return_install_script_provider(ctx):
    """An installer script, that makes a symlink to the generated iml file."""

    ctx.actions.expand_template(
        output=ctx.outputs.iml_module_installer_script_file,
        template=ctx.file._install_script_template_file,
        substitutions={
          "{{iml_path_relative_to_workspace_root}}": _iml_path_relative_to_workspace_root(ctx),
          "{{source_iml_file_name}}": ctx.attr.name + ".iml",
          "{{dest_iml_file_name}}": _symlink_iml_name(ctx) + ".iml",
        },
        is_executable=True)

    transitive_install_script_files = []
    for dep in _compile_module_deps(ctx):
        transitive_install_script_files += [dep[install_script_provider].install_script_file]
        transitive_install_script_files += dep[install_script_provider].transitive_install_script_files

    return install_script_provider(
        install_script_file=ctx.outputs.iml_module_installer_script_file,
        transitive_install_script_files=transitive_install_script_files
    )

def _impl(ctx):
    """Based on ctx.attr inputs, invoke the iml-generating executable,
       and write the result to the designated iml path."""
    debug_log_lines = []
    debug_log_lines.append("START IML")
    debug_log_lines.append("ctx output iml_file: '%s'" % ctx.outputs.iml_file.path)
    debug_log_lines.append("ctx output iml_gen_debug_log: '%s'" % ctx.outputs.iml_gen_debug_log.path)

    sources_roots_args = []
    if ctx.attr.java_source != None:
        for source_folder in ctx.attr.java_source[intellij_java_source_info_provider].source_folders:
            sources_roots_args.append("--sources-root")
            sources_roots_args.append(source_folder)

    test_sources_roots_args = []
    if ctx.attr.test_java_source != None:
        for source_folder in ctx.attr.test_java_source[intellij_java_source_info_provider].source_folders:
            test_sources_roots_args.append("--test-sources-root")
            test_sources_roots_args.append(source_folder)

    resources_roots_args = []
    for resources_root_attr in ctx.attr.resources_roots:
        resources_roots_args.append("--resources-root")
        resources_roots_args.append(resources_root_attr)

    module_manifest_file = _prepare_module_manifest_file(ctx, {"COMPILE": _compile_module_deps(ctx)}, "modules.manifest", debug_log_lines)

    src_deps = []
    if ctx.attr.java_source != None:
        src_deps = [ctx.attr.java_source]

    test_deps = []
    if ctx.attr.test_java_source != None:
        test_deps = [ctx.attr.test_java_source]

    library_manifest_file = _prepare_library_manifest_file_from_java_runtime_classpath_info(
        ctx,
        {"COMPILE": src_deps, "TEST": test_deps},
        "libraries.manifest",
        debug_log_lines)

    # kwargs broken out from invocation site, for auditing purposes.
    kwargs = {
        "executable": ctx.executable._intellij_generate_iml,
        "arguments": [
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

    # TODO: convert intellij_iml to use JavaInfo instead of the (old) .java
    # This struct makes this rule usable anywhere any java rule can go,
    # e.g. in a list of java_library deps

    if ctx.attr.java_source!=None:
        return struct(
            java=ctx.attr.java_source.java,
            providers=[
                ctx.attr.java_source[JavaInfo],

                _prepare_iml_info_provider_result(ctx),
                _create_install_script_and_return_install_script_provider(ctx),
                _prepare_transitive_iml_provider(ctx),
                _prepare_intellij_java_source_info_provider(ctx),
            ],
        )
    else:
        return struct(
            providers=[
                _prepare_iml_info_provider_result(ctx),
                _create_install_script_and_return_install_script_provider(ctx),
                _prepare_transitive_iml_provider(ctx),
            ],
        )

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
        "_install_script_template_file": attr.label(
            default=Label("//private:install_intellij_iml.sh.template"), allow_files=True, single_file=True),
        "_intellij_generate_iml": attr.label(
            default=Label("//private:intellij_generate_iml"),
            executable=True,
            cfg="target"),

        "use_directory_name_as_symlink_name": attr.bool(
            doc="If there is only one iml file for this directory, " +
                "this option causes the name of the installed iml symlink to match the directory name. " +
                "Intellij then conveniently collapses the directory and iml name into a single name, in the IDE project view. " +
                "Opt out of this behavior by explicitly setting this to False - this will use the target name as the iml name.",
            default=True),

        "java_source": attr.label(doc="", providers=[JavaInfo, intellij_java_source_info_provider]),
        "test_java_source": attr.label(doc="", providers=[JavaInfo, intellij_java_source_info_provider]),

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

    outputs={
        "iml_file": "%{name}.iml",
        "iml_gen_debug_log": "iml_gen_debug.log",
        "iml_module_installer_script_file": "install_%{name}_intellij_iml_module.sh"
    },
)
