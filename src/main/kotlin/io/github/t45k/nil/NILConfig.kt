package io.github.t45k.nil

import java.io.File

data class NILConfig(
    val src: File,
    val minLine: Int = 6,
    val minToken: Int = 50,
    val gramSize: Int = 5,
    val partitionSize: Int = 500_000,
    val filteringThreshold: Int = 10,
    val verifyingThreshold: Int = 70,
    val outputFileName: String = "result.csv",
    val threads: Int = 0,
    val isForBigCloneEval: Boolean = false,
    val isForMutationInjectionFramework: Boolean = false,
)

fun parseArgs(args: Array<String>): NILConfig {
    var src: File? = null
    var minLine = 6
    var minToken = 50
    var gramSize = 5
    var partitionSize = 500_000
    var filteringThreshold = 10
    var verifyingThreshold = 70
    var outputFileName: String? = null
    var threads = 0
    var isForBigCloneEval = false
    var isForMutationInjectionFramework = false

    val iterator = args.iterator()
    while (iterator.hasNext()) {
        when (val optionName = iterator.next().toLowerCase()) {
            "-s", "--src" -> src = File(iterator.next())
            "-mil", "--min-line" -> minLine = iterator.next().toInt()
            "-mit", "--min-token" -> minToken = iterator.next().toInt()
            "-n", "--n-gram" -> gramSize = iterator.next().toInt()
            "-p", "--partition-size" -> partitionSize = iterator.next().toInt()
            "-f", "--filtering-threshold" -> filteringThreshold = iterator.next().toInt()
            "-v", "--verifying-threshold" -> verifyingThreshold = iterator.next().toInt()
            "-o", "--output" -> outputFileName = iterator.next()
            "-t", "--threads" -> threads = iterator.next().toInt()
            "-bce", "--bigcloneeval" -> isForBigCloneEval = true
            "-mif", "--mutationinjectionframework" -> isForMutationInjectionFramework = true
            else -> throw InvalidOptionException(optionName)
        }
    }

    if(isForBigCloneEval && isForMutationInjectionFramework){
        throw InvalidOptionException("Cannot specify both -bce and -mif")
    }

    return NILConfig(
        src!!,
        minLine,
        minToken,
        gramSize,
        partitionSize,
        filteringThreshold,
        verifyingThreshold,
        outputFileName ?: "result_${gramSize}_${filteringThreshold}_${verifyingThreshold}.csv",
        threads,
        isForBigCloneEval,
        isForMutationInjectionFramework,
    )
}

class InvalidOptionException(private val option: String) : RuntimeException() {
    override val message: String
        get() = """$option is invalid option.
            |-s, --src${'\t'}Source directory (must be specified)
            |-mil, --min-line${'\t'}Minimum line (default: 6)
            |-mit, --min-token${'\t'}Minimum token (default: 50)
            |-n, --n-gram${'\t'}N of N-gram (default: 5)
            |-p, --partition-size${'\t'}Size of partition (default: 500000)
            |-f, --filtering-threshold${'\t'}Filtering threshold (default: 10%)
            |-v, --verifying-threshold${'\t'}Verifying threshold (default: 70%)
            |-o, --output${'\t'}Output file name (default: result_{N-gram}_{filtering_threshold}_{verifying_threshold}.csv)
            |-t, --thrads${'\t'}The number of threads used for parallel execution (default: all threads)
            |-bce, --bigcloneeval${'\t'}Output result feasible to BigCloneEval (default: false)
            |-mif, --mutationinjectionframework${'\t'}Output result feasible to MutationInjectionFramework (default: false)
        """.trimMargin()
}
