package hu.raven.puppet.utility.extention

import java.io.ByteArrayOutputStream
import java.nio.charset.StandardCharsets.UTF_8
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream

fun String.compress(): ByteArray {
    val bos = ByteArrayOutputStream()
    GZIPOutputStream(bos).bufferedWriter(UTF_8).use { it.write(this) }
    return bos.toByteArray()
}

fun ByteArray.decompress(): String =
    GZIPInputStream(inputStream()).bufferedReader(UTF_8).use { it.readText() }
