import os
import sys

# Ensure the tool root is importable so `import price_generator` works when
# pytest is invoked from anywhere.
sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
