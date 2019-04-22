import unittest, sys
from s01_one_class_test import S01OneClassTest
from s02_one_class_and_one_test import S02OneClassAndOneTest
from s03_basic_test import S03BasicTest
from s04_transitive_with_export_test import S04TransitiveWithExportTest
from s05_annotation_processor_test import S05AnnotationProcessorTest
from s06_protobuf_messages_test import S06ProtobufMessagesTest
from s07_grpc_test import S07GrpcTest
from s08_auto_value_test import S08AutoValueTest
from s09_python_test import S09PythonTest
from s12_kotlin_test import S12KotlinTest
from s13_minor_features_test import S13MinorFeaturesTest
from s14_all_dependency_types_test import S14AllDependencyTypesTest

if __name__ == '__main__':
    suite = unittest.TestSuite()
    suite.addTest(unittest.defaultTestLoader.loadTestsFromTestCase(S01OneClassTest))
    suite.addTest(unittest.defaultTestLoader.loadTestsFromTestCase(S02OneClassAndOneTest))
    suite.addTest(unittest.defaultTestLoader.loadTestsFromTestCase(S03BasicTest))
    suite.addTest(unittest.defaultTestLoader.loadTestsFromTestCase(S04TransitiveWithExportTest))
    suite.addTest(unittest.defaultTestLoader.loadTestsFromTestCase(S05AnnotationProcessorTest))
    suite.addTest(unittest.defaultTestLoader.loadTestsFromTestCase(S06ProtobufMessagesTest))
    suite.addTest(unittest.defaultTestLoader.loadTestsFromTestCase(S07GrpcTest))
    suite.addTest(unittest.defaultTestLoader.loadTestsFromTestCase(S08AutoValueTest))
    suite.addTest(unittest.defaultTestLoader.loadTestsFromTestCase(S08AutoValueTest))
    suite.addTest(unittest.defaultTestLoader.loadTestsFromTestCase(S09PythonTest))
    suite.addTest(unittest.defaultTestLoader.loadTestsFromTestCase(S12KotlinTest))
    suite.addTest(unittest.defaultTestLoader.loadTestsFromTestCase(S13MinorFeaturesTest))
    suite.addTest(unittest.defaultTestLoader.loadTestsFromTestCase(S14AllDependencyTypesTest))

    # python unit test main's must end with this or the test will exit with status code 0,
    # and thus it will not fail the bazel test run if there's a test failure.
    return_value = not unittest.TextTestRunner(verbosity=2).run(suite).wasSuccessful()
    sys.exit(return_value)
