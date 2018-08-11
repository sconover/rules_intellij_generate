import unittest

import sys, os
sys.path.append(os.path.join(os.path.dirname(__file__), '../src'))

from echo import echo

class EchoTest(unittest.TestCase):
    def test_echo(self):
        self.assertEqual("echoing: yy", echo ("yy"))