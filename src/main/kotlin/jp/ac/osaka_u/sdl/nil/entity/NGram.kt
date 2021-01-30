package jp.ac.osaka_u.sdl.nil.entity

/**
 * NGram is a hash value of N-gram
 */
typealias NGram = Int

/**
 * NGrams is a distinct list of each N-gram's hash value.
 */
typealias NGrams = List<NGram>

/**
 * NGramInfo is pair of
 * Id: the index of token sequence of N-gram
 * Size: the size of a distinct set of N-gram
 */
data class NGramInfo(val id: Id, val size: Size)

infix fun Id.to(size: Size): NGramInfo = NGramInfo(this, size)

typealias Id = Int
typealias Size = Int
