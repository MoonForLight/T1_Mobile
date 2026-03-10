// Interface untuk produk diskon
interface Discountable {
    fun discountedPrice(): Double
}

// Data class
data class Produk(
    val id: String,
    val nama: String,
    val harga: Double,
    val kategori: String,
    var stok: Int,
    val diskonPersen: Double? = null,  // null safety
    val diskonNominal: Double? = null
) : Discountable {
    override fun discountedPrice(): Double {
        val percentOff = diskonPersen?.let { harga * (it / 100.0) } ?: 0.0
        val nominalOff = diskonNominal ?: 0.0
        val final = harga - percentOff - nominalOff
        return if (final < 0) 0.0 else final
    }
}

data class Customer(
    val id: String,
    val nama: String,
    val email: String,
    val alamat: String? = null
)

data class CartItem(val produk: Produk, var jumlah: Int)

sealed class OrderStatus {
    data object Pending : OrderStatus()
    data object Processing : OrderStatus()
    data object Shipped : OrderStatus()
    data object Delivered : OrderStatus()
    data object Cancelled : OrderStatus()
}

sealed class PaymentMethod {
    data object Cash : PaymentMethod()
    data object Transfer : PaymentMethod()
    data object EWallet : PaymentMethod()
}

data class Order(
    val id: String,
    val customer: Customer,
    val items: List<CartItem>,
    var status: OrderStatus,
    val paymentMethod: PaymentMethod,
    val totalHarga: Double
)

class ShoppingCart {
    private val items = mutableListOf<CartItem>()

    fun addProduk(p: Produk, qty: Int) {
        if (qty <= 0) return
        if (p.stok < qty) {
            println("Stok tidak cukup! Stok tersedia: ${p.stok}")
            return
        }
        val existing = items.find { it.produk.id == p.id }
        if (existing != null) existing.jumlah += qty else items.add(CartItem(p, qty))
        p.stok -= qty
        println("Berhasil menambah ${p.nama} x$qty ke keranjang.")
    }

    fun removeProduk(idProduk: String) {
        val idx = items.indexOfFirst { it.produk.id == idProduk }
        if (idx == -1) {
            println("Produk tidak ada di keranjang.")
            return
        }
        val removed = items.removeAt(idx)
        removed.produk.stok += removed.jumlah
        println("Dihapus dari keranjang: ${removed.produk.nama} x${removed.jumlah}")
    }

    fun listItems(): List<CartItem> = items.toList()

    fun isEmpty(): Boolean = items.isEmpty()

    // Higher-order function untuk hitung total dengan custom discount calculator
    fun total(customDiscount: (CartItem) -> Double = { it.produk.discountedPrice() * it.jumlah }): Double {
        return items.sumOf { customDiscount(it) }
    }

    fun clear() = items.clear()
}

class TokoOnline {
    private val produkList = mutableListOf(
        Produk("P01", "Skincare A", 55000.0, "Skincare", 10, diskonPersen = 10.0),
        Produk("P02", "Skincare B", 75000.0, "Skincare", 8),
        Produk("P03", "Serum C", 99000.0, "Skincare", 5, diskonNominal = 5000.0),
        Produk("P04", "Body Lotion", 45000.0, "Bodycare", 12),
        Produk("P05", "Sunscreen", 65000.0, "Skincare", 7, diskonPersen = 5.0),
        Produk("P06", "Parfum", 120000.0, "Fragrance", 4)
    )

    private val cart = ShoppingCart()
    private val riwayatOrder = mutableListOf<Order>()

    fun run() {
        println("===== SISTEM TOKO ONLINE (BONUS) =====")

        val customer = inputCustomer()

        while (true) {
            println(
                """
                
                Menu:
                1. Lihat Produk
                2. Tambah ke Keranjang
                3. Hapus dari Keranjang
                4. Lihat Keranjang & Total
                5. Checkout
                6. Tracking Status Pesanan
                7. Lihat Riwayat Pesanan
                0. Keluar
                """.trimIndent()
            )

            when (readInt("Pilih menu: ")) {
                1 -> showProdukMenu()
                2 -> tambahKeranjangMenu()
                3 -> hapusKeranjangMenu()
                4 -> lihatKeranjang()
                5 -> checkout(customer)
                6 -> tracking()
                7 -> riwayat()
                0 -> return
                else -> println("Menu tidak valid.")
            }
        }
    }

    private fun inputCustomer(): Customer {
        print("Nama customer: ")
        val nama = readLine()?.trim().orEmpty().ifEmpty { "Customer" }
        print("Email: ")
        val email = readLine()?.trim().orEmpty().ifEmpty { "customer@mail.com" }
        print("Alamat (boleh kosong): ")
        val alamat = readLine()?.trim().takeIf { !it.isNullOrBlank() }
        return Customer("C01", nama, email, alamat)
    }

    private fun showProdukMenu() {
        println("\n===== DAFTAR PRODUK =====")

        // Collection operations: filter/sort/grouping contoh
        val sorted = produkList.sortedBy { it.harga }
        sorted.forEach {
            val hargaDiskon = it.discountedPrice()
            val diskonInfo = if (hargaDiskon != it.harga) " (Diskon -> ${hargaDiskon.toInt()})" else ""
            println("${it.id} | ${it.nama} | Rp${it.harga.toInt()}$diskonInfo | ${it.kategori} | stok: ${it.stok}")
        }

        println("\n[Info] Grouping kategori:")
        val group = produkList.groupBy { it.kategori }
        group.forEach { (kat, list) -> println("- $kat: ${list.size} produk") }
    }

    private fun tambahKeranjangMenu() {
        val id = readString("Masukkan ID produk: ")
        val p = produkList.find { it.id.equals(id, ignoreCase = true) }
        if (p == null) {
            println("Produk tidak ditemukan.")
            return
        }
        val qty = readInt("Jumlah: ")
        cart.addProduk(p, qty)
    }

    private fun hapusKeranjangMenu() {
        val id = readString("Masukkan ID produk yang mau dihapus: ")
        cart.removeProduk(id)
    }

    private fun lihatKeranjang() {
        println("\n===== KERANJANG =====")
        if (cart.isEmpty()) {
            println("(Keranjang kosong)")
            return
        }
        cart.listItems().forEachIndexed { i, item ->
            val hargaSatuan = item.produk.discountedPrice()
            println("${i + 1}. ${item.produk.nama} x${item.jumlah} | satuan: Rp${hargaSatuan.toInt()} | subtotal: Rp${(hargaSatuan * item.jumlah).toInt()}")
        }
        val total = cart.total()
        println("Total: Rp${total.toInt()}")
    }

    private fun checkout(customer: Customer) {
        if (cart.isEmpty()) {
            println("Keranjang masih kosong.")
            return
        }

        val metode = pilihPembayaran() ?: return

        val orderId = "ORD${riwayatOrder.size + 1}".padStart(6, '0')
        val total = cart.total()

        val order = Order(
            id = orderId,
            customer = customer,
            items = cart.listItems(),
            status = OrderStatus.Pending,
            paymentMethod = metode,
            totalHarga = total
        )

        riwayatOrder.add(order)
        cart.clear()

        println("\nCheckout berhasil! ID Pesanan: ${order.id}")
        println("Total Bayar: Rp${order.totalHarga.toInt()}")
        println("Status: ${order.status::class.simpleName}")
    }

    private fun pilihPembayaran(): PaymentMethod? {
        println("\nPilih pembayaran: 1) Cash  2) Transfer  3) EWallet")
        return when (readInt("Pilihan: ")) {
            1 -> PaymentMethod.Cash
            2 -> PaymentMethod.Transfer
            3 -> PaymentMethod.EWallet
            else -> {
                println("Pilihan tidak valid.")
                null
            }
        }
    }

    private fun tracking() {
        if (riwayatOrder.isEmpty()) {
            println("Belum ada pesanan.")
            return
        }
        val id = readString("Masukkan ID pesanan: ")
        val order = riwayatOrder.find { it.id.equals(id, ignoreCase = true) }
        if (order == null) {
            println("Pesanan tidak ditemukan.")
            return
        }

        println("Pesanan ${order.id} | Status saat ini: ${order.status::class.simpleName}")

        println("Update status? 1) Processing 2) Shipped 3) Delivered 4) Cancelled 0) Tidak")
        when (readInt("Pilihan: ")) {
            1 -> order.status = OrderStatus.Processing
            2 -> order.status = OrderStatus.Shipped
            3 -> order.status = OrderStatus.Delivered
            4 -> order.status = OrderStatus.Cancelled
            0 -> return
            else -> println("Tidak diubah.")
        }
        println("Status terbaru: ${order.status::class.simpleName}")
    }

    private fun riwayat() {
        println("\n===== RIWAYAT PESANAN =====")
        if (riwayatOrder.isEmpty()) {
            println("(Belum ada)")
            return
        }
        riwayatOrder.forEach { o ->
            println("${o.id} | ${o.customer.nama} | ${o.status::class.simpleName} | ${o.paymentMethod::class.simpleName} | Rp${o.totalHarga.toInt()}")
        }
    }
}

// Helpers input
fun readInt(prompt: String): Int {
    while (true) {
        print(prompt)
        val n = readLine()?.trim()?.toIntOrNull()
        if (n != null) return n
        println("Input harus angka.")
    }
}

fun readString(prompt: String): String {
    print(prompt)
    return readLine()?.trim().orEmpty()
}

fun main() {
    TokoOnline().run()
}