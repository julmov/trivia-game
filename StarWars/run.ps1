$ErrorActionPreference = "Stop"

New-Item -ItemType Directory -Force out | Out-Null
mvn -q compile exec:java