// Nama: [Lalu Muhammad Rizaldi Kurniawan]
// NIM: [F1D02310120]

fun main() {
    println("===== SISTEM PENILAIAN =====")

    print("Masukkan Nama Mahasiswa: ")
    val nama = readLine()?.trim().orEmpty().ifEmpty { "Tanpa Nama" }

    val uts = readIntInRange("Masukkan Nilai UTS (0-100): ", 0, 100)
    val uas = readIntInRange("Masukkan Nilai UAS (0-100): ", 0, 100)
    val tugas = readIntInRange("Masukkan Nilai Tugas (0-100): ", 0, 100)

    val nilaiAkhir = (uts * 0.3) + (uas * 0.4) + (tugas * 0.3)

    val grade = when (nilaiAkhir.toInt()) {
        in 85..100 -> "A"
        in 70..84 -> "B"
        in 60..69 -> "C"
        in 50..59 -> "D"
        in 0..49 -> "E"
        else -> "?"
    }

    val keterangan = when (grade) {
        "A" -> "Sangat Baik"
        "B" -> "Baik"
        "C" -> "Cukup"
        "D" -> "Kurang"
        "E" -> "Sangat Kurang"
        else -> "Tidak Valid"
    }

    val status = if (nilaiAkhir >= 60) "LULUS" else "TIDAK LULUS"

    println("\n===== HASIL PENILAIAN =====")
    println("Nama        : $nama")
    println("Nilai UTS   : $uts (Bobot 30%)")
    println("Nilai UAS   : $uas (Bobot 40%)")
    println("Nilai Tugas : $tugas (Bobot 30%)")
    println("----------------------------")
    println("Nilai Akhir : ${"%.1f".format(nilaiAkhir)}")
    println("Grade       : $grade")
    println("Keterangan  : $keterangan")
    println("Status      : $status")

    if (status == "LULUS") println("\nSelamat! Anda dinyatakan LULUS.")
    else println("\nMaaf, Anda dinyatakan TIDAK LULUS.")
}

fun readIntInRange(prompt: String, min: Int, max: Int): Int {
    while (true) {
        print(prompt)
        val input = readLine()?.trim()
        val angka = input?.toIntOrNull()
        if (angka == null) {
            println("Input harus angka. Coba lagi.")
            continue
        }
        if (angka !in min..max) {
            println("Nilai harus di range $min-$max. Coba lagi.")
            continue
        }
        return angka
    }
}