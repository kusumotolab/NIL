# NIL
NIL is a clone detector using N-gram, Inverted index, and LCS.
NIL provides scalable large-variance clone detection.

## Requirements
- JDK 11+

## Usage
- Clone this repository.
- Move NIL's directory and build NIL (`./gradlew ShadowJar`)
- Run NIL (`java -jar ./build/libs/NIL-all.jar [options]`)
- Check the result file
  - If you didn't specify `-bce` option, the format is `/path/to/file_A,start_line_A,end_line_A,/path/to/file_B,start_line_B,end_line_B`.
  - If you specified `-bce` option, the format is `dir_A,file_A,start_line_A,end_line_A,dir_B,file_B,start_line_B,end_line_B`

## Options
|Name|Description|Default|
|:--:|:--|:--:|
|`-s`,`--src`|Input source directory. You must specify the target dir.|None|
|`-mil`,`--min-line`|Minimum number of lines that a code fragment must be to be treated as a clone.|`6`|
|`-mit`,`--mit`|Minimum number of tokens that a code fragment must be to be treated as a clone.|`50`|
|`-n`,`--n-gram`|N for N-gram.|`5`|
|`-p`,`--partition-size`|The number of partitions.|`10`|
|`-f`,`--filtration-threshold`|Threshold used in the filtration phase (%).|`10`|
|`-v`,`--verification-threshold`|Threshold used in the verificatioin phase (%).|`70`|
|`-o`,`--output`|Output file name.|`result_{n}_{f}_{v}.csv`|
|`-t`,`--threads`|The number of threads used for parallel execution (both the *Preprocess* and *Clone detection* phases)|all threads|
|`-bce`,`--bigcloneeval`|If you specify `-bce` option, NIL outputs result file feasible to BigCloneEval.|false|
|`-mif`,`--mutationinjectionframework`|If you specify `-mif` option, NIL outputs nothing except for the output file name as standard output.|false|

If you execute NIL on 250-MLOC codebase, we recommend `-p` option to 135.
