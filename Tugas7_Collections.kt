data class NilaiMahasiswa(
    val nim: String,
    val nama: String,
    val mataKuliah: String,
    val nilai: Int
)

fun main() {
    println("===== MANAJEMEN DATA DENGAN COLLECTIONS =====")

    // Minimal 10 data (dummy)
    val data = listOf(
        NilaiMahasiswa("2024001", "Budi Santoso", "Pemrograman", 85),
        NilaiMahasiswa("2024002", "Ani Wijaya", "Pemrograman", 92),
        NilaiMahasiswa("2024003", "Citra Dewi", "Pemrograman", 68),
        NilaiMahasiswa("2024004", "Dani Pratama", "Pemrograman", 45),
        NilaiMahasiswa("2024005", "Eka Putri", "Pemrograman", 73),
        NilaiMahasiswa("2024006", "Fajar Hidayat", "Pemrograman", 59),
        NilaiMahasiswa("2024007", "Gita Lestari", "Pemrograman", 70),
        NilaiMahasiswa("2024008", "Hendra Saputra", "Pemrograman", 88),
        NilaiMahasiswa("2024009", "Intan Maharani", "Pemrograman", 61),
        NilaiMahasiswa("2024010", "Joko Nugroho", "Pemrograman", 50)
    )

    // 1. Tampilkan semua data
    println("\n===== DATA NILAI MAHASISWA =====")
    printTable(data)

    // 4. Rata-rata nilai keseluruhan
    val rataRata = data.map { it.nilai }.average()
    println("\n===== STATISTIK =====")
    println("Total Mahasiswa : ${data.size}")
    println("Rata-rata Nilai : ${"%.1f".format(rataRata)}")

    // 5. Nilai tertinggi
    val max = data.maxByOrNull { it.nilai }
    if (max != null) println("Nilai Tertinggi : ${max.nilai} (${max.nama})")

    // 6. Nilai terendah
    val min = data.minByOrNull { it.nilai }
    if (min != null) println("Nilai Terendah  : ${min.nilai} (${min.nama})")

    // 2. Filter mahasiswa lulus (>=70)
    val lulus = data.filter { it.nilai >= 70 }
    println("\n===== MAHASISWA LULUS (>= 70) =====")
    if (lulus.isEmpty()) println("(Tidak ada)")
    else lulus.forEachIndexed { i, m -> println("${i + 1}. ${m.nama} - ${m.nilai} (${getGrade(m.nilai)})") }

    // 3. Filter mahasiswa tidak lulus (<70)
    val tidakLulus = data.filter { it.nilai < 70 }
    println("\n===== MAHASISWA TIDAK LULUS (< 70) =====")
    if (tidakLulus.isEmpty()) println("(Tidak ada)")
    else tidakLulus.forEachIndexed { i, m -> println("${i + 1}. ${m.nama} - ${m.nilai} (${getGrade(m.nilai)})") }

    // 7. Urutkan ascending & descending
    val asc = data.sortedBy { it.nilai }
    val desc = data.sortedByDescending { it.nilai }

    println("\n===== URUT NILAI ASCENDING =====")
    printTable(asc)

    println("\n===== URUT NILAI DESCENDING =====")
    printTable(desc)

    // 8. Kelompokkan berdasarkan grade (A/B/C/D/E)
    val groupByGrade = data.groupBy { getGrade(it.nilai) }
    println("\n===== KELOMPOK BERDASARKAN GRADE =====")
    groupByGrade.toSortedMap().forEach { (grade, list) ->
        println("Grade $grade:")
        list.forEach { println(" - ${it.nama} (${it.nilai})") }
    }

    // 9. Hitung jumlah mahasiswa per grade
    println("\n===== JUMLAH PER GRADE =====")
    groupByGrade.toSortedMap().forEach { (grade, list) ->
        println("Grade $grade : ${list.size} mahasiswa")
    }

    // 10. Cari mahasiswa berdasarkan nama (contains)
    println("\n===== CARI MAHASISWA (NAMA CONTAINS) =====")
    print("Masukkan kata kunci nama: ")
    val keyword = readLine()?.trim().orEmpty()

    val hasilCari = data.filter { it.nama.contains(keyword, ignoreCase = true) }
    if (keyword.isBlank()) {
        println("Kata kunci kosong.")
    } else if (hasilCari.isEmpty()) {
        println("Tidak ditemukan.")
    } else {
        printTable(hasilCari)
    }
}

fun getGrade(nilai: Int): String = when (nilai) {
    in 85..100 -> "A"
    in 70..84 -> "B"
    in 60..69 -> "C"
    in 50..59 -> "D"
    in 0..49 -> "E"
    else -> "?"
}

fun printTable(list: List<NilaiMahasiswa>) {
    println("No  NIM      Nama               MataKuliah      Nilai")
    println("--------------------------------------------------------")
    list.forEachIndexed { i, m ->
        println(
            "%-3d %-8s %-18s %-13s %3d".format(
                i + 1, m.nim, m.nama, m.mataKuliah, m.nilai
            )
        )
    }
}