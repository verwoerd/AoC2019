
#!/usr/bin/env bash

echo "Running day $1 First problem"
kotlin -cp out/production/AoC2019/ day$1.first.SolutionKt < resources/day$1.input
