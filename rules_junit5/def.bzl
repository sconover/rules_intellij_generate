def junit5_all_in_package_test(name=None, java_package=None, runtime_deps=[]):
    native.java_test(
        name=name,
        main_class="org.junit.platform.console.ConsoleLauncher",
        args=[
            "--select-package %s" % java_package,
            "--details none",
        ],
        runtime_deps=runtime_deps + [
            "@org_apiguardian_apiguardian_api//jar",
            "@org_junit_jupiter_junit_jupiter_api//jar",
            "@org_junit_jupiter_junit_jupiter_engine//jar",
            "@org_junit_platform_junit_platform_commons//jar",
            "@org_junit_platform_junit_platform_console//jar",
            "@org_junit_platform_junit_platform_engine//jar",
            "@org_junit_platform_junit_platform_launcher//jar",
            "@org_junit_platform_junit_platform_runner//jar",
            "@org_opentest4j_opentest4j//jar",
        ],
        use_testrunner=False,
        size="small",
    )

def junit5_repositories():
    native.maven_jar(name="org_junit_jupiter_junit_jupiter_api",         artifact="org.junit.jupiter:junit-jupiter-api:5.0.1")
    native.maven_jar(name="org_junit_jupiter_junit_jupiter_engine",      artifact="org.junit.jupiter:junit-jupiter-engine:5.0.1")
    native.maven_jar(name="org_junit_platform_junit_platform_commons",   artifact="org.junit.platform:junit-platform-commons:1.0.1")
    native.maven_jar(name="org_junit_platform_junit_platform_console",   artifact="org.junit.platform:junit-platform-console:1.0.1")
    native.maven_jar(name="org_junit_platform_junit_platform_engine",    artifact="org.junit.platform:junit-platform-engine:1.0.1")
    native.maven_jar(name="org_junit_platform_junit_platform_launcher",  artifact="org.junit.platform:junit-platform-launcher:1.0.1")
    native.maven_jar(name="org_junit_platform_junit_platform_runner",    artifact="org.junit.platform:junit-platform-runner:1.0.1")
    native.maven_jar(name="org_opentest4j_opentest4j",                   artifact="org.opentest4j:opentest4j:1.0.0")
    native.maven_jar(name="org_apiguardian_apiguardian_api",             artifact="org.apiguardian:apiguardian-api:1.0.0")

JUNIT5_MINIMAL_DEPS = [
    "@org_apiguardian_apiguardian_api//jar",
    "@org_junit_jupiter_junit_jupiter_api//jar",
    "@org_opentest4j_opentest4j//jar",
]