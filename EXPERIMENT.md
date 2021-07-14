# Experiment information

This page desribes experiments we conduct in the paper and how to reproduce them.

We used the following clone detectors.
|Name|Link|
|:--:|:--|
|NIL|https://github.com/kusumotolab/NIL/releases/tag/v1.0.0|
|CCAligner|https://github.com/PCWcn/CCAligner/tree/f27622d6f1500536c45862c4d49bd5f5d6802ace|
|LVMapper|It is not publicly availalbe. Please contact the authors to obtain the tool.|
|SourcererCC|https://github.com/Mondego/SourcererCC/tree/afc5755273fc91754ee08326a6946fad85e85b94|
|NiCad|https://www.txl.ca/download/23869-NiCad-6.2.tar.gz|

The execution commands or steps are as follows.
- NIL
  - Build NIL (Please refer README.md)
  - `java -Xmx12G -jar ./build/libs/NIL-all.jar -s /path/to/src`
  - In the case of 250 MLOC codebase, we specifed `-p 135`.
- CCAligner and LVMapper
  - They come with a shell file to run, named `runner`, so we ran it.
- SourcererCC
  - We modified min length to six lines and the number of processes to four,
  - and ran it as described in README.md (https://github.com/Mondego/SourcererCC/tree/afc5755273fc91754ee08326a6946fad85e85b94#readme).
- NiCad
  - We modified `minsize` to 6 and `cluster` to `no` in `config/default.cfg`,
  - and ran `./nicad6 functions java /path/to/src default`

## Parameter setting

We used the set of source files curated from IJaDataset for BigCloneBench in this experiment.<br>
It is available from https://github.com/jeffsvajlenko/BigCloneEval .

Run NIL with the following command.<br>
`java -jar ./build/libs/NIL-all.jar -s /path/to/src -n $N`<br>
In this command, `$N` represents the size of N-gram.

## Large-variance clone detection

### Precision

We used [Ant 1.10.1](https://github.com/apache/ant/releases/tag/rel%2F1.10.1) and [Maven 3.5.0](https://github.com/apache/maven/releases/tag/maven-3.5.0).<br>
We conducted the experiment as following steps.
1. We ran NIL, CCAligner and LVMapper on the systems.
2. we randomly selected 100 large-variance clone pairs with more than ten lines for each tool and target system
3. We manually confirmed whether the clone pairs were correct.

Manual validation descriptions are available at https://zenodo.org/record/4490845

### Recall

We used our tool, [LVMutator](https://github.com/T45K/LVMutator/tree/101cfa84b4c1d935503a8a0d47b72de4141f6cbc), which is based on Mutation framework and perform large-variance clone generation and recall evaluation.
The usage of LVMutator is specified at https://github.com/T45K/LVMutator/tree/master#readme.

The generated large-variance clone pairs are available at https://zenodo.org/record/4491016.

## General clone detection

### Mutation Framework

Mutation Framework is available from https://github.com/jeffsvajlenko/MutationInjectionFramework/releases/tag/v1.0.<br>
We executed Mutation Framework with all the default settings.<br>
The generated clone pairs are available at https://zenodo.org/record/4491052.

### BigCloneEval

BigCloneEval is available from https://github.com/jeffsvajlenko/BigCloneEval/releases/tag/v1.1.<br>
The exection command to evaluate each tool is `./evaluateToll -t num -o output_file_name --st BOTH --mit 50 --mil 6 --mip 6`.

We also conducted manual validation in this experiment.
The descriptions are available at https://zenodo.org/record/4493069.

### Scalability

We created datasets with 1, 10, 100, and 250 MLOC from IJaDataset.
These datasets and an executable file of NIL are available at https://zenodo.org/record/4491208.

In this experiment, we also examined how effective the location and filtration phases are for NIL’s scalability
by modifying NIL's source code.
