package models

import "gorm.io/gorm"

type Karyawan struct {
	gorm.Model
	Nik           string `gorm:"type:varchar(255);not null" json:"nik" form:"nik"`
	Nama_Karyawan string `gorm:"type:varchar(35); not null" json:"nama_karyawan" form:"nama_karyawan"`
	Username      string `gorm:"type:varchar(25); not null" json:"username" form:"username"`
	Password      string `gorm:"type:varchar(100); not null" json:"password" form:"password"`
	Telp          string `gorm:"type:varchar(13); not null" json:"telp" form:"telp"`
	TokoProfileID uint
	TokoProfile   TokoProfile
	Role          string `gorm:"type:enum('admin','kasir'); not null" json:"role" form:"role"`
	Transaksi     []Transaksi
}
