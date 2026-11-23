package com.example.temuin.feature.login.data.repository

import com.example.temuin.feature.review.data.model.Review
import com.example.temuin.feature.review.data.model.ReviewRole

class DummyReviewRepository : ReviewRepository {

    private val reviews = mutableListOf<Review>().apply {

        // 24 ulasan untuk Budi Santoso (user-01)

        val budiReviewers = listOf(
            Triple("user-02", "Dennisa Syafa", "Sangat responsif dan amanah saat menemukan KTM saya di kantin CL. Proses serah terima cepat dan kartu identitas dikembalikan dalam kondisi baik."),
            Triple("user-05", "Fajar Siddiq", "Proses pencocokan STNK dan kunci fisik sangat detail di Pos Satpam Gedung F. Orangnya ramah dan sabar menunggu."),
            Triple("user-06", "Ailsa Nasywa", "Sangat membantu. Binder catatan kuliah yang tertinggal di lab dikembalikan lengkap tanpa ada yang kurang."),
            Triple("user-07", "Reza Rahardian", "Komunikasi via WhatsApp responsif, serah terima lancar di lobi Gedung G."),
            Triple("user-08", "Tania Putri", "Barang ditemukan dan disimpan dengan aman. Terima kasih sudah menghubungi saya."),
            Triple("user-09", "Rafi Maulana", "Budi memastikan detail barang sebelum diserahkan. Prosesnya transparan dan terpercaya."),
            Triple("user-10", "Nadia Putri", "Sangat teliti saat memeriksa ciri-ciri barang. Komunikasinya juga jelas."),
            Triple("user-11", "Kevin Aditya", "Cepat mengabari setelah menemukan dompet saya. Semua isi masih lengkap."),
            Triple("user-12", "Maya Anindita", "Ramah dan kooperatif dari awal sampai barang diterima."),
            Triple("user-13", "Dimas Pratama", "Titik temu jelas dan proses pengembalian tidak berbelit-belit."),
            Triple("user-14", "Salsa Maharani", "Bukti kepemilikan dicek dengan teliti sebelum barang diberikan."),
            Triple("user-15", "Arga Wijaya", "Sangat membantu, bahkan memberi kabar saat sudah tiba di lokasi."),
            Triple("user-16", "Nabila Zahra", "Barang kembali dalam keadaan baik. Terima kasih banyak!"),
            Triple("user-17", "Farhan Akbar", "Respons cepat dan informasinya akurat."),
            Triple("user-18", "Citra Lestari", "Proses serah terima aman dan nyaman."),
            Triple("user-19", "Yoga Saputra", "Budi sangat bertanggung jawab selama proses pengembalian."),
            Triple("user-20", "Intan Permata", "Mudah dihubungi dan bersedia menyesuaikan waktu."),
            Triple("user-21", "Rizky Ramadhan", "Sangat jujur, barang langsung dilaporkan melalui Temuin."),
            Triple("user-22", "Ayu Safitri", "Barang disimpan dengan baik sampai saya datang mengambilnya."),
            Triple("user-23", "Bagas Prakoso", "Detail barang dicocokkan dengan benar. Pelayanan memuaskan."),
            Triple("user-24", "Nanda Kirana", "Komunikasinya sopan dan prosesnya cepat."),
            Triple("user-25", "Rendra Kurniawan", "Bukti kepemilikan diverifikasi secara teliti."),
            Triple("user-26", "Dewi Anggraini", "Terima kasih sudah menjaga barang saya sampai ditemukan pemiliknya."),
            Triple("user-27", "Alif Hidayat", "Pengembalian berjalan lancar dan sangat terbantu."),
        )

        val budiItems = listOf(
            "KTM Mahasiswa UB",
            "Kunci Motor Honda",
            "Binder Catatan Kuliah",
            "Kacamata Frame Hitam",
            "Dompet Kulit Cokelat",
            "Flashdisk 32 GB",
            "Tumbler Stainless",
            "Payung Lipat",
            "Charger Laptop",
            "Earphone Putih",
        )

        budiReviewers.forEachIndexed { index, person ->

            add(
                Review(
                    id = "BUDI-${index + 1}",
                    reviewerId = person.first,
                    reviewerName = person.second,
                    reviewedUserId = "user-01",
                    reviewedUserName = "Budi Santoso",

                    targetRole = if ((index % 3) == 1)
                        ReviewRole.CLAIMANT
                    else
                        ReviewRole.REPORTER,

                    reportId = "FND-${882 + index}",
                    itemName = budiItems[index % budiItems.size],

                    rating = if (index == 4 || index == 17)
                        4
                    else
                        5,

                    comment = person.third,

                    tags = when (index % 4) {
                        0 -> listOf("Sangat Responsif", "Jujur & Amanah")
                        1 -> listOf("Tepat Waktu", "Verifikasi Teliti")
                        2 -> listOf("Ramah & Sopan")
                        else -> listOf("Informasi Akurat", "Komunikasi Jelas")
                    },

                    createdAt = listOf(
                        "2 hari lalu",
                        "1 minggu lalu",
                        "2 minggu lalu",
                        "1 bulan lalu"
                    )[index % 4],

                    helpfulCount = (index * 3) % 12
                )
            )
        }

        // 18 ulasan untuk Dennisa Syafa (user-02)

        val denReviews = listOf(
            "Pelapor datang tepat waktu dan membawa bukti kepemilikan yang lengkap. Proses verifikasi berjalan lancar.",
            "Komunikasi sangat jelas sejak awal. Barang dikembalikan sesuai kesepakatan.",
            "Sangat teliti mencocokkan nomor seri charger sebelum menerima barang.",
            "Ramah dan sopan saat proses serah terima di Pos Satpam Gedung F.",
            "Informasi lokasi dan waktu pengambilan akurat, jadi tidak perlu menunggu lama.",
            "Menunjukkan bukti kepemilikan dengan lengkap dan kooperatif.",
            "Responsif saat dihubungi dan memberikan konfirmasi setelah barang diterima.",
            "Proses pengambilan berjalan aman. Terima kasih sudah mengikuti prosedur.",
            "Sangat menghargai komunikasi yang terbuka dan sikapnya yang ramah.",
            "Datang sesuai waktu yang disepakati dan memeriksa barang bersama-sama.",
            "Verifikasi dilakukan dengan teliti sehingga barang tidak salah diberikan.",
            "Mudah diajak berkoordinasi meskipun jadwal cukup padat.",
            "Memberikan informasi ciri-ciri barang secara detail.",
            "Sopan dan sabar selama proses pencocokan barang.",
            "Proses serah terima cepat, aman, dan transparan.",
            "Cepat mengonfirmasi kepemilikan melalui bukti yang sesuai.",
            "Sangat kooperatif dan menjaga komunikasi sampai selesai.",
            "Pengambilan barang berjalan lancar tanpa kendala."
        )

        val denNames = listOf(
            "Budi Santoso",
            "Tiara Aristawidya",
            "Rian Pratama",
            "Nadhira Putri",
            "Fajar Siddiq",
            "Ailsa Nasywa",
            "Reza Rahardian",
            "Tania Putri",
            "Kevin Aditya",
            "Maya Anindita",
            "Dimas Pratama",
            "Salsa Maharani",
            "Arga Wijaya",
            "Nabila Zahra",
            "Farhan Akbar",
            "Citra Lestari",
            "Yoga Saputra",
            "Intan Permata"
        )

        val denItems = listOf(
            "KTM Mahasiswa UB",
            "Dompet Kulit Cokelat",
            "Kunci Motor Vario",
            "Charger Asus Type-C",
            "Tumbler Minum",
            "Binder Catatan Kuliah"
        )

        denReviews.forEachIndexed { index, comment ->

            add(
                Review(
                    id = "DEN-${index + 1}",
                    reviewerId = "den-reviewer-$index",
                    reviewerName = denNames[index],
                    reviewedUserId = "user-02",
                    reviewedUserName = "Dennisa Syafa",

                    targetRole = if (index % 2 == 0)
                        ReviewRole.REPORTER
                    else
                        ReviewRole.CLAIMANT,

                    reportId = "FND-${915 + index}",
                    itemName = denItems[index % denItems.size],

                    rating = if (index == 3 || index == 11)
                        4
                    else
                        5,

                    comment = comment,

                    tags = when (index % 3) {
                        0 -> listOf("Sangat Responsif", "Jujur & Amanah")
                        1 -> listOf("Tepat Waktu", "Bukti Akurat")
                        else -> listOf("Verifikasi Teliti")
                    },

                    createdAt = listOf(
                        "Kemarin",
                        "3 hari lalu",
                        "1 minggu lalu",
                        "2 minggu lalu"
                    )[index % 4],

                    helpfulCount = (index * 2) % 9
                )
            )
        }
    }

    override fun getReviewsForUser(userId: String): List<Review> {
        return reviews
            .filter { it.reviewedUserId == userId }
            .sortedBy { reviewAgeInDays(it.createdAt) }
    }

    override fun getReviewsWrittenBy(userId: String): List<Review> {
        return reviews
            .filter { it.reviewerId == userId }
            .sortedBy { reviewAgeInDays(it.createdAt) }
    }

    // Mengubah keterangan waktu relatif menjadi angka hari.
    // Angka yang lebih kecil berarti ulasan lebih baru.
    private fun reviewAgeInDays(createdAt: String): Int {
        val value = createdAt.trim().lowercase()

        if (value == "baru saja" || value == "barusan") return 0
        if (value == "kemarin") return 1

        val match = Regex("""(\d+)\s*(hari|minggu|bulan|tahun)\s* lalu""")
            .find(value) ?: return Int.MAX_VALUE

        val amount = match.groupValues[1].toIntOrNull() ?: return Int.MAX_VALUE

        return when (match.groupValues[2]) {
            "hari" -> amount
            "minggu" -> amount * 7
            "bulan" -> amount * 30
            "tahun" -> amount * 365
            else -> Int.MAX_VALUE
        }
    }

    override fun getReviewById(reviewId: String): Review? {
        return reviews.find { it.id == reviewId }
    }

    override fun addReview(review: Review) {
        reviews.add(0, review)
    }

    override fun updateReview(review: Review) {
        val index = reviews.indexOfFirst {
            it.id == review.id
        }

        if (index >= 0) {
            reviews[index] = review
        }
    }

    override fun deleteReview(reviewId: String) {
        reviews.removeAll {
            it.id == reviewId
        }
    }
}