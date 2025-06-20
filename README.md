Sistem Manajemen Tugas Mahasiswa
Aplikasi desktop untuk membantu mahasiswa mengelola tugas akademik dan personal dengan antarmuka yang user-friendly dan fitur-fitur lengkap.

📋 Deskripsi Proyek
Sistem Manajemen Tugas Mahasiswa adalah aplikasi Java Swing yang dikembangkan sebagai Tugas Besar mata kuliah Pemrograman Berorientasi Objek (PBO) di Institut Teknologi Kalimantan (ITK). Aplikasi ini dirancang untuk membantu mahasiswa mengorganisir dan melacak tugas-tugas mereka dengan lebih efektif.

✨ Fitur Utama
🔐 Autentikasi
Login & Register: Sistem autentikasi pengguna yang aman
Session Management: Pengelolaan sesi pengguna
🏠 Dashboard
Overview Tugas: Tampilan ringkasan semua tugas mendatang
Notifikasi Deadline: Pengingat otomatis untuk tugas yang mendekati deadline
Status Indicator: Indikator visual untuk status setiap tugas
Card Layout: Tampilan kartu yang menarik dan informatif
📚 Manajemen Mata Kuliah
Tambah Mata Kuliah: Menambahkan mata kuliah baru
Hapus Mata Kuliah: Menghapus mata kuliah beserta semua tugas terkait
Validasi: Pencegahan duplikasi nama mata kuliah
📝 Manajemen Tugas
Dua Jenis Tugas:
Academic Task: Tugas yang terkait dengan mata kuliah
Personal Task: Tugas pribadi dengan kategori custom
CRUD Operations: Create, Read, Update, Delete tugas
Status Management: Belum Mulai, Sedang Dikerjakan, Selesai
Search & Filter: Pencarian dan filter berdasarkan berbagai kriteria
🔍 Fitur Pencarian & Filter
Real-time Search: Pencarian langsung saat mengetik
Multiple Filters:
Filter berdasarkan status
Filter berdasarkan mata kuliah/kategori
Filter berdasarkan deadline (1 hari, 3 hari, 1 minggu, 1 bulan)
🏗️ Arsitektur Aplikasi
Design Pattern
MVC (Model-View-Controller): Pemisahan logika bisnis, tampilan, dan kontrol
Singleton Pattern: Untuk koneksi database
Factory Pattern: Untuk pembuatan objek UI

🎯 Cara Penggunaan
1. Login/Register
Jalankan aplikasi
Daftar akun baru atau login dengan akun existing
Sistem akan menyimpan data pengguna secara lokal
2. Mengelola Mata Kuliah
Navigasi ke menu "Mata Kuliah"
Tambah mata kuliah baru dengan nama yang unik
Hapus mata kuliah yang tidak diperlukan
3. Menambah Tugas
Navigasi ke menu "Tugas"
Pilih jenis tugas (Academic/Personal)
Isi detail tugas (judul, deskripsi, deadline, dll.)
Simpan tugas
4. Mengelola Status Tugas
Pilih tugas dari tabel
Klik "Ubah Status" untuk mengupdate progress
Status tersedia: Belum Mulai, Sedang Dikerjakan, Selesai
5. Mencari dan Filter Tugas
Gunakan search box untuk pencarian cepat
Klik tombol filter untuk opsi filter lanjutan
Filter berdasarkan status, mata kuliah, atau deadline
🎨 Konsep OOP yang Diimplementasikan
1. Encapsulation
Semua atribut class menggunakan access modifier private
Akses data melalui getter dan setter methods
Validasi data dalam setter methods
2. Inheritance
TaskBase sebagai abstract class untuk AcademicTask dan PersonalTask
BasePanel sebagai parent class untuk semua panel UI
Pewarisan properties dan methods umum
3. Polymorphism
Method overriding dalam subclass
Interface Savable diimplementasikan berbeda di setiap class
Runtime polymorphism dalam penanganan berbagai jenis tugas
4. Abstraction
Abstract class TaskBase mendefinisikan struktur umum tugas
Interface Savable untuk operasi database
Abstraksi kompleksitas database di layer controller
🗄️ Struktur Database
Tabel Users
user_id (PRIMARY KEY)
name (UNIQUE)
password
Tabel Courses
course_id (PRIMARY KEY)
course_name
user_id (FOREIGN KEY)
Tabel AcademicTasks
task_id (PRIMARY KEY)
title
description
deadline
status
course_id (FOREIGN KEY)
Tabel PersonalTasks
personal_task_id (PRIMARY KEY)
title
description
category
deadline
status
user_id (FOREIGN KEY)

🚀 Fitur Lanjutan
Notifikasi Deadline
Popup otomatis saat aplikasi dibuka
Menampilkan tugas yang mendekati deadline (3 hari ke depan)
Visual indicator untuk tugas yang terlambat
Search & Filter Real-time
Pencarian langsung tanpa perlu menekan tombol
Multiple filter yang dapat dikombinasikan
Sorting otomatis berdasarkan deadline
Status Management
Visual indicator dengan warna berbeda untuk setiap status
Update status yang mudah melalui dialog
Tracking progress tugas
👥 Tim Pengembang
Mata Kuliah: Pemrograman Berorientasi Objek (PBO)
Institusi: Institut Teknologi Kalimantan (ITK)
Semester: 2/2025

📄 Lisensi
Proyek ini dikembangkan untuk keperluan akademik di Institut Teknologi Kalimantan.

🤝 Kontribusi
Proyek ini merupakan tugas besar mata kuliah. Untuk saran dan perbaikan, silakan hubungi tim pengembang.
