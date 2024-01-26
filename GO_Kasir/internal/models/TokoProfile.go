package models

import "gorm.io/gorm"

type TokoProfile struct {
	gorm.Model
	Name   string `gorm:"type:varchar(50);not null" json:"name" form:"name"`
	Npwp   string `gorm:"type:varchar(50);not null" json:"npwp" form:"npwp"`
	Email  string `gorm:"type:text;not null" json:"email" form:"email"`
	Telp   string `gorm:"type:varchar(13);not null" json:"telp" form:"telp"`
	Alamat string `gorm:"type:varchar(255);not null" json:"alamat" form:"alamat"`
}
