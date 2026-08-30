@echo off
echo Building TerminalAlpha (Zero-Dependency Artifact)...

:: Clean previous artifacts to ensure a fresh, deterministic build
if exist *.class del *.class

:: Compile strictly without debug metadata for reproducible byte-identical output
javac -g:none TerminalAlpha.java

echo Build successful.
echo.
echo Verifying Reproducible Build Hash (SHA-256):
powershell -Command "(Get-FileHash TerminalAlpha.class -Algorithm SHA256).Hash"