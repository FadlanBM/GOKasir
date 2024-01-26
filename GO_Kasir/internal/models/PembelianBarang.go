package models

import "gorm.io/gorm"

type PembelianBarang struct {
	gorm.Model
	TransaksiID   uint
	Transaksi     Transaksi
	BarangID      uint
	Barang        Barang
	Quantity      uint    `json:"quantity" form:"quantity"`
	SubTotalHarga float64 `gorm:"type:decimal(15,2); not null" json:"subTotalHarga" form:"subTotalHarga"`
	Discount      float64 `gorm:"type:dec" json:"discount" form:"discount"`
}
