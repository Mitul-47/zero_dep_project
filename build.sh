#!/bin/bash
echo "Building TerminalAlpha (Zero-Dependency Artifact)..."

# Clean previous artifacts to ensure a fresh, deterministic build
rm -f *.class

# Compile strictly without debug metadata
javac -g:none TerminalAlpha.java

echo "Build successful."
echo ""
echo "Verifying Reproducible Build Hash (SHA-256):"
shasum -a 256 TerminalAlpha.class
