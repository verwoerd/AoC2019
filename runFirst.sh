
#!/usr/bin/env bash

echo "Running day $1 First problem"
kotlin -cp "lib/kotlinx-coroutines-core-1.3.2.jar;out/production/AoC2019/" day$1.first.SolutionKt < resources/day$1.input
