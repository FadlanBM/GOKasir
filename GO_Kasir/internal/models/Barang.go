package models

import "gorm.io/gorm"

type Barang struct {
	gorm.Model
	Name       string  `gorm:"type:varchar(50);not null" json:"name" form:"name"`
	CodeBarang string  `gorm:"type:varchar(50);not null" json:"code_barang" form:"code_barang"`
	Merek      string  `gorm:"type:varchar(50);not nutt" json:"merek" form:"merek"`
	Tipe       string  `gorm:"type:varchar(50);not null" json:"tipe" form:"tipe"`
	Price      float64 `gorm:"type:decimal(15,2);not null" json:"price" form:"price"`
	Stock      int     `json:"stock" form:"stock"`
}
