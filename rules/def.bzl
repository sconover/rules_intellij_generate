load("@rules_intellij_generate//private:intellij_iml.bzl", "intellij_iml")
load("@rules_intellij_generate//private:intellij_modules_xml.bzl", "intellij_modules_xml")
load("@rules_intellij_generate//private:intellij_compiler_xml.bzl", "intellij_compiler_xml")

def repositories_for_intellij_generate():
    native.maven_jar(name="com_beust_jcommander", artifact="com.beust:jcommander:1.72")

