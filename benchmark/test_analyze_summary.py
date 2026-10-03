"""Synthetic schema fixtures only; these numbers are not experimental evidence."""
import unittest
from analyze_summary import extract_rows

class SummaryTests(unittest.TestCase):
    def setUp(self):
        self.metrics = {
            'http_req_duration': {'avg': 2, 'med': 2, 'p(90)': 3, 'p(95)': 4, 'p(99)': 5, 'max': 6},
            'http_reqs': {'count': 10, 'rate': 2},
            'http_req_failed': {'rate': 0},
        }

    def test_flat_and_nested_have_same_output(self):
        flat = extract_rows({'metrics': self.metrics})
        nested = extract_rows({'metrics': {k: {'values': v} for k, v in self.metrics.items()}})
        self.assertEqual(flat, nested)
        self.assertIn(('http_req_duration', 'p99_ms', 5), flat)
        self.assertIn(('http_req_failed', 'failure_rate', 0), flat)

    def test_absent_p99_is_not_silently_blank(self):
        del self.metrics['http_req_duration']['p(99)']
        with self.assertRaisesRegex(ValueError, r'p\(99\)'):
            extract_rows({'metrics': self.metrics})

    def test_invalid_failure_rates_are_rejected(self):
        for value in (-1, 1.1, float('nan'), float('inf'), True, '0'):
            with self.subTest(value=value):
                self.metrics['http_req_failed']['rate'] = value
                with self.assertRaises(ValueError):
                    extract_rows({'metrics': self.metrics})

    def test_empty_or_unknown_summaries_are_rejected(self):
        for data in ({}, {'metrics': {}}, {'metrics': []}):
            with self.subTest(data=data), self.assertRaises(ValueError):
                extract_rows(data)

    def test_zero_requests_are_rejected(self):
        self.metrics['http_reqs']['count'] = 0
        with self.assertRaises(ValueError):
            extract_rows({'metrics': self.metrics})

if __name__ == '__main__':
    unittest.main()
