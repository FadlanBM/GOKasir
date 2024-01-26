package models

import (
	"gorm.io/gorm"
)

type Transaksi struct {
	gorm.Model
	TotalPrice      float64 `gorm:"type:decimal(15,2);not null" json:"total_price" form:"total_price"`
	NominalTunai    float64 `gorm:"type:decimal(15,2);not null" json:"nominal_tunai" form:"nominal_tunai"`
	PPN             float64 `gorm:"type:decimal(15,2);not null" json:"ppn" form:"ppn"`
	Kembalian       float64 `gorm:"type:decimal(15,2);not null" json:"kembalian" form:"kembalian"`
	Point           uint    `json:"point" form:"point"`
	TokoProfileID   uint
	TokoProfile     TokoProfile
	VoucerID        uint `gorm:"default:null"`
	Voucer          Voucer
	MemberID        uint `gorm:"default:null"`
	Member          Member
	KaryawanID      uint
	Karyawan        Karyawan
	BarangTransaksi []PembelianBarang
}
