# Configuration file for the Sphinx documentation builder.
#
# For the full list of built-in configuration values, see the documentation:
# https://www.sphinx-doc.org/en/master/usage/configuration.html

import sphinx.locale

# Compatibility patch: javasphinx uses l_ which was removed in modern Sphinx versions.
# We restore it as an alias for the standard translation function.
if not hasattr(sphinx.locale, 'l_'):
    sphinx.locale.l_ = sphinx.locale._

# -- Project information -----------------------------------------------------
project = 'Panacea'
copyright = '2026, Javastro'
author = 'Javastro'
release = '0.1.0-SNAPSHOT'

# -- General configuration ---------------------------------------------------
extensions = [
    'javasphinx',
    'sphinx.ext.intersphinx',
    'sphinx.ext.autosectionlabel',
    'sphinxcontrib.plantuml',
]

templates_path = ['_templates']
exclude_patterns = ['_build', 'Thumbs.db', '.DS_Store']

# -- Options for HTML output -------------------------------------------------
html_theme = 'sphinx_rtd_theme'
html_static_path = ['_static']
html_title = 'IVOACore Documentation'

# -- Intersphinx mapping -----------------------------------------------------
intersphinx_mapping = {
    'python': ('https://docs.python.org/3', None),
}

# -- Java package index configuration ----------------------------------------
# Used by javasphinx to cross-reference Java types from IVOA entities
java_base_url = 'javadoc/'
