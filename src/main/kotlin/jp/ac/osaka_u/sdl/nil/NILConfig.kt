package jp.ac.osaka_u.sdl.nil

import java.io.File

data class NILConfig(
    val src: File,
    val minLine: Int = 6,
    val minToken: Int = 50,
    val gramSize: Int = 5,
    val partitionNum: Int = 10,
    val filtrationThreshold: Int = 10,
    val verificationThreshold: Int = 70,
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
    var partitionNum = 10
    var filtrationThreshold = 10
    var verificationThreshold = 70
    var outputFileName: String? = null
    var threads = 0
    var isForBigCloneEval = false
    var isForMutationInjectionFramework = false

    val iterator = args.iterator()
    while (iterator.hasNext()) {
        when (val optionName = iterator.next().toLowerCase()) {
            "-s", "--src" -> src = File(iterator.next())
            "-mil", "--min-line" -> minLine = iterator.next().toIntOrException(optionName)
            "-mit", "--min-token" -> minToken = iterator.next().toIntOrException(optionName)
            "-n", "--n-gram" -> gramSize = iterator.next().toIntOrException(optionName)
            "-p", "--partition-number" -> partitionNum = iterator.next().toIntOrException(optionName)
            "-f", "--filtering-threshold" -> filtrationThreshold = iterator.next().toIntOrException(optionName)
            "-v", "--verifying-threshold" -> verificationThreshold = iterator.next().toIntOrException(optionName)
            "-o", "--output" -> outputFileName = iterator.next()
            "-t", "--threads" -> threads = iterator.next().toInt()
            "-bce", "--bigcloneeval" -> isForBigCloneEval = true
            "-mif", "--mutationinjectionframework" -> isForMutationInjectionFramework = true
            else -> throw InvalidOptionException("$optionName is invalid option.")
        }
    }

    if (isForBigCloneEval && isForMutationInjectionFramework) {
        throw InvalidOptionException("Cannot specify both -bce and -mif.")
    }

    return NILConfig(
        src ?: throw InvalidOptionException("-s must be specified."),
        minLine,
        minToken,
        gramSize,
        partitionNum,
        filtrationThreshold,
        verificationThreshold,
        outputFileName ?: "result_${gramSize}_${filtrationThreshold}_${verificationThreshold}.csv",
        threads,
        isForBigCloneEval,
        isForMutationInjectionFramework,
    )
}

fun String.toIntOrException(optionName: String): Int =
    try {
        this.toInt()
    } catch (e: NumberFormatException) {
        throw InvalidOptionException("$optionName value $this is not integer.")
    }

class InvalidOptionException(private val option: String) : RuntimeException() {
    override val message: String
        get() = """$option
            |-s, --src${'\t'}${'\t'}${'\t'}${'\t'}Source directory (must be specified)
            |-mil, --min-line${'\t'}${'\t'}${'\t'}Minimum line (default: 6)
            |-mit, --min-token${'\t'}${'\t'}${'\t'}Minimum token (default: 50)
            |-n, --n-gram${'\t'}${'\t'}${'\t'}${'\t'}N of N-gram (default: 5)
            |-p, --partition-number${'\t'}${'\t'}${'\t'}The number of partition (default: 10)   
            |-f, --filtering-threshold${'\t'}${'\t'}Filtering threshold (default: 10%)
            |-v, --verifying-threshold${'\t'}${'\t'}Verifying threshold (default: 70%)
            |-o, --output${'\t'}${'\t'}${'\t'}${'\t'}Output file name (default: result_{N-gram}_{filtering_threshold}_{verifying_threshold}.csv)
            |-t, --thrads${'\t'}${'\t'}${'\t'}${'\t'}The number of threads used for parallel execution (default: all threads)
            |-bce, --bigcloneeval${'\t'}${'\t'}${'\t'}Output result feasible to BigCloneEval (default: false)
            |-mif, --mutationinjectionframework${'\t'}Output result feasible to MutationInjectionFramework (default: false)
        """.trimMargin()
}
